/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
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
