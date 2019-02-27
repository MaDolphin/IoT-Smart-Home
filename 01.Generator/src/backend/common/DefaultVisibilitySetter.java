/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package backend.common;

import common.DexTransformation;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.Optional;

/**
 * This transformation makes all classes, interfaces, and enums publicly
 * visible.
 *
 * @author Alexander Roth
 */
public class DefaultVisibilitySetter extends DexTransformation {

  @Override
  protected void transform() {

    // set default visibility of classes to public
    for (ASTCDClass clazz : getAst().getCDClassList()) {
      clazz.setModifier(createModifierIfNecessary(clazz.getModifierOpt()));
    }

    // set default visibility of enums to public
    for (ASTCDEnum enu : getAst().getCDEnumList()) {
      enu.setModifier(createModifierIfNecessary(enu.getModifierOpt()));
    }

    // set default visibility of interfaces to public
    for (ASTCDInterface interf : getAst().getCDInterfaceList()) {
      interf.setModifier(createModifierIfNecessary(interf.getModifierOpt()));
    }
  }

  /**
   * Create a modifier if it does not exist already.
   *
   * @param modifier the modifier
   * @return either the modifier set to public
   */
  public static ASTModifier createModifierIfNecessary(Optional<ASTModifier> modifier) {
    if (modifier.isPresent()) {
      // if nothing is set
      if (!modifier.get().isPublic() && !modifier.get().isPrivate()
          && !modifier.get().isProtected()) {

        modifier.get().setPublic(true);
      }
      return modifier.get();
    }
    else {

      ASTModifier newModifier = CD4AnalysisNodeFactory.createASTModifier();
      newModifier.setPublic(true);

      return newModifier;
    }
  }

}
