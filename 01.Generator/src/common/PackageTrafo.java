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

package common;

import common.DexTransformation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;
import static common.util.TransformationUtils.CLASSES_PACKAGE;
import static common.util.TransformationUtils.setProperty;

public class PackageTrafo extends DexTransformation {

  @Override
  protected void transform() {
    // checkArgument(symbolTable.isPresent());
    List<ASTCDType> types = new ArrayList<>();
    types.addAll(getAst().getCDClassList());
    types.addAll(getAst().getCDInterfaceList());
    types.addAll(getAst().getCDEnumList());
    types.forEach(t -> setProperty(t, SUBPACKAGE_PROPERTY,
        Joiners.DOT.join(CLASSES_PACKAGE, (t.getSymbolOpt().isPresent() ?
            t.getSymbol().getName().toLowerCase() :
            t.getName().toLowerCase()))));
  }
}
