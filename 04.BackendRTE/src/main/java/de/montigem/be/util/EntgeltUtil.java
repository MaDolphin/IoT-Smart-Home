/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntgeltUtil {

  public final static Map<String, Integer> entgeldGruppen = new HashMap<String, Integer>() {{
    put("TV-L 15Ü", 18);
    put("TV-L 15", 17);
    put("TV-L 14", 16);
    put("TV-L 13Ü", 15);
    put("TV-L 13", 14);
    put("TV-L 12", 13);
    put("TV-L 11", 12);
    put("TV-L 10", 11);
    put("TV-L 9", 10);
    put("TV-L 8", 9);
    put("TV-L 7", 8);
    put("TV-L 6", 7);
    put("TV-L 5", 6);
    put("TV-L 4", 5);
    put("TV-L 3", 4);
    put("TV-L 2Ü", 3);
    put("TV-L 2", 2);
    put("TV-L 1", 1);
  }};

  public final static Map<String, String> entgeltGruppenHiwi = new HashMap<String, String>() {{
    put("SHK", "Studentische Hilfskraft ohne Abschluss (SHK)");
    put("WHF", "Studentische Hilfskraft mit Bachelor (WHF)");
    put("WHK", "Wissenschaftliche Hilfskraft (WHK)");
  }};

  public final static List<Integer> entgeltStufen = new ArrayList<Integer>() {{
    add(1);
    add(2);
    add(3);
    add(4);
    add(5);
    add(6);
  }};

  private final static double[][] entgelt = {
      { -1, 2310.00, 2340.00, 2390.00, 2440.00, 2550.00 },
      { 2560.00, 2820.00, 2890.00, 2970.00, 3140.00, 3330.00 },
      { 2650.00, 2910.00, 3010.00, 3130.00, 3210.00, 3280.00 },
      { 2760.00, 3040.00, 3110.00, 3240.00, 3330.00, 3420.00 },
      { 2800.00, 3080.00, 3270.00, 3380.00, 3490.00, 3560.00 },
      { 2940.00, 3230.00, 3380.00, 3520.00, 3640.00, 3720.00 },
      { 3060.00, 3360.00, 3520.00, 3670.00, 3770.00, 3880.00 },
      { 3110.00, 3430.00, 3630.00, 3780.00, 3910.00, 4010.00 },
      { 3310.00, 3650.00, 3800.00, 3940.00, 4100.00, 4200.00 },
      { 3520.00, 3880.00, 4070.00, 4560.00, 4980.00, 5050.00 },
      { 3960.00, 4360.00, 4680.00, 5010.00, 5630.00, 5710.00 },
      { 4100.00, 4510.00, 4840.00, 5330.00, 6050.00, 6140.00 },
      { 4240.00, 4680.00, 5330.00, 5910.00, 6650.00, 6740.00 },
      { 4710.00, 5220.00, 5500.00, 6040.00, 6790.00, 6890.00 },
      { -1, 5220.00, 5500.00, 6480.00, 7230.00, 7340.00 },
      { 5100.00, 5660.00, 5990.00, 6480.00, 7230.00, 7340.00 },
      { 5640.00, 6250.00, 6480.00, 7300.00, 7920.00, 8040.00 },
      { 7090.00, 7870.00, 8610.00, 9090.00, 9210.00, -1 } };

  private final static Map<String, Double> entgeltHiwi = new HashMap<String, Double>() {{
    put("SHK", 52.491);
    put("WHF", 62.883);
    put("WHK", 88.575);
  }};

  public static double getEntgelt(String gruppe, int stufe) {
    if (entgeldGruppen.containsKey(gruppe) && entgeltStufen.contains(stufe)) {
      return entgelt[entgeldGruppen.get(stufe) - 1][entgeltStufen.get(stufe) - 1];
    } else {
      return -1;
    }
  }

  public static double getEntgeldHiwi(String gruppeHiwi) {
    if (entgeltHiwi.containsKey(gruppeHiwi)) {
      return entgeltHiwi.get(gruppeHiwi);
    } else {
      return -1;
    }
  }

}
