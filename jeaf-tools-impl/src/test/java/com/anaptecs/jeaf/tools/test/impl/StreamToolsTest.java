/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.tools.annotations.StreamToolsConfig;
import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.file.FileTools;
import com.anaptecs.jeaf.tools.api.performance.Stopwatch;
import com.anaptecs.jeaf.tools.api.performance.TimePrecision;
import com.anaptecs.jeaf.tools.api.stream.BufferingMode;
import com.anaptecs.jeaf.tools.api.stream.StreamTools;
import com.anaptecs.jeaf.tools.impl.stream.PoolableByteArray;
import com.anaptecs.jeaf.tools.impl.stream.PoolableByteArrayAllocator;
import com.anaptecs.jeaf.tools.impl.stream.StreamToolsConfiguration;
import com.anaptecs.jeaf.tools.impl.stream.StreamToolsImpl;
import com.anaptecs.jeaf.tools.test.impl.stream.InputStreamChunker;

import stormpot.Poolable;
import stormpot.Slot;

@StreamToolsConfig(bufferSize = 4096, bufferPoolSize = 5)
public class StreamToolsTest {
  @Test
  public void testStreamToolsConfiguration( ) {
    // Test default configuration
    StreamToolsConfiguration lConfiguration = new StreamToolsConfiguration();
    assertEquals(8192, lConfiguration.getBufferSize());
    assertEquals(20, lConfiguration.getBufferPoolSize());

    // Test custom configuration
    lConfiguration = new StreamToolsConfiguration("MyStreamTools", "META-INF", true);
    assertEquals(4096, lConfiguration.getBufferSize());
    assertEquals(5, lConfiguration.getBufferPoolSize());

    // Test empty configuration
    StreamToolsConfig lEmptyConfiguration = lConfiguration.getEmptyConfiguration();
    assertEquals(StreamToolsConfig.class, lEmptyConfiguration.annotationType());
    assertEquals(StreamToolsConfig.DEFAULT_BUFFER_SIZE, lEmptyConfiguration.bufferSize());
    assertEquals(StreamToolsConfig.DEFAULT_BUFFER_POOL_SIZE, lEmptyConfiguration.bufferPoolSize());

    List<String> lErrors = lConfiguration.checkCustomConfiguration(lEmptyConfiguration);
    assertEquals(null, lErrors);

    // Test invalid configuration
    lConfiguration = new StreamToolsConfiguration("InvalidStreamToolsConfig", "META-INF", true);
    assertEquals(0, lConfiguration.getBufferSize());
    lErrors =
        lConfiguration.checkCustomConfiguration(InvalidStreamToolsConfig.class.getAnnotation(StreamToolsConfig.class));
    assertEquals(1, lErrors.size());
    assertEquals("Illegal value for 'bufferSize'. Configured value is 0", lErrors.get(0));

    // Test configuration with disabled buffer pool.
    lConfiguration = new StreamToolsConfiguration("NoBufferPoolingConfig", "META-INF", true);
    assertEquals(0, lConfiguration.getBufferPoolSize());
  }

  @Test
  public void testChunkedStream( ) throws IOException {
    String lFileContent = FileTools.getFileTools().getFileContentAsString("testdata/jeaf-test-dataset.xml");

    // Read file content with a stream that chunks the content.
    StreamTools lStreamTools = Tools.getStreamTools();

    // Test in case with available smaller than chunk size
    InputStream lChunkedInputStream =
        new InputStreamChunker(new FileInputStream("testdata/jeaf-test-dataset.xml"), 1024, 512);
    String lChunkedContent = lStreamTools.getStreamContentAsString(lChunkedInputStream);
    assertEquals(lFileContent.length(), lChunkedContent.length());
    assertEquals(lFileContent, lChunkedContent);

    // Test in case with available greater than chunk size
    lChunkedInputStream = new InputStreamChunker(new FileInputStream("testdata/jeaf-test-dataset.xml"), 1024, 1050);
    lChunkedContent = lStreamTools.getStreamContentAsString(lChunkedInputStream);
    assertEquals(lFileContent.length(), lChunkedContent.length());
    assertEquals(lFileContent, lChunkedContent);

    // Test copying to chunked streams.
    lChunkedInputStream = new InputStreamChunker(new FileInputStream("testdata/jeaf-test-dataset.xml"), 1024, 512);
    File lTempFile = File.createTempFile("jeaf-tools-test", "tmp");
    FileOutputStream lFileOutputStream = new FileOutputStream(lTempFile);
    lStreamTools.copyContent(lTempFile.getCanonicalPath(), lChunkedInputStream, lFileOutputStream, true);
    assertEquals(lFileContent, FileTools.getFileTools().getFileContentAsString(lTempFile));

    // Test in case with available greater than chunk size
    lChunkedInputStream = new InputStreamChunker(new FileInputStream("testdata/jeaf-test-dataset.xml"), 1024, 2048);
    lTempFile = File.createTempFile("jeaf-tools-test", "tmp");
    lFileOutputStream = new FileOutputStream(lTempFile);
    lStreamTools.copyContent(lTempFile.getCanonicalPath(), lChunkedInputStream, lFileOutputStream, true);
    assertEquals(lFileContent, FileTools.getFileTools().getFileContentAsString(lTempFile));
  }

  @Test
  public void testStreamBufferingFeatures( ) throws IOException {
    StreamToolsConfiguration lConfiguration = new StreamToolsConfiguration("NoBufferPoolingConfig", "META-INF", true);
    StreamToolsImpl lStreamToolsImpl = new StreamToolsImpl(lConfiguration);

    // Test different buffering modes.
    byte[] lFileContent = FileTools.getFileTools().getFileContent("testdata/jeaf-test-dataset.xml");
    ByteArrayOutputStream lOutputStream = new ByteArrayOutputStream();
    lStreamToolsImpl.copyContent("Test", new ByteArrayInputStream(lFileContent), lOutputStream, true,
        BufferingMode.AUTO);
    assertEquals(true, Arrays.equals(lFileContent, lOutputStream.toByteArray()));

    lOutputStream = new ByteArrayOutputStream();
    lStreamToolsImpl.copyContent("Test", new BufferedInputStream(new FileInputStream("testdata/jeaf-test-dataset.xml")),
        lOutputStream, true, BufferingMode.AUTO);
    assertEquals(true, Arrays.equals(lFileContent, lOutputStream.toByteArray()));

    lOutputStream = new ByteArrayOutputStream();
    lStreamToolsImpl.copyContent("Test", new FileInputStream("testdata/jeaf-test-dataset.xml"), lOutputStream, true,
        BufferingMode.ALWAYS);
    assertEquals(true, Arrays.equals(lFileContent, lOutputStream.toByteArray()));

    lOutputStream = new ByteArrayOutputStream();
    lStreamToolsImpl.copyContent("Test", new FileInputStream("testdata/jeaf-test-dataset.xml"), lOutputStream, true,
        BufferingMode.NO_BUFFERING);
    assertEquals(true, Arrays.equals(lFileContent, lOutputStream.toByteArray()));

    lOutputStream = new ByteArrayOutputStream();
    lStreamToolsImpl.copyContent("Test", new FileInputStream("testdata/jeaf-test-dataset.xml"), lOutputStream, true,
        null);
    assertEquals(true, Arrays.equals(lFileContent, lOutputStream.toByteArray()));

  }

  @Test
  @Disabled
  public void testStreamPerformance( ) throws IOException {
    int lLoops = 10000000;
    // Test performance of buffered streams.
    byte[] lFileContent = FileTools.getFileTools().getFileContent("testdata/jeaf-test-dataset.xml");
    Stopwatch lStopwatch;

    // Run performance test for buffered streams
    StreamToolsImpl lBufferedStreamTools = new StreamToolsImpl();
    lStopwatch = Tools.getPerformanceTools().createStopwatch("Buffered streams", TimePrecision.NANOS).start();
    for (int i = 0; i < lLoops; i++) {
      ByteArrayInputStream lInputStream = new ByteArrayInputStream(lFileContent);
      ByteArrayOutputStream lOutputStream = new ByteArrayOutputStream(lFileContent.length);
      lBufferedStreamTools.copyContent("Buffered streams", lInputStream, lOutputStream, true);
    }
    lStopwatch.stopAndTrace(lLoops);

    // Run performance test for unbuffered streams
    StreamToolsConfiguration lConfiguration = new StreamToolsConfiguration("NoBufferPoolingConfig", "META-INF", true);
    StreamToolsImpl lUnbufferedStreamTools = new StreamToolsImpl(lConfiguration);
    lStopwatch = Tools.getPerformanceTools().createStopwatch("Not buffered streams", TimePrecision.NANOS).start();
    for (int i = 0; i < lLoops; i++) {
      ByteArrayInputStream lInputStream = new ByteArrayInputStream(lFileContent);
      ByteArrayOutputStream lOutputStream = new ByteArrayOutputStream(lFileContent.length);
      lUnbufferedStreamTools.copyContent("Not Buffered streams", lInputStream, lOutputStream, true);
    }
    lStopwatch.stopAndTrace(lLoops);

  }

  @Test
  public void testPoolableByteArray( ) {
    // Test standard creation
    MyTestSlotImpl lSlot = new MyTestSlotImpl();
    int lSize = 1024;
    PoolableByteArray lPoolableByteArray = new PoolableByteArray(lSize, lSlot);
    assertEquals(false, lSlot.released);
    assertEquals(false, lSlot.expired);

    // Check if byte array is empty.
    byte[] lEmptyByteArray = new byte[lSize];
    byte[] lByteArray = lPoolableByteArray.getByteArray();
    assertEquals(lSize, lByteArray.length);
    assertEquals(true, Arrays.equals(lEmptyByteArray, lByteArray));

    // Write data to byte array
    new Random().nextBytes(lByteArray);
    assertEquals(false, Arrays.equals(lEmptyByteArray, lByteArray));

    // Simulate returning the byte array to the pool.
    lPoolableByteArray.release();
    assertEquals(true, lSlot.released);
    assertEquals(false, lSlot.expired);
    assertEquals(true, Arrays.equals(lEmptyByteArray, lByteArray));

    // Test creation without slot
    lSize = 227;
    lPoolableByteArray = new PoolableByteArray(lSize, null);
    lEmptyByteArray = new byte[lSize];
    lByteArray = lPoolableByteArray.getByteArray();
    assertEquals(lSize, lByteArray.length);
    assertEquals(true, Arrays.equals(lEmptyByteArray, lByteArray));

    new Random().nextBytes(lByteArray);
    assertEquals(false, Arrays.equals(lEmptyByteArray, lByteArray));

    // Simulate returning the byte array to the pool.
    lPoolableByteArray.release();
    assertEquals(true, Arrays.equals(lEmptyByteArray, lByteArray));
  }

  @Test
  public void testPoolableByteArrayAllocator( ) throws Exception {
    MyTestSlotImpl lSlot = new MyTestSlotImpl();
    PoolableByteArrayAllocator lAllocator = new PoolableByteArrayAllocator(512);
    PoolableByteArray lPooledObject = lAllocator.allocate(lSlot);

    assertEquals(512, lPooledObject.getByteArray().length);
    lPooledObject.release();
    assertEquals(true, lSlot.released);
    assertEquals(false, lSlot.expired);

    // Call deallocation method just for coverage reasons. The implementation itself does not contain any implementation
    lAllocator.deallocate(lPooledObject);
  }
}

class MyTestSlotImpl implements Slot {
  public boolean released = false;

  public boolean expired = false;

  @Override
  public void release( Poolable pObject ) {
    released = true;
  }

  @Override
  public void expire( Poolable pObject ) {
    expired = true;
  }

}

@StreamToolsConfig(bufferSize = 0)
interface InvalidStreamToolsConfig {
}

@StreamToolsConfig(bufferPoolSize = 0)
interface NoBufferPoolingConfig {
}
