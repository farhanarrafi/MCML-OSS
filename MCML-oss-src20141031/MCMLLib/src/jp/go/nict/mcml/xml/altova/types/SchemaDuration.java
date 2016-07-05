/**
 * SchemaDuration.java
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

import java.lang.*;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.ParseException;


public class SchemaDuration extends SchemaCalendarBase {
  protected boolean bNegative;

  // construction
  public SchemaDuration() {
    super();
    bNegative = false;
  }

  public SchemaDuration(SchemaDuration newvalue) {
    year = newvalue.year;
    month = newvalue.month;
    day = newvalue.day;
    hour = newvalue.hour;
    minute = newvalue.minute;
    second = newvalue.second;
    partsecond = newvalue.partsecond;
    hasTZ = newvalue.hasTZ;
    offsetTZ = newvalue.offsetTZ;
    isempty = newvalue.isempty;
  }

  public SchemaDuration(int newyear, int newmonth, int newday, int newhour, int newminute, int newsecond, double newpartsecond, boolean newisnegative) {
    setYear(newyear);
    setMonth(newmonth);
    setDay(newday);
    setHour(newhour);
    setMinute(newminute);
    setSecond(newsecond);
    setPartSecond(newpartsecond);
    bNegative = newisnegative;
  }

  public SchemaDuration(String newvalue) {
    parse(newvalue);
  }

  public SchemaDuration(SchemaType newvalue) {
    assign( newvalue );
  }

  public SchemaDuration(SchemaTypeCalendar newvalue) {
    assign( (SchemaType)newvalue );
  }

  // setValue, getValue
  public int getYear() {
    return year;
  }

  public int getMonth() {
    return month;
  }

  public int getDay() {
    return day;
  }

  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return minute;
  }

  public int getSecond() {
    return second;
  }

  public double getPartSecond() {
    return partsecond;
  }

  public int getMillisecond() {
    return (int)java.lang.Math.round(partsecond*1000.0);
  }

  public boolean isNegative() {
    return bNegative;
  }

  public void setYear(int newyear) {
    if (newyear < 0) {
      year = -newyear;
      bNegative = true;
    }
    else
      year = newyear;
    isempty = false;
  }

  public void setMonth(int newmonth) {
    if (newmonth < 0) {
      month = -newmonth;
      bNegative = true;
    }
    else
      month = newmonth;
    isempty = false;
  }

  public void setDay(int newday) {
    if (newday < 0) {
      day = -newday;
      bNegative = true;
    }
    else
      day = newday;
    isempty = false;
  }

  public void setHour(int newhour) {
    if (newhour < 0) {
      hour = -newhour;
      bNegative = true;
    }
    else
      hour = newhour;
    isempty = false;
  }

  public void setMinute(int newminute) {
    if (newminute < 0) {
      minute = -newminute;
      bNegative = true;
    }
    else
      minute = newminute;
    isempty = false;
  }

  public void setSecond(int newsecond) {
    if (newsecond < 0) {
      second = -newsecond;
      bNegative = true;
    }
    else
      second = newsecond;
    isempty = false;
  }

  public void setPartSecond(double newpartsecond) {
    if (newpartsecond < 0) {
      partsecond = -newpartsecond;
      bNegative = true;
    }
    else
      partsecond = newpartsecond;
    isempty = false;
  }

  public void setMillisecond(int newmillisecond) {
    int normalizedMSec = newmillisecond;
    // must normallize, because 0 <= partseconds < 1
    if (normalizedMSec < 0) {
      int neededSeconds = newmillisecond / 1000 + 1;
      normalizedMSec = neededSeconds * 1000 + newmillisecond;
      if (!bNegative)
        second -= neededSeconds;
      else
        second += neededSeconds - 1;
    }
    if (normalizedMSec >= 1000) {
      int overflowSeconds = normalizedMSec / 1000;
      normalizedMSec = normalizedMSec % 1000;
      if (!bNegative)
        second += overflowSeconds;
      else
        second -= overflowSeconds;
    }
    partsecond = (double) normalizedMSec / 1000;
    isempty = false;
  }

  public void setNegative(boolean newisnegative) {
    bNegative = newisnegative;
    isempty = false;
  }


  public void assign(SchemaType newvalue) {
    if( newvalue == null || newvalue.isEmpty() ) {
      setInternalValues( 0,0,0, 0,0,0, 0.0,false,0 );
      isempty = true;
      return;
    }
    if (newvalue instanceof SchemaDuration) {
      setInternalValues( ( (SchemaDuration) newvalue).year,
                        ( (SchemaDuration) newvalue).month,
                        ( (SchemaDuration) newvalue).day,
                        ( (SchemaDuration) newvalue).hour,
                        ( (SchemaDuration) newvalue).minute,
                        ( (SchemaDuration) newvalue).second,
                        ( (SchemaDuration) newvalue).partsecond, false, 0);
      bNegative = ( (SchemaDuration) newvalue).bNegative;
    }
    else if (newvalue instanceof SchemaString)
      parse(newvalue.toString());
    else
      throw new TypesIncompatibleException(newvalue, this);
  }

  // further
  public Object clone() {
    return new SchemaDuration( this );
  }

  public String toString() {
    if (isempty)
      return "";
    StringBuffer s = new StringBuffer();
    if (bNegative)
      s.append("-");
    s.append("P");
    if (year != 0) {
      s.append(new DecimalFormat("0").format( (long) year));
      s.append("Y");
    }
    if (month != 0) {
      s.append(new DecimalFormat("0").format( (long) month));
      s.append("M");
    }
    if (day != 0) {
      s.append(new DecimalFormat("0").format( (long) day));
      s.append("D");
    }
	if (hour!=0 || minute!=0 || second!=0 || partsecond>0 )	{
    	s.append("T");
	    if (hour != 0) {
    		s.append(new DecimalFormat("0").format( (long) hour));
	    	s.append("H");
    	}
	    if (minute != 0) {
    		s.append(new DecimalFormat("0").format( (long) minute));
	    	s.append("M");
    	}
	    if (second != 0)
    		s.append(new DecimalFormat("0").format( (long) second));
	    if (partsecond > 0 && partsecond < 1) {
    		String sPartSecond = new DecimalFormat("0.0###############").format(partsecond);
	    	s.append(".");
    		s.append(sPartSecond.substring(2, sPartSecond.length()));
	    }
	    if (second != 0 || (partsecond > 0 && partsecond < 1))
    		s.append("S");
	}
    return s.toString();
  }

  public boolean booleanValue() {
    return true;
  }

  protected void parse(String newvalue) {
    int nStart = newvalue.indexOf("P");
    if (nStart < 0)
      throw new StringParseException("P expected", 0);
    if (nStart > 0 &&
        newvalue.substring(nStart - 1, nStart).compareTo("-") == 0)
      bNegative = true;
    else
      bNegative = false;
    int nEnd = newvalue.indexOf("Y", nStart + 1);
    int nLastEnd = nEnd;
    int nTPos = newvalue.indexOf("T", nStart + 1);
    nLastEnd = (nTPos > nLastEnd ? nTPos : nLastEnd);
    try {
      if (nEnd > nStart) {
        year = Integer.parseInt(newvalue.substring(nStart + 1, nEnd));
        nStart = nEnd;
      }
      else
        year = 0;

      nEnd = newvalue.indexOf("M", nStart + 1);
      nLastEnd = (nEnd > nLastEnd ? nEnd : nLastEnd);
      if ( (nEnd > nStart) && (nTPos == -1 || nEnd < nTPos)) {
        month = Integer.parseInt(newvalue.substring(nStart + 1, nEnd));
        nStart = nEnd;
      }
      else
        month = 0;

      nEnd = newvalue.indexOf("D", nStart + 1);
      nLastEnd = (nEnd > nLastEnd ? nEnd : nLastEnd);
      if (nEnd > nStart) {
        day = Integer.parseInt(newvalue.substring(nStart + 1, nEnd));
        nStart = nEnd;
      }
      else
        day = 0;

      if (nTPos > -1) {
        nStart = nTPos;
        nEnd = newvalue.indexOf("H", nStart + 1);
        nLastEnd = (nEnd > nLastEnd ? nEnd : nLastEnd);
        if (nEnd > nStart) {
          hour = Integer.parseInt(newvalue.substring(nStart + 1, nEnd));
          nStart = nEnd;
        }
        else
          hour = 0;

        nEnd = newvalue.indexOf("M", nStart + 1);
        nLastEnd = (nEnd > nLastEnd ? nEnd : nLastEnd);
        if (nEnd > nStart) {
          minute = Integer.parseInt(newvalue.substring(nStart + 1, nEnd));
          nStart = nEnd;
        }
        else
          minute = 0;

        second = 0;
        partsecond = 0;
        int nComma = newvalue.indexOf(".", nStart + 1);
        nEnd = newvalue.indexOf("S", nStart + 1);
        nLastEnd = (nEnd > nLastEnd ? nEnd : nLastEnd);
        if (nComma == -1 && nEnd > nStart)
          second = Integer.parseInt(newvalue.substring(nStart + 1, nEnd));
        else if (nComma > nStart && nEnd > nComma) {
          second = Integer.parseInt(newvalue.substring(nStart + 1, nComma));
          partsecond = Double.parseDouble("0." + newvalue.substring(nComma + 1, nEnd));
        }
      }
      else
        nLastEnd = (nLastEnd>-1 ? nLastEnd : 0);
    }
    catch (NumberFormatException e) {
      throw new StringParseException("invalid duration format", 2);
    }
    if ((nLastEnd+1) < newvalue.length())
      throw new StringParseException( "Invalid characters after the duration string", 2);
    if (year < 0 || month < 0 || day < 0 || hour < 0 || minute < 0 || second < 0 || partsecond < 0)
      throw new StringParseException(
          "no negative values allowed in parts. Use '-' before 'P'.", 3);
    isempty = false;
  }

  // ---------- interface SchemaTypeCalendar ----------
  public int calendarType() {
    return CALENDAR_VALUE_DURATION;
  }

  public SchemaDuration durationValue() {
    return new SchemaDuration( this );
  }

  public SchemaDateTime dateTimeValue() {
    throw new TypesIncompatibleException( this, new SchemaDateTime( "2003-07-28T12:00:00" ) );
  }

  public SchemaDate dateValue() {
    throw new TypesIncompatibleException( this, new SchemaDate( "2003-07-28" ) );
  }

  public SchemaTime timeValue() {
    throw new TypesIncompatibleException( this, new SchemaTime( "12:00:00" ) );
  }
}