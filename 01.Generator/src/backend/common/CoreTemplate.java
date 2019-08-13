/* (c) https://github.com/MontiCore/monticore */

package backend.common;

import common.DexCoreTemplate;

/**
 * Keep track of all templates that are necessary for generating code.
 */
public enum CoreTemplate implements DexCoreTemplate {

  EMPTY_METHOD("backend.coretemplates.EmptyMethod"),

  CLASS_ATTRIBUTE_VALUE("backend.coretemplates.AttributeValue"),

  DEFAULT_VALUE_METHOD("backend.coretemplates.DefaultMethodBody"),

  CONSTRUCTOR(EMPTY_METHOD.toString()),

  CLASS_ATTRIBUTE("backend.coretemplates.Attribute"),

  CLASS("backend.coretemplates.Class"),

  CLASS_GENERICS("backend.coretemplates.Generics"),



  CLASS_METHOD("backend.coretemplates.Method"),

  ENUM("backend.coretemplates.Enum"),

  INTERFACE("backend.coretemplates.Interface"),

  INTERFACE_METHOD("backend.coretemplates.InterfaceMethod"),
  
  THROW_NOTIMPLEMENTED_EXCEPTION("backend.coretemplates.ThrowExceptionMethodBody"),

  THROW_DERIVED_NOTIMPLEMENTED_EXCEPTION("backend.coretemplates.ThrowDerivedExceptionMethodBody"),

  THROW_DATASTRUCTUREVIOLATION_EXCEPTION("backend.coretemplates.association.AssociationConstraintException"),

  THROW_DATASTRUCTUREVIOLATION_EXCEPTION_METHOD("backend.coretemplates.association.AssociationConstraintExceptionMethodBody");

  private String template;

  CoreTemplate(String str) {
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
