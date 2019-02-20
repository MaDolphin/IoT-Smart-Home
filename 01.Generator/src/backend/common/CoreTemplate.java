/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
