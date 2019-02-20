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
