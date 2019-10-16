/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.rte.validator;

import java.time.*;

public class ValidatorUtils {


  public static boolean isDateInRange(ZonedDateTime date, int minusYears, int plusYears) {
    return ZonedDateTime.now().minusYears(minusYears).isBefore(date) && date.isBefore(ZonedDateTime.now().plusYears(plusYears));
  }

  public static boolean isDateAfterRWTHFounding(ZonedDateTime date) {
    ZonedDateTime rwthFounding = ZonedDateTime.of(1870, 10, 10, 0, 0, 0, 0, ZoneId.of("GMT+2"));
    return rwthFounding.isBefore(date);
  }

}
