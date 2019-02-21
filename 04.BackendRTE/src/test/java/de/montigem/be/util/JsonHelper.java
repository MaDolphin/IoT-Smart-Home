package de.montigem.be.util;

import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.json.JsonBooleanValue;
import de.montigem.be.util.json.JsonDoubleValue;
import de.montigem.be.util.json.JsonIntValue;
import de.montigem.be.util.json.JsonLongValue;
import de.montigem.be.util.json.JsonStringValue;

public class JsonHelper {

  public static String longToJson(long l) {
    return JsonMarshal.getInstance().marshal(new JsonLongValue(l));
  }
  
  public static String intToJson(int i) {
    return JsonMarshal.getInstance().marshal(new JsonIntValue(i));
  }
  
  public static String stringToJson(String s) {
    return JsonMarshal.getInstance().marshal(new JsonStringValue(s));
  }
  
  public static String booleanToJson(boolean b) {
    return JsonMarshal.getInstance().marshal(new JsonBooleanValue(b));
  }
  
  public static String doubleToJson(double d) {
    return JsonMarshal.getInstance().marshal(new JsonDoubleValue(d));
  }
}
