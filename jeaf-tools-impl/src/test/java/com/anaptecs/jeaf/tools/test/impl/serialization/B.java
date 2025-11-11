/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class B implements Serializable {
  private static final long serialVersionUID = 1L;

  List<A> as = new ArrayList<>();

  String name;
}
