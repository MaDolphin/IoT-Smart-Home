package backend.dtos;

import backend.common.CoreTemplate;
import backend.coretemplates.association.AssociationNameUtil;
import com.google.common.collect.Lists;
import common.TrafoForAggregateModels;
import common.util.*;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;
import static common.util.TransformationUtils.setProperty;

public class FullDtoCreator extends TrafoForAggregateModels {

  private TypeHelper typeHelper = new TypeHelper();

  public static final String DTO = "FullDTO";

  public FullDtoCreator() {
    super(DTO);
  }

  public FullDtoCreator(String o) {
    super(o);
  }

  @Override
  // This trafo creates an additional class for the aggregate class
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
                                                          CDTypeSymbol typeSymbol) {
    String aggregatesPackage = TransformationUtils.getPackageName(getOutputAstRoot()) + "."
            + TransformationUtils.DTOS_PACKAGE + "." +
            domainClass.getName().toLowerCase() + ".*";

    String className = typeSymbol.getName() + suffix;
    CDClassBuilder clazzBuilder = new CDClassBuilder();
    ASTCDClass addedClass = clazzBuilder.Public()
            .superclass(typeSymbol.getName() + DtoForAggregatesCreator.DTO)
            .subpackage(aggregatesPackage).setName(className).build();
    addedClass.setSymbol(typeSymbol);

    setProperty(addedClass, SUBPACKAGE_PROPERTY, TransformationUtils.DTOS_PACKAGE);
    getOutputAst().getCDClassList().add(addedClass);

    return Optional.of(addedClass);
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("java.util.ArrayList");
    imports.add("java.util.List");
    imports.add("java.util.Optional");
    imports.add("java.util.stream.Collectors");
    imports.add("de.macoco.be.util.DAOLib");
    imports.add("javax.persistence.EntityNotFoundException");
    imports.add("javax.xml.bind.ValidationException");
    imports.add("de.macoco.be.authz.util.SecurityHelper");
    return imports;
  }

  @Override
  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass,
                            CDTypeSymbol typeSymbol) {
    super.addImports(extendedClass, domainClass, typeSymbol);
    TransformationUtils
            .addStarImportForCDType(extendedClass, domainClass.getName(), getAstRoot());
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass handledClass, ASTCDClass clazz,
                                                  CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    attributes.addAll(getAttributesForAssociations(typeSymbol));
    return attributes;
  }

  private Collection<? extends ASTCDAttribute> getAttributesForAssociations(CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attrList = new ArrayList<>();
    List<CDAssociationSymbol> assocs = typeSymbol.getAllAssociations();
    // get all visible parameters
    for (CDAssociationSymbol assoc : assocs) {
      String typeName;
      String name;
      if (CDAssociationUtil.isMultiple(assoc)) {
        typeName = "List<Long>";
        name = CDAssociationUtil.getAssociationName(assoc) + "List";
      } else {
        typeName = "Long";
        name = CDAssociationUtil.getAssociationName(assoc);
      }

      attrList.add(new CDAttributeBuilder().Protected()
              .type(typeName)
              .setName(name).build());
    }

    return attrList;
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass dtoClass, ASTCDClass domainClass,
                                                      CDTypeSymbol typeSymbol) {
    List<ASTCDConstructor> constructors = new ArrayList<>();

    // prepare for constructor
    List<ASTCDParameter> paramList = new ArrayList<>();
    String origName = typeSymbol.getName();
    paramList.add(
            CD4AnalysisMill.cDParameterBuilder().setName(TransformationUtils.uncapitalize(origName))
                    .setType(new CDSimpleReferenceBuilder().name(origName).build()).build());

    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
            .setName(dtoClass.getName())
            .setCDParameterList(paramList).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new TemplateHookPoint("backend.dtos.FullConstructorFromDataobject", TransformationUtils.removeTOPExtensionIfExistent(dtoClass.getName()), origName,
                     typeSymbol.getAllAssociations()));
    constructors.add(constructor);
    return constructors;
  }

  protected ASTCDConstructor createFullConstructor(ASTCDClass dtoClass, CDTypeSymbol type) {
    // prepare for constructor
    List<ASTCDParameter> paramList = new ArrayList<>();
    List<CDFieldSymbol> fields = TransformationUtils
            .excludeCollectionAttributes(symbolTable.get().getVisibleAttributes(type.getName()),
                    typeHelper);
    // get all visible parameters
    for (CDFieldSymbol field : fields) {
      String typeName = "";
      if (typeHelper.isOptional(field.getType())
              && !field.getType().getActualTypeArguments().isEmpty()) {
        typeName = field.getType().getActualTypeArguments().get(0).getType().getName();
      } else {
        typeName = field.getType().getStringRepresentation();
      }

      paramList.add(CD4AnalysisMill.cDParameterBuilder().setName(field.getName()).setType(
              new CDSimpleReferenceBuilder().name(typeName).build())
              .build());
    }

    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
            .setName(type.getName() + DTO)
            .setCDParameterList(paramList).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new TemplateHookPoint("backend.dtos.DtoConstructor", TransformationUtils.removeTOPExtensionIfExistent(dtoClass.getName()), fields));
    return constructor;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass dtoClass, ASTCDClass clazz,
                                            CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    methods.add(createToStringMethod(dtoClass, typeSymbol));
    methods.add(createToBuilderMethod(typeSymbol));
    methods.addAll(addMethods(typeSymbol));
    return methods;
  }

  private List<ASTCDMethod> addMethods(CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> additionalMethods = Lists.newArrayList();

    for (CDAssociationSymbol assoc : typeSymbol.getAllAssociations()) {
      additionalMethods.add(createDefaultSet(assoc));
      additionalMethods.addAll(createDefaultGet(assoc));
    }

    return additionalMethods;
  }

  private List<ASTCDMethod> createDefaultGet(CDAssociationSymbol assoc) {
    List<ASTCDMethod> createdMethods = new ArrayList<>();
    String attrName = CDAssociationUtil.getAssociationName(assoc);
    String methodName = GetterSetterHelper.GET_PREFIX + TransformationUtils.capitalize(attrName);
    String returnType;
    if (CDAssociationUtil.isMultiple(assoc)) {
      returnType = "List<Long>";
      attrName += "List";
      methodName = AssociationNameUtil.getGetAllMethodName(assoc).orElse("");
    } else if (CDAssociationUtil.isOptional(assoc)) {
      returnType = "Optional<Long>";
    } else {
      returnType = "Long";
    }

    // add getter
    ASTCDMethod getMethod = new CDMethodBuilder().Public()
            .returnType(returnType)
            .name(methodName)
            .build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), getMethod,
            new TemplateHookPoint("backend.data.dataclass.accessmethods.Get",
                    attrName, CDAssociationUtil.isOptional(assoc)));

    createdMethods.add(getMethod);
    return createdMethods;
  }

  private ASTCDMethod createDefaultSet(CDAssociationSymbol assoc) {
    String attrName = CDAssociationUtil.getAssociationName(assoc);
    String methodName = GetterSetterHelper.SET_PREFIX + TransformationUtils.capitalize(attrName);
    String paramType;

    if (CDAssociationUtil.isMultiple(assoc)) {
      paramType = "List<Long>";
      attrName += "List";
      methodName = AssociationNameUtil.getSetAllMethodName(assoc).orElse("");
    } else if (CDAssociationUtil.isOptional(assoc)) {
      paramType = "Optional<Long>";
    } else {
      paramType = "Long";
    }

    ASTCDMethod setMethod = new CDMethodBuilder().Public()
            .addParameter(paramType, "o")// attrName
            .name(methodName).build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), setMethod,
            new TemplateHookPoint("backend.data.dataclass.accessmethods.Set",
                "Long",
                    attrName,
                    "o", CDAssociationUtil.isOptional(assoc)));

    return setMethod;
  }

  //------------------------------------------------- Methods -------------------------------------------------

  private ASTCDMethod createToStringMethod(ASTCDClass proxy, CDTypeSymbol typeSymbol) {
    ASTCDMethod toStringMethod = new CDMethodBuilder().Public()
            .name("toString")
            .returnType("String").build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), toStringMethod,
            new TemplateHookPoint("backend.dtos.FullToString",
                    typeSymbol.getAllAssociations()));
    return toStringMethod;
  }

  private ASTCDMethod createToBuilderMethod(CDTypeSymbol typeSymbol) {
    ASTCDMethod builderMethod = new CDMethodBuilder().Public()
            .name("toBuilder")
            .addParameter("DAOLib", "daoLib")
            .addParameter("SecurityHelper", "securityHelper")
            .exceptions("EntityNotFoundException")
            .exceptions("ValidationException")
            .returnType(typeSymbol.getName() + "Builder").build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), builderMethod,
            new TemplateHookPoint("backend.dtos.FullToBuilder", typeSymbol.getName(),
                    typeSymbol.getAllAssociations()));
    return builderMethod;
  }

}
