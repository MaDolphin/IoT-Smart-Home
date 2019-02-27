/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package backend.data.test;

import java.util.HashMap;

public class TestClassManager {
  private static HashMap<String, String> testConfiguration;

  public static HashMap<String, String> getTestConfiguration() {
    if(null==testConfiguration){
      testConfiguration = new HashMap<>();
      testConfiguration.put("DataTest","GenTest");
      testConfiguration.put("ProxyTest","ProxyGenTest");
      testConfiguration.put("ValidatorTest","ValidatorGenTest");
      testConfiguration.put("BuilderTest","BuilderGenTest");
    }
    return testConfiguration;
  }
}
