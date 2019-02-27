/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package common.util;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import common.CDSymbolTable;
import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.io.paths.IterablePath;
import de.monticore.literals.literals._ast.ASTStringLiteral;
import de.monticore.literals.literals._ast.LiteralsMill;
import de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import frontend.data.DTOCreator;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class TransformationUtils {

  public static final String JAVA_FILE_EXTENSION = ".java";

  public static final String TEMPLATE_FILE_EXTENSION = ".ftl";

  public static final String DOT_TYPESCRIPT_FILE_EXTENSION = ".ts";

  public static final String TYPESCRIPT_FILE_EXTENSION = "ts";

  public static final String TOP_EXTENSION = "TOP";

  public static final String DAO_EXTENSION = "DAO";

  public static final String CREATE_CMD = "_create";

  public static final String DELETE_CMD = "_delete";

  public static final String GETBYID_CMD = "_getById";

  public static final String SET_CMD = "_set";

  public static final String GETALL_CMD = "_getAll";

  public static final String UPDATE_CMD = "_update";

  public static final String DOT_STAR = ".*";

  public static final String MONTIGEM_BASE = "de.montigem.be";

  public static final String DAO_PACKAGE = "cdmodelhwc.daos";

  public static final String CLASSES_PACKAGE = "cdmodelhwc.classes";

  public static final String COMMANDS_PACKAGE = "commands";

  public static final String DTOS_PACKAGE = "dtos";

  public static final String UTIL_PACKAGE = "util";

  public static final String MARSHALLING_PACKAGE = "marshalling";

  public static final String RTE_PACKAGE = "rte";

  public static final String DOMAIN_NAME = "Domain";

  public static final String STEREOTYPE_HUMANNAME = "human";

  public static final String STEREOTYPE_NOCASCADE = "nocascade";

  public static final String STEREOTYPE_MAXLENGTH = "length";

  public static final String STEREOTYPE_DBCOLUMNDEFINITION = "dbColumnDefinition";

  public static final String STEREOTYPE_DBCOLUMN = "dbColumn";

  public static final String ATTRIBUTE_ANNOATION = "attributeAnnotation";

  public static final String METHOD_ANNOTATION = "methodAnnotation";

  private static TransformationUtils theInstance;

  private static TypeHelper typeHelper = new TypeHelper();

  private static Collection<String> defaultImportsClass = Lists.newArrayList(
          "java.util.*", "com.google.common.collect.*");

  private Map<ASTNode, Map<String, Collection<String>>> trafoProperties = Maps.newHashMap();

  public static void setProperty(ASTNode node, String property, Collection<String> value) {
    getInstance().getProperties(node).put(property, value);
  }

  public static void setProperty(ASTNode node, String property, String value) {
    setProperty(node, property, Sets.newHashSet(value));
  }

  public static void addPropertyValue(ASTNode node, String property, String value) {
    getInstance().getProperties(node, property).add(value);
  }

  public static Collection<String> getProperty(ASTCDType node, String property) {
    if (getInstance().trafoProperties.containsKey(node) &&
            getInstance().trafoProperties.get(node).containsKey(property)) {
      return getInstance().trafoProperties.get(node).get(property);
    }
    return Sets.newHashSet();
  }

  public static Optional<String> getSingleProperty(ASTNode node, String property) {
    if (getInstance().trafoProperties.containsKey(node)) {
      Collection<String> properties = getInstance().trafoProperties.get(node).get(property);
      if (properties != null && properties.size() == 1) {
        return Optional.of(properties.iterator().next());
      }
    }
    return Optional.empty();
  }

  private Map<String, Collection<String>> getProperties(ASTNode node) {
    Map<String, Collection<String>> properties = trafoProperties.get(node);
    if (properties == null) {
      properties = Maps.newHashMap();
      trafoProperties.put(node, properties);
    }
    return properties;
  }

  private Collection<String> getProperties(ASTNode node, String property) {
    Map<String, Collection<String>> properties = getProperties(node);
    Collection<String> propertyValue = properties.get(property);
    if (propertyValue == null) {
      propertyValue = Sets.newHashSet();
      properties.put(property, propertyValue);
    }
    return propertyValue;
  }

  /**
   * Capitalizes the first character of the string
   *
   * @param string the string
   * @return the capitalized string
   */
  public static String capitalize(String string) {
    checkNotNull(string);
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, string);
  }

  /**
   * Uncapitalizes the first character of the string
   *
   * @param string the string
   * @return the capitalized string
   */
  public static String uncapitalize(final String string) {
    checkNotNull(string, "Cannot uncapitalize an invalid string.");
    checkArgument(!string.isEmpty(), "Cannot uncapitalize an empty string.");

    return Character.toLowerCase(string.charAt(0)) + string.substring(1);
  }

  /**
   * Convert a snake case string to a camel case string
   * test_string -> TestString
   *
   * @param string the string
   * @return the capitalized string
   */
  public static String snakeCaseToCamelCase(final String string) {
    checkNotNull(string, "Cannot change an invalid string.");
    checkArgument(!string.isEmpty(), "Cannot change an empty string.");

    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string);
  }

  /**
   * Convert a snake case string to a camel case string split on underscores
   * test_string -> Test String
   *
   * @param string the string
   * @return the capitalized string
   */
  public static String snakeCaseToSplitCamelCase(final String string) {
    checkNotNull(string, "Cannot change an invalid string.");
    checkArgument(!string.isEmpty(), "Cannot change an empty string.");

    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string.replaceAll("_", " _"));
  }

  /**
   * remove the trailing s of a string
   *
   * @param string
   * @return
   */
  public static String removeTrailingS(final String string) {
    checkNotNull(string, "Cannot singularize an invalid string.");
    checkArgument(!string.isEmpty(), "Cannot singularize an empty string.");

    return (string.charAt(string.length() - 1) == 's') ?
            string.substring(0, string.length() - 1) :
            string;
  }

  /**
   * Returns a string in camel case notation
   *
   * @param stringList a list of strings
   * @return the string in camel case notation
   */
  public static String makeCamelCase(String... stringList) {
    List<String> lst = Lists.newArrayList(stringList);
    Iterator<String> namesIterator = lst.iterator();
    StringBuilder fullName = new StringBuilder();

    if (namesIterator.hasNext()) {
      fullName.append(namesIterator.next());
    }

    while (namesIterator.hasNext()) {
      fullName.append(capitalize(namesIterator.next()));
    }

    return fullName.toString();
  }

  public static String convertPackagePrefixToLowerCase(String qualifiedName) {
    String superClass = qualifiedName;

    String simple = Names.getSimpleName(superClass);
    String[] packA = Names.getQualifier(superClass).split("\\.");
    String sep = "";
    StringBuilder pack = new StringBuilder();

    for (int i = 0; i < packA.length; i++) {
      String packPart = packA[i];
      pack.append(sep);
      sep = ".";
      if (i == packA.length - 1) {
        pack.append(packPart.toLowerCase());
      } else {
        pack.append(packPart);
      }
    }
    if (qualifiedName.contains(".")) {
      pack.append(sep);
    }
    pack.append(simple);
    superClass = pack.toString();
    return superClass;
  }

  /**
   * Converts identifier to String
   *
   * @param _string the identifier
   * @return the capitalized string
   */
  public static String identToString(String _string) {
    checkNotNull(_string);
    if (_string.startsWith("\"") || _string.endsWith("\"")) {
      return _string;
    }
    return "\"" + _string + "\"";
  }

  /**
   * Checks if a handwritten file (java class or Freemarker template) with
   * the given qualifiedName (dot-separated) exists on the given path
   *
   * @param fileName interfaceName of the class to search for
   * @return true if a handwritten file (java class or Freemarker template)
   * with the qualifiedName exists
   */
  public static boolean existsHandwrittenFile(String fileName, String packageName,
                                              IterablePath path, String extension) {
    Path filePath = Paths
            .get(Names.getPathFromPackage(packageName), Names.getPathFromPackage(fileName) + extension);
    return existsHandwrittenFile(path, filePath);
  }

  /**
   * Checks if a handwritten file (java class or Freemarker template) with
   * the given qualifiedName (dot-separated) exists on the given path
   *
   * @param fileName name of the class to search for
   * @return true if a handwritten file (java class or Freemarker template)
   * with the qualifiedName exists
   */
  public static boolean existsHandwrittenDotFile(String fileName, String packageName,
                                                 IterablePath path, String extension) {
    Path filePath = Paths
            .get(Names.getPathFromPackage(packageName), fileName + extension);
    return existsHandwrittenFile(path, filePath);
  }

  /**
   * Checks if a given path (e.g. file) exists on the given iterable path.
   *
   * @param handcodedPath the path where to look for the handwritten file
   * @param path          to search for
   * @return true if the path exists
   */
  public static boolean existsHandwrittenFile(IterablePath handcodedPath, Path path) {
    Iterator<Path> targetPathIt = handcodedPath.get();
    while (targetPathIt.hasNext()) {
      Path it = targetPathIt.next();
      if (it.toString().endsWith(path.toString()) || it.toString().equals(path.toString())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether there exists a valid signature extension for the given
   * type. Reports an error, if there is just a signature extending
   * interface but no implementing subclass.
   *
   * @param type            the type (class) the signature extension is looked up for
   * @param sigSuffix       the interfaceName of signature extending interfaces
   * @param extensionSuffix the interfaceName of subclasses extending generated
   *                        classes
   * @return true, if there is a valid signature extension, false otherwise
   */
  public static boolean existsValidSignatureExtensionFor(String type, String packageName,
                                                         String sigSuffix, String extensionSuffix, IterablePath targetPath) {
    if (existsHandwrittenFile(type + sigSuffix, packageName, targetPath, JAVA_FILE_EXTENSION)) {
      if (existsHandwrittenFile(type + extensionSuffix, packageName,
              targetPath, JAVA_FILE_EXTENSION)) {
        return true;
      } else {
        Log.error("0xD3010: Handwritten signature extension for "
                + type
                + " was found without an implementing subclass of the form "
                + type
                + extensionSuffix
                + JAVA_FILE_EXTENSION
                + "."
                +
                "\nFor now we will ignore the signature extension, i.e., your handwritten extension. "
                + "Implement the corresponding subclass and regenerate to resolve this error"
                + " and to use the handwritten signature extension.");
      }
    }
    return false;
  }

  /**
   * Checks whether there exists a handwritten Freemarker template with a
   * given interfaceName
   *
   * @param templateName the qualified template interfaceName
   * @param templatePath path for the handwritten templates
   * @return true, if there is a valid signature extension, false otherwise
   */
  public boolean existsHandwrittenTemplate(String templateName, IterablePath templatePath) {
    return existsHandwrittenFile(templateName, "", templatePath, TEMPLATE_FILE_EXTENSION);
  }


  /**
   * Returns the interfaceName plus the extension for handwritten code if the
   * corresponding handwritten file does exist.
   *
   * @param className the class interfaceName
   * @return either the interfaceName of the class or the interfaceName of the class plus the
   * EIMP extension, if the handwritten code exists.
   */
  public static String getJavaClassName(String className, String packageName, String extension,
                                        IterablePath targetPath) {
    if (existsHandwrittenFile(className, packageName, targetPath, JAVA_FILE_EXTENSION)) {
      return className + extension;
    }
    return className;
  }

  public static Set<String> getImports(ASTCDType node, Collection<String> astImports,
                                       boolean withDefault) {
    Set<String> imports = new LinkedHashSet<>(astImports);
    if (withDefault) {
      imports.addAll(getDefaultImports(node));
    }
    // imports.addAll(getSpecificImports(node));
    imports.addAll(getProperty(node,
            CompilationUnit.IMPORTS_PROPERTY));
    imports.removeAll(imports.stream()
            .filter(i -> !isStarImport(i) && foundStarImport(i, imports))
            .collect(Collectors.toList()));
    return imports;
  }

  private static boolean foundStarImport(String imported, Set<String> importList) {
    String checkedQualifier = Names.getQualifier(imported);
    return importList
            .stream()
            .filter(
                    i -> isStarImport(i)
                            && checkedQualifier.equals(Names.getQualifier(i)))
            .findAny()
            .isPresent();
  }

  private static boolean isStarImport(String imported) {
    return imported.indexOf('.') != -1 && imported.endsWith("*");
  }

  public static String getPackageName(ASTCDCompilationUnit ast) {
    return Joiner.on(".").skipNulls().join(ast.getPackageList());
  }

  public static String getClassPackageName(ASTCDCompilationUnit ast, String name) {
    return Joiners.DOT
            .join(getPackageName(ast), CLASSES_PACKAGE, name.toLowerCase());
  }

  public static Collection<String> getJavaImports(ASTCDCompilationUnit ast) {
    Collection<String> importList = Lists.newArrayList();
    for (ASTImportStatement imp : ast.getImportStatementList()) {
      if (!imp.isStar()) {
        importList.add(Joiner.on(".").skipNulls().join(imp.getImportList()));
      }
    }
    return importList;
  }

  public static String getQualifiedName(ASTCDCompilationUnit ast, String name) {
    return getQualifiedName(ast, name, "");
  }

  public static String getQualifiedName(String packageName, String name) {
    return Joiners.DOT
            .join(packageName, CLASSES_PACKAGE, name.toLowerCase(), name);
  }

  public static String getQualifiedName(ASTCDCompilationUnit astRoot, String name, String suffix) {
    return Joiners.DOT
            .join(getPackageName(astRoot), CLASSES_PACKAGE, name.toLowerCase(),
                    name) + suffix;
  }

  public static String getQualifiedNameForDomainOrDtoType(String packageName, String typeName,
                                                          boolean isDomainEnum) {
    return isDomainEnum ? Joiners.DOT
            .join(packageName, CLASSES_PACKAGE, typeName.toLowerCase(), typeName) :
            Joiners.DOT
                    .join(packageName, DTOS_PACKAGE, typeName + DTOCreator.DTO);

  }

  public static String getQualifiedName(ASTCDCompilationUnit astRoot, String name, String suffix,
                                        String subPackage) {
    return Joiners.DOT
            .join(getPackageName(astRoot), CLASSES_PACKAGE, subPackage,
                    name) + suffix;
  }

  public static TransformationUtils getInstance() {
    if (theInstance == null) {
      theInstance = new TransformationUtils();
    }
    return theInstance;
  }

  /**
   * This constructor has to be public because of using in templates
   */
  public TransformationUtils() {
  }

  private static final List<Integer> exceptionNumbers = Lists
          .newArrayList();

  public static String getErrorCode() {
    StringBuilder str = new StringBuilder("0xD");
    int newExc = 0;

    do {
      newExc = getRandom(4);
    } while (exceptionNumbers.contains(newExc));

    exceptionNumbers.add(newExc);

    str.append(newExc);
    return str.toString();
  }

  public static Integer getRandom(Integer pSize) {

    if (pSize <= 0) {
      return null;
    }
    Double dMin = Math.pow(10, pSize.doubleValue() - 1D);
    Double dMax = (Math.pow(10, (pSize).doubleValue())) - 1D;
    int min = dMin.intValue();
    int max = dMax.intValue();
    Random ran = new Random();
    return ran.nextInt(max - min) + min;
  }

  public static List<CDFieldSymbol> excludeCollectionAttributes(List<CDFieldSymbol> fields,
                                                                TypeHelper helper) {
    return fields.stream().filter(f -> !helper.isGenericList(f.getType().getStringRepresentation()))
            .collect(Collectors.toList());
  }

  public static Collection<String> getDefaultImports(ASTCDType node) {
    // TODO GV
    return defaultImportsClass;
  }

  public static boolean isDataType(ASTCDType type) {
    return type.getSymbolOpt().isPresent() && type.getSymbol().getName()
            .equalsIgnoreCase(type.getName());
  }

  public static String printCDEnumParameters(List<ASTCDEnumParameter> parameters) {
    if (parameters.isEmpty()) {
      return "";
    }
    StringBuilder output = new StringBuilder("(");
    String delim = "";
    LiteralsPrettyPrinterConcreteVisitor printer = new LiteralsPrettyPrinterConcreteVisitor(new IndentPrinter());
    for (ASTCDEnumParameter param : parameters) {
      output.append(delim);
      if (param.getValue().getSignedLiteral() instanceof ASTStringLiteral) {
        // TODO Entferne dies, wenn die Values komplexere Werte annehmen können
        output.append(((ASTStringLiteral) param.getValue().getSignedLiteral()).getSource());
      } else {
        output.append(printer.prettyprint(param.getValue().getSignedLiteral()));
      }
      delim = ", ";
    }
    output.append(")");
    return output.toString();
  }

  public static String printCDEnumParameter(ASTCDEnumParameter parameter) {
    if (parameter.getValue().getSignedLiteral() instanceof ASTStringLiteral) {
      // TODO Entferne dies, wenn die Values komplexere Werte annehmen können
      return (((ASTStringLiteral) parameter.getValue().getSignedLiteral()).getSource());
    } else {
      LiteralsPrettyPrinterConcreteVisitor printer = new LiteralsPrettyPrinterConcreteVisitor(new IndentPrinter());
      return printer.prettyprint(parameter.getValue().getSignedLiteral());
    }
  }

  public static String printCDEnumConstants(List<ASTCDEnumConstant> constants) {
    return Joiners.COMMA.join(constants.stream()
            .map(constant -> printCDEnumConstants(constant))
            .collect(Collectors.toList()));
  }

  public static String printCDEnumConstants(ASTCDEnumConstant constant) {
    List<ASTCDEnumParameter> parameters = constant.getCDEnumParameterList();
    return parameters.isEmpty() ?
            constant.getName() :
            (constant.getName() + printCDEnumParameters(parameters));
  }

  public static void addImportForCDType(ASTNode ast, ASTType type, ASTCDCompilationUnit root,
                                        CDSymbolTable symbolTable) {
    if (type instanceof ASTSimpleReferenceType) {
      List<String> typeNames = ((ASTSimpleReferenceType) type).getNameList();
      if (!typeNames.isEmpty() && symbolTable.isTypeDefinedInModel(typeNames.get(0))) {
        addPropertyValue(ast, CompilationUnit.IMPORTS_PROPERTY,
                getQualifiedName(root, typeNames.get(0)));
      }
    }
  }

  public static void addImportForCdOrDtoType(ASTNode ast, CDTypeSymbolReference type,
                                             ASTCDCompilationUnit root,
                                             CDSymbolTable symbolTable) {
    String typeName = type.getName();
    if (typeHelper.isGenericNotStringList(typeName)) {
      typeName = typeHelper.getFirstTypeArgumentOfGeneric(typeName, TypeHelper.LIST.length());
    } else if (typeHelper.isGenericOptional(typeName)) {
      typeName = typeHelper.getFirstTypeArgumentOfOptional(typeName);
    }
    Optional<CDTypeSymbol> cdType = symbolTable.resolve(typeName);
    if (cdType.isPresent() && symbolTable.isTypeDefinedInModel(cdType.get())) {
      String qualifiedName = getQualifiedNameForDomainOrDtoType(cdType.get().getPackageName(),
              typeName,
              isDomainEnum(cdType.get()));
      addPropertyValue(ast, CompilationUnit.IMPORTS_PROPERTY, qualifiedName);
    }
  }

  public static void addImportForCdOrDtoType(ASTNode ast, ASTType type, ASTCDCompilationUnit root,
                                             CDSymbolTable symbolTable) {
    if (type instanceof ASTSimpleReferenceType) {
      List<String> typeNames = ((ASTSimpleReferenceType) type).getNameList();
      if (typeNames.isEmpty()) {
        return;
      }
      String typeName = typeNames.get(0);
      if (typeHelper.isList(typeName)) {
        Optional<ASTSimpleReferenceType> typeArgument = typeHelper
                .getFirstTypeArgumentOfGenericType((ASTSimpleReferenceType) type, "List");
        if (typeArgument.isPresent()) {
          typeName = typeArgument.get().getNameList().get(0);
        }
      } else if (typeHelper.isOptional(typeName)) {
        Optional<ASTSimpleReferenceType> typeArgument = typeHelper
                .getFirstTypeArgumentOfGenericType((ASTSimpleReferenceType) type, "Optional");
        if (typeArgument.isPresent()) {
          typeName = typeArgument.get().getNameList().get(0);
        }
      }
      Optional<CDTypeSymbol> cdType = symbolTable.resolve(typeName);
      if (cdType.isPresent() && symbolTable.isTypeDefinedInModel(cdType.get())) {
        String qualifiedName = getQualifiedNameForDomainOrDtoType(cdType.get().getPackageName(),
                typeName,
                isDomainEnum(cdType.get()));
        addPropertyValue(ast, CompilationUnit.IMPORTS_PROPERTY, qualifiedName);
      }
    }
  }

  public static boolean isDomainType(CDTypeSymbol type) {
    return Names.getSimpleName(type.getModelName()).equals(DOMAIN_NAME);
  }

  public static boolean isDomainEnum(CDTypeSymbol type) {
    return type.isEnum() && isDomainType(type);
  }

  public static boolean isDomainType(CDSymbolTable symbolTable, CDTypeSymbol type) {
    return symbolTable.isTypeDefinedInModel(type) && Names.getSimpleName(type.getModelName())
            .equals(DOMAIN_NAME);
  }

  public static boolean isDTOType(CDSymbolTable symbolTable, CDTypeSymbol type) {
    return symbolTable.isTypeDefinedInModel(type) && !type.isEnum();
  }

  public static boolean isDTOType(CDSymbolTable symbolTable, String typeName) {
    Optional<CDTypeSymbol> type = symbolTable.getTypeSymbolIfDefinedInModel(typeName);
    return type.isPresent() && !type.get().isEnum();
  }

  public static boolean isDomainType(CDSymbolTable symbolTable, String typeName) {
    Optional<CDTypeSymbol> type = symbolTable.getTypeSymbolIfDefinedInModel(typeName);
    return type.isPresent() && Names.getSimpleName(type.get().getModelName())
            .equals(DOMAIN_NAME);
  }

  public static String getStringRepresentationForCdOrDtoType(String typeName,
                                                             CDSymbolTable symbolTable) {
    Optional<CDTypeSymbol> type = symbolTable.getTypeSymbolIfDefinedInModel(typeName);
    return type.isPresent() && type.get() instanceof CDTypeSymbolReference ?
            getStringRepresentationForCdOrDtoType((CDTypeSymbolReference) type.get(), symbolTable) :
            typeName;
  }

  public static String getStringRepresentationForCdOrDtoType(CDTypeSymbolReference type,
                                                             CDSymbolTable symbolTable) {
    String typeName = "";
    if (typeHelper.isOptional(type)
            && type.getActualTypeArguments().size() > 0) {
      typeName = type.getActualTypeArguments().get(0).getType().getName();
      if (isDTOType(symbolTable, typeName)) {
        typeName += DTOCreator.DTO;
      }
    } else if (typeHelper.isList(type) && type.getActualTypeArguments().size() > 0) {
      String typeArgument = type.getActualTypeArguments().get(0).getType().getName();
      typeName = isDTOType(symbolTable, typeArgument) ?
              "List<" + typeArgument + DTOCreator.DTO + ">" :
              type.getStringRepresentation();
    } else {
      typeName = type.getStringRepresentation() + (isDTOType(symbolTable,
              type) ? DTOCreator.DTO : "");
    }
    return typeName;
  }

  public static void addImportForCDType(ASTNode ast, CDTypeSymbolReference type,
                                        ASTCDCompilationUnit root, CDSymbolTable symbolTable) {
    String name = type.getName();
    if (symbolTable.isTypeDefinedInModel(name)) {
      addPropertyValue(ast, CompilationUnit.IMPORTS_PROPERTY,
              getQualifiedName(root, name));
    }
  }

  public static void addStarImportForCDType(ASTNode ast, ASTType type, ASTCDCompilationUnit astRoot,
                                            CDSymbolTable symbolTable) {
    if (type instanceof ASTSimpleReferenceType) {
      List<String> typeNames = ((ASTSimpleReferenceType) type).getNameList();
      if (typeNames.size() > 0 && symbolTable.isTypeDefinedInModel(typeNames.get(0))) {
        addPropertyValue(ast, CompilationUnit.IMPORTS_PROPERTY,
                getClassPackageName(astRoot, typeNames.get(0)) + DOT_STAR);
      }
    }
  }

  public static void addStarImportForCDType(ASTCDType clazz, String type,
                                            ASTCDCompilationUnit astRoot) {
    addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
            getClassPackageName(astRoot, type) + DOT_STAR);
  }

  public static void addImportForCDType(ASTCDType clazz, String type,
                                        ASTCDCompilationUnit astRoot) {
    addImportForCDType(clazz, type, astRoot, "");
  }

  public static void addImportForCDType(ASTCDType clazz, String type,
                                        ASTCDCompilationUnit astRoot, String suffix) {
    addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
            getQualifiedName(astRoot, type, suffix));
  }

  /**
   * adds a list of annotations to the ASTModifier
   *
   * @param modifier : ASTModifier
   * @param annos    ... : String
   */
  public static void addAnnos(ASTModifier modifier, String... annos) {
    ASTCDStereotype type = CD4AnalysisMill.cDStereotypeBuilder().build();
    List<String> annoList = Lists.newArrayList(annos);
    for (String anno : annoList) {
      ASTCDStereoValue value = CD4AnalysisMill.cDStereoValueBuilder().setName(anno).build();
      type.getValueList().add(value);
    }
    modifier.setStereotype(type);
  }

  /**
   * adds a list of annotations to the ASTModifier
   *
   * @param modifier : ASTModifier
   * @param annos    ... : String
   */
  public static void addAttributeAnnos(ASTModifier modifier, String... annos) {
    ASTCDStereotype type = CD4AnalysisMill.cDStereotypeBuilder().build();
    List<String> annoList = Lists.newArrayList(annos);
    for (String anno : annoList) {
      ASTCDStereoValue value = CD4AnalysisMill.cDStereoValueBuilder().setName(anno)
              .setValue(ATTRIBUTE_ANNOATION).build();
      type.getValueList().add(value);
    }
    modifier.setStereotype(type);
  }

  public static boolean hasStereotype(ASTCDType ast, String stereotypeName) {
    if (ast.getModifierOpt().isPresent() && ast.getModifierOpt().get().getStereotypeOpt()
            .isPresent()) {
      ASTCDStereotype stereotype = ast.getModifierOpt().get()
              .getStereotype();
      return stereotype.getValueList().stream().filter((v) -> {
        return v.getName().equals(stereotypeName);
      }).findAny().isPresent();
    }
    return false;
  }

  public static String getJsonClass(CDTypeSymbol type) {
    String typeName = type.getName();
    switch (typeName) {
      case "long":
        return "JsonLongValue";
      case "String":
        return "JsonStringValue";
      case "boolean":
        return "JsonBooleanValue";
      case "int":
        return "JsonIntValue";
      case "double":
        return "JsonDoubleValue";
      case "ZonedDateTime":
        return "ZonedDateTime";
      default:
        return typeName;
    }
  }

  public static String getHumanName(ASTCDAttribute attribute) {
    String attributeName = TransformationUtils.capitalize(attribute.getName());
    Optional<String> humanName = getStereotypeValue(attribute.getModifierOpt(),
            TransformationUtils.STEREOTYPE_HUMANNAME);
    return humanName.orElse(capitalize(attributeName.toLowerCase()));
  }

  public static Optional<String> getColumDefinitionAnnotation(ASTCDAttribute attribute) {
    return getStereotypeValue(attribute.getModifierOpt(),
            TransformationUtils.STEREOTYPE_DBCOLUMNDEFINITION);
  }

  public static Optional<String> getDBColumnAnnotation(ASTCDAttribute attribute) {
    return getStereotypeValue(attribute.getModifierOpt(),
            TransformationUtils.STEREOTYPE_DBCOLUMN);
  }

  /* Retrieve modifier from attribute */
  public static ASTModifier getMethodModifier(ASTCDAttribute attr) {
    // if nothing is set don't use package visibility
    if (attr.getModifierOpt().isPresent() && !attr.getModifier().isPrivate()
            && !attr.getModifier().isPublic()
            && !attr.getModifier().isProtected()) {

      ASTModifier clonedMod = attr.getModifier().deepClone();
      clonedMod.setFinal(false);
      clonedMod.setPublic(true);

      return clonedMod;
    } else if (attr.getModifierOpt().isPresent()) {
      ASTModifier clonedMod = attr.getModifier().deepClone();
      clonedMod.setFinal(false);

      return clonedMod;
    } else {
      ASTModifier modifier = CD4AnalysisNodeFactory.createASTModifier();
      modifier.setPublic(true);

      return modifier;
    }
  }

  /**
   * Creates an instance of the {@link ASTCDAttribute} using given attribute
   * definition ( e.g. private List<A> a; )
   *
   * @param attributeDefinition attribute definition to parse
   * @return Optional of the created ast node or Optional.empty() if the
   * attribute definition couldn't be parsed
   */
  public static Optional<ASTCDAttribute> CdAttributeUsingDefinition(String attributeDefinition) {
    checkArgument(!Strings.isNullOrEmpty(attributeDefinition),
            "Attribute can't be added to the CD class because of null or empty attribute definition");
    Optional<ASTCDAttribute> astAttribute = Optional.empty();
    try {
      astAttribute = (new CD4AnalysisParser())
              .parseCDAttribute(new StringReader(attributeDefinition));
    } catch (RecognitionException | IOException e) {
      Log.error("Attribute can't be created: catched exception: " + e);
    }
    return astAttribute;
  }

  public static Optional<ASTValue> getAttributeValue(CDFieldSymbol field) {
    if (field.getAstNode().isPresent() && field.getAstNode().get() instanceof ASTCDAttribute) {
      return ((ASTCDAttribute) field.getAstNode().get()).getValueOpt();
    }
    return Optional.empty();
  }

  public static Optional<String> getTextOfComment(List<Comment> comments) {
    if (comments.isEmpty()) {
      return Optional.empty();
    }
    String text = comments.get(comments.size() - 1).getText();
    if (text.length() < 5 || !text.startsWith("/*") || !text.endsWith("*/")) {
      return Optional.empty();
    }
    String commentText = text.substring(2, text.length() - 3).trim();
    if (commentText.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of("\"" + commentText + "\"");
  }

  public static String dotJoinIfNotEmpty(String first, String second, String... rest) {
    List<String> callParams = Lists.newArrayList(first, second);
    callParams.addAll(Arrays.asList(rest));
    List<String> newParams = new ArrayList<>();
    callParams.stream().filter(s -> !s.isEmpty()).forEach(newParams::add);
    if (newParams.size() > 1) {
      return Joiners.DOT.join(newParams);
    }
    return newParams.size() == 0 ? "" : newParams.get(0);
  }

  public static String comaJoinIfNotEmpty(String first, String second, String... rest) {
    List<String> callParams = Lists.newArrayList(first, second);
    callParams.addAll(Arrays.asList(rest));
    List<String> newParams = new ArrayList<>();
    callParams.stream().filter(s -> !s.isEmpty()).forEach(newParams::add);
    if (newParams.size() > 1) {
      return Joiners.COMMA.join(newParams);
    }
    return newParams.size() == 0 ? "" : newParams.get(0);
  }

  public static List<ASTCDEnumParameter> transformStringToEnumParameter(List<String> params) {
    return params.stream()
            .map(param -> CD4AnalysisMill.cDEnumParameterBuilder().setValue(
                    CD4AnalysisMill.valueBuilder()
                            .setSignedLiteral(LiteralsMill.stringLiteralBuilder().setSource(param).build())
                            .build()).build()).collect(Collectors.toList());
  }

  public static Optional<String> getHumanName(Optional<ASTModifier> modifier) {
    if (!modifier.isPresent() || !modifier.get().getStereotypeOpt().isPresent()) {
      return Optional.empty();
    }
    ASTCDStereotype stereotype = modifier.get().getStereotype();
    for (ASTCDStereoValue value : stereotype.getValueList()) {
      if (STEREOTYPE_HUMANNAME.equals(value.getName())) {
        return value.getValueOpt();
      }
    }
    return Optional.empty();
  }

  public static Optional<String> getStereotypeValue(Optional<ASTModifier> modifier,
                                                    String stereotypeName) {
    if (!modifier.isPresent() || !modifier.get().getStereotypeOpt().isPresent()) {
      return Optional.empty();
    }
    ASTCDStereotype stereotype = modifier.get().getStereotype();
    for (ASTCDStereoValue value : stereotype.getValueList()) {
      if (stereotypeName.equals(value.getName())) {
        return value.getValueOpt();
      }
    }
    return Optional.empty();
  }

  public static Optional<Integer> getMaxLengthValue(CDFieldSymbol field) {
    if (!field.getAstNode().isPresent() || !(field.getAstNode().get() instanceof ASTCDAttribute)) {
      return Optional.empty();
    }
    Optional<String> value = getStereotypeValue(
            ((ASTCDAttribute) field.getAstNode().get()).getModifierOpt(),
            TransformationUtils.STEREOTYPE_MAXLENGTH);
    if (!value.isPresent()) {
      return Optional.empty();
    }
    try {
      return Optional.of(Integer.parseInt(value.get()));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public static void handleAttributeAnnotation(ASTCDAttribute attribute) {
    if (attribute.getModifierOpt().isPresent()) {
      if (attribute.getModifier().getStereotypeOpt().isPresent()) {
        attribute.getModifier().getStereotype().getValueList()
                .removeIf((stereo) -> !ATTRIBUTE_ANNOATION.equals(stereo.getValueOpt().orElse("")));
      }
    }
  }

  public static void handleMethodAnnotation(ASTCDMethod method) {
    if (method.getModifier().getStereotypeOpt().isPresent()) {
      method.getModifier().getStereotype().getValueList()
              .removeIf((stereo) -> !METHOD_ANNOTATION.equals(stereo.getValueOpt().orElse("")));
    }
  }

  public static String removeTOPExtensionIfExistent(String className) {
    if (className.endsWith(TransformationUtils.TOP_EXTENSION)) {
      return className.substring(0, className.indexOf(TransformationUtils.TOP_EXTENSION));
    } else {
      return className;
    }
  }

  /**
   * Generates an error code suffix in format "_ddd" where d is a decimal.
   *
   * @param className
   * @return generated error code suffix in format "xddd" where d is a decimal.
   */
  public static String getErrorCode(String className) {
    int hashCode = Math.abs(className.hashCode());
    String errorCodeSuffix = String.valueOf(hashCode);
    return "x" + (hashCode < 1000 ? errorCodeSuffix : errorCodeSuffix
            .substring(errorCodeSuffix.length() - 3));
  }

  public static String createEnumParameter(String name) {
    StringBuilder paramName = new StringBuilder();
    String delim = "";
    paramName.append("\"");
    for (String s : name.split("_")) {
      paramName.append(delim);
      paramName.append(TransformationUtils.capitalize(s.toLowerCase()));
      delim = " ";
    }
    paramName.append("\"");
    return paramName.toString();
  }

}
