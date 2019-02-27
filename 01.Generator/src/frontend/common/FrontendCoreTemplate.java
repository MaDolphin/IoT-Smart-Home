/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package frontend.common;

import common.DexCoreTemplate;

/**
 * Keep track of all templates that are necessary for generating code.
 */
public enum FrontendCoreTemplate implements DexCoreTemplate {

  EMPTY_METHOD("backend.coretemplates.EmptyMethod"),

  CLASS_ATTRIBUTE_VALUE("frontend.coretemplates.AttributeValue"),

  CONSTRUCTOR(EMPTY_METHOD.toString()),

  CLASS_ATTRIBUTE("frontend.coretemplates.Attribute"),

  CLASS("frontend.coretemplates.Class"),

  CLASS_METHOD("frontend.coretemplates.Method"),

  ENUM("frontend.coretemplates.Enum"),

  INTERFACE("frontend.coretemplates.Interface"),

  INTERFACE_METHOD("frontend.coretemplates.InterfaceMethod");

  private String template;

  FrontendCoreTemplate(String str) {
    this.template = str;
  }

  @Override
  public String getTemplate() {
    return this.template;
  }

  @Override public String toString() {
    return this.template;
  }
}
