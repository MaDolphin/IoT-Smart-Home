/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package common;

import common.DexTransformation;
import common.util.TypeHelper;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class AttributeRenameTransformer extends DexTransformation {

  @Override
  protected void transform() {
    checkArgument(symbolTable.isPresent());

    for (ASTCDClass clazz : getAst().getCDClassList()) {
      for (ASTCDAttribute attribute : clazz.getCDAttributeList()) {
        if (attribute.getType() instanceof ASTSimpleReferenceType) {
          List<String> typeNames = ((ASTSimpleReferenceType) attribute.getType()).getNameList();
          if (typeNames.size() > 0 && (new TypeHelper()).isCollection(typeNames.get(0))) {
            attribute.setName(attribute.getName() + "s");
          }
        }
      }
    }
  }
}
