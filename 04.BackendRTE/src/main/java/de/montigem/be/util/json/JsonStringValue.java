package de.montigem.be.util.json;

public class JsonStringValue {

  private String value;
  
  protected JsonStringValue() {}
  
  public JsonStringValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
