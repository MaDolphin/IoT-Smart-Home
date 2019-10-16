/* (c) https://github.com/MontiCore/monticore */

package backend.data.builder;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;

import java.util.ArrayList;
import java.util.List;

public class BuilderCreator extends CreateTrafo {

  private TypeHelper typeHelper = new TypeHelper();

  public static final String BUILDER = "Builder";

  public BuilderCreator() {
    super(BUILDER);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass builderClass, ASTCDClass clazz,
      CDTypeSymbol type) {
    return Lists
        .newArrayList(new CDConstructorBuilder().Public().setName(builderClass.getName()).build());
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass builderClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    methods.addAll(createGetterSetterMethods(clazz, typeSymbol, builderClass));
    methods.add(createBuildMethod(clazz, typeSymbol, builderClass));
    methods.add(createBuildUpdateMethod(clazz, typeSymbol, builderClass));
    return methods;
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass builderClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    attributes.addAll(getAttributesForFields(clazz, typeSymbol));
    attributes.addAll(getAttributesForAssociations(clazz, typeSymbol));
    if (!clazz.getSuperclassOpt().isPresent()) {
      ASTCDAttribute id = new CDAttributeBuilder().Protected().type("long").setName("id")
              .build();
      attributes.add(id);
    }
    return attributes;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("java.util.ArrayList");
    imports.add("java.util.List");
    imports.add("java.util.Optional");
    imports.add("com.google.common.base.Objects");
    imports.add("de.montigem.be.domain.rte.interfaces.IDomainBuilder");
    imports.add("javax.xml.bind.ValidationException");
    return imports;
  }

  @Override
  protected List<String> getInterfaceNames(CDTypeSymbol typeSymbol) {
    List<String> interfaces = new ArrayList<>();
    if (!typeSymbol.getSuperClass().isPresent()) {
      interfaces.add("IDomainBuilder<" + typeSymbol.getName() + ">");
    }
    return interfaces;
  }

  //----------- PRIVATE  METHODS -------------------------------------

  private ASTCDMethod createBuildMethod(ASTCDClass clazz, CDTypeSymbol typeSymbol,
      ASTCDClass builderClass) {
    ASTCDMethod method = new CDMethodBuilder().returnType(clazz.getName()).name("build")
        .exceptions("ValidationException").build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{return build(false);}"));

    return method;
  }

  private ASTCDMethod createBuildUpdateMethod(ASTCDClass clazz, CDTypeSymbol typeSymbol,
      ASTCDClass builderClass) {
    ASTCDMethod method = new CDMethodBuilder().returnType(clazz.getName()).name("build").addParameter("boolean", "addToBidirectionalAssociation")
        .exceptions("ValidationException").build();

    List<ASTCDParameter> constructorArguments = symbolTable.get().getConstructorParameter(clazz);
    List<CDAssociationSymbol> allAssociations = CDAssociationUtil.getAllAssociations(clazz, symbolTable.get());

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.builder.Build", typeSymbol, constructorArguments, allAssociations));

    return method;
  }

  private List<ASTCDAttribute> getAttributesForFields(ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleAttributes(clazz.getName());

    // get all visible parameters
    for (CDFieldSymbol field : fields) {
      if (!field.isDerived()) {
        ASTCDAttribute attr = new CDAttributeBuilder().Protected()
            .type(field.getType().getStringRepresentation())
            .setName(field.getName()).build();
        if (typeHelper.isOptional(field.getType())) {
          getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
              CDSimpleReferenceBuilder.DataTypes.OPTIONAL_ABSENT);
        }
        else if (typeHelper.isWrapperType(field.getType())) {
          getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
              new StringHookPoint(
                  "=" + typeHelper.getDefaultValue(field.getType().getName()) + ";"));
        }
        else if (typeHelper.isPrimitive(field.getType())) {
          if ("istAktiv".equals(field.getName())) {
            getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
                new StringHookPoint(
                    "=true;"));
          }
          else {
            getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
                new StringHookPoint(
                    "=" + typeHelper.getDefaultValue(field.getType().getName()) + ";"));
          }
        }
        else if (typeHelper.isString(field.getType())) {
          getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
              new StringHookPoint(
                  "=" + typeHelper.getDefaultValue(field.getType().getName()) + ";"));
        }
        else if (typeHelper.isList(field.getType())) {
          getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
              CDSimpleReferenceBuilder.CollectionTypes.LIST_VALUE);
        }
        else if (typeHelper.isSet(field.getType())) {
          getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
              CDSimpleReferenceBuilder.CollectionTypes.LIST_VALUE);
        }
        attrList.add(attr);
      }
    }

    return attrList;
  }

  private List<ASTCDAttribute> getAttributesForAssociations(ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    // handle associations
    for (CDAssociationSymbol association : symbolTable.get()
        .getAllNonDerivedAssociationsForClass(clazz.getName())) {

      ASTCDParameter param = CDAssociationUtil.associationToCDParameter(association);
      ASTCDAttribute attribute = new CDAttributeBuilder().Protected().setType(param.getType())
          .setName(TransformationUtils.uncapitalize(param.getName())).build();
      attrList.add(attribute);

      // qualified association
      if (CDAssociationUtil.isQualified(association)
          && (CDAssociationUtil.isOne(association) || CDAssociationUtil
          .isOptional(association))) {
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attribute,
            CDSimpleReferenceBuilder.CollectionTypes.MAP_VALUE);
      }
      else if (CDAssociationUtil.isQualified(association)
          && CDAssociationUtil.isOrdered(association)) {
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attribute,
            CDSimpleReferenceBuilder.CollectionTypes.MULTIMAP_ORDERED_VALUE);
      }
      else if (CDAssociationUtil.isQualified(association)) {
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attribute,
            CDSimpleReferenceBuilder.CollectionTypes.MULTIMAP_ORDERED_VALUE);
      }
      else if (association.getTargetCardinality().isMultiple()
          && CDAssociationUtil.isOrdered(association)) {
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(),
            attribute, CDSimpleReferenceBuilder.CollectionTypes.LIST_VALUE);
      }
      else if (association.getTargetCardinality().isMultiple()
          && !CDAssociationUtil.isOrdered(association)) {
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(),
            attribute, CDSimpleReferenceBuilder.CollectionTypes.LIST_VALUE);
      }
      else if (CDAssociationUtil.isOptional(association)) {
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(),
            attribute, CDSimpleReferenceBuilder.DataTypes.OPTIONAL_ABSENT);
      }
    }

    return attrList;
  }

  private List<ASTCDMethod> createGetterSetterMethods(ASTCDClass clazz, CDTypeSymbol typeSymbol,
      ASTCDClass builderClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    List<CDFieldSymbol> inheritedFields = symbolTable.get().getInheritedAttributes(clazz);
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleNotDerivedAttributesInHierarchy(clazz.getName());
    fields.removeAll(inheritedFields);

    if (!clazz.getSuperclassOpt().isPresent()) {
      fields.add(new CDFieldSymbol("id", new CDTypeSymbolReference("long", typeSymbol.getEnclosingScope())));
    }

    for (CDFieldSymbol field : fields) {
      methodList.add(createSetMethod(builderClass.getName(), field));
      methodList.add(createGetMethod(builderClass.getName(), field));
    }

    for (CDFieldSymbol field : inheritedFields) {
      methodList.add(createInheritedSetMethod(builderClass.getName(), field));
      // the getter in the super class doesn't need to be overwritten
    }

    for (CDAssociationSymbol association : symbolTable.get()
        .getAllNonDerivedAssociationsForClass(clazz.getName())) {
      if (association.getQualifier().isPresent()) {
        methodList.addAll(getMethodsForQualifiedAssociation(association, clazz.getName()));
      }
      else if (association.getTargetCardinality().isMultiple()) {
        methodList.add(createSetMultipleAssociationMethod(builderClass.getName(), association));
        methodList.add(createAddMultipleAssociationMethod(builderClass.getName(), association));
      }
      else {
        methodList.add(createSetAssociationMethod(builderClass.getName(),
            determineArgumentType(association), association));
      }

      methodList.add(createGetMethod(builderClass.getName(), association));
    }
    return methodList;
  }

  private ASTSimpleReferenceType determineArgumentType(CDAssociationSymbol association) {
    if (association.getTargetCardinality().getMin() == 1) {
      return new CDSimpleReferenceBuilder().name(association.getTargetType().getName()).build();
    }
    return CDSimpleReferenceBuilder.DataTypes
        .createOptional(association.getTargetType().getName());
  }

  private ASTCDMethod createSetAssociationMethod(String builderName,
      ASTSimpleReferenceType argumentType, CDAssociationSymbol association) {
    CDMethodBuilder methodBuilder = new CDMethodBuilder().returnType(builderName)
        .addParameter(argumentType, "o")
        .name(TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association)));
    if (CDAssociationUtil.isOppositeQualified(association)) {
      methodBuilder.Protected();
    }
    else {
      methodBuilder.Public();
    }

    ASTCDMethod method = methodBuilder.build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ this."
            + TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association))
            + "= o; return this;}"));
    return method;
  }

  private ASTCDMethod createSetMultipleAssociationMethod(String builderName,
      CDAssociationSymbol association) {
    ASTCDParameter param = CDAssociationUtil.associationToCDParameter(association);
    CDMethodBuilder methodBuilder = new CDMethodBuilder().returnType(builderName)
        .addParameter(TypesPrinter.printType(param.getType()), "o")
        .name(TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association)));

    if (CDAssociationUtil.isOppositeQualified(association)) {
      methodBuilder.Protected();
    }
    else {
      methodBuilder.Public();
    }

    ASTCDMethod method = methodBuilder.build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ this."
            + TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association))
            + "= o; return this;}"));
    return method;
  }

  private ASTCDMethod createAddMultipleAssociationMethod(String builderName,
      CDAssociationSymbol association) {
    CDMethodBuilder methodBuilder = new CDMethodBuilder().returnType(builderName)
        .addParameter(association.getTargetType().getName(), "o")
        .name("add"
            + TransformationUtils.capitalize(CDAssociationUtil.getAssociationName(association)));

    if (CDAssociationUtil.isOppositeQualified(association)) {
      methodBuilder.Protected();
    }
    else {
      methodBuilder.Public();
    }

    ASTCDMethod method = methodBuilder.build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ this."
            + TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association))
            + ".add(o); return this;}"));
    return method;
  }

  private ASTCDMethod createSetMethod(String builderName, CDFieldSymbol field) {
    ASTCDMethod method = new CDMethodBuilder().Public().returnType(builderName)
        .addParameter(field.getType().getStringRepresentation(), field.getName())
        .name(field.getName()).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.builder.Set", field, new TypeHelper()));
    return method;
  }

  private ASTCDMethod createInheritedSetMethod(String builderName, CDFieldSymbol field) {
    ASTCDMethod method = new CDMethodBuilder().Public().returnType(builderName)
        .addParameter(field.getType().getStringRepresentation(), field.getName())
        .name(field.getName()).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ super." + field.getName() + "("
            + field.getName() + "); return this;}"));
    return method;
  }

  private ASTCDMethod createGetMethod(String builderName, CDFieldSymbol field) {
    ASTCDMethod method = new CDMethodBuilder().Public()
        .returnType(field.getType().getStringRepresentation())
        .name(TransformationUtils.makeCamelCase("get", field.getName())).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{return this." + field.getName() + ";}"));
    return method;
  }

  private ASTCDMethod createGetMethod(String builderName, CDAssociationSymbol association) {
    ASTCDParameter param = CDAssociationUtil.associationToCDParameter(association);
    ASTCDMethod method = new CDMethodBuilder().Public()
        .returnType(TypesPrinter.printType(param.getType()))
        .name(TransformationUtils.makeCamelCase("get",
            TransformationUtils.capitalize(CDAssociationUtil.getAssociationName(association))))
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{return this." + TransformationUtils
            .uncapitalize(CDAssociationUtil.getAssociationName(association)) + ";}"));
    return method;
  }

  private List<ASTCDMethod> getMethodsForQualifiedAssociation(CDAssociationSymbol symbol,
      String className) {
    List<ASTCDMethod> methodList = Lists.newLinkedList();
    ASTCDParameter param = CDAssociationUtil.associationToCDParameter(symbol);

    // add method
    CDMethodBuilder methodBuilder = new CDMethodBuilder().addParameter(param.getType(), "o")
        .returnType(className + BUILDER).name(TransformationUtils.uncapitalize(param.getName()));
    if (CDAssociationUtil.isOppositeQualified(symbol)) {
      methodBuilder.Protected();
    }
    else {
      methodBuilder.Public();
    }

    ASTCDMethod method = methodBuilder.build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ this."
            + TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(symbol))
            + " = o; return this;}"));
    methodList.add(method);

    // create add method
    CDMethodBuilder addMethod = new CDMethodBuilder().addParameter(
        TypesPrinter.printType(
            ((ASTCDQualifier) symbol.getQualifier().get().getAstNode().get()).getType()),
        "k")
        .addParameter(symbol.getTargetType().getName(), "o")
        .returnType(className + BUILDER)
        .name("add" + TransformationUtils.capitalize(param.getName()));

    ASTCDMethod addM = addMethod.build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), addM,
        new StringHookPoint("{ this."
            + TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(symbol))
            + ".put(k,o); return this;}"));
    methodList.add(addM);

    return methodList;
  }

}
