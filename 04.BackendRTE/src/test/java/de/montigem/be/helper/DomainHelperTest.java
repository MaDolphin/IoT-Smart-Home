/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.helper;

import de.montigem.be.util.DomainHelper;
import de.se_rwth.commons.Joiners;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static de.montigem.be.util.DomainHelper.getDateDifference;
import static org.junit.Assert.assertEquals;

public class DomainHelperTest {
  
  @Test
  public void testGetDateDifference() {
    
    // case 1: 01.01.2017 - 31.12.2017 date difference = 12
    assertEquals(12.0, getDateDifference(ZonedDateTime
        .of(2017, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")), ZonedDateTime
            .of(2017, 12, 31, 0, 0, 0, 0, ZoneId.of("Z"))),
        0);
    
    // case 2: 15.01.2017 - 27.02.2017 date difference = 1.4
    assertEquals(1.4, getDateDifference(ZonedDateTime
        .of(2017, 1, 15, 0, 0, 0, 0, ZoneId.of("Z")), ZonedDateTime
            .of(2017, 2, 27, 0, 0, 0, 0, ZoneId.of("Z"))),
        0);
    
    // case 3: 27.01.2017 - 15.02.2017 date difference = 0.7
    assertEquals(0.7, getDateDifference(ZonedDateTime
        .of(2017, 1, 27, 0, 0, 0, 0, ZoneId.of("Z")), ZonedDateTime
            .of(2017, 2, 15, 0, 0, 0, 0, ZoneId.of("Z"))),
        0);
    
    // case 4: 27.01.2017 - 15.03.2017 date difference = 1.6
    assertEquals(1.6, getDateDifference(ZonedDateTime
        .of(2017, 1, 27, 0, 0, 0, 0, ZoneId.of("Z")), ZonedDateTime
            .of(2017, 3, 15, 0, 0, 0, 0, ZoneId.of("Z"))),
        0);
    
    // case 5: 01.01.2017 - 28.02.2017 date difference = 2.0
    assertEquals(2.0, getDateDifference(ZonedDateTime
        .of(2017, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")), ZonedDateTime
            .of(2017, 2, 28, 0, 0, 0, 0, ZoneId.of("Z"))),
        0);
    
    // case 6: 01.01.2016 - 29.02.2016 date difference = 2.0 in case of leap
    // year
    assertEquals(2.0, getDateDifference(ZonedDateTime
        .of(2016, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")), ZonedDateTime
            .of(2016, 2, 29, 0, 0, 0, 0, ZoneId.of("Z"))),
        0);
    
  }

  @Test
  public void testDat2String() {
    ZonedDateTime time = ZonedDateTime
        .of(2017, 11, 21, 3, 4, 5, 0, ZoneId.of("Z"));
    assertEquals(DomainHelper.date2String(time),
        Joiners.DOT.join(time.getDayOfMonth(), time.getMonthValue(), time.getYear()));
  }
  
}
