/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package frontend.data;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import frontend.common.FrontendTransformationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.*;
import static common.util.TransformationUtils.addPropertyValue;
import static frontend.common.FrontendTransformationUtils.createJsonMemberStereotype;

public class FullDTOCreator extends CreateTrafo {

  public static final String SUBPACKAGE = "dtos";

  public static final String FILEEXTENION = "fulldto";

  public static final String FULLDTO = "FullDTO";

  private TypeHelper typeHelper = new TypeHelper();

  public FullDTOCreator() {
    super(FULLDTO);
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
    attributes.addAll(getAttributesForAssociations(typeSymbol));
    return attributes;
  }


  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass,
                                            ASTCDClass domainClass,
                                            CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = new ArrayList<>();
    methods.add(createTransformMethod(domainClass, typeSymbol));
    methods.add(getGetDataMethod(domainClass, typeSymbol));
    methods.add(getCreateMethod(domainClass, typeSymbol));
    methods.add(getUpdateMethod(domainClass, typeSymbol));
    return methods;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("{JsonMember, JsonObject, TypedJSON} from '@upe/typedjson'");
    imports.add(
            "{CommandResult} from '../../../src/app/shared/architecture/command/response/command.result'");

    imports.add("{ replaceMinusOnes } from '../../../src/app/shared/architecture/data/utils/utils'");

    imports.add(
        "{DTO, IDTO} from '../../../src/app/shared/architecture/data/aggregates/dto'");
    imports.add("{ CommandManager } from '@shared/architecture/command/rte/command.manager'");
    imports.add("{ IdDTO } from '@shared/architecture/command/aggregate/id.dto'");

    imports.add(FrontendTransformationUtils
        .getImportCheckHWC(typeSymbol.getName() + TransformationUtils.CREATE_CMD, handcodePath, "create", CommandCreator.SUBPACKAGE,
            Optional.of(typeSymbol.getName().toLowerCase())));
    imports.add(FrontendTransformationUtils
        .getImportCheckHWC(typeSymbol.getName() + TransformationUtils.UPDATE_CMD, handcodePath, "update", CommandCreator.SUBPACKAGE,
            Optional.of(typeSymbol.getName().toLowerCase())));

    Optional<String> hwcImport = FrontendTransformationUtils
            .getImportForHWC(typeSymbol.getName() + FULLDTO, handcodePath, FILEEXTENION, SUBPACKAGE,
                    Optional.of(typeSymbol.getName().toLowerCase()));
    if (hwcImport.isPresent()) {
      imports.add(hwcImport.get());
    }
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
    String superClassName = typeName + DTOCreator.DTO;
    ASTCDClass addedClass = new CDClassBuilder().superclass(superClassName)
            .setName(typeName + FULLDTO).build();
    String qualifiedName = FrontendTransformationUtils
            .getImportCheckHWC(superClassName, handcodePath, DTOCreator.FILEEXTENION,
                    SUBPACKAGE, Optional
                            .of(typeName.toLowerCase()));
    addPropertyValue(addedClass, CompilationUnit.IMPORTS_PROPERTY, qualifiedName);

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

  //----------- PRIVATE  METHODS -------------------------------------

  private Optional<ASTCDConstructor> createCopyConstructor(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol type) {
    List<ASTCDAttribute> attributes = createAttributes(extendedClass, domainClass, type);

    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .addParameter(extendedClass.getName() + " | " + type.getName() + DTOCreator.DTO, "dto?")
        .setName(extendedClass.getName())
        .build();

    boolean useTypeof = true;

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new TemplateHookPoint("frontend.data.DTOConstructor", attributes, true, TransformationUtils.removeTOPExtensionIfExistent(extendedClass.getName()), useTypeof));
    return Optional.of(constructor);
  }

  private Collection<? extends ASTCDAttribute> getAttributesForAssociations(CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attrList = new ArrayList<>();
    List<CDAssociationSymbol> assocs = typeSymbol.getAllAssociations();
    // get all visible parameters
    for (CDAssociationSymbol assoc : assocs) {
      String frontendType;
      String name;
      boolean isRequired = true;
      if (CDAssociationUtil.isMultiple(assoc)) {
        frontendType = FrontendTransformationUtils.NUMERICARRAY_FRONTEND;
        name = CDAssociationUtil.getAssociationName(assoc) + "List";
      } else {
        isRequired = !CDAssociationUtil.isOptional(assoc);
        frontendType = FrontendTransformationUtils.NUMERIC_FRONTEND;
        name = CDAssociationUtil.getAssociationName(assoc);
      }
      ASTCDAttribute attr = new CDAttributeBuilder().Public()
              .type(frontendType)
              .setName(name).build();
      attr.getModifier()
              .setStereotype(
                      createJsonMemberStereotype(name,
                              frontendType,
                              isRequired,
                              CDAssociationUtil.isMultiple(assoc)));

      attrList.add(attr);
    }

    return attrList;
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
    ASTCDMethod method = new CDMethodBuilder()
            .name("getData")
            .returnType(typeSymbol.getName() + FULLDTO).Public().build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("frontend.data.DTOgetData",
                    typeSymbol.getName()));
    return method;
  }

  private ASTCDMethod getCreateMethod(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder()
        .name("create")
        .addParameter("CommandManager", "commandManager")
        .returnType("Promise<IdDTO>").Public().build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("frontend.data.DTOCreate",
            typeSymbol.getName()));
    return method;
  }

  private ASTCDMethod getUpdateMethod(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder()
        .name("update")
        .addParameter("CommandManager", "commandManager")
        .returnType("Promise<IdDTO>").Public().build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("frontend.data.DTOUpdate",
            typeSymbol.getName()));
    return method;
  }

}
