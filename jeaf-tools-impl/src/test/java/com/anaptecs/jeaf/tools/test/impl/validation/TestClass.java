/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.validation;

import javax.validation.constraints.NotNull;

public class TestClass {
  @NotNull
  private String string;

  public String getString( ) {
    return string;
  }

  public void setString( String pString ) {
    string = pString;
  }

}
