/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

/**
 * Class provides the generation of secure alphanumeric tokens. Therefore it uses a secure number generator.
 * 
 * The class supports multi-threading.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.2
 */
public final class SecureTokenGenerator {
  private static final int CONST_52 = 52;

  private static final int CONST_26 = 26;

  private static final int CONST_62 = 62;

  /**
   * Algorithm that is used to create token, if nothing else is defined.
   */
  private static final String DEFAULT_ALGORITHM = "SHA1PRNG";

  /**
   * Class SecureRandom will be used a generator for one-time tokens.
   */
  private SecureRandom secureNumberGenerator;

  /**
   * Length of the tokens that should be generated.
   */
  private int tokenLength;

  /**
   * Create new secure token generator. The object will use the defined default algorithm to generate tokens.
   * 
   * @param pTokenLength Length of the tokens that should be generated. All generated token will have this length.
   */
  public SecureTokenGenerator( int pTokenLength ) {
    this(pTokenLength, DEFAULT_ALGORITHM);
  }

  /**
   * Create new secure token generator. The object will use the passed algorithm to generate tokens.
   * 
   * @param pTokenLength Length of the tokens that should be generated. All generated token will have this length.
   * @param pAlgorithm Algorithm that should be used to generate tokens. The name of the passed algorithm must be
   * supported by class {@link SecureRandom}. The parameter must not be null.
   */
  public SecureTokenGenerator( int pTokenLength, String pAlgorithm ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pAlgorithm, "pAlgorithm");

    if (pTokenLength > 0) {

      // Set token length
      tokenLength = pTokenLength;

      // Create secure random number generator
      try {
        secureNumberGenerator = SecureRandom.getInstance(pAlgorithm);
      }
      catch (NoSuchAlgorithmException e) {
        throw new JEAFSystemException(ToolsMessages.UNKNOWN_ALGORITHM, e, pAlgorithm);
      }
    }
    // Invalid token length
    else {
      throw new JEAFSystemException(ToolsMessages.INVALID_TOKEN_LENGTH);
    }
  }

  /**
   * Method generates a secure one-time token using the defined algorithm and length.
   * 
   * @return String Generated one-time token. The method never returns null.
   */
  public String getToken( ) {
    return this.getToken(false);
  }

  /**
   * Method generates a secure one-time token using the defined algorithm and length. In order to avoid problems in
   * reading by humans characters and digits that get easily mixed up might not be not used.
   * 
   * @param pHumanFriendly Parameter defines is only human readable friendly characters should be used or not.
   * @return String Generated one-time token. The method never returns null.
   */
  public String getToken( boolean pHumanFriendly ) {
    // Create new byte[] to store token
    char[] lChars = new char[tokenLength];

    // Generate secure token, convert it to a string and return it.
    for (int i = 0; i < lChars.length; i++) {
      int lNextInt = secureNumberGenerator.nextInt(CONST_62);
      if (lNextInt < CONST_26) {
        lChars[i] = (char) (((char) lNextInt) + 'A');
      }
      else if (lNextInt < CONST_52) {
        lChars[i] = (char) (((char) lNextInt) + 'a' - CONST_26);
      }
      else {
        lChars[i] = (char) (((char) lNextInt) + '0' - CONST_52);
      }

      // In order to avoid problems for users in separating '0' from 'O' and 'o' those characters will be replaced ;-)
      if (pHumanFriendly == true && (lChars[i] == '0' || lChars[i] == 'o' || lChars[i] == 'O')) {
        lChars[i] = 'z';
      }
    }

    // Return created token.
    return new String(lChars);
  }

}
