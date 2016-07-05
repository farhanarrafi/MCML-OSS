/**
 * SchemaByte.java
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

public class SchemaByte implements SchemaTypeNumber {
  protected byte value;
  protected boolean isempty;

  // construction
  public SchemaByte() {
    value = 0;
    isempty = true;
  }

  public SchemaByte(SchemaByte newvalue) {
    value = newvalue.value;
    isempty = newvalue.isempty;
  }

  public SchemaByte(int newvalue) {
    setValue(newvalue);
  }

  public SchemaByte(String newvalue) {
    parse(newvalue);
  }

  public SchemaByte(SchemaType newvalue) {
    assign(newvalue);
  }

  public SchemaByte(SchemaTypeNumber newvalue) {
    assign( (SchemaType)newvalue );
  }

  // getValue, setValue
  public byte getValue() {
    return value;
  }

  public void setValue(int newvalue) {
    value = (byte) newvalue;
    isempty = false;
  }

  public void parse(String newvalue) {
    if( newvalue == null  ||  newvalue == "" ) {
      isempty = true;
      value = 0;
    }
    else {
      isempty = false;
      value = Byte.parseByte(newvalue);
    }
  }

  public void assign(SchemaType newvalue) {
    if( newvalue == null || newvalue.isEmpty() ) {
      isempty = true;
      value = 0;
      return;
    }
    isempty = false;
    if( newvalue instanceof SchemaTypeNumber )
      value = (byte)((SchemaTypeNumber)newvalue).intValue();
    else
      throw new TypesIncompatibleException( newvalue, this );
  }

  // further
  public int hashCode() {
    return value;
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof SchemaByte))
      return false;
    return value == ( (SchemaByte) obj).value;
  }

  public Object clone() {
    return new SchemaByte(value);
  }

  public String toString() {
    if( isempty )
      return "";
    return Byte.toString(value);
  }

  public int length() {
    return toString().length();
  }

  public boolean booleanValue() {
    return value != 0;
  }

  public boolean isEmpty() {
    return isempty;
  }

  public int compareTo(Object obj) {
    return compareTo( (SchemaByte) obj);
  }

  public int compareTo(SchemaByte obj) {
    return new Byte(value).compareTo(new Byte(obj.value));
  }

  // interface SchemaTypeNumber
  public int numericType() {
    return NUMERIC_VALUE_INT;
  }

  public int intValue() {
    return value;
  }

  public long longValue() {
    return value;
  }

  public BigInteger bigIntegerValue() {
    return BigInteger.valueOf(value);
  }

  public float floatValue() {
    return value;
  }

  public double doubleValue() {
    return value;
  }

  public BigDecimal bigDecimalValue() {
    return BigDecimal.valueOf(value);
  }

}
