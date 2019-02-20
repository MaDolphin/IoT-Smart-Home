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
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;

public class BuilderTestClassCreator extends CreateTrafo {

  public static final String DummyCreator = "DummyCreator";

  public BuilderTestClassCreator(){
    super(TestClassManager.getTestConfiguration().get("BuilderTest"));
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass handledClass, ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    ArrayList<ASTCDMethod> methods = Lists.newArrayList();
    if (!typeSymbol.isAbstract()) {
      methods.add(createTestMethod(clazz.getName()));
      methods.add(createEmptyBuilderTestMethod(clazz.getName()));
    }
    methods.add(createGetDummyCreatorMethod(clazz.getName()));
    methods.addAll(createGetDummyCreatorMethods(clazz.getName()));
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
    imports.add("static org.junit.Assert.assertNull");
    imports.add("static junit.framework.TestCase.fail");
    imports.add("javax.xml.bind.ValidationException");
    return imports;
  }

  private ASTCDMethod createTestMethod(String clazz){
    ASTCDMethod method;
    method = new CDMethodBuilder().Public().returnType("void")
        .name("test").build();
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleNotDerivedAttributesInHierarchy(clazz);
    List<CDAssociationSymbol> assocs = symbolTable.get().getAllAssociationsForType(clazz);
    TransformationUtils.addAnnos(method.getModifier(), "@Test");
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.test.builderTest", clazz, fields, assocs));
    return method;
  }

  private ASTCDMethod createEmptyBuilderTestMethod(String clazz){
    ASTCDMethod method;
    method = new CDMethodBuilder().Public().returnType("void")
      .name("testEmptyBuilderTestMethod").build();
    TransformationUtils.addAnnos(method.getModifier(), "@Test");
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
      new TemplateHookPoint("backend.data.test.emptyBuilderTest", clazz));
    return method;
  }

  private ASTCDMethod createGetDummyCreatorMethod(String name) {
    ASTCDMethod method;
    String typeName = name + DummyCreator;
    method = new CDMethodBuilder().Protected().returnType(typeName)
        .name("get" + typeName).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ return new " + typeName + "(); }"));
    return method;
  }

  private List<ASTCDMethod> createGetDummyCreatorMethods(String clazz) {
    List<ASTCDMethod> methods = new ArrayList<>();
    for(CDAssociationSymbol assoc: symbolTable.get().getAllAssociationsForType(clazz)) {
      String typeName = TransformationUtils.capitalize(assoc.getTargetType().getName()) + DummyCreator;
      String methodName = "getDummy" + TransformationUtils.capitalize(assoc.getDerivedName()) + "Creator";
      ASTCDMethod method = new CDMethodBuilder().Protected().returnType(typeName)
          .name(methodName).build();
      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
          new StringHookPoint("{ return new " + typeName + "(); }"));
      methods.add(method);
    }
    return methods;
  }

}
