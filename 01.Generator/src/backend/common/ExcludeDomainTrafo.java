/* (c) https://github.com/MontiCore/monticore */

package backend.common;

import common.DexTransformation;
import common.util.TransformationUtils;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

import java.util.ArrayList;
import java.util.List;

import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;

public class ExcludeDomainTrafo extends DexTransformation {

  @Override
  protected void transform() {
    List<ASTCDClass> toRemove = new ArrayList<>();
    for (ASTCDClass clazz : getAst().getCDClassList()) {
      if (!TransformationUtils
          .getSingleProperty(clazz, SUBPACKAGE_PROPERTY).isPresent()) {
        toRemove.add(clazz);
      }
    }
    getAst().getCDClassList().removeAll(toRemove);
  }

}
