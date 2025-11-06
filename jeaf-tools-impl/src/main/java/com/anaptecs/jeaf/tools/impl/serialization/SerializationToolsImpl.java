/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.serialization.SerializationTools;
import com.anaptecs.jeaf.xfun.api.checks.Check;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

/**
 * Class implements method to make to serialization and deserialization of objects as easy as possible.
 * 
 * @author JEAF Development Team
 * @version JEAF Release 1.2
 */
@ToolsImplementation(toolsInterface = SerializationTools.class)
public class SerializationToolsImpl implements SerializationTools {

  /**
   * Method serializes the passed object to the returned byte[].
   * 
   * @param pObject Object that should be serialized. The parameter may be null.
   * @return byte[] Byte array representing the serialized object. The method returns null if the parameter is null.
   */
  public byte[] serializeObject( Serializable pObject ) {
    byte[] lBytes;
    if (pObject != null) {
      try {
        // Create and open streams for serialization.
        ByteArrayOutputStream lByteOutputStream = new ByteArrayOutputStream();
        ObjectOutput lOut = new ObjectOutputStream(lByteOutputStream);

        // Write object to stream and get its bytes.
        lOut.writeObject(pObject);
        lBytes = lByteOutputStream.toByteArray();

        // Close streams again.
        lOut.close();
        lByteOutputStream.close();
      }
      // Catch IOException and rethrow it as system exception.
      catch (IOException e) {
        throw new JEAFSystemException(ToolsMessages.UNABLE_TO_SERIALIZE_OBJECT, e, pObject.getClass().getName());
      }
    }
    // Passed object is null.
    else {
      lBytes = null;
    }
    // Returned serialized object.
    return lBytes;
  }

  /**
   * Method deserializes the passed byte[] to an object of the passed type.
   * 
   * @param <T>
   * @param pBytes Byte array that should be used to deserialize the object. The parameter may be null.
   * @param pType Class of the expected object to be returned. The parameter must not be null.
   * @return T Object that was deserialized from the passed bytes. The method returns null if null was passed.
   */
  @SuppressWarnings("unchecked")
  public <T extends Serializable> T deserializeObject( byte[] pBytes, Class<T> pType ) {
    // Check parameter
    Check.checkInvalidParameterNull(pType, "pType");

    T lObject;
    if (pBytes != null) {
      try {
        ByteArrayInputStream lByteInputStream = new ByteArrayInputStream(pBytes);
        ObjectInputStream lInputStream = new ObjectInputStream(lByteInputStream);
        lObject = (T) lInputStream.readObject();

        lByteInputStream.close();
        lInputStream.close();
      }
      catch (ClassNotFoundException | IOException e) {
        throw new JEAFSystemException(ToolsMessages.UNABLE_TO_DESERIALIZE_OBJECT, e, pType.getName());
      }
    }
    // Byte array is null.
    else {
      lObject = null;
    }
    return lObject;
  }
}
