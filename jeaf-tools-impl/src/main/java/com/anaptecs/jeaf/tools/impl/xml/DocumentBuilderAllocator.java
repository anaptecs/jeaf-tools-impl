/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.xml;

import stormpot.Allocator;
import stormpot.Slot;

/**
 * Class is a factory class to create pooled xml document builders.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.5.x
 */
public class DocumentBuilderAllocator implements Allocator<PooledDocumentBuilder> {
  /**
   * Attribute defines if a validating document builder or not will be created.
   */
  private final boolean validating;

  /**
   * Initialize object.
   * 
   * @param pValidating Parameter defines if the document builder should be validating or not.
   */
  public DocumentBuilderAllocator( boolean pValidating ) {
    validating = pValidating;
  }

  /**
   * Method creates a new pooled document builder.
   * 
   * @return {@link PooledDocumentBuilder} New pooled document builder that was created. The method never returns null.
   */
  @Override
  public PooledDocumentBuilder allocate( Slot pSlot ) {
    return new PooledDocumentBuilder(pSlot, validating);
  }

  /**
   * Method will be called when a pooled document builder is no longer used.
   */
  @Override
  public void deallocate( PooledDocumentBuilder pPoolable ) {
    // Nothing to do.
  }
}
