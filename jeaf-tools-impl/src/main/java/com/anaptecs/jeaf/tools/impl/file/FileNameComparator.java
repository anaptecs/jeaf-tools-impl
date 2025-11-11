/**
 * Copyright 2004 - 2020 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.file;

import java.io.File;
import java.util.Comparator;

public class FileNameComparator implements Comparator<File> {

  @Override
  public int compare( File pFile1, File pFile2 ) {
    return pFile1.getName().compareTo(pFile2.getName());
  }
}
