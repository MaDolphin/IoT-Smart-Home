/* (c) https://github.com/MontiCore/monticore */

package backend.data.test;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import common.CreateTrafo;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static backend.data.dataclass.DataClassTrafo.RAW_INIT_ATTRS;
import static com.google.common.base.Preconditions.checkArgument;

public class DummyTestClassCreator extends CreateTrafo {

  private TypeHelper typeHelper = new TypeHelper();

  public static final String DUMMYCREATOR = "DummyCreator";

  private Optional<ASTCDCompilationUnit> domainAst = Optional.empty();

  private Set<String> excludes = Sets.newHashSet("getHumanName");

  public DummyTestClassCreator() {
    super(DUMMYCREATOR);
  }

  public final void transform(ASTCDCompilationUnit ast, ASTCDCompilationUnit domainAst) {
    this.domainAst = Optional.ofNullable(domainAst);
    transform(ast);
  }

  @Override
  protected void transform() {
    checkArgument(handcodePath.isPresent());
    checkArgument(symbolTable.isPresent());
    checkArgument(domainAst.isPresent());
    // Extend existing domain classes
    getDomainAst().getCDClassList().forEach(this::extendDomainClass);
  }

  /**
   * return input ast
   *
   * @return
   */
  protected final ASTCDDefinition getDomainAst() {
    return this.domainAst.get().getCDDefinition();
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    // Add attribute id that can be used to identify the object
    ASTCDAttribute attribute = new CDAttributeBuilder().type(clazz.getName())
        .setName("obj").build();
    attributes.add(attribute);
    return attributes;
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass dummyClass, ASTCDClass clazz,
      CDTypeSymbol type) {
    List<ASTCDConstructor> constructors = new ArrayList<>();
    createInitializedObjectConstructor(dummyClass, clazz, type).ifPresent(constructors::add);
    return constructors;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass dummyClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    methods.addAll(createGetterMethods(clazz, typeSymbol));
    methods.add(createGetTestedObjectMethod(clazz, typeSymbol));
    return methods;
  }

  @Override
  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    super.addImports(extendedClass, domainClass, typeSymbol);

    domainClass.getCDAttributeList().forEach(attr -> TransformationUtils
        .addStarImportForCDType(extendedClass, attr.getType(), getAstRoot(), symbolTable.get()));

    symbolTable.get().getDerivedAttributes(domainClass.getName()).forEach(
        attr -> TransformationUtils.addImportForCDType(extendedClass, attr.getType(), getAstRoot(), symbolTable.get()));
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("java.util.ArrayList");
    imports.add("java.util.List");
    imports.add("java.util.Optional");
    imports.add("java.time.ZonedDateTime");
    return imports;
  }

  //----------- PRIVATE  METHODS -------------------------------------

  private List<ASTCDMethod> createGetterMethods(ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    return clazz.getCDMethodList().stream().filter(this::isGetMethod).filter(m -> !m.getName().equals("getValidator"))
        .map(m -> createGetMethod(typeSymbol.getName(), m)).collect(
            Collectors.toList());
  }

  private ASTCDMethod createGetMethod(String typeName, ASTCDMethod domainMethod) {
    ASTCDMethod method = new CDMethodBuilder().Public()
        .setReturnType(domainMethod.getReturnType().deepClone()).setName(domainMethod.getName())
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ return get" + typeName + "()." + domainMethod.getName() + "(); }"));
    return method;
  }

  private ASTCDMethod createGetTestedObjectMethod(ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder().returnType(clazz.getName())
        .name("get" + clazz.getName())
        .build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint(
            "{ return this.obj; }"));
    return method;
  }

  private boolean isGetMethod(ASTCDMethod method) {
    return GetterSetterHelper.isGetMethod(method) && !excluded(method.getName());
  }

  private boolean excluded(String name) {
    return excludes.stream().filter(e -> name.startsWith(e)).findAny().isPresent();
  }

  private Optional<ASTCDConstructor> createInitializedObjectConstructor(ASTCDClass dummyClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    if (typeSymbol.isAbstract()) {
      return Optional.empty();
    }
    Optional<ASTCDMethod> rawInitAttrsWithParams = clazz.getCDMethodList().stream()
        .filter(c -> c.getName().equals(RAW_INIT_ATTRS) && c.getCDParameterList().size() > 0)
        .findFirst();
    if (!rawInitAttrsWithParams.isPresent()) {
      return Optional.of(getDefaultConstructor(dummyClass, clazz, typeSymbol));
    }
    List<ASTCDParameter> parameters = rawInitAttrsWithParams.get().getCDParameterList();
    List<String> callParameters = new ArrayList<>();
    List<CDFieldSymbol> assocs = new ArrayList();
    for (ASTCDParameter param : parameters) {
      String type = TypesPrinter.printType(param.getType());
      String name = TransformationUtils.uncapitalize(param.getName());
      String value = typeHelper.getDefaultValue(type, param.getName(), "testOf");
      if (!value.equals("null")) {
        // Optional, List or primitive data types
        callParameters.add(value);
      }
      else {
        Optional<CDTypeSymbol> cdType = symbolTable.get().resolve(type);
        if (!cdType.isPresent()) {
          // handle the extern type
          callParameters.add(value);
        }
        else {
          // handle the CD type
          if (cdType.get().isEnum()) {
            // handle enums
            callParameters.add(type + ".values()[0]");
          }
          else {
            assocs.add(new CDFieldSymbol(name,
                new CDTypeSymbolReference(type, param.getEnclosingScopeOpt().get())));
            callParameters.add(TransformationUtils.uncapitalize(param.getName()));
          }
        }
      }
    }
    String callParam = Joiners.COMMA.join(callParameters);
    ASTCDConstructor constructor = new CDConstructorBuilder().Public().setName(dummyClass.getName())
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new TemplateHookPoint("backend.data.test.dummyCreator", clazz.getName(), assocs,
            callParam));
    return Optional.of(constructor);
  }

  private ASTCDConstructor getDefaultConstructor(ASTCDClass dummyClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .setName(dummyClass.getName())
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new StringHookPoint("{ this.obj = new " + typeSymbol.getName() + "(); }"));
    return constructor;
  }

}
