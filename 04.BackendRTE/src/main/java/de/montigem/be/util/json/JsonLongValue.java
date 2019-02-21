package de.montigem.be.util.json;

public class JsonLongValue {

  private long value;
  
  protected JsonLongValue() {}
  
  public JsonLongValue(long value) {
    this.value = value;
  }
  
  public long getValue() {
    return value;
  }
}
