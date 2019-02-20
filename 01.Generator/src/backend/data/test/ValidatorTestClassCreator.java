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

package backend.data.test;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.CDMethodBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;

public class ValidatorTestClassCreator extends CreateTrafo {

  public ValidatorTestClassCreator(){
    super(TestClassManager.getTestConfiguration().get("ValidatorTest"));
  }

  private ASTCDMethod createTestMethod(String clazz){
    ASTCDMethod method;
    method = new CDMethodBuilder().Public().returnType("void")
      .name("test").build();
    TransformationUtils.addAnnos(method.getModifier(), "@Test");
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
      new TemplateHookPoint("backend.data.test.validatorTest", clazz));
    return method;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass handledClass, ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    ArrayList<ASTCDMethod> methods = Lists.newArrayList();
    if (!typeSymbol.isAbstract()) {
      methods.add(createTestMethod(clazz.getName()));
    }
    methods.add(createGetDummyCreatorMethod(clazz.getName()));
    return methods;
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass handledClass, ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    return super.createAttributes(handledClass, clazz, typeSymbol);
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("de.se_rwth.commons.logging.Log");
    imports.add("java.util.List");
    imports.add("java.util.Optional");
    imports.add("org.junit.Test");
    imports.add("static org.junit.Assert.assertEquals");
    imports.add("static org.junit.Assert.assertNotNull");
    imports.add("static junit.framework.TestCase.fail");
    imports.add("javax.xml.bind.ValidationException");
    return imports;
  }

  private ASTCDMethod createGetDummyCreatorMethod(String name) {
    ASTCDMethod method;
    String typeName = name + "DummyCreator";
    method = new CDMethodBuilder().Protected().returnType(typeName)
        .name("get" + typeName).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ return new " + typeName + "(); }"));
    return method;
  }

}
