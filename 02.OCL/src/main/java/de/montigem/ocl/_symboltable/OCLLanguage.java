package de.montigem.ocl._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

import java.util.Optional;

public class OCLLanguage extends OCLLanguageTOP {

  private static final String LANG_NAME = "OCL for MontiGEM";

  private static final String FILE_EXTENSION = "ocl";

  public OCLLanguage() {
    super(LANG_NAME, FILE_EXTENSION);
  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new OCLModelLoader(this);
  }

  @Override
  public Optional<OCLSymbolTableCreator> getSymbolTableCreator(ResolvingConfiguration resolvingConfiguration, MutableScope mutableScope) {
    return Optional.of(new OCLSymbolTableCreator(resolvingConfiguration, mutableScope));
  }
}
