/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.monitoring;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.anaptecs.jeaf.tools.annotations.MonitoringToolsConfig;
import com.anaptecs.jeaf.tools.api.ToolsLoader;
import com.anaptecs.jeaf.tools.api.monitoring.MeterRegistryFactory;
import com.anaptecs.jeaf.tools.impl.DefaultToolsConfiguration;
import com.anaptecs.jeaf.xfun.api.config.AnnotationBasedConfiguration;

public class MonitoringToolsConfiguration extends AnnotationBasedConfiguration<MonitoringToolsConfig> {
  public MonitoringToolsConfiguration( ) {
    // Call super class constructor.
    this(MonitoringToolsConfig.MONITORING_TOOLS_CONFIG_RESOURCE_NAME, ToolsLoader.TOOLS_BASE_PATH, true);
  }

  public MonitoringToolsConfiguration( String pCustomConfigurationResourceName,
      String pCustomConfigurationBasePackagePath, boolean pExceptionOnError ) {
    super(pCustomConfigurationResourceName, pCustomConfigurationBasePackagePath, pExceptionOnError);
  }

  @Override
  protected Class<MonitoringToolsConfig> getAnnotationClass( ) {
    return MonitoringToolsConfig.class;
  }

  @Override
  protected String getDefaultConfigurationClass( ) {
    return DefaultToolsConfiguration.class.getName();
  }

  @Override
  public MonitoringToolsConfig getEmptyConfiguration( ) {
    return new MonitoringToolsConfig() {

      @Override
      public Class<? extends Annotation> annotationType( ) {
        return MonitoringToolsConfig.class;
      }

      @Override
      public Class<? extends MeterRegistryFactory> meterRegistryFactory( ) {
        return null;
      }

      @Override
      public TimeUnit defaultTimeUnit( ) {
        return MonitoringToolsConfig.DEFAULT_TIME_UNIT;
      }

      @Override
      public String domain( ) {
        return "";
      }

    };
  }

  @Override
  public List<String> checkCustomConfiguration( MonitoringToolsConfig pCustomConfiguration ) {
    List<String> lConfiguratonErrors = new ArrayList<>(0);
    this.tryNewInstance(pCustomConfiguration.meterRegistryFactory(), MeterRegistryFactory.class, lConfiguratonErrors);
    return lConfiguratonErrors;
  }

  public MeterRegistryFactory loadMeterRegistryFactory( ) {
    return this.newInstance(customConfig.meterRegistryFactory(), defaultConfig.meterRegistryFactory(),
        exceptionOnError);
  }

  public TimeUnit getDefaultTimeUnit( ) {
    return theConfig.defaultTimeUnit();
  }

  public String getDomain( ) {
    return theConfig.domain();
  }

}
