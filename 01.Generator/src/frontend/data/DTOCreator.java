package frontend.data;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.literals.literals._ast.ASTStringLiteral;
import de.monticore.literals.literals._ast.LiteralsMill;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import frontend.common.FrontendTransformationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.*;
import static common.util.TransformationUtils.*;
import static frontend.common.FrontendTransformationUtils.*;

public class DTOCreator extends CreateTrafo {

  public static final String SUBPACKAGE = "dtos";

  public static final String FILEEXTENION = "dto";

  public static final String DTO = "DTO";

  protected TypeHelper typeHelper = new TypeHelper();

  public DTOCreator() {
    super(DTO);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDConstructor> constructors = Lists.newArrayList();
    createCopyConstructor(extendedClass, domainClass, typeSymbol).ifPresent(constructors::add);
    return constructors;
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass extendedClass, ASTCDClass domainClass,
                                                  CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    /*if (!domainClass.getSuperclassOpt().isPresent()) {
      attributes.add(createIDAttribute());
    }*/
    attributes.addAll(getAttributesForFields(extendedClass, domainClass, typeSymbol));
    return attributes;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass,
                                            ASTCDClass domainClass,
                                            CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = new ArrayList<>();
    methods.add(createTransformMethod(domainClass, typeSymbol));
    methods.add(getGetDataMethod(domainClass, typeSymbol));
    methods.add(getGetByIdMethod(domainClass, typeSymbol));
    return methods;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add(
            "{DTO, IDTO} from '../../../src/app/shared/architecture/data/aggregates/dto'");
    imports.add("{JsonMember, JsonObject, TypedJSON} from '@upe/typedjson'");
    imports.add("{ CommandManager } from '@shared/architecture/command/rte/command.manager'");
    imports.add("{ IdDTO } from '@shared/architecture/command/aggregate/id.dto'");

    imports.add("{ replaceMinusOnes } from '../../../src/app/shared/architecture/data/utils/utils'");

    Optional<String> hwcImport = FrontendTransformationUtils
            .getImportForHWC(typeSymbol.getName() + DTO, handcodePath, FILEEXTENION, SUBPACKAGE,
                    Optional.of(typeSymbol.getName().toLowerCase()));
    if (hwcImport.isPresent()) {
      imports.add(hwcImport.get());
    }

    imports.add(FrontendTransformationUtils
            .getImportCheckHWC(typeSymbol.getName() + TransformationUtils.GETBYID_CMD, handcodePath, CommandGetByIdCreator.FILEEXTENSION, CommandCreator.SUBPACKAGE,
                    Optional.of(typeSymbol.getName().toLowerCase())));

    return imports;
  }

  @Override
  protected Optional<ASTCDStereotype> getStereotype(ASTCDType type) {
    // Add annotation JsonObject()
    ASTCDStereotype stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
    ASTCDStereoValue value = CD4AnalysisMill.cDStereoValueBuilder().setName("@JsonObject()").build();
    stereotype.getValueList().add(value);
    return Optional.of(stereotype);
  }

  @Override
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
                                                          CDTypeSymbol typeSymbol) {
    String typeName = typeSymbol.getName();
    String superClassName = "";
    if (domainClass.getSuperclassOpt().isPresent()) {
      superClassName = TypesPrinter.printType(domainClass.getSuperclass()) + DTO;
    } else {
      superClassName = "DTO" + "<" + typeName + DTO + ">";
    }
    ASTCDClass addedClass = new CDClassBuilder().superclass(superClassName)
            .setName(typeName + DTO).build();
    if (domainClass.getSuperclassOpt().isPresent()) {
      String qualifiedName = FrontendTransformationUtils
              .getImportCheckHWC(superClassName, handcodePath, FILEEXTENION,
                      SUBPACKAGE, Optional
                              .of(typeHelper.getTypeFromTypeWithSuffix(superClassName, DTOCreator.DTO)
                                      .toLowerCase()));
      addPropertyValue(addedClass, CompilationUnit.IMPORTS_PROPERTY, qualifiedName);
    }
    addedClass.setModifier(CD4AnalysisMill.modifierBuilder().build());
    addedClass.setSymbol(typeSymbol);
    TransformationUtils.setProperty(addedClass, SUBPACKAGE_PROPERTY, SUBPACKAGE);
    TransformationUtils.setProperty(addedClass, FILEEXTENSION_PROPERTY, FILEEXTENION);
    TransformationUtils.setProperty(addedClass, FILENAME_PROPERTY, typeName.toLowerCase());
    getOutputAst().getCDClassList().add(addedClass);
    return Optional.of(addedClass);
  }

  @Override
  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass,
                            CDTypeSymbol typeSymbol) {
    checkArgument(symbolTable.isPresent());
    // Clear all CD imports
    this.getOutputAstRoot().setImportStatementList(new ArrayList<>());

    getImports(typeSymbol).forEach(i -> TransformationUtils
            .addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, i));
  }

  @Override
  protected Optional<ASTCDEnum> getOrCreateExtendedEnum(ASTCDEnum domainEnum, CDTypeSymbol typeSymbol) {
    String typeName = typeSymbol.getName();
    ASTCDEnum addedEnum = new CDEnumBuilder()
            .setName(typeName + DTO).build();
    TransformationUtils.setProperty(addedEnum, SUBPACKAGE_PROPERTY, SUBPACKAGE);
    TransformationUtils.setProperty(addedEnum, FILEEXTENSION_PROPERTY, FILEEXTENION);
    TransformationUtils.setProperty(addedEnum, FILENAME_PROPERTY, typeName.toLowerCase());
    for (ASTCDEnumConstant enumConst : domainEnum.getCDEnumConstantList()) {
      ASTCDEnumConstantBuilder constBuilder = CD4AnalysisMill.cDEnumConstantBuilder();
      constBuilder.setName(enumConst.getName());
      String name;
      if (!enumConst.getCDEnumParameterList().isEmpty() &&
              (enumConst.getCDEnumParameter(0).getValue().getSignedLiteral() instanceof ASTStringLiteral)) {
        name = "\"" + ((ASTStringLiteral) enumConst.getCDEnumParameter(0).getValue().getSignedLiteral()).getSource() + "\"";
      } else {
        name = TransformationUtils.createEnumParameter(enumConst.getName());
      }
      ASTValue val = CD4AnalysisMill.valueBuilder().setSignedLiteral(LiteralsMill.stringLiteralBuilder().setSource(name).build()).build();
      addedEnum.addCDEnumConstant(constBuilder.addCDEnumParameter(CD4AnalysisMill.cDEnumParameterBuilder().setValue(val).build()).build());
    }
    getOutputAst().getCDEnumList().add(addedEnum);
    return Optional.of(addedEnum);
  }

  @Override
  protected Collection<ASTCDMethod> createMethods(ASTCDEnum extendedEnum, ASTCDEnum domainEnum, CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();

    // method getAllNames
    ASTCDMethod method = new CDMethodBuilder()
            .name("getAllNames")
            .returnType(FrontendTransformationUtils.STRINGARRAY_FRONTEND)
            .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("frontend.data.GetAllNames", extendedEnum));
    methods.add(method);

    // method getAllEntries
    method = new CDMethodBuilder()
            .name("getAllEntries")
            .returnType("EnumEntries[]")
            .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("frontend.data.GetAllEntries", extendedEnum));
    methods.add(method);

    // method getAllEntries
    method = new CDMethodBuilder()
            .name("getAllSelectOptions")
            .returnType("ISelectOptions[]")
            .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("frontend.data.GetAllSelectOptions", extendedEnum));
    methods.add(method);
    return methods;
  }

  @Override
  protected void addImports(ASTCDEnum extendedEnum, ASTCDEnum domainEnum, CDTypeSymbol typeSymbol) {
    TransformationUtils
            .addPropertyValue(extendedEnum, CompilationUnit.IMPORTS_PROPERTY,
                    "{ EnumEntries } from '@shared/architecture/data/utils/enum.entries'");
    TransformationUtils
            .addPropertyValue(extendedEnum, CompilationUnit.IMPORTS_PROPERTY,
                    "{ ISelectOptions  } from '@shared/generic-form/generic-form/decoretors/options'");
  }

//----------- PRIVATE  METHODS -------------------------------------

  private Optional<ASTCDConstructor> createCopyConstructor(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol type) {
    List<ASTCDAttribute> attributes = createAttributes(extendedClass, domainClass, type);

    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
            .addParameter(extendedClass.getName(), "dto?")
            .setName(extendedClass.getName())
            .build();

    boolean useTypeof = false;

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
            new TemplateHookPoint("frontend.data.DTOConstructor", attributes, true, TransformationUtils.removeTOPExtensionIfExistent(extendedClass.getName()), useTypeof));
    return Optional.of(constructor);
  }

  private List<ASTCDAttribute> getAttributesForFields(
          ASTCDClass extendedClass, ASTCDClass clazz,
          CDTypeSymbol typeSymbol) {

    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleAttributes(clazz.getName());
    fields.addAll(symbolTable.get().getDerivedAttributes(clazz.getName()));

    // get all visible parameters
    for (CDFieldSymbol field : fields) {
      Optional<CDTypeSymbol> cdType = symbolTable.get()
              .resolve(field.getType().getStringRepresentation());
      String typeName = field.getType().getStringRepresentation();
      String frontendType = "";
      boolean isRequired = !typeHelper.isGenericOptional(typeName) && !field.isDerived();
      if (cdType.isPresent() && cdType.get().isEnum()) {
        frontendType = STRING_FRONTEND;
      } else {
        frontendType = getFrontendType(typeName, cdType, symbolTable.get());
      }
      ASTCDAttribute attr = new CDAttributeBuilder().Public()
              .type(frontendType)
              .setName(field.getName()).build();

      Optional<ASTValue> value = TransformationUtils.getAttributeValue(field);
      if (value.isPresent()) {
        attr.setValue(value.get());
      } else {
        String defaultValue = typeHelper.isGenericList(typeName) ?
                FrontendTransformationUtils.ARRAY :
                getDefaultValueForType(frontendType);
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
                new StringHookPoint(
                        "= " + defaultValue + ";"));
      }
      attr.getModifier()
              .setStereotype(
                      createJsonMemberStereotype(field.getName(),
                              frontendType,
                              isRequired,
                              typeHelper.isGenericNotStringList(typeName)));

      if (typeHelper.isGenericList(typeName) && symbolTable.get().isTypeDefinedInModel(typeHelper.getFirstTypeArgumentOfList(typeName)) ) {
        addImportForAttributeType(typeHelper.getTypeFromTypeWithSuffix(frontendType, ARRAY),
                extendedClass, typeSymbol);
      } else if (typeHelper.isGenericOptional(typeName) && symbolTable.get().isTypeDefinedInModel(typeHelper.getFirstTypeArgumentOfOptional(typeName)) ) {
        addImportForAttributeType(frontendType, extendedClass, typeSymbol);
      } else if (cdType.isPresent() && !cdType.get().isEnum() && symbolTable.get().isTypeDefinedInModel(cdType.get())) {
        addImportForAttributeType(frontendType, extendedClass, typeSymbol);
      }

      attrList.add(attr);
    }
    return attrList;
  }

  private void addImportForAttributeType(String frontendType, ASTCDClass clazz,
                                         CDTypeSymbol typeSymbol) {
    String qualifiedName = "";
    String fileName = typeHelper.getTypeFromTypeWithSuffix(frontendType, DTOCreator.DTO);
    if (typeSymbol.getName().equals(fileName)) {
      // if the attribute type is the same as the dto type it doesn't need to be imported
      return;
    }
    if (isDTOType(symbolTable.get(), fileName)) {
      qualifiedName = FrontendTransformationUtils
              .getImportCheckHWC(frontendType, handcodePath, FILEEXTENION,
                      SUBPACKAGE, Optional.of(fileName.toLowerCase()));
    } else {
      qualifiedName = FrontendTransformationUtils
              .getImportCheckHWC(frontendType, handcodePath, ModelCreator.FILEEXTENSION,
                      ModelCreator.SUBPACKAGE, Optional.empty());
    }
    addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, qualifiedName);
  }

  private ASTCDAttribute createIDAttribute() {
    ASTCDAttribute id = new CDAttributeBuilder().Public()
            .type(NUMERIC_FRONTEND).setName("id").build();
    getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), id,
            new StringHookPoint(
                    "= 0;"));
    id.getModifier().setStereotype(
            createJsonMemberStereotype(id.getName(), NUMERIC_FRONTEND, true));
    return id;
  }

  private ASTCDMethod createTransformMethod(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = domainClass.getCDAttributeList();
    List<ASTCDAttribute> lists = new ArrayList<>();
    List<ASTCDAttribute> dtos = new ArrayList<>();
    List<ASTCDAttribute> opts = new ArrayList<>();

    for (ASTCDAttribute attr : attributes) {
      if (typeHelper.isGenericList(attr.printType())) {
        if (attr.getType() instanceof ASTSimpleReferenceType) {
          String type = typeHelper.getFirstTypeArgumentOfGeneric((ASTSimpleReferenceType) attr.getType());
          if (typeHelper.isReference(type) && !typeHelper.isZonedDateTime(type)) {
            if (!(symbolTable.get().resolve(type).isPresent() && symbolTable.get().resolve(type).get().isEnum())) {
              lists.add(attr);
            }
          }
        } else {
          throw new RuntimeException(attr.getType() + " is not a ASTSimpleReferenceType");
        }
      } else {
        if (typeHelper.isReference(attr.printType()) && !typeHelper.isZonedDateTime(attr.printType())) {
          if (typeHelper.isGenericOptional(attr.printType())) {
            String type = typeHelper.getFirstTypeArgumentOfGeneric((ASTSimpleReferenceType) attr.getType());
            if (typeHelper.isReference(type) && !typeHelper.isZonedDateTime(type)) {
              if (!(symbolTable.get().resolve(type).isPresent() && symbolTable.get().resolve(type).get().isEnum())) {
                opts.add(attr);
              }
            }
          } else {
            if (!(symbolTable.get().resolve(attr.printType()).isPresent() && symbolTable.get().resolve(attr.printType()).get().isEnum())) {
              dtos.add(attr);
            }
          }
        }
      }
    }
    ASTCDMethod method = new CDMethodBuilder().Public()
            .name("transform").build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("frontend.data.DTOTransform", domainClass.getSuperclassOpt().isPresent(), dtos, opts, lists));
    return method;
  }

  private ASTCDMethod getGetDataMethod(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    boolean existsHandwrittenFile = FrontendTransformationUtils.existsHandwrittenFile(typeSymbol.getName() + DTO, handcodePath, FILEEXTENION, SUBPACKAGE,
            Optional.of(typeSymbol.getName().toLowerCase()));

    ASTCDMethod method = new CDMethodBuilder()
            .name("getData")
            .returnType(typeSymbol.getName() + DTO + (existsHandwrittenFile ? TOP_EXTENSION : "")).Public().build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("frontend.data.DTOgetData",
                    typeSymbol.getName()));
    return method;
  }

  private ASTCDMethod getGetByIdMethod(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder()
            .name("getById")
            .addParameter("number", "objectId")
        .addParameter("CommandManager", "commandManager")
            .returnType("Promise<" + typeSymbol.getName() + "DTO>").Public().Static().build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("frontend.data.DTOGetById",
                    typeSymbol.getName()));
    return method;
  }

}
