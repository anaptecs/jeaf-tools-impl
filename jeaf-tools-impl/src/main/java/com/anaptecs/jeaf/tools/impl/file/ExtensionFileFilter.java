/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.collections.CollectionTools;
import com.anaptecs.jeaf.xfun.api.checks.Assert;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

/**
 * Class implements interface FilenameFilter to filter all files by its extension. The class implements two ways to
 * filter files. By default all files that do have the defined extensions are accepted. In addition the class also
 * provides the possibility to specify an exclusion list of explicit file names.
 * 
 * @author JEAF Development Team
 * @version 1.0
 */
public class ExtensionFileFilter implements FilenameFilter {
  /**
   * List contains the extensions of all file types that are accepted by the filter. The list is never null and contains
   * at least one element.
   */
  private final List<String> acceptedExtensions;

  /**
   * List contains the names of all files that should be excluded by the filter.
   */
  private final List<String> exclusionList;

  /**
   * Constructor initializes the object. Thereby no actions are performed. The object does not use an exclusion list.
   * 
   * @param pAcceptedExtensions List contains the white list for all file extensions that should be accepted by the
   * filter. The extensions can be provided with or without a leading star operator (*.txt or .txt / *.hbm.xml or
   * .hbm.xml). First characters must either be '*.' or '.' and the trimmed string must at least contain 1 real
   * character in addition to the mentioned prefix. If the passed list of accepted extensions is null or empty then this
   * means that all extensions are accepted.
   */
  public ExtensionFileFilter( List<String> pAcceptedExtensions ) {
    this(pAcceptedExtensions, null);
  }

  /**
   * Constructor initializes the filter. Thereby a set of excluded files is passed to the object. The exclusion list is
   * an additional feature of the filter in addition to the extension based filtering.
   * 
   * @param pAcceptedExtensions List contains the white list for all file extensions that should be accepted by the
   * filter. The extensions can be provided with or without a leading star operator (*.txt or .txt / *.hbm.xml or
   * .hbm.xml). First characters must either be '*.' or '.' and the trimmed string must at least contain 1 real
   * character in addition to the mentioned prefix. If the passed list of accepted extensions is null or empty then this
   * means that all extensions are accepted.
   * @param pExclusionList List contains names of all files that should be excluded by the filter. The file names must
   * not contain any path information. The parameter may be null.
   */
  public ExtensionFileFilter( List<String> pAcceptedExtensions, List<String> pExclusionList ) {
    // Both parameters may be null.

    // List of extensions was provided
    acceptedExtensions = this.normalizeAcceptedExtensions(pAcceptedExtensions);

    // Split all strings that are separated by ";"
    if (pExclusionList != null) {
      exclusionList = new ArrayList<>();
      for (String lNextParam : pExclusionList) {
        StringTokenizer lTokenizer = new StringTokenizer(lNextParam, ";");
        while (lTokenizer.hasMoreTokens()) {
          exclusionList.add(lTokenizer.nextToken().trim());
        }
      }
    }
    // No exclusion list provided.
    else {
      exclusionList = Collections.emptyList();
    }
  }

  /**
   * Method converts the passed list of extension into an internal normalized format.
   * 
   * @param pAcceptedExtensions List contains the white list for all file extensions that should be accepted by the
   * filter. The extensions can be provided with or without a leading star operator (*.txt or .txt / *.hbm.xml or
   * .hbm.xml). First characters must either be '*.' or '.' and the trimmed string must at least contain 1 real
   * character in addition to the mentioned prefix. If the passed list of accepted extensions is null or empty then this
   * means that all extensions are accepted.
   * @return List with extensions after the normalization was done. The method never returns null. The returned list is
   * immutable.
   */
  private List<String> normalizeAcceptedExtensions( List<String> pAcceptedExtensions ) {
    List<String> lExtensions;
    if (pAcceptedExtensions != null) {
      lExtensions = new ArrayList<>(pAcceptedExtensions.size());
      for (String lNextExtension : pAcceptedExtensions) {
        lExtensions.add(this.normalizeExtension(lNextExtension));
      }
    }
    // null instead of accepted extensions. Here, that's the same as an empty list.
    else {
      lExtensions = Collections.emptyList();
    }
    return CollectionTools.getCollectionTools().unmodifiableClone(lExtensions);
  }

  /**
   * Method normalizes the passed extension to it's internal representation.
   * 
   * @param pExtension Extension that should be normalized. The parameter must not be null.
   * @return {@link String} Normalized version of the passed extension.
   */
  private String normalizeExtension( String pExtension ) {
    // Check parameter
    Assert.assertNotNull(pExtension, "pExtension");

    // Trim passed extension.
    pExtension = pExtension.trim();

    String lNormalizedExtension;
    if (pExtension.startsWith("*.")) {
      if (pExtension.length() > 2) {
        lNormalizedExtension = pExtension.substring(1, pExtension.length());
      }
      // Invalid file extension filter.
      else {
        throw new JEAFSystemException(ToolsMessages.INVALID_EXTENSION, pExtension);
      }
    }
    else if (pExtension.startsWith(".")) {
      if (pExtension.length() > 2) {
        lNormalizedExtension = pExtension;
      }
      // Invalid file extension filter.
      else {
        throw new JEAFSystemException(ToolsMessages.INVALID_EXTENSION, pExtension);
      }
    }
    // Extension does not start with one of the expected character sequences.
    else {
      throw new JEAFSystemException(ToolsMessages.INVALID_EXTENSION, pExtension);
    }

    // Return normalized extension.
    return lNormalizedExtension;
  }

  /**
   * Method checks whether the file with the passed name is a accepted by the filter or not.
   * 
   * @param pDirectory File object representing the directory in which the file was found. The parameter is never used.
   * @param pFilename Name of the file that should be checked for compliance with this filter. The parameter must not be
   * null.
   * @return boolean Method returns true if pFile is accepted by the filter and false in all other cases.
   * 
   * @see java.io.FileFilter#accept(java.io.File)
   */
  public boolean accept( File pDirectory, String pFilename ) {
    // Check pDirectory and pFileName for null.
    Assert.assertNotNull(pFilename, "pFileName");

    // Filter accepts only files that have one of the accepted extensions.
    boolean lAcceptedExtension = false;

    // List of extensions contains at least one entry.
    if (acceptedExtensions.isEmpty() == false) {
      for (String lExtension : acceptedExtensions) {
        lAcceptedExtension = pFilename.toUpperCase().endsWith(lExtension.toUpperCase());
        if (lAcceptedExtension == true) {
          break;
        }
      }
    }
    // All extensions are accepted.
    else {
      lAcceptedExtension = true;
    }

    // Check if the file was not explicitly excluded.
    boolean lAcceptedFile;
    if (lAcceptedExtension == true) {
      // The passed file is accepted if it is not specified in the exclusion list.
      lAcceptedFile = !exclusionList.contains(pFilename);
    }
    else {
      lAcceptedFile = false;
    }
    return lAcceptedFile;
  }
}
