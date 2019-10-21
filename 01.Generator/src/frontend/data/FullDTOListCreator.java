/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */

package frontend.data;

import backend.common.CoreTemplate;
import backend.dtos.DTOListCreator;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import frontend.common.FrontendTransformationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.*;
import static frontend.common.FrontendTransformationUtils.createJsonMemberStereotype;

public class FullDTOListCreator extends CreateTrafo {

  public static final String SUBPACKAGE = "dtos";

  public static final String FILEEXTENION = "fulldtolist";

  public static final String FULLDTOLIST = "FullDTOList";

  private TypeHelper typeHelper = new TypeHelper();

  public FullDTOListCreator() {
    super(FULLDTOLIST);
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
    attributes.add(getDTOListAttribute(typeSymbol));
    return attributes;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass,
      ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = new ArrayList<>();
    methods.add(createTransformMethod(domainClass, typeSymbol));
    methods.add(getGetDataMethod(domainClass, typeSymbol));
    methods.add(getGetAllMethod(domainClass, typeSymbol));
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
        .getImportCheckHWC(typeSymbol.getName() + TransformationUtils.GETALL_CMD, handcodePath, CommandGetAllCreator.FILEEXTENSION, CommandCreator.SUBPACKAGE,
            Optional.of(typeSymbol.getName().toLowerCase())));

    Optional<String> hwcImport = FrontendTransformationUtils
        .getImportForHWC(typeSymbol.getName() + FULLDTOLIST, handcodePath, FILEEXTENION, SUBPACKAGE,
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

    String superClassName;
    if (domainClass.getSuperclassOpt().isPresent()) {
      superClassName = TypesPrinter.printType(domainClass.getSuperclass()) + FULLDTOLIST;
    }
    else {
      superClassName = "DTO" + "<" + typeName + FULLDTOLIST + ">";
    }

    ASTCDClass addedClass = new CDClassBuilder().superclass(superClassName)
        .setName(typeName + FULLDTOLIST).build();

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
        .addParameter(extendedClass.getName(), "dto?")
        .setName(extendedClass.getName())
        .build();

    boolean useTypeof = true;

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new TemplateHookPoint("frontend.data.DTOConstructor", attributes, true, TransformationUtils.removeTOPExtensionIfExistent(extendedClass.getName()), useTypeof));
    return Optional.of(constructor);
  }

  private ASTCDAttribute getDTOListAttribute(CDTypeSymbol typeSymbol) {
    String frontendType = typeSymbol.getName() + FULLDTOLIST;
    String name = "dto";
    boolean isRequired = true;
    ASTCDAttribute attr = new CDAttributeBuilder().Public()
        .type(frontendType + FrontendTransformationUtils.ARRAY)
        .setName(name).build();
    attr.getModifier()
        .setStereotype(
            createJsonMemberStereotype(name,
                frontendType,
                true,
                true));

    return attr;
  }

  private ASTCDMethod createTransformMethod(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder().Public()
        .name("transform").build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("frontend.data.DTOListTransform", domainClass.getSuperclassOpt().isPresent()));
    return method;
  }

  private ASTCDMethod getGetDataMethod(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder()
        .name("getData")
        .returnType(typeSymbol.getName() + FULLDTOLIST).Public().build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("frontend.data.DTOgetData",
            typeSymbol.getName()));
    return method;
  }

  private ASTCDMethod getGetAllMethod(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder().Static()
        .name("getAll")
        .addParameter("CommandManager", "commandManager")
        .returnType("Promise<" + domainClass.getName() + DTOListCreator.DTOLIST + ">").Public().build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("frontend.data.DTOGetAll",
            typeSymbol.getName(), typeSymbol.getName() + DTOListCreator.DTOLIST));
    return method;
  }

}
