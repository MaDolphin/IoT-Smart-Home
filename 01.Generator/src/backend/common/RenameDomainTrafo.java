/* (c) https://github.com/MontiCore/monticore */

package backend.common;

import common.DexTransformation;
import common.util.TransformationUtils;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.se_rwth.commons.Joiners;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;

public class RenameDomainTrafo extends DexTransformation {

  @Override
  protected void transform() {
    checkArgument(symbolTable.isPresent());
    checkArgument(handcodePath.isPresent());
    if (!generateTOP) {
      return;
    }
    for (ASTCDClass clazz : getAst().getCDClassList()) {
      Collection<String> packageProperty = TransformationUtils
          .getProperty(clazz, SUBPACKAGE_PROPERTY);
      String packageName = packageProperty.size() == 1 ?
          packageProperty.iterator().next() :
          Joiners.DOT.join(TransformationUtils.CLASSES_PACKAGE, clazz.getName().toLowerCase());
      // Check if a handwritten class exists
      if (TransformationUtils
          .existsHandwrittenFile(clazz.getName(), packageName, handcodePath.get(),
              TransformationUtils.JAVA_FILE_EXTENSION)) {
        // Rename class
        clazz.setName(clazz.getName() + TransformationUtils.TOP_EXTENSION);
        // TOP classes are always abstract
        ASTModifier modifier = clazz.getModifierOpt().isPresent() ?
            clazz.getModifier() :
            CD4AnalysisNodeFactory
                .createASTModifier();
        modifier.setAbstract(true);
        clazz.setModifier(modifier);
        // change constructor
        for (ASTCDConstructor constructor : clazz.getCDConstructorList()) {
          constructor.setName(clazz.getName());
        }
      }
    }
  }

}
