/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.stream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import com.anaptecs.jeaf.tools.annotations.StreamToolsConfig;
import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.encoding.EncodingTools;
import com.anaptecs.jeaf.tools.api.stream.BufferingMode;
import com.anaptecs.jeaf.tools.api.stream.StreamTools;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.checks.Assert;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;
import com.anaptecs.jeaf.xfun.api.messages.MessageID;
import com.anaptecs.jeaf.xfun.api.trace.Trace;

import stormpot.BlazePool;
import stormpot.Config;
import stormpot.PoolException;
import stormpot.Timeout;

@ToolsImplementation(toolsInterface = StreamTools.class)
public class StreamToolsImpl implements StreamTools {
  /**
   * Size of the buffer that is used. The value can be configured via annotation {@link StreamToolsConfig#bufferSize()}.
   * Default value is {@link StreamToolsConfig#DEFAULT_BUFFER_SIZE}.
   */
  private final int bufferSize;

  /**
   * Stream tools also support to pool buffers. This reduces the produced garbage but requires some memory to be
   * allocated and reserved. Pool size can be configured via annotation {@link StreamToolsConfig#bufferPoolSize()}.
   * Default value is {@link StreamToolsConfig#DEFAULT_BUFFER_POOL_SIZE}.
   */
  private final int bufferPoolSize;

  /**
   * Timeout that is used we requesting a buffer from the pool.
   */
  private static final Timeout TIMEOUT = new Timeout(1, TimeUnit.NANOSECONDS);

  /**
   * Reference to buffer pool. Reference is null if pool is disabled.
   */
  private final BlazePool<PoolableByteArray> bufferPool;

  /**
   * Initialize object.
   */
  public StreamToolsImpl( ) {
    // Use default configuration.
    this(new StreamToolsConfiguration());
  }

  /**
   * Initialize object.
   * 
   * @param pConfiguration Configuration parameters for stream tools. The parameter must not be null.
   */
  public StreamToolsImpl( StreamToolsConfiguration pConfiguration ) {
    // Check parameters
    com.anaptecs.jeaf.xfun.bootstrap.Check.checkInvalidParameterNull(pConfiguration, "pConfiguration");

    // Resolve buffer size.
    bufferSize = pConfiguration.getBufferSize();

    // Create new pool for validating document builders
    bufferPoolSize = pConfiguration.getBufferPoolSize();
    bufferPool = this.createBufferPool(bufferPoolSize, bufferSize);
  }

  /**
   * Method creates a new buffer pool based on the defined pool size.
   * 
   * @return {@link BlazePool} Pool for buffers or null is pooling is disabled.
   */
  private BlazePool<PoolableByteArray> createBufferPool( int pBufferPoolSize, int pBufferSize ) {
    BlazePool<PoolableByteArray> lBufferPool;
    if (pBufferPoolSize > 0) {
      Config<PoolableByteArray> lConfig = new Config<>();
      lConfig.setSize(pBufferPoolSize);
      lConfig.setAllocator(new PoolableByteArrayAllocator(pBufferSize));
      lBufferPool = new BlazePool<>(lConfig);

      // Initialize pool directly.
      PoolableByteArray lBuffer = null;
      try {
        lBuffer = lBufferPool.claim(new Timeout(50, TimeUnit.MILLISECONDS));
      }
      catch (PoolException e) {
        XFun.getTrace().error("Unable to initialize buffer pool with pool size " + pBufferPoolSize + ".");
        XFun.getTrace().error(e.getMessage(), e);
      }
      catch (InterruptedException e) {
        XFun.getTrace().error(e.getMessage(), e);
        Thread.currentThread().interrupt();
      }
      finally {
        if (lBuffer != null) {
          lBuffer.release();
        }
      }
    }
    else {
      lBufferPool = null;
    }
    return lBufferPool;
  }

  /**
   * Method returns a buffer that can be used to transfer data using streams. In order to reduce the amount or garbage
   * that is produced this method tries to acquire a buffer from the pool. In case that the pool if overloaded for some
   * reason then the method will return an unpooled buffer.
   * 
   * @return {@link PoolableByteArray} The requested buffer. The method never returns null.
   */
  private PoolableByteArray getBuffer( ) {
    // Check if a pool for buffers is used.
    PoolableByteArray lBuffer;
    if (bufferPool != null) {
      // Acquire buffer from pool.
      try {
        lBuffer = bufferPool.claim(TIMEOUT);

        // There might be situations where a buffer can not be claimed from pool.
        if (lBuffer == null) {
          lBuffer = new PoolableByteArray(bufferSize, null);
        }
      }
      // Unable to get buffer from pool. In this case we create a new buffer that afterwards will be remove by the
      // Garbage Collector.
      catch (PoolException e) {
        XFun.getTrace().error(
            "Exception when trying to claim object from buffer pool. Working with unpooled object instead. Please check root cause.");
        XFun.getTrace().error(e.getMessage(), e);
        lBuffer = new PoolableByteArray(bufferSize, null);
      }
      catch (InterruptedException e) {
        XFun.getTrace().error(e.getMessage(), e);
        Thread.currentThread().interrupt();
        lBuffer = new PoolableByteArray(bufferSize, null);
      }
    }
    // Buffers are not pooled at all.
    else {
      lBuffer = new PoolableByteArray(bufferSize, null);
    }
    return lBuffer;
  }

  /**
   * Method returns the content of the passed stream.
   * 
   * @param pInputStream Input stream from which the content should be returned. The parameter must not be null.
   * @return byte[] byte Array with the content of the file. The method never returns null. If the file is zero bytes
   * then an empty array with length 0 will be returned.
   * @throws IOException
   */
  @Override
  public byte[] getStreamContent( InputStream pInputStream ) throws IOException {
    // Return stream content.
    return this.getContent(pInputStream);
  }

  /**
   * Method returns the content of the passed stream. The file content will be converted from bytes to String using the
   * configured default encoding.
   * 
   * @param pInputStream Input stream with the content that should be returned. The parameter must not be null.
   * @return {@link String} Content of the stream as string. The method never returns null. If the stream contains zero
   * bytes then an empty String will be returned.
   * @throws IOException
   */
  @Override
  public String getStreamContentAsString( InputStream pInputStream ) throws IOException {
    // Read content from stream and convert it to string.
    byte[] lContent = this.getContent(pInputStream);
    return new String(lContent, EncodingTools.getEncodingTools().getDefaultCharset());
  }

  /**
   * Method copies the content of the input stream to the passed output stream.
   * 
   * @param pStreamName Name of the stream that will be copied. The name is only required for tracing.
   * @param pInputStream Source of the data that should be copied. The parameter must not be null.
   * @param pOutputStream Destination where the data should be copied to. The parameter must not be null.
   * @param pCloseStreams If the parameter is set to true then the streams will be closed in all cases.
   * @return long Number of bytes that where copied.
   * @throws IOException If an exception occurs during copying.
   */
  @Override
  public long copyContent( String pStreamName, InputStream pInputStream, OutputStream pOutputStream,
      boolean pCloseStreams )
    throws IOException {

    // Copy content without limiting the amount of transfered bytes.
    return this.copyContent(pStreamName, pInputStream, pOutputStream, pCloseStreams, Long.MAX_VALUE,
        BufferingMode.AUTO);

  }

  /**
   * Method copies the content of the input stream to the passed output stream.
   * 
   * @param pStreamName Name of the stream that will be copied. The name is only required for tracing.
   * @param pInputStream Source of the data that should be copied. The parameter must not be null.
   * @param pOutputStream Destination where the data should be copied to. The parameter must not be null.
   * @param pCloseStreams If the parameter is set to true then the streams will be closed in all cases.
   * @param pMaxBytes Maximum bytes that should be written. If the streams contains more bytes then the copy process
   * will be aborted.
   * @param pNoBuffering Parameter defines if buffer should used or not. The parameter may be null. In this case
   * {@link BufferingMode#AUTO} will be used.
   * @return long Number of bytes that where copied.
   * @throws IOException If an exception occurs during copying.
   */
  @Override
  public long copyContent( String pStreamName, InputStream pInputStream, OutputStream pOutputStream,
      boolean pCloseStreams, long pMaxBytes )
    throws IOException {

    // Copy content with buffering mode AUTO.
    return this.copyContent(pStreamName, pInputStream, pOutputStream, pCloseStreams, pMaxBytes, BufferingMode.AUTO);
  }

  /**
   * Method copies the content of the input stream to the passed output stream.
   * 
   * @param pStreamName Name of the stream that will be copied. The name is only required for tracing.
   * @param pInputStream Source of the data that should be copied. The parameter must not be null.
   * @param pOutputStream Destination where the data should be copied to. The parameter must not be null.
   * @param pCloseStreams If the parameter is set to true then the streams will be closed in all cases.
   * @param pNoBuffering Parameter defines if buffer should used or not. The parameter may be null. In this case
   * {@link BufferingMode#AUTO} will be used.
   * @return long Number of bytes that where copied.
   * @throws IOException If an exception occurs during copying.
   */
  @Override
  public long copyContent( String pStreamName, InputStream pInputStream, OutputStream pOutputStream,
      boolean pCloseStreams, BufferingMode pBufferingMode )
    throws IOException {

    // Copy content with buffering mode AUTO.
    return this.copyContent(pStreamName, pInputStream, pOutputStream, pCloseStreams, Long.MAX_VALUE, pBufferingMode);
  }

  /**
   * Method copies the content of the input stream to the passed output stream.
   * 
   * @param pStreamName Name of the stream that will be copied. The name is only required for tracing.
   * @param pInputStream Source of the data that should be copied. The parameter must not be null.
   * @param pOutputStream Destination where the data should be copied to. The parameter must not be null.
   * @param pCloseStreams If the parameter is set to true then the streams will be closed in all cases.
   * @param pMaxBytes Maximum bytes that should be written. If the streams contains more bytes then the copy process
   * will be aborted.
   * @param pNoBuffering Parameter defines if buffer should used or not. The parameter may be null. In this case
   * {@link BufferingMode#AUTO} will be used.
   * @return long Number of bytes that where copied.
   * @throws IOException If an exception occurs during copying.
   */
  @Override
  public long copyContent( String pStreamName, InputStream pInputStream, OutputStream pOutputStream,
      boolean pCloseStreams, long pMaxBytes, BufferingMode pBufferingMode )
    throws IOException {

    // Check parameters.
    Check.checkInvalidParameterNull(pInputStream, "pInputStream");
    Check.checkInvalidParameterNull(pOutputStream, "pOutputStream");

    // Use buffered streams if needed.
    InputStream lBufferedInputStream;
    if (this.requiresBuffering(pInputStream, pBufferingMode) == true) {
      lBufferedInputStream = new BufferedInputStream(pInputStream, bufferSize);
      lBufferedInputStream = pInputStream;
    }
    else {
      lBufferedInputStream = pInputStream;
    }
    OutputStream lBufferedOutputStream;
    if (this.requiresBuffering(pOutputStream, pBufferingMode) == true) {
      lBufferedOutputStream = new BufferedOutputStream(pOutputStream, bufferSize);
    }
    else {
      lBufferedOutputStream = pOutputStream;
    }

    // Copy content.
    long lStart = System.nanoTime();
    PoolableByteArray lPoolableBuffer = null;
    try {
      lPoolableBuffer = this.getBuffer();
      byte[] lBuffer = lPoolableBuffer.getByteArray();
      int lLength;
      long lByteCount = 0;
      while ((lLength = lBufferedInputStream.read(lBuffer)) != -1) {
        lBufferedOutputStream.write(lBuffer, 0, lLength);
        lByteCount += lLength;

        // Check if maximum bytes that can be transferred are exceeded.
        if (lByteCount > pMaxBytes) {
          throw new JEAFSystemException(ToolsMessages.MAX_BYTES_EXCEEDED, Long.toString(pMaxBytes));
        }
      }
      // Data transfer successful.
      lBufferedOutputStream.flush();

      // Trace info about copy.
      long lEnd = System.nanoTime();
      this.traceIOSummary(pStreamName, lByteCount, lEnd - lStart, ToolsMessages.TRANSFERED_FILE_CONTENT);

      return lByteCount;
    }
    // If requested we also have to close the passed streams.
    finally {
      if (lPoolableBuffer != null) {
        lPoolableBuffer.release();
      }
      if (pCloseStreams == true) {
        try {
          lBufferedInputStream.close();
        }
        finally {
          lBufferedOutputStream.close();
        }
      }
    }
  }

  /**
   * Method returns the content of the passed input stream.
   * 
   * @param pInputStream Stream to access the content. The parameter must not be null.
   * @return byte[] Available content of the stream. The method never returns null.
   */
  private byte[] getContent( InputStream pInputStream ) throws IOException {
    // Check parameter
    Check.checkInvalidParameterNull(pInputStream, "pInputStream");
    int lAvailableBytes = pInputStream.available();
    ByteArrayOutputStream lBytes = new ByteArrayOutputStream(lAvailableBytes);

    // Read as much bytes as possible into the buffer.
    int lBytesRead;
    PoolableByteArray lPoolableBuffer = null;
    try {
      lPoolableBuffer = this.getBuffer();
      byte[] lBuffer = lPoolableBuffer.getByteArray();
      while ((lBytesRead = pInputStream.read(lBuffer, 0, lBuffer.length)) != -1) {
        lBytes.write(lBuffer, 0, lBytesRead);
      }
      return lBytes.toByteArray();
    }
    finally {
      if (lPoolableBuffer != null) {
        lPoolableBuffer.release();
      }
    }
  }

  /**
   * Method traces information about a copy process.
   * 
   * @param pFileName Name of the file that was copied.
   * @param pByteCount Number of bytes that were copied.
   * @param pDuration Duration of the copy process in nanoseconds.
   */
  @Override
  public void traceIOSummary( String pFileName, long pByteCount, long pDuration, MessageID pMessageID ) {
    // Check parameters.
    Assert.assertNotNull(pFileName, "pFileName");

    // Check if trace level is enabled.
    if (pMessageID.isEnabled() == true) {
      // Calculate duration in milliseconds.
      Trace lTrace = XFun.getTrace();
      double lDurationInMillis = ((double) pDuration) / (1000 * 1000);
      String lDurationString;
      if (lDurationInMillis < 1) {
        lDurationString = new DecimalFormat("#.###").format(lDurationInMillis);
      }
      else if (lDurationInMillis < 10) {
        lDurationString = new DecimalFormat("#.#").format(lDurationInMillis);
      }
      else {
        lDurationString = new DecimalFormat("#").format(lDurationInMillis);
      }

      // Calculate transfer rate.
      DecimalFormat lTwoDigits = new DecimalFormat("#.##");
      double lFileSizeMB = ((double) pByteCount) / (1024 * 1024);
      double lTransferrate = (lFileSizeMB / lDurationInMillis) * 1000;
      String lTransferrateString = lTwoDigits.format(lTransferrate);

      // Calculate file size in a human readable way.
      // Unit is bytes as the file is smaller than 1kB.
      String llFileSizeString;
      if (pByteCount < 1024) {
        llFileSizeString = pByteCount + " Bytes";
      }
      // Unit is kB as the file is smaller than 1 MB
      else if (pByteCount < (1024 * 1024)) {
        double lKilobytes = ((double) pByteCount) / 1024;
        llFileSizeString = lTwoDigits.format(lKilobytes) + " KB";
      }
      // Unit is MB.
      else {
        double lMegabytes = ((double) pByteCount) / (1024 * 1024);
        llFileSizeString = lTwoDigits.format(lMegabytes) + " MB";
      }

      lTrace.write(pMessageID, pFileName, lDurationString, llFileSizeString, lTransferrateString);
    }
  }

  /**
   * Method checks if it makes sense to use buffered streams to be used in combination with the passed input stream.
   * 
   * @param pInputStream Input stream for which should be checked if buffering will improve performance. The parameter
   * must not be null.
   * @param pBufferingMode The passed buffering mode also is taken into account when the check is done. The parameter
   * may be null.
   * @return boolean Method returns true if usage of buffered streams will improve performance and false otherwise.
   */
  private boolean requiresBuffering( InputStream pInputStream, BufferingMode pBufferingMode ) {
    // Use AUTO buffering mode if buffering is not defined.
    if (pBufferingMode == null) {
      pBufferingMode = BufferingMode.AUTO;
    }

    // Always use buffers
    boolean lRequiresBuffering;
    if (pBufferingMode == BufferingMode.ALWAYS) {
      lRequiresBuffering = true;
    }
    // Never use buffers
    else if (pBufferingMode == BufferingMode.NO_BUFFERING) {
      lRequiresBuffering = false;
    }
    // Use buffers in case that steam is not already buffered
    else if (pBufferingMode == BufferingMode.AUTO) {
      // BufferedInputStream and ByteArrayInputStream do not require any further buffering.
      if (pInputStream instanceof BufferedInputStream || pInputStream instanceof ByteArrayInputStream) {
        lRequiresBuffering = false;
      }
      else {
        lRequiresBuffering = true;
      }
    }
    // Unexpected buffering mode.
    else {
      Assert.unexpectedEnumLiteral(pBufferingMode);
      lRequiresBuffering = false;
    }
    return lRequiresBuffering;
  }

  /**
   * Method checks if it makes sense to use buffered streams to be used in combination with the passed input stream.
   * 
   * @param pOutputStream Output stream for which should be checked if buffering will improve performance. The parameter
   * must not be null.
   * @param pBufferingMode The passed buffering mode also is taken into account when the check is done. The parameter
   * may be null.
   * @return boolean Method returns true if usage of buffered streams will improve performance and false otherwise.
   */
  private boolean requiresBuffering( OutputStream pOutputStream, BufferingMode pBufferingMode ) {
    // Use AUTO buffering mode if buffering is not defined.
    if (pBufferingMode == null) {
      pBufferingMode = BufferingMode.AUTO;
    }

    // Always use buffers
    boolean lRequiresBuffering;
    if (pBufferingMode == BufferingMode.ALWAYS) {
      lRequiresBuffering = true;
    }
    // Never use buffers
    else if (pBufferingMode == BufferingMode.NO_BUFFERING) {
      lRequiresBuffering = false;
    }
    // Use buffers in case that steam is not already buffered
    else if (pBufferingMode == BufferingMode.AUTO) {
      // BufferedOutputStream and ByteArrayOutputStream do not require any further buffering
      if (pOutputStream instanceof BufferedOutputStream || pOutputStream instanceof ByteArrayOutputStream) {
        lRequiresBuffering = false;
      }
      else {
        lRequiresBuffering = true;
      }
    }
    // Unexpected buffering mode.
    else {
      Assert.unexpectedEnumLiteral(pBufferingMode);
      lRequiresBuffering = false;
    }
    return lRequiresBuffering;
  }
}
