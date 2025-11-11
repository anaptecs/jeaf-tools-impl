/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.xml.DocumentProperties;
import com.anaptecs.jeaf.tools.api.xml.XMLTools;
import com.anaptecs.jeaf.xfun.annotations.StartupInfoWriterImpl;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.trace.StartupInfoWriter;
import com.anaptecs.jeaf.xfun.api.trace.Trace;
import com.anaptecs.jeaf.xfun.api.trace.TraceLevel;

import stormpot.BlazePool;
import stormpot.Config;
import stormpot.Pool;
import stormpot.PoolException;
import stormpot.Timeout;

/**
 * Class simplifies the handling of XML documents. Thus the class offers method to easily create DOM documents. However
 * it does not handle exceptions that may occur.
 * 
 * @author JEAF Development Team
 * @version 1.0
 */
@StartupInfoWriterImpl
@ToolsImplementation(toolsInterface = XMLTools.class)
public class XMLToolsImpl implements XMLTools, StartupInfoWriter {
  /**
   * Constant for name of system property that can be used to define the document builder pools size
   */
  public static final String DOCUMENT_BUILDER_POOL_SIZE_PROPERTY = "jeaf.tools.xml.document.builder.pool.size";

  /**
   * Counter is used to monitor the amount of times when we were not able to used a pooled document builder.
   */
  private static int noPooledObjectCounter = 0;

  /**
   * Timeout for max. waiting time for a new document builder.
   * 
   * Max. waiting time must be greater than 0 in order to avoid too many objects instead of taking them from the pool.
   * Behavior is only visible in load test with high pressure on XML parsing.
   */
  private static final Timeout TIMEOUT = new Timeout(1, TimeUnit.NANOSECONDS);

  /**
   * Size of the document builder pools. Default size is 100 entries per pool.
   */
  private final int poolSize;

  /**
   * Pool with all validating document builders.
   */
  private final Pool<PooledDocumentBuilder> validatingPool;

  /**
   * Pool with all non-validating document builders.
   */
  private final Pool<PooledDocumentBuilder> nonValidatingPool;

  /**
   * Constructor of this class is private in order to ensure that no instances of this class can be created.
   */
  public XMLToolsImpl( ) {
    // Lookup pools size. If it is not configured the default size will be 100 entries, which should be fine for most
    // cases.
    XMLToolsConfiguration lConfiguration = new XMLToolsConfiguration();
    poolSize = lConfiguration.getDocumentBuilderPoolSize();

    // Create new pool for validating document builders
    Config<PooledDocumentBuilder> lConfig = new Config<>();
    lConfig.setAllocator(new DocumentBuilderAllocator(true));
    lConfig.setBackgroundExpirationEnabled(false).setSize(poolSize);
    validatingPool = new BlazePool<>(lConfig);

    // Create new pool for non-validating document builders
    lConfig = new Config<>();
    lConfig.setAllocator(new DocumentBuilderAllocator(false));
    lConfig.setBackgroundExpirationEnabled(false);
    lConfig.setSize(poolSize);
    nonValidatingPool = new BlazePool<>(lConfig);
  }

  /**
   * Method creates a new and empty DOM document and returns it.
   * 
   * @return Document Created DOM document. The method never returns null.
   * @throws ParserConfigurationException if no non validating document builder can be created.
   */
  @Override
  public Document createDocument( ) throws ParserConfigurationException {
    // Create new DocumentBuilder object. Depending on parameter pValidating it is either validating or not. As default
    // error handler, an implementation is used that always reacts on problems with an exception.
    DocumentBuilder lDocumentBuilder = this.createDocumentBuilder(false);
    lDocumentBuilder.setErrorHandler(new ErrorHandlerImpl());
    return lDocumentBuilder.newDocument();
  }

  /**
   * Method creates new DocumentBuilder object, which already has an error handler set. This error handler reacts on all
   * occurring problems with an exception.
   * 
   * @param pValidating Parameter indicates whether a validating DocumentBuilder object should be created or not. If the
   * parameter is true, a validating one is created, otherwise not.
   * @return DocumentBuilder Created DocumentBuilder object. The method never returns null.
   * @throws ParserConfigurationException if no DocumentBuilder object could be created.
   */
  @Override
  public DocumentBuilder createDocumentBuilder( boolean pValidating ) throws ParserConfigurationException {
    DocumentBuilderFactory lFactory = XMLToolsImpl.createSecuredDocumentBuilderFactory(pValidating);
    DocumentBuilder lDocumentBuilder = lFactory.newDocumentBuilder();
    lDocumentBuilder.setErrorHandler(new ErrorHandlerImpl());
    return lDocumentBuilder;
  }

  /**
   * Method creates a new secured document build factory. Secured in this case means that it is configured in a way to
   * prevent XXE attacks. The configuration is based on the recommendations from OWASP Foundation that can be found
   * here: https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#java
   * 
   * @param pValidating Parameter indicates whether a validating DocumentBuilder object should be created or not. If the
   * parameter is true, a validating one is created, otherwise not.
   * @return {@link DocumentBuilderFactory} Created factory. The method never returns null.
   * @throws ParserConfigurationException
   */
  static DocumentBuilderFactory createSecuredDocumentBuilderFactory( boolean pValidating )
    throws ParserConfigurationException {

    // Create new document builder factory and afterwards secure it as much as possible.
    DocumentBuilderFactory lFactory = DocumentBuilderFactory.newInstance();
    lFactory.setValidating(pValidating);

    // Enable some security setting to prevent XXE attacks
    // The following lines of code are based on recommendations of
    // https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#java
    //
    // If you can't completely disable DTDs, then at least do the following:
    // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
    // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
    // JDK7+ - http://xml.org/sax/features/external-general-entities
    // This feature has to be used together with the following one, otherwise it will not protect you from XXE for sure
    lFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);

    // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
    // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
    // JDK7+ - http://xml.org/sax/features/external-parameter-entities
    // This feature has to be used together with the previous one, otherwise it will not protect you from XXE for sure
    lFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

    // Disable external DTDs as well
    lFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

    // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
    lFactory.setXIncludeAware(false);
    lFactory.setExpandEntityReferences(false);

    // Return secured factory.
    return lFactory;
  }

  /**
   * Method creates a new DOM document out of the file with the passed file name. Therefore a DocumentBuilder object is
   * used that is depending on parameter pValidating either validated or not.
   * 
   * @param pFileName Name of the file out of which the DOM document should be created. The parameter must not be null.
   * @param pValidating Parameter indicates whether a validating DocumentBuilder object should be used to create the new
   * DOM document. If the parameter is true, the document is validated, otherwise not.
   * @return Document Created DOM document. The returned document is already normalized. The method never returns null.
   * @throws JEAFSystemException if no DocumentBuilder object could be created or an error occurs during the parsing
   * process or an error occurs while reading from the file with the passed name.
   */
  @Override
  public Document parseFile( String pFileName, boolean pValidating ) throws JEAFSystemException {
    return this.parseFile(pFileName, pValidating, null);
  }

  /**
   * Method creates a new DOM document out of the file with the passed file name. Therefore a DocumentBuilder object is
   * used that is depending on parameter pValidating either validated or not.
   * 
   * @param pFileName Name of the file out of which the DOM document should be created. The parameter must not be null.
   * @param pValidating Parameter indicates whether a validating DocumentBuilder object should be used to create the new
   * DOM document. If the parameter is true, the document is validated, otherwise not.
   * @param pSystemID The system id provides the base for resolving relative URIs during the parsing process. The
   * parameter may be null.
   * @return Document Created DOM document. The returned document is already normalized. The method never returns null.
   * @throws JEAFSystemException if no DocumentBuilder object could be created or an error occurs during the parsing
   * process or an error occurs while reading from the file with the passed name.
   */
  @Override
  public Document parseFile( String pFileName, boolean pValidating, String pSystemID ) throws JEAFSystemException {
    // Check parameter pXMLFile
    Check.checkInvalidParameterNull(pFileName, "pFileName");

    // Create input stream from passed file and parse it.
    File lResourceFile = new File(pFileName);
    try (InputStream lResourceStream = new FileInputStream(lResourceFile);) {
      return this.parseInputStream(lResourceStream, pValidating, pSystemID, pFileName);
    }
    // Error during file access.
    catch (IOException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_PARSE_XML_FILE, e, pFileName);
    }
  }

  /**
   * Method creates a new DOM document out of the file with the passed file name. Therefore a DocumentBuilder object is
   * used that is depending on parameter pValidating either validated or not.
   * 
   * @param pInputStream Input stream which is used as input for the XML parser to create the DOM document. The
   * parameter must not be null. In order to improve the parsing performance the input stream will be buffered.
   * @param pValidating Parameter indicates whether a validating DocumentBuilder object should be used to create the new
   * DOM document. If the parameter is true, the document is validated, otherwise not.
   * @return Document Created DOM document. The returned document is already normalized. The method never returns null.
   * @throws JEAFSystemException if no DocumentBuilder object could be created or an error occurs during the parsing
   * process or an error occurs while reading from the file with the passed name.
   */
  @Override
  public Document parseInputStream( InputStream pInputStream, boolean pValidating ) throws JEAFSystemException {
    return this.parseInputStream(pInputStream, pValidating, null, null);
  }

  /**
   * Method creates a new DOM document out of the file with the passed file name. Therefore a DocumentBuilder object is
   * used that is depending on parameter pValidating either validated or not.
   * 
   * @param pInputStream Input stream which is used as input for the XML parser to create the DOM document. The
   * parameter must not be null. In order to improve the parsing performance the input stream will be buffered.
   * @param pValidating Parameter indicates whether a validating DocumentBuilder object should be used to create the new
   * DOM document. If the parameter is true, the document is validated, otherwise not.
   * @param pSystemID The system id provides the base for resolving relative URIs during the parsing process. The
   * parameter may be null.
   * @param pStreamName Name of the stream that is read. The parameter is only used in cases that an exception occurs.
   * The parameter may be null.
   * @return Document Created DOM document. The returned document is already normalized. The method never returns null.
   * @throws JEAFSystemException if no DocumentBuilder object could be created or an error occurs during the parsing
   * process or an error occurs while reading from the file with the passed name.
   */
  @Override
  public Document parseInputStream( InputStream pInputStream, boolean pValidating, String pSystemID,
      String pStreamName )
    throws JEAFSystemException {

    // Check parameter pInputStream
    Check.checkInvalidParameterNull(pInputStream, "pInputStream");
    String lStreamName = pStreamName;
    if (lStreamName == null) {
      lStreamName = "unknown";
    }

    // Check if the input stream has to be buffered.
    BufferedInputStream lBufferedInputStream;

    // Input stream is already buffered.
    if (pInputStream instanceof BufferedInputStream) {
      lBufferedInputStream = (BufferedInputStream) pInputStream;
    }
    // In order to improve the performance of the parsing process the input stream will be buffered.
    else {
      lBufferedInputStream = new BufferedInputStream(pInputStream);
    }

    // Create document builder.
    PooledDocumentBuilder lPooledProxy = null;
    try {
      if (pValidating == true) {
        lPooledProxy = validatingPool.claim(TIMEOUT);
      }
      else {
        lPooledProxy = nonValidatingPool.claim(TIMEOUT);
      }

      DocumentBuilder lDocumentBuilder;
      if (lPooledProxy != null) {
        lDocumentBuilder = lPooledProxy.getDocumentBuilder();
      }
      // During startup it may happen that the document builder pool is not yet ready.
      else {
        int lCount = 0;
        synchronized (XMLToolsImpl.class) {
          noPooledObjectCounter++;
          lCount = noPooledObjectCounter;
        }
        if (noPooledObjectCounter > 1) {
          XFun.getTrace().writeEmergencyTrace("Max. waiting time for pooled document builder exeeded. Waited "
              + TIMEOUT.getTimeout()
              + "ns. This problem may occur once during startup. If you see this message more often inside your logs then please check your environment configuration. The pool size can be configured using system property '"
              + DOCUMENT_BUILDER_POOL_SIZE_PROPERTY + "'. Current pool size is " + poolSize + ". Problem occurred "
              + lCount + " times.", null, TraceLevel.INFO);
        }
        lDocumentBuilder = this.createDocumentBuilder(pValidating);
      }

      // Parse input stream either with reference to system ID or not.
      Document lDocument;
      if (pSystemID != null) {
        lDocument = lDocumentBuilder.parse(lBufferedInputStream, pSystemID);
      }
      else {
        lDocument = lDocumentBuilder.parse(lBufferedInputStream);
      }
      lDocument.getDocumentElement().normalize();
      return lDocument;
    }
    // Unable to parse XML
    catch (SAXException | IOException | ParserConfigurationException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_PARSE_XML_FILE, e, lStreamName);
    }
    // Unable to claim document builder from pool.
    catch (PoolException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_CLAIM_POOLED_DOCUMENT_BUILDER, e,
          Integer.toString(poolSize));
    }
    catch (InterruptedException e) {
      XFun.getTrace().warn("Thread interrupted when trying to get XML Document Builder from pool.", e);
      Thread.currentThread().interrupt();
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_CLAIM_POOLED_DOCUMENT_BUILDER, e,
          Integer.toString(poolSize));
    }
    finally {
      if (lPooledProxy != null) {
        lPooledProxy.release();
      }
    }
  }

  /**
   * Method creates a new DOM document out of the passed xml encoded string. Therefore a DocumentBuilder object is used
   * that is depending on parameter pValidating either validated or not.
   * 
   * @param pXMLEncodedString String which is used as input for the XML parser to create the DOM document. The parameter
   * must not be null.
   * @param pValidating Parameter indicates whether a validating DocumentBuilder object should be used to create the new
   * DOM document. If the parameter is true, the document is validated, otherwise not.
   * @param pSystemID The system id provides the base for resolving relative URIs during the parsing process. The
   * parameter may be null.
   * @param pStringName Name of the string that is read. The parameter is only used in cases that an exception occurs.
   * The parameter may be null.
   * @return Document Created DOM document. The returned document is already normalized. The method never returns null.
   * @throws JEAFSystemException if no DocumentBuilder object could be created or an error occurs during the parsing
   * process or an error occurs while reading from the file with the passed name.
   */
  @Override
  public Document parseString( String pXMLEncodedString, boolean pValidating, String pSystemID, String pStringName )
    throws JEAFSystemException {

    // Check parameters.
    Check.checkInvalidParameterNull(pStringName, "pStringName");

    // Convert string to input stream.
    InputStream lInputStream = new ByteArrayInputStream(pXMLEncodedString.getBytes(Charset.defaultCharset()));

    // Parse input stream and return result.
    return this.parseInputStream(lInputStream, pValidating, pSystemID, pStringName);
  }

  /**
   * Method writes the passed DOM document to the passed output stream.
   * 
   * @param pDocument DOM document that should be written to the output stream. The parameter must not be null.
   * @param pOutputStream Output stream to which the document should be written. The parameter must not be null and the
   * stream must be open. The method won't close the passed stream no matter what happens.
   */
  @Override
  public void writeDocument( Document pDocument, OutputStream pOutputStream ) {
    this.writeDocument(pDocument, null, pOutputStream);
  }

  /**
   * Method writes the passed DOM document to the passed output stream.
   * 
   * @param pDocument DOM document that should be written to the output stream. The parameter must not be null.
   * @param pDocumentProperties Document properties define additional information like the XML Prolog and references to
   * a DTD. The parameter may be null. In this case default values will be used.
   * @param pOutputStream Output stream to which the document should be written. The parameter must not be null and the
   * stream must be open. The method won't close the passed stream no matter what happens.
   */
  @Override
  public void writeDocument( Document pDocument, DocumentProperties pDocumentProperties, OutputStream pOutputStream ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pDocument, "pDocument");

    this.writeNode(pDocument, pDocumentProperties, pOutputStream);
  }

  /**
   * Method writes the passed DOM node to the passed output stream.
   * 
   * @param pNode DOM node that should be written to the output stream. The parameter must not be null.
   * @param pDocumentProperties Document properties define additional information like the XML Prolog and references to
   * a DTD. The parameter may be null. In this case default values will be used.
   * @param pOutputStream Output stream to which the document should be written. The parameter must not be null and the
   * stream must be open. The method won't close the passed stream no matter what happens.
   */
  private void writeNode( Node pNode, DocumentProperties pDocumentProperties, OutputStream pOutputStream ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pNode, "pNode");
    Check.checkInvalidParameterNull(pOutputStream, "pOutputStream");

    // Create source and result objects for transformation.
    DOMSource lSource = new DOMSource(pNode);
    StreamResult lStreamResult = new StreamResult(pOutputStream);

    // Create new transformer and run transformation.
    try {
      Transformer lTransformer = this.createTransformer(pDocumentProperties);
      lTransformer.transform(lSource, lStreamResult);
    }
    catch (TransformerException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_WRITE_XML, e, e.getMessage());
    }
  }

  /**
   * Method converts the passed XML document object to a string.
   * 
   * @param pDocument Document object that should be converted to a string. The parameter must not be null.
   * @return String Containing the complete XML document as string. The method never returns null.
   * @throws TransformerException
   */
  @Override
  public String toString( Document pDocument ) {
    return this.toString(pDocument, null);
  }

  /**
   * Method converts the passed XML document object to a string.
   * 
   * @param pDocument Document object that should be converted to a string. The parameter must not be null.
   * @return String Containing the complete XML document as string. The method never returns null.
   * @throws TransformerException
   */
  @Override
  public String toString( Document pDocument, DocumentProperties pDocumentProperties ) {
    ByteArrayOutputStream lOutputStream = new ByteArrayOutputStream();
    this.writeDocument(pDocument, pDocumentProperties, lOutputStream);
    return lOutputStream.toString();
  }

  /**
   * Method creates a new transformer that can be used to serialize a XML document.
   * 
   * @param pDocumentProperties configuration parameters for the generation of the XML document. The parameter may be
   * null.
   * @return {@link Transformer} Created transformer. The method never returns null.
   * @throws TransformerException
   */
  private Transformer createTransformer( DocumentProperties pDocumentProperties ) {
    // If no document properties are defined then we use default settings.
    if (pDocumentProperties == null) {
      pDocumentProperties = DocumentProperties.Builder.newBuilder().build();
    }

    // Create new transformer
    try {
      // For security reasons we have to restrict resolution of external DTD and schemas
      TransformerFactory lFactory = TransformerFactory.newInstance();
      lFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      lFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");

      // Try to restrict access to external schemas. As this feature is only supported by some implementations.
      // Unfortunately there is no other way then trying and catching a may be thrown exception.
      try {
        lFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      }
      // Depending on the JAXP implementation attribute might not be supported, but that's the only way to find out.
      catch (IllegalArgumentException e) {
        // Nothing to do.
      }

      // Create new transformer
      Transformer lTransformer = lFactory.newTransformer();

      // Use passed document properties as settings for the transformer.
      lTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
          this.booleanToYesNo(pDocumentProperties.omitXMLDeclaration()));

      // Configure transformer based on the passed document properties.
      lTransformer.setOutputProperty(OutputKeys.VERSION, pDocumentProperties.getVersion());
      lTransformer.setOutputProperty(OutputKeys.ENCODING, pDocumentProperties.getEncoding());

      String lDoctypePublic = pDocumentProperties.getDoctypePublic();
      if (lDoctypePublic != null) {
        lTransformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, lDoctypePublic);
      }
      String lDoctypeSystem = pDocumentProperties.getDoctypeSystem();
      if (lDoctypeSystem != null) {
        lTransformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, lDoctypeSystem);
      }

      lTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
      lTransformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, pDocumentProperties.getCDataSectionElements());
      lTransformer.setOutputProperty(OutputKeys.INDENT, this.booleanToYesNo(pDocumentProperties.isIndent()));

      return lTransformer;
    }
    catch (TransformerException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_WRITE_XML, e, e.getMessage());
    }
  }

  private String booleanToYesNo( boolean pValue ) {
    String lYesNo;
    if (pValue == true) {
      lYesNo = "yes";
    }
    else {
      lYesNo = "no";
    }
    return lYesNo;
  }

  /**
   * Method creates a String representation if the passed element. The method does not care about any child elements.
   * 
   * @param pElement XML element that should be returned as String representation. The parameter must not be null.
   * @param pIndent Parameter defines if the element should be indented if it has children.
   * @return {@link String} String representation of this element. The method never returns null.
   */
  @Override
  public String toString( Element pElement ) {
    return this.toString(pElement, true);
  }

  /**
   * Method creates a String representation if the passed element. The method does not care about any child elements.
   * 
   * @param pElement XML element that should be returned as String representation. The parameter must not be null.
   * @param pIndent Parameter defines if the element should be indented if it has children.
   * @return {@link String} String representation of this element. The method never returns null.
   */
  @Override
  public String toString( Element pElement, boolean pIndent ) {
    // Check parameter
    Check.checkInvalidParameterNull(pElement, "pElement");

    DocumentProperties.Builder lBuilder = DocumentProperties.Builder.newBuilder();
    lBuilder.omitXMLDeclaration(true);
    if (pElement.hasChildNodes() == true) {
      lBuilder.setIndent(pIndent);
    }
    else {
      lBuilder.setIndent(false);
    }
    ByteArrayOutputStream lOutputStream = new ByteArrayOutputStream();

    this.writeNode(pElement, lBuilder.build(), lOutputStream);
    return lOutputStream.toString();
  }

  /**
   * Method returns the element with the passed name form the passed subtree of a DOM document. The method requires that
   * the searched elements exists exactly once within the passed subtree.
   * 
   * @param pElementName Name of the element whose value should be returned from the subtree. There must be exactly one
   * element with the passed name in the passed subtree. The parameter must not be null.
   * @param pElement Subtree from which the element value should be returned. The parameter must not be null.
   * @param pRequired Parameter defines if the searched element is required or optional. If the parameter is true than
   * the element is required.
   * @return {@link Element} Only element within the subtree with the passed name. The method may return null if an
   * optional element is not defined in the passed subtree.
   */
  @Override
  public Element getSingleElement( String pElementName, Element pElement, boolean pRequired ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pElement, "pElement");
    Check.checkInvalidParameterNull(pElementName, pElementName);

    // Get element by name and ensure that there is only on element
    NodeList lElementsByTagName = pElement.getElementsByTagName(pElementName);

    // Check occurrence of element.
    Element lElement;
    int lSize = lElementsByTagName.getLength();

    // Element not found
    if (lSize == 0) {
      if (pRequired == false) {
        lElement = null;
      }
      else {
        throw new JEAFSystemException(ToolsMessages.REQUIRED_ELEMENT_NOT_FOUND, pElementName);
      }
    }
    // Found searched element. Now we have to get its value.
    else if (lSize == 1) {
      lElement = (Element) lElementsByTagName.item(0);
    }
    // Element exists more than once.
    else {
      throw new JEAFSystemException(ToolsMessages.ELEMENT_NOT_UNIQUE, pElementName);
    }
    // Return found element
    return lElement;
  }

  /**
   * Method returns the element with the passed name form the passed DOM document. The method requires that the searched
   * elements exists exactly once within the passed subtree.
   * 
   * @param pElementName Name of the element whose value should be returned from the subtree. There must be exactly one
   * element with the passed name in the passed subtree. The parameter must not be null.
   * @param pDocument Document from which the element value should be returned. The parameter must not be null.
   * @param pRequired Parameter defines if the searched element is required or optional. If the parameter is true than
   * the element is required.
   * @return {@link Element} Only element within the subtree with the passed name. The method may return null if an
   * optional element is not defined in the passed subtree.
   */
  @Override
  public Element getSingleElement( String pElementName, Document pDocument, boolean pRequired ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pDocument, "pDocument");

    return this.getSingleElement(pElementName, pDocument.getDocumentElement(), pRequired);
  }

  /**
   * Method returns all elements with the passed name form the passed subtree.
   * 
   * @param pElementName Name of the element that should be returned from the subtree.
   * @param pElement Subtree from which the element value should be returned. The parameter must not be null.
   * @param pRequired Parameter defines if the searched element is required or optional. If the parameter is true than
   * the element is required.
   * @return {@link List} List of all elements with the passed name in the subtree. The method never returns null.
   */
  @Override
  public List<Element> getDescendantElements( String pElementName, Element pElement, boolean pRequired ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pElementName, "pElementName");
    Check.checkInvalidParameterNull(pElement, "pElement");

    NodeList lNodeList = pElement.getElementsByTagName(pElementName);
    int lElementCount = lNodeList.getLength();
    List<Element> lElements;
    if (lElementCount > 0) {
      // Create list with all elements
      lElements = new ArrayList<>(lElementCount);
      for (int i = 0; i < lElementCount; i++) {
        lElements.add((Element) lNodeList.item(i));
      }
    }
    else if (pRequired == false) {
      lElements = Collections.emptyList();
    }
    // Invalid discovery info. Expecting at least one attribute
    else {
      throw new JEAFSystemException(ToolsMessages.REQUIRED_ELEMENT_NOT_FOUND, pElementName);
    }
    // Return list with all elements found.
    return lElements;
  }

  /**
   * Method returns all descendant elements with the passed name from the passed document.
   * 
   * @param pElementName Name of the element that should be returned from the subtree.
   * @param pDocument Document who's descendants should be resolved. The parameter must not be null.
   * @param pRequired Parameter defines if the searched element is required or optional. If the parameter is true than
   * at least one element is required.
   * @return {@link List} List of all elements with the passed name in the subtree. The method never returns null.
   */
  @Override
  public List<Element> getDescendantElements( String pElementName, Document pDocument, boolean pRequired ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDocument, "pDocument");

    return this.getDescendantElements(pElementName, pDocument.getDocumentElement(), pRequired);
  }

  /**
   * Method returns the value of the passed element.
   * 
   * @param pElement Element whose value should be returned. The parameter must not be null.
   * @param pRequired Parameter defines if the passed element must have a real value or if it is optional.
   * @return String Value of the passed element. The method may return null if the value is optional.
   */
  @Override
  public String getElementValue( Element pElement, boolean pRequired ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pElement, "pElement");

    String lElementValue;

    // Get value of element from its child nodes.
    NodeList lChildNodes = pElement.getChildNodes();

    // Check all child nodes but ignore non real strings as value.
    lElementValue = null;
    for (int i = 0; i < lChildNodes.getLength(); i++) {
      Node lNextNode = lChildNodes.item(i);
      String lNodeValue = lNextNode.getNodeValue().trim();
      // Found real string
      if (lNodeValue.length() > 0) {
        lElementValue = lNodeValue;
        break;
      }
    }

    // Return value of element.
    if (lElementValue != null || pRequired == false) {
      return lElementValue;
    }
    // Element is expected to have a node value but it does not.
    else {
      throw new JEAFSystemException(ToolsMessages.ELEMENT_VALUE_MISSING, pElement.getNodeName());
    }
  }

  /**
   * Method returns the value of the element with the passed name form the passed subtree of a DOM document. The method
   * requires that the searched elements exists exactly once within the passed subtree.
   * 
   * @param pElementName Name of the element that should be returned from the subtree. There must be exactly one element
   * with the passed name in the passed subtree. The parameter must not be null.
   * @param pElement Subtree from which the element should be returned. The parameter must not be null.
   * @param pRequired Parameter defines if the searched element is required or optional. If the parameter is true than
   * the element is required.
   * @return {@link String} Value of the element. The method may return null in cases where {@link String#trim()}
   * returns an empty string or if an optional element is not defined in the passed subtree.
   */
  @Override
  public String getElementValueFromSubtree( String pElementName, Element pElement, boolean pRequired ) {
    // Parameters don't have to be checked as this will be done by the called method.

    // Get single element
    Element lElement = this.getSingleElement(pElementName, pElement, pRequired);
    String lElementValue;

    // Found searched element, so now we need to get its value.
    if (lElement != null) {
      lElementValue = this.getElementValue(lElement, pRequired);
    }
    // Element not found, so return null.
    else {
      lElementValue = null;
    }

    // Return value of element.
    return lElementValue;
  }

  /**
   * Method returns the value of the element with the passed name form the passed DOM document. The method requires that
   * the searched elements exists exactly once within the passed document.
   * 
   * @param pElementName Name of the element that should be returned from the document. There must be exactly one
   * element with the passed name in the passed subtree. The parameter must not be null.
   * @param pDocument Document who's descendants should be resolved. The parameter must not be null.
   * @param pRequired Parameter defines if the searched element is required or optional. If the parameter is true than
   * the element is required.
   * @return {@link String} Value of the element. The method may return null in cases where {@link String#trim()}
   * returns an empty string or if an optional element is not defined in the passed subtree.
   */
  @Override
  public String getElementValueFromSubtree( String pElementName, Document pDocument, boolean pRequired ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pDocument, "pDocument");

    return this.getElementValueFromSubtree(pElementName, pDocument.getDocumentElement(), pRequired);
  }

  /**
   * Method returns the first Element with the given name from the passed subtree of a DOM document. The method does not
   * require that the searched elements exist exactly once within the passed subtree.
   * 
   * @param pElementName Name of the element that should be returned from the subtree. The parameter must not be null.
   * @param pElement Subtree from which the element value should be returned. The parameter must not be null.
   * @param pRequired Parameter defines if the passed element must have a real value or if it is optional.
   * @return {@link Element} The first element within the subtree with the passed name. The method may return null if an
   * optional element is not defined in the passed subtree.
   */
  @Override
  public Element getFirstChildElement( String pElementName, Element pElement, boolean pRequired ) {
    // Check parameters
    Check.checkInvalidParameterNull(pElement, "pElement");
    Check.checkInvalidParameterNull(pElementName, pElementName);

    Element lFirstChildElement = null;
    NodeList lChildNodes = pElement.getChildNodes();
    for (int i = 0; i < lChildNodes.getLength(); i++) {
      Node lItem = lChildNodes.item(i);
      if (pElementName.equals(lItem.getNodeName())) {
        lFirstChildElement = (Element) lItem;
        break;
      }
    }
    // Return first child or null.
    if (lFirstChildElement != null || pRequired == false) {
      return lFirstChildElement;
    }
    // First child is required but missing.
    else {
      throw new JEAFSystemException(ToolsMessages.REQUIRED_ELEMENT_NOT_FOUND, pElementName);
    }
  }

  /**
   * Method returns the children of the passed element.
   * 
   * @param pElementName Name of the element that should be returned from the children. The parameter must not be null.
   * @param pElement Element from which the children should be returned. The parameter must not be null.
   * @param pRequired Parameter defines if at least one child is required or if it is optional.
   * @return {@link List} List of children. The method never returns null.
   */
  @Override
  public List<Element> getChildren( String pElementName, Element pElement, boolean pRequired ) {
    List<Element> lFilteredElements = new ArrayList<>();
    NodeList lChildNodes = pElement.getChildNodes();
    for (int i = 0; i < lChildNodes.getLength(); i++) {
      Node lItem = lChildNodes.item(i);
      if (pElementName.equals(lItem.getNodeName())) {
        lFilteredElements.add((Element) lItem);
      }
    }
    // Return list with found elements
    if (lFilteredElements.isEmpty() == false || pRequired == false) {
      return lFilteredElements;
    }
    // At least one child element with the passed name was required to be found.
    else {
      throw new JEAFSystemException(ToolsMessages.REQUIRED_ELEMENT_NOT_FOUND, pElementName);
    }
  }

  /**
   * Method returns the attribute with the passed name from the passed element
   * 
   * @param pElement Element whose attribute value should be returned. The parameter must not be null.
   * @param pAttributeName Name of the attribute whose value should be returned. The parameter must not be null.
   * @param pRequired Parameter defines is the attribute is required nor not.
   * @return {@link String} Value of the attribute or null if it does not exist and has no default value. In case that
   * the attribute is required then an exception instead of null will be returned.
   * @throws JEAFSystemException If parameter <code>pRequired</code> is set to true and the attribute is not present
   * then an exception will be thrown.
   */
  @Override
  public String getAttributeValue( Element pElement, String pAttributeName, boolean pRequired ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pElement, "pElement");
    Check.checkInvalidParameterNull(pAttributeName, "pAttributeName");

    // Resolve value of attribute
    String lValue;
    if (pElement.hasAttribute(pAttributeName) == true) {
      lValue = pElement.getAttribute(pAttributeName);
    }
    else {
      lValue = null;
    }

    // Attribute is optional, so we do not have to care about its value
    if (pRequired == false) {
      return lValue;
    }
    // Attribute is required and set.
    else if (lValue != null) {
      return lValue;
    }
    // Required attribute not found.
    else {
      throw new JEAFSystemException(ToolsMessages.REQUIRED_ATTRIBUTE_NOT_FOUND, pAttributeName, pElement.getTagName());
    }
  }

  public static int getNoPooledObjectCounter( ) {
    return noPooledObjectCounter;
  }

  @Override
  public Class<?> getStartupCompletedEventSource( ) {
    return Tools.class;
  }

  @Override
  public void traceStartupInfo( Trace pTrace, TraceLevel pTraceLevel ) {
    // Trace information about used JAXP library
    String lClass = DocumentBuilderFactory.newInstance().getClass().getName();
    pTrace.writeInitInfo("Using class " + lClass + " as JAXP XML library", pTraceLevel);
  }

}