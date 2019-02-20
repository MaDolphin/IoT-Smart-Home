/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package backend.dtos;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.TrafoForAggregateModels;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.CDTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DtoForAggregatesCreator extends TrafoForAggregateModels {

  public static final String DTO = "DTO";

  public DtoForAggregatesCreator() {
    super(DTO);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass clazz, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDConstructor> constructors = Lists.newArrayList();
    // add empty constructor for DTO
    ASTCDConstructor emptyConstructor = new CDConstructorBuilder().Package().Public().setName(clazz.getName()).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), emptyConstructor,
        new StringHookPoint("{this.typeName = \"" + TransformationUtils.removeTOPExtensionIfExistent(clazz.getName()) + "\";}"));

    constructors.add(emptyConstructor);
    createFullConstructor(clazz, domainClass, typeSymbol).ifPresent(constructors::add);
    return constructors;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("de.macoco.be.dtos.rte.DTO");
    return imports;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass dtoClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    for (ASTCDAttribute attr : dtoClass.getCDAttributeList()) {
      methods.add(createDefaultSet(attr));
      methods.add(createDefaultGet(attr));
    }
    methods.add(createToStringMethod(dtoClass, typeSymbol));
    return methods;
  }

  private ASTCDMethod createDefaultGet(ASTCDAttribute attr) {
    String attrName = attr.getName();

    // add getter
    ASTCDMethod getMethod = new CDMethodBuilder().Public()
        .name(GetterSetterHelper.getPlainGetter(attr))
        .setReturnType((ASTReturnType) attr.getType().deepClone()).build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), getMethod,
        new TemplateHookPoint("backend.data.proxy.Get", attrName,
            this.typeHelper.isPrimitive(attr.getType()), this.typeHelper.isList(attr.getType())));

    return getMethod;
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    if (!handledClass.getSuperclassOpt().isPresent()) {
      attributes.add(new CDAttributeBuilder().Private()
          .setType(CDSimpleReferenceBuilder.PrimitiveTypes.createLong()).setName("id")
          .build());
    }
    attributes.addAll(getAttributesForFields(clazz, typeSymbol));
    return attributes;
  }

  @Override
  protected Optional<String> getSuperclassName(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    if (domainClass.getSuperclassOpt().isPresent()) {
      return Optional.of(TypesPrinter.printReferenceType(domainClass.getSuperclass()) + DTO);
    }
    else {
      return Optional.of("DTO");
    }
  }

  //----------- PRIVATE  METHODS -------------------------------------

  private Optional<ASTCDConstructor> createFullConstructor(ASTCDClass clazz, ASTCDClass domainClass, CDTypeSymbol type) {

    // prepare for constructor
    List<CDFieldSymbol> localAttributes = symbolTable.get().getVisibleAttributes(type.getName());

    boolean hasSuperclass = domainClass.getSuperclassOpt().isPresent();
    List<CDFieldSymbol> superAttributes = hasSuperclass ?
        symbolTable.get().getInheritedVisibleAttributesInHierarchy(domainClass.getName()) :
        new ArrayList<>();

    List<ASTCDParameter> constructorArguments = new ArrayList<>();
    List<ASTCDParameter> superArguments = new ArrayList<>();

    Optional<ASTCDParameter> id = addIdAttribute(type);
    if (id.isPresent()) {
      constructorArguments.add(id.get());
    }

    // get all visible parameters
    localAttributes.stream().map(a -> CD4AnalysisMill.cDParameterBuilder().setName(a.getName()).setType(
        new CDSimpleReferenceBuilder().name(TransformationUtils
            .getStringRepresentationForCdOrDtoType(a.getType(), symbolTable.get())).build())
        .build()).forEach(constructorArguments::add);

    // get all visible of the super class
    superAttributes.stream().map(a -> CD4AnalysisMill.cDParameterBuilder().setName(a.getName()).setType(
        new CDSimpleReferenceBuilder().name(TransformationUtils
            .getStringRepresentationForCdOrDtoType(a.getType(), symbolTable.get())).build())
        .build()).forEach(superArguments::add);

    if (hasSuperclass) {
      constructorArguments.addAll(superArguments);
      superArguments.add(0, id.get());
    }

    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .setName(type.getName() + DTO)
        .setCDParameterList(constructorArguments).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new TemplateHookPoint("backend.dtos.DtoConstructor", clazz.getName(), superArguments.stream()
            .map(ASTCDParameter::getName).collect(Collectors.toList()),
            localAttributes));
    return Optional.of(constructor);
  }

  protected Optional<ASTCDParameter> addIdAttribute(CDTypeSymbol type) {
    return Optional.of(CD4AnalysisMill.cDParameterBuilder().setName("id").setType(
        new CDSimpleReferenceBuilder().name("long").build())
        .build());
  }

  private ASTCDMethod createToStringMethod(ASTCDClass proxy, CDTypeSymbol typeSymbol) {
    ASTCDMethod toStringMethod = new CDMethodBuilder().Public()
        .name("toString")
        .returnType("String").build();
    List<CDFieldSymbol> fields = symbolTable.get()
        .getVisibleNotDerivedAttributesInHierarchy(typeSymbol.getName());
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), toStringMethod,
        new TemplateHookPoint("backend.dtos.ToString", proxy.getName(),
            proxy.getCDAttributeList()));
    return toStringMethod;
  }

  private ASTCDMethod createDefaultSet(ASTCDAttribute attr) {
    String attrName = attr.getName();
    ASTCDMethod setMethod = new CDMethodBuilder().Public()
        .addParameter((ASTType) attr.getType().deepClone(), "o")// attrName
        .name(GetterSetterHelper.getPlainSetter(attr)).build();
    // check if the attribute is static
    if (attr.getModifierOpt().isPresent() && attr.getModifier().isStatic()) {
      setMethod.getModifier().setStatic(true);
    }

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), setMethod,
        new TemplateHookPoint("backend.data.dataclass.accessmethods.Set",
            attr.getType(),
            attrName,
            "o", false));

    return setMethod;
  }

  protected Collection<ASTCDAttribute> getAttributesForFields(ASTCDClass clazz,
                                                              CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attrList = new ArrayList<>();
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleAttributes(clazz.getName());
    fields.addAll(symbolTable.get().getDerivedAttributes(clazz.getName()));
    // get all visible parameters
    for (CDFieldSymbol field : fields) {
      String typeName = TransformationUtils
          .getStringRepresentationForCdOrDtoType(field.getType(), symbolTable.get());
      ASTCDAttribute attribute = new CDAttributeBuilder().Protected()
          .type(typeName)
          .setName(field.getName()).build();
      if (typeHelper.isList(field.getType())) {
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attribute,
            CDSimpleReferenceBuilder.CollectionTypes.LIST_VALUE);
      }
      else if (CDTypes.isLong(field.getType().getName())) {
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attribute,
            new StringHookPoint("= -1L;"));
      }
      else {
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attribute,
            new StringHookPoint(
                "=" + typeHelper.getDefaultValue(field.getType().getName()) + ";"));
      }
      attrList.add(attribute);
    }

    return attrList;
  }

}
