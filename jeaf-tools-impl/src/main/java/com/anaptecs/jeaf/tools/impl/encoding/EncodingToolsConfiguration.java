/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.encoding;

import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

import com.anaptecs.jeaf.tools.annotations.EncodingToolsConfig;
import com.anaptecs.jeaf.tools.api.ToolsLoader;
import com.anaptecs.jeaf.tools.impl.DefaultToolsConfiguration;
import com.anaptecs.jeaf.xfun.api.config.AnnotationBasedConfiguration;

public class EncodingToolsConfiguration extends AnnotationBasedConfiguration<EncodingToolsConfig> {

  public EncodingToolsConfiguration( ) {
    // Call super class constructor.
    this(EncodingToolsConfig.ENCODING_TOOLS_CONFIG_RESOURCE_NAME, ToolsLoader.TOOLS_BASE_PATH, true);
  }

  public EncodingToolsConfiguration( String pCustomConfigurationResourceName,
      String pCustomConfigurationBasePackagePath, boolean pExceptionOnError ) {
    super(pCustomConfigurationResourceName, pCustomConfigurationBasePackagePath, pExceptionOnError);
  }

  @Override
  protected Class<EncodingToolsConfig> getAnnotationClass( ) {
    return EncodingToolsConfig.class;
  }

  @Override
  protected String getDefaultConfigurationClass( ) {
    return DefaultToolsConfiguration.class.getName();
  }

  @Override
  public EncodingToolsConfig getEmptyConfiguration( ) {
    return new EncodingToolsConfig() {

      @Override
      public Class<? extends Annotation> annotationType( ) {
        return EncodingToolsConfig.class;
      }

      @Override
      public boolean useSystemDefaultEncoding( ) {
        return EncodingToolsConfig.SYSTEM_DEFAULT_ENCODING_ENABLED;
      }

      @Override
      public String defaultEncoding( ) {
        return EncodingToolsConfig.DEFAULT_ENCODING;
      }
    };
  }

  @Override
  public List<String> checkCustomConfiguration( EncodingToolsConfig pCustomConfiguration ) {
    // Ensure that configured encoding is correct.
    List<String> lErrors;
    if (pCustomConfiguration.useSystemDefaultEncoding() == false) {
      // Try to lookup charset. If no exception occurs then everything is fine.
      try {
        Charset.forName(pCustomConfiguration.defaultEncoding());
        lErrors = null;
      }
      catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
        lErrors = new ArrayList<>(1);
        lErrors.add(e.getMessage());
      }
    }
    else {
      lErrors = null;
    }
    return lErrors;
  }

  /**
   * Method returns if the system default encoding should be used or not.
   * 
   * @return boolean Method returns true if Java default should be used.
   */
  public boolean useSystemDefaultEncoding( ) {
    return theConfig.useSystemDefaultEncoding();
  }

  /**
   * Method returns the configured default charset.
   * 
   * @return {@link Charset} Charset that should be used. The method never returns null.
   */
  public Charset getDefaultCharset( ) {
    Charset lDefaultCharset;

    // Use Java default charset.
    if (theConfig.useSystemDefaultEncoding() == true) {
      lDefaultCharset = Charset.defaultCharset();
    }
    // Use configured charset.
    else {
      lDefaultCharset = Charset.forName(theConfig.defaultEncoding());
    }
    return lDefaultCharset;
  }
}
