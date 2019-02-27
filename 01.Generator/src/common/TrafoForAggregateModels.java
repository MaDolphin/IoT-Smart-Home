/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

/**
 * The common transformation for creating of additional data classes
 *
 * @author Galina Volkova
 */
package common;

import common.util.CDClassBuilder;
import common.util.CompilationUnit;
import common.util.TransformationUtils;
import common.util.TypeHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;
import static common.util.TransformationUtils.setProperty;

/**
 * The common transformation to create additional classes for aggregate models
 *
 * @author Galina Volkova
 */
public abstract class TrafoForAggregateModels extends CreateTrafo {

  protected TypeHelper typeHelper = new TypeHelper();

  public TrafoForAggregateModels(String suffix) {
    super(suffix);
  }

  public TrafoForAggregateModels(String suffix, String prefix) {
    super(suffix, prefix);
  }

  @Override
  // This trafo creates an additional class for the aggregate class
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    String aggregatesPackage = TransformationUtils.getPackageName(getOutputAstRoot()) + "."
        + TransformationUtils.DTOS_PACKAGE + "." +
        domainClass.getName().toLowerCase() + ".*";

    String className = typeSymbol.getName() + suffix;
    CDClassBuilder clazzBuilder = new CDClassBuilder();
    getSuperclassName(domainClass, typeSymbol).ifPresent(clazzBuilder::superclass);
    ASTCDClass addedClass = clazzBuilder.Public()
        .subpackage(aggregatesPackage).setName(className).build();
    addedClass.setSymbol(typeSymbol);

    setProperty(addedClass, SUBPACKAGE_PROPERTY, TransformationUtils.DTOS_PACKAGE);
    getOutputAst().getCDClassList().add(addedClass);

    return Optional.of(addedClass);
  }

  @Override
  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    checkArgument(symbolTable.isPresent());

    domainClass.getInterfaceList().forEach(interf -> TransformationUtils
        .addImportForCDType(extendedClass, interf, getAstRoot(), symbolTable.get()));

    // Add imports for all associations
    symbolTable.get()
        .getAllAssociationsForType(typeSymbol.getName()).forEach(assoc -> TransformationUtils
        .addStarImportForCDType(extendedClass, assoc.getTargetType().getName(), getAstRoot()));

    domainClass.getCDAttributeList().forEach(attr -> TransformationUtils
        .addImportForCdOrDtoType(extendedClass, attr.getType(), getAstRoot(), symbolTable.get()));

    if (domainClass.getSuperclassOpt().isPresent()) {
      // add import for the super class
      TransformationUtils
          .addImportForCdOrDtoType(extendedClass, domainClass.getSuperclass(), getAstRoot(),
              symbolTable.get());
      Optional<CDTypeSymbol> cdType = symbolTable.get()
          .resolve(TypesPrinter.printType(domainClass.getSuperclass()));

      // add imports for the attribute of the super class
      if (cdType.isPresent() && cdType.get().getAstNode().isPresent() && cdType.get().getAstNode()
          .get() instanceof ASTCDClass) {
        ((ASTCDClass) cdType.get().getAstNode().get()).getCDAttributeList()
            .forEach(attr -> TransformationUtils
                .addImportForCdOrDtoType(extendedClass, attr.getType(), getAstRoot(),
                    symbolTable.get()));
      }
    }

    getImports(typeSymbol).forEach(i -> TransformationUtils
        .addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, i));

  }

}
