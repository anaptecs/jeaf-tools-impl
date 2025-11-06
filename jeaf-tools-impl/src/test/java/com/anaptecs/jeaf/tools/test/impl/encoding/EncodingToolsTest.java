/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.tools.annotations.EncodingToolsConfig;
import com.anaptecs.jeaf.tools.api.encoding.EncodingTools;
import com.anaptecs.jeaf.tools.impl.encoding.EncodingToolsConfiguration;

@EncodingToolsConfig(useSystemDefaultEncoding = false, defaultEncoding = "ISO-8859-1")
public class EncodingToolsTest {
  @Test
  public void testEncondingConfiguration( ) {
    // Test default configuration.
    EncodingToolsConfiguration lConfiguration = new EncodingToolsConfiguration();
    assertEquals(true, lConfiguration.useSystemDefaultEncoding());
    assertEquals(Charset.defaultCharset(), lConfiguration.getDefaultCharset());

    // Test empty configuration
    EncodingToolsConfig lEmptyConfiguration = lConfiguration.getEmptyConfiguration();
    assertEquals(EncodingToolsConfig.class, lEmptyConfiguration.annotationType());
    assertEquals(true, lEmptyConfiguration.useSystemDefaultEncoding());
    assertEquals("UTF-8", lEmptyConfiguration.defaultEncoding());
    assertEquals(null, lConfiguration.checkCustomConfiguration(lEmptyConfiguration));

    // Test custom configuration
    lConfiguration = new EncodingToolsConfiguration("MyEncodingConfig", "META-INF", true);
    assertEquals(false, lConfiguration.useSystemDefaultEncoding());
    assertEquals(StandardCharsets.ISO_8859_1, lConfiguration.getDefaultCharset());
    assertEquals(null,
        lConfiguration.checkCustomConfiguration(EncodingToolsTest.class.getAnnotation(EncodingToolsConfig.class)));

    // Test invalid configuration
    lConfiguration = new EncodingToolsConfiguration("InvalidEncodingConfig", "META-INF", true);
    assertEquals(false, lConfiguration.useSystemDefaultEncoding());
    List<String> lErrors = lConfiguration
        .checkCustomConfiguration(InvalidEncodingConfiguration.class.getAnnotation(EncodingToolsConfig.class));
    assertEquals(1, lErrors.size());
    assertEquals("Invalid", lErrors.get(0));
  }

  @Test
  public void testEncodingTools( ) {
    EncodingTools lEncodingTools = EncodingTools.getEncodingTools();
    assertEquals(Charset.defaultCharset(), lEncodingTools.getDefaultCharset());
    assertEquals(Charset.defaultCharset().name(), lEncodingTools.getDefaultCharsetName());
  }
}

@EncodingToolsConfig(useSystemDefaultEncoding = false, defaultEncoding = "Invalid")
interface InvalidEncodingConfiguration {
}
