/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.ocl._symboltable;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class OCLModelLoader extends OCLModelLoaderTOP {

  public OCLModelLoader(OCLLanguage language) {
    super(language);
  }

  @Override
  protected void createSymbolTableFromAST(final ocl.monticoreocl.ocl._ast.ASTCompilationUnit ast, final String modelName,
      final MutableScope enclosingScope, final ResolvingConfiguration resolvingConfiguration) {
    final OCLSymbolTableCreator symbolTableCreator =
        getModelingLanguage().getSymbolTableCreator(resolvingConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          OCLModelLoader.class.getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("0xA7001x743 Top scope of model " + modelName + " is expected to be an artifact scope, but"
            + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", OCLModelLoader.class.getSimpleName());
    }
    else {
      Log.warn("0xA7002x743 No symbol created, because '" + getModelingLanguage().getName()
          + "' does not define a symbol table creator.");
    }
  }
}
