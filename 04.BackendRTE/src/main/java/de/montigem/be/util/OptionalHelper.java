/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.util;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * This class is required as long as JPA does not support to store {@link java.util.Optional}. The
 * workaround is: Store the data type without optional, but provide it as Optional to getter and
 * setter methods. Therefore, a constant for each data type which represents the value being absent,
 * has to be defined. Additionally, Optional<X> getOptionalX(X x) methods exist for each data type.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class OptionalHelper {

  protected OptionalHelper() {
    // intentionally left empty
  }

  public static final int INT_ABSENT_CONSTANT = -1;

  public static final long LONG_ABSENT_CONSTANT = -1L;

  public static final double DOUBLE_ABSENT_CONSTANT = -1.0;

  public static final float FLOAT_ABSENT_CONSTANT = -1.0f;

  public static final String STRING_ABSENT_CONSTANT = "";

  public static final ZonedDateTime DATETIME_ABSENT_CONSTANT = null;

  public static final int ENUM_ABSENT_CONSTANT = -1;

  public static Optional<Integer> getOptionalInt(int i) {
    if (INT_ABSENT_CONSTANT == i) {
      return Optional.empty();
    }
    return Optional.ofNullable(i);
  }

  public static Optional<Long> getOptionalLong(long l) {
    if (LONG_ABSENT_CONSTANT == l) {
      return Optional.empty();
    }
    return Optional.ofNullable(l);
  }

  public static Optional<Double> getOptionalDouble(double d) {
    if (DOUBLE_ABSENT_CONSTANT == d) {
      return Optional.empty();
    }
    return Optional.ofNullable(d);
  }

  public static Optional<Float> getOptionalFloat(float f) {
    if (FLOAT_ABSENT_CONSTANT == f) {
      return Optional.empty();
    }
    return Optional.ofNullable(f);
  }

  public static Optional<String> getOptionalString(String s) {
    if (null == s || STRING_ABSENT_CONSTANT.equals(s)) {
      return Optional.empty();
    }
    return Optional.ofNullable(s);
  }

  public static Optional<ZonedDateTime> getOptionalDate(ZonedDateTime d) {
    return Optional.ofNullable(d);
  }

}
