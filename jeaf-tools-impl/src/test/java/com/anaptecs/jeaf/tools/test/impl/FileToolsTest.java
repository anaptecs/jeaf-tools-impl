/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * Copyright 2004 - 2014 All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.file.FileTools;
import com.anaptecs.jeaf.tools.impl.file.ExtensionFileFilter;
import com.anaptecs.jeaf.tools.impl.file.FileNameComparator;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.checks.Assert;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.info.OperatingSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Test class to test the functionality of class com.anaptecs.jeaf.fwk.util.Tools.
 *
 * @author JEAF Development Team
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileToolsTest {

  /** The Constant DESTINATION_DIRECTORY. */
  private static final String DESTINATION_DIRECTORY = "./CopyTestDestination";

  @BeforeEach
  protected void setUp( ) throws Exception {
    File lDirectory = new File(DESTINATION_DIRECTORY);
    if (lDirectory.exists() == false) {
      lDirectory.mkdirs();
    }
  }

  /**
   * Method test coping of small files.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void testFileToolsCopyVerySmallFiles( ) throws IOException {
    String lSourceFileName = "./testdata/Calc_16.png";
    String lDestinationFileName = DESTINATION_DIRECTORY + "/VerySmallFile.png";
    FileTools.getFileTools().tryDelete(lDestinationFileName);

    // Test coping of files.
    FileTools lFileTools = FileTools.getFileTools();
    lFileTools.copyFile(lSourceFileName, lDestinationFileName);

    // Check if file was completely written
    try (FileInputStream lSourceInputStream = new FileInputStream(lSourceFileName);
        FileChannel lSourceChannel = lSourceInputStream.getChannel();
        FileInputStream lDestinationInputStream = new FileInputStream(lDestinationFileName);
        FileChannel lDestinationChannel = lDestinationInputStream.getChannel();) {

      assertEquals(lSourceChannel.size(), lDestinationChannel.size(), "File not completly written.");
    }
  }

  /**
   * Method test coping of small files.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void testFileToolsCopySmallFiles( ) throws IOException {
    String lSourceFileName = "./testdata/Screenshot_Maps_1.jpg";
    String lDestinationFileName = DESTINATION_DIRECTORY + "/SmallFile.jpg";
    FileTools.getFileTools().tryDelete(lDestinationFileName);

    // Test coping of files.
    FileTools.getFileTools().copyFile(lSourceFileName, lDestinationFileName);

    // Check if file was completely written
    try (FileInputStream lSourceInputStream = new FileInputStream(lSourceFileName);
        FileChannel lSourceChannel = lSourceInputStream.getChannel();
        FileInputStream lDestinationInputStream = new FileInputStream(lDestinationFileName);
        FileChannel lDestinationChannel = lDestinationInputStream.getChannel();) {

      assertEquals(lSourceChannel.size(), lDestinationChannel.size(), "File not completly written.");
    }

    // Compare source and destination file byte by byte
    FileTools lFileTools = FileTools.getFileTools();

    byte[] lSourceContent = lFileTools.getFileContent(lSourceFileName);

    byte[] lDestinationContent = lFileTools.getFileContent(lDestinationFileName);
    assertTrue(Arrays.equals(lSourceContent, lDestinationContent), "Source and destination file are not the same");

    // Try to overwrite file.
    try {
      lFileTools.copyFile(new File(lSourceFileName), new File(lDestinationFileName));
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_CREATE_NEW_FILE, e.getErrorCode());
    }

    // Test error handling.
    // Try to write file to directory without write access.
    OperatingSystem lOperatingSystem = XFun.getInfoProvider().getOperatingSystem();
    File lInvalidTarget;
    switch (lOperatingSystem) {
      case WINDOWS:
        lInvalidTarget = new File("C:\\PerfLogs\\Invalid.txt");
        break;

      case LINUX:
        lInvalidTarget = new File("/root/Invalid.txt");
        break;

      case MAC:
        lInvalidTarget = new File("/System/Invalid.txt");
        break;

      default:
        lInvalidTarget = null;
        Assert.unexpectedEnumLiteral(lOperatingSystem);
    }

    try {
      lFileTools.copyFile(new File(lSourceFileName), lInvalidTarget);
    }
    catch (IOException e) {
      // Nothing to do.
    }
  }

  /**
   * Method test coping of small files.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void testFileToolsCopyMediumFiles( ) throws IOException {
    String lSourceFileName = "./testdata/Nirvana - Smells Like Teen Spirit.mp3";
    String lDestinationFileName = DESTINATION_DIRECTORY + "/MediumFile.mp3";
    FileTools.getFileTools().tryDelete(lDestinationFileName);

    // Test coping of files.
    FileTools.getFileTools().copyFile(lSourceFileName, lDestinationFileName);

    // Check if file was completely written
    try (FileInputStream lSourceInputStream = new FileInputStream(lSourceFileName);
        FileChannel lSourceChannel = lSourceInputStream.getChannel();
        FileInputStream lDestinationInputStream = new FileInputStream(lDestinationFileName);
        FileChannel lDestinationChannel = lDestinationInputStream.getChannel();) {

      assertEquals(lSourceChannel.size(), lDestinationChannel.size(), "File not completly written.");
    }

    // Compare source and destination file byte by byte
    byte[] lSourceContent = FileTools.getFileTools().getFileContent(lSourceFileName);
    byte[] lDestinationContent = FileTools.getFileTools().getFileContent(lDestinationFileName);
    assertTrue(Arrays.equals(lSourceContent, lDestinationContent), "Source and destination file are not the same");
  }

  /**
   * Method test coping of small files.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void testFileToolsCopyLargeFiles( ) throws IOException {
    String lSourceFileName = "./testdata/JEAF_Icons.zip";
    String lDestinationFileName = DESTINATION_DIRECTORY + "/MediumFile.mp3";
    FileTools.getFileTools().tryDelete(lDestinationFileName);

    // Test coping of files.
    FileTools.getFileTools().copyFile(lSourceFileName, lDestinationFileName);

    // Check if file was completely written
    try (FileInputStream lSourceInputStream = new FileInputStream(lSourceFileName);
        FileChannel lSourceChannel = lSourceInputStream.getChannel();
        FileInputStream lDestinationInputStream = new FileInputStream(lDestinationFileName);
        FileChannel lDestinationChannel = lDestinationInputStream.getChannel();) {

      assertEquals(lSourceChannel.size(), lDestinationChannel.size(), "File not completly written.");
    }

    // Compare source and destination file byte by byte
    byte[] lSourceContent = FileTools.getFileTools().getFileContent(lSourceFileName);
    byte[] lDestinationContent = FileTools.getFileTools().getFileContent(lDestinationFileName);
    assertTrue(Arrays.equals(lSourceContent, lDestinationContent), "Source and destination file are not the same");
  }

  @Test
  public void testGetFileContent( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();
    String lPlainTextFileName = "./testdata/plain.txt";
    File lPlainFile = new File(lPlainTextFileName);
    String lFileContentAsString = lFileTools.getFileContentAsString(lPlainTextFileName);

    OperatingSystem lOperatingSystem = XFun.getInfoProvider().getOperatingSystem();
    if (lOperatingSystem == OperatingSystem.WINDOWS) {
      String lFileContentWindows = "This is a simple file with plain text.\r\nSecond line of plain text.";
      assertEquals(lFileContentWindows, lFileTools.getFileContentAsString(lPlainTextFileName));
    }
    else if (lOperatingSystem == OperatingSystem.LINUX) {
      String lFileContentLinux = "This is a simple file with plain text.\nSecond line of plain text.";
      assertEquals(lFileContentLinux, lFileTools.getFileContentAsString(lPlainTextFileName));
    }

    if (lOperatingSystem == OperatingSystem.WINDOWS) {
      String lFileContentWindows = "This is a simple file with plain text.\r\nSecond line of plain text.";
      assertEquals(lFileContentWindows, lFileTools.getFileContentAsString(lPlainFile));
    }
    else if (lOperatingSystem == OperatingSystem.LINUX) {
      String lFileContentLinux = "This is a simple file with plain text.\nSecond line of plain text.";
      assertEquals(lFileContentLinux, lFileTools.getFileContentAsString(lPlainFile));
    }

    if (lOperatingSystem == OperatingSystem.WINDOWS) {
      try (InputStream lInputStream = new FileInputStream(lPlainTextFileName)) {
        lFileContentAsString = lFileTools.getFileContentAsString(lInputStream);
        assertEquals("This is a simple file with plain text.\r\nSecond line of plain text.", lFileContentAsString);
      }
    }
    else if (lOperatingSystem == OperatingSystem.LINUX) {
      try (InputStream lInputStream = new FileInputStream(lPlainTextFileName)) {
        lFileContentAsString = lFileTools.getFileContentAsString(lInputStream);
        assertEquals("This is a simple file with plain text.\nSecond line of plain text.", lFileContentAsString);
      }
    }

    // Try to read content from file that does not exist.
    try {
      lFileTools.getFileContentAsString("InvalidFile.txt");
      fail("Exception expected when trying to read from not existing file.");
    }
    catch (IOException e) {
      // Nothing to do.
    }

    try {
      lFileTools.getFileContentAsString(new File("InvalidFile.txt"));
      fail("Exception expected when trying to read from not existing file.");
    }
    catch (IOException e) {
      // Nothing to do.
    }

    // Test file content access as bytes.
    byte[] lExpectedBytes;
    if (lOperatingSystem == OperatingSystem.WINDOWS) {
      lExpectedBytes = new byte[] { 84, 104, 105, 115, 32, 105, 115, 32, 97, 32, 115, 105, 109, 112, 108, 101, 32, 102,
        105, 108, 101, 32, 119, 105, 116, 104, 32, 112, 108, 97, 105, 110, 32, 116, 101, 120, 116, 46, 13, 10, 83, 101,
        99, 111, 110, 100, 32, 108, 105, 110, 101, 32, 111, 102, 32, 112, 108, 97, 105, 110, 32, 116, 101, 120, 116,
        46 };
    }
    else if (lOperatingSystem == OperatingSystem.LINUX || lOperatingSystem == OperatingSystem.MAC) {
      lExpectedBytes = new byte[] { 84, 104, 105, 115, 32, 105, 115, 32, 97, 32, 115, 105, 109, 112, 108, 101, 32, 102,
        105, 108, 101, 32, 119, 105, 116, 104, 32, 112, 108, 97, 105, 110, 32, 116, 101, 120, 116, 46, 10, 83, 101, 99,
        111, 110, 100, 32, 108, 105, 110, 101, 32, 111, 102, 32, 112, 108, 97, 105, 110, 32, 116, 101, 120, 116, 46 };
    }
    else {
      fail("Unexpected operating system " + lOperatingSystem);
      lExpectedBytes = null;
    }
    byte[] lFileContent = lFileTools.getFileContent(lPlainTextFileName);
    assertTrue(Arrays.equals(lExpectedBytes, lFileContent));

    lFileContent = lFileTools.getFileContent(lPlainFile);
    assertTrue(Arrays.equals(lExpectedBytes, lFileContent));

    try (InputStream lInputStream = new FileInputStream(lPlainTextFileName)) {
      lFileContent = lFileTools.getFileContent(lInputStream);
      assertTrue(Arrays.equals(lExpectedBytes, lFileContent));
    }
  }

  @Test
  public void testWriteContent( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();
    File lOutputFile = File.createTempFile("testWriteContent-", "");
    String lFileContent = "This is some simple file content.";
    String lOutputFileName = lOutputFile.getCanonicalPath();
    lFileTools.writeFileContent(lOutputFileName, lFileContent);

    // Check if file was really written.
    assertEquals(lFileContent, lFileTools.getFileContentAsString(lOutputFileName));

    // Try to write content using streams.
    try (OutputStream lOutputStream = new FileOutputStream(lOutputFileName)) {
      lFileTools.writeFileContent(lOutputStream, lFileContent);
    }
    assertEquals(lFileContent, lFileTools.getFileContentAsString(lOutputFileName));

    // Write bytes
    byte[] lContentAsBytes =
        new byte[] { 84, 104, 105, 115, 32, 105, 115, 32, 97, 32, 115, 105, 109, 112, 108, 101, 32, 102, 105, 108, 101,
          32, 119, 105, 116, 104, 32, 112, 108, 97, 105, 110, 32, 116, 101, 120, 116, 46, 13, 10, 83, 101, 99, 111, 110,
          100, 32, 108, 105, 110, 101, 32, 111, 102, 32, 112, 108, 97, 105, 110, 32, 116, 101, 120, 116, 46 };

    lFileTools.writeFileContent(lOutputFileName, lContentAsBytes);
    lFileContent = "This is a simple file with plain text.\r\nSecond line of plain text.";
    assertEquals(lFileContent, lFileTools.getFileContentAsString(lOutputFileName));

    lFileTools.writeFileContent(lOutputFile, lContentAsBytes);
    lFileContent = "This is a simple file with plain text.\r\nSecond line of plain text.";
    assertEquals(lFileContent, lFileTools.getFileContentAsString(lOutputFileName));

    // Try to write content using streams.
    try (OutputStream lOutputStream = new FileOutputStream(lOutputFileName)) {
      lFileTools.writeFileContent(lOutputStream, lContentAsBytes);
    }
    assertEquals(lFileContent, lFileTools.getFileContentAsString(lOutputFileName));

    // Write empty files.
    lFileTools.writeFileContent(lOutputFileName, (byte[]) null);
    assertEquals(0, lOutputFile.length());
    byte[] lBytes = lFileTools.getFileContent(lOutputFileName);
    assertEquals(0, lBytes.length);

    lFileTools.writeFileContent(lOutputFileName, (String) null);
    assertEquals(0, lOutputFile.length());
    lBytes = lFileTools.getFileContent(lOutputFileName);
    assertEquals(0, lBytes.length);

    lFileTools.writeFileContent(lOutputFile, (byte[]) null);
    assertEquals(0, lOutputFile.length());
    lBytes = lFileTools.getFileContent(lOutputFileName);
    assertEquals(0, lBytes.length);

    lFileTools.writeFileContent(lOutputFile, (String) null);
    assertEquals(0, lOutputFile.length());
    lBytes = lFileTools.getFileContent(lOutputFileName);
    assertEquals(0, lBytes.length);

    lFileTools.writeFileContent(lOutputFileName, "");
    assertEquals(0, lOutputFile.length());
    lBytes = lFileTools.getFileContent(lOutputFileName);
    assertEquals(0, lBytes.length);

    try (OutputStream lOutputStream = new BufferedOutputStream(new FileOutputStream(lOutputFileName))) {
      lFileTools.writeFileContent(lOutputStream, (byte[]) null);
    }
    String lFileContentAsString = lFileTools.getFileContentAsString(lOutputFileName);
    assertEquals(0, lFileContentAsString.length());
    try (OutputStream lOutputStream = new BufferedOutputStream(new FileOutputStream(lOutputFileName))) {
      lFileTools.writeFileContent(lOutputStream, (String) null);
    }
    lFileContentAsString = lFileTools.getFileContentAsString(lOutputFileName);
    assertEquals(0, lFileContentAsString.length());
    try (OutputStream lOutputStream = new BufferedOutputStream(new FileOutputStream(lOutputFileName))) {
      lFileTools.writeFileContent(lOutputStream, "");
    }
    assertEquals(0, lFileContentAsString.length());
  }

  @Test
  public void testCopyContentStream( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();
    File lPlainTextFile = new File("./testdata/plain.txt");
    String lPlainTextFileName = lPlainTextFile.getCanonicalPath();
    File lDestinationFile = File.createTempFile("testCopyContent-", "");

    InputStream lInputStream = new FileInputStream(lPlainTextFile);
    OutputStream lOutputStream = new FileOutputStream(lDestinationFile);
    long lBytesCopied = lFileTools.copyContent(lPlainTextFile.getName(), lInputStream, lOutputStream, true);

    // Ensure that data was copied and that stream is closed
    OperatingSystem lOperatingSystem = XFun.getInfoProvider().getOperatingSystem();
    if (lOperatingSystem == OperatingSystem.WINDOWS) {
      assertEquals(66, lBytesCopied);
      String lFileContentWindows = "This is a simple file with plain text.\r\nSecond line of plain text.";
      assertEquals(lFileContentWindows, lFileTools.getFileContentAsString(lDestinationFile));
    }
    else if (lOperatingSystem == OperatingSystem.LINUX || lOperatingSystem == OperatingSystem.MAC) {
      assertEquals(65, lBytesCopied);
      String lFileContentLinux = "This is a simple file with plain text.\nSecond line of plain text.";
      assertEquals(lFileContentLinux, lFileTools.getFileContentAsString(lDestinationFile));
    }
    else {
      fail("Unexpected operating system: " + lOperatingSystem);
    }

    try {
      lInputStream.available();
      fail("Exception expected as stream is already closed");
    }
    catch (IOException e) {
      assertEquals("Stream Closed", e.getMessage());
    }
    try {
      lOutputStream.write(0);
      fail("Exception expected as stream is already closed");
    }
    catch (IOException e) {
      assertEquals("Stream Closed", e.getMessage());
    }

    // Copy content once again. This time streams will not be closed.
    lDestinationFile = File.createTempFile("testCopyContent-", "");
    lInputStream = new FileInputStream(lPlainTextFile);
    lOutputStream = new FileOutputStream(lDestinationFile);
    lBytesCopied = lFileTools.copyContent(lPlainTextFile.getName(), lInputStream, lOutputStream, false);

    // Ensure that data was copied and that stream is closed
    lInputStream.available();
    lOutputStream.write('A');

    // Close streams
    lInputStream.close();
    lOutputStream.close();

    if (lOperatingSystem == OperatingSystem.WINDOWS) {
      assertEquals(66, lBytesCopied);
      String lFileContentWindows = "This is a simple file with plain text.\r\nSecond line of plain text.A";
      assertEquals(lFileContentWindows, lFileTools.getFileContentAsString(lDestinationFile));
    }
    else if (lOperatingSystem == OperatingSystem.LINUX) {
      assertEquals(65, lBytesCopied);
      String lFileContentLinux = "This is a simple file with plain text.\nSecond line of plain text.A";
      assertEquals(lFileContentLinux, lFileTools.getFileContentAsString(lDestinationFile));
    }

    lInputStream = new FileInputStream(lPlainTextFile);
    lOutputStream = new FileOutputStream(lDestinationFile);
    try {
      lFileTools.copyContent(lPlainTextFileName, lInputStream, lOutputStream, true, 8);
      fail("Expecting exceptin in case that more bytes are available then it's supposed to be.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.MAX_BYTES_EXCEEDED, e.getErrorCode());
    }
    try {
      lInputStream.available();
      fail("Exception expected as stream is already closed");
    }
    catch (IOException e) {
      assertEquals("Stream Closed", e.getMessage());
    }
    try {
      lOutputStream.write(0);
      fail("Exception expected as stream is already closed");
    }
    catch (IOException e) {
      assertEquals("Stream Closed", e.getMessage());
    }
  }

  @Test
  public void testCopyContentRandomAccessFile( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();
    File lPlainTextFile = new File("./testdata/plain.txt");
    String lPlainTextFileName = lPlainTextFile.getCanonicalPath();
    File lDestinationFile = File.createTempFile("testCopyContent-", "");

    InputStream lInputStream = new FileInputStream(lPlainTextFile);
    RandomAccessFile lOutputFile = new RandomAccessFile(lDestinationFile, "rw");
    long lBytesCopied = lFileTools.copyContent(lPlainTextFile.getName(), lInputStream, lOutputFile, true);

    // Ensure that data was copied and that stream is closed
    OperatingSystem lOperatingSystem = XFun.getInfoProvider().getOperatingSystem();
    if (lOperatingSystem == OperatingSystem.WINDOWS) {
      assertEquals(66, lBytesCopied);
      String lFileContentWindows = "This is a simple file with plain text.\r\nSecond line of plain text.";
      assertEquals(lFileContentWindows, lFileTools.getFileContentAsString(lDestinationFile));
    }
    else if (lOperatingSystem == OperatingSystem.LINUX || lOperatingSystem == OperatingSystem.MAC) {
      assertEquals(65, lBytesCopied);
      String lFileContentLinux = "This is a simple file with plain text.\nSecond line of plain text.";
      assertEquals(lFileContentLinux, lFileTools.getFileContentAsString(lDestinationFile));
    }
    else {
      fail("Unexpected operating system: " + lOperatingSystem);
    }

    try {
      lInputStream.available();
      fail("Exception expected as stream is already closed");
    }
    catch (IOException e) {
      assertEquals("Stream Closed", e.getMessage());
    }
    try {
      lOutputFile.read();
      fail("Exception expected as stream is already closed");
    }
    catch (IOException e) {
      assertEquals("Stream Closed", e.getMessage());
    }

    // Copy content once again. This time streams will not be closed.
    lDestinationFile = File.createTempFile("testCopyContent-", "");
    lInputStream = new FileInputStream(lPlainTextFile);
    lOutputFile = new RandomAccessFile(lDestinationFile, "rw");
    lBytesCopied = lFileTools.copyContent(lPlainTextFile.getName(), lInputStream, lOutputFile, false);

    // Ensure that data was copied and that stream is closed
    lInputStream.available();
    lOutputFile.read();

    // Close streams
    lInputStream.close();
    lOutputFile.close();

    if (lOperatingSystem == OperatingSystem.WINDOWS) {
      assertEquals(66, lBytesCopied);
      String lFileContentWindows = "This is a simple file with plain text.\r\nSecond line of plain text.";
      assertEquals(lFileContentWindows, lFileTools.getFileContentAsString(lDestinationFile));
    }
    else if (lOperatingSystem == OperatingSystem.LINUX || lOperatingSystem == OperatingSystem.MAC) {
      assertEquals(65, lBytesCopied);
      String lFileContentLinux = "This is a simple file with plain text.\nSecond line of plain text.";
      assertEquals(lFileContentLinux, lFileTools.getFileContentAsString(lDestinationFile));
    }
    else {
      fail("Unexpected operating system: " + lOperatingSystem);
    }

    lInputStream = new FileInputStream(lPlainTextFile);
    lOutputFile = new RandomAccessFile(lDestinationFile, "rw");
    try {
      lFileTools.copyContent(lPlainTextFileName, lInputStream, lOutputFile, true, 8);
      fail("Expecting exceptin in case that more bytes are available then it's supposed to be.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.MAX_BYTES_EXCEEDED, e.getErrorCode());
    }
    try {
      lInputStream.available();
      fail("Exception expected as stream is already closed");
    }
    catch (IOException e) {
      assertEquals("Stream Closed", e.getMessage());
    }
    try {
      lOutputFile.read();
      fail("Exception expected as stream is already closed");
    }
    catch (IOException e) {
      assertEquals("Stream Closed", e.getMessage());
    }

  }

  /**
   * Test file deletion.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void testFileDeletion( ) throws IOException {
    // Create directory structure for test case.
    File lSourceFile = new File("./testdata/JEAF_Icons.zip");
    String lDestinationFileName = DESTINATION_DIRECTORY + "/FileToDelete.zip";
    File lDestinationFile = new File(lDestinationFileName);
    File lSubDirs = new File(DESTINATION_DIRECTORY + "/Level_1" + "/Level_2" + "/Level_3");

    FileTools lFileTools = FileTools.getFileTools();
    lFileTools.tryDeleteRecursive(lSubDirs, true);

    assertFalse(lSubDirs.exists(), "Subdirectory structure must not exist yet " + lSubDirs.getAbsolutePath());
    lSubDirs.mkdirs();
    assertTrue(lSubDirs.exists(), "Subdirectory structure was not created");

    // Test single deletion.
    FileTools.getFileTools().tryDelete(lDestinationFileName);
    lFileTools.copyFile(lSourceFile, lDestinationFile);
    assertTrue(lDestinationFile.exists(), "Test file does not exist.");
    lFileTools.delete(lDestinationFile);
    assertFalse(lDestinationFile.exists(), "Test file does still exist.");

    lFileTools.copyFile(lSourceFile, lDestinationFile);
    assertTrue(lDestinationFile.exists(), "Test file does not exist.");
    lFileTools.delete(lDestinationFile.getCanonicalPath());
    assertFalse(lDestinationFile.exists(), "Test file does still exist.");

    lFileTools.copyFile(lSourceFile, lDestinationFile);
    assertTrue(lDestinationFile.exists(), "Test file does not exist.");
    lFileTools.delete(lDestinationFile.toPath());
    assertFalse(lDestinationFile.exists(), "Test file does still exist.");

    // Test try delete variants
    lFileTools.copyFile(lSourceFile, lDestinationFile);
    assertTrue(lDestinationFile.exists(), "Test file does not exist.");
    boolean lDeleted = lFileTools.tryDelete(lDestinationFile);
    assertTrue(lDeleted);
    assertFalse(lDestinationFile.exists(), "Test file does still exist.");

    lFileTools.copyFile(lSourceFile, lDestinationFile);
    assertTrue(lDestinationFile.exists(), "Test file does not exist.");
    lDeleted = lFileTools.tryDelete(lDestinationFile.getAbsolutePath());
    assertTrue(lDeleted);
    assertFalse(lDestinationFile.exists(), "Test file does still exist.");

    lFileTools.copyFile(lSourceFile, lDestinationFile);
    assertTrue(lDestinationFile.exists(), "Test file does not exist.");
    lDeleted = lFileTools.tryDelete(lDestinationFile.toPath());
    assertTrue(lDeleted);
    assertFalse(lDestinationFile.exists(), "Test file does still exist.");

    // Try to delete file a second time.
    try {
      lFileTools.delete(lDestinationFile);
      fail("Files can not be deleted more than once.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_DELETE_FILE, e.getErrorCode(), "Wrong error code.");
    }

    // Test deletion of not empty directory.
    try {
      lFileTools.delete(DESTINATION_DIRECTORY);
      fail("Non empty directory must not be deleted.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_DELETE_FILE, e.getErrorCode(), "Wrong error code.");
    }

    // Test recursive deletion of directory.
    File lDestinationDir = new File(DESTINATION_DIRECTORY);
    lFileTools.deleteRecursive(lDestinationDir);
    assertFalse(lSubDirs.exists(), "Directory structure was not deleted.");
    assertFalse(lDestinationDir.exists(), "Directory structure was not deleted.");

    // Test tryDelete methods
    assertFalse(lSubDirs.exists(), "Subdirectory structure must not exist yet " + lSubDirs.getAbsolutePath());
    lSubDirs.mkdirs();
    assertTrue(lSubDirs.exists(), "Subdirectory structure was not created");

    // Test single deletion.
    lFileTools.copyFile(lSourceFile, lDestinationFile);
    assertTrue(lDestinationFile.exists(), "Test file does not exist.");
    lFileTools.tryDelete(lDestinationFile);
    assertFalse(lDestinationFile.exists(), "Test file does still exist.");

    // Try to delete file a second time.
    lDeleted = lFileTools.tryDelete(lDestinationFile);
    assertFalse(lDeleted, "Files can not be deleted more than once.");

    // Test deletion of not empty directory.
    lDeleted = lFileTools.tryDelete(DESTINATION_DIRECTORY);
    assertFalse(lDeleted, "Non empty directory must not be deleted.");

    // Try to delete directory with an open file.
    lDestinationDir = new File(DESTINATION_DIRECTORY);
    RandomAccessFile lFile = new RandomAccessFile(DESTINATION_DIRECTORY + "/VerySmallFile.png", "rw");
    lDeleted = lFileTools.tryDeleteRecursive(lDestinationDir.getCanonicalPath(), false);
    // assertFalse(lDeleted);
    lDeleted = lFileTools.tryDeleteRecursive(lDestinationDir.toPath(), true);
    assertFalse(lDeleted);

    // Test error handling when trying to delete a directory recursively.
    try {
      lFileTools.deleteRecursive(lDestinationDir);
      fail("Exception expected when recursive deletion of directory fails.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_DELETE_FILE, e.getErrorCode());
    }
    try {
      lFileTools.deleteRecursive(lDestinationDir.getCanonicalPath());
      fail("Exception expected when recursive deletion of directory fails.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_DELETE_FILE, e.getErrorCode());
    }
    try {
      lFileTools.deleteRecursive(lDestinationDir.toPath());
      fail("Exception expected when recursive deletion of directory fails.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_DELETE_FILE, e.getErrorCode());
    }

    // Close file again so that directory can now be deleted.
    lFile.close();

    // Test recursive deletion of directory.
    lDeleted = lFileTools.tryDeleteRecursive(lDestinationDir, true);
    assertFalse(lSubDirs.exists(), "Directory structure was not deleted.");
    assertFalse(lDestinationDir.exists(), "Directory structure was not deleted.");

    // Create test directory again
    File lExtractDir = new File(DESTINATION_DIRECTORY + "/zipTest");
    lFileTools.tryDeleteRecursive(lExtractDir, true);
    File lZipFile = new File("./testdata/JEAF_Icons.zip");
    lFileTools.extractZipFile(lZipFile, lExtractDir, Long.MAX_VALUE);
    assertTrue(lExtractDir.exists());
    lFileTools.deleteRecursive(lExtractDir.getCanonicalPath());
    assertFalse(lExtractDir.exists());
    lFileTools.extractZipFile(lZipFile, lExtractDir, Long.MAX_VALUE);
    assertTrue(lExtractDir.exists());
    lFileTools.deleteRecursive(lExtractDir.toPath());
    assertFalse(lExtractDir.exists());
  }

  /**
   * Method tests JEAF's file tools methods that handle file names.
   */
  @Test
  public void testFileNameMethods( ) {
    FileTools lFileTools = FileTools.getFileTools();

    // Test base name and extension detection
    String lFilename = "abc.docx";
    assertEquals("abc", lFileTools.getBaseName(lFilename), "Wrong base name");
    assertEquals("docx", lFileTools.getExtension(lFilename), "Wrong extension");

    lFilename = "abc DCT.xls";
    assertEquals("abc DCT", lFileTools.getBaseName(lFilename), "Wrong base name");
    assertEquals("xls", lFileTools.getExtension(lFilename), "Wrong extension");
    assertEquals("abc DCT", lFileTools.getBaseName(new File(lFilename)), "Wrong base name");
    assertEquals("xls", lFileTools.getExtension(new File(lFilename)), "Wrong extension");
    assertEquals("abc DCT", lFileTools.getBaseName(new File(lFilename).toPath()), "Wrong base name");
    assertEquals("xls", lFileTools.getExtension(new File(lFilename).toPath()), "Wrong extension");

    lFilename = ".xls";
    assertNull(lFileTools.getBaseName(lFilename), "Wrong base name");
    assertEquals("xls", lFileTools.getExtension(lFilename), "Wrong extension");

    lFilename = "";
    assertNull(lFileTools.getBaseName(lFilename), "Wrong base name");
    assertNull(lFileTools.getExtension(lFilename), "Wrong extension");

    lFilename = "abc.";
    assertEquals("abc", lFileTools.getBaseName(lFilename), "Wrong base name");
    assertNull(lFileTools.getExtension(lFilename), "Wrong extension");
  }

  @Test
  public void testExtractArchive( ) throws IOException {
    File lExtractDir = new File(DESTINATION_DIRECTORY + "/zipTest");
    FileTools lFileTools = FileTools.getFileTools();
    lFileTools.tryDeleteRecursive(lExtractDir, true);

    File lExtractRoot = new File(lExtractDir, "JEAF_Icons");
    assertFalse(lExtractDir.exists());
    assertFalse(lExtractRoot.exists());

    File lZipFile = new File("./testdata/JEAF_Icons.zip");
    lFileTools.extractZipFile(lZipFile, lExtractDir, Long.MAX_VALUE);

    assertTrue(lExtractDir.exists());
    List<File> lFiles = lFileTools.listFiles(lExtractDir, null, null);
    assertEquals(1, lFiles.size());
    File lDirectory = lFiles.get(0);
    assertTrue(lDirectory.isDirectory());
    assertEquals(lExtractRoot.getCanonicalPath(), lDirectory.getCanonicalPath());
    assertTrue(lDirectory.exists());
    assertTrue(lExtractRoot.exists());
    long lSizeOfDirectory = lFileTools.calculateDirectorySize(lExtractDir);
    final int lTotalExpectedSize = 52512857;
    assertEquals(lTotalExpectedSize, lSizeOfDirectory);

    lFileTools.tryDeleteRecursive(lExtractDir, true);

    lFileTools.extractZipFile(lZipFile.getCanonicalPath(), lExtractDir.getCanonicalPath(), Long.MAX_VALUE);
    lSizeOfDirectory = lFileTools.calculateDirectorySize(lExtractDir);
    assertEquals(lTotalExpectedSize, lSizeOfDirectory);

    // Try to extract ZIP archive with size limitation.
    try {
      lFileTools.extractZipFile(lZipFile, lExtractDir, 10 * 1024 * 1024);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.ZIP_EXTRACTION_ABORTED, e.getErrorCode());
    }

    // Test directory traversal attack detection.
    try {
      lFileTools.extractZipFile(new File("./testdata/evil-zip-slip.zip"), lExtractDir, Long.MAX_VALUE);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.PREVENTED_DIRECTORY_TRAVERSAL_ATTACK, e.getErrorCode());
    }
  }

  @Test
  public void testExtensionFileFilter( ) {
    List<String> lExtensions = new ArrayList<>();
    lExtensions.add("*.txt");
    lExtensions.add(".doc");
    lExtensions.add(" *.docx");

    File lDirectory = new File(".");

    FilenameFilter lFilter = new ExtensionFileFilter(lExtensions);
    assertTrue(lFilter.accept(lDirectory, "My First Document.doc"), "'My First Document.doc' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello.txt"), "'Hello.txt' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello.docx"), "'Hello.txt' not accepted.");
    assertFalse(lFilter.accept(lDirectory, "Hello.dotx"), "'Hello.dotx' not accepted.");

    FileTools lFileTools = FileTools.getFileTools();
    lFilter = lFileTools.createExtensionFilenameFilter(lExtensions);
    assertTrue(lFilter.accept(lDirectory, "My First Document.doc"), "'My First Document.doc' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello.txt"), "'Hello.txt' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello.docx"), "'Hello.txt' not accepted.");
    assertFalse(lFilter.accept(lDirectory, "Hello.dotx"), "'Hello.dotx' not accepted.");

    // Test exclusion list.
    List<String> lExclusions = new ArrayList<>();
    lExclusions.add("Hello.txt");
    lExclusions.add("Weird.docx");
    lFilter = new ExtensionFileFilter(lExtensions, lExclusions);
    assertFalse(lFilter.accept(lDirectory, "Hello.txt"), "'Hello.txt' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello1.txt"), "'Hello1.txt' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello.docx"), "'Hello.txt' not accepted.");
    assertFalse(lFilter.accept(lDirectory, "Hello"), "'Hello.dotx' not accepted.");
    assertFalse(lFilter.accept(lDirectory, "Weird.docx"), "'Hello.txt' not accepted.");

    lFilter = lFileTools.createExtensionFilenameFilter(lExtensions, lExclusions);
    assertFalse(lFilter.accept(lDirectory, "Hello.txt"), "'Hello.txt' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello1.txt"), "'Hello1.txt' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello.docx"), "'Hello.txt' not accepted.");
    assertFalse(lFilter.accept(lDirectory, "Hello"), "'Hello.dotx' not accepted.");
    assertFalse(lFilter.accept(lDirectory, "Weird.docx"), "'Hello.txt' not accepted.");

    // Test filter that accepts all extensions.
    lFilter = new ExtensionFileFilter(null);
    assertTrue(lFilter.accept(lDirectory, "Hello.txt"), "'Hello.txt' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello.docx"), "'Hello.txt' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "windows.exe"), "'Hello.dotx' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello"), "'Hello.dotx' not accepted.");

    // Test empty filter.
    lFilter = new ExtensionFileFilter(new ArrayList<>());
    assertTrue(lFilter.accept(lDirectory, "Hello.txt"), "'Hello.txt' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello.docx"), "'Hello.txt' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "windows.exe"), "'Hello.dotx' not accepted.");
    assertTrue(lFilter.accept(lDirectory, "Hello"), "'Hello.dotx' not accepted.");

    // Test error handling
    try {
      lExtensions.add("*.");
      new ExtensionFileFilter(lExtensions);
      fail("Exception expected if an invalid extension is passed.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_EXTENSION, e.getErrorCode());
    }

    try {
      lExtensions.clear();
      lExtensions.add("*");
      new ExtensionFileFilter(lExtensions);
      fail("Exception expected if an invalid extension is passed.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_EXTENSION, e.getErrorCode());
    }

    try {
      lExtensions.clear();
      lExtensions.add(".");
      new ExtensionFileFilter(lExtensions);
      fail("Exception expected if an invalid extension is passed.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.INVALID_EXTENSION, e.getErrorCode());
    }
  }

  @Test
  public void testListFiles( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();

    // Prepare directory that should be used during this test.
    File lWorkingDir = new File(DESTINATION_DIRECTORY + "/listDirTest");
    lFileTools.tryDeleteRecursive(lWorkingDir, true);
    lWorkingDir.mkdirs();

    assertTrue(lWorkingDir.exists(), lWorkingDir.getCanonicalPath() + " does not exist.");

    File lZipFile = new File("./testdata/JEAF_Icons.zip");
    lFileTools.extractZipFile(lZipFile, lWorkingDir, Long.MAX_VALUE);
    File lRootDir = new File(lWorkingDir, "JEAF_Icons");
    assertTrue(lRootDir.exists(), lRootDir.getCanonicalPath() + " does not exist.");

    // List all files
    List<File> lFiles = lFileTools.listFiles(lRootDir);
    lFiles.sort(new FileNameComparator());
    assertEquals(8, lFiles.size());
    assertEquals("ai", lFiles.get(0).getName());
    assertTrue(lFiles.get(0).isDirectory());
    assertEquals("icon_list.txt", lFiles.get(1).getName());
    assertTrue(lFiles.get(1).isFile());
    assertEquals("icons", lFiles.get(2).getName());
    assertTrue(lFiles.get(2).isDirectory());
    assertEquals("license.txt", lFiles.get(3).getName());
    assertTrue(lFiles.get(3).isFile());
    assertEquals("preview", lFiles.get(4).getName());
    assertTrue(lFiles.get(4).isDirectory());
    assertEquals("preview.jpg", lFiles.get(5).getName());
    assertTrue(lFiles.get(5).isFile());
    assertEquals("preview_small.jpg", lFiles.get(6).getName());
    assertTrue(lFiles.get(6).isFile());
    assertEquals("readme.txt", lFiles.get(7).getName());
    assertTrue(lFiles.get(7).isFile());

    // List files without any restrictions.
    lFiles = lFileTools.listFiles(lRootDir, null, null);
    lFiles.sort(new FileNameComparator());
    assertEquals(8, lFiles.size());
    assertEquals("ai", lFiles.get(0).getName());
    assertTrue(lFiles.get(0).isDirectory());
    assertEquals("icon_list.txt", lFiles.get(1).getName());
    assertTrue(lFiles.get(1).isFile());
    assertEquals("icons", lFiles.get(2).getName());
    assertTrue(lFiles.get(2).isDirectory());
    assertEquals("license.txt", lFiles.get(3).getName());
    assertTrue(lFiles.get(3).isFile());
    assertEquals("preview", lFiles.get(4).getName());
    assertTrue(lFiles.get(4).isDirectory());
    assertEquals("preview.jpg", lFiles.get(5).getName());
    assertTrue(lFiles.get(5).isFile());
    assertEquals("preview_small.jpg", lFiles.get(6).getName());
    assertTrue(lFiles.get(6).isFile());
    assertEquals("readme.txt", lFiles.get(7).getName());
    assertTrue(lFiles.get(7).isFile());

    List<String> lExtensions = new ArrayList<>();
    lExtensions.add("*.jpg");
    List<String> lExclusions = new ArrayList<>();
    lFiles = lFileTools.listFiles(lRootDir, lExtensions, null);
    assertEquals(2, lFiles.size());
    assertEquals("preview.jpg", lFiles.get(0).getName());
    assertEquals("preview_small.jpg", lFiles.get(1).getName());

    // Test with empty directory.
    File lEmptyRootDir = new File(lWorkingDir, "EmptyRootDir");
    assertTrue(lEmptyRootDir.mkdir());
    lFiles = lFileTools.listFiles(lEmptyRootDir, null, null);
    assertEquals(0, lFiles.size());

    // Try to list not existing directory.
    try {
      lFileTools.listFiles(new File(lWorkingDir, "NotExisting"), null, null);
      fail("Exception expected when trying to list not existing directory.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.NOT_A_DIRECTORY, e.getErrorCode());
    }

    // Try to list file.
    try {
      lFileTools.listFiles(lZipFile, null, null);
      fail("Exception expected when trying to list file.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.NOT_A_DIRECTORY, e.getErrorCode());
    }

    lExclusions.add("preview.jpg");
    lFiles = lFileTools.listFiles(lRootDir, lExtensions, lExclusions);
    assertEquals(1, lFiles.size());
    assertEquals("preview_small.jpg", lFiles.get(0).getName());

    List<String> lFileNames = lFileTools.listFiles(lRootDir.getCanonicalPath());
    Comparator<String> lStringComparator = Comparator.comparing(String::toString);
    lFileNames.sort(lStringComparator);
    assertEquals(8, lFileNames.size());
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "ai", lFileNames.get(0));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "icon_list.txt", lFileNames.get(1));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "icons", lFileNames.get(2));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "license.txt", lFileNames.get(3));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "preview", lFileNames.get(4));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "preview.jpg", lFileNames.get(5));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "preview_small.jpg", lFileNames.get(6));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "readme.txt", lFileNames.get(7));

    lFileNames = lFileTools.listFiles(lRootDir.getCanonicalPath(), null, null);
    lFileNames.sort(lStringComparator);
    assertEquals(8, lFileNames.size());
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "ai", lFileNames.get(0));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "icon_list.txt", lFileNames.get(1));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "icons", lFileNames.get(2));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "license.txt", lFileNames.get(3));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "preview", lFileNames.get(4));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "preview.jpg", lFileNames.get(5));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "preview_small.jpg", lFileNames.get(6));
    assertEquals(lRootDir.getCanonicalPath() + File.separatorChar + "readme.txt", lFileNames.get(7));
    // Cleanup
    lFileTools.tryDeleteRecursive(lWorkingDir, true);

  }

  @Test
  public void testCalculateDirectorySize( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();

    // Prepare directory that should be used during this test.
    File lWorkingDir = new File(DESTINATION_DIRECTORY + "/sizeCalculationTest");
    lFileTools.tryDeleteRecursive(lWorkingDir, true);
    lWorkingDir.mkdirs();

    assertTrue(lWorkingDir.exists(), lWorkingDir.getCanonicalPath() + " does not exist.");

    File lZipFile = new File("./testdata/JEAF_Icons.zip");
    lFileTools.extractZipFile(lZipFile, lWorkingDir, Long.MAX_VALUE);
    final int lTotalExpectedSize = 52512857;
    long lDirectorySize = lFileTools.calculateDirectorySize(lWorkingDir);
    assertEquals(lTotalExpectedSize, lDirectorySize);

    // Delete file and test size calculation again.
    File lFile = new File(lWorkingDir, "JEAF_Icons/preview_small.jpg");
    long lFileSize = lFile.length();
    lFileTools.delete(lFile);
    lDirectorySize = lFileTools.calculateDirectorySize(lWorkingDir);
    assertEquals(lTotalExpectedSize - lFileSize, lDirectorySize);

    // Test exception handling.
    try {
      lFileTools.calculateDirectorySize(null);
      fail("Expecting exception when trying to calculate size of directory 'null'.");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }

    try {
      lFileTools.calculateDirectorySize(new File(lWorkingDir, "JEAF_Icons/preview_small.jpg"));
      fail("Expecting exception when trying to calculate size of file instead of directory.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.CALCULATION_OF_DIR_SIZE_FAILED, e.getErrorCode());
    }
    try {
      lFileTools.calculateDirectorySize(new File(lWorkingDir, "I_do_not_exist"));
      fail("Expecting exception when trying to calculate size of file instead of directory.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.CALCULATION_OF_DIR_SIZE_FAILED, e.getErrorCode());
    }

    // Check handling of restricted directories.
    try {
      File lRestrictedDirectory;
      OperatingSystem lOperatingSystem = XFun.getInfoProvider().getOperatingSystem();
      if (lOperatingSystem == OperatingSystem.WINDOWS) {
        lRestrictedDirectory = new File("C:\\PerfLogs");
      }
      else if (lOperatingSystem == OperatingSystem.LINUX) {
        lRestrictedDirectory = new File("/root");
      }
      else if (lOperatingSystem == OperatingSystem.MAC) {
        lRestrictedDirectory = new File("/System");
      }
      else {
        lRestrictedDirectory = null;
        fail("Unexpected operating system " + lOperatingSystem);
      }
      assertTrue(lRestrictedDirectory.exists(), lRestrictedDirectory.getCanonicalPath() + " does not exist.");
      lFileTools.calculateDirectorySize(lRestrictedDirectory);
      fail("Expecting exception when trying to calculate size of file instead of directory.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.CALCULATION_OF_DIR_SIZE_FAILED, e.getErrorCode());
    }
  }

  @Test
  public void testFileAttributesAcccess( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();
    File lZipFile = new File("./testdata/JEAF_Icons.zip");
    BasicFileAttributes lAttributes = Files.readAttributes(lZipFile.toPath(), BasicFileAttributes.class);
    assertEquals(lAttributes.creationTime().toMillis(), lFileTools.getCreation(lZipFile).getTimeInMillis());
    assertEquals(lAttributes.creationTime().toMillis(), lFileTools.getCreationTime(lZipFile).getTime());

    assertEquals(lAttributes.lastModifiedTime().toMillis(), lFileTools.getLastModification(lZipFile).getTimeInMillis());
    assertEquals(lAttributes.lastModifiedTime().toMillis(), lFileTools.getLastModificationTime(lZipFile).getTime());

    assertEquals(lAttributes.lastAccessTime().toMillis(), lFileTools.getLastAccess(lZipFile).getTimeInMillis());
    assertEquals(lAttributes.lastAccessTime().toMillis(), lFileTools.getLastAccessTime(lZipFile).getTime());

    // Test error handling
    try {
      lFileTools.getLastAccess(new File("./testdata/Not_existing_file.txt"));
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_ACCESS_FILE_ATTRIBUTES, e.getErrorCode());
    }
  }

  @Test
  public void testGetResourceAsStream( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();

    InputStream lInputStream = lFileTools.getResourceAsStream("plain.txt");
    String lResourceContent = lFileTools.getFileContentAsString(lInputStream);

    OperatingSystem lOperatingSystem = XFun.getInfoProvider().getOperatingSystem();
    if (lOperatingSystem == OperatingSystem.WINDOWS) {
      String lFileContentWindows = "This is a simple file with plain text.\r\nSecond line of plain text.";
      assertEquals(lFileContentWindows, lResourceContent);
    }
    else if (lOperatingSystem == OperatingSystem.LINUX) {
      String lFileContentLinux = "This is a simple file with plain text.\nSecond line of plain text.";
      assertEquals(lFileContentLinux, lResourceContent);
    }

    // Load XML file as stream from other jars
    lInputStream = lFileTools.getResourceAsStream("ToolsMessages.xml");
    assertNotNull(lInputStream);

    // Load class file.
    String lClassResourceName = this.getClass().getName().replace('.', '/') + ".class";
    lInputStream = lFileTools.getResourceAsStream(lClassResourceName);
    assertNotNull(lInputStream);

    // Test error handling
    String lResourceName = "Not_existing_resource";
    try {
      lFileTools.getResourceAsStream(lResourceName);
      fail("Expected exception when trying to access resource that is not available in the application's classpath.");
    }
    catch (IOException e) {
      String lExpectedMessage =
          "Resource '" + lResourceName + "' could not be found within the application class path.";
      assertEquals(lExpectedMessage, e.getMessage());
    }

    try {
      lFileTools.getResourceAsStream(null);
      fail("Expected exception when passing null");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testLocateResourceURL( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();

    URL lResourceURL = lFileTools.locateResourceURL("plain.txt");
    assertTrue(lResourceURL.getPath().endsWith("jeaf-tools-impl/target/test-classes/plain.txt"));
    lResourceURL = lFileTools.locateResourceURL("ToolsMessages.xml");
    assertTrue(lResourceURL.getPath().endsWith("/ToolsMessages.xml"));

    // Test error handling
    String lResourceName = "Not_existing_resource";
    try {
      lFileTools.locateResourceURL(lResourceName);
      fail("Expected exception when trying to access resource that is not available in the application's classpath.");
    }
    catch (IOException e) {
      String lExpectedMessage =
          "Resource '" + lResourceName + "' could not be found within the application class path.";
      assertEquals(lExpectedMessage, e.getMessage());
    }

    try {
      lFileTools.locateResourceURL(null);
      fail("Expected exception when passing null");
    }
    catch (IllegalArgumentException e) {
      // Nothing to do.
    }
  }

  @Test
  public void testCreateDirectory( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();
    String lTempDirPath = System.getProperty("java.io.tmpdir");
    File lTempDir = new File(lTempDirPath);
    File lNewDir = new File(lTempDir, "Test01");
    lFileTools.tryDelete(lNewDir);
    assertFalse(lNewDir.exists());
    lFileTools.createDirectory(lNewDir);
    assertTrue(lNewDir.exists());

    // Try to create directory a second time.
    lFileTools.createDirectory(lNewDir);

    lNewDir = new File(lTempDir, "Test02");
    lFileTools.tryDelete(lNewDir);
    assertFalse(lNewDir.exists());
    lFileTools.createDirectory(lNewDir.toPath());
    assertTrue(lNewDir.exists());
    lFileTools.createDirectory(lNewDir.toPath());

    lNewDir = new File(lTempDir, "Test03");
    lFileTools.tryDelete(lNewDir);
    assertFalse(lNewDir.exists());
    lFileTools.createDirectory(lNewDir.getCanonicalPath());
    assertTrue(lNewDir.exists());
    lFileTools.createDirectory(lNewDir.getCanonicalFile());

    File lRestrictedDirectory;
    OperatingSystem lOperatingSystem = XFun.getInfoProvider().getOperatingSystem();
    if (lOperatingSystem == OperatingSystem.WINDOWS) {
      lRestrictedDirectory = new File("C:\\PerfLogs");
    }
    else if (lOperatingSystem == OperatingSystem.LINUX) {
      lRestrictedDirectory = new File("/root");
    }
    else if (lOperatingSystem == OperatingSystem.MAC) {
      lRestrictedDirectory = new File("/System");
    }
    else {
      lRestrictedDirectory = null;
      fail("Unexpected operating system " + lOperatingSystem);
    }
    assertTrue(lRestrictedDirectory.exists(), lRestrictedDirectory.getCanonicalPath() + " does not exist.");
    try {
      lFileTools.createDirectory(new File(lRestrictedDirectory, "dir"));
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_CREATE_DIRECTORY, e.getErrorCode());
    }
  }

  @Test
  public void testLineReading( ) throws IOException {
    FileTools lFileTools = FileTools.getFileTools();
    List<String> lLines = lFileTools.readLines("./testdata/multiple_lines.txt", 0, 2);
    assertEquals("This", lLines.get(0));
    assertEquals("is", lLines.get(1));

    lLines = lFileTools.readLines(new File("./testdata/multiple_lines.txt"), 4, 3);
    assertEquals("with", lLines.get(0));
    assertEquals("multiple", lLines.get(1));
    assertEquals("lines", lLines.get(2));

    FileInputStream lInputStream = new FileInputStream("./testdata/multiple_lines.txt");
    lLines = lFileTools.readLines(lInputStream, 4, 8);
    assertEquals("with", lLines.get(0));
    assertEquals("multiple", lLines.get(1));
    assertEquals("lines", lLines.get(2));

    try {
      lInputStream.available();
      fail();
    }
    catch (IOException e) {
      assertEquals("Stream Closed", e.getMessage());
    }

    // Also test reading multiple lines into a single string.
    String lLinesAsString = lFileTools.readLinesAsString("./testdata/multiple_lines.txt", 0, 2);
    assertEquals("This" + File.separator + "is", lLinesAsString);

    lLinesAsString = lFileTools.readLinesAsString(new File("./testdata/multiple_lines.txt"), 4, 3);
    assertEquals("with" + File.separator + "multiple" + File.separator + "lines", lLinesAsString);

    lInputStream = new FileInputStream("./testdata/multiple_lines.txt");
    lLinesAsString = lFileTools.readLinesAsString(lInputStream, 4, 8);
    assertEquals("with" + File.separator + "multiple" + File.separator + "lines", lLinesAsString);

    try {
      lInputStream.available();
      fail();
    }
    catch (IOException e) {
      assertEquals("Stream Closed", e.getMessage());
    }
  }
}
