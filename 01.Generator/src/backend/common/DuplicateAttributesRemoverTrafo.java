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

import com.google.common.collect.*;
import common.DexTransformation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Fixing duplicated attributes in an inheritance hierarchy. This means that in
 * situations like <code>class A { int c;} class B extends A {int c}</code> the
 * attribute of B is neglected. This is due to the fact that the constructors
 * are created based on all visible attributes. Consequently, in this situation
 * the created constructor of class B would be <code> public B(int c, int c)
 * </code>. However, a problem occurs if the type of the attribute in class B is
 * different, then the attribute is still neglected!!
 */
public class DuplicateAttributesRemoverTrafo extends DexTransformation {

  @Override
  protected void transform() {

    checkArgument(symbolTable.isPresent());

    Multimap<ASTCDClass, String> removableAttr = ArrayListMultimap.create();

    for (ASTCDClass clazz : getAst().getCDClassList()) {
      // create a multiset of all visible attributes in hierarchy so that we can
      // count elements
      Multiset<String> mulSet = HashMultiset.create();
      mulSet.addAll(symbolTable.get().getVisibleNotDerivedAttributesInHierarchy(clazz.getName()).stream()
          .map(x -> x.getName()).collect(Collectors.toList()));
      mulSet.addAll(symbolTable.get().getDerivedAttributesInHierarchy(clazz.getName()).stream()
          .map(x -> x.getName()).collect(Collectors.toList()));

      List<String> multiple = mulSet.stream().filter(x -> (mulSet.count(x) > 1)).collect(Collectors.toList());
      for (String s : multiple) {
        removableAttr.put(clazz, s);
      }
    }

    // remove the duplicate attrs
    for (Map.Entry<ASTCDClass, Collection<String>> entry : removableAttr.asMap().entrySet()) {
      removeAttributes(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Removes attributes from a class that match the names given in a list
   *
   * @param clazz
   * @param attrs
   */
  private void removeAttributes(ASTCDClass clazz, Collection<String> attrs) {
    List<ASTCDAttribute> attrToRemove = Lists.newArrayList();
    for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
      if (attrs.contains(attr.getName())) {
        attrToRemove.add(attr);
      }
    }

    clazz.getCDAttributeList().removeAll(attrToRemove);
  }
}
