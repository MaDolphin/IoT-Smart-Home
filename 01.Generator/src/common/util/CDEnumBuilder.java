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

import com.google.common.collect.Lists;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

public class CDEnumBuilder extends ASTCDEnumBuilder implements CompilationUnit<CDEnumBuilder> {

  private ConcreteModifierDelegate modDelegator = new ConcreteModifierDelegate();

  private String packageName = "";

  private Collection<String> imports = new LinkedHashSet<>();

  public CDEnumBuilder Public() {
    this.modifier = Optional.of(modDelegator.Public(this.modifier.orElse(null)));
    return this;
  }

  public CDEnumBuilder Package() {
    this.modifier = Optional.of(modDelegator.Package(this.modifier.orElse(null)));
    return this;
  }

  public CDEnumBuilder interfaces(String interfaceName) {
    this.interfaces.add(new CDSimpleReferenceBuilder().name(interfaceName).build());
    return this;
  }

  @Override
  public CDEnumBuilder subpackage(String packageName) {
    this.packageName = packageName;
    return this;
  }

  @Override
  public CDEnumBuilder additionalImport(String imports) {
    this.imports.add(imports);
    return this;
  }

  @Override
  public CDEnumBuilder imports(Collection<String> imports) {
    this.imports = imports;
    return this;
  }

  @Override
  public ASTCDEnum build() {
    ASTCDEnum enumeration = super.build();
    TransformationUtils.setProperty(enumeration, IMPORTS_PROPERTY, this.imports);
    TransformationUtils.setProperty(enumeration, SUBPACKAGE_PROPERTY, this.packageName);
    return enumeration;
  }

  public void setAnnotation(String name){//}, String value){
    ASTCDStereotypeBuilder b = CD4AnalysisMill.cDStereotypeBuilder();
    ASTCDStereoValueBuilder bc = CD4AnalysisMill.cDStereoValueBuilder();
    bc.setName(name);
    //bc.value(value);
    b.setValueList(Lists.newArrayList(bc.build())).build();
  }
}
