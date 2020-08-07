/* (c) https://github.com/MontiCore/monticore */

/**
 * The common transformation for creating of additional data classes
 *
 */
package common;

import common.util.CDClassBuilder;
import common.util.CDInterfaceBuilder;
import common.util.TransformationUtils;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Joiners;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The common transformation to create additional classes and interfaces for the existing domain types
 *
 */
public abstract class CreateTrafo extends ExtendTrafo {

  /** The interfaceName extension for the created class */
  protected String suffix = "";

  /** The name extension for the created class */
  protected String prefix = "";

  public CreateTrafo(String suffix) {
    this(suffix, "");
  }

  public CreateTrafo(String suffix, String prefix) {
    super();
    this.suffix = suffix;
    this.prefix = prefix;
  }

  @Override
  protected void transform() {
    checkArgument(handcodePath.isPresent());
    super.transform();
  }

  @Override
  // This trafo creates an additional class for the existing domain class
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    String packageName =
        TransformationUtils.CLASSES_PACKAGE + "." + typeSymbol.getName().toLowerCase();
    CDClassBuilder clazzBuilder = new CDClassBuilder().Public();

    String className = typeSymbol.getName() + suffix;
    // Check if a handwritten class exists
    className = checkIfTOPExtension(packageName, className);

    if (typeSymbol.getSuperClass().isPresent()) {
      String superClassName = typeSymbol.getSuperClass().get().getName();
      clazzBuilder.superclass(superClassName + suffix);
      clazzBuilder
          .additionalImport(Joiners.DOT.join(TransformationUtils.getPackageName(getOutputAstRoot()),
              TransformationUtils.CLASSES_PACKAGE, superClassName.toLowerCase(),
              superClassName + suffix));
    }

    ASTCDClass addedClass = clazzBuilder.setName(className).build();
    addedClass.setSymbol(typeSymbol);
    getOutputAst().getCDClassList().add(addedClass);
    return Optional.of(addedClass);
  }

  @Override
  // This trafo creates an additional interface for the existing domain interface
  protected Optional<ASTCDInterface> getOrCreateExtendedInterface(ASTCDInterface domainInterface, CDTypeSymbol typeSymbol) {
    String packageName =
        TransformationUtils.CLASSES_PACKAGE + "." + typeSymbol.getName().toLowerCase();
    CDInterfaceBuilder builder = new CDInterfaceBuilder();

    String interfaceName = prefix + typeSymbol.getName() + suffix;
    // Check if a handwritten class exists
    interfaceName = checkIfTOPExtension(packageName, interfaceName);

    for (CDTypeSymbolReference superInterface: typeSymbol.getInterfaces()) {
      String superInterfaceName = superInterface.getName();
      builder.interfaces(superInterfaceName + suffix);
      builder
          .additionalImport(Joiners.DOT.join(TransformationUtils.getPackageName(getOutputAstRoot()),
              TransformationUtils.CLASSES_PACKAGE, superInterfaceName.toLowerCase(),
              superInterfaceName + suffix));
    }

    ASTCDInterface addedInterface = builder.setName(interfaceName).build();
    addedInterface.setSymbol(typeSymbol);
    getOutputAst().getCDInterfaceList().add(addedInterface);
    return Optional.of(addedInterface);
  }

}
