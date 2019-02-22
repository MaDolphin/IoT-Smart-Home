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
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProxyTestClassCreator extends CreateTrafo {

  private static String superClassName = "AbstractDomainTest";

  public ProxyTestClassCreator() {
    super(TestClassManager.getTestConfiguration().get("ProxyTest"));
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass proxyClass, ASTCDClass clazz,
      CDTypeSymbol type) {
    return Lists
        .newArrayList(
            new CDConstructorBuilder().Public().setName(proxyClass.getName()).build());
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    ArrayList<ASTCDMethod> methods = Lists.newArrayList();
    ASTCDMethod method = new CDMethodBuilder().Public().returnType("void").name("init").build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{  }"));
    TransformationUtils.addAnnos(method.getModifier(), "@Before");
    methods.add(method);
    methods.addAll(createGetDummyCreatorMethods(clazz.getName()));
    //methods.add(createTestMethod(clazz.getName()));
    return methods;
  }

  @Override
  protected List<ASTCDAttribute> createStaticAttributes(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    ArrayList<ASTCDAttribute> attributes = Lists.newArrayList();
    ASTCDStereotype inject = CD4AnalysisMill.cDStereotypeBuilder().build();
    inject.getValueList().add(CD4AnalysisMill.cDStereoValueBuilder().setName("@Inject").build());

    ASTCDAttribute attribute = new CDAttributeBuilder().Private().type(clazz.getName() + "DAO")
        .setName(TransformationUtils.uncapitalize(clazz.getName()) + "DAO")
        .setModifier(CD4AnalysisMill.modifierBuilder().setStereotype(inject).build()).build();
    ASTCDAttribute proxy = new CDAttributeBuilder().Private().type(clazz.getName() + "Proxy")
        .setName(TransformationUtils.uncapitalize(clazz.getName()) + "Proxy").build();

    attributes.add(attribute);
    attributes.add(proxy);
    ArrayList<String> assocs = findTransitiveAssocs(clazz.getName());
    for (String a : assocs) {
      ASTCDAttribute dao = new CDAttributeBuilder().Private().type(a + "DAO")
          .setName(TransformationUtils.uncapitalize(a) + "DAO")
          .setModifier(CD4AnalysisMill.modifierBuilder().setStereotype(inject).build()).build();
      attributes.add(dao);
    }
    return attributes;
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    return super.createAttributes(handledClass, clazz, typeSymbol);
  }

  @Override
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {

    CDClassBuilder clazzBuilder = new CDClassBuilder().Public();
    String className = typeSymbol.getName() + suffix;
    clazzBuilder.superclass(superClassName);
    ASTCDClass addedClass = clazzBuilder.setName(className).build();
    addedClass.setSymbol(typeSymbol);
    getOutputAst().getCDClassList().add(addedClass);
    return Optional.of(addedClass);
  }

  private ASTCDMethod createTestMethod(String clazz) {
    ASTCDMethod method;
    method = new CDMethodBuilder().Public().returnType("void")
        .name("testDAO").exceptions("Exception").build();
    TransformationUtils.addAnnos(method.getModifier(), "@Test");
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleNotDerivedAttributesInHierarchy(clazz);
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.test.daoTest",
            clazz,
            fields,
            findTransitiveAssocs(clazz),
            findTransitiveAssocsFields(clazz)));
    return method;
  }

  private void decide1TargetType(ArrayList<String> assocClasses, CDAssociationSymbol assocSymbol) {
    if (CDAssociationUtil.isOne(assocSymbol)) {
      assocClasses.add(assocSymbol.getTargetType().getName());
    }
  }

  private ArrayList<ASTCDMethod> createGetDummyCreatorMethods(String clazz) {
    ArrayList<String> listAssocs = findTransitiveAssocs(clazz);
    ArrayList<ASTCDMethod> createDummyMethods = new ArrayList<>();
    createDummyMethods.add(createGetDummyCreatorMethod(clazz));

    for (String assocClass : listAssocs) {
      createDummyMethods.add(createGetDummyCreatorMethod(assocClass));
    }

    return createDummyMethods;
  }

  private ASTCDMethod createGetDummyCreatorMethod(String name) {
    ASTCDMethod method;
    String typeName = name + "DummyCreator";
    method = new CDMethodBuilder().Protected().returnType(typeName).name("get" + typeName).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ return new " + typeName + "(); }"));
    return method;
  }

  private ArrayList<String> findTransitiveAssocs(String clazz) {
    ArrayList<String> assocClasses = new ArrayList<>();
    for (CDAssociationSymbol association : symbolTable.get()
        .getAllNonDerivedAssociationsForClass(clazz)) {
      if (CDAssociationUtil.isOne(association)) {
        assocClasses.add(association.getTargetType().getName());
        association.getTargetType().getAssociations()
            .forEach(a -> decide1TargetType(assocClasses, a));
      }
    }
    return assocClasses;
  }

  private ArrayList<String> findTransitiveAssocsFields(String clazz) {
    ArrayList<String> assocClasses = findTransitiveAssocs(clazz);
    ArrayList<String> fieldsOfAssocClass = new ArrayList<>();
    GetterSetterHelper helper = new GetterSetterHelper();
    for (String assocClass : assocClasses) {
      List<CDFieldSymbol> fields = symbolTable.get().getVisibleNotDerivedAttributesInHierarchy(assocClass);
      fieldsOfAssocClass.add("#" + assocClass);
      for (CDFieldSymbol field : fields) {
        fieldsOfAssocClass.add(helper.getGetter(field));
      }
    }
    return fieldsOfAssocClass;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("de.se_rwth.commons.logging.Log");
    imports.add("java.util.List");
    imports.add("java.util.Optional");
    imports.add("javax.xml.bind.ValidationException");
    imports.add("java.time.ZonedDateTime");
    imports.add("org.junit.Test");
    imports.add("org.junit.Before");
    imports.add("static org.junit.Assert.assertNotNull");
    imports.add("org.junit.Assert");
    imports.add("javax.inject.Inject");
    imports.add("de.montigem.be.domain.cdmodelhwc.daos.*");
    imports.add("de.montigem.be.AbstractDomainTest");
    imports.add("static org.junit.Assert.fail");
    imports.add("de.montigem.be.domain.cdmodelhwc.classes.account.AccountDummyCreator");
    imports.add("de.montigem.be.domain.cdmodelhwc.classes.account.Account");
    imports.add("de.montigem.be.domain.cdmodelhwc.classes.account.AccountBuilder");
    return imports;
  }
}
