/* (c) https://github.com/MontiCore/monticore */

package backend.data.validator;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;
import ocl.monticoreocl.ocl._ast.ASTOCLInvariant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidatorCreator extends CreateTrafo {

  public static final String VALIDATOR = "Validator";

  private static final String VALID ="Valid";
  
  private static final String DTO = "DTO";
  
  private static final String OPTIONAL_STRING = "Optional<String>";

  private Map<String, List<ASTCDMethod>> constraints = new HashMap<>();

  private Map<String, List<ASTCDMethod>> assocConstraints = new HashMap<>();

  private List<ASTCDMethod> constraintsWithMultipleParameters = new ArrayList<>();

  public ValidatorCreator() {
    super(VALIDATOR);

  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass validatorClass, ASTCDClass clazz,
      CDTypeSymbol type) {
    String name = validatorClass.getName();

    ASTCDConstructor constructor = new CDConstructorBuilder()
            .Protected()
            .setName(name)
            .build();

    return Lists.newArrayList(constructor);
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass validatorClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    List<ASTCDMethod> oclMethods = createAllOCLConstraintMethods(clazz);

    methods.add(createGetValidationErrorsMethod(typeSymbol));
    methods.add(createGetValidationErrorsMethodDTO(typeSymbol));
    methods.add(createIsNotNullMethod(clazz));
    methods.add(createIsNotNullMethodDTO(clazz));
    methods.addAll(createIsValidMethods(clazz));
    methods.addAll(oclMethods);

    return methods;
  }

  @Override
  protected List<ASTCDMethod> createStaticMethods(ASTCDClass validatorClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = new ArrayList<>();

    methods.add(createGetValidatorMethod(typeSymbol));
    methods.add(createSetValidatorMethod(typeSymbol));

    return methods;
  }

  @Override
  protected List<ASTCDAttribute> createStaticAttributes(ASTCDClass validatorClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    String type = typeSymbol + VALIDATOR;
    String name = VALIDATOR.toLowerCase();

    ASTCDAttribute attribute = new CDAttributeBuilder()
            .Private()
            .Static()
            .type(type)
            .setName(name)
            .build();

    return Lists.newArrayList(attribute);
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("java.util.ArrayList");
    imports.add("java.util.List");
    imports.add("java.util.Optional");
    imports.add("java.util.stream.Collectors");
    imports.add("com.google.common.base.Objects");
    imports.add("de.se_rwth.commons.logging.Log");
    imports.add("de.montigem.be.domain.rte.interfaces.IDomainValidator");
    imports.add("de.montigem.be.domain.rte.validator.ValidatorUtils");
    imports.add("static de.montigem.be.util.DomainHelper.getErrors");
    imports.add("de.montigem.be.domain.dtos.*");
    imports.add("javax.xml.bind.ValidationException");
    imports.add("static de.montigem.be.util.DomainHelper.date2String");
    return imports;
  }

  @Override
  protected List<String> getClassInterfaceNames(CDTypeSymbol typeSymbol) {
    List<String> interfaces = new ArrayList<>();
    if (!typeSymbol.getSuperClass().isPresent()) {
      interfaces.add("IDomainValidator<" + typeSymbol.getName() + ">");
    }
    return interfaces;
  }

  //----------- getValidationErrors(DOMAIN) ----------------------------------------------------------------------------

  private ASTCDMethod createGetValidationErrorsMethod(CDTypeSymbol typeSymbol) {
    String domainName = typeSymbol.getName();
    List<CDAssociationSymbol> allAssociationsForClass =
        symbolTable.get().getAllAssociationsForType(domainName);

    List<CDFieldSymbol> visibleSuperAttributes = symbolTable.get()
        .getVisibleNotDerivedAttributesInHierarchy(domainName);

    ASTCDMethod getValErrors = new CDMethodBuilder()
        .name("getValidationErrors")
        .Public()
        .addParameter(domainName, TransformationUtils.uncapitalize(domainName))
        .returnType(OPTIONAL_STRING)
        .build();

    List<ASTCDMethod> constraints = new ArrayList<>(constraintsWithMultipleParameters);
    
    getGlex().replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(),
        getValErrors,
        new TemplateHookPoint("backend.data.validator.getValidationErrors", domainName,
            visibleSuperAttributes, allAssociationsForClass, typeSymbol.getSuperClass().isPresent(), constraints));
    
    return getValErrors;
  }

  //----------- getValidationErrors(DTO) -------------------------------------------------------------------------------

  private ASTCDMethod createGetValidationErrorsMethodDTO(CDTypeSymbol typeSymbol) {
    String domainName = typeSymbol.getName();

    List<CDFieldSymbol> visibleSuperAttributes = symbolTable.get()
            .getVisibleNotDerivedAttributesInHierarchy(domainName);

    ASTCDMethod getValErrors = new CDMethodBuilder()
            .name("getValidationErrors")
            .Public()
            .addParameter(domainName + DTO, TransformationUtils.uncapitalize(domainName) + DTO)
            .returnType(OPTIONAL_STRING)
            .build();
    getGlex().replaceTemplate(
            CoreTemplate.EMPTY_METHOD.toString(),
            getValErrors,
            new TemplateHookPoint("backend.data.validator.getValidationErrorsForDTO", domainName,
                    visibleSuperAttributes, typeSymbol.getSuperClass().isPresent()));
    return getValErrors;
  }

  //----------- isNotNull(DOMAIN) --------------------------------------------------------------------------------------

  private ASTCDMethod createIsNotNullMethod(ASTCDClass clazz) {

    String paramType = clazz.getName();
    String paramName = TransformationUtils.uncapitalize(paramType);
    String methodName = "isNotNull";

    ASTCDMethod method = new CDMethodBuilder()
            .Public()
            .returnType(OPTIONAL_STRING)
            .name(methodName)
            .addParameter(paramType, paramName)
            .build();

    getGlex().replaceTemplate(
            CoreTemplate.EMPTY_METHOD.toString(),
            method,
            new TemplateHookPoint("backend.data.validator.IsNotNull", paramName));
    return method;
  }

  //----------- isNotNull(DTO) --------------------------------------------------------------------------------------

  private ASTCDMethod createIsNotNullMethodDTO(ASTCDClass clazz) {

    String paramType = clazz.getName() + DTO;
    String paramName = TransformationUtils.uncapitalize(paramType);
    String methodName = "isNotNull" + DTO;

    ASTCDMethod method = new CDMethodBuilder()
            .Public()
            .returnType(OPTIONAL_STRING)
            .name(methodName)
            .addParameter(paramType, paramName)
            .build();

    getGlex().replaceTemplate(
            CoreTemplate.EMPTY_METHOD.toString(),
            method,
            new TemplateHookPoint("backend.data.validator.IsNotNull", paramName));
    return method;
  }

  //----------- isAttributeValid(DOMAIN), isAttributeValid(DTO), isAttributeValid(VALUE), isAssociationValid(DOMAIN)----

  private List<ASTCDMethod> createIsValidMethods(ASTCDClass clazz) {
    List<ASTCDMethod> methodList = new ArrayList<>();

    for (CDFieldSymbol field : symbolTable.get().getVisibleAttributes(clazz.getName())) {

      methodList.add(createIsValidAttributeMethod(clazz, field));

      methodList.add(createIsValidAttributeMethodForDTO(clazz, field));

      methodList.add(createIsValidAttributeMethodForIsolatedValue(field));
    }
    methodList.addAll(createIsValidForAllAssociationsMethod(clazz));

    return methodList;
  }

  //----------- isAttributeValid(DOMAIN) -------------------------------------------------------------------------------

  private ASTCDMethod createIsValidAttributeMethod(ASTCDClass clazz, CDFieldSymbol field) {

    String paramType = clazz.getName();
    String paramName = TransformationUtils.uncapitalize(paramType);
    String methodName = "is" + TransformationUtils.capitalize(field.getName()) + VALID;

    ASTCDMethod method = new CDMethodBuilder()
            .Public()
            .returnType(OPTIONAL_STRING)
            .name(methodName)
            .addParameter(paramType, paramName)
            .build();
    
    getGlex().replaceTemplate(
            CoreTemplate.EMPTY_METHOD.toString(),
            method,
            new TemplateHookPoint("backend.data.validator.IsValidAttribute", field, paramName));
    return method;
  }

  //----------- isAttributeValid(DTO) ----------------------------------------------------------------------------------

  private ASTCDMethod createIsValidAttributeMethodForDTO(ASTCDClass clazz, CDFieldSymbol field) {

    String paramType = clazz.getName() + DTO;
    String paramName = TransformationUtils.uncapitalize(paramType);
    String methodName = "is" + TransformationUtils.capitalize(field.getName()) + VALID + DTO;

    ASTCDMethod method = new CDMethodBuilder()
            .Public()
            .returnType(OPTIONAL_STRING)
            .name(methodName)
            .addParameter(paramType, paramName)
            .build();

    getGlex().replaceTemplate(
            CoreTemplate.EMPTY_METHOD.toString(),
            method,
            new TemplateHookPoint("backend.data.validator.IsValidAttributeForDTO", symbolTable.get(), field, paramName));
    return method;
  }

  //----------- isAttributeValid(VALUE) ----------------------------------------------------------------------------------

  private ASTCDMethod createIsValidAttributeMethodForIsolatedValue(CDFieldSymbol field) {

    String paramType = field.getType().getStringRepresentation();
    String paramName = field.getName();
    String methodName = "is" + TransformationUtils.capitalize(paramName) + VALID + "Value";

    ASTCDMethod method = new CDMethodBuilder()
            .Public()
            .returnType(OPTIONAL_STRING)
            .name(methodName)
            .addParameter(paramType, paramName)
            .build();

    List<ASTCDMethod> attributeRelatedOCLConstraints = constraints.getOrDefault(field.getName(), new ArrayList<>());

    getGlex().replaceTemplate(
            CoreTemplate.EMPTY_METHOD.toString(),
            method,
            new TemplateHookPoint("backend.data.validator.IsValidAttributeForIsolatedValue",
                    field, attributeRelatedOCLConstraints));
    return method;
  }


  //----------- isAssociationValid(DOMAIN) -----------------------------------------------------------------------------

  private List<ASTCDMethod> createIsValidForAllAssociationsMethod(ASTCDClass clazz) {
    List<ASTCDMethod> methodList = new ArrayList<>();

    symbolTable.get().getAllAssociationsForType(clazz.getName()).forEach(a -> methodList
        .add(createIsValidAssociationMethod(clazz.getName(), a)));
    return methodList;
  }

  //----------- isAssociationValid(DOMAIN) -----------------------------------------------------------------------------

  private ASTCDMethod createIsValidAssociationMethod(String className, CDAssociationSymbol association) {
    String targetType;
    if (CDAssociationUtil.isOptional(association)) {
      targetType = "Optional<" + association.getTargetType().getName() + ">";
    }
    else if (CDAssociationUtil.isMultiple(association)) {
      targetType = "List<" + association.getTargetType().getName() + ">";
    }
    else {
      targetType = association.getTargetType().getName();
    }

    String paramType = className;
    String paramName = "value";
    String methodName = "is" + TransformationUtils.capitalize(association.getDerivedName()) + VALID;

    List<ASTCDMethod> associationRelatedConstraints = assocConstraints.getOrDefault(association.getTargetRole().orElse(""), new ArrayList<>());


    ASTCDMethod method = new CDMethodBuilder()
            .Public()
            .returnType(OPTIONAL_STRING)
            .name(methodName)
            .addParameter(paramType, paramName)
            .build();

    getGlex().replaceTemplate(
            CoreTemplate.EMPTY_METHOD.toString(),
            method,
            new TemplateHookPoint("backend.data.validator.IsValidAssociation", className, association, targetType, associationRelatedConstraints));

    return method;
  }

  //----------- OCL Constraints ----------------------------------------------------------------------------------------

  private List<ASTCDMethod> createAllOCLConstraintMethods(ASTCDClass domainClass) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    this.constraints.clear();
    this.assocConstraints.clear();
    this.constraintsWithMultipleParameters.clear();

    List<ASTOCLInvariant> invariants = getOCLContextsForDomainClass(domainClass);

    for (ASTOCLInvariant inv : invariants) {
      List<ASTCDMethod> methods = createConstraintMethod(inv, domainClass);
      methodList.addAll(methods);
    }

    return methodList;
  }

  private List<ASTOCLInvariant> getOCLContextsForDomainClass(ASTCDClass domainClass) {
    if (this.ocl.isPresent()) {
      return this.ocl.get().getOCLConstraintList().stream()
          .filter(c -> c instanceof ASTOCLInvariant)
          .map(c -> (ASTOCLInvariant) c)
          .filter(c -> domainClass.getName().equals(TypesPrinter.printType(c.getOCLClassContext().getOCLContextDefinitionList().get(0).getType())))
          .collect(Collectors.toList());
    }
    return Lists.newArrayList();
  }

  private List<ASTCDMethod> createConstraintMethod(ASTOCLInvariant inv, ASTCDClass domainClass) {
    List<ASTCDMethod> result = new ArrayList<>();
    String methodName = inv.getNameOpt().orElse("checkConstraint" + inv.hashCode());

    OCL2JavaPrettyPrinter ocl2Java = new OCL2JavaPrettyPrinter(new IndentPrinter(), this.symbolTable.get(), domainClass.getName());
    List<String> expressions = new ArrayList<>();

    for (ASTExpression expression : inv.getStatementsList()) {
      expressions.add(ocl2Java.prettyprint(expression));
    }
    String message = ocl2Java.getMessage();

    List<ASTCDParameter> parameters = ocl2Java.getParameters();
    List<ASTCDParameter> assocParameters = ocl2Java.getParametersForAssocs();

    ASTCDMethod method;

    if (parameters.size() == 1 && assocParameters.isEmpty()) {
      String attributeName = parameters.get(0).getName();
      method = createDefaultMethod(methodName, parameters);
      addMethodToAttributeConstraints(attributeName, method);
    }
    else if (parameters.isEmpty() && assocParameters.size() == 1) {
      String assocName = assocParameters.get(0).getName();
      method = createDefaultMethod(methodName, assocParameters);
      addMethodToAssocConstraints(assocName, method);
    }
    else {
      parameters.addAll(assocParameters);
      method = createDefaultMethod(methodName, parameters);
      addMethodToConstraintWithMultipleParameters(method);
      result.add(createDelegateToConstraintMethodWithMultipleParameters(method, domainClass));
    }

    getGlex().replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(),
        method,
        new TemplateHookPoint("backend.data.validator.checkOCLConstraint", expressions, message));

    result.add(method);
    return result;
  }

  private ASTCDMethod createDefaultMethod(String name, List<ASTCDParameter> parameters) {
    CDMethodBuilder methodBuilder = new CDMethodBuilder()
            .Public()
            .returnType(OPTIONAL_STRING)
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

  private void addMethodToAssocConstraints(String assocName, ASTCDMethod method) {
    if (!assocConstraints.containsKey(assocName)) {
      assocConstraints.put(assocName, new ArrayList<>());
    }
    assocConstraints.get(assocName).add(method);
  }

  private void addMethodToConstraintWithMultipleParameters(ASTCDMethod method) {
    constraintsWithMultipleParameters.add(method);
  }

  private ASTCDMethod createDelegateToConstraintMethodWithMultipleParameters(ASTCDMethod constraint, ASTCDClass domainClass) {
    String methodName = constraint.getName();
    String paramType = domainClass.getName();
    String paramName = TransformationUtils.uncapitalize(paramType);

    ASTCDMethod method = new CDMethodBuilder()
        .Public()
        .returnType(OPTIONAL_STRING)
        .name(methodName)
        .addParameter(paramType, paramName)
        .build();

    List<String> getterForAttributes = new ArrayList<>();
    for (ASTCDParameter param : constraint.getCDParameterList()) {
      String optional = TypesHelper.isOptional(param.getType()) ? "Optional" : "";
      String getter = paramName + "." + GetterSetterHelper.getPlainGetter(param) + optional + "()";
      getterForAttributes.add(getter);
    }

    String methodsToCall = Joiners.COMMA.join(getterForAttributes);

    getGlex().replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(),
        method,
        new TemplateHookPoint("backend.data.validator.delegateOCLConstraint", methodName, methodsToCall));

    return method;
  }

  //----------- getValidator -------------------------------------------------------------------------------------------

  private ASTCDMethod createGetValidatorMethod(CDTypeSymbol typeSymbol) {

    String typeName = typeSymbol + VALIDATOR;
    String methodName = "get" + VALIDATOR;

    ASTCDMethod method = new CDMethodBuilder()
            .Public()
            .Static()
            .returnType(typeName)
            .name(methodName)
            .build();

    getGlex().replaceTemplate(
            CoreTemplate.EMPTY_METHOD.toString(),
            method,
            new TemplateHookPoint("backend.data.validator.GetValidator", typeName));

    return method;
  }

  //----------- setValidator -------------------------------------------------------------------------------------------

  private ASTCDMethod createSetValidatorMethod(CDTypeSymbol typeSymbol) {

    String returnType = "void";
    String methodName = "set" + VALIDATOR;
    String paramType = typeSymbol + VALIDATOR;
    String paramName = "v";

    ASTCDMethod method = new CDMethodBuilder()
            .Public()
            .Static()
            .returnType(returnType)
            .name(methodName)
            .addParameter(paramType, paramName)
            .build();

    getGlex().replaceTemplate(
            CoreTemplate.EMPTY_METHOD.toString(),
            method,
            new TemplateHookPoint("backend.data.validator.SetValidator"));
    return method;
  }

}
