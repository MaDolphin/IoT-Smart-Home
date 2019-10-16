/* (c) https://github.com/MontiCore/monticore */

package frontend.data.validator;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;
import frontend.common.FrontendTransformationUtils;
import frontend.data.DTOCreator;
import ocl.monticoreocl.ocl._ast.ASTOCLInvariant;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.*;

public class FrontendValidatorCreator extends CreateTrafo {

  private static final String VALIDATOR = "Validator";

  public static final String SUBPACKAGE = "validators";

  public static final String FILE_EXTENSION = "validator";

  private Map<String, List<ASTCDMethod>> constraints = new HashMap<>();

  public FrontendValidatorCreator() {
    super(VALIDATOR);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass validatorClass, ASTCDClass clazz,
      CDTypeSymbol type) {
    ASTCDConstructor constructor = new CDConstructorBuilder().Private().setName(validatorClass.getName()).build();
    return Lists.newArrayList(constructor);
  }

  @Override
  protected List<ASTCDMethod> createStaticMethods(ASTCDClass validatorClass, ASTCDClass clazz,
                                                  CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = new ArrayList<>();
    methods.addAll(createAllOCLConstraintMethods(clazz));
    methods.addAll(createIsValidMethods(clazz));
    return methods;
  }

  @Override
  protected List<String> getInterfaceNames(CDTypeSymbol typeSymbol) {
    return new ArrayList<>();
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("{ ValidationError } from '@shared/generic-form/validator'");
    imports.add("{ ValidatorUtils } from '@shared/architecture/data/validators/validatorutils'");
    imports.add("{ isBefore } from '@shared/architecture/data/validators/asserts/isBefore'");
    imports.add("{ isAfter } from '@shared/architecture/data/validators/asserts/isAfter'");
    imports.add("{ isAfter } from '@shared/architecture/data/validators/asserts/isAfter'");
    imports.add("{ date2String } from '@shared/architecture/data/validators/date2String'");

    Optional<String> hwcImport = FrontendTransformationUtils
        .getImportForHWC(typeSymbol.getName() + VALIDATOR, handcodePath, FILE_EXTENSION, SUBPACKAGE,
            Optional.of(typeSymbol.getName().toLowerCase()));
    hwcImport.ifPresent(imports::add);

    String fileName = Joiners.DOT.join(typeSymbol.getName().toLowerCase(), DTOCreator.FILEEXTENION);
    if (TransformationUtils
        .existsHandwrittenDotFile(fileName, DTOCreator.SUBPACKAGE, handcodePath.get(),
            TransformationUtils.DOT_TYPESCRIPT_FILE_EXTENSION)) {
      imports.add(
          "{" + typeSymbol.getName() + DTOCreator.DTO + "} from '../../../src/app/shared/architecture/data/dtos/"
              + typeSymbol.getName().toLowerCase() + ".dto'");
    }
    else {
      imports.add(
          "{" + typeSymbol.getName() + DTOCreator.DTO + "} from '../dtos/"
              + typeSymbol.getName().toLowerCase() + ".dto'");
    }
    return imports;
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
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    String typeName = typeSymbol.getName();
    ASTCDClass addedClass = new CDClassBuilder().setName(typeName + VALIDATOR).build();
    addedClass.setModifier(CD4AnalysisMill.modifierBuilder().build());
    addedClass.setSymbol(typeSymbol);
    TransformationUtils.setProperty(addedClass, SUBPACKAGE_PROPERTY, SUBPACKAGE);
    TransformationUtils.setProperty(addedClass, FILEEXTENSION_PROPERTY, FILE_EXTENSION);
    TransformationUtils.setProperty(addedClass, FILENAME_PROPERTY, typeName.toLowerCase());
    getOutputAst().getCDClassList().add(addedClass);
    return Optional.of(addedClass);
  }

  //----------- PRIVATE  METHODS -------------------------------------

  private List<ASTCDMethod> createIsValidMethods(ASTCDClass clazz) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (CDFieldSymbol field : symbolTable.get().getVisibleAttributesInHierarchy(clazz.getName())) {
      methodList.add(createIsAttributeValid(clazz, field));
    }
    return methodList;
  }

  private ASTCDMethod createIsAttributeValid(ASTCDClass clazz, CDFieldSymbol field) {
    String validatorClassName = clazz.getName() + VALIDATOR;
    String type = TypesPrinter.printType(getFrontendType(field.getType().getStringRepresentation()));
    ASTCDMethod method = new CDMethodBuilder().Public().Static()
        .addParameter(type, "val")
        .name("is" + TransformationUtils.capitalize(field.getName()) + "Valid").build();
    List<ASTCDMethod> oclConstraints = constraints.getOrDefault(field.getName(), new ArrayList<>());
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("frontend.data.validator.IsValidAttribute", validatorClassName, field, oclConstraints));
    return method;
  }

  //----------- OCL Constraints ----------------------------------------------------------------------------------------

  private List<ASTCDMethod> createAllOCLConstraintMethods(ASTCDClass domainClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    this.constraints.clear();

    List<ASTOCLInvariant> invariants = getOCLContextsForDomainClass(domainClass);

    for (ASTOCLInvariant inv : invariants) {
      Optional<ASTCDMethod> method = createConstraintMethod(inv, domainClass);
      method.ifPresent(methodList::add);
    }

    return methodList;
  }

  private List<ASTOCLInvariant> getOCLContextsForDomainClass(ASTCDClass domainClass) {
    if (this.ocl.isPresent()) {
      return this.ocl.get().getOCLConstraintList().stream()
          .filter(c -> c instanceof ASTOCLInvariant)
          .map(c -> (ASTOCLInvariant) c)
          .filter(c -> domainClass.getName().equals(TypesPrinter.printType(c.getOCLClassContext().getOCLContextDefinitionList().get(0).getType()))
          || symbolTable.get().getAllSuperClasses(domainClass.getName()).stream().map(CDTypeSymbol::getName).collect(Collectors.toList()).contains(TypesPrinter.printType(c.getOCLClassContext().getOCLContextDefinitionList().get(0).getType())))
          .collect(Collectors.toList());
    }
    return Lists.newArrayList();
  }

  private Optional<ASTCDMethod> createConstraintMethod(ASTOCLInvariant inv, ASTCDClass domainClass) {
    String methodName = inv.getNameOpt().orElse("checkConstraint" + inv.hashCode());

    OCL2TypeScriptPrettyPrinter ocl2TS = new OCL2TypeScriptPrettyPrinter(new IndentPrinter(), this.symbolTable.get(), domainClass.getName());
    List<String> expressions = new ArrayList<>();

    for (ASTExpression expression : inv.getStatementsList()) {
      expressions.add(ocl2TS.prettyprint(expression));
    }
    String message = ocl2TS.getMessage();

    List<ASTCDParameter> parameters = ocl2TS.getParameters();
    List<ASTCDParameter> assocParameters = ocl2TS.getParametersForAssocs();

    if (!assocParameters.isEmpty()) {
      return Optional.empty();
    }

    ASTCDMethod method = createDefaultMethod(methodName, parameters);

    if (parameters.size() > 0) {
      String attributeName = parameters.size() == 1 ? parameters.get(0).getName() : "";
      addMethodToAttributeConstraints(attributeName, method);

      getGlex().replaceTemplate(
          CoreTemplate.EMPTY_METHOD.toString(),
          method,
          new TemplateHookPoint("frontend.data.validator.checkOCLConstraint", expressions, message));

      return Optional.of(method);
    }
    return Optional.empty();
  }

  private ASTCDMethod createDefaultMethod(String name, List<ASTCDParameter> parameters) {
    CDMethodBuilder methodBuilder = new CDMethodBuilder()
        .Public()
        .Static()
        .name(name);

    for (ASTCDParameter param : parameters) {
      methodBuilder.addCDParameter(param);
    }

    return methodBuilder.build();
  }

  private void addMethodToAttributeConstraints(String attributeName, ASTCDMethod method) {
    if (!constraints.containsKey(attributeName)) {
      constraints.put(attributeName, new ArrayList<>());
    }
    constraints.get(attributeName).add(method);
  }


  public static ASTType getFrontendType(String typeName) {
    CDSimpleReferenceBuilder builder = new CDSimpleReferenceBuilder();
    TypeHelper typeHelper = new TypeHelper();
    if (typeHelper.isOptionalString(typeName)) {
      builder.name("string | null");
    }
    else if (typeHelper.isOptionalZonedDateTime(typeName)) {
      builder.name("Date | string | null");
    }
    else if (typeHelper.isString(typeName)) {
      builder.name("string");
    }
    else if (typeHelper.isZonedDateTime(typeName)) {
      builder.name("Date | string");
    }
    else if (typeHelper.isListOfStrings(typeName)) {
      builder.name("string []");
    }
    else if (typeHelper.isNumber(typeName)) {
      builder.name("number");
    }
    else if (typeHelper.isBoolean(typeName)) {
      builder.name("boolean");
    }
    else {
      builder.name("any");
    }
    return builder.build();
  }
}
