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

import common.util.CDAttributeBuilder;
import common.util.CDSimpleReferenceBuilder;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;

public class AssociationAttributeFactory {

  private AssociationAttributeFactory() {

  }

  public static ASTCDAttribute createOne(String name, ASTType type) {
    return new CDAttributeBuilder().Private().setName(name).setType(type).build();
  }

  public static ASTCDAttribute createMany(String name, ASTType type) {
    return new CDAttributeBuilder().Private().setName(name)
        .setType(CDSimpleReferenceBuilder.CollectionTypes.createList(type)).build();
  }

  public static ASTCDAttribute createQualifiedMany(String name, ASTType keyType,
      ASTType valueType) {
    return new CDAttributeBuilder().Private().setName(name)
        .setType(CDSimpleReferenceBuilder.CollectionTypes.createMultimap(keyType, valueType))
        .build();
  }

  public static ASTCDAttribute createQualifiedOne(String name, ASTType keyType, ASTType valueType) {
    return new CDAttributeBuilder()
        .Private().setName(name)
        .setType(CDSimpleReferenceBuilder.CollectionTypes.createMap(keyType, valueType)).build();
  }

  public static ASTCDAttribute createOrdered(String name, ASTType type) {
    return new CDAttributeBuilder().Private().setName(name)
        .setType(CDSimpleReferenceBuilder.CollectionTypes.createList(type))
        .build();
  }

  public static ASTCDAttribute createQualifiedOrderedMany(String name, ASTType keyType,
      ASTType valueType) {
    return new CDAttributeBuilder().Private().setName(name)
        .setType(CDSimpleReferenceBuilder.CollectionTypes.createListMultimap(keyType, valueType))
        .build();
  }

  public static ASTCDAttribute createQualifiedOrderedOne(String name, ASTType keyType,
      ASTType valueType) {
    return new CDAttributeBuilder().Private()
        .setName(name)
        .setType(CDSimpleReferenceBuilder.CollectionTypes.createLinkedMap(keyType, valueType))
        .build();
  }
}
