/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.encryption;

import com.anaptecs.jeaf.tools.annotations.EncryptionToolsConfig;
import com.anaptecs.jeaf.tools.api.encryption.AESKeyLength;

@EncryptionToolsConfig(
    aesDefaultKeyLength = AESKeyLength.AES_128,
    secureTokenRandomAlgorithm = "SHA1PRNG_TEST",
    rsaAlgorithm = "MyRSA",
    rsaTransform = "MyTransform1024")
public interface EncryptionConfig {

}
