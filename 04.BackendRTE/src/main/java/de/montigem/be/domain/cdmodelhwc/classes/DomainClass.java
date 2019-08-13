/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.domain.cdmodelhwc.classes;

import com.google.common.base.CaseFormat;

public interface DomainClass {

  /*default String getHumanName() {
    return getClass().getName();
  }*/

  static String getHumanNameForAttribute(String attributeName) {
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, attributeName.toLowerCase());
  }

  long getPermissionId();

  String getPermissionClass();
}
