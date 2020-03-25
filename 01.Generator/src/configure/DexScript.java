/* (c) https://github.com/MontiCore/monticore */
package configure;

import backend.common.CoreTemplate;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import common.CDSymbolTable;
import common.DexCoreTemplate;
import common.EnumCoCo;
import common.util.CompilationUnit;
import common.util.TransformationUtils;
import de.montigem.ocl._symboltable.OCLLanguage;
import de.montigem.ocl._symboltable.OCLModelLoader;
import de.monticore.ModelingLanguageFamily;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.umlcd4a.prettyprint.AstPrinter;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import frontend.common.FrontendCoreTemplate;
import groovy.lang.Script;
import ocl.monticoreocl.ocl._ast.ASTCompilationUnit;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.*;
import static common.util.TransformationUtils.*;
import static frontend.common.FrontendTransformationUtils.getImportPathCheckHWC;

/**
 * Script class for processing Dex models
 *
 */
public class DexScript extends Script implements GroovyRunner {

  protected static final String[] DEFAULT_IMPORTS = { "de.monticore.umlcd4a._ast" };

  protected static final String LOG = "DexScript";

  protected AstPrinter astPrinter = new AstPrinter();
  
  protected Collection<ASTCDCompilationUnit> cdList = Lists.newArrayList();

  /**
   * Parses the specified class diagram file and creates the corresponding AST representation.
   *
   * @param model - diagram file to be parsed
   * @return the class diagram AST
   */
  public Optional<ASTCDCompilationUnit> parseClassDiagram(File model) {
    debug("Start parsing of the cd model " + model.getAbsolutePath());
    try {
      CD4AnalysisParser parser = new CD4AnalysisParser();
      java.util.Optional<ASTCDCompilationUnit> ast = parser.parse(model.getAbsolutePath());

      if (!parser.hasErrors() && ast.isPresent()) {
        debug("CD Model " + model.getAbsolutePath() + " parsed successfully");
      }
      else {
        error("There are parsing errors while parsing of the model " + model.getAbsolutePath());
      }
      Optional<ASTCDCompilationUnit> result = Optional.empty();
      if (ast.isPresent()) {
        result = Optional.of(ast.get());
      }
      return result;
    }
    catch (IOException e) {
      // TODO: is this the best option? also see above (this could be handled by
      // the parser internally)
      throw new RuntimeException(e);
    }
  }

  /**
   * Parses the specified ocl file and creates the corresponding AST representation.
   *
   * @param modelFullQualifiedFilename - diagram file to be parsed
   * @param modelPaths - the model path
   * @return the class diagram AST
   */
  public Optional<ASTCompilationUnit> parseOCLFile(String modelFullQualifiedFilename, List<File> modelPaths) {
    debug("Start parsing of the ocl model " + modelFullQualifiedFilename);

    OCLLanguage ocllang = new OCLLanguage();
    CD4AnalysisLanguage cd4aLang = new CD4AnalysisLanguage();
    ModelPath modelPath = new ModelPath(modelPaths.stream().map(File::toPath).collect(Collectors.toList()));

    ModelingLanguageFamily modelingLanguageFamily = new ModelingLanguageFamily();
    modelingLanguageFamily.addModelingLanguage(ocllang);
    modelingLanguageFamily.addModelingLanguage(cd4aLang);
    GlobalScope scope = new GlobalScope(modelPath, modelingLanguageFamily);

    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(ocllang.getResolvingFilters());
    resolvingConfiguration.addDefaultFilters(cd4aLang.getResolvingFilters());

    OCLModelLoader oclModelLoader = ocllang.getModelLoader();


    Collection<ASTCompilationUnit> models = oclModelLoader
        .loadModelsIntoScope(modelFullQualifiedFilename, modelPath, scope, resolvingConfiguration);
    if (models.isEmpty()) {
      info("No model found with name: " + modelFullQualifiedFilename);
      return Optional.empty();
    }
    if(models.size() > 1)
      error("Multiple models found with name: " + modelFullQualifiedFilename);

    return Optional.of(models.iterator().next());
  }

  /**
   * Parses the given model file.
   *
   * @param model - path to the model file
   * @return model AST
   */
  public Optional<ASTCDCompilationUnit> parseClassDiagram(Path model) {
    if (!model.toFile().isFile()) {
      Log.error("0xA1016 Cannot read " + model.toString() + " as it is not a file.");
    }
    return parseClassDiagram(model.toFile());
  }

  public ASTCDCompilationUnit createNewAst(String name, String packageName) {
    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setCDDefinition(CD4AnalysisMill.cDDefinitionBuilder().setName(name).build())
        .setPackageList(
            Splitters.DOT.splitToList(packageName)).build();
  }

  /**
   * Checks all CD4A CoCos on the given AST.
   *
   * @param ast the ast to check the CoCos on.
   */
  public void checkCD4ACoCos(ASTCDCompilationUnit ast) {
    debug("Checking CD4A CoCos");
    // check if the symtab is avaliable
    checkArgument(ast.getCDDefinition().getEnclosingScopeOpt().isPresent());
    new CD4ACoCos().getCheckerForAllCoCos().checkAll(ast);
  }

  /**
   * Checks CD4Code CoCos on the given AST.
   *
   * @param ast the ast to check the CoCos on.
   */
  public void checkMontiGemCoCos(ASTCDCompilationUnit ast) {
    debug("Checking MontiGem CoCos");
    // check if the symtab is avaliable
    checkArgument(ast.getCDDefinition().getEnclosingScopeOpt().isPresent());
    new CD4ACoCos().getCheckerForCode().addCoCo(new EnumCoCo()).checkAll(ast);
  }

  /**
   * Starts code generation by using the standard template set for Java.
   *
   * @param ast          - top ast node of the Cd4Analysis grammar
   * @param glex         - TODO: write me!
   * @param modelPath    - the model path
   * @param outputDir    - the output directory
   * @param targetPath   - the path for the hand-written artifacts of the target system
   * @param templatePath - the path for the hand-written templates
   */
  public void generateJavaFiles(ASTCDCompilationUnit ast,
      GlobalExtensionManagement glex, List<String> modelPath, File outputDir,
      IterablePath targetPath, IterablePath templatePath) {

    Preconditions.checkArgument(ast != null && ast.getCDDefinition() != null);

    ASTCDDefinition astDef = ast.getCDDefinition();
    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDir);
    setup.setTracing(false);
    setup.setGlex(glex);

    final GeneratorEngine generator = new GeneratorEngine(setup);

    // generate classes
    astDef.getCDClassList()
        .forEach(clazz -> generateJavaForType(clazz, ast, generator, CoreTemplate.CLASS));

    // generate interfaces
    astDef.getCDInterfaceList()
        .forEach(interf -> generateJavaForType(interf, ast, generator, CoreTemplate.INTERFACE));

    // generate Enumerations
    astDef.getCDEnumList().forEach(
        enumeration -> generateJavaForType(enumeration, ast, generator, CoreTemplate.ENUM));
  }

  public void generateTypeScriptFiles(ASTCDCompilationUnit ast,
      GlobalExtensionManagement glex, List<String> modelPath, File outputDir,
      IterablePath targetPath, IterablePath templatePath) {

    Preconditions.checkArgument(ast != null && ast.getCDDefinition() != null);

    ASTCDDefinition astDef = ast.getCDDefinition();

    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(outputDir);
    setup.setTracing(false);
    setup.setGlex(glex);

    final GeneratorEngine generator = new GeneratorEngine(setup);

    // generate classes
    astDef.getCDClassList().forEach(
        clazz -> generateTypeScriptForType(clazz, ast, generator, FrontendCoreTemplate.CLASS,
            setup.getOutputDirectory()));

    // generate interfaces
    astDef.getCDInterfaceList().forEach(interf -> generateTypeScriptForType(interf, ast, generator,
        FrontendCoreTemplate.INTERFACE, setup.getOutputDirectory()));

    // generate Enumerations
    astDef.getCDEnumList().forEach(
        enumeration -> generateTypeScriptForType(enumeration, ast, generator,
            FrontendCoreTemplate.ENUM, setup.getOutputDirectory()));
  }

  private void generateJavaForType(ASTCDType type, ASTCDCompilationUnit ast,
      GeneratorEngine generator, DexCoreTemplate startTemplate) {
    String headPackage = TransformationUtils.getPackageName(ast);
    String packageName = getPackageName(type, headPackage);
    final Path filePath = Paths.get(
        packageName.replace('.', java.io.File.separatorChar), type.getName()
            + JAVA_FILE_EXTENSION);

    Collection<String> importList = TransformationUtils.getJavaImports(ast);

    generator.generate(startTemplate.getTemplate(), filePath,
        type, packageName, getImports(type, ast, importList, packageName));
  }

  private void generateTypeScriptForType(ASTCDType type,
      ASTCDCompilationUnit ast, GeneratorEngine generator, DexCoreTemplate startTemplate,
      File outputDirectory) {
    String packageName = getPackageName(type, "");
    String fileExtension = getSingleProperty(type, CompilationUnit.FILEEXTENSION_PROPERTY)
        .orElse("");
    String simpleFileName = TransformationUtils
        .getSingleProperty(type, FILENAME_PROPERTY).orElse(type.getName().toLowerCase());

    Collection<String> importList = astPrinter.printImportList(ast
        .getImportStatementList());

    String extendFileProperty = TransformationUtils
        .getSingleProperty(type, EXTENDFILE_PROPERTY).orElse("");

    String fileName = dotJoinIfNotEmpty(simpleFileName, fileExtension,
        TYPESCRIPT_FILE_EXTENSION);

    if (EXTENDFILE_PROPERTY_VALUE.equals(extendFileProperty)) {
      fileName = Joiner.on(java.io.File.separatorChar).skipNulls()
          .join(outputDirectory.getAbsolutePath(),
              packageName.replace('.', java.io.File.separatorChar),
              fileName);
      try (
          FileWriter writer = new FileWriter(fileName, true)) {
        generator.generate(startTemplate.getTemplate(),
            writer, type, packageName,
            getImports(type, ast, importList, packageName, false));
        return;
      }
      catch (IOException e) {
        Log.error(e.getMessage());
      }
    }
    final Path filePath = Paths.get(
        packageName.replace('.', java.io.File.separatorChar), fileName);

    generator.generate(startTemplate.getTemplate(),
        filePath, type, packageName,
        getImports(type, ast, importList, packageName, false));

  }

  private String getPackageName(ASTNode node, String packageName) {
    Optional<String> subPackage = getSingleProperty(node,
        CompilationUnit.SUBPACKAGE_PROPERTY);
    return (subPackage.isPresent() ?
        dotJoinIfNotEmpty(packageName, subPackage.get()) :
        packageName);
  }

  private Collection<String> getImports(ASTCDType node,
      ASTCDCompilationUnit ast, Collection<String> astImports, String packageName,
      boolean withDefault) {
    return TransformationUtils.getImports(node, astImports, withDefault);
  }

  private Collection<String> getImports(ASTCDType node,
      ASTCDCompilationUnit ast, Collection<String> astImports, String packageName) {
    return getImports(node, ast, astImports, packageName, true);
  }
  
  public void aggregateCD(ASTCDCompilationUnit ast) {
    cdList.add(ast);
  }
  
  public void clearClassDiagram(ASTCDCompilationUnit ast) {
    ast.getCDDefinition().clearCDAssociations();
    ast.getCDDefinition().clearCDClasss();
    ast.getCDDefinition().clearCDEnums();
    ast.getCDDefinition().clearCDInterfaces();
  }

  public ASTCDCompilationUnit createClassDiagram() {
    String packageName = TransformationUtils.MONTIGEM_BASE;
    ASTCDCompilationUnitBuilder bCDCompUnit= CD4AnalysisMill.cDCompilationUnitBuilder();
    ASTCDDefinitionBuilder bCDDef = CD4AnalysisMill.cDDefinitionBuilder();
    return bCDCompUnit.setCDDefinition(bCDDef.setName("Aggregates").build()).addPackage(packageName).build();
  }

  public Collection<ASTCDCompilationUnit> getParsedCds() {
    return cdList;
  }

  /**
   * Executes the default DEx Groovy script.
   *
   * @param configuration
   */
  public void run(Optional<Configuration> configuration) {
    try {
      ClassLoader l = DexScript.class.getClassLoader();
      String script = Resources.asCharSource(
          l.getResource("configure/domainBackend.groovy"), Charset.forName("UTF-8"))
          .read();
      run(configuration, script);
    }
    catch (IOException e) {
      Log.error("Failed to load default DEx script.", e);
    }
  }

  /**
   * Executes the given DEx script with the given optional configuration.
   *
   * @param configuration - of this DEx execution
   * @param script        to execute (NOT file or path, the actual Groovy source code)
   */
  public void run(Optional<Configuration> configuration, String script) {
    GroovyInterpreter.Builder builder = getBuilder();

    if (configuration.isPresent()) {
      DexConfiguration dexConfig = DexConfiguration.withConfiguration(configuration
          .get());
      // we add the configuration object as property with a special property
      // name
      builder.addVariable(DexConfiguration.CONFIGURATION_PROPERTY, dexConfig);

      dexConfig.getAllValues().forEach((key, value) -> builder.addVariable(key, value));

      // after adding everything we override a couple of known variable bindings
      // to have them properly typed in the script
      builder.addVariable(DexConfiguration.Options.MODEL.toString(),
          dexConfig.getModel());
      builder.addVariable(DexConfiguration.Options.MODELS.toString(),
          dexConfig.getModels().getResolvedPaths());
      builder.addVariable(DexConfiguration.Options.CMDMODEL.toString(),
              dexConfig.getCmdModel());
      builder.addVariable(DexConfiguration.Options.OCL.toString(),
          dexConfig.getOCL());
      builder.addVariable(DexConfiguration.Options.MODELPATH.toString(),
          dexConfig.getModelPath());
      builder.addVariable(DexConfiguration.Options.OUT.toString(),
          dexConfig.getOut());
      builder.addVariable(DexConfiguration.Options.TEST_OUT.toString(),
          dexConfig.getTestOut());
      builder.addVariable(DexConfiguration.Options.HANDCODEDPATH.toString(),
          dexConfig.getHandcodedPath());
      builder.addVariable(DexConfiguration.Options.TEMPLATEPATH.toString(),
          dexConfig.getTemplatePath());
    }

    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }

  /* Temporary hookpoint for DexDevScript to supply itself as base class */
  protected GroovyInterpreter.Builder getBuilder() {
    return GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(DexScript.class)
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));
  }

  // #######################
  // log functions
  // #######################

  public boolean isDebugEnabled() {
    return Log.isDebugEnabled(LOG);
  }

  public void debug(String msg) {
    Log.debug(msg, LOG);
  }

  public void debug(String msg, Throwable t) {
    Log.debug(msg, t, LOG);
  }

  public boolean isInfoEnabled() {
    return Log.isInfoEnabled(LOG);
  }

  public void info(String msg) {
    Log.info(msg, LOG);
  }

  public void info(String msg, Throwable t) {
    Log.info(msg, t, LOG);
  }

  public void warn(String msg) {
    Log.warn(msg);
  }

  public void warn(String msg, Throwable t) {
    Log.warn(msg, t);
  }

  public void error(String msg) {
    Log.error(msg);
  }

  public void error(String msg, Throwable t) {
    Log.error(msg, t);
  }

  /**
   * @see groovy.lang.Script#run()
   */
  @Override
  public Object run() {
    return true;
  }

  @Override
  public void run(String s, Configuration configuration) {
    new DexScript().run(Optional.ofNullable(configuration), s);
  }

  public CDSymbolTable createSymTab(ASTCDCompilationUnit compUnit, List<File> modelPaths) {
    return ConfigureDexGenerator.createCDSymbolTable(compUnit, modelPaths);
  }

  public Map<String, List<ASTCDClass>> createImportMap() {
    return new HashMap<String, List<ASTCDClass>>();
  }

  public void getClassesFromAst(Map<String, List<ASTCDClass>> map, ASTCDCompilationUnit compUnit, String pckg) {
    if (map == null) {
      map = new HashMap<String, List<ASTCDClass>>();
    }

    map.putIfAbsent(pckg, new ArrayList<>());

    map.get(pckg).addAll(compUnit.getCDDefinition().getCDClassList());
  }


  public void generateTSConfigFile(Map<String, List<ASTCDClass>> map, File out, IterablePath hwc) {
    Map<String,String> importMap = new HashMap<>();

    Iterator<Map.Entry<String, List<ASTCDClass>>> it = map.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, List<ASTCDClass>> pair = it.next();

      for (ASTCDClass clazz : pair.getValue()){
        String fileExtension = "";
        String className = clazz.getName();
        if (pair.getKey().equals(DTOS_PACKAGE) || pair.getKey().equals(CLASSES_PACKAGE)) {
          fileExtension = DTOS_PACKAGE;
          className=className + "-dto";
        }

        importMap.put(className, getImportPathCheckHWC(clazz.getName(), Optional.of(hwc), fileExtension, pair.getKey(), Optional.empty()));
      }
      it.remove();
    }

    final GeneratorSetup tsConfigGeneratorSetup = new GeneratorSetup();
    tsConfigGeneratorSetup.setTracing(true);
    tsConfigGeneratorSetup.setOutputDirectory(out);
    final GeneratorEngine generator = new GeneratorEngine(tsConfigGeneratorSetup);
    generator.generateNoA("frontend.data.CDToolTSConfig", Paths.get("tsconfig.json"), importMap);

  }


}
