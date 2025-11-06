/**
 * Copyright 2004 - 2021 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.serialization;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.tools.api.ToolsMessages;
import com.anaptecs.jeaf.tools.api.serialization.SerializationTools;
import com.anaptecs.jeaf.xfun.api.errorhandling.JEAFSystemException;

public class SerializationToolsTest {
  static byte[] OBJECT = new byte[] { -84, -19, 0, 5, 115, 114, 0, 49, 99, 111, 109, 46, 97, 110, 97, 112, 116, 101, 99,
    115, 46, 106, 101, 97, 102, 46, 116, 111, 111, 108, 115, 46, 116, 101, 115, 116, 46, 105, 109, 112, 108, 46, 115,
    101, 114, 105, 97, 108, 105, 122, 97, 116, 105, 111, 110, 46, 65, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 2, 73, 0, 2, 105,
    100, 76, 0, 2, 98, 115, 116, 0, 16, 76, 106, 97, 118, 97, 47, 117, 116, 105, 108, 47, 76, 105, 115, 116, 59, 120,
    112, 0, 0, 0, 1, 115, 114, 0, 19, 106, 97, 118, 97, 46, 117, 116, 105, 108, 46, 65, 114, 114, 97, 121, 76, 105, 115,
    116, 120, -127, -46, 29, -103, -57, 97, -99, 3, 0, 1, 73, 0, 4, 115, 105, 122, 101, 120, 112, 0, 0, 0, 2, 119, 4, 0,
    0, 0, 2, 115, 114, 0, 49, 99, 111, 109, 46, 97, 110, 97, 112, 116, 101, 99, 115, 46, 106, 101, 97, 102, 46, 116,
    111, 111, 108, 115, 46, 116, 101, 115, 116, 46, 105, 109, 112, 108, 46, 115, 101, 114, 105, 97, 108, 105, 122, 97,
    116, 105, 111, 110, 46, 66, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 2, 76, 0, 2, 97, 115, 113, 0, 126, 0, 1, 76, 0, 4, 110,
    97, 109, 101, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 120,
    112, 115, 113, 0, 126, 0, 3, 0, 0, 0, 1, 119, 4, 0, 0, 0, 1, 113, 0, 126, 0, 2, 120, 116, 0, 2, 66, 49, 115, 113, 0,
    126, 0, 5, 115, 113, 0, 126, 0, 3, 0, 0, 0, 1, 119, 4, 0, 0, 0, 1, 113, 0, 126, 0, 2, 120, 116, 0, 2, 66, 50, 120 };

  @Test
  public void testSerialization( ) {
    A lA1 = new A();
    lA1.id = 1;

    B lB1 = new B();
    lB1.name = "B1";
    lB1.as.add(lA1);
    lA1.bs.add(lB1);

    B lB2 = new B();
    lB2.name = "B2";
    lB2.as.add(lA1);
    lA1.bs.add(lB2);

    SerializationTools lSerializationTools = Tools.getSerializationTools();

    // Try to serialize objects
    byte[] lBytes = lSerializationTools.serializeObject(lA1);
    assertNotNull(lBytes);

    A lDeserializeObject = lSerializationTools.deserializeObject(lBytes, A.class);
    assertEquals(lA1.id, lDeserializeObject.id);
    assertEquals("B1", lDeserializeObject.bs.get(0).name);
    assertEquals("B2", lDeserializeObject.bs.get(1).name);

    assertEquals(lDeserializeObject, lDeserializeObject.bs.get(0).as.get(0));
    assertEquals(lDeserializeObject, lDeserializeObject.bs.get(1).as.get(0));

    lDeserializeObject = lSerializationTools.deserializeObject(OBJECT, A.class);
    assertEquals(lA1.id, lDeserializeObject.id);
    assertEquals("B1", lDeserializeObject.bs.get(0).name);
    assertEquals("B2", lDeserializeObject.bs.get(1).name);

    assertEquals(lDeserializeObject, lDeserializeObject.bs.get(0).as.get(0));
    assertEquals(lDeserializeObject, lDeserializeObject.bs.get(1).as.get(0));

    // Test exception handling
    lBytes = lSerializationTools.serializeObject(null);
    assertNull(lBytes);

    lDeserializeObject = lSerializationTools.deserializeObject(null, A.class);
    assertNull(lDeserializeObject);

    TroubleMaker lTroubleMaker = new TroubleMaker();
    lTroubleMaker.writeTroubleIO = true;

    try {
      lSerializationTools.serializeObject(lTroubleMaker);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_SERIALIZE_OBJECT, e.getErrorCode());
      assertEquals("Causing trouble on write.", e.getCause().getMessage());
      assertEquals(IOException.class, e.getCause().getClass());
    }

    lTroubleMaker = new TroubleMaker();
    lTroubleMaker.readTroubleIO = true;
    lBytes = lSerializationTools.serializeObject(lTroubleMaker);

    try {
      lSerializationTools.deserializeObject(lBytes, A.class);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_DESERIALIZE_OBJECT, e.getErrorCode());
      assertEquals("Causing trouble on read.", e.getCause().getMessage());
      assertEquals(IOException.class, e.getCause().getClass());
    }

    lTroubleMaker = new TroubleMaker();
    lTroubleMaker.readTroubleCNF = true;
    lBytes = lSerializationTools.serializeObject(lTroubleMaker);

    try {
      lSerializationTools.deserializeObject(lBytes, A.class);
      fail("Exception expected.");
    }
    catch (JEAFSystemException e) {
      assertEquals(ToolsMessages.UNABLE_TO_DESERIALIZE_OBJECT, e.getErrorCode());
      assertEquals("Causing trouble on read.", e.getCause().getMessage());
      assertEquals(ClassNotFoundException.class, e.getCause().getClass());
    }
  }
}
