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

package common.util;

import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;

/**
 * This concrete delegate is dedicated to manage modifiers. To make use of this
 * delegate, a builder has to implement the <code>ModifierModifiable</code>
 * interface and use this class to delegate the method calls to.
 *
 * @author Alexander Roth
 */
public class ConcreteModifierDelegate {

  public ASTModifier Public(ASTModifier modifier) {
    if (modifier == null) {
      modifier = CD4AnalysisNodeFactory.createASTModifier();
    }
    modifier.setPublic(true);
    return modifier;
  }

  public ASTModifier Private(ASTModifier modifier) {
    if (modifier == null) {
      modifier = CD4AnalysisNodeFactory.createASTModifier();
    }
    modifier.setPrivate(true);
    return modifier;
  }

  public ASTModifier Protected(ASTModifier modifier) {
    if (modifier == null) {
      modifier = CD4AnalysisNodeFactory.createASTModifier();
    }
    modifier.setProtected(true);
    return modifier;
  }

  public ASTModifier Static(ASTModifier modifier) {
    // by default use public if nothing is set before
    if (modifier == null) {
      modifier = Public(modifier);
    }
    modifier.setStatic(true);
    return modifier;
  }

  public ASTModifier Final(ASTModifier modifier) {
    // by default use public if nothing is set before
    if (modifier == null) {
      modifier = Public(modifier);
    }
    modifier.setFinal(true);
    return modifier;
  }

  public ASTModifier Abstract(ASTModifier modifier){
    if (modifier == null) {
      modifier = Public(modifier);
    }
    modifier.setAbstract(true);
    return modifier;
  }

  public ASTModifier Package(ASTModifier modifier) {
    // by default use public if nothing is set before
    if (modifier == null) {
      modifier = CD4AnalysisNodeFactory.createASTModifier();
    }
    return modifier;
  }

}
