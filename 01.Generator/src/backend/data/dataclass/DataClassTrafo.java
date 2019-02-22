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

package backend.data.dataclass;

import backend.common.CoreTemplate;
import backend.coretemplates.association.AssociationNameUtil;
import backend.data.validator.ValidatorCreator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import common.ExtendTrafo;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.literals.literals._ast.ASTStringLiteral;
import de.monticore.literals.literals._ast.LiteralsMill;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static common.util.CompilationUnit.IMPORTS_PROPERTY;
import static common.util.TransformationUtils.*;

public class DataClassTrafo extends ExtendTrafo {

  public static final String RAW_INIT_ATTRS = "rawInitAttrs";

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    if (!clazz.getSuperclassOpt().isPresent()) {
      // Add attribute id that can be used to identify the object
      ASTCDAttribute identifyer = new CDAttributeBuilder().Protected().type("long").setName("id")
          .build();
      attributes.add(identifyer);
    }

    return attributes;
  }

  @Override
  protected void extendDomainEnum(ASTCDEnum domainEnum, Optional<CDTypeSymbol> symbol) {
    super.extendDomainEnum(domainEnum, symbol);
    for (ASTCDEnumConstant enumConst : domainEnum.getCDEnumConstantList()) {
      if (!enumConst.getCDEnumParameterList().isEmpty()) {
        for (ASTCDEnumParameter param : enumConst.getCDEnumParameterList()) {
          if (param.getValue().getSignedLiteral() instanceof ASTStringLiteral) {
            String val = ((ASTStringLiteral) param.getValue().getSignedLiteral()).getSource();
            param.getValue().setSignedLiteral(
                LiteralsMill.stringLiteralBuilder().setSource("\"" + val + "\"").build());
          }
        }
      }
      else {
        String name = TransformationUtils.createEnumParameter(enumConst.getName());
        ASTValue val = CD4AnalysisMill.valueBuilder()
            .setSignedLiteral(LiteralsMill.stringLiteralBuilder().setSource(name).build()).build();
        enumConst
            .addCDEnumParameter(CD4AnalysisMill.cDEnumParameterBuilder().setValue(val).build());
      }
    }

  }

  @Override
  protected List<String> getInterfaceNames(CDTypeSymbol typeSymbol) {
    List<String> interfaces = new ArrayList<>();
    interfaces.add("DomainClass");
    interfaces.add("IObject");
    return interfaces;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("java.util.ArrayList");
    imports.add("java.util.List");
    imports.add("com.google.common.base.Objects");
    imports.add("com.google.common.primitives.Longs");
    imports.add("javax.persistence.*");
    imports.add("de.montigem.be.domain.rte.interfaces.IDomainValidator");
    imports.add("de.montigem.be.domain.rte.interfaces.IObject");
    imports.add("de.montigem.be.domain.cdmodelhwc.classes.DomainClass");

    List<CDTypeSymbol> s = symbolTable.get().getAllSuperClasses(typeSymbol.getName());
    if (s.size() > 1) {
      imports
          .add(TransformationUtils.getQualifiedName(getAstRoot(), s.get(s.size() - 1).getName()));
    }

    return imports;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    methods.add(createRawInitAttrs(handledClass, typeSymbol));
    methods.add(createMergeMethod(handledClass, typeSymbol));
    methods.add(createMergeWithoutAssociationsMethod(handledClass, typeSymbol));
    methods.add(createEqualsMethod(handledClass, typeSymbol));
    methods.add(createHashCodeMethod(handledClass, typeSymbol));
    methods.add(createToStringMethod(handledClass, typeSymbol));
    methods.add(createToDetailedStringMethod(handledClass, typeSymbol));
    // TODO: SVa/GV create these methods for every list type
    if (handledClass.getCDAttributeList().stream().anyMatch(a -> a.getName().equals("labels"))) {
      methods.add(createAddLabelMethod(typeSymbol));
      methods.add(createRemoveLabelMethod(typeSymbol));
      methods.add(createHasLabelMethod(typeSymbol));
    }
    Optional<ASTCDMethod> addMethod = createGetHumanNameMethod(handledClass, typeSymbol);
    if (addMethod.isPresent()) {
      methods.add(addMethod.get());
    }
    methods.addAll(createGetHumanNameForAttributeMethods(handledClass, typeSymbol));
    methods.addAll(createGetHumanNameForAssociationsMethods(handledClass, typeSymbol));
    methods.add(createGetValidatorMethod(handledClass, typeSymbol));
    methods.add(createGetPermissionIdMethod(handledClass, typeSymbol));
    methods.add(createGetPermissionClassMethod(handledClass, typeSymbol));
    methods.addAll(createCheckForOptionalAssocicationMethods(handledClass, typeSymbol));
    return methods;
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass clazz, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDConstructor> constructors = Lists.newArrayList();
    // add empty constructor for builder pattern
    constructors
        .add(new CDConstructorBuilder().Package().Public().setName(clazz.getName()).build());
    // BR, full constructior removed, because not needed:
    // constructors.add(createFullConstructor(clazz, typeSymbol));
    return constructors;
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDEnum extendedEnum,
      CDTypeSymbol typeSymbol) {
    List<ASTCDConstructor> constructors = Lists.newArrayList();
    ASTCDConstructor constr = new CDConstructorBuilder()
        .Package()
        .addParameter("String", "name")
        .setName(extendedEnum.getName())
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constr,
        new StringHookPoint("{ this.name = name; }"));
    constructors.add(constr);
    return constructors;
  }

  @Override
  protected Collection<ASTCDMethod> createMethods(ASTCDEnum extendedEnum, ASTCDEnum domainEnum,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    ASTCDMethod method = new CDMethodBuilder()
        .Public()
        .Static()
        .name("getAllNames")
        .returnType("List<String>")
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.dataclass.GetAllNames", extendedEnum));
    methods.add(method);
    method = new CDMethodBuilder()
        .Public()
        .name("getName")
        .returnType("String")
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ return this.name; }"));
    methods.add(method);
    return methods;
  }

  @Override
  protected Collection<ASTCDAttribute> createAttributes(ASTCDEnum extendedEnum,
      ASTCDEnum domainEnum, CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = Lists.newArrayList();
    attributes.add(new CDAttributeBuilder().type("String").setName("name").build());
    return attributes;
  }

  //----------- PRIVATE  METHODS -------------------------------------

  private ASTCDMethod createToStringMethod(ASTCDClass handledClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod toStringMethod = new CDMethodBuilder().Public()
        .name("toString")
        .returnType("String").build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), toStringMethod,
        new TemplateHookPoint("backend.data.dataclass.ToString", handledClass.getName(),
            symbolTable.get().getVisibleAttributes(handledClass.getName()),
            handledClass.getSuperclassOpt().isPresent()));
    return toStringMethod;
  }

  private ASTCDMethod createToDetailedStringMethod(ASTCDClass handledClass,
      CDTypeSymbol typeSymbol) {
    ASTCDMethod toStringMethod = new CDMethodBuilder().Public()
        .name("toDetailedString")
        .returnType("String").build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), toStringMethod,
        new TemplateHookPoint("backend.data.dataclass.ToDetailedString", handledClass.getName(),
            handledClass.getCDAttributeList(), handledClass.getSuperclassOpt().isPresent()));
    return toStringMethod;
  }

  private ASTCDMethod createAddLabelMethod(CDTypeSymbol typeSymbol) {
    ASTCDMethod createMethod = new CDMethodBuilder().Public()
        .addParameter("String", "newLabel")
        .name("addLabel")
        .returnType("void").build();
    String newLabel = "newLabel";
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), createMethod,
        new TemplateHookPoint("backend.data.dataclass.addLabel", newLabel));
    return createMethod;
  }

  private ASTCDMethod createRemoveLabelMethod(CDTypeSymbol typeSymbol) {
    ASTCDMethod createMethod = new CDMethodBuilder().Public()
        .addParameter("String", "targetLabel")
        .name("removeLabel")
        .returnType("boolean").build();
    String newLabel = "targetLabel";
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), createMethod,
        new TemplateHookPoint("backend.data.dataclass.removeLabel", newLabel));
    return createMethod;
  }

  private ASTCDMethod createHasLabelMethod(CDTypeSymbol typeSymbol) {
    ASTCDMethod createMethod = new CDMethodBuilder().Public()
        .addParameter("String", "targetLabel")
        .name("hasLabel")
        .returnType("boolean").build();
    String newLabel = "targetLabel";
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), createMethod,
        new TemplateHookPoint("backend.data.dataclass.hasLabel", newLabel));
    return createMethod;
  }

  private ASTCDMethod createMergeMethod(ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    List<CDAssociationSymbol> localAssociations = symbolTable.get().getLocalAssociations(clazz)
        .stream()
        .filter(x -> !x.isDerived()).collect(Collectors.toList());

    List<CDFieldSymbol> localAttributes = symbolTable.get().getVisibleAttributes(clazz.getName());

    List<CDFieldSymbol> parentAttributes = symbolTable.get()
        .getInheritedVisibleAttributesInHierarchy(clazz.getName());

    List<CDAssociationSymbol> parentAssociations = symbolTable.get()
        .getInheritedAssociations(clazz);

    // add merge for builder pattern
    List<ASTCDParameter> paramList = Lists.newArrayList();
    paramList.add(CD4AnalysisMill.cDParameterBuilder().setName("nv")
        .setType((new CDSimpleReferenceBuilder()).name("IObject").build()).build());
    ASTCDMethod method = new CDMethodBuilder().Public().name("merge").setCDParameterList(paramList)
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.dataclass.Merge",
            clazz, typeSymbol.getName(), parentAttributes, parentAssociations, localAttributes,
            localAssociations,
            new AssociationNameUtil(),
            new CDAssociationUtil()));
    return method;
  }

  private ASTCDMethod createMergeWithoutAssociationsMethod(ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    List<CDAssociationSymbol> localAssociations = symbolTable.get().getLocalAssociations(clazz)
        .stream()
        .filter(x -> !x.isDerived()).collect(Collectors.toList());

    List<CDFieldSymbol> localAttributes = symbolTable.get().getVisibleAttributes(clazz.getName());

    List<CDFieldSymbol> parentAttributes = symbolTable.get()
        .getInheritedVisibleAttributesInHierarchy(clazz.getName());

    List<CDAssociationSymbol> parentAssociations = symbolTable.get()
        .getInheritedAssociations(clazz);

    // add merge for builder pattern
    List<ASTCDParameter> paramList = Lists.newArrayList();
    paramList.add(CD4AnalysisMill.cDParameterBuilder().setName("nv")
        .setType((new CDSimpleReferenceBuilder()).name("IObject").build()).build());
    ASTCDMethod method = new CDMethodBuilder().Public().name("mergeWithoutAssociations").setCDParameterList(paramList)
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.dataclass.MergeWithoutAssociations",
            clazz, typeSymbol.getName(), parentAttributes, localAttributes));
    return method;
  }

  // new version of consructor using the rawInit method
  private ASTCDConstructor createFullConstructor(ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    List<CDAssociationSymbol> localAssociations = CDAssociationUtil
        .getLocalAssociations(clazz, symbolTable.get());

    addPropertyValues(clazz, localAssociations);

    // Constructor parameter
    List<ASTCDParameter> constructorArguments = symbolTable.get().getConstructorParameter(clazz);
    List<String> constructorArgumentNames = constructorArguments.stream()
        .map(ASTCDParameter::getName).collect(Collectors.toList());

    // add constructor for builder pattern
    ASTCDConstructor builderConstr = new CDConstructorBuilder().Public().setName(clazz.getName())
        .setCDParameterList(constructorArguments).build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), builderConstr,
        new TemplateHookPoint("backend.data.dataclass.ConstructorWithRawInit",
            constructorArgumentNames));

    return builderConstr;
  }

  // initialization method that replaces the _static_ full constructor and this is inherited.
  private ASTCDMethod createRawInitAttrs(ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    List<CDFieldSymbol> localAttributes = symbolTable.get().getVisibleAttributes(clazz.getName());
    List<CDAssociationSymbol> localAssociations = CDAssociationUtil
        .getLocalAssociations(clazz, symbolTable.get());

    addPropertyValues(clazz, localAssociations);

    // Method parameters
    List<ASTCDParameter> constructorArguments = symbolTable.get().getConstructorParameter(clazz);
    List<String> superArguments = symbolTable.get().getSuperConstructorParameter(clazz).stream()
        .map(ASTCDParameter::getName).collect(Collectors.toList());

    /// +"TOP" im Namen? Woher kommt das Wissen?
    ASTCDMethod method = new CDMethodBuilder().Public().returnType(clazz.getName())
        .name(RAW_INIT_ATTRS)
        .setCDParameterList(constructorArguments).build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.dataclass.RawInitAttrs", superArguments,
            localAttributes, localAssociations, clazz.getName()));
    return method;
  }

  private ASTCDMethod createEqualsMethod(ASTCDClass handledClass, CDTypeSymbol typeSymbol) {
    List<ASTCDParameter> paramList = Lists.newArrayList();
    paramList.add(CD4AnalysisMill.cDParameterBuilder().setName("o")
        .setType((new CDSimpleReferenceBuilder()).name(handledClass.getName()).build()).build());
    ASTCDMethod method = new CDMethodBuilder().Public().returnType("boolean").name("equals")
        .setCDParameterList(paramList)
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.dataclass.Equals",
            typeSymbol.getName()));
    return method;
  }

  private ASTCDMethod createHashCodeMethod(ASTCDClass handledClass, CDTypeSymbol typeSymbol) {
    List<ASTCDParameter> paramList = Lists.newArrayList();
    paramList.add(CD4AnalysisMill.cDParameterBuilder().setName("o")
        .setType((new CDSimpleReferenceBuilder()).name(typeSymbol.getName()).build()).build());
    ASTCDMethod method = new CDMethodBuilder().Public().returnType("int").name("hashCode")
        .setCDParameterList(paramList)
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{\n return Longs.hashCode(id); \n}"));
    return method;
  }

  private ASTCDMethod createGetValidatorMethod(ASTCDClass handledClass, CDTypeSymbol typeSymbol) {
    // TODO: SVa should there be a method for each class in hierarchy?
    String className = getUpmostSuperclassName(handledClass);
    ASTCDMethod method = new CDMethodBuilder().Public()
        .returnType("IDomainValidator<" + className + ">").name("getValidator")
        .build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.dataclass.getValidator",
            typeSymbol.getName(), ValidatorCreator.VALIDATOR));

    return method;
  }

  private ASTCDMethod createGetPermissionIdMethod(ASTCDClass handledClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder().Public()
        .returnType("long").name("getPermissionId")
        .build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.dataclass.getPermissionId",
            handledClass));

    return method;
  }

  private ASTCDMethod createGetPermissionClassMethod(ASTCDClass handledClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder().Public()
        .returnType("String").name("getPermissionClass")
        .build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.dataclass.getPermissionClass",
            handledClass, handledClass.getName()));

    return method;
  }

  private String getUpmostSuperclassName(ASTCDClass handledClass) {
    return Iterables.getLast(symbolTable.get().getAllSuperClasses(handledClass.getName()))
        .getName();
  }

  private void addPropertyValues(ASTCDClass clazz, List<CDAssociationSymbol> localAssociations) {
    if (CDAssociationUtil.hasQualifiedOrderedAssociations(localAssociations)) {
      addPropertyValue(clazz, IMPORTS_PROPERTY, "com.google.common.collect.ListMultimap");
    }
    if (CDAssociationUtil.hasQualifiedNotOrderedAssociations(localAssociations)) {
      addPropertyValue(clazz, IMPORTS_PROPERTY, "com.google.common.collect.Multimap");
      addPropertyValue(clazz, IMPORTS_PROPERTY, "com.google.common.collect.ArrayListMultimap");
    }
  }

  private Optional<ASTCDMethod> createGetHumanNameMethod(ASTCDClass handledClass,
      CDTypeSymbol typeSymbol) {
    Optional<String> humanName = getStereotypeValue(handledClass.getModifierOpt(),
        TransformationUtils.STEREOTYPE_HUMANNAME);
    if (humanName.isPresent()) {
      /*ASTStereotype override = CD4AnalysisMill.stereotypeBuilder().build();
      override.getValueList().add(CD4AnalysisMill.stereoValueBuilder().setName("@Override").build());*/
      ASTCDMethod method = new CDMethodBuilder()
          .modifier(CD4AnalysisMill.modifierBuilder().build()).Public().Static()
          .name("getHumanName").returnType("String")
          .build();
      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
          new StringHookPoint("{ return " + identToString(humanName.get()) + "; }"));
      return Optional.of(method);
    }
    else {
      ASTCDMethod method = new CDMethodBuilder()
          .modifier(CD4AnalysisMill.modifierBuilder().build()).Public().Static()
          .name("getHumanName").returnType("String")
          .build();
      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
          new StringHookPoint("{ return " + identToString(handledClass.getName()) + "; }"));
      return Optional.of(method);
    }
    // return Optional.empty();
  }

  private List<ASTCDMethod> createGetHumanNameForAttributeMethods(ASTCDClass handledClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    for (ASTCDAttribute attribute : handledClass.getCDAttributeList()) {
      String attributeName = TransformationUtils.capitalize(attribute.getName());
      ASTCDMethod method = new CDMethodBuilder()
          .modifier(CD4AnalysisMill.modifierBuilder().build()).Public().Static()
          .name("getHumanNameForAttribute" + attributeName).returnType("String")
          .build();
      Optional<String> humanName = getStereotypeValue(attribute.getModifierOpt(),
          TransformationUtils.STEREOTYPE_HUMANNAME);
      String methodBody = humanName.isPresent() ? identToString(humanName.get()) :
          "DomainClass.getHumanNameForAttribute(\"" + attribute.getName() + "\")";
      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
          new StringHookPoint("{ return " + methodBody + "; }"));
      methods.add(method);
    }
    return methods;
  }

  private List<ASTCDMethod> createGetHumanNameForAssociationsMethods(ASTCDClass handledClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    for (CDAssociationSymbol association : typeSymbol.getAllAssociations()) {
      String attributeName = TransformationUtils
          .capitalize(CDAssociationUtil.getAssociationName(association));
      ASTCDMethod method = new CDMethodBuilder()
          .modifier(CD4AnalysisMill.modifierBuilder().build()).Public().Static()
          .name("getHumanNameForAssociation" + attributeName).returnType("String")
          .build();
      // TODO: SVa, GV: should an assoc also have a optional human name?
      String methodBody = "DomainClass.getHumanNameForAttribute(\"" + CDAssociationUtil
          .getAssociationName(association) + "\")";
      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
          new StringHookPoint("{ return " + methodBody + "; }"));
      methods.add(method);
    }
    return methods;
  }

  private List<ASTCDMethod> createCheckForOptionalAssocicationMethods(ASTCDClass handledClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    for (CDAssociationSymbol association : typeSymbol.getAllAssociations()) {
      if (CDAssociationUtil.isOptional(association)) {
        String name = TransformationUtils
            .capitalize(CDAssociationUtil.getAssociationName(association));
        ASTCDMethod method = new CDMethodBuilder()
            .modifier(CD4AnalysisMill.modifierBuilder().build()).Public()
            .name("has" + name).returnType("boolean")
            .build();
        getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new StringHookPoint("{ return get" + name + "() != null; }"));
        methods.add(method);
      }
    }
    return methods;
  }

}
