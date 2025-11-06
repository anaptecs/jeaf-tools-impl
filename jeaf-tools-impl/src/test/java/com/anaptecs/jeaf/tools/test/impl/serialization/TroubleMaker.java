/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TroubleMaker implements Serializable {

  private static final long serialVersionUID = 1L;

  boolean readTroubleIO = false;

  boolean readTroubleCNF = false;

  boolean writeTroubleIO = false;

  private void writeObject( ObjectOutputStream out ) throws IOException {
    out.defaultWriteObject();

    if (writeTroubleIO == true) {
      throw new IOException("Causing trouble on write.");
    }
  }

  private void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();

    if (readTroubleCNF == true) {
      throw new ClassNotFoundException("Causing trouble on read.");
    }
    if (readTroubleIO == true) {
      throw new IOException("Causing trouble on read.");
    }
  }
}
