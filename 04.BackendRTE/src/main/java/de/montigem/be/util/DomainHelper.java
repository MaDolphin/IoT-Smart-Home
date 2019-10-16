/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class DomainHelper {

  public static final int aktuellesJahr = Calendar.getInstance().get(Calendar.YEAR);
  
  public static <T> List<T> getList(Collection<T> c) {
    return new ArrayList<T>(c);
  }
  
  public static <T> Set<T> getSet(Collection<T> c) {
    return new HashSet<T>(c);
  }
  
  public static double getDateDifference(ZonedDateTime startDate, ZonedDateTime endDate) {
    LocalDate start = LocalDate.of(startDate.getYear(), startDate.getMonthValue(),
        startDate.getDayOfMonth());
    LocalDate end = LocalDate.of(endDate.getYear(), endDate.getMonthValue(),
        endDate.getDayOfMonth());
    
    Period period = Period.between(start, end);
    double days = period.getDays() + 1;
    double duration = period.toTotalMonths();
    
    if (start.getDayOfMonth() == 1 && isEndOfMonth(end)) {
      duration++;
    }
    else {
      duration += days / 30.5;
    }
    return duration >= 0 ? Math.round(duration * 10) / 10.0 : 0.0;
  }
  
  public static boolean isEndOfMonth(LocalDate endDate) {
    if (endDate.getMonthValue() != 2 || endDate.isLeapYear()) {
      return endDate.getDayOfMonth() == endDate.getMonth().maxLength();
    }
    return endDate.getDayOfMonth() == endDate.getMonth().maxLength() - 1;
  }

  public static boolean isDateBetween(ZonedDateTime date, ZonedDateTime start, ZonedDateTime end) {
    return date.isAfter(start) && date.isBefore(end);
  }

  public static boolean isDateBetweenOrStart(ZonedDateTime date, ZonedDateTime start, ZonedDateTime end) {
    return !date.isBefore(start) && date.isBefore(end);
  }

  public static boolean isDateBetweenOrEnd(ZonedDateTime date, ZonedDateTime start, ZonedDateTime end) {
    return date.isAfter(start) && !date.isAfter(end);
  }

  public static boolean isDateBetweenOrStartOrEnd(ZonedDateTime date, ZonedDateTime start, ZonedDateTime end) {
    return !date.isBefore(start) && !date.isAfter(end);
  }

  public static boolean isDateBeforeNow(ZonedDateTime date1, int byAtLeast) {
    return isDateBefore(date1, ZonedDateTime.now(), byAtLeast);
  }

  public static boolean isDateBefore(ZonedDateTime date1, ZonedDateTime date2, int byAtLeast) {
    long duration = ChronoUnit.DAYS.between(date1, date2);
    return duration >= 0 && duration < byAtLeast;
  }

  public static int getYearFromString(String year) {
    try {
      return Integer.parseInt(year);
    } catch (NumberFormatException e) {
      return aktuellesJahr;
    }
  }

  public static String getErrors(Optional<String> errors) {
    return errors.isPresent()? errors.get(): "";
  }

  public static String date2String(ZonedDateTime date) {
    return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(date);
  }

  public static ZonedDateTime date(int y, int m, int d) {
    return ZonedDateTime.of(y, m, d, 0, 0, 0, 0, ZoneId.of("Z"));
  }

}
