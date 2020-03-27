/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UsageList {
  public List<String> strings;

  private UsageList(List<String> strings) {
    this.strings = strings;
  }

  public static UsageList all() {
    return new UsageList(Collections.singletonList("All"));
  }

  public boolean contains(String usage) {
    return strings.contains(usage);
  }

  public static UsageList valueOf(String listOfStrings) {
    return new UsageList(Stream.of(listOfStrings.split(","))
        .map(String::new)
        .collect(Collectors.toList()));
  }
}
