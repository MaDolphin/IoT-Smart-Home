package de.montigem.be.util.json;

public class JsonIntValue {

  private int value;
  
  protected JsonIntValue() {}
  
  public JsonIntValue(int value) {
    this.value = value;
  }
  
  public int getValue() {
    return value;
  }
}
