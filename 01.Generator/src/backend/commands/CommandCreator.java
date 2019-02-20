/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package backend.commands;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CommandCreator extends CreateTrafo {

  public static final String COMMAND = "CommandDTO";

  public CommandCreator() {
    super("");
  }

  public CommandCreator(String suffix) {
    super(suffix);
  }

  @Override
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
                                                          CDTypeSymbol typeSymbol) {
    String typeName = typeSymbol.getName();
    ASTCDClass addedClass = new CDClassBuilder().Public().superclass(COMMAND)
        .subpackage(TransformationUtils.COMMANDS_PACKAGE)
        .setName(checkIfTOPExtension(TransformationUtils.COMMANDS_PACKAGE, typeName + suffix)).build();
    getOutputAst().getCDClassList().add(addedClass);
    return Optional.of(addedClass);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass clazz, ASTCDClass domainClass,
                                                      CDTypeSymbol typeSymbol) {
    List<ASTCDConstructor> constructors = Lists.newArrayList();

    // add empty constructor for deserializing
    ASTCDConstructor emptyConstructor = new CDConstructorBuilder().Package().Public().setName(clazz.getName()).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), emptyConstructor,
        new StringHookPoint("{this.typeName = \"" + TransformationUtils.removeTOPExtensionIfExistent(clazz.getName()) + "\";}"));

    constructors.add(emptyConstructor);
    createFullConstructor(clazz, domainClass, typeSymbol).ifPresent(constructors::add);
    return constructors;
  }

  protected Optional<ASTCDConstructor> createFullConstructor(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = createAttributes(extendedClass, domainClass, typeSymbol);

    List<ASTCDParameter> paramList = new ArrayList<>();
    attributes.forEach(a -> paramList.add(
        CD4AnalysisMill.cDParameterBuilder().setName(TransformationUtils.uncapitalize(a.getName()))
            .setType(new CDSimpleReferenceBuilder().name(TypesPrinter.printType(a.getType())).build()).build()));

    if (paramList.isEmpty()) {
      return Optional.empty();
    }

    ASTCDConstructor fullConstructor = new CDConstructorBuilder().Public().setName(extendedClass.getName())
        .setCDParameterList(paramList).build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), fullConstructor,
        new TemplateHookPoint("backend.commands.FullConstructor", attributes));

    return Optional.of(fullConstructor);
  }

  /**
   * @see common.ExtendTrafo#createMethods(ASTCDClass,
   * ASTCDClass,
   * CDTypeSymbol)
   */
  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass, ASTCDClass domainClass,
                                            CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methodList = Lists.newArrayList();
    methodList.add(createDoRunMethod(extendedClass, domainClass));

    List<ASTCDAttribute> attrs = createAttributes(extendedClass, domainClass, typeSymbol);
    for (ASTCDAttribute attr : attrs) {
      methodList.add(createGetMethod(extendedClass.getName(), attr));
    }

    return methodList;
  }

  protected ASTCDMethod createGetMethod(String className, ASTCDAttribute attr) {
    ASTCDMethod method = new CDMethodBuilder().Public()
        .returnType(TypesPrinter.printType(attr.getType()))
        .name(TransformationUtils.makeCamelCase("get", attr.getName())).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{return this." + attr.getName() + ";}"));
    return method;
  }

  /**
   * @see common.ExtendTrafo#addImports(ASTCDClass, ASTCDClass, CDTypeSymbol)
   */
  @Override
  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass,
                            CDTypeSymbol typeSymbol) {
    getImports(typeSymbol).forEach(i -> TransformationUtils
        .addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, i));
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("de.macoco.be.authz.Roles;");
    imports.add("javax.xml.bind.ValidationException");
    imports.add("de.macoco.be.authz.util.SecurityHelper");
    imports.add("de.macoco.be.authz.ObjectClasses");
    imports.add("de.macoco.be.authz.Permissions");
    imports.add("de.macoco.be.util.DAOLib");
    imports.add("de.macoco.be.error.MaCoCoError");
    imports.add("de.macoco.be.error.MaCoCoErrorFactory");
    imports.add("de.macoco.be.command.rte.general.CommandDTO");
    imports.add("de.macoco.be.dtos.rte.DTO");
    imports.add("de.macoco.be.dtos.rte.IdDTO");
    imports.add("de.macoco.be.dtos.rte.OkDTO");
    imports.add("de.macoco.be.dtos.rte.ErrorDTO");
    imports.add("de.macoco.be.domain.dtos.*");
    imports.add("de.se_rwth.commons.logging.Log");

    imports.addAll(additionalImports(typeSymbol));
    return imports;
  }

  protected List<String> additionalImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add(Joiners.DOT.join(TransformationUtils.getPackageName(getOutputAstRoot()),
            TransformationUtils.DTOS_PACKAGE, typeSymbol.getName() + "DTOLoader"));
    imports.add(Joiners.DOT.join(TransformationUtils.getPackageName(getOutputAstRoot()),
        TransformationUtils.CLASSES_PACKAGE, typeSymbol.getName().toLowerCase(), "*"));
    return imports;
  }

  private ASTCDMethod createDoRunMethod(ASTCDClass clazz, ASTCDClass domainClass) {
    ASTCDMethod method = new CDMethodBuilder().Public().name("doRun")
        .addParameter("SecurityHelper", "securityHelper")
        .addParameter("DAOLib", "daoLib")
        .returnType("DTO")
        .exceptions("MaCoCoError")
        .build();

    setTemplate(method, domainClass, clazz.getName());

    return method;
  }

  protected abstract void setTemplate(ASTCDMethod method, ASTCDClass domainClass, String className);

}
