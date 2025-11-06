/**
 * Copyright 2004 - 2019 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl;

import com.anaptecs.jeaf.tools.annotations.EncodingToolsConfig;
import com.anaptecs.jeaf.tools.annotations.EncryptionToolsConfig;
import com.anaptecs.jeaf.tools.annotations.MonitoringToolsConfig;
import com.anaptecs.jeaf.tools.annotations.NetworkingToolsConfig;
import com.anaptecs.jeaf.tools.annotations.StreamToolsConfig;
import com.anaptecs.jeaf.tools.annotations.XMLToolsConfig;
import com.anaptecs.jeaf.tools.impl.monitoring.simple.SimpleMeterRegistryFactory;

/**
 * Interface with its annotation defines the default configuration for JEAF Tools
 */

// We will just use the default values of encryption tools configuration.
@EncryptionToolsConfig

// We will just use the default values of encoding tools configuration.
@EncodingToolsConfig

@MonitoringToolsConfig(meterRegistryFactory = SimpleMeterRegistryFactory.class)

// We will just use the default values of networking tools configuration.
@NetworkingToolsConfig

// We will just use the default values of stream tools configuration.
@StreamToolsConfig

// We will just use the default values of XML tools configuration.
@XMLToolsConfig

public interface DefaultToolsConfiguration {

}
