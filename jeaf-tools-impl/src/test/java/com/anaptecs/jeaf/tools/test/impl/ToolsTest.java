/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.impl.cache.CacheToolsImpl;
import com.anaptecs.jeaf.tools.impl.collections.CollectionToolsImpl;
import com.anaptecs.jeaf.tools.impl.date.DateToolsImpl;
import com.anaptecs.jeaf.tools.impl.encoding.EncodingToolsImpl;
import com.anaptecs.jeaf.tools.impl.encryption.EncryptionToolsImpl;
import com.anaptecs.jeaf.tools.impl.file.FileToolsImpl;
import com.anaptecs.jeaf.tools.impl.http.WebToolsImpl;
import com.anaptecs.jeaf.tools.impl.lang.AutoBoxingToolsImpl;
import com.anaptecs.jeaf.tools.impl.locale.LocaleToolsImpl;
import com.anaptecs.jeaf.tools.impl.monitoring.MonitoringToolsImpl;
import com.anaptecs.jeaf.tools.impl.network.NetworkingToolsImpl;
import com.anaptecs.jeaf.tools.impl.performance.PerformanceToolsImpl;
import com.anaptecs.jeaf.tools.impl.pooling.PoolingToolsImpl;
import com.anaptecs.jeaf.tools.impl.reflect.ReflectionToolsImpl;
import com.anaptecs.jeaf.tools.impl.regexp.RegExpToolsImpl;
import com.anaptecs.jeaf.tools.impl.serialization.SerializationToolsImpl;
import com.anaptecs.jeaf.tools.impl.ssl.SSLToolsImpl;
import com.anaptecs.jeaf.tools.impl.stream.StreamToolsImpl;
import com.anaptecs.jeaf.tools.impl.string.StringToolsImpl;
import com.anaptecs.jeaf.tools.impl.validation.ValidationToolsImpl;
import com.anaptecs.jeaf.tools.impl.xml.XMLToolsImpl;

public class ToolsTest {
  @Test
  void testToolsConfiguration( ) {
    assertEquals(AutoBoxingToolsImpl.class, Tools.getAutoBoxingTools().getClass());
    assertEquals(CacheToolsImpl.class, Tools.getCacheTools().getClass());
    assertEquals(CollectionToolsImpl.class, Tools.getCollectionTools().getClass());
    assertEquals(DateToolsImpl.class, Tools.getDateTools().getClass());
    assertEquals(EncodingToolsImpl.class, Tools.getEncodingTools().getClass());
    assertEquals(EncryptionToolsImpl.class, Tools.getEncryptionTools().getClass());
    assertEquals(FileToolsImpl.class, Tools.getFileTools().getClass());
    assertEquals(LocaleToolsImpl.class, Tools.getLocaleTools().getClass());
    assertEquals(MonitoringToolsImpl.class, Tools.getMonitoringTools().getClass());
    assertEquals(NetworkingToolsImpl.class, Tools.getNetworkingTools().getClass());
    assertEquals(PerformanceToolsImpl.class, Tools.getPerformanceTools().getClass());
    assertEquals(PoolingToolsImpl.class, Tools.getPoolingTools().getClass());
    assertEquals(ReflectionToolsImpl.class, Tools.getReflectionTools().getClass());
    assertEquals(RegExpToolsImpl.class, Tools.getRegExpTools().getClass());
    assertEquals(SerializationToolsImpl.class, Tools.getSerializationTools().getClass());
    assertEquals(SSLToolsImpl.class, Tools.getSSLTools().getClass());
    assertEquals(StreamToolsImpl.class, Tools.getStreamTools().getClass());
    assertEquals(StringToolsImpl.class, Tools.getStringTools().getClass());
    assertEquals(ValidationToolsImpl.class, Tools.getValidationTools().getClass());
    assertEquals(WebToolsImpl.class, Tools.getWebTools().getClass());
    assertEquals(XMLToolsImpl.class, Tools.getXMLTools().getClass());
  }
}
