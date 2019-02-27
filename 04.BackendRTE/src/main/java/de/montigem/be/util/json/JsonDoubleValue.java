/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.util.json;

public class JsonDoubleValue {

  private double value;
  
  protected JsonDoubleValue() {}
  
  public JsonDoubleValue(double value) {
    this.value = value;
  }
  
  public double getValue() {
    return value;
  }
}
