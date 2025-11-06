/**
 * Copyright 2004 - 2018 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.validation;

import java.util.Locale;

import javax.validation.MessageInterpolator;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.messages.LocalizedObject;
import com.anaptecs.jeaf.xfun.api.messages.MessageRepository;
import com.anaptecs.jeaf.xfun.bootstrap.Check;

public class JEAFMessageInterpolator implements MessageInterpolator {
  private final MessageInterpolator defaultMessageInterpolator;

  public JEAFMessageInterpolator( MessageInterpolator pDefaultMessageInterpolator ) {
    // Check parameter
    Check.checkInvalidParameterNull(pDefaultMessageInterpolator, "pDefaultMessageInterpolator");

    defaultMessageInterpolator = pDefaultMessageInterpolator;
  }

  @Override
  public String interpolate( String pMessageTemplate, Context pContext ) {
    return this.interpolate(pMessageTemplate, pContext, Tools.getLocaleTools().getCurrentLocale());
  }

  @Override
  public String interpolate( String pMessageTemplate, Context pContext, Locale pLocale ) {
    // Try to create message id from the passed message template
    String lMessage;

    // In case that a JEAF message should be used the message template must contain the message code.
    if (Tools.getStringTools().containsDigitsOnly(pMessageTemplate) == true) {
      // Transform message template into localized message
      int lCode = Integer.parseInt(pMessageTemplate);

      // Check if message with detected code is available in message repository.
      MessageRepository lMessageRepository = XFun.getMessageRepository();
      if (lMessageRepository.existsMessage(lCode) == true) {
        LocalizedObject lLocalizedObject = lMessageRepository.getLocalizedObject(lCode);
        lMessage = lMessageRepository.getMessage(lLocalizedObject, pLocale);
      }
      // Use standard error message
      else {
        lMessage = defaultMessageInterpolator.interpolate(pMessageTemplate, pContext, pLocale);
      }
    }
    // Message template does not contain a message code so we use standard mechanism to generate message.
    else {
      lMessage = defaultMessageInterpolator.interpolate(pMessageTemplate, pContext, pLocale);
    }

    // Return validation message
    return lMessage;
  }
}
