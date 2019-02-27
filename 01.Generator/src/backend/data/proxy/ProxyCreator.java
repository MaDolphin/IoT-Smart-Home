/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package backend.data.proxy;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProxyCreator extends CreateTrafo {

  private TypeHelper typeHelper = new TypeHelper();

  public static final String PROXY = "Proxy";

  public ProxyCreator() {
    super(PROXY);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass proxyClass, ASTCDClass clazz,
      CDTypeSymbol type) {
    return Lists
        .newArrayList(getConstructors(proxyClass, type));
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass proxyClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    for (ASTCDAttribute attr : proxyClass.getCDAttributeList()) {
      methods.addAll(createDefaultGet(attr));
      methods.add(createDefaultSet(attr));
    }
    methods.addAll(getSetIDMethods(proxyClass, typeSymbol));
    methods.add(createToStringMethod(proxyClass, typeSymbol));
    methods.add(createGetFromDAOLibMethod(proxyClass, clazz, typeSymbol));
    return methods;
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass builderClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    attributes.addAll(getAttributesForFields(clazz, typeSymbol));
    attributes.add(new CDAttributeBuilder().Private()
        .setType(CDSimpleReferenceBuilder.PrimitiveTypes.createLong()).setName("id")
        .build());
    return attributes;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("java.util.ArrayList");
    imports.add("java.util.List");
    imports.add("java.util.Optional");
    imports.add("javax.xml.bind.ValidationException");
    imports.add("de.montigem.be.util.OptionalHelper");
    imports.add("de.montigem.be.domain.rte.interfaces.IObjectProxy");
    imports.add("de.montigem.be.util.DAOLib");
    imports.add("de.se_rwth.commons.logging.Log");
    return imports;
  }

  @Override
  protected List<String> getInterfaceNames(CDTypeSymbol typeSymbol) {
    List<String> interfaces = new ArrayList<>();
    interfaces.add("IObjectProxy");
    return interfaces;
  }

  //----------- PRIVATE  METHODS -------------------------------------

  private List<ASTCDConstructor> getConstructors(ASTCDClass proxy, CDTypeSymbol type) {
    List<ASTCDConstructor> constructors = new ArrayList<>();
    // prepare for constructor
    List<ASTCDParameter> paramList = new ArrayList<>();
    // add all mandatory fields
    String origName = type.getName();
    paramList.add(
        CD4AnalysisMill.cDParameterBuilder().setName(TransformationUtils.uncapitalize(origName))
            .setType(new CDSimpleReferenceBuilder().name(origName).build()).build());

    List<CDFieldSymbol> fields = symbolTable.get()
        .getVisibleNotDerivedAttributesInHierarchy(type.getName());
    List<CDAssociationSymbol> assocs = symbolTable.get().getLocalAssociations(type);
    // add derived attributes to fields
    fields.addAll(symbolTable.get().getDerivedAttributes(type.getName()));

    // add empty constructor for builder pattern
    ASTCDConstructor builderEmtpyConstr = new CDConstructorBuilder().Package().Protected()
        .setName(proxy.getName()).setCDParameterList(Lists.newArrayList()).build();
    constructors.add(builderEmtpyConstr);

    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .setName(proxy.getName())
        .setCDParameterList(paramList).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new TemplateHookPoint("backend.data.proxy.Constructor", origName, fields, assocs));
    constructors.add(constructor);
    return constructors;
  }

  private ASTCDMethod createToStringMethod(ASTCDClass proxy, CDTypeSymbol typeSymbol) {
    ASTCDMethod toStringMethod = new CDMethodBuilder().Public()
        .name("toString")
        .returnType("String").build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), toStringMethod,
        new TemplateHookPoint("backend.data.proxy.ToString", proxy.getName(), proxy.getCDAttributeList()));
    return toStringMethod;
  }



  private ASTCDMethod createGetFromProxyMethod(ASTCDClass proxy, ASTCDClass clazz, CDTypeSymbol typeSymbol){
    ASTCDMethod toStringMethod = new CDMethodBuilder().Public()
      .name("getDataObject")
      .returnType(clazz.getName()).exceptions("ValidationException").build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), toStringMethod,
      new TemplateHookPoint("backend.data.proxy.GetFromProxy", proxy.getName(),clazz.getCDAttributeList()));
    return toStringMethod;
  }

  private ASTCDMethod createGetFromDAOLibMethod(ASTCDClass proxy, ASTCDClass clazz, CDTypeSymbol typeSymbol){
    ASTCDMethod toStringMethod = new CDMethodBuilder().Public()
        .addParameter("DAOLib", "daoLib")
        .name("get" + typeSymbol.getName() + "FromDAOLib")
        .returnType(clazz.getName()).exceptions("ValidationException").build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), toStringMethod,
        new TemplateHookPoint("backend.data.proxy.GetFromDAOLib", proxy.getName(),clazz.getCDAttributeList()));
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

    // add setter
    if ((attr.getModifierOpt().isPresent() && !attr.getModifier().isFinal())
        || !attr.getModifierOpt().isPresent()) {

      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), setMethod,
          new TemplateHookPoint("backend.data.dataclass.accessmethods.Set",
              attr.getType(),
              attrName,
              "o", false));
    }
    else {
      // handle final attributes
      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(),
          setMethod,
          new TemplateHookPoint("backend.data.dataclass.accessmethods.FinalSet", "$"
              + attrName + "initialized", attrName, "o"));
    }

    return setMethod;
  }

  private List<ASTCDMethod> createDefaultGet(ASTCDAttribute attr) {
    List<ASTCDMethod> createdMethods = new ArrayList<>();
    String attrName = attr.getName();

    // add getter
    ASTCDMethod getMethod = new CDMethodBuilder().Public()
        .name(GetterSetterHelper.getPlainGetter(attr))
        .setReturnType((ASTReturnType) attr.getType().deepClone()).build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), getMethod,
        new TemplateHookPoint("backend.data.proxy.Get", attrName,
            this.typeHelper.isPrimitive(attr.getType()), this.typeHelper.isList(attr.getType())));

    createdMethods.add(getMethod);

    if (getMethod.getModifier().isProtected() || getMethod.getModifier().isPrivate()) {
      // add getter
      ASTCDMethod rawGetMethod = new CDMethodBuilder().name(GetterSetterHelper.getGetter(attr))
          .Public()
          .setReturnType((ASTReturnType) attr.getType().deepClone()).build();

      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), rawGetMethod,
          new StringHookPoint("{ return " + GetterSetterHelper.getPlainGetter(attr) + "();}"));
      createdMethods.add(rawGetMethod);
    }
    return createdMethods;
  }

  private Collection<? extends ASTCDAttribute> getAttributesForFields(ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attrList = new ArrayList<>();
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleAttributes(clazz.getName());
    fields.addAll(symbolTable.get().getDerivedAttributes(clazz.getName()));

    // get all visible parameters
    for (CDFieldSymbol field : fields) {
      String typeName = "";
      if (typeHelper.isOptional(field.getType())
          && field.getType().getActualTypeArguments().size() > 0) {
        typeName = field.getType().getActualTypeArguments().get(0).getType().getName();
      }
      else {
        typeName = field.getType().getStringRepresentation();
      }
      ASTCDAttribute attr = new CDAttributeBuilder().Private()
          .type(typeName)
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
        if (field.getName().equals("isActive")) {
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

      attrList.add(new CDAttributeBuilder().Private()
          .type(typeName)
          .setName(field.getName()).build());
    }

    // handle associations
    for (CDAssociationSymbol association : symbolTable.get()
        .getAllNonDerivedAssociationsForClass(clazz.getName())) {
      CDAttributeBuilder builder = new CDAttributeBuilder().Private();
      if (CDAssociationUtil.isMultiple(association)) {
        builder.type("List<Long>").setName(TransformationUtils
            .removeTrailingS(
                TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association)))
            + "Ids");
      }
      else {
        builder.type("long").setName(TransformationUtils
            .removeTrailingS(
                TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association)))
            + "Id");
      }
      ASTCDAttribute attribute = builder.build();

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
            attribute, new StringHookPoint(" = -1L;"));
      }

      attrList.add(attribute);
    }

    return attrList;
  }

  private Collection<? extends ASTCDMethod> getSetIDMethods(ASTCDClass proxyClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = new ArrayList<>();
    for (CDAssociationSymbol association : symbolTable.get()
        .getAllNonDerivedAssociationsForClass(typeSymbol.getName())) {
      // Add import
      String importString = TransformationUtils.getPackageName(getOutputAstRoot()) + "."
          + TransformationUtils.CLASSES_PACKAGE + "." +
          association.getTargetType().getName().toLowerCase() + ".*";
      TransformationUtils.addPropertyValue(proxyClass, CDClassBuilder.IMPORTS_PROPERTY,
          importString);

      if (CDAssociationUtil.isOptional(association)) {
        // Create additional set-methods
        String methodName = "get" + TransformationUtils
            .capitalize(CDAssociationUtil.getAssociationName(association)) + "Id";
        String paramType = "Optional<" + association.getTargetType().getName() + ">";
        String paramName = "o";
        ASTCDMethod setMethod = new CDMethodBuilder()
            .returnType("long")
            .addParameter(paramType, paramName)
            .name(methodName).build();

        getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), setMethod,
            new TemplateHookPoint("backend.data.proxy.GetTargetId", paramName));

        methods.add(setMethod);
      }
    }
    return methods;
  }

}
