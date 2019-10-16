/* (c) https://github.com/MontiCore/monticore */

package backend.data.dataclass;

import common.DexTransformation;
import common.util.CDClassBuilder;
import common.util.TransformationUtils;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;

import static com.google.common.base.Preconditions.checkArgument;

public class ImportCreatorTrafo extends DexTransformation {

  @Override
  protected void transform() {
    checkArgument(symbolTable.isPresent());

    for (ASTCDClass clazz : getAst().getCDClassList()) {
      // Add import for superclass
      if (clazz.getSuperclassOpt().isPresent()) {
        TransformationUtils.addImportForCDType(clazz, clazz.getSuperclass(), getAstRoot(), symbolTable.get());
      }

      // Add imports for all associations
      for (CDAssociationSymbol assoc : symbolTable.get().getAllAssociationsForType(clazz.getName())) {
        TransformationUtils.addStarImportForCDType(clazz, assoc.getTargetType().getName(), getAstRoot());
      }

      for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
        TransformationUtils.addImportForCDType(clazz, attr.getType(), getAstRoot(), symbolTable.get());
      }

      // Add hibernate Imports
      TransformationUtils.addPropertyValue(clazz, CDClassBuilder.IMPORTS_PROPERTY,
          "javax.persistence.*");
    }
  }
}
