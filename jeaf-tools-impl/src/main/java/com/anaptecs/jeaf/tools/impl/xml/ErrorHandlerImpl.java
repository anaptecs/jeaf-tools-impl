/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class implements SAX-ErrorHandler-Interface. Objects of this class react on all occurring errors or warnings by
 * throwing the exception object that is passed to the called method.
 * 
 * @author JEAF Development Team
 * @version 1.0
 * 
 * @see org.xml.sax.ErrorHandler
 */
public class ErrorHandlerImpl implements ErrorHandler {
  /**
   * Empty default constructor.
   */
  public ErrorHandlerImpl( ) {
    // Nothing to do.
  }

  /**
   * Inherited method from interface ErrorHandler. Method is called whenever a DocumentBuilder detects a warning during
   * a parse process of an XML document.
   * 
   * @param pException SAXParseException that represents the detected waring. The exception is always thrown as
   * reaction.
   * @throws SAXException Method always throws the passed exception object.
   * 
   * @see ErrorHandler#warning(SAXParseException)
   */
  public void warning( SAXParseException pException ) throws SAXException {
    // Throw passed exception object.
    throw pException;
  }

  /**
   * Inherited method from interface ErrorHandler. Method is called whenever a DocumentBuilder detects an error during a
   * parse process of an XML document.
   * 
   * @param pException SAXParseException that represents the detected error. The exception is always thrown as reaction.
   * @throws SAXException Method always throws the passed exception object.
   * 
   * @see ErrorHandler#error(SAXParseException)
   */
  public void error( SAXParseException pException ) throws SAXException {
    // Throw passed exception object.
    throw pException;
  }

  /**
   * Inherited method from interface ErrorHandler. Method is called whenever a DocumentBuilder detects a fatal error
   * during a parse process of an XML document.
   * 
   * @param pException SAXParseException that represents the detected fatal error. The exception is always thrown as
   * reaction.
   * @throws SAXException Method always throws the passed exception object.
   * 
   * @see ErrorHandler#fatalError(SAXParseException)
   */
  public void fatalError( SAXParseException pException ) throws SAXException {
    // Throw passed exception object.
    throw pException;
  }

}