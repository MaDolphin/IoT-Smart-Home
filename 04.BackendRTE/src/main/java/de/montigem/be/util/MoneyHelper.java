/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.util;

import de.montigem.be.config.Config;
import org.javamoney.moneta.FastMoney;

import javax.money.MonetaryAmount;

/**
 * TODO: Write me!
 *
 */
public class MoneyHelper {
  
  protected MoneyHelper() {
    // intentionally left empty
  }
  
  public static double toDouble(MonetaryAmount ma) {
    return ma.getNumber().doubleValue();
  }
  
  public static MonetaryAmount toMonetaryAmount(double d, String currencyCode) {
    return FastMoney.of(d, currencyCode);
  }
  
  public static MonetaryAmount toMonetaryAmount(double d) {
    return toMonetaryAmount(d, Config.DEFAULT_CURRENCY);
  }
  
}
