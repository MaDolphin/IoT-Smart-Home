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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import common.DexTransformation;
import common.util.CDSimpleReferenceBuilder;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

import static com.google.common.base.Preconditions.checkArgument;

// TODO: extend this class to search for predefined types List,Set, and Optional.
// if the types are found, then include the import
public class TypesConverter extends DexTransformation {

  private static final String OPTIONAL_FULL = "java.util.Optional";

  private static final String OPTIONAL = "Optional";

  @Override
  public void transform() {

    if (foundOptional(getAst())) {
      // remove import and make it java.util.Optional
      ASTImportStatement opt = null;
      for (ASTImportStatement statement : getAstRoot().getImportStatementList()) {
        if (OPTIONAL_FULL.equals(Joiner.on(".").join(statement.getImportList()))) {
          opt = statement;
        }
      }

      if (opt != null) {
        getAstRoot().getImportStatementList().remove(opt);
      }

      // add Optional
      getAstRoot().getImportStatementList().add(
          CD4AnalysisMill.importStatementBuilder()
              .setImportList(Lists.newArrayList("java", "util", "Optional")).build());

    }
  }

  /**
   * find all optional attributes in classes and convert them to
   * java.util.Optional
   *
   * @param ast
   * @return
   */
  private boolean foundOptional(ASTCDDefinition ast) {
    boolean foundOptional = false;

    for (ASTCDClass clazz : ast.getCDClassList()) {

      for (ASTCDAttribute attr : clazz.getCDAttributeList()) {

        if (TypesPrinter.printType(attr.getType()).startsWith(OPTIONAL)
            || TypesPrinter.printType(attr.getType()).startsWith(OPTIONAL_FULL)) {
          foundOptional = true;
          // find the generic type
          ASTSimpleReferenceType ref = (ASTSimpleReferenceType) attr.getType();
          checkArgument(ref.getTypeArgumentsOpt().isPresent());

          attr.setType(new CDSimpleReferenceBuilder()
              .name(Lists.newArrayList(OPTIONAL))
              .setTypeArguments(ref.getTypeArguments()).build());

        }
      }
    }
    return foundOptional;
  }

}
