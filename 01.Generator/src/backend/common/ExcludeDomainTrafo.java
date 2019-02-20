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
