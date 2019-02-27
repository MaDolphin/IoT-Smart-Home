/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package common.util;

public class JsonUtil {

  private JsonUtil() {
    throw new IllegalStateException("JsonUtil");
  }
  
  public final static String JSON_BOOLEAN = "JsonBooleanValue";
  
  public final static String JSON_STRING = "JsonStringValue";
  
  public final static String JSON_INT = "JsonIntValue";
  
  public final static String JSON_DOUBLE = "JsonDoubleValue";
  
  public final static String JSON_LONG = "JsonLongValue";
  
  public static String getJsonClass(String type) {
    if ("boolean".equals(type)) {
      return JSON_BOOLEAN;
    }
    else if ("long".equals(type)) {
      return JSON_STRING;
    }
    else if ("int".equals(type)) {
      return JSON_STRING;
    }
    else if ("double".equals(type)) {
      return JSON_STRING;
    }
    return type;
  }
  
  public static String getJsonClassOfOptional(String type) {
    type = type.substring(type.indexOf("<") + 1);
    type = type.substring(0, type.length() - 1);
    return type;
  }
  
  public static String getDecoder(String type) {
    if ("long".equals(type)) {
      return "Long.parseLong";
    }
    else if ("int".equals(type)) {
      return "Integer.parseInt";
    }
    else if ("double".equals(type)) {
      return "Double.parseDouble";
    }
    return "";
  }
  
}
