/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.network;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anaptecs.jeaf.tools.annotations.NetworkingToolsConfig;
import com.anaptecs.jeaf.tools.api.ToolsLoader;
import com.anaptecs.jeaf.tools.impl.DefaultToolsConfiguration;
import com.anaptecs.jeaf.xfun.api.config.AnnotationBasedConfiguration;

public class NetworkingToolsConfiguration extends AnnotationBasedConfiguration<NetworkingToolsConfig> {
  public NetworkingToolsConfiguration( ) {
    // Call super class constructor.
    this(NetworkingToolsConfig.NETWORKING_TOOLS_CONFIG_RESOURCE_NAME, ToolsLoader.TOOLS_BASE_PATH, true);
  }

  public NetworkingToolsConfiguration( String pCustomConfigurationResourceName,
      String pCustomConfigurationBasePackagePath, boolean pExceptionOnError ) {
    super(pCustomConfigurationResourceName, pCustomConfigurationBasePackagePath, pExceptionOnError);
  }

  @Override
  protected Class<NetworkingToolsConfig> getAnnotationClass( ) {
    return NetworkingToolsConfig.class;
  }

  @Override
  protected String getDefaultConfigurationClass( ) {
    return DefaultToolsConfiguration.class.getName();
  }

  @Override
  public NetworkingToolsConfig getEmptyConfiguration( ) {
    return new NetworkingToolsConfig() {

      @Override
      public Class<? extends Annotation> annotationType( ) {
        return NetworkingToolsConfig.class;
      }

      @Override
      public int pingTimeout( ) {
        return NetworkingToolsConfig.DEFAULT_PING_TIMEOUT;
      }
    };
  }

  @Override
  public List<String> checkCustomConfiguration( NetworkingToolsConfig pCustomConfiguration ) {
    // Check that timeout is not negative
    List<String> lErrors;
    if (pCustomConfiguration.pingTimeout() < 0) {
      lErrors = new ArrayList<>();
      lErrors.add("'pingTimeout' must be zero or greater. Current value is " + pCustomConfiguration.pingTimeout());
    }
    else {
      lErrors = Collections.emptyList();
    }
    return lErrors;
  }

  public int getPingTimeout( ) {
    return theConfig.pingTimeout();
  }

}
