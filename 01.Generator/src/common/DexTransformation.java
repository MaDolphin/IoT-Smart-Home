/* (c) https://github.com/MontiCore/monticore */
package common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import common.util.TransformationUtils;
import configure.ConfigureDexGenerator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import ocl.monticoreocl.ocl._ast.ASTCompilationUnit;
import ocl.monticoreocl.ocl._ast.ASTOCLFile;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @since TODO: add version number
 */
// TODO: concatenation von transformationen. aka. sub trafos und hintereinander
// ausführungen

// TODO:
// - Pre / Post conditions
// - dependencies to other trafos
// - sub trafos registrieren
abstract public class DexTransformation {

  private ASTCDCompilationUnit inputCompUnit;

  private ASTCDCompilationUnit outputCompUnit;

  protected Optional<CDSymbolTable> symbolTable = Optional.empty();

  protected Optional<CDSymbolTable> outputSymTab = Optional.empty();

  private boolean checkCoCos = false;

  private boolean noSymTabUpdate = true;

  protected Optional<IterablePath> handcodePath = Optional.empty();

  private List<File> modelPath;

  protected boolean generateTOP = false;

  protected Optional <ASTOCLFile> ocl = Optional.empty();

  /**
   * Set the path for handcoding extensions
   *
   * @param path
   * @return
   */
  public final DexTransformation handcodedPath(IterablePath path) {
    this.handcodePath = Optional.of(path);
    return this;
  }

  public final DexTransformation generateTOPClasses(boolean flag) {
    this.generateTOP = flag;
    return this;
  }
  
  /**
   * Execute transformation
   *
   * @param ast
   * @param symTab
   * @param modelPath
   */
  private final void transform(ASTCDCompilationUnit ast, CDSymbolTable symTab, List<File> modelPath) {
    checkNotNull(ast);
    checkNotNull(ast.getCDDefinition());
    checkNotNull(modelPath);

    this.modelPath = modelPath;

    // set the output
    this.outputCompUnit = ast;
    this.outputSymTab = Optional.ofNullable(symTab);

    // check if an input has been specified. if not the output will be the input
    if (this.inputCompUnit == null) {
      this.inputCompUnit = this.outputCompUnit;
      this.symbolTable = this.outputSymTab;
    }

    // perform transformation
    this.transform();

    // check context conditions if enabled
    if (this.checkCoCos) {
      new CD4ACoCos().getCheckerForAllCoCos().checkAll(getOutputAst());
    }

    // update symbol table
    if (!this.noSymTabUpdate) {
      ConfigureDexGenerator.createCDSymbolTable(ast, modelPath);
    }
  }

  protected List<File> getModelPath() {
    return this.modelPath;
  }

  public final void transform(ASTCDCompilationUnit ast, CDSymbolTable symTab) {
    checkNotNull(symTab);
    transform(ast, symTab, Lists.newArrayList());
  }

  public final void transform(ASTCDCompilationUnit ast, List<File> modelPath) {
    transform(ast, ConfigureDexGenerator.createCDSymbolTableIfNecessary(ast, modelPath), modelPath);
  }

  public final void transform(ASTCDCompilationUnit ast) {
    // check if symbol table exists. If so create it form the ast
    if (ast.getCDDefinition().getEnclosingScopeOpt().isPresent()) {
      transform(ast, ConfigureDexGenerator.buildCDSymbolTableFromAST(ast), Lists.newArrayList());
    }
    else {
      transform(ast, null, Lists.newArrayList());
    }
  }

  /**
   * Specify an additional input ast if existing
   *
   * @param ast
   * @param symTab
   */
  private final DexTransformation input(ASTCDCompilationUnit ast, CDSymbolTable symTab,
      List<File> modelPath) {
    checkNotNull(ast);
    checkNotNull(ast.getCDDefinition());

    // create a new symbol table
    if (modelPath != null) {
      this.symbolTable = Optional.of(ConfigureDexGenerator.createCDSymbolTable(ast, modelPath));
    }
    else {
      this.symbolTable = Optional.ofNullable(symTab);
    }

    this.inputCompUnit = ast;
    return this;
  }

  public final DexTransformation input(ASTCDCompilationUnit ast, CDSymbolTable symTab) {
    return input(ast, symTab, null);
  }

  public final DexTransformation input(ASTCDCompilationUnit ast, List<File> modelPath) {
    return input(ast, null, modelPath);
  }

  public final DexTransformation input(ASTCDCompilationUnit ast) {
    if (ast.getCDDefinition().getEnclosingScopeOpt().isPresent()) {
      return input(ast, ConfigureDexGenerator.buildCDSymbolTableFromAST(ast));
    }
    else {
      return input(ast, null, null);
    }
  }

  public final DexTransformation input(Optional<ASTCompilationUnit> ast) {
    if (ast.isPresent() && ast.get().getOCLFile() != null) {
      this.ocl = Optional.of(ast.get().getOCLFile());
    }
    return this;
  }

  /**
   * Specify if context conditions should be executed
   */
  public final DexTransformation doCoCoCheck() {
    this.checkCoCos = true;
    return this;
  }

  /**
   * Make sure the symbol table is updated after all transformations have been
   * performed
   */
  public final DexTransformation updateSymbolTable() {
    this.noSymTabUpdate = false;
    return this;
  }

  /**
   * check if the top class exists
   *
   * @param generateTOP
   * @param type
   * @param packageName
   * @param handcodePath
   * @return
   */
  public static boolean isTopClassExistend(boolean generateTOP, String type, String packageName, Optional<IterablePath> handcodePath) {
    return generateTOP && handcodePath.isPresent() && TransformationUtils.existsHandwrittenFile(type, packageName, handcodePath.get(), TransformationUtils.JAVA_FILE_EXTENSION);
  }

  /**
   * check if the top class exists
   *
   * @param generateTOP
   * @param type
   * @param handcodePath
   * @return
   */
  public static boolean isTopClassExistend(boolean generateTOP, String type, Optional<IterablePath> handcodePath) {
    String packageName = TransformationUtils.CLASSES_PACKAGE + "."
        + type.toLowerCase();

    return isTopClassExistend(generateTOP, type, packageName, handcodePath);
  }

  /**
   * return input ast
   *
   * @return
   */
  protected final ASTCDDefinition getAst() {
    return this.inputCompUnit.getCDDefinition();
  }

  protected final ASTCDCompilationUnit getAstRoot() {
    return this.inputCompUnit;
  }

  /**
   * Return output ast
   *
   * @return
   */
  protected final ASTCDDefinition getOutputAst() {
    return this.outputCompUnit.getCDDefinition();
  }

  protected final ASTCDCompilationUnit getOutputAstRoot() {
    return this.outputCompUnit;
  }

  protected final GlobalExtensionManagement getGlex() {
    return ConfigureDexGenerator.getGlex();
  }

  /**
   * This method is called when the transformation should be executed
   */
  protected abstract void transform();
}
