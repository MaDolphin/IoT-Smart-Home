/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.util.json;

public class JsonBooleanValue {

  private boolean value;
  
  protected JsonBooleanValue() {}
  
  public JsonBooleanValue(boolean value) {
    this.value = value;
  }

  public boolean getValue() {
    return value;
  }
}
