/*
 * anaptecs GmbH, Ricarda-Huch-Str. 71, 72760 Reutlingen, Germany
 * 
 * Copyright 2004 - 2019. All rights reserved.
 */
package com.anaptecs.jeaf.tools.test.impl.validation;

import java.util.Calendar;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.anaptecs.jeaf.tools.api.Tools;
import com.anaptecs.jeaf.xfun.api.checks.Check;

/**
 * @author JEAF Generator
 * @version JEAF Release 1.4.x
 */
public class ValidationTestObject {
  @Email
  @NotNull
  private String myEMail;

  @Past
  private Calendar dateOfBirth;

  @SuppressWarnings("unused")
  private boolean notValidated;

  @Valid
  private TestClass testClass;

  @Valid
  @NotNull
  private TestClass class2;

  /**
   * Initialize object using the passed builder.
   * 
   * @param pBuilder Builder that should be used to initialize this object. The parameter must not be null.
   */
  protected ValidationTestObject( Builder pBuilder ) {
    // Ensure that builder is not null.
    Check.checkInvalidParameterNull(pBuilder, "pBuilder");
    // Read attribute values from builder.
    myEMail = pBuilder.myEMail;
    dateOfBirth = pBuilder.dateOfBirth;
  }

  /**
   * Class implements builder to create a new instance of class ValidationTestObject. As the class has readonly
   * attributes or associations instances can not be created directly. Instead this builder class has to be used.
   */
  public static class Builder {
    /**
     * 
     */
    @Email
    @NotNull
    private String myEMail;

    /**
     * 
     */
    @Past
    private Calendar dateOfBirth;

    /**
     * Use {@link #newBuilder()} instead of private constructor to create new builder.
     */
    protected Builder( ) {
    }

    /**
     * Use {@link #newBuilder(ValidationTestObject)} instead of private constructor to create new builder.
     */
    protected Builder( ValidationTestObject pObject ) {
      if (pObject != null) {
        // Read attribute values from passed object.
        myEMail = pObject.myEMail;
        dateOfBirth = pObject.dateOfBirth;
      }
    }

    /**
     * Method returns a new builder.
     * 
     * @return {@link Builder} New builder that can be used to create new ImmutablePOJOParent objects.
     */
    public static Builder newBuilder( ) {
      return new Builder();
    }

    /**
     * Method creates a new builder and initialize it with the data from the passed object.
     * 
     * @param pObject Object that should be used to initialize the builder. The parameter may be null.
     * @return {@link Builder} New builder that can be used to create new ValidationTestObject objects. The method never
     * returns null.
     */
    public static Builder newBuilder( ValidationTestObject pObject ) {
      return new Builder(pObject);
    }

    /**
     * Method sets the attribute "myEMail".
     * 
     * @param pMyEMail Value to which the attribute "myEMail" should be set.
     */
    public Builder setMyEMail( String pMyEMail ) {
      // Assign value to attribute
      myEMail = pMyEMail;
      return this;
    }

    /**
     * Method sets the attribute "dateOfBirth".
     * 
     * @param pDateOfBirth Value to which the attribute "dateOfBirth" should be set.
     */
    public Builder setDateOfBirth( Calendar pDateOfBirth ) {
      // Assign value to attribute
      dateOfBirth = pDateOfBirth;
      return this;
    }

    /**
     * Method creates a new instance of class ValidationTestObject. The object will be initialized with the values of
     * the builder.
     * 
     * @return ValidationTestObject Created object. The method never returns null.
     */
    public ValidationTestObject build( ) {
      return new ValidationTestObject(this);
    }

    /**
     * Method creates a new instance of class ValidationTestObject. The object will be initialized with the values of
     * the builder.
     * 
     * @param pValidate Parameter defines if the created POJO should be validated using Java Validation.
     * @return ValidationTestObject Created object. The method never returns null.
     */
    public ValidationTestObject build( boolean pValidate ) {
      ValidationTestObject lPOJO = this.build();
      if (pValidate == true) {
        Tools.getValidationTools().validateObject(lPOJO);
      }
      return lPOJO;
    }
  }

  /**
   * Method returns the attribute "myEMail".
   * 
   * 
   * @return String Value to which the attribute "myEMail" is set.
   */
  public String getMyEMail( ) {
    return myEMail;
  }

  /**
   * Method sets the attribute "myEMail".
   * 
   * 
   * @param pMyEMail Value to which the attribute "myEMail" should be set.
   */
  public void setMyEMail( String pMyEMail ) {
    // Assign value to attribute
    myEMail = pMyEMail;
  }

  /**
   * Method returns the attribute "dateOfBirth".
   * 
   * 
   * @return Calendar Value to which the attribute "dateOfBirth" is set.
   */
  public Calendar getDateOfBirth( ) {
    return dateOfBirth;
  }

  /**
   * Method sets the attribute "dateOfBirth".
   * 
   * 
   * @param pDateOfBirth Value to which the attribute "dateOfBirth" should be set.
   */
  public void setDateOfBirth( Calendar pDateOfBirth ) {
    // Assign value to attribute
    dateOfBirth = pDateOfBirth;
  }

  public TestClass getTestClass( ) {
    return testClass;
  }

  public void setTestClass( TestClass pTestClass ) {
    testClass = pTestClass;
  }

  public TestClass getClass2( ) {
    return class2;
  }

  public void setClass2( TestClass pClass2 ) {
    class2 = pClass2;
  }

}
