/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;

import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

import stormpot.Poolable;
import stormpot.Slot;

/**
 * Class implements a poolable document builder. As performance analysis showed when parsing small XML documents a
 * rather high amount of time is spent by creating document builders. To avoid this poolable document builders can be
 * used.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.5.x
 */
public class PooledDocumentBuilder implements Poolable {
  /**
   * Location inside a pool where this instance is stored.
   */
  private final Slot slot;

  /**
   * Wrapped document builder that should be pooled.
   */
  private final DocumentBuilder documentBuilder;

  /**
   * Error handler that is used by default. This error handler may be overwritten by users of the document builder.
   */
  private final ErrorHandler errorHandler;

  /**
   * Initialize object.
   * 
   * @param pSlot Slot where the pooled object will be stored.
   * @param pValidating Parameter defines if the document builder should be validating or not.
   */
  PooledDocumentBuilder( Slot pSlot, boolean pValidating ) {
    slot = pSlot;

    // Try to create document builder.
    try {
      DocumentBuilderFactory lFactory = XMLToolsImpl.createSecuredDocumentBuilderFactory(pValidating);
      documentBuilder = lFactory.newDocumentBuilder();
      errorHandler = new ErrorHandlerImpl();
      documentBuilder.setErrorHandler(errorHandler);
    }
    catch (ParserConfigurationException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_CREATE_DOCUMENT_BUILDER, e);
    }
  }

  /**
   * Operation has to be used to indicate that the document builder is no longer used and should be returned to the
   * pool. This will cause that all allocated resources will be released.
   */
  @Override
  public void release( ) {
    // Return document builder to pool
    slot.release(this);

    // Reset all settings of document build and return it into its initial state.
    documentBuilder.reset();
    documentBuilder.setErrorHandler(errorHandler);
  }

  /**
   * Method returns that actual document builder.
   * 
   * @return {@link DocumentBuilder} Document build that is pooled through this class. The method never returns null.
   */
  public DocumentBuilder getDocumentBuilder( ) {
    return documentBuilder;
  }
}
