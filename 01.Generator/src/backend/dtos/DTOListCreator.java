/* (c) https://github.com/MontiCore/monticore */
package backend.dtos;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import java.util.Collections;
import common.TrafoForAggregateModels;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DTOListCreator extends TrafoForAggregateModels {

  public static final String DTO = "FullDTO";
  public static final String DTOLIST = "FullDTOList";

  public DTOListCreator() {
    super(DTOLIST);
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
    createConstructorFromObject(clazz, domainClass, typeSymbol).ifPresent(constructors::add);
    return constructors;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("de.montigem.be.dtos.rte.DTO");
    imports.add("java.util.stream.Collectors");
    imports.add(Joiners.DOT.join(TransformationUtils.getPackageName(getOutputAstRoot()),
        TransformationUtils.CLASSES_PACKAGE, typeSymbol.getName().toLowerCase(),
        typeSymbol.getName()));
    return imports;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass dtoClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();

    methods.add(createDefaultGet(new CDAttributeBuilder().Protected()
        .setType(CDSimpleReferenceBuilder.CollectionTypes.createList(clazz.getName() + DTO)).setName("dtos")
        .build()));

    methods.add(createDefaultSet(new CDAttributeBuilder().Protected()
        .setType(CDSimpleReferenceBuilder.CollectionTypes.createList(clazz.getName() + DTO)).setName("dtos")
        .build()));

    methods.add(createToStringMethod(dtoClass, typeSymbol));
    return methods;
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();

    attributes.add(new CDAttributeBuilder().Protected()
        .setType(CDSimpleReferenceBuilder.CollectionTypes.createList(clazz.getName() + DTO)).setName("dtos")
        .build());

    if (!handledClass.getSuperclassOpt().isPresent()) {
      attributes.add(new CDAttributeBuilder().Private()
          .setType(CDSimpleReferenceBuilder.PrimitiveTypes.createLong()).setName("id")
          .build());
    }
    // attributes.addAll(getAttributesForFields(clazz, typeSymbol));
    return attributes;
  }

  @Override
  protected Optional<String> getSuperclassName(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    return Optional.of("DTO");
  }

  //----------- PRIVATE  METHODS -------------------------------------

  private Optional<ASTCDConstructor> createFullConstructor(ASTCDClass clazz, ASTCDClass domainClass, CDTypeSymbol type) {

    // prepare for constructor
    List<ASTCDAttribute> localAttributes = new ArrayList<>();
    localAttributes.add(new CDAttributeBuilder().Protected()
        .setType(CDSimpleReferenceBuilder.CollectionTypes.createList(clazz.getName())).setName("dtos")
        .build());

    List<ASTCDParameter> constructorArguments = new ArrayList<>();

    Optional<ASTCDParameter> id = addIdAttribute(type);
    if (id.isPresent()) {
      constructorArguments.add(id.get());
    }

    constructorArguments.add(CD4AnalysisMill.cDParameterBuilder().setName("dtos").setType(
        CDSimpleReferenceBuilder.CollectionTypes.createList(domainClass.getName() + DTO))
        .build());

    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .setName(type.getName() + DTOLIST)
        .setCDParameterList(constructorArguments).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new TemplateHookPoint("backend.dtos.DTOListConstructor", clazz.getName(), Collections.emptyList(),
            localAttributes));
    return Optional.of(constructor);
  }

  private Optional<ASTCDConstructor> createConstructorFromObject(ASTCDClass clazz, ASTCDClass domainClass, CDTypeSymbol type) {
    // prepare for constructor
    List<ASTCDAttribute> attributes = new ArrayList<>();

    List<ASTCDParameter> constructorArguments = new ArrayList<>();

    constructorArguments.add(CD4AnalysisMill.cDParameterBuilder().setName("dtos").setType(
        CDSimpleReferenceBuilder.CollectionTypes.createList(domainClass.getName()))
        .build());

    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .setName(type.getName() + DTOLIST)
        .setCDParameterList(constructorArguments).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new TemplateHookPoint("backend.dtos.ConstructorFromDataobjectList", clazz.getName(), domainClass.getName() + DTO, Collections.emptyList(),
            attributes));
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

}
