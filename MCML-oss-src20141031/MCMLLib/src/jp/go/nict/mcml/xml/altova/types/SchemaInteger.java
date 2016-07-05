/**
 * SchemaInteger.java
 *
 * This file was generated by XMLSPY 2004 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSPY Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package jp.go.nict.mcml.xml.altova.types;

import java.math.BigInteger;
import java.math.BigDecimal;

public class SchemaInteger implements SchemaTypeNumber {
  protected BigInteger value;
  protected boolean isempty;

  // construction
  public SchemaInteger() {
    value = BigInteger.valueOf(0);
    isempty = true;
  }

  public SchemaInteger(SchemaInteger newvalue) {
    value = newvalue.value;
    isempty = newvalue.isempty;
  }

  public SchemaInteger(BigInteger newvalue) {
    setValue( newvalue );
  }

  public SchemaInteger(long newvalue) {
    setValue( newvalue );
  }

  public SchemaInteger(String newvalue) {
    parse( newvalue );
  }

  public SchemaInteger(SchemaType newvalue) {
    assign(newvalue);
  }

  public SchemaInteger(SchemaTypeNumber newvalue) {
    assign( (SchemaType)newvalue );
  }

  // getValue, setValue
  public BigInteger getValue() {
    return value;
  }

  public void setValue(BigInteger newvalue) {
    if( newvalue == null ) {
      isempty = true;
      value = BigInteger.valueOf(0);
      return;
    }
    value = newvalue;
    isempty = false;
  }

  public void setValue(long newvalue) {
    value = BigInteger.valueOf(newvalue);
    isempty = false;
  }

  public void parse(String newvalue) {
    if( newvalue == null  ||  newvalue == "" ) {
      isempty = true;
      value = BigInteger.valueOf(0);
      return;
    }
    try {
      value = new BigInteger(newvalue);
      isempty = false;
    } catch( NumberFormatException e ) {
      throw new StringParseException(e);
    }
  }

  public void assign(SchemaType newvalue) {
    if( newvalue == null || newvalue.isEmpty() ) {
      isempty = true;
      value = BigInteger.valueOf(0);
      return;
    }
    if( newvalue instanceof SchemaTypeNumber ) {
      value = ( (SchemaTypeNumber) newvalue).bigIntegerValue();
      isempty = false;
    }
    else
      throw new TypesIncompatibleException( newvalue, this );
  }

  // further
  public int hashCode() {
    return value.hashCode();
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof SchemaInteger))
      return false;
    return value.equals( ( (SchemaInteger) obj).value);
  }

  public Object clone() {
    return new SchemaInteger(value.toString());
  }

  public String toString() {
    if( isempty )
      return "";
    return value.toString();
  }

  public int length() {
    return toString().length();
  }

  public boolean booleanValue() {
    return value.compareTo(BigInteger.valueOf(0)) != 0;
  }

  public boolean isEmpty() {
    return isempty;
  }

  public int compareTo(Object obj) {
    return compareTo( (SchemaInteger) obj);
  }

  public int compareTo(SchemaInteger obj) {
    return value.compareTo(obj.value);
  }

  // interface SchemaTypeNumber
  public int numericType() {
    return NUMERIC_VALUE_BIGINTEGER;
  }

  public int intValue() {
    return value.intValue();
  }

  public long longValue() {
    return value.longValue();
  }

  public BigInteger bigIntegerValue() {
    return value;
  }

  public float floatValue() {
    return value.floatValue();
  }

  public double doubleValue() {
    return value.doubleValue();
  }

  public BigDecimal bigDecimalValue() {
    try {
      return new BigDecimal(value.toString());
    } catch( NumberFormatException e) {
      throw new ValuesNotConvertableException(this, new SchemaDecimal( 0 ) );
    }
  }
}