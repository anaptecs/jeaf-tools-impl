/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.stream;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamChunker extends InputStream {
  private final InputStream theStream;

  private final int chunkSize;

  private final int maxAvailable;

  public InputStreamChunker( InputStream pInputStream, int pChunkSize, int pMaxAvailable ) {
    super();
    theStream = pInputStream;
    chunkSize = pChunkSize;
    maxAvailable = pMaxAvailable;
  }

  @Override
  public int read( ) throws IOException {
    return theStream.read();
  }

  @Override
  public int read( byte[] pBytes ) throws IOException {
    return theStream.read(pBytes);
  }

  @Override
  public int read( byte[] pBytes, int pOffset, int pLength ) throws IOException {
    // Do not read more bytes than the defined chunk size.
    int lNewLength = Math.min(chunkSize, pLength);
    return theStream.read(pBytes, pOffset, lNewLength);
  }

  @Override
  public long skip( long pN ) throws IOException {
    return theStream.skip(pN);
  }

  @Override
  public int available( ) throws IOException {
    return Math.min(maxAvailable, theStream.available());
  }

  @Override
  public void close( ) throws IOException {
    theStream.close();
  }

  @Override
  public synchronized void mark( int pReadlimit ) {
    theStream.mark(pReadlimit);
  }

  @Override
  public synchronized void reset( ) throws IOException {
    theStream.reset();
  }

  @Override
  public boolean markSupported( ) {
    return theStream.markSupported();
  }
}
