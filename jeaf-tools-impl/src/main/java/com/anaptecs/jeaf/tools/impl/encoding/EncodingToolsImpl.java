/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.encoding;

import java.nio.charset.Charset;

import com.anaptecs.jeaf.tools.annotations.EncodingToolsConfig;
import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.encoding.EncodingTools;

/**
 * Class provides an implementation of JEAF's Encoding Tools ({@link EncodingTools}).
 * 
 * @author JEAF Development Team
 */
@ToolsImplementation(toolsInterface = EncodingTools.class)
public class EncodingToolsImpl implements EncodingTools {
  /**
   * Default charset that should be used.
   */
  private final Charset defaultCharset;

  /**
   * Initialize object.
   */
  public EncodingToolsImpl( ) {
    EncodingToolsConfiguration lConfiguration = new EncodingToolsConfiguration();
    defaultCharset = lConfiguration.getDefaultCharset();
  }

  /**
   * Method returns the default charset / encoding as it is configured for JEAF Tools through annotation
   * {@link EncodingToolsConfig}
   * 
   * @return {@link Charset} Charset that is defined as default for JEAF Tools. The method never returns null.
   */
  @Override
  public Charset getDefaultCharset( ) {
    return defaultCharset;
  }

  /**
   * Method returns the name of the default charset as it is defined.
   * 
   * @return String Name of the defined default charset. The method never returns null.
   */
  @Override
  public String getDefaultCharsetName( ) {
    return defaultCharset.name();
  }

}
