/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package de.montigem.be.marshalling;

/**
 * This class manages a singleton instance of an {@link IConcreteJsonMarshal}. It can be used to
 * conveniently exchange the default {@link GsonMarshal} with another instance to marshal and
 * unmarshal from/to JSON.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonMarshal {
  
  /**
   * static Singleton instance
   */
  private static IConcreteJsonMarshal INSTANCE;
  
  /**
   * Private constructor for singleton
   */
  private JsonMarshal() {
  }
  
  public static IConcreteJsonMarshal getDefaultSerializer() {
    return new GsonMarshal();
  }
  
  /**
   * Static getter method for retrieving the singleton instance
   */
  public static IConcreteJsonMarshal getInstance() {
    if (INSTANCE == null) {
      INSTANCE = getDefaultSerializer();
    }
    return INSTANCE;
  }
  
  /**
   * Static getter method for retrieving the singleton instance
   */
  public static void setInstance(IConcreteJsonMarshal serializer) {
    INSTANCE = serializer;
  }
  
}
