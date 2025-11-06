/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.stream;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.anaptecs.jeaf.tools.annotations.StreamToolsConfig;
import com.anaptecs.jeaf.tools.api.ToolsLoader;
import com.anaptecs.jeaf.tools.impl.DefaultToolsConfiguration;
import com.anaptecs.jeaf.xfun.api.config.AnnotationBasedConfiguration;

public class StreamToolsConfiguration extends AnnotationBasedConfiguration<StreamToolsConfig> {

  public StreamToolsConfiguration( ) {
    // Call super class constructor.
    this(StreamToolsConfig.STREAM_TOOLS_CONFIG_RESOURCE_NAME, ToolsLoader.TOOLS_BASE_PATH, true);
  }

  public StreamToolsConfiguration( String pCustomConfigurationResourceName, String pCustomConfigurationBasePackagePath,
      boolean pExceptionOnError ) {
    super(pCustomConfigurationResourceName, pCustomConfigurationBasePackagePath, pExceptionOnError);
  }

  @Override
  protected Class<StreamToolsConfig> getAnnotationClass( ) {
    return StreamToolsConfig.class;
  }

  @Override
  protected String getDefaultConfigurationClass( ) {
    return DefaultToolsConfiguration.class.getName();
  }

  @Override
  public StreamToolsConfig getEmptyConfiguration( ) {
    return new StreamToolsConfig() {

      @Override
      public Class<? extends Annotation> annotationType( ) {
        return StreamToolsConfig.class;
      }

      @Override
      public int bufferSize( ) {
        return StreamToolsConfig.DEFAULT_BUFFER_SIZE;
      }

      @Override
      public int bufferPoolSize( ) {
        return StreamToolsConfig.DEFAULT_BUFFER_POOL_SIZE;
      }
    };
  }

  @Override
  public List<String> checkCustomConfiguration( StreamToolsConfig pCustomConfiguration ) {
    List<String> lErrors;
    // Configuration is correct
    if (pCustomConfiguration.bufferSize() > 0) {
      lErrors = null;
    }
    // Buffer size must be greater the zero.
    else {
      lErrors = new ArrayList<>(1);
      lErrors.add("Illegal value for 'bufferSize'. Configured value is " + pCustomConfiguration.bufferSize());
    }
    return lErrors;
  }

  /**
   * Method returns the configured buffer size for all kind of stream activities.
   * 
   * @return int Configured buffer size.
   */
  public int getBufferSize( ) {
    return theConfig.bufferSize();
  }

  /**
   * Method returns the size of the buffer pool that should be used when copying data from one stream to another.
   * 
   * @return Size of the buffer pool.
   */
  public int getBufferPoolSize( ) {
    return theConfig.bufferPoolSize();
  }
}
