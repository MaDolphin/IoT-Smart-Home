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

import static com.google.common.base.Preconditions.checkArgument;

import common.DexTransformation;
import common.util.TypeHelper;
import common.util.CDSimpleReferenceBuilder;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.prettyprint.AstPrinter;

/**
 * This transformation replaces all invalid definitions of e.g. List<int>,
 * Set<char> etc. with valid once e.g. List<Integer>, Set<Character>
 *
 * @author Alexander Roth
 */
public class CollectionTypesWrapper extends DexTransformation {

  /**
   * Run this method to execute the transformation
   *
   */
  @Override
  protected void transform() {

    TypeHelper t = new TypeHelper();

    for (ASTCDClass c : getAst().getCDClassList()) {

      for (ASTCDAttribute attr : c.getCDAttributeList()) {

        if (attr.getType() instanceof ASTSimpleReferenceType) {
          ASTSimpleReferenceType attrType = (ASTSimpleReferenceType) attr.getType();

          if (attrType.getTypeArgumentsOpt().isPresent()) {
            String type = new AstPrinter().printType(attr.getType());
            type = type.substring(type.indexOf("<")+1).replace(">", "");
            setNewType(attrType, t.getWrapperTypeWithoutPackage(type));
          }
        }
      }
    }

    /**
     * Fix qualified association types. Use wrapper type instead of primitive types
     */
    for (ASTCDAssociation as : getAst().getCDAssociationList()) {
      if (as.getLeftQualifierOpt().isPresent() && as.getLeftQualifier().getTypeOpt().isPresent()) {
        String type = t.getWrapperTypeWithoutPackage(new AstPrinter().printType(as.getLeftQualifier().getType()));
        as.getLeftQualifier().setType(new CDSimpleReferenceBuilder().name(type).build());
      }

      if (as.getRightQualifierOpt().isPresent() && as.getRightQualifier().getTypeOpt().isPresent()) {
        String type = t.getWrapperTypeWithoutPackage(new AstPrinter().printType(as.getRightQualifier().getType()));
        as.getRightQualifier().setType(new CDSimpleReferenceBuilder().name(type).build());
      }
    }
  }

  /**
   * Set the new generic type
   *
   * @param type    the type that will receive the generic type newType
   * @param newType the new type to be set
   */
  private void setNewType(ASTSimpleReferenceType type, String newType) {
    checkArgument(type.getTypeArgumentsOpt().isPresent(), "Failed while trying to set the generic type.");

    type.getTypeArguments().getTypeArgumentList().clear();

    type.getTypeArguments().getTypeArgumentList().add(new CDSimpleReferenceBuilder().name(newType).build());
  }
}
