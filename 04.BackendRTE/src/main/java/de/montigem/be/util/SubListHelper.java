/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package de.montigem.be.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is a helper class providing methods to manage parts of lists with reasonable defaults
 * applied in order to avoid unnecessary exceptions
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class SubListHelper {
  
  protected SubListHelper() {
    // intentionally left empty
  }
  
  /**
   * Returns a sublist of the given list, without throwing exceptions. If first or max are negative,
   * the entire list is returned
   *
   * @param list
   * @param first
   * @param max
   * @return
   */
  public static <E> List<E> get(Collection<E> list, int first, int max) {
    List<E> input = new ArrayList<E>();
    input.addAll(list);
    List<E> result = new ArrayList<E>();
    int last = first + max;
    if (first >= 0 && first < input.size() && max > 0) {
      result = input.subList(first, last > input.size() ? input.size() : last);
    } else if (first < 0 || max < 0) {
      return input;
    }
    return result;
  }
  
}
