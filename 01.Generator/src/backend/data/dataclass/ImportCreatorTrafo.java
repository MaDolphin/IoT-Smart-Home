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
