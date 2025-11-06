/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.encryption;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import com.anaptecs.jeaf.tools.annotations.EncryptionToolsConfig;
import com.anaptecs.jeaf.tools.api.ToolsLoader;
import com.anaptecs.jeaf.tools.api.encryption.AESKeyLength;
import com.anaptecs.jeaf.tools.impl.DefaultToolsConfiguration;
import com.anaptecs.jeaf.xfun.api.config.AnnotationBasedConfiguration;

public class EncryptionToolsConfiguration extends AnnotationBasedConfiguration<EncryptionToolsConfig> {

  public EncryptionToolsConfiguration( ) {
    // Call super class constructor.
    this(EncryptionToolsConfig.ENCRYPTION_TOOLS_CONFIG_RESOURCE_NAME, ToolsLoader.TOOLS_BASE_PATH, true);
  }

  public EncryptionToolsConfiguration( String pCustomConfigurationResourceName,
      String pCustomConfigurationBasePackagePath, boolean pExceptionOnError ) {
    super(pCustomConfigurationResourceName, pCustomConfigurationBasePackagePath, pExceptionOnError);
  }

  @Override
  protected Class<EncryptionToolsConfig> getAnnotationClass( ) {
    return EncryptionToolsConfig.class;
  }

  @Override
  protected String getDefaultConfigurationClass( ) {
    return DefaultToolsConfiguration.class.getName();
  }

  @Override
  public EncryptionToolsConfig getEmptyConfiguration( ) {
    return new EncryptionToolsConfig() {

      @Override
      public Class<? extends Annotation> annotationType( ) {
        return EncryptionToolsConfig.class;
      }

      @Override
      public String secureTokenRandomAlgorithm( ) {
        return null;
      }

      @Override
      public String aesTransformation( ) {
        return null;
      }

      @Override
      public AESKeyLength aesDefaultKeyLength( ) {
        return null;
      }

      @Override
      public String rsaAlgorithm( ) {
        return null;
      }

      @Override
      public String rsaTransform( ) {
        return null;
      }
    };
  }

  @Override
  public List<String> checkCustomConfiguration( EncryptionToolsConfig pCustomConfiguration ) {
    // Nothing to do.
    return Collections.emptyList();
  }

  /**
   * Method returns the configured AES default key length.
   * 
   * @return {@link AESKeyLength} Configured AES key length. The method never returns null.
   */
  public AESKeyLength getAESDefaultKeyLength( ) {
    return theConfig.aesDefaultKeyLength();
  }

  /**
   * Method returns the configured AES transformation. The AES transformation defines the variant of the AES algorithm
   * that should be used.
   * 
   * @return {@link String} AES transformation that should be used when working with AES.
   */
  public String getAESTransformation( ) {
    return theConfig.aesTransformation();
  }

  /**
   * Method returns the algorithm that should be used to generate random numbers for secure tokens.
   * 
   * @return {@link String} Algorithm that should be used. The method never returns null.
   */
  public String getSecureTokenRandomAlgorithm( ) {
    return theConfig.secureTokenRandomAlgorithm();
  }

  /**
   * Method returns the RSA algorithm that should be used to for RSA keys.
   * 
   * @return {@link String} Algorithm that should be used for RSA keys. The method never returns null.
   */
  public String getRSAAlgorithm( ) {
    return theConfig.rsaAlgorithm();
  }

  /**
   * Method returns the RSA transformation that should be used to encrypt and decrypt messages using RSA.
   * 
   * @return {@link String} RSA transformation that should be used. The method never returns null.
   */
  public String getRSATransformation( ) {
    return theConfig.rsaTransform();
  }
}
