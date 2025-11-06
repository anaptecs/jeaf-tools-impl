/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.anaptecs.jeaf.tools.annotations.EncryptionToolsConfig;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.encryption.AESEncrypted;
import com.anaptecs.jeaf.tools.api.encryption.AESKeyLength;
import com.anaptecs.jeaf.tools.api.encryption.AESSecretKey;
import com.anaptecs.jeaf.tools.api.encryption.EncryptionTools;
import com.anaptecs.jeaf.tools.impl.encryption.EncryptionToolsConfiguration;
import com.anaptecs.jeaf.tools.impl.encryption.SecureTokenGenerator;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

import junit.framework.TestCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EncryptionToolsTest {

  private static final int LOOPS = 100;

  private static String encryptedString;

  private static String testString;

  private static String encryptedString2;

  /** The Constant default hash iterations. */
  private static final int DEFAULT_HASH_ITERATIONS = 1000;

  private static PublicKey publicKey;

  private static PrivateKey privateKey;

  /**
   * Test checks the encryption functionality of the EncryptionTools.
   */
  @Test
  @Order(10)
  public void testEncryptString( ) {
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();

    testString = "username;password";

    encryptedString = lEncryptionTools.encryptAES(testString, AESKeyLength.AES_128, DEFAULT_HASH_ITERATIONS);

    TestCase.assertNotNull("The encrypted String must not be null", encryptedString);
    TestCase.assertNotSame("The two Strings must not be the same.", testString, encryptedString);
    TestCase.assertEquals("Encryption does not produce stable results.", "Y1v4mxFsoQvi8BNN7IRkyL3AFx24iD95z175T504HcM=",
        encryptedString);

    encryptedString2 = lEncryptionTools.encryptAES(testString, AESKeyLength.AES_128, DEFAULT_HASH_ITERATIONS);

    TestCase.assertNotNull("The encrypted String must not be null", encryptedString2);
    TestCase.assertNotSame("The two Strings must not be the same.", testString, encryptedString2);
    TestCase.assertEquals("The two Strings must not be the same.", encryptedString, encryptedString2);

    // Try to encrypt null value
    String lEncryptedNull = lEncryptionTools.encryptAES(null, AESKeyLength.AES_128, DEFAULT_HASH_ITERATIONS);
    TestCase.assertNull("The encrypted String must not be null", lEncryptedNull);
    String lDecryptedNull = lEncryptionTools.decryptAES(lEncryptedNull, AESKeyLength.AES_128, DEFAULT_HASH_ITERATIONS);
    TestCase.assertNull("The encrypted String must be null", lDecryptedNull);

  }

  /**
   * Test checks the decoding functionality of the EncryptionTools
   */
  @Test
  @Order(20)
  public void testDecryptString( ) {

    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();

    String lDecryptedString =
        lEncryptionTools.decryptAES(encryptedString, AESKeyLength.AES_128, DEFAULT_HASH_ITERATIONS);

    TestCase.assertNotNull("The encrypted String must not be null", lDecryptedString);
    TestCase.assertEquals("The two Strings must be the same.", testString, lDecryptedString);

    String lDecryptedString2 =
        lEncryptionTools.decryptAES(encryptedString2, AESKeyLength.AES_128, DEFAULT_HASH_ITERATIONS);

    TestCase.assertNotNull("The encrypted String must not be null", lDecryptedString2);
    TestCase.assertEquals("The two Strings must be the same.", testString, lDecryptedString2);
    TestCase.assertEquals("The two Strings must be the same.", lDecryptedString, lDecryptedString2);
  }

  @Test
  public void testAESEncryption( ) throws GeneralSecurityException {
    // Test how to generate a secure key.
    KeyGenerator lKeyGenerator = KeyGenerator.getInstance("AES");
    lKeyGenerator.init(256);
    SecretKey lNewKey = lKeyGenerator.generateKey();
    System.out.println(new String(lNewKey.getEncoded(), StandardCharsets.UTF_8));

    String lKey = "ThisIsMyVeryPrivateKey";
    String lSalt = "Salt and Pepper?";
    AESKeyLength lKeyLength = AESKeyLength.AES_256;
    int lIterations = 4711;
    String lHashAlgorithm = EncryptionTools.PBKDF2_WITH_HMAC_SHA1;
    AESSecretKey lSecretKey = new AESSecretKey(lKey, lSalt, lKeyLength, lHashAlgorithm, lIterations);

    String lPlainText = "123456789 ���! ���$. Hello World of encryption. Lets see if we got it ;-)";
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();

    // Encrypt using AES and check result
    AESEncrypted lEncrypted = lEncryptionTools.encrypt(lPlainText, lSecretKey);
    TestCase.assertNotNull("Encryption result expected.", lEncrypted);
    byte[] lPlainBytes = lPlainText.getBytes();
    byte[] lCipherTextBytes = lEncrypted.getCipherText();
    TestCase.assertFalse("Plain text and encrypted text must not be the same",
        Arrays.equals(lPlainBytes, lCipherTextBytes));
    TestCase.assertNotNull("IV missing.", lEncrypted.getIV());

    // Test string conversions
    AESEncrypted lNewCipher = new AESEncrypted(lEncrypted.getIVAsString(), lEncrypted.getCipherTextAsString());
    TestCase.assertTrue("Error in IV string conversion.", Arrays.equals(lEncrypted.getIV(), lNewCipher.getIV()));
    TestCase.assertTrue("Error in cipher text string conversion.",
        Arrays.equals(lEncrypted.getCipherText(), lNewCipher.getCipherText()));

    // Test decryption
    String lDecryptResult = lEncryptionTools.decrypt(lEncrypted, lSecretKey);
    TestCase.assertEquals("Decrypted string is not equal to plain text.", lPlainText, lDecryptResult);

    // Test encryption of null
    lEncrypted = lEncryptionTools.encrypt(null, lSecretKey);
    TestCase.assertNull("Cipher text must be null.", lEncrypted.getCipherText());
    TestCase.assertNull("IV must be null.", lEncrypted.getIV());
    String lDecryptedText = lEncryptionTools.decrypt(lEncrypted, lSecretKey);
    TestCase.assertNull("Decrypted string is not equal to plain text.", lDecryptedText);

    // Test encryption with invalid key.
    AESSecretKey lInvalidSecretKey = new AESSecretKey(new InvalidSecretKey());
    try {
      lEncryptionTools.encrypt(lPlainText, lInvalidSecretKey);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_ENCRYPT_STRING, e.getErrorCode());
    }
    // Test decryption with invalid key.
    try {
      lEncryptionTools.decrypt(lNewCipher, lInvalidSecretKey);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_DECRYPT_STRING, e.getErrorCode());
    }
  }

  @Test
  public void testAESPerformance128_500( ) {
    this.testAESPerformance(AESKeyLength.AES_128, 500);
  }

  @Test
  public void testAESPerformance192_500( ) {
    this.testAESPerformance(AESKeyLength.AES_192, 500);
  }

  @Test
  public void testAESPerformance256_500( ) {
    this.testAESPerformance(AESKeyLength.AES_256, 500);
  }

  @Test
  public void testAESPerformance128_1000( ) {
    this.testAESPerformance(AESKeyLength.AES_128, 1000);
  }

  @Test
  public void testAESPerformance256_1000( ) {
    this.testAESPerformance(AESKeyLength.AES_256, 1000);
  }

  @Test
  public void testAESPerformance128_10000( ) {
    this.testAESPerformance(AESKeyLength.AES_128, 10000);
  }

  @Test
  public void testAESPerformance256_10000( ) {
    this.testAESPerformance(AESKeyLength.AES_256, 10000);
  }

  private void testAESPerformance( AESKeyLength pKeyLength, int pIterations ) {
    String lKey = "ThisIsMyVeryPrivateKey";
    String lSalt = "Salt and Pepper?";
    String lHashAlgorithm = EncryptionTools.PBKDF2_WITH_HMAC_SHA1;
    AESSecretKey lSecretKey = new AESSecretKey(lKey, lSalt, pKeyLength, lHashAlgorithm, pIterations);

    String lPlainText = "123456789 ���! ���$. Hello World of encryption. Lets see if we got it ;-)";
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();

    // Encrypt using AES and check result
    for (int i = 0; i < LOOPS; i++) {
      AESEncrypted lCipher = lEncryptionTools.encrypt(lPlainText, lSecretKey);
      TestCase.assertNotNull("Encryption result expected.", lCipher);
      TestCase.assertFalse("Plain text and encrypted text must not be the same",
          Arrays.equals(lPlainText.getBytes(), lCipher.getCipherText()));
      TestCase.assertNotNull("IV missing.", lCipher.getIV());

      String lDecryptResult = lEncryptionTools.decrypt(lCipher, lSecretKey);
      TestCase.assertEquals("Decrypted string is not equal to plain text.", lPlainText, lDecryptResult);
    }
  }

  @Test
  public void testRSAEnryption( ) throws IOException {
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();
    KeyPair lKeyPair = lEncryptionTools.generateRSAKeyPair(4096);
    byte[] lPublicKeyBytes = lKeyPair.getPublic().getEncoded();
    byte[] lPrivateKeyBytes = lKeyPair.getPrivate().getEncoded();

    System.out.println("Public-Key: " + lEncryptionTools.getKeyAsBase64(lKeyPair.getPublic()));
    System.out.println("Public-Key size: " + lPublicKeyBytes.length);
    System.out.println("Private-Key: " + lEncryptionTools.getKeyAsBase64(lKeyPair.getPrivate()));
    System.out.println("Private-Key size: " + lPrivateKeyBytes.length);

    // Try explicit usage of algorithm
    KeyPair lOtherKeyPair = lEncryptionTools.generateRSAKeyPair(1024, "RSA");
    assertNotNull(lOtherKeyPair);

    // Check error handling in case of invalid algorithm
    try {
      lEncryptionTools.generateRSAKeyPair(1024, "XYZ");
      fail("Expection expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_GENERATE_KEY_PAIR, e.getErrorCode());
    }

    // Convert bytes back to key objects
    publicKey = lEncryptionTools.createRSAPublicKey(lPublicKeyBytes);
    privateKey = lEncryptionTools.createRSAPrivateKey(lPrivateKeyBytes);

    TestCase.assertEquals("Conversion generated wrong public key.", lKeyPair.getPublic(), publicKey);
    TestCase.assertEquals("Conversion generated wrong private key.", lKeyPair.getPrivate(), privateKey);

    // Try to encrypt some text and afterwards decrypt it again.
    byte[] lBytes = new byte[] { 0, 1, 2, 3, 4 };
    byte[] lEncryptedBytes = lEncryptionTools.encryptRSA(lBytes, publicKey);
    byte[] lDecryptedBytes = lEncryptionTools.decryptRSA(lEncryptedBytes, privateKey);
    TestCase.assertTrue("Error after encryption roundtrip.", Arrays.equals(lBytes, lDecryptedBytes));

    String lContent = "Hello World of RSA encryption!";
    Charset lEncoding = StandardCharsets.UTF_8;
    byte[] lContentBytes = lContent.getBytes(lEncoding);
    byte[] lEncryptedContent = lEncryptionTools.encryptRSA(lContentBytes, publicKey);
    byte[] lDecryptedContent = lEncryptionTools.decryptRSA(lEncryptedContent, privateKey);
    TestCase.assertEquals("Error after encryption roundtrip.", lContent, new String(lDecryptedContent, lEncoding));

    // Check length constraints.
    lBytes = new byte[446];
    lEncryptionTools.encryptRSA(lBytes, publicKey);

    lBytes = new byte[447];
    try {
      lEncryptionTools.encryptRSA(lBytes, publicKey);
      TestCase.fail("Invalid content length not detected.");
    }
    catch (JEAFSystemException e) {
      TestCase.assertEquals("Wrong error code.", ToolsMessages.RSA_MAX_MESSAGE_SIZE_EXCEEDED, e.getErrorCode());
    }

    // Try to encrypt with invalid public key
    try {
      PublicKey lInvalidPublicKey = new InvalidPublicKey();
      lEncryptionTools.encryptRSA("Hello".getBytes(lEncoding), lInvalidPublicKey);
      fail("Expection expected in case of invalid key");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_ENCRYPT_STRING, e.getErrorCode());
    }

    // Try to decrypt invalid string.
    try {
      lEncryptionTools.decryptRSA(new byte[] { 1, 2, 3 }, privateKey);
      fail("Expection expected in case of invalid string");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_DECRYPT_STRING, e.getErrorCode());
    }
  }

  @Test
  public void testCreateRSAPrivateKey( ) {
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();

    KeyPair lKeyPair = lEncryptionTools.generateRSAKeyPair(4096);
    byte[] lPrivateKeyBytes = lKeyPair.getPrivate().getEncoded();

    PrivateKey lPrivateKey = lEncryptionTools.createRSAPrivateKey(lPrivateKeyBytes);
    assertEquals("RSA", lPrivateKey.getAlgorithm());
    assertTrue(Arrays.equals(lPrivateKeyBytes, lPrivateKey.getEncoded()));
    assertEquals("PKCS#8", lPrivateKey.getFormat());

    lPrivateKey = lEncryptionTools.createRSAPrivateKey(lPrivateKeyBytes, "RSA");
    assertEquals("RSA", lPrivateKey.getAlgorithm());
    assertTrue(Arrays.equals(lPrivateKeyBytes, lPrivateKey.getEncoded()));
    assertEquals("PKCS#8", lPrivateKey.getFormat());

    // Test exception handling
    try {
      lEncryptionTools.createRSAPrivateKey(lPrivateKeyBytes, "Invalid");
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_GENERATE_KEY_FROM_BYTES, e.getErrorCode());
    }
  }

  @Test
  public void testCreateRSAPublicKey( ) {
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();

    KeyPair lKeyPair = lEncryptionTools.generateRSAKeyPair(4096);
    byte[] lPublicKeyBytes = lKeyPair.getPublic().getEncoded();

    PublicKey lPublicKey = lEncryptionTools.createRSAPublicKey(lPublicKeyBytes);
    assertEquals("RSA", lPublicKey.getAlgorithm());
    assertTrue(Arrays.equals(lPublicKeyBytes, lPublicKey.getEncoded()));
    assertEquals("X.509", lPublicKey.getFormat());

    lPublicKey = lEncryptionTools.createRSAPublicKey(lPublicKeyBytes, "RSA");
    assertEquals("RSA", lPublicKey.getAlgorithm());
    assertTrue(Arrays.equals(lPublicKeyBytes, lPublicKey.getEncoded()));
    assertEquals("X.509", lPublicKey.getFormat());

    // Test exception handling
    try {
      lEncryptionTools.createRSAPublicKey(lPublicKeyBytes, "Invalid");
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_GENERATE_KEY_FROM_BYTES, e.getErrorCode());
    }
  }

  @Test
  @Disabled
  public void testRSAKeyGenerationPerformance( ) {
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();
    for (int i = 0; i < LOOPS; i++) {
      KeyPair lKeyPair = lEncryptionTools.generateRSAKeyPair(4096);
      System.out
          .println("Key " + i + " created: " + Base64.getEncoder().encodeToString((lKeyPair.getPublic().getEncoded())));
    }
  }

  @Test
  public void testRSASignatures( ) {
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();
    KeyPair lKeyPair1 = lEncryptionTools.generateRSAKeyPair(2048);
    String lMessage = "Hello World of Encryption!";
    byte[] lMessageBytes = lMessage.getBytes();
    String lAlgorithm = "SHA256withRSA";
    byte[] lSignature = lEncryptionTools.sign(lMessageBytes, lAlgorithm, lKeyPair1.getPrivate());

    KeyPair lKeyPair2 = lEncryptionTools.generateRSAKeyPair(2048);

    assertTrue(lEncryptionTools.verify(lMessageBytes, lAlgorithm, lSignature, lKeyPair1.getPublic()));
    assertFalse(lEncryptionTools.verify(lMessageBytes, lAlgorithm, lSignature, lKeyPair2.getPublic()));

    // Sign with KeyPair
    lSignature = lEncryptionTools.sign(lMessageBytes, lAlgorithm, lKeyPair2);
    assertTrue(lEncryptionTools.verify(lMessageBytes, lAlgorithm, lSignature, lKeyPair2));
    assertFalse(lEncryptionTools.verify(lMessageBytes, lAlgorithm, lSignature, lKeyPair1));

    // Test error handling
    try {
      lEncryptionTools.sign(lMessageBytes, "RSA", lKeyPair2);
      fail("Exception expected when using not supported algorithm");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_SIGN_MESSAGE, e.getErrorCode());
    }

    try {
      lEncryptionTools.verify(lMessageBytes, "RSA", lSignature, lKeyPair2);
      fail("Exception expected when using not supported algorithm");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_VERIFY_SIGNATURE, e.getErrorCode());
    }
  }

  @Test
  public void testSecureTokenAccess( ) {
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();
    String lSecureToken = lEncryptionTools.getSecureToken(8);
    assertEquals(8, lSecureToken.length());

    lSecureToken = lEncryptionTools.getSecureToken(32, true);
    assertEquals(32, lSecureToken.length());
  }

  @Test
  public void testSecureTokenGenerator( ) {
    SecureTokenGenerator lTokenGenerator = new SecureTokenGenerator(4, "SHA1PRNG");

    // Try to ensure that secure tokens have a good entropies.
    String lShortToken = lTokenGenerator.getToken();
    for (int i = 0; i < 100; i++) {
      assertNotEquals(lShortToken, lTokenGenerator.getToken());
    }

    // Try to ensure that some characters are not present in human readable tokens.
    lTokenGenerator = new SecureTokenGenerator(64);
    for (int i = 0; i < 100; i++) {
      String lToken = lTokenGenerator.getToken(true);
      assertFalse(lToken.contains("0"));
      assertFalse(lToken.contains("o"));
      assertFalse(lToken.contains("O"));
    }

    // Test error handling
    try {
      lTokenGenerator = new SecureTokenGenerator(5, "XYZ");
      fail("Invalid algorithm should cause exception.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNKNOWN_ALGORITHM, e.getErrorCode());
    }
    try {
      lTokenGenerator = new SecureTokenGenerator(-5);
      fail("Negative key length should cause exception.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_TOKEN_LENGTH, e.getErrorCode());
    }
  }

  @Test
  public void testRSAPublicKeyRecoverPerformance( ) {
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();
    KeyPair lKeyPair = lEncryptionTools.generateRSAKeyPair(4096);
    byte[] lPublicKeyBytes = lKeyPair.getPublic().getEncoded();

    for (int i = 0; i < LOOPS; i++) {
      lEncryptionTools.createRSAPublicKey(lPublicKeyBytes);
    }
  }

  @Test
  public void testRSAPrivateKeyRecoverPerformance( ) {
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();
    KeyPair lKeyPair = lEncryptionTools.generateRSAKeyPair(4096);
    byte[] lPrivateKeyBytes = lKeyPair.getPrivate().getEncoded();

    for (int i = 0; i < LOOPS; i++) {
      lEncryptionTools.createRSAPrivateKey(lPrivateKeyBytes);
    }
  }

  @Test
  public void testRSAEncryptionPerformance( ) {
    byte[] lBytes = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
      24, 25, 26, 27, 28, 29, 30, 31 };
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();
    for (int i = 0; i < LOOPS; i++) {
      lEncryptionTools.encryptRSA(lBytes, publicKey);
    }
  }

  @Test
  public void testRSADecryptionPerformance( ) {
    byte[] lBytes = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
      24, 25, 26, 27, 28, 29, 30, 31 };
    EncryptionTools lEncryptionTools = EncryptionTools.getEncryptionTools();
    byte[] lEncryptedBytes = lEncryptionTools.encryptRSA(lBytes, publicKey);
    for (int i = 0; i < LOOPS; i++) {
      lEncryptionTools.decryptRSA(lEncryptedBytes, privateKey);
    }
  }

  @Test
  public void testEncryptionToolsConfiguration( ) {
    // Test default configuration
    EncryptionToolsConfiguration lConfiguration = new EncryptionToolsConfiguration();
    assertEquals(AESKeyLength.AES_256, lConfiguration.getAESDefaultKeyLength());
    assertEquals("SHA1PRNG", lConfiguration.getSecureTokenRandomAlgorithm());
    assertEquals(EncryptionToolsConfig.RSA_DEFAULT_ALGORITHM, lConfiguration.getRSAAlgorithm());
    assertEquals(EncryptionToolsConfig.RSA_DEFAULT_TRANSFORM, lConfiguration.getRSATransformation());

    // Test empty configuration.
    EncryptionToolsConfig lEmptyConfiguration = lConfiguration.getEmptyConfiguration();
    assertEquals(EncryptionToolsConfig.class, lEmptyConfiguration.annotationType());
    assertNull(lEmptyConfiguration.aesDefaultKeyLength());
    assertNull(lEmptyConfiguration.secureTokenRandomAlgorithm());
    assertNull(lEmptyConfiguration.rsaAlgorithm());
    assertNull(lEmptyConfiguration.rsaTransform());

    assertEquals(0, lConfiguration.checkCustomConfiguration(lEmptyConfiguration).size());
    assertEquals(0, lConfiguration.checkCustomConfiguration(null).size());

    // Test key length resolution by encryption tools
    assertEquals(AESKeyLength.AES_256, EncryptionTools.getEncryptionTools().getAESDefaultKeyLength());

    // Test custom configuration.
    lConfiguration = new EncryptionToolsConfiguration("MyEncryptionTools", "META-INF", true);
    assertEquals(AESKeyLength.AES_128, lConfiguration.getAESDefaultKeyLength());
    assertEquals("SHA1PRNG_TEST", lConfiguration.getSecureTokenRandomAlgorithm());
    assertEquals("MyRSA", lConfiguration.getRSAAlgorithm());
    assertEquals("MyTransform1024", lConfiguration.getRSATransformation());
  }
}

class InvalidPublicKey implements PublicKey, RSAKey {
  private static final long serialVersionUID = 1L;

  @Override
  public String getFormat( ) {
    return "XXX";
  }

  @Override
  public byte[] getEncoded( ) {
    return new byte[] { 1, 2, 3 };
  }

  @Override
  public String getAlgorithm( ) {
    return "XYZ";
  }

  @Override
  public BigInteger getModulus( ) {
    return new BigInteger(
        "816245376215299330217030823112113669486335042338994104764591046005820592983539413954215030222471704838104876932724616263848567234813641799583812025625525487249038779207597102385735873197178215064456122380608229794120292540836795985102240252576857181164646893534670639365441166569147001482547047483315512782433950523913207481527755787923232240735847907970006245481242301834225779808058877546743312839783388680951807373716878685974835071853521708000756321728236458982991449870340739625929364300133997443181350879754261307494763253342846077533506787403271743252044213012950327220612569752359226967052054302648028049720700055094800530286282439081417274459131266612069404181735557393974365209348663243303607096752527922787325503338833695166418037628781258272787395556095728404050135293932716763978719458805232092006128826387548951426953479948906618567109148639794490836659318906873809347258401980200633327887124799228950408551792992478916963383570857920898652221942897493361793737465953541597003223365382724722054812319900867007708015122659453526823721255643694775873972082448593196148339610393636077897927538982683315039227225509238005956095989808412981745395010871089032215632626027341613282697479667218083277787723353486240691642589837");
  }
}

class InvalidSecretKey implements SecretKey {

  private static final long serialVersionUID = 1L;

  @Override
  public String getAlgorithm( ) {
    return "unknown";
  }

  @Override
  public String getFormat( ) {
    return "XXX";
  }

  @Override
  public byte[] getEncoded( ) {
    return new byte[] { 1, 2, 3 };
  }

}
