/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.file;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.date.DateTools;
import com.anaptecs.jeaf.tools.api.file.FileTools;
import com.anaptecs.jeaf.xfun.api.checks.Assert;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.ErrorCode;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

/**
 * Class provides useful helper methods for handling of files.
 * 
 * @author JEAF Development Team
 * @version 1.0
 */
@ToolsImplementation(toolsInterface = FileTools.class)
public final class FileToolsImpl implements FileTools {
  /**
   * We use a buffer with 8k size.
   */
  private static final int BUFFER_SIZE = 8 * 1024;

  private static final String EXTENSION_DELIMITER = ".";

  private static final char ZIP_FILE_SEPARATOR = '\\';

  /**
   * Default character set.
   */
  private static final Charset ZIP_CHARSET = Charset.forName("CP437");

  /**
   * Constructor of this class is private in order to ensure that no instances of this class can be created.
   */
  public FileToolsImpl( ) {
    // Nothing to do.
  }

  /**
   * Method tries to locate the resource with the passed name within the classpath of this application. Therefore the
   * class uses the class loader that was used to load this class.
   * 
   * @param pResourceName Name of the resource that should be loaded. The parameter must not be null.
   * @return URL URL of the resource with the passed name. The method never returns null.
   * @throws IOException if the resource could not be found.
   */
  @Override
  public URL locateResourceURL( String pResourceName ) throws IOException {
    // Check pResourceName for null.
    Check.checkInvalidParameterNull(pResourceName, "pResourceName");

    // Get class loader of this class.
    ClassLoader lClassLoader = this.getClass().getClassLoader();
    URL lResourceURL = lClassLoader.getResource(pResourceName);
    if (lResourceURL != null) {
      return lResourceURL;
    }
    // Resource could not be found within the application class path.
    else {
      String lMessage = "Resource '" + pResourceName + "' could not be found within the application class path.";
      throw new IOException(lMessage);
    }
  }

  @Override
  public InputStream getResourceAsStream( String pResourceName ) throws IOException {
    // Check pResourceName for null.
    Check.checkInvalidParameterNull(pResourceName, "pResourceName");

    // Get class loader of this class.
    ClassLoader lClassLoader = this.getClass().getClassLoader();
    InputStream lResourceAsStream = lClassLoader.getResourceAsStream(pResourceName);

    if (lResourceAsStream == null) {
      String lMessage = "Resource '" + pResourceName + "' could not be found within the application class path.";
      throw new IOException(lMessage);
    }
    return lResourceAsStream;
  }

  /**
   * Method returns the content of the passed file.
   * 
   * @param pFile File whose content should be returned. The parameter must not be null.
   * @return byte[] byte Array with the content of the file. The method never returns null. If the file is zero bytes
   * then an empty array with length 0 will be returned.
   * @throws IOException
   */
  @Override
  public byte[] getFileContent( File pFile ) throws IOException {
    // Check parameter
    Check.checkInvalidParameterNull(pFile, "pFile");

    try (FileInputStream lInputStream = new FileInputStream(pFile);) {
      return Tools.getStreamTools().getStreamContent(lInputStream);
    }
  }

  /**
   * Method returns the content of the file with the passed name.
   * 
   * @param pFileName Name of the file whose content should be returned. The parameter must not be null.
   * @return byte[] byte Array with the content of the file. The method never returns null. If the file is zero bytes
   * then an empty array with length 0 will be returned.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public byte[] getFileContent( String pFileName ) throws IOException {
    // Check parameter
    Check.checkInvalidParameterNull(pFileName, "pFileName");

    try (FileInputStream lInputStream = new FileInputStream(pFileName);) {
      return Tools.getStreamTools().getStreamContent(lInputStream);
    }
  }

  /**
   * Method returns the content of the file with the passed name.
   * 
   * @param pInputStream Input stream from which the content should be returned. The parameter must not be null.
   * @return byte[] byte Array with the content of the file. The method never returns null. If the file is zero bytes
   * then an empty array with length 0 will be returned.
   * @throws IOException
   */
  @Override
  public byte[] getFileContent( InputStream pInputStream ) throws IOException {
    // Check parameter
    Check.checkInvalidParameterNull(pInputStream, "pInputStream");

    return Tools.getStreamTools().getStreamContent(pInputStream);
  }

  /**
   * Method returns the content of the passed file. The file content will be converted from bytes to String using the
   * current encoding.
   * 
   * @param pFile File whose content should be returned. The parameter must not be null.
   * @return {@link String} Content of the file as string. The method never returns null. If the file is zero bytes then
   * an empty String will be returned.
   * @throws IOException
   */
  @Override
  public String getFileContentAsString( File pFile ) throws IOException {
    byte[] lFileContent = this.getFileContent(pFile);
    return new String(lFileContent);
  }

  /**
   * Method returns the content of the file with the passed name. The file content will be converted from bytes to
   * String using the current encoding.
   * 
   * @param pFileName Name of the file whose content should be returned. The parameter must not be null.
   * @return {@link String} Array with the content of the file. The method never returns null. If the file is zero bytes
   * then an empty String will be returned.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public String getFileContentAsString( String pFileName ) throws IOException {
    byte[] lFileContent = this.getFileContent(pFileName);
    return new String(lFileContent);
  }

  /**
   * Method returns the content of the file with the passed name. The file content will be converted from bytes to
   * String using the current encoding.
   * 
   * @param pInputStream Input stream with the content that should be returned. The parameter must not be null.
   * @return {@link String} Content of the stream as string. The method never returns null. If the stream contains zero
   * bytes then an empty String will be returned.
   * @throws IOException
   */
  @Override
  public String getFileContentAsString( InputStream pInputStream ) throws IOException {
    return Tools.getStreamTools().getStreamContentAsString(pInputStream);
  }

  /**
   * Method reads the passed amount of lines from the passed input stream.
   * 
   * @param pInputStream Input stream from which the content should be read. The parameter must not be null.
   * @param pFirstLine Index of the first line that should be read. Index starts with 0. The value must be zero or
   * greater.
   * @param pAmountOfLines Amount of lines that should be read. The value must be greater than zero.
   * @return {@link List} List of Strings. Each string represents a single line of the file. The method never returns
   * null.
   */
  @Override
  public List<String> readLines( InputStream pInputStream, int pFirstLine, int pAmountOfLines ) throws IOException {
    // Check parameters.
    Check.checkInvalidParameterNull(pInputStream, "pInputStream");
    Check.checkIsZeroOrGreater(pFirstLine, "pFirstLine");
    Check.checkIsZeroOrGreater(pAmountOfLines, "pAmountOfLines");

    // Process passed input stream. The stream will be closed when we are done.
    try (BufferedReader lReader = new BufferedReader(new InputStreamReader(pInputStream))) {
      List<String> lLines = new ArrayList<>(pAmountOfLines);
      int lLinesToBeRead = pFirstLine + pAmountOfLines;
      Iterator<String> lIterator = lReader.lines().iterator();
      int i = 0;

      // Read lines of file until file ends of maximum amount of lines is read.
      while (lIterator.hasNext()) {
        String lNextLine = lIterator.next();
        // Check if we already reached the first line that was requested.
        if (i >= pFirstLine) {
          lLines.add(lNextLine);
        }
        // We just have to skip this line.
        else {
          // Nothing to do.
        }
        // Increment line index
        i++;

        // Check if we already reach the last requested line.
        if (i >= lLinesToBeRead) {
          break;
        }
      }
      // Return read lines.
      return lLines;
    }
  }

  /**
   * Method reads the passed amount of lines from the passed file.
   * 
   * @param pFile File from which the content should be read. The parameter must not be null.
   * @param pFirstLine Index of the first line that should be read. Index starts with 0. The value must be zero or
   * greater.
   * @param pAmountOfLines Amount of lines that should be read. The value must be greater than zero.
   * @return {@link List} List of Strings. Each string represents a single line of the file. The method never returns
   * null.
   */
  @Override
  public List<String> readLines( File pFile, int pFirstLine, int pAmountOfLines ) throws IOException {
    // Check parameter
    Check.checkInvalidParameterNull(pFile, "pFile");

    return this.readLines(new FileInputStream(pFile), pFirstLine, pAmountOfLines);
  }

  /**
   * Method reads the passed amount of lines from the file with the passed name.
   * 
   * @param pFileName Name of the file from which the content should be read. The parameter must not be null.
   * @param pFirstLine Index of the first line that should be read. Index starts with 0. The value must be zero or
   * greater.
   * @param pAmountOfLines Amount of lines that should be read. The value must be greater than zero.
   * @return {@link List} List of Strings. Each string represents a single line of the file. The method never returns
   * null.
   */
  @Override
  public List<String> readLines( String pFileName, int pFirstLine, int pAmountOfLines ) throws IOException {
    // Check parameter
    Check.checkInvalidParameterNull(pFileName, "pFileName");

    return this.readLines(new FileInputStream(pFileName), pFirstLine, pAmountOfLines);
  }

  /**
   * Method returns the passed amount of lines from the passed input stream as one String. {@link File#separator} will
   * be used as separator for each line.
   * 
   * @param pInputStream Input stream from which the content should be read. The parameter must not be null.
   * @param pFirstLine Index of the first line that should be read. Index starts with 0. The value must be zero or
   * greater.
   * @param pAmountOfLines Amount of lines that should be read. The value must be greater than zero.
   * @return {@link String} String containing all the lines that were read. The method never returns null.
   */
  @Override
  public String readLinesAsString( InputStream pInputStream, int pFirstLine, int pAmountOfLines ) throws IOException {
    List<String> lLines = this.readLines(pInputStream, pFirstLine, pAmountOfLines);
    return this.linesToSingleString(lLines);
  }

  /**
   * Method returns the passed amount of lines from the passed file as one String. {@link File#separator} will be used
   * as separator for each line.
   * 
   * @param pFile File from which the content should be read. The parameter must not be null.
   * @param pFirstLine Index of the first line that should be read. Index starts with 0. The value must be zero or
   * greater.
   * @param pAmountOfLines Amount of lines that should be read. The value must be greater than zero.
   * @return {@link String} String containing all the lines that were read. The method never returns null.
   */
  @Override
  public String readLinesAsString( File pFile, int pFirstLine, int pAmountOfLines ) throws IOException {
    List<String> lLines = this.readLines(pFile, pFirstLine, pAmountOfLines);
    return this.linesToSingleString(lLines);
  }

  /**
   * Method returns the passed amount of lines from the file with passed name as one String. {@link File#separator} will
   * be used as separator for each line.
   * 
   * @param pFileName Name of the file from which the content should be read. The parameter must not be null.
   * @param pFirstLine Index of the first line that should be read. Index starts with 0. The value must be zero or
   * greater.
   * @param pAmountOfLines Amount of lines that should be read. The value must be greater than zero.
   * @return {@link String} String containing all the lines that were read. The method never returns null.
   */
  @Override
  public String readLinesAsString( String pFileName, int pFirstLine, int pAmountOfLines ) throws IOException {
    List<String> lLines = this.readLines(pFileName, pFirstLine, pAmountOfLines);
    return this.linesToSingleString(lLines);
  }

  /**
   * Method converts the passed list of string into one single string where each string is treated as a line. Between
   * the lines {@link File#separator} is used.
   * 
   * @param pLines Lines that should be concatenated into one single string.
   * @return {@link String} All lines as one single string. The method never returns null.
   */
  private String linesToSingleString( List<String> pLines ) {
    StringBuilder lBuilder = new StringBuilder();
    int i = 0;
    for (String lNextLine : pLines) {
      lBuilder.append(lNextLine);
      i++;
      if (i < pLines.size()) {
        lBuilder.append(File.separator);
      }
    }
    return lBuilder.toString();
  }

  /**
   * Method writes the passed content to the file with the passed name.
   * 
   * @param pOutputStream Output stream to which the content should be written. The parameter must not be null and its
   * the caller's responsibility to close the stream again.
   * @param pFileContent Byte array with the content that should be written. The parameter may be null. In this case the
   * file will be empty.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public void writeFileContent( OutputStream pOutputStream, String pFileContent ) throws IOException {
    byte[] lContentAsBytes;
    if (pFileContent != null) {
      lContentAsBytes = pFileContent.getBytes();
    }
    else {
      lContentAsBytes = null;
    }
    this.writeFileContent(pOutputStream, lContentAsBytes);
  }

  /**
   * Method writes the passed content to the file with the passed name.
   * 
   * @param pOutputStream Output stream to which the content should be written. The parameter must not be null and its
   * the caller's responsibility to close the stream again.
   * @param pFileContent Byte array with the content that should be written. The parameter may be null. In this case the
   * file will be empty.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public void writeFileContent( OutputStream pOutputStream, byte[] pFileContent ) throws IOException {
    // Check parameters.
    Check.checkInvalidParameterNull(pOutputStream, "pOutputStream");

    // Handle null arrays.
    if (pFileContent == null) {
      pFileContent = new byte[0];
    }

    BufferedOutputStream lBufferedOutputStream;
    if (pOutputStream instanceof BufferedOutputStream) {
      lBufferedOutputStream = (BufferedOutputStream) pOutputStream;
    }
    else {
      lBufferedOutputStream = new BufferedOutputStream(pOutputStream, BUFFER_SIZE);
    }

    lBufferedOutputStream.write(pFileContent);
    lBufferedOutputStream.flush();
  }

  /**
   * Method writes the passed content to the file with the passed name.
   * 
   * @param pFileName Name of the file whose content should be returned. The parameter must not be null.
   * @param pFileContent Byte array with the content that should be written. The parameter may be null. In this case the
   * file will be empty.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public void writeFileContent( String pFileName, byte[] pFileContent ) throws IOException {
    // Check parameters.
    Check.checkInvalidParameterNull(pFileName, "pFileName");

    try (OutputStream lOutputStream = new FileOutputStream(pFileName)) {
      this.writeFileContent(lOutputStream, pFileContent);
    }
  }

  /**
   * Method writes the passed content to the passed file.
   * 
   * @param pFile File whose content should be written. The parameter must not be null.
   * @param pFileContent Byte array with the content that should be written. The parameter may be null. In this case the
   * file will be empty.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public void writeFileContent( File pFile, byte[] pFileContent ) throws IOException {
    // Check parameters.
    Check.checkInvalidParameterNull(pFile, "pFile");

    try (OutputStream lOutputStream = new FileOutputStream(pFile)) {
      this.writeFileContent(lOutputStream, pFileContent);
    }
  }

  /**
   * Method writes the passed content to the passed file.
   * 
   * @param pFile File whose content should be written. The parameter must not be null.
   * @param pFileContent String with the content that should be written. The parameter may be null. In this case the
   * file will be empty.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public void writeFileContent( File pFile, String pFileContent ) throws IOException {
    // Check parameters.
    Check.checkInvalidParameterNull(pFile, "pFile");

    try (OutputStream lOutputStream = new FileOutputStream(pFile)) {
      this.writeFileContent(lOutputStream, pFileContent);
    }
  }

  /**
   * Method writes the passed content to the file with the passed name.
   * 
   * @param pFileName Name of the file whose content should be returned. The parameter must not be null.
   * @param pFileContent String with the content that should be written. The parameter may be null. In this case the
   * file will be empty.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  public void writeFileContent( String pFileName, String pFileContent ) throws IOException {
    byte[] lContentAsBytes;
    if (pFileContent != null) {
      lContentAsBytes = pFileContent.getBytes();
    }
    else {
      lContentAsBytes = null;
    }

    this.writeFileContent(pFileName, lContentAsBytes);
  }

  /**
   * Method copies the file with the passed name to the passed destination.
   * 
   * @param pSource Name of the file that should be copied. The parameter must not be null.
   * @param pDestination Name of the destination file. The parameter must not be null.
   * @return long Size of the copied file in bytes.
   * @throws IOException if an error occurs when trying to copy the file.
   */
  @Override
  public long copyFile( String pSource, String pDestination ) throws IOException {
    // Check parameters.
    Check.checkInvalidParameterNull(pSource, "pSource");
    Check.checkInvalidParameterNull(pDestination, "pDestination");

    // Copy file.
    return this.copyFile(new File(pSource), new File(pDestination));
  }

  /**
   * Method copies the file with the passed name to the passed destination.
   * 
   * @param pSource File that should be copied. The parameter must not be null.
   * @param pDestination Destination file. The parameter must not be null.
   * @return long Size of the copied file in bytes.
   * @throws IOException if an error occurs when trying to copy the file.
   */
  @Override
  public long copyFile( File pSource, File pDestination ) throws IOException {
    // Check parameters.
    Check.checkInvalidParameterNull(pSource, "pSource");
    Check.checkInvalidParameterNull(pDestination, "pDestination");

    // Create destination file if it does not already exist.
    long lStart = System.nanoTime();
    boolean lFileCreated = pDestination.createNewFile();

    if (lFileCreated == true) {
      // Check if destination is a file.
      // Use file channel to copy files.
      long lTransferedBytes = 0;
      try (
          // Open streams. They will be closed automatically
          FileInputStream lFileInputStream = new FileInputStream(pSource);
          FileChannel lSourceChannel = lFileInputStream.getChannel();
          FileOutputStream lFileOutputStream = new FileOutputStream(pDestination);
          FileChannel lDestinationChannel = lFileOutputStream.getChannel();) {

        // Transfer data
        lTransferedBytes = lDestinationChannel.transferFrom(lSourceChannel, 0, lSourceChannel.size());
      }
      // Trace info about copy.
      long lEnd = System.nanoTime();
      Tools.getStreamTools().traceIOSummary(pSource.getName(), lTransferedBytes, lEnd - lStart,
          ToolsMessages.TRANSFERED_FILE_CONTENT);

      // Return size of copied file.
      return lTransferedBytes;
    }
    // Unable to create new file as it already exists.
    else {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_CREATE_NEW_FILE, pDestination.getName());
    }
  }

  /**
   * Method copies the content of the input stream to the passed output stream.
   * 
   * @param pFileName Name of the file that will be copied using streams. The name is only required for tracing.
   * @param pInputStream Source of the data that should be copied. The parameter must not be null.
   * @param pOutputStream Destination where the data should be copied to. The parameter must not be null.
   * @param pCloseStreams If the parameter is set to true then the streams will be closed in all cases.
   * @param pMaxBytes Maximum bytes that should be written. If the streams contains more bytes then the copy process
   * will be aborted. It's the responsibility of the caller to delete the created file if desired.
   * @return long Number of bytes that where copied.
   * @throws IOException If an exception occurs during copying.
   */
  @Override
  public long copyContent( String pFileName, InputStream pInputStream, OutputStream pOutputStream,
      boolean pCloseStreams )
    throws IOException {

    // Copy content using stream tools.
    return Tools.getStreamTools().copyContent(pFileName, pInputStream, pOutputStream, pCloseStreams);
  }

  /**
   * Method copies the content of the input stream to the passed output stream.
   * 
   * @param pFileName Name of the file that will be copied using streams. The name is only required for tracing.
   * @param pInputStream Source of the data that should be copied. The parameter must not be null.
   * @param pOutputStream Destination where the data should be copied to. The parameter must not be null.
   * @param pCloseStreams If the parameter is set to true then the streams will be closed in all cases.
   * @param pMaxBytes Maximum bytes that should be written. If the streams contains more bytes then the copy process
   * will be aborted. It's the responsibility of the caller to delete the created file if desired.
   * @return long Number of bytes that where copied.
   * @throws IOException If an exception occurs during copying.
   */
  @Override
  public long copyContent( String pFileName, InputStream pInputStream, OutputStream pOutputStream,
      boolean pCloseStreams, long pMaxBytes )
    throws IOException {

    // Copy content using stream tools.
    return Tools.getStreamTools().copyContent(pFileName, pInputStream, pOutputStream, pCloseStreams, pMaxBytes);
  }

  /**
   * Method transfers the content of the passed input stream to the passed file.
   * 
   * @param pFileName Name of the target file. The parameter must not be null.
   * @param pInputStream Input stream. The parameter must not be null.
   * @param pFile File to which the input stream should be written. The parameter must not be null.
   * @param pClose If the parameter is set to true then the stream and file will be closed in any cases.
   * @return long Number of bytes that were copied.
   * @throws IOException If an exception occurs during copying.
   */
  @Override
  public long copyContent( String pFileName, InputStream pInputStream, RandomAccessFile pFile, boolean pClose )
    throws IOException {

    // Copy content without limiting the amount of transfered bytes.
    return this.copyContent(pFileName, pInputStream, pFile, pClose, Long.MAX_VALUE);
  }

  /**
   * Method transfers the content of the passed input stream to the passed file.
   * 
   * @param pFileName Name of the target file. The parameter must not be null.
   * @param pInputStream Input stream. The parameter must not be null.
   * @param pFile File to which the input stream should be written. The parameter must not be null.
   * @param pClose If the parameter is set to true then the stream and file will be closed in any cases.
   * @param pMaxBytes Maximum bytes that should be written. If the streams contains more bytes then the copy process
   * will be aborted. It's the responsibility of the caller to delete the created file if desired.
   * @return long Number of bytes that were copied.
   * @throws IOException If an exception occurs during copying.
   */
  @Override
  public long copyContent( String pFileName, InputStream pInputStream, RandomAccessFile pFile, boolean pClose,
      long pMaxBytes )
    throws IOException {

    // Check parameters.
    Assert.assertNotNull(pInputStream, "pInputStream");
    Assert.assertNotNull(pFile, "pFile");

    // Copy content.
    long lStart = System.nanoTime();

    try {
      byte[] lBuffer = new byte[BUFFER_SIZE];
      int lLength;
      long lBytes = 0;
      pFile.setLength(0);
      while ((lLength = pInputStream.read(lBuffer)) != -1) {
        pFile.write(lBuffer, 0, lLength);
        lBytes += lLength;

        // Check if maximum bytes that can be transferred are exceeded.
        if (lBytes > pMaxBytes) {
          throw new JEAFSystemException(ToolsMessages.MAX_BYTES_EXCEEDED, Long.toString(pMaxBytes));
        }
      }
      // Trace info about copy.
      long lEnd = System.nanoTime();
      Tools.getStreamTools().traceIOSummary(pFileName, lBytes, lEnd - lStart, ToolsMessages.TRANSFERED_FILE_CONTENT);

      return lBytes;
    }
    finally {
      if (pClose == true) {
        pInputStream.close();
        pFile.close();
      }
    }

  }

  /**
   * COPYRIGHT 2001-2006 The Apache Software Foundation.
   * 
   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
   * the License. You may obtain a copy of the License at
   * 
   * http://www.apache.org/licenses/LICENSE-2.0
   * 
   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
   * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
   * specific language governing permissions and limitations under the License.
   * 
   * Recursively count size of a directory (sum of the length of all files).
   * 
   * @param pDirectory directory directory to inspect, not null
   * @return size of directory in bytes, 0 if directory is security restricted
   */
  @Override
  public long calculateDirectorySize( File pDirectory ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pDirectory, "pDirectory");

    // Request is for an existing directory.
    if (pDirectory.isDirectory()) {
      long lSize = 0;
      // Recursively determine all files.
      File[] lFiles = pDirectory.listFiles();
      if (lFiles != null) {
        for (int i = 0; i < lFiles.length; i++) {
          File lFile = lFiles[i];
          if (lFile.isDirectory()) {
            lSize = lSize + this.calculateDirectorySize(lFile);
          }
          else {
            lSize = lSize + lFile.length();
          }
        }
        return lSize;
      }
      // Most likely user does not have appropriate access rights
      else {
        throw new JEAFSystemException(ToolsMessages.CALCULATION_OF_DIR_SIZE_FAILED, pDirectory.getAbsolutePath());
      }
    }
    // Parameter does not point to an existing directory.
    else {
      throw new JEAFSystemException(ToolsMessages.CALCULATION_OF_DIR_SIZE_FAILED, pDirectory.getAbsolutePath());
    }
  }

  /**
   * Method creates a new directory. If a directory already exists then nothing happens.
   * 
   * @param pDirectoryPath Path of the file or directory that should be deleted. The parameter must not be null.
   * @throws JEAFSystemException if the new directory cold not be created for some reason.
   */
  @Override
  public void createDirectory( String pDirectoryPath ) throws JEAFSystemException {
    // Check parameter.
    Check.checkInvalidParameterNull(pDirectoryPath, "pDirectoryPath");

    this.createDirectory(new File(pDirectoryPath));
  }

  /**
   * Method creates a new directory. If a directory already exists then nothing happens.
   * 
   * @param pDirectoryPath Path of the file or directory that should be deleted. The parameter must not be null.
   * @throws JEAFSystemException if the new directory cold not be created for some reason.
   */
  @Override
  public void createDirectory( Path pDirectoryPath ) throws JEAFSystemException {
    // Check parameter.
    Check.checkInvalidParameterNull(pDirectoryPath, "pDirectoryPath");

    this.createDirectory(pDirectoryPath.toFile());
  }

  /**
   * Method creates a new directory. If a directory already exists then nothing happens.
   * 
   * @param pDirectory Path of the file or directory that should be deleted. The parameter must not be null.
   * @throws JEAFSystemException if the new directory cold not be created for some reason.
   */
  @Override
  public void createDirectory( File pDirectory ) throws JEAFSystemException {
    // Check parameter.
    Check.checkInvalidParameterNull(pDirectory, "pDirectory");

    // Check if directory already exists.
    if (pDirectory.exists() == false) {
      boolean lSuccessful = pDirectory.mkdirs();
      if (lSuccessful == false) {
        throw new JEAFSystemException(ToolsMessages.UNABLE_TO_CREATE_DIRECTORY, pDirectory.getAbsolutePath());
      }
    }
    // Directory already exists.
    else {
      // Nothing to do.
    }
  }

  /**
   * Method deletes the file or directory with the passed path. If the passed file path points to a directory then it
   * can only be deleted if it is empty. If the file could not be deleted the method will throw an exception.
   * 
   * @param pFilePath Path of the file or directory that should be deleted. The parameter must not be null.
   * @throws JEAFSystemException if the passed file path could not be deleted.
   */
  @Override
  public void delete( String pFilePath ) throws JEAFSystemException {
    // Check parameter.
    Check.checkInvalidParameterNull(pFilePath, "pFilePath");

    // Delete path.
    this.delete(new File(pFilePath));
  }

  /**
   * Method deletes the file or directory with the passed path. If the passed file path points to a directory then it
   * can only be deleted if it is empty. If the file could not be deleted the method will throw an exception.
   * 
   * @param pFilePath Path of the file or directory that should be deleted. The parameter must not be null.
   * @throws JEAFSystemException if the passed file path could not be deleted.
   */
  @Override
  public void delete( Path pFilePath ) throws JEAFSystemException {
    // Check parameter.
    Check.checkInvalidParameterNull(pFilePath, "pFilePath");

    // Delete path.
    this.delete(pFilePath.toFile());
  }

  /**
   * Method deleted the passed file object. If the passed file object points to a directory then it can only be deleted
   * if it is empty. If the file could not be deleted the method will throw an exception.
   * 
   * @param pFile File object that should be deleted. The parameter must not be null.
   * @throws JEAFSystemException if the passed file object could not be deleted.
   */
  @Override
  public void delete( File pFile ) throws JEAFSystemException {
    // Check parameter.
    Check.checkInvalidParameterNull(pFile, "pFile");

    // Delete file
    try {
      Files.delete(pFile.toPath());
    }
    catch (IOException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_DELETE_FILE, e, pFile.getName());
    }
  }

  /**
   * Method deletes the file or directory with the passed path. If the passed file path points to a directory then it
   * can only be deleted if it is empty. If the file could not be deleted the method return false.
   * 
   * @param pFilePath Path of the file or directory that should be deleted. The parameter must not be null.
   * @return boolean Method returns true if the file could be deleted and false if not.
   */
  @Override
  public boolean tryDelete( String pFilePath ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFilePath, "pFilePath");

    // Delete path.
    return this.tryDelete(new File(pFilePath));
  }

  /**
   * Method deletes the file or directory with the passed path. If the passed file path points to a directory then it
   * can only be deleted if it is empty. If the file could not be deleted the method return false.
   * 
   * @param pFilePath Path of the file or directory that should be deleted. The parameter must not be null.
   * @return boolean Method returns true if the file could be deleted and false if not.
   */
  @Override
  public boolean tryDelete( Path pFilePath ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFilePath, "pFilePath");

    // Delete path.
    return this.tryDelete(pFilePath.toFile());
  }

  /**
   * Method deleted the passed file object. If the passed file object points to a directory then it can only be deleted
   * if it is empty. If the file could not be deleted the method return false.
   * 
   * @param pFile File object that should be deleted. The parameter must not be null.
   * @return boolean Method returns true if the file could be deleted and false if not.
   */
  @Override
  public boolean tryDelete( File pFile ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFile, "pFile");

    // Try to delete file.
    return pFile.delete();
  }

  /**
   * Method deletes the passed file path and all of its child elements if it is a directory. If the passed file path
   * points to a directory then the directory may also contain other files and directories which will be deleted too.
   * The method thus is equivalent to "rm -rf".
   * 
   * @param pFilePath Path of the file or directory that should be deleted. The parameter must not be null.
   * @throws JEAFSystemException if the passed file path or any of its child elements could not be deleted.
   */
  @Override
  public void deleteRecursive( String pFilePath ) throws JEAFSystemException {
    // Check parameter.
    Check.checkInvalidParameterNull(pFilePath, "pFilePath");

    // Delete file path recursively.
    this.deleteRecursive(new File(pFilePath));
  }

  /**
   * Method deletes the passed file path and all of its child elements if it is a directory. If the passed file path
   * points to a directory then the directory may also contain other files and directories which will be deleted too.
   * The method thus is equivalent to "rm -rf".
   * 
   * @param pFilePath Path of the file or directory that should be deleted. The parameter must not be null.
   * @throws JEAFSystemException if the passed file path or any of its child elements could not be deleted.
   */
  @Override
  public void deleteRecursive( Path pFilePath ) throws JEAFSystemException {
    // Check parameter.
    Check.checkInvalidParameterNull(pFilePath, "pFilePath");

    // Delete file path recursively.
    this.deleteRecursive(pFilePath.toFile());
  }

  /**
   * Method deletes the passed file object and all of its child elements if it is a directory. If the passed file object
   * points to a directory then the directory may also contain other files and directories which will be deleted too.
   * The method thus is equivalent to "rm -rf".
   * 
   * @param pFile File object that should be deleted. The parameter must not be null.
   * @throws JEAFSystemException if the passed file object or any of its child elements could not be deleted.
   */
  @Override
  public void deleteRecursive( File pFile ) throws JEAFSystemException {
    // Check parameter.
    Check.checkInvalidParameterNull(pFile, "pFile");

    // Get child elements of passed file objects and delete them first.
    File[] lChildElements = pFile.listFiles();
    if (lChildElements != null) {
      for (int i = 0; i < lChildElements.length; i++) {
        this.deleteRecursive(lChildElements[i]);
      }
    }
    // Delete passed file itself.
    try {
      Files.delete(pFile.toPath());
    }
    catch (IOException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_DELETE_FILE, pFile.getAbsolutePath());
    }
  }

  /**
   * Method deletes the passed file path and all of its child elements if it is a directory. If the passed file path
   * points to a directory then the directory may also contain other files and directories which will be deleted too.
   * The method thus is equivalent to "rm -rf".
   * 
   * @param pFilePath Path of the file or directory that should be deleted. The parameter must not be null.
   * @param pContinue Parameter defines if the method should try to delete further files if one file or directory can
   * not be deleted. <code>true</code> means that the method continues to try to delete and <code>false</code> not.
   * @return boolean Method returns true if the file or directory could be deleted and false if not.
   */
  @Override
  public boolean tryDeleteRecursive( String pFilePath, boolean pContinue ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFilePath, "pFilePath");

    // Delete file path recursively.
    return this.tryDeleteRecursive(new File(pFilePath), pContinue);
  }

  /**
   * Method deletes the passed file path and all of its child elements if it is a directory. If the passed file path
   * points to a directory then the directory may also contain other files and directories which will be deleted too.
   * The method thus is equivalent to "rm -rf".
   * 
   * @param pFilePath Path of the file or directory that should be deleted. The parameter must not be null.
   * @param pContinue Parameter defines if the method should try to delete further files if one file or directory can
   * not be deleted. <code>true</code> means that the method continues to try to delete and <code>false</code> not.
   * @return boolean Method returns true if the file or directory could be deleted and false if not.
   */
  @Override
  public boolean tryDeleteRecursive( Path pFilePath, boolean pContinue ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFilePath, "pFilePath");

    // Delete file path recursively.
    return this.tryDeleteRecursive(pFilePath.toFile(), pContinue);
  }

  /**
   * Method deletes the passed file object and all of its child elements if it is a directory. If the passed file object
   * points to a directory then the directory may also contain other files and directories which will be deleted too.
   * The method thus is equivalent to "rm -rf".
   * 
   * @param pFile File object that should be deleted. The parameter must not be null.
   * @param pContinue Parameter defines if the method should try to delete further files if one file or directory can
   * not be deleted. <code>true</code> means that the method continues to try to delete and <code>false</code> not.
   * @return boolean Method returns true if the file or directory could be deleted and false if not.
   */
  @Override
  public boolean tryDeleteRecursive( File pFile, boolean pContinue ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFile, "pFile");

    // Get child elements of passed file objects and delete them first.
    boolean lDeleteSucccessful = true;
    File[] lChildElements = pFile.listFiles();
    if (lChildElements != null) {
      for (int i = 0; i < lChildElements.length; i++) {
        boolean lDeleted = this.tryDeleteRecursive(lChildElements[i], pContinue);
        lDeleteSucccessful = lDeleteSucccessful && lDeleted;
        if (lDeleteSucccessful == false && pContinue == false) {
          break;
        }
      }
    }
    // We do not continue deleting file.
    if (lDeleteSucccessful == false && pContinue == false) {
      // So nothing to do here
    }
    // Continue deleting.
    else {
      // Delete passed file itself.
      boolean lDeleted = pFile.delete();
      lDeleteSucccessful = lDeleteSucccessful && lDeleted;
    }
    return lDeleteSucccessful;
  }

  /**
   * Method extracts the passed zip file to the passed directory.
   * 
   * @param pZipFilePath Zip file that should be extracted. The parameter must not be null.
   * @param pExtractDirectoryPath Directory to which the zip file should be extracted. The extract directory must
   * already exist. The parameter must not be null
   * @param pMaxSize Maximum size that the extracted archive may consume on the file system. The parameter is required
   * for security reasons to prevent Zip Bomb attacks. If the check should not be executed then 0 should be passed as
   * maximum size.
   * @throws IOException if an IOException occurs when trying to extract the passed zip file.
   */
  @Override
  public void extractZipFile( String pZipFilePath, String pExtractDirectoryPath, long pMaxSize ) throws IOException {
    this.extractZipFile(new File(pZipFilePath), new File(pExtractDirectoryPath), pMaxSize);
  }

  /**
   * Method extracts the passed zip file to the passed directory.
   * 
   * @param pZipFile Zip file that should be extracted. The parameter must not be null.
   * @param pExtractDirectory Directory to which the zip file should be extracted. The extract directory must already
   * exist. The parameter must not be null
   * @param pExtractMaxSize Maximum size that the extracted archive may consume on the file system. The parameter is
   * required for security reasons to prevent Zip Bomb attacks. If the check should not be executed then 0 should be
   * passed as maximum size.
   * @throws IOException if an IOException occurs when trying to extract the passed zip file.
   */
  @Override
  public void extractZipFile( File pZipFile, File pExtractDirectory, long pMaxExtractSize ) throws IOException {
    // Check parameters
    Check.checkInvalidParameterNull(pZipFile, "pZipFile");
    Check.checkInvalidParameterNull(pExtractDirectory, "pExtractDirectory");

    // Create file object for zip file.
    long lStart = System.nanoTime();

    // Process zip file.
    try (ZipFile lZipFile = new ZipFile(pZipFile, ZIP_CHARSET);) {

      // In order to prevent Zip Bomb attacks we can limit the amount of bytes written to the file systems.
      long lBytesLeft = pMaxExtractSize;
      final String lCanonicalExtractDirectory = pExtractDirectory.getCanonicalPath();

      // For purpose of proper cleanup we keep a list of all files that were already created.
      List<File> lCreatedFiles = new ArrayList<>(lZipFile.size());

      // Process each entry
      Enumeration<? extends ZipEntry> lZipFileEntries = lZipFile.entries();
      while (lZipFileEntries.hasMoreElements()) {
        // Get next zip entry.
        ZipEntry lZipEntry = lZipFileEntries.nextElement();

        // Create new file object for current zip entry. Due to the fact that the path separator for ZIP archives is
        // always '\' we have to ensure that the right path separator is used depending on the platform.
        String lCurrentEntryName = lZipEntry.getName().replace(ZIP_FILE_SEPARATOR, File.separatorChar);
        File lTempFile = new File(pExtractDirectory, lCurrentEntryName);

        // In order to protect us from directory traversal attacks we have to compute the canonical path even though it
        // costs a little performance.
        String lNewFileCanonicalPath = lTempFile.getCanonicalPath();
        File lNewFile = new File(lNewFileCanonicalPath);

        // Ensure that extracted files do not break out from target directory.
        if (lNewFileCanonicalPath.startsWith(lCanonicalExtractDirectory + File.separator) == true) {
          // Create parent directory if required.
          File lEntryParent = lNewFile.getParentFile();
          lEntryParent.mkdirs();

          // Extraction only needs to be done fore files.
          if (lZipEntry.isDirectory() == false) {
            // Add created file to list of all created files.
            lCreatedFiles.add(lNewFile);

            // Extract current zip entry.
            lBytesLeft =
                this.extractZipEntry(lZipFile, lZipEntry, lNewFile, lBytesLeft, pMaxExtractSize, lCreatedFiles);
          }
        }
        // Entry tries a directory traversal attacks.
        else {
          throw new JEAFSystemException(ToolsMessages.PREVENTED_DIRECTORY_TRAVERSAL_ATTACK, pZipFile.getName(),
              lNewFileCanonicalPath);
        }
      }

      // Trace info about extraction.
      long lEnd = System.nanoTime();
      Tools.getStreamTools().traceIOSummary(pZipFile.getName(), pZipFile.length(), lEnd - lStart,
          ToolsMessages.EXTRACTED_FILE);
    }
  }

  /**
   * Method extracts the passed zip entry to the passed file.
   * 
   * @param pZipFile Zip file to which the zip entry belongs to. The parameter must not be null.
   * @param pZipEntry Zip entry that should be extracted. The parameter must not be null.
   * @param pOutputFile File to which the entry should be extracted. The parameter must not be null.s
   * @param pMaxBytes Maximum bytes that are allowed to be written during extraction. If the limit is exceeded then the
   * extraction process will be aborted.
   * @param pMaxExtractSize Maximum overall extract size.
   * @param pCreatedFiles Files that were already created during the extraction process. This information is required
   * for cleanup purposes if an exception occurs during extraction.
   * @return
   * @throws IOException
   */
  private long extractZipEntry( ZipFile pZipFile, ZipEntry pZipEntry, File pOutputFile, long pMaxBytes,
      long pMaxExtractSize, List<File> pCreatedFiles )
    throws IOException {

    InputStream lInputStream = pZipFile.getInputStream(pZipEntry);
    FileOutputStream lFileOutputStream = new FileOutputStream(pOutputFile);
    OutputStream lOutputStream = new BufferedOutputStream(lFileOutputStream, BUFFER_SIZE);
    try {
      long lBytesTransfered = this.copyContent(pZipEntry.getName(), lInputStream, lOutputStream, true, pMaxBytes);
      return pMaxBytes - lBytesTransfered;
    }
    catch (JEAFSystemException e) {
      // Delete all already created files and throw exception afterwards
      for (File lNextFile : pCreatedFiles) {
        this.tryDelete(lNextFile);
      }
      ErrorCode lErrorCode = ToolsMessages.ZIP_EXTRACTION_ABORTED;
      throw new JEAFSystemException(lErrorCode, e, pZipFile.getName(), Long.toString(pMaxExtractSize));
    }
    catch (IOException e) {
      // Delete all already created files and throw exception afterwards
      for (File lNextFile : pCreatedFiles) {
        this.tryDelete(lNextFile);
      }
      throw e;
    }
  }

  /**
   * Method returns the extension of the passed file name.
   * 
   * @param pFile The file. The parameter must not be null.
   * @return {@link String} Base name of the file. If the file has no base name then null will be returned.
   */
  @Override
  public String getBaseName( Path pFile ) {
    // Check parameter
    Check.checkInvalidParameterNull(pFile, "pFile");

    return this.getBaseName(pFile.toString());
  }

  /**
   * Method returns the extension of the passed file name.
   * 
   * @param pFile The file. The parameter must not be null.
   * @return {@link String} Base name of the file. If the file has no base name then null will be returned.
   */
  @Override
  public String getBaseName( File pFile ) {
    // Check parameter
    Check.checkInvalidParameterNull(pFile, "pFile");

    return this.getBaseName(pFile.getName());
  }

  /**
   * Method returns the extension of the passed file name.
   * 
   * @param pFilename Complete name of the file. The parameter must not be null.
   * @return {@link String} Base name of the file. If the file has no base name then null will be returned.
   */
  @Override
  public String getBaseName( String pFilename ) {
    // Check parameter
    Check.checkInvalidParameterNull(pFilename, "pFilename");

    // Locate extension delimiter and return extension
    int lLastIndex = pFilename.lastIndexOf(EXTENSION_DELIMITER);
    String lBaseName;
    if (lLastIndex > 0) {
      lBaseName = pFilename.substring(0, lLastIndex);
    }
    else {
      lBaseName = null;
    }
    return lBaseName;
  }

  /**
   * Method returns the extension of the passed file name.
   * 
   * @param pFile The file. The parameter must not be null.
   * @return {@link String} Extension of the file. Method returns null if the file name has no extension.
   */
  @Override
  public String getExtension( Path pFile ) {
    // Check parameter
    Check.checkInvalidParameterNull(pFile, "pFile");

    return this.getExtension(pFile.toString());
  }

  /**
   * Method returns the extension of the passed file name.
   * 
   * @param pFile The file. The parameter must not be null.
   * @return {@link String} Extension of the file. Method returns null if the file name has no extension.
   */
  @Override
  public String getExtension( File pFile ) {
    // Check parameter
    Check.checkInvalidParameterNull(pFile, "pFile");

    return this.getExtension(pFile.getName());
  }

  /**
   * Method returns the extension of the passed file name.
   * 
   * @param pFilename Complete name of the file. The parameter must not be null.
   * @return {@link String} Extension of the file. Method returns null if the file name has no extension.
   */
  @Override
  public String getExtension( String pFilename ) {
    // Check parameter
    Check.checkInvalidParameterNull(pFilename, "pFilename");

    // Locate extension delimiter and return extension
    int lLastIndex = pFilename.lastIndexOf(EXTENSION_DELIMITER);
    String lExtension;

    // File has extension
    if (lLastIndex >= 0 && pFilename.length() > lLastIndex + 1) {
      lExtension = pFilename.substring(lLastIndex + 1);
    }
    // File has no extension thus we return null.
    else {
      lExtension = null;
    }
    return lExtension;
  }

  /**
   * Method returns the creation time of the file as {@link Date}.
   * 
   * @param pFile File for which the creation time should be returned. The parameter must not be null.
   * @return {@link Date} Creation time of the file. The method never returns null.
   */
  @Override
  public Date getCreationTime( File pFile ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFile, "pFile");

    // Resolve file attributes.
    BasicFileAttributes lFileAttributes = this.getFileAttributes(pFile);

    // Return creation time as java.util.Date
    return DateTools.getDateTools().toDate(lFileAttributes.creationTime());
  }

  /**
   * Method returns the last modification of the file as {@link Date}.
   * 
   * @param pFile File for which the last modification time should be returned. The parameter must not be null.
   * @return {@link Date} Last modification time of the file. The method never returns null.
   */
  @Override
  public Date getLastModificationTime( File pFile ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFile, "pFile");

    // Resolve file attributes.
    BasicFileAttributes lFileAttributes = this.getFileAttributes(pFile);

    // Return last modification time as java.util.Date
    return DateTools.getDateTools().toDate(lFileAttributes.lastModifiedTime());
  }

  /**
   * Method returns the last access time of the file as {@link Date}.
   * 
   * @param pFile File for which the last access time should be returned. The parameter must not be null.
   * @return {@link Date} Last access time of the file. The method never returns null.
   */
  @Override
  public Date getLastAccessTime( File pFile ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFile, "pFile");

    // Resolve file attributes.
    BasicFileAttributes lFileAttributes = this.getFileAttributes(pFile);

    // Return last access time as java.util.Date
    return DateTools.getDateTools().toDate(lFileAttributes.lastAccessTime());
  }

  /**
   * Method returns the creation time of the file as {@link Calendar}.
   * 
   * @param pFile File for which the creation time should be returned. The parameter must not be null.
   * @return {@link Calendar} Creation time of the file. The method never returns null.
   */
  @Override
  public Calendar getCreation( File pFile ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFile, "pFile");

    // Resolve file attributes.
    BasicFileAttributes lFileAttributes = this.getFileAttributes(pFile);

    // Return creation time as java.util.Calendar
    return DateTools.getDateTools().toCalendar(lFileAttributes.creationTime());
  }

  /**
   * Method returns the last modification of the file as {@link Calendar}.
   * 
   * @param pFile File for which the last modification time should be returned. The parameter must not be null.
   * @return {@link Calendar} Last modification time of the file. The method never returns null.
   */
  @Override
  public Calendar getLastModification( File pFile ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFile, "pFile");

    // Resolve file attributes.
    BasicFileAttributes lFileAttributes = this.getFileAttributes(pFile);

    // Return creation time as java.util.Calendar
    return DateTools.getDateTools().toCalendar(lFileAttributes.lastModifiedTime());
  }

  /**
   * Method returns the last access time of the file as {@link Calendar}.
   * 
   * @param pFile File for which the last access time should be returned. The parameter must not be null.
   * @return {@link Calendar} Last access time of the file. The method never returns null.
   */
  @Override
  public Calendar getLastAccess( File pFile ) {
    // Check parameter.
    Check.checkInvalidParameterNull(pFile, "pFile");

    // Resolve file attributes.
    BasicFileAttributes lFileAttributes = this.getFileAttributes(pFile);

    // Return creation time as java.util.Calendar
    return DateTools.getDateTools().toCalendar(lFileAttributes.lastAccessTime());
  }

  /**
   * Method returns the file attributes of Java NIO
   * 
   * @param pFile File object whose file attributes should be returned. The parameter must not be null.
   * @return {@link BasicFileAttributes} File attributes of the passed file. The method never returns null.
   * @throws JEAFSystemException in case that the file attributes can not be accessed.
   */
  private BasicFileAttributes getFileAttributes( File pFile ) throws JEAFSystemException {
    try {
      return Files.readAttributes(pFile.toPath(), BasicFileAttributes.class);
    }
    // Unable to access file attributes
    catch (IOException e) {
      ErrorCode lErrorCode = ToolsMessages.UNABLE_TO_ACCESS_FILE_ATTRIBUTES;
      throw new JEAFSystemException(lErrorCode, e, pFile.getAbsolutePath());
    }
  }

  /**
   * Method creates a new file name filter that filters based on the passed extensions.
   * 
   * @param pAcceptedExtensions List contains the white list for all file extensions that should be accepted by the
   * filter. The extensions can be provided with or without a leading star operator (*.txt or .txt / *.hbm.xml or
   * .hbm.xml). First characters must either be '*.' or '.' and the trimmed string must at least contain 1 real
   * character in addition to the mentioned prefix. If the passed list of accepted extensions is null or empty then this
   * means that all extensions are accepted.
   */
  @Override
  public FilenameFilter createExtensionFilenameFilter( List<String> pAcceptedExtensions ) {
    return new ExtensionFileFilter(pAcceptedExtensions);
  }

  /**
   * Method creates a new file name filter that filters based on the passed extensions. The exclusion list is an
   * additional feature of the filter in addition to the extension based filtering.
   * 
   * @param pAcceptedExtensions List contains the white list for all file extensions that should be accepted by the
   * filter. The extensions can be provided with or without a leading star operator (*.txt or .txt / *.hbm.xml or
   * .hbm.xml). First characters must either be '*.' or '.' and the trimmed string must at least contain 1 real
   * character in addition to the mentioned prefix. If the passed list of accepted extensions is null or empty then this
   * means that all extensions are accepted.
   * @param pExclusionList List contains names of all files that should be excluded by the filter. The file names must
   * not contain any path information. The parameter must not be null.
   */
  @Override
  public FilenameFilter createExtensionFilenameFilter( List<String> pAcceptedExtensions, List<String> pExclusionList ) {
    return new ExtensionFileFilter(pAcceptedExtensions, pExclusionList);
  }

  /**
   * Method returns all files that are inside the passed directory.
   * 
   * @param pDirectory Directory that should be used as the basis for the lookup for files. The parameter must not be
   * null and the passed File object must be an existing directory.
   * @return {@link List} List with all files that were found. The method never returns null.
   */
  @Override
  public List<File> listFiles( File pDirectory ) {
    return this.listFiles(pDirectory, null);
  }

  /**
   * Method returns all files that are inside the passed directory and match the passed filter criteria.
   * 
   * @param pDirectory Directory that should be used as the basis for the lookup for files. The parameter must not be
   * null and the passed File object must be an existing directory.
   * @param pAcceptedExtensions List contains the white list for all file extensions that should be accepted by the
   * filter. The extensions can be provided with or without a leading star operator (*.txt or .txt / *.hbm.xml or
   * .hbm.xml). First characters must either be '*.' or '.' and the trimmed string must at least contain 1 real
   * character in addition to the mentioned prefix. If the passed list of accepted extensions is null or empty then this
   * means that all extensions are accepted.
   * @param pExclusionList List contains names of all files that should be excluded by the filter. The file names must
   * not contain any path information. The parameter may be null.
   * @return {@link List} List with all files that were found according to the passed filter criteria. The method never
   * returns null.
   */
  @Override
  public List<File> listFiles( File pDirectory, List<String> pAcceptedExtensions, List<String> pExclusionList ) {
    // Create filter according to passed criteria and lookup files within passed directory.
    FilenameFilter lFileFilter = this.createExtensionFilenameFilter(pAcceptedExtensions, pExclusionList);

    return listFiles(pDirectory, lFileFilter);
  }

  /**
   * Method returns all files that are inside the passed directory and match the passed filter criteria.
   * 
   * @param pDirectory Directory that should be used as the basis for the lookup for files. The parameter must not be
   * null and the passed File object must be an existing directory.
   * @param pFileFilter File filter that should be used to filter directory content. Only files that are accepted by the
   * passed filter will be returned. The parameter may be null.
   * @return {@link List} List with all files that were found according to the passed filter criteria. The method never
   * returns null.
   */
  @Override
  public List<File> listFiles( File pDirectory, FilenameFilter pFileFilter ) {
    // Check parameters. Only directory in mandatory.
    Check.checkInvalidParameterNull(pDirectory, "pDirectory");

    // Ensure that pDirectory points to a really existing directory was passed.
    if (pDirectory.isDirectory()) {

      // As we ensure above that the directory is always valid lFiles will never be null.
      File[] lFiles;
      if (pFileFilter != null) {
        lFiles = pDirectory.listFiles(pFileFilter);
      }
      else {
        lFiles = pDirectory.listFiles();
      }
      return Arrays.asList(lFiles);
    }
    // Passed directory is not a directory or does not exist.
    else {
      throw new JEAFSystemException(ToolsMessages.NOT_A_DIRECTORY, pDirectory.getAbsolutePath());
    }
  }

  /**
   * Method returns all files that are inside the passed directory.
   * 
   * @param pDirectoryName Name of the Directory that should be used as the basis for the lookup for files. The
   * parameter must not be null and the passed file must be a directory.
   * @return {@link List} List with all files that were found. The method never returns null.
   */
  @Override
  public List<String> listFiles( String pDirectoryName ) {
    return this.listFiles(pDirectoryName, null);
  }

  /**
   * Method returns the names of all files that are inside the passed directory and match the passed filter criteria.
   * 
   * @param pDirectoryName Name of the Directory that should be used as the basis for the lookup for files. The
   * parameter must not be null and the passed file must be a directory.
   * @param pAcceptedExtensions List contains the white list for all file extensions that should be accepted by the
   * filter. The extensions can be provided with or without a leading star operator (*.txt or .txt / *.hbm.xml or
   * .hbm.xml). First characters must either be '*.' or '.' and the trimmed string must at least contain 1 real
   * character in addition to the mentioned prefix. If the passed list of accepted extensions is null or empty then this
   * means that all extensions are accepted.
   * @param pExclusionList List contains names of all files that should be excluded by the filter. The file names must
   * not contain any path information. The parameter may be null.
   * @return {@link List} List with all file names that were found according to the passed filter criteria. The method
   * never returns null.
   */
  @Override
  public List<String> listFiles( String pDirectoryName, List<String> pAcceptedExtensions,
      List<String> pExclusionList ) {

    // Create filter according to passed criteria and lookup files within passed directory.
    FilenameFilter lFileFilter = this.createExtensionFilenameFilter(pAcceptedExtensions, pExclusionList);

    return this.listFiles(pDirectoryName, lFileFilter);
  }

  /**
   * Method returns all files that are inside the passed directory and match the passed filter criteria.
   * 
   * @param pDirectoryName Name of the Directory that should be used as the basis for the lookup for files. The
   * parameter must not be null and the passed file must be a directory.
   * @param pFileFilter File filter that should be used to filter directory content. Only files that are accepted by the
   * passed filter will be returned. The parameter may be null.
   * @return {@link List} List with all files that were found according to the passed filter criteria. The method never
   * returns null.
   */
  @Override
  public List<String> listFiles( String pDirectoryName, FilenameFilter pFileFilter ) {
    // Check parameter. Only directory name is mandatory.
    Check.checkInvalidParameterNull(pDirectoryName, "pDirectoryName");

    // Lookup all files and convert them to a list of file names.
    File lCurrentFile = null;
    try {
      List<File> lFiles = this.listFiles(new File(pDirectoryName), pFileFilter);
      List<String> lFileNames = new ArrayList<>(lFiles.size());
      for (File lNextFile : lFiles) {
        lCurrentFile = lNextFile;
        lFileNames.add(lNextFile.getCanonicalPath());
      }
      return lFileNames;
    }
    catch (IOException e) {
      throw new JEAFSystemException(ToolsMessages.UNABLE_TO_CREATE_CANONICAL_PATH, e, lCurrentFile.getAbsolutePath(),
          e.getMessage());
    }
  }
}
