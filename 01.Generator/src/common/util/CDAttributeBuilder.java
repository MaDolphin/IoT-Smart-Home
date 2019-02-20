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

import java.util.Optional;

import de.monticore.literals.literals._ast.ASTStringLiteral;
import de.monticore.literals.literals._ast.LiteralsMill;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttributeBuilder;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

public class CDAttributeBuilder extends ASTCDAttributeBuilder implements
    ModifierModifiable<CDAttributeBuilder> {

  private ConcreteModifierDelegate modDelegator = new ConcreteModifierDelegate();

  @Override
  public CDAttributeBuilder Public() {
    this.modifier = Optional.of(modDelegator.Public(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDAttributeBuilder Private() {
    this.modifier = Optional.of(modDelegator.Private(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDAttributeBuilder Protected() {
    this.modifier = Optional.of(modDelegator.Protected(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDAttributeBuilder Static() {
    this.modifier = Optional.of(modDelegator.Static(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDAttributeBuilder Final() {
    this.modifier = Optional.of(modDelegator.Final(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDAttributeBuilder Package() {
    this.modifier = Optional.of(modDelegator.Package(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDAttributeBuilder Abstract() {
    this.modifier = Optional.of(modDelegator.Abstract(this.modifier.orElse(null)));
    return this;
  }

  public CDAttributeBuilder type(String type) {
    return (CDAttributeBuilder) super.setType(new CDSimpleReferenceBuilder().name(type).build());
  }

  public CDAttributeBuilder val(String value) {
    ASTStringLiteral astValue = LiteralsMill.stringLiteralBuilder().setSource(value).build();
    return (CDAttributeBuilder) super.setValueOpt(Optional.of(CD4AnalysisMill.valueBuilder().setSignedLiteral(astValue).build()));
  }

  @Override
  public ASTCDAttribute build() {
    return super.build();
  }
}
