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

package backend.coretemplates.association;

import java.util.List;
import java.util.Optional;

import de.monticore.generating.templateengine.HookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

/**
 * A general interface to define a strategy for handling associations
 *
 * @author Alexander Roth
 */
public interface AssociationStrategy {

  Optional<ASTCDAttribute> getAssociationAttribute();

  List<ASTCDMethod> getAddMethods();

  List<ASTCDMethod> getRawAddMethods();

  List<ASTCDMethod> getGetMethods();

  List<ASTCDMethod> getRemoveMethods();

  List<ASTCDMethod> getRawRemoveMethods();

  List<ASTCDMethod> getCommonMethods();

  void setHookPoint(HookPoint hp);

  void setAddMethodCall(String callMethod);

  void setRemoveMethodCall(String callMethod);
}
