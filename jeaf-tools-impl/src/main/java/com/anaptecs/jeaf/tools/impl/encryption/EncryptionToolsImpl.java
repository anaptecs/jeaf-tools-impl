/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2015 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.encryption;

import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.anaptecs.jeaf.tools.annotations.EncryptionToolsConfig;
import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.encryption.AESEncrypted;
import com.anaptecs.jeaf.tools.api.encryption.AESKeyLength;
import com.anaptecs.jeaf.tools.api.encryption.AESSecretKey;
import com.anaptecs.jeaf.tools.api.encryption.EncryptionTools;
import com.anaptecs.jeaf.xfun.api.checks.Assert;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

/**
 * The Class EncryptionToolsImpl.
 */
@EncryptionToolsConfig
@ToolsImplementation(toolsInterface = EncryptionTools.class)
public class EncryptionToolsImpl implements EncryptionTools {

  private static final int RSA_HASH_SIZE = 32;

  /** Initialization vector. */
  private static final IvParameterSpec ENCRYPTION_IV = new IvParameterSpec("aao48dh30smsandk".getBytes());

  /** Instance of the cipher for encryption. */
  private Cipher cipher;

  /** Instance of the cipher for decryption. */
  private Cipher decipher;

  /** The Secret Key. */
  private SecretKeySpec secret;

  private Map<Integer, SecureTokenGenerator> secureTokenGenerators = new HashMap<>();

  /**
   * AES transformation that should be used when working with AES encryption.
   */
  private final String aesTransformation;

  /**
   * Default key length for AES encryption.
   */
  private final AESKeyLength aesDefaultKeyLength;

  /**
   * Algorithm that will be used for RSA keys.
   */
  private final String rsaAlgorithm;

  /**
   * Algorithm that will be used for RSA transformations.
   */
  private final String rsaTransform;

  /**
   * Initialize object.
   */
  public EncryptionToolsImpl( ) {
    // Load and check configuration.
    EncryptionToolsConfiguration lConfiguration = new EncryptionToolsConfiguration();
    aesTransformation = lConfiguration.getAESTransformation();
    aesDefaultKeyLength = this.resolveAESKeyLength(lConfiguration.getAESDefaultKeyLength());
    rsaAlgorithm = lConfiguration.getRSAAlgorithm();
    rsaTransform = lConfiguration.getRSATransformation();
  }

  /**
   * Method checks if the configured AES key length is really supported on the current runtime environment.
   * 
   * @param pConfiguredKeyLength
   * @return {@link AESKeyLength} AES key length that matches to the configured one and is really supported. The method
   * never returns null.
   */
  private AESKeyLength resolveAESKeyLength( AESKeyLength pConfiguredKeyLength ) {
    // Check if request key length is supported.
    int lSupportedKeyLength;
    try {
      // Try to resolve max key length.
      lSupportedKeyLength = Cipher.getMaxAllowedKeyLength(aesTransformation);
      // Everything is fine.
      AESKeyLength lKeyLenght;
      if (pConfiguredKeyLength.getKeyLength() <= lSupportedKeyLength) {
        lKeyLenght = pConfiguredKeyLength;
      }
      // Let's get the best matching one.
      else {
        lKeyLenght = AESKeyLength.fromKeyLength(lSupportedKeyLength);
      }
      return lKeyLenght;
    }
    catch (NoSuchAlgorithmException e) {
      throw new JEAFSystemException(ToolsMessages.UNSUPPORTED_ALGORITHM, e, aesTransformation);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.anaptecs.jeaf.fwk.core.EncryptionTools#encryptString(java.lang.String, int, int)
   */
  @Override
  public String encryptAES( String pString, AESKeyLength pKeyLength, int pHashIterations ) {
    String lEncryptedString;
    if (pString != null) {
      Arrays.fill(pString.toCharArray(), Character.MIN_VALUE);
      byte[] lBytes = pString.getBytes(StandardCharsets.UTF_8);

      try {
        cipher = this.getCipher(pKeyLength, pHashIterations);

        byte[] lEncryptedTextBytes = cipher.doFinal(lBytes);
        lEncryptedString = Base64.getEncoder().encodeToString(lEncryptedTextBytes);
      }
      catch (GeneralSecurityException e) {
        throw new JEAFSystemException(ToolsMessages.UNABLE_TO_ENCRYPT_STRING, e);
      }
    }
    else {
      lEncryptedString = null;
    }
    return lEncryptedString;
  }

  /**
   * Method encrypts the passed plain text using AES.
   * 
   * @param pPlainText Text that should be encrypted. The parameter may be null.
   * @param pKey Parameter contains the AES secret key that should be used. The parameter must not be null.
   * @return {@link AESEncrypted} Result of the encryption. As AES generates the encrypted text as output as well as
   * some initialization vector (IV) we have to return an object. Both information have to be kept and are needed for
   * decryption.
   */
  @Override
  public AESEncrypted encrypt( String pPlainText, AESSecretKey pKey ) {
    // Check parameters
    Check.checkInvalidParameterNull(pKey, "pKey");

    AESEncrypted lAESCipher;
    if (pPlainText != null) {
      try {
        // Derive secret key object from parameters.
        SecretKey lSecretKey = pKey.getSecretKey();

        // Encrypt the message.
        Cipher lEncrypter = Cipher.getInstance(aesTransformation);
        lEncrypter.init(Cipher.ENCRYPT_MODE, lSecretKey);
        AlgorithmParameters params = lEncrypter.getParameters();
        byte[] lIV = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] lCipherText = lEncrypter.doFinal(pPlainText.getBytes(StandardCharsets.UTF_8));

        // Return result of encryption.
        lAESCipher = new AESEncrypted(lIV, lCipherText);
      }
      // Handle exception during encryption.
      catch (GeneralSecurityException e) {
        throw new JEAFSystemException(ToolsMessages.UNABLE_TO_ENCRYPT_STRING, e);
      }
    }
    else {
      lAESCipher = new AESEncrypted((byte[]) null, (byte[]) null);
    }
    return lAESCipher;
  }

  /**
   * Method decrypts the passed AES cipher.
   * 
   * @param pAESCipher Object describing the encrypted text. The parameter must not be null.
   * @param pKey Parameter contains the AES secret key that should be used. The parameter must not be null.
   * @return {@link String} Decrypted text. The method may return null if null was encrypted.
   */
  @Override
  public String decrypt( AESEncrypted pAESCipher, AESSecretKey pKey ) {
    // Check parameters
    Check.checkInvalidParameterNull(pAESCipher, "pAESCipher");
    Check.checkInvalidParameterNull(pKey, "pKey");

    byte[] lCipherText = pAESCipher.getCipherText();
    String lPlainText;
    if (lCipherText != null) {
      try {
        // Derive secret key object from parameters.
        SecretKey lSecretKey = pKey.getSecretKey();

        Cipher lDecrypter = Cipher.getInstance(aesTransformation);
        lDecrypter.init(Cipher.DECRYPT_MODE, lSecretKey, new IvParameterSpec(pAESCipher.getIV()));
        lPlainText = new String(lDecrypter.doFinal(lCipherText), StandardCharsets.UTF_8);
      }
      // Handle exception during encryption.
      catch (GeneralSecurityException e) {
        throw new JEAFSystemException(ToolsMessages.UNABLE_TO_DECRYPT_STRING, e);
      }
    }
    else {
      lPlainText = null;
    }
    return lPlainText;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.anaptecs.jeaf.fwk.core.EncryptionTools#decryptString(java.lang.String, int, int)
   */
  @Override
  public String decryptAES( String pString, AESKeyLength pKeyLength, int pHashIterations ) {
    // Passed value is real value
    String lDecryptedString;
    if (pString != null) {
      try {
        byte[] encryptedTextBytes = Base64.getDecoder().decode(pString);

        // Decrypt the message
        decipher = this.getDecipher(pKeyLength, pHashIterations);

        byte[] decryptedTextBytes = decipher.doFinal(encryptedTextBytes);
        lDecryptedString = new String(decryptedTextBytes, StandardCharsets.UTF_8);
      }
      catch (GeneralSecurityException e) {
        throw new JEAFSystemException(ToolsMessages.UNABLE_TO_DECRYPT_STRING, e);
      }
    }
    // Null stays null.
    else {
      lDecryptedString = null;
    }
    return lDecryptedString;
  }

  /**
   * Method gets the cipher object for encryption. if the cipher object does not exist it will be created.
   * 
   * @param pKeyLength the master key length
   * @param pHashIterations the hash iterations
   * @return the cipher for encryption.
   * @throws GeneralSecurityException the general security exception
   */
  private Cipher getCipher( AESKeyLength pKeyLength, int pHashIterations ) throws GeneralSecurityException {
    if (cipher == null) {
      secret = this.getSecret(pKeyLength, pHashIterations);

      // encrypt the message
      cipher = Cipher.getInstance(aesTransformation);

      cipher.init(Cipher.ENCRYPT_MODE, secret, ENCRYPTION_IV);
    }
    return cipher;
  }

  /**
   * Gets the secret key. If the key does not exist it will be created.
   * 
   * @param pKeyLength the master key length
   * @param pHashIterations the hash iterations
   * @return the secret key.
   * @throws GeneralSecurityException the general security exception
   */
  private SecretKeySpec getSecret( AESKeyLength pKeyLength, int pHashIterations ) throws GeneralSecurityException {
    if (secret == null) {
      int lKeyLength = pKeyLength.getKeyLength();
      PBEKeySpec lKeySpec = new PBEKeySpec(null, "ctA9sWW0nxEa2kXi".getBytes(), pHashIterations, lKeyLength);

      SecretKeyFactory lSecretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_WITH_HMAC_SHA1);
      SecretKey lSecretKey = lSecretKeyFactory.generateSecret(lKeySpec);
      secret = new SecretKeySpec(lSecretKey.getEncoded(), AES_ALGORITHM);

      lKeySpec.clearPassword();
    }
    return secret;
  }

  /**
   * Method gets the cipher object for decryption. If the cipher object does not exist it will be created.
   * 
   * @param pKeyLength the master key length
   * @param pHashIterations the hash iterations
   * @return the cipher for decryption.
   * @throws GeneralSecurityException the general security exception
   */
  private Cipher getDecipher( AESKeyLength pKeyLength, int pHashIterations ) throws GeneralSecurityException {
    if (decipher == null) {
      secret = this.getSecret(pKeyLength, pHashIterations);

      // encrypt the message
      decipher = Cipher.getInstance(aesTransformation);

      decipher.init(Cipher.DECRYPT_MODE, secret, ENCRYPTION_IV);
    }
    return decipher;
  }

  /**
   * Methods signs the passed message with the passed private key.
   * 
   * @param pMessage Message that should be signed. The parameter must not be null.
   * @param pAlgorithm Algorithm that should be used to sign the message. The parameter must not be null.
   * @param pKeyPair Key pair (or actually the private key of it) that should be used to sign the message. The parameter
   * must not be null.
   * 
   * @return byte[] Signature for the passed message. The method never returns null.
   */
  @Override
  public byte[] sign( byte[] pMessage, String pAlgorithm, KeyPair pKeyPair ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pKeyPair, "pKeyPair");

    return this.sign(pMessage, pAlgorithm, pKeyPair.getPrivate());
  }

  /**
   * Method verifies the passed message against the passed signature using the passed certificate that was used to sign
   * the message.
   * 
   * @param pMessage Message that should be verified. The parameter must not be null.
   * @param pAlgorithm Algorithm that should be used to verify the signature.
   * @param pSignature Signature that was created by the sender of the message. The parameter must not be null.
   * @param pKeyPair Key pair (or actually the public key of it) that should have been used to sign the message. The
   * parameter must not be null.
   * @return boolean Method returns true if the passed messages matches with the passed signature and the certificate
   * and otherwise false.
   */
  @Override
  public boolean verify( byte[] pMessage, String pAlgorithm, byte[] pSignature, KeyPair pKeyPair ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pKeyPair, "pKeyPair");

    return this.verify(pMessage, pAlgorithm, pSignature, pKeyPair.getPublic());
  }

  /**
   * Methods signs the passed message with the passed private key.
   * 
   * @param pMessage Message that should be signed. The parameter must not be null.
   * @param pAlgorithm Algorithm that should be used to sign the message. The parameter must not be null.
   * @param pPrivateKey Private key that should be used to sign the message. The parameter must not be null.
   * 
   * @return byte[] Signature for the passed message. The method never returns null.
   */
  @Override
  public byte[] sign( byte[] pMessage, String pAlgorithm, PrivateKey pPrivateKey ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pMessage, "pMessage");
    Check.checkInvalidParameterNull(pAlgorithm, "pAlgorithm");
    Check.checkInvalidParameterNull(pPrivateKey, "pPrivateKey");

    try {
      Signature lSignature = Signature.getInstance(pAlgorithm);
      lSignature.initSign(pPrivateKey);
      lSignature.update(pMessage);
      return lSignature.sign();
    }
    catch (GeneralSecurityException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_SIGN_MESSAGE, e);
    }
  }

  /**
   * Method verifies the passed message against the passed signature using the passed public key that was used to sign
   * the message.
   * 
   * @param pMessage Message that should be verified. The parameter must not be null.
   * @param pAlgorithm Algorithm that should be used to verify the signature. The parameter must not be null.
   * @param pSignature Signature that was created by the sender of the message. The parameter must not be null.
   * @param pPublicKey Public key that should have been used to sign the message. The parameter must not be null.
   * @return boolean Method returns true if the passed messages matches with the passed signature and the public key and
   * otherwise false.
   */
  @Override
  public boolean verify( byte[] pMessage, String pAlgorithm, byte[] pSignature, PublicKey pPublicKey ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pMessage, "pMessage");
    Check.checkInvalidParameterNull(pAlgorithm, "pAlgorithm");
    Check.checkInvalidParameterNull(pSignature, "pSignature");
    Check.checkInvalidParameterNull(pPublicKey, "pPublicKey");

    try {
      Signature lSignature = Signature.getInstance(pAlgorithm);
      lSignature.initVerify(pPublicKey);
      lSignature.update(pMessage);
      return lSignature.verify(pSignature);
    }
    catch (GeneralSecurityException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_VERIFY_SIGNATURE, e);
    }
  }

  /**
   * Method generates a new key pair (public and private key) for the passed asymmetric encryption algorithm.
   * 
   * @param pAlgorithm Algorithm that should be used for the keys. The parameter must not be null.
   * @param pKeyLength Length of the generated keys. It's strongly recommended that the key length is at least 2048.
   * @return {@link KeyPair} Generated public and private key. The method never returns null.
   */
  @Override
  public KeyPair generateRSAKeyPair( int pKeyLength ) {
    // Get the public/private key pair
    return this.generateRSAKeyPair(pKeyLength, rsaAlgorithm);
  }

  /**
   * Method generates a new key pair (public and private key) for the passed asymmetric encryption algorithm.
   * 
   * @param pAlgorithm Algorithm that should be used for the keys. The parameter must not be null.
   * @param pKeyLength Length of the generated keys. It's strongly recommended that the key length is at least 2048.
   * @return {@link KeyPair} Generated public and private key. The method never returns null.
   */
  @Override
  public KeyPair generateRSAKeyPair( int pKeyLength, String pAlgorithm ) {
    // Check parameter
    Check.checkInvalidParameterNull(pAlgorithm, "pAlgorithm");

    // Get the public/private key pair
    try {
      KeyPairGenerator lKeyGenerator = KeyPairGenerator.getInstance(pAlgorithm);
      lKeyGenerator.initialize(pKeyLength);
      return lKeyGenerator.genKeyPair();
    }
    catch (NoSuchAlgorithmException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_GENERATE_KEY_PAIR, e);
    }
  }

  /**
   * Method generates a new RSA public key object from the passed bytes.
   * 
   * @param pPublicKeyBytes Bytes that should be used to generate the public key. The parameter must not be null.
   * @return {@link PublicKey} RSA public key that was generated using the passed bytes. The method never returns null.
   */
  @Override
  public PublicKey createRSAPublicKey( byte[] pPublicKeyBytes ) {
    return this.createRSAPublicKey(pPublicKeyBytes, rsaAlgorithm);
  }

  /**
   * Method generates a new RSA public key object from the passed bytes.
   * 
   * @param pPublicKeyBytes Bytes that should be used to generate the public key. The parameter must not be null.
   * @param pAlgorithm Algorithm that should be used for the keys. The parameter must not be null.
   * @return {@link PublicKey} RSA public key that was generated using the passed bytes. The method never returns null.
   */
  @Override
  public PublicKey createRSAPublicKey( byte[] pPublicKeyBytes, String pAlgorithm ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pPublicKeyBytes, "pPublicKeyBytes");

    // Create public key from byte array and return it.
    try {
      X509EncodedKeySpec lX509PublicKey = new X509EncodedKeySpec(pPublicKeyBytes);
      KeyFactory lKeyFactory = KeyFactory.getInstance(pAlgorithm);
      return lKeyFactory.generatePublic(lX509PublicKey);
    }
    catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_GENERATE_KEY_FROM_BYTES, e);
    }
  }

  /**
   * Method generates a new RSA private key object from the passed bytes.
   * 
   * @param pPrivateKeyBytes Bytes that should be used to generate the private key. The parameter must not be null.
   * @return {@link PrivateKey} RSA private key that was generated using the passed bytes. The method never returns
   * null.
   */
  @Override
  public PrivateKey createRSAPrivateKey( byte[] pPrivateKeyBytes ) {
    return this.createRSAPrivateKey(pPrivateKeyBytes, rsaAlgorithm);
  }

  /**
   * Method generates a new RSA private key object from the passed bytes.
   * 
   * @param pPrivateKeyBytes Bytes that should be used to generate the private key. The parameter must not be null.
   * @param pAlgorithm Algorithm that should be used for the keys. The parameter must not be null.
   * @return {@link PrivateKey} RSA private key that was generated using the passed bytes. The method never returns
   * null.
   */
  @Override
  public PrivateKey createRSAPrivateKey( byte[] pPrivateKeyBytes, String pAlgorithm ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pPrivateKeyBytes, "pPrivateKeyBytes");

    // Create public key from byte array and return it.
    try {
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pPrivateKeyBytes);
      KeyFactory lKeyFactory = KeyFactory.getInstance(pAlgorithm);
      return lKeyFactory.generatePrivate(keySpec);
    }
    catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_GENERATE_KEY_FROM_BYTES, e);
    }
  }

  /**
   * Method encrypts the passed content using RSA algorithm and the passed public key.
   * 
   * @param pContent Content that should be encrypted. The parameter must not be null.
   * @param pPublicKey Public key that should be used for the RSA encryption. The parameter must not be null.
   * @return byte[] Encrypted content as bytes. The method never returns null.
   */
  @Override
  public byte[] encryptRSA( byte[] pContent, PublicKey pPublicKey ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pContent, "pContent");
    Check.checkInvalidParameterNull(pPublicKey, "pPublicKey");

    // Check combination of key length and content. RSA does not support to encrypt content that is longer than the key.
    int lMaxMessageSize = this.calcluateMaxMessageSize((RSAKey) pPublicKey);
    if (pContent.length <= lMaxMessageSize) {
      try {
        Cipher lCipher = Cipher.getInstance(rsaTransform);
        lCipher.init(Cipher.ENCRYPT_MODE, pPublicKey);
        return lCipher.doFinal(pContent);
      }
      catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
          | BadPaddingException e) {
        throw new JEAFSystemException(ToolsMessages.UNABLE_TO_ENCRYPT_STRING, e);
      }
    }
    // Max. message size exceeded.
    else {
      String[] lParams = new String[] { String.valueOf(pContent.length), String.valueOf(lMaxMessageSize) };
      throw new JEAFSystemException(ToolsMessages.RSA_MAX_MESSAGE_SIZE_EXCEEDED, lParams);
    }
  }

  /**
   * Method calculates the maximum number of bytes that can be encrypted using RSA algorithm and the passed key.
   * 
   * @param lKey Key that should be used for encryption. The parameter must not be null.
   * @return int Maximum number of bytes that can be encrypted with RSA and the passed key.
   */
  private int calcluateMaxMessageSize( RSAKey lKey ) {
    // Check parameter
    Assert.assertNotNull(lKey, "lKey");

    // Max length can be calculated using the following formula:
    // max message length = key length / 8 - ( 2 * hash output size) - 2
    // As we use SHA-256 for hashing its 256 / 8 = 32 bytes
    int lKeyLength = lKey.getModulus().bitLength();
    return lKeyLength / 8 - (2 * RSA_HASH_SIZE) - 2;
  }

  /**
   * Method decrypts the passed content using RSA algorithm and the passed private key.
   * 
   * @param pContent Content that should be decrypted. The parameter must not be null.
   * @param pPrivateKey Private key that should be used for the RSA encryption. The parameter must not be null.
   * @return byte[] Decrypted content as bytes. The method never returns null.
   */
  @Override
  public byte[] decryptRSA( byte[] pEncryptedContent, PrivateKey pPrivateKey ) {
    try {
      Cipher lCipher = Cipher.getInstance(rsaTransform);
      lCipher.init(Cipher.DECRYPT_MODE, pPrivateKey);
      return lCipher.doFinal(pEncryptedContent);
    }
    catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_DECRYPT_STRING, e);
    }
  }

  /**
   * Method converts the passed key into a base 64 string representation.
   * 
   * @param pKey Key that should be converted. The parameter must not be null.
   * @return {@link String} Base 64 encoded key. The method never returns null.
   */
  @Override
  public String getKeyAsBase64( Key pKey ) {
    return Base64.getEncoder().encodeToString(pKey.getEncoded());
  }

  /**
   * Method generates a secure token with the passed length.
   * 
   * @param pTokenLength Length of the tokens that should be generated.
   * @return String Generated one-time token. The method never returns null.
   */
  @Override
  public String getSecureToken( int pTokenLength ) {
    return this.getSecureToken(pTokenLength, false);
  }

  /**
   * Method generates a secure token of the passed length.
   * 
   * @param pTokenLength Length of the tokens that should be generated.
   * @param pHumanReadable Parameter defines if the returned token should be optimized for human readability. If you
   * enable human readability please ensure the you increase the length of the created token.
   * @return String Generated token. The method never returns null.
   */
  @Override
  public String getSecureToken( int pTokenLength, boolean pHumanReadable ) {
    return this.getSecureTokenGenerator(pTokenLength).getToken(pHumanReadable);
  }

  /**
   * Method returns a secure token generator that is configured to generate tokens of the passed length.
   * 
   * @param pTokenLength Length of the tokens that should be generated.
   * @return {@link SecureTokenGenerator} Token generator of the passed length. The method never returns null.
   */
  private SecureTokenGenerator getSecureTokenGenerator( int pTokenLength ) {
    return secureTokenGenerators.computeIfAbsent(pTokenLength, f -> new SecureTokenGenerator(pTokenLength));
  }

  /**
   * Method returns the defined AES default key length. The result of this operation depends on the encryption
   * capabilities of the used JDK (@see https://www.oracle.com/java/technologies/javase-jce8-downloads.html) as well as
   * on the configured maximum key length using @link {@link EncryptionToolsConfig#aesDefaultKeyLength()}
   * 
   * @return {@link AESKeyLength} AESDefault key length. The method never returns null.
   */
  @Override
  public AESKeyLength getAESDefaultKeyLength( ) {
    return aesDefaultKeyLength;
  }
}
