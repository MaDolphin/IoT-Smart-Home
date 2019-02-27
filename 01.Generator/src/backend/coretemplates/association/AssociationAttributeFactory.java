/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
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
