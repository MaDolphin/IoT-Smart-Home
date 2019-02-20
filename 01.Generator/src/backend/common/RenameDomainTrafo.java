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
