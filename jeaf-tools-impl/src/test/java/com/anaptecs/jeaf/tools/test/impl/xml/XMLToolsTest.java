/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.anaptecs.jeaf.tools.annotations.XMLToolsConfig;
import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.file.FileTools;
import com.anaptecs.jeaf.tools.api.xml.DocumentProperties;
import com.anaptecs.jeaf.tools.api.xml.DocumentProperties.Builder;
import com.anaptecs.jeaf.tools.api.xml.XMLTools;
import com.anaptecs.jeaf.tools.impl.DefaultToolsConfiguration;
import com.anaptecs.jeaf.tools.impl.xml.DocumentBuilderAllocator;
import com.anaptecs.jeaf.tools.impl.xml.ErrorHandlerImpl;
import com.anaptecs.jeaf.tools.impl.xml.PooledDocumentBuilder;
import com.anaptecs.jeaf.tools.impl.xml.XMLToolsConfiguration;
import com.anaptecs.jeaf.tools.impl.xml.XMLToolsImpl;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.info.JavaRelease;
import com.anaptecs.jeaf.xfun.api.trace.TraceLevel;

import stormpot.Poolable;
import stormpot.Slot;

@XMLToolsConfig(documenBuilderPoolSize = 1)
public class XMLToolsTest {
  @Test
  public void testXMLStreamParsing( ) throws IOException {
    XMLTools lXMLTools = Tools.getXMLTools();
    InputStream lInputStream = new FileInputStream("./src/test/resources/Test.xml");
    Document lDocument = lXMLTools.parseInputStream(lInputStream, false);
    assertNotNull(lDocument);
    assertEquals("Root", lDocument.getDocumentElement().getNodeName());
    lInputStream.close();

    lInputStream = new FileInputStream("./src/test/resources/Test.xml");
    lDocument = lXMLTools.parseInputStream(lInputStream, false, "./src/test/resources/Test.dtd", "Test.xml");
    assertNotNull(lDocument);
    assertEquals("Root", lDocument.getDocumentElement().getNodeName());
    lInputStream.close();

    lInputStream = new FileInputStream("./src/test/resources/Test.xml");
    lDocument = lXMLTools.parseInputStream(new BufferedInputStream(lInputStream), false,
        "./src/test/resources/Test.dtd", "Test.xml");
    assertNotNull(lDocument);
    assertEquals("Root", lDocument.getDocumentElement().getNodeName());
  }

  @Test
  @Disabled
  public void testDocumentBuilderPooling( ) throws InterruptedException, IOException {
    XMLToolsConfiguration lConfiguration = new XMLToolsConfiguration();
    assertEquals(1, lConfiguration.getDocumentBuilderPoolSize());

    String lXML = FileTools.getFileTools().getFileContentAsString("./src/test/resources/TestNoDTD.xml");

    int lNoPooledObjectCounter = XMLToolsImpl.getNoPooledObjectCounter();

    // Create threads
    List<Thread> lThreads = new ArrayList<>();
    int lThreadCount = 50;
    for (int i = 0; i < lThreadCount; i++) {
      FileParser lRunnable = new FileParser();
      lRunnable.xmlString = lXML;
      Thread lThread = new Thread(lRunnable);
      lThreads.add(lThread);
    }

    for (int i = 0; i < lThreadCount; i++) {
      Thread lThread = lThreads.get(i);
      lThread.start();
    }

    // Run threads
    Thread.sleep(1000);

    // Check if some objects were not taken from pool
    int lNewDocumentBuilders = XMLToolsImpl.getNoPooledObjectCounter() - lNoPooledObjectCounter;
    assertTrue(lNewDocumentBuilders > 0);
    assertTrue(lNewDocumentBuilders < lThreadCount);
  }

  @Test
  public void testXMLFileParsing( ) {
    XMLTools lXMLTools = Tools.getXMLTools();

    Document lDocument = lXMLTools.parseFile("./src/test/resources/Test.xml", true, "./src/test/resources/Test.dtd");
    assertNotNull(lDocument);
    assertEquals("Root", lDocument.getDocumentElement().getNodeName());

    lDocument = lXMLTools.parseFile("./src/test/resources/TestNoDTD.xml", false);
    assertNotNull(lDocument);
    assertEquals("Root", lDocument.getDocumentElement().getNodeName());

    // Try to validate xml file without DTD
    try {
      lXMLTools.parseFile("./src/test/resources/TestNoDTD.xml", true);
      fail("Exception expected when trying to validate xml file without DTD.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_PARSE_XML_FILE, e.getErrorCode());
    }

    // Try to read file that does not exist
    try {
      lXMLTools.parseFile("./FileNotFound.xml", false);
      fail("Exception expected when trying to parse not existing file.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_PARSE_XML_FILE, e.getErrorCode());
    }
  }

  @Test
  public void testXMLSingleElementAccess( ) {
    XMLTools lXMLTools = Tools.getXMLTools();

    Document lDocument = lXMLTools.parseFile("./src/test/resources/Test.xml", true, "./src/test/resources/Test.dtd");
    assertNotNull(lDocument);

    Element lRoot = lDocument.getDocumentElement();
    Element lElement = lXMLTools.getSingleElement("ClassInfo", lRoot, false);
    assertNotNull(lElement);
    assertEquals("ClassInfo", lElement.getNodeName());

    // Try to find element that does not exist in subtree.
    Element lChild = lXMLTools.getSingleElement("Message", lElement, false);
    assertNull(lChild);

    // Try to resolve element that does not exist.
    lElement = lXMLTools.getSingleElement("NotExistingElement", lRoot, false);
    assertNull(lElement);

    // Try to resolve element that does not exist.
    try {
      lXMLTools.getSingleElement("NotExistingElement", lRoot, true);
      fail("Exception expected when trying to get required element.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.REQUIRED_ELEMENT_NOT_FOUND, e.getErrorCode());
    }

    // Test error handling
    try {
      lXMLTools.getSingleElement("Message", lRoot, false);
      fail("Expected exception when trying to get single element but multiple are available.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.ELEMENT_NOT_UNIQUE, e.getErrorCode());
    }

    try {
      lXMLTools.getSingleElement("ClassInfo", (Element) null, false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    try {
      lXMLTools.getSingleElement(null, lRoot, false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testXMLSingleElementAccessViaDocument( ) {
    XMLTools lXMLTools = Tools.getXMLTools();

    Document lDocument = lXMLTools.parseFile("./src/test/resources/Test.xml", true, "./src/test/resources/Test.dtd");
    assertNotNull(lDocument);

    Element lElement = lXMLTools.getSingleElement("ClassInfo", lDocument, false);
    assertNotNull(lElement);
    assertEquals("ClassInfo", lElement.getNodeName());

    // Try to resolve element that does not exist.
    lElement = lXMLTools.getSingleElement("NotExistingElement", lDocument, false);
    assertNull(lElement);

    // Try to resolve element that does not exist.
    try {
      lXMLTools.getSingleElement("NotExistingElement", lDocument, true);
      fail("Exception expected when trying to get required element.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.REQUIRED_ELEMENT_NOT_FOUND, e.getErrorCode());
    }

    // Test error handling
    try {
      lXMLTools.getSingleElement("Message", lDocument, false);
      fail("Expected exception when trying to get single element but multiple are available.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.ELEMENT_NOT_UNIQUE, e.getErrorCode());
    }

    try {
      lXMLTools.getSingleElement("ClassInfo", (Document) null, false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    try {
      lXMLTools.getSingleElement(null, lDocument, false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testXMLAttributeAccess( ) {
    XMLTools lXMLTools = Tools.getXMLTools();

    Document lDocument = lXMLTools.parseFile("./src/test/resources/Test.xml", true, "./src/test/resources/Test.dtd");
    assertNotNull(lDocument);

    Element lElement = lXMLTools.getSingleElement("ClassInfo", lDocument, false);
    assertNotNull(lElement);

    // Test access to existing attribute.
    String lAttributeValue = lXMLTools.getAttributeValue(lElement, "className", true);
    assertEquals("XFunMessages", lAttributeValue);
    lAttributeValue = lXMLTools.getAttributeValue(lElement, "className", false);
    assertEquals("XFunMessages", lAttributeValue);

    // Test access to not existing attribute.
    lAttributeValue = lXMLTools.getAttributeValue(lElement, "missing", false);
    assertNull(lAttributeValue);

    try {
      lXMLTools.getAttributeValue(lElement, "missing", true);
      fail("Exception exception when accessing required but missing attribute.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.REQUIRED_ATTRIBUTE_NOT_FOUND, e.getErrorCode());
    }

    // Test exception handling
    try {
      lXMLTools.getAttributeValue(lElement, null, false);
      fail("Exception expected when passing null for required paramater.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lXMLTools.getAttributeValue(null, "className", false);
      fail("Exception expected when passing null for required paramater.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testElementValueAccess( ) {
    XMLTools lXMLTools = Tools.getXMLTools();

    Document lDocument = lXMLTools.parseFile("./src/test/resources/Test.xml", true, "./src/test/resources/Test.dtd");
    assertNotNull(lDocument);

    Element lElement = lXMLTools.getSingleElement("ClassInfo", lDocument, false);
    assertNotNull(lElement);

    // Get value from element that has no value.
    String lValue = lXMLTools.getElementValue(lElement, false);
    assertNull(lValue);

    try {
      lXMLTools.getElementValue(lElement, true);
      fail("Expected exception when required element value is missing.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.ELEMENT_VALUE_MISSING, e.getErrorCode());
      assertEquals("ClassInfo", e.getMessageParameters()[0]);
    }

    lElement = lXMLTools.getSingleElement("ElementWithValue", lDocument, true);
    lValue = lXMLTools.getElementValue(lElement, true);
    assertEquals("My value ...", lValue);

    lElement = lXMLTools.getSingleElement("EmptyElement", lDocument, true);
    lValue = lXMLTools.getElementValue(lElement, false);
    assertNull(lValue);

    lElement = lXMLTools.getSingleElement("NoContentElement", lDocument, true);
    lValue = lXMLTools.getElementValue(lElement, false);
    assertNull(lValue);
  }

  @Test
  public void testChildrenAccess( ) {
    XMLTools lXMLTools = Tools.getXMLTools();

    Document lDocument = lXMLTools.parseFile("./src/test/resources/Test.xml", true, "./src/test/resources/Test.dtd");
    assertNotNull(lDocument);

    Element lRootElement = lDocument.getDocumentElement();
    List<Element> lChildren = lXMLTools.getChildren("MessageFolder", lRootElement, false);
    assertEquals(1, lChildren.size());
    lChildren = lXMLTools.getChildren("MessageFolder", lDocument.getDocumentElement(), false);
    assertEquals(1, lChildren.size());

    lChildren = lXMLTools.getChildren("NotExistingElement", lRootElement, false);
    assertEquals(0, lChildren.size());

    try {
      lXMLTools.getChildren("NotExistingElement", lRootElement, true);
      fail("Exception expected when required element is missing.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.REQUIRED_ELEMENT_NOT_FOUND, e.getErrorCode());
    }

  }

  @Test
  public void testDescendantsAccess( ) {
    XMLTools lXMLTools = Tools.getXMLTools();

    Document lDocument = lXMLTools.parseFile("./src/test/resources/Test.xml", true, "./src/test/resources/Test.dtd");
    assertNotNull(lDocument);

    List<Element> lElements = lXMLTools.getDescendantElements("Message", lDocument.getDocumentElement(), true);
    assertEquals(49, lElements.size());

    lElements = lXMLTools.getDescendantElements("Message", lDocument.getDocumentElement(), false);
    assertEquals(49, lElements.size());

    lElements = lXMLTools.getDescendantElements("Message", lDocument, true);
    assertEquals(49, lElements.size());

    Element lElement = lXMLTools.getSingleElement("EmptyElement", lDocument, false);
    lElements = lXMLTools.getDescendantElements("Message", lElement, false);
    assertEquals(0, lElements.size());

    try {
      lXMLTools.getDescendantElements("Message", lElement, true);
      fail("Expected exception when required element could not be found.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.REQUIRED_ELEMENT_NOT_FOUND, e.getErrorCode());
    }
  }

  @Test
  public void testGetElementValueFromSubtree( ) {
    XMLTools lXMLTools = Tools.getXMLTools();

    Document lDocument = lXMLTools.parseFile("./src/test/resources/Test.xml", true, "./src/test/resources/Test.dtd");
    assertNotNull(lDocument);

    String lValue = lXMLTools.getElementValueFromSubtree("ElementWithValue", lDocument.getDocumentElement(), false);
    assertEquals("My value ...", lValue);

    lValue = lXMLTools.getElementValueFromSubtree("EmptyElement", lDocument.getDocumentElement(), false);
    assertNull(lValue);

    lValue = lXMLTools.getElementValueFromSubtree("NoContentElement", lDocument, false);
    assertNull(lValue);

    lValue = lXMLTools.getElementValueFromSubtree("NotExistingElement", lDocument, false);
    assertNull(lValue);

    try {
      lXMLTools.getElementValueFromSubtree("EmptyElement", lDocument.getDocumentElement(), true);
      fail("Exception expected when required value is mising.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.ELEMENT_VALUE_MISSING, e.getErrorCode());
    }

    try {
      lXMLTools.getElementValueFromSubtree("NotExistingElement", lDocument.getDocumentElement(), true);
      fail("Exception expected when required value is mising.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.REQUIRED_ELEMENT_NOT_FOUND, e.getErrorCode());
    }

    // Check exception handling.
    try {
      lXMLTools.getElementValueFromSubtree("EmptyElement", (Element) null, false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lXMLTools.getElementValueFromSubtree("EmptyElement", (Document) null, false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lXMLTools.getElementValueFromSubtree(null, lDocument, false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lXMLTools.getElementValueFromSubtree(null, lDocument.getDocumentElement(), false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testGetFirstChildElement( ) {
    XMLTools lXMLTools = Tools.getXMLTools();

    Document lDocument = lXMLTools.parseFile("./src/test/resources/Test.xml", true, "./src/test/resources/Test.dtd");
    assertNotNull(lDocument);

    Element lElement = lXMLTools.getFirstChildElement("MessageFolder", lDocument.getDocumentElement(), true);
    assertNotNull(lElement);
    lElement = lXMLTools.getFirstChildElement("MessageFolder", lElement, true);
    assertNotNull(lElement);

    Element lFirstChildElement = lXMLTools.getFirstChildElement("Message", lElement, true);
    assertNotNull(lFirstChildElement);
    assertEquals("Message", lFirstChildElement.getNodeName());

    lFirstChildElement = lXMLTools.getFirstChildElement("Message", lElement, false);
    assertNotNull(lFirstChildElement);
    assertEquals("Message", lFirstChildElement.getNodeName());

    // Try to get child element that does not exist.
    lFirstChildElement = lXMLTools.getFirstChildElement("NotExistingElement", lElement, false);
    assertNull(lFirstChildElement);

    try {
      lXMLTools.getFirstChildElement("NotExistingElement", lElement, true);
      fail("Expecting exception when required element is missing.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.REQUIRED_ELEMENT_NOT_FOUND, e.getErrorCode());
    }

    // Test exception handling.
    try {
      lXMLTools.getFirstChildElement(null, lElement, false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lXMLTools.getFirstChildElement("Message", null, false);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testCreateDocument( ) throws ParserConfigurationException {
    XMLTools lXMLTools = Tools.getXMLTools();
    Document lDocument = lXMLTools.createDocument();
    assertNotNull(lDocument);
  }

  @Test
  public void testWriteDocument( ) throws ParserConfigurationException, IOException {
    XMLTools lXMLTools = Tools.getXMLTools();
    Document lDocument = lXMLTools.createDocument();

    String lNamespace = "https://jeaf.de/xml/test";
    Element lRoot = lDocument.createElementNS(lNamespace, "Root");
    lDocument.appendChild(lRoot);

    ByteArrayOutputStream lOutputStream = new ByteArrayOutputStream();
    lXMLTools.writeDocument(lDocument, lOutputStream);
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + System.lineSeparator()
        + "<Root xmlns=\"https://jeaf.de/xml/test\"/>" + System.lineSeparator(), lOutputStream.toString());

    lOutputStream = new ByteArrayOutputStream();
    lXMLTools.writeDocument(lDocument, null, lOutputStream);
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + System.lineSeparator()
        + "<Root xmlns=\"https://jeaf.de/xml/test\"/>" + System.lineSeparator(), lOutputStream.toString());

    Builder lBuilder = DocumentProperties.Builder.newBuilder();
    lBuilder.omitXMLDeclaration(true);
    lBuilder.setIndent(false);
    DocumentProperties lProperties = lBuilder.build();
    lOutputStream = new ByteArrayOutputStream();
    lXMLTools.writeDocument(lDocument, lProperties, lOutputStream);
    assertEquals("<Root xmlns=\"https://jeaf.de/xml/test\"/>", lOutputStream.toString());

    // Extend simple document and write it again.
    Element lElement = lDocument.createElementNS(lNamespace, "MessageFolder");
    lElement.setAttribute("id", "4711");
    lRoot.appendChild(lElement);

    lBuilder = DocumentProperties.Builder.newBuilder();
    lBuilder.omitXMLDeclaration(false);
    lBuilder.setIndent(true);
    lBuilder.setEncoding("ISO-8859-1");
    lBuilder.setDoctypePublic("Test");
    lBuilder.setDoctypeSystem("Test.dtd");
    lProperties = lBuilder.build();
    lOutputStream = new ByteArrayOutputStream();
    lXMLTools.writeDocument(lDocument, lProperties, lOutputStream);

    // Indentation behavior changed a little bit with Java 11.
    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_11)) {
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>" + System.lineSeparator()
              + "<!DOCTYPE Root PUBLIC \"Test\" \"Test.dtd\">" + System.lineSeparator()
              + "<Root xmlns=\"https://jeaf.de/xml/test\">" + System.lineSeparator()
              + "    <MessageFolder id=\"4711\"/>" + System.lineSeparator() + "</Root>" + System.lineSeparator(),
          lOutputStream.toString());
    }
    else {
      assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>" + System.lineSeparator()
          + "<!DOCTYPE Root PUBLIC \"Test\" \"Test.dtd\">" + System.lineSeparator()
          + "<Root xmlns=\"https://jeaf.de/xml/test\">" + System.lineSeparator() + "<MessageFolder id=\"4711\"/>"
          + System.lineSeparator() + "</Root>" + System.lineSeparator(), lOutputStream.toString());
    }

    // Test exception handling
    FileOutputStream lFileOutputStream = new FileOutputStream(File.createTempFile("jeaf-test-", ""));
    lFileOutputStream.close();
    try {
      lXMLTools.writeDocument(lDocument, lProperties, lFileOutputStream);
      fail("Exception expected when trying to write to closed stream.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_WRITE_XML, e.getErrorCode());
      assertEquals(TransformerException.class, e.getCause().getClass());
    }

    // Test exception handling
    try {
      lXMLTools.writeDocument(null, lProperties, lFileOutputStream);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lXMLTools.writeDocument(lDocument, lProperties, null);
      fail("Exception expected.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testToString( ) throws ParserConfigurationException, TransformerException {
    XMLTools lXMLTools = Tools.getXMLTools();
    Document lDocument = lXMLTools.createDocument();

    String lNamespace = "https://jeaf.de/xml/test";
    Element lRoot = lDocument.createElementNS(lNamespace, "Root");
    lDocument.appendChild(lRoot);

    String lString = lXMLTools.toString(lDocument);

    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + System.lineSeparator()
        + "<Root xmlns=\"https://jeaf.de/xml/test\"/>" + System.lineSeparator(), lString);

    Element lElement = lDocument.createElementNS(lNamespace, "MessageFolder");
    lElement.setAttribute("id", "4711");
    lString = lXMLTools.toString(lElement);

    assertEquals("<MessageFolder id=\"4711\" xmlns=\"https://jeaf.de/xml/test\"/>", lString);

    // Add child elements and check again.
    lElement.appendChild(lDocument.createElement("Message"));
    lElement.appendChild(lDocument.createElement("Message"));
    lString = lXMLTools.toString(lElement);

    // Indentation behavior changed a little bit with Java 11.
    if (XFun.getInfoProvider().getJavaRuntimeEnvironment().isEqualOrHigher(JavaRelease.JAVA_11)) {
      assertEquals("<MessageFolder id=\"4711\" xmlns=\"https://jeaf.de/xml/test\">" + System.lineSeparator()
          + "    <Message/>" + System.lineSeparator() + "    <Message/>" + System.lineSeparator() + "</MessageFolder>"
          + System.lineSeparator(), lString);
    }
    else {
      assertEquals("<MessageFolder id=\"4711\" xmlns=\"https://jeaf.de/xml/test\">" + System.lineSeparator()
          + "<Message/>" + System.lineSeparator() + "<Message/>" + System.lineSeparator() + "</MessageFolder>"
          + System.lineSeparator(), lString);
    }

    lString = lXMLTools.toString(lElement, false);
    assertEquals("<MessageFolder id=\"4711\" xmlns=\"https://jeaf.de/xml/test\"><Message/><Message/></MessageFolder>",
        lString);

    // Text exception handling
    try {
      lXMLTools.toString((Document) null);
      fail("Exception expected");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lXMLTools.toString((Document) null, null);
      fail("Exception expected");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
    try {
      lXMLTools.toString((Element) null);
      fail("Exception expected");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    try {
      lXMLTools.toString((Element) null, false);
      fail("Exception expected");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testXMLToolsConfiguration( ) {
    XMLToolsImpl lXMLTools = (XMLToolsImpl) Tools.getXMLTools();
    assertEquals(Tools.class, lXMLTools.getStartupCompletedEventSource());
    lXMLTools.traceStartupInfo(XFun.getTrace(), TraceLevel.INFO);

    // Test default configuration
    XMLToolsConfiguration lConfiguration = new XMLToolsConfiguration();
    XMLToolsConfig lAnnotation = DefaultToolsConfiguration.class.getAnnotation(XMLToolsConfig.class);
    List<String> lErrors = lConfiguration.checkCustomConfiguration(lAnnotation);
    assertEquals(0, lErrors.size());

    // Test invalid configuration
    lAnnotation = InvalidXMLConfigHolder.class.getAnnotation(XMLToolsConfig.class);
    lErrors = lConfiguration.checkCustomConfiguration(lAnnotation);
    assertEquals(1, lErrors.size());
    assertEquals("'documentBuilderPoolSize' must be zero or greater. Current value is -1", lErrors.get(0));

    // Test empty configuration
    XMLToolsConfig lEmptyConfiguration = lConfiguration.getEmptyConfiguration();
    assertEquals(XMLToolsConfig.class, lEmptyConfiguration.annotationType());
    assertEquals(XMLToolsConfig.DEFAULT_DOCUMENT_BUILDER_POOL_SIZE, lEmptyConfiguration.documenBuilderPoolSize());
  }

  @Test
  public void testErrorHandler( ) {
    ErrorHandler lErrorHandler = new ErrorHandlerImpl();
    SAXParseException lException = new SAXParseException("Something went wrong", null, null, 1, 1);
    try {
      lErrorHandler.warning(lException);
      fail("Exception expected to be thrown.");
    }
    catch (SAXException e) {
      assertEquals(lException, e);
    }

    try {
      lErrorHandler.error(lException);
      fail("Exception expected to be thrown.");
    }
    catch (SAXException e) {
      assertEquals(lException, e);
    }
    try {
      lErrorHandler.fatalError(lException);
      fail("Exception expected to be thrown.");
    }
    catch (SAXException e) {
      assertEquals(lException, e);
    }
  }

  @Test
  public void testPooledDocumentBuilder( ) {
    DocumentBuilderAllocator lAllocator = new DocumentBuilderAllocator(true);
    SlotImpl lSlot = new SlotImpl();
    PooledDocumentBuilder lPool = lAllocator.allocate(lSlot);
    DocumentBuilder lDocumentBuilder = lPool.getDocumentBuilder();
    assertNotNull(lDocumentBuilder);

    assertFalse(lSlot.released());
    lPool.release();
    assertTrue(lSlot.released());

    lAllocator.deallocate(lPool);
  }
}

class FileParser implements Runnable {
  String xmlString;

  @Override
  public void run( ) {
    Tools.getXMLTools().parseString(xmlString, false, null, "TestNoDTD.xml");
    XFun.getTrace().info(Thread.currentThread().getName() + " done.");
  }
}

class SlotImpl implements Slot {
  private boolean released = false;

  @Override
  public void release( Poolable pObj ) {
    released = true;
  }

  @Override
  public void expire( Poolable pObj ) {
  }

  public boolean released( ) {
    return released;
  }
}

@XMLToolsConfig(documenBuilderPoolSize = -1)
interface InvalidXMLConfigHolder {

}
