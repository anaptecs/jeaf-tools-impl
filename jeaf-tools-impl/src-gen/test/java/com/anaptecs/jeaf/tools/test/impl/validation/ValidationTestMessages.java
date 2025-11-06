package com.anaptecs.jeaf.tools.test.impl.validation;

import com.anaptecs.jeaf.xfun.annotations.MessageResource;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.errorhandling.ErrorCode;
import com.anaptecs.jeaf.xfun.api.messages.MessageRepository;

/**
 * Class contains all generated constants for messages used by the mail service provider.
 *
 * @author JEAF Development Team
 * @version JEAF Release 1.3
 */
@MessageResource(path = "ValidationTestMessages.xml")
public final class ValidationTestMessages {
  /**
   * Constant for XML file that contains all messages that are defined within this class.
   */
  private static final String MESSAGE_RESOURCE = "ValidationTestMessages.xml";

  /**
   * Exceptions with this error code are thrown if constraint checks for the parameters of a request fail.
   */
  public static final ErrorCode TEST_VALIDATION_MESSAGE;
  /**
   * Static initializer contains initialization for all generated constants.
   */
  static {
    MessageRepository lRepository = XFun.getMessageRepository();
    lRepository.loadResource(MESSAGE_RESOURCE);
    // Handle all info messages.
    // Handle all messages for errors.
    TEST_VALIDATION_MESSAGE = lRepository.getErrorCode(199920);
    // Handle all localized strings.
  }

  /**
   * Constructor is private to ensure that no instances of this class will be created.
   */
  private ValidationTestMessages( ) {
    // Nothing to do.
  }
}