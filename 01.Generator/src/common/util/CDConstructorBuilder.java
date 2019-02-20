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

package common.util;

import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructorBuilder;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

/**
 * This builder is only a decorator for the underlying builder of CDConstructor.
 * The decoration is concerned with using default values for optional fields, if
 * nothing is given.
 *
 * @author Alexander Roth
 */
public class CDConstructorBuilder extends ASTCDConstructorBuilder implements
    ModifierModifiable<CDConstructorBuilder> {

  private ConcreteModifierDelegate modDelegator = new ConcreteModifierDelegate();

  public CDConstructorBuilder Public() {
    this.modifier = modDelegator.Public(this.modifier);
    return this;
  }

  public CDConstructorBuilder Private() {
    this.modifier = modDelegator.Private(this.modifier);
    return this;
  }

  public CDConstructorBuilder Protected() {
    this.modifier = modDelegator.Protected(this.modifier);
    return this;
  }

  public CDConstructorBuilder Static() {
    this.modifier = modDelegator.Static(this.modifier);
    return this;
  }

  public CDConstructorBuilder Final() {
    this.modifier = modDelegator.Final(this.modifier);
    return this;
  }

  @Override
  public CDConstructorBuilder Package() {
    this.modifier = modDelegator.Package(this.modifier);
    return this;
  }

  public CDConstructorBuilder addParameter(ASTType type, String name) {
    this.cDParameters.add(CD4AnalysisMill.cDParameterBuilder().setType(type).setName(name)
        .build());
    return this;
  }

  public CDConstructorBuilder addParameter(String type, String name) {
    addParameter(new CDSimpleReferenceBuilder().name(type).build(), name);
    return this;
  }

  @Override public CDConstructorBuilder Abstract() {
    this.modifier = modDelegator.Abstract(this.modifier);
    return this;
  }

  // use default values if not given
  public ASTCDConstructor build() {
    if (this.modifier == null) {
      Public();
    }
    return super.build();
  }
}
