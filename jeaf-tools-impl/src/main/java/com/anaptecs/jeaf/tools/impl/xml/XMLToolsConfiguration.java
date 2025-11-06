/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.xml;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anaptecs.jeaf.tools.annotations.XMLToolsConfig;
import com.anaptecs.jeaf.tools.api.ToolsLoader;
import com.anaptecs.jeaf.tools.impl.DefaultToolsConfiguration;
import com.anaptecs.jeaf.xfun.api.config.AnnotationBasedConfiguration;

public class XMLToolsConfiguration extends AnnotationBasedConfiguration<XMLToolsConfig> {
  public XMLToolsConfiguration( ) {
    // Call super class constructor.
    this(XMLToolsConfig.XML_TOOLS_CONFIG_RESOURCE_NAME, ToolsLoader.TOOLS_BASE_PATH, true);
  }

  public XMLToolsConfiguration( String pCustomConfigurationResourceName, String pCustomConfigurationBasePackagePath,
      boolean pExceptionOnError ) {
    super(pCustomConfigurationResourceName, pCustomConfigurationBasePackagePath, pExceptionOnError);
  }

  @Override
  protected Class<XMLToolsConfig> getAnnotationClass( ) {
    return XMLToolsConfig.class;
  }

  @Override
  protected String getDefaultConfigurationClass( ) {
    return DefaultToolsConfiguration.class.getName();
  }

  @Override
  public XMLToolsConfig getEmptyConfiguration( ) {
    return new XMLToolsConfig() {

      @Override
      public Class<? extends Annotation> annotationType( ) {
        return XMLToolsConfig.class;
      }

      @Override
      public int documenBuilderPoolSize( ) {
        return XMLToolsConfig.DEFAULT_DOCUMENT_BUILDER_POOL_SIZE;
      }
    };
  }

  @Override
  public List<String> checkCustomConfiguration( XMLToolsConfig pCustomConfiguration ) {
    // Check that pool size is not negative
    List<String> lErrors;
    if (pCustomConfiguration.documenBuilderPoolSize() < 0) {
      lErrors = new ArrayList<>();
      lErrors.add("'documentBuilderPoolSize' must be zero or greater. Current value is "
          + pCustomConfiguration.documenBuilderPoolSize());
    }
    else {
      lErrors = Collections.emptyList();
    }
    return lErrors;
  }

  public int getDocumentBuilderPoolSize( ) {
    return theConfig.documenBuilderPoolSize();
  }
}
