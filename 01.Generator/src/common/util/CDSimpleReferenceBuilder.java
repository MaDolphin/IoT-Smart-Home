/* (c) https://github.com/MontiCore/monticore */


package common.util;

import com.google.common.collect.Lists;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.types._ast.*;

import java.util.List;

public class CDSimpleReferenceBuilder extends ASTSimpleReferenceTypeBuilder {

  public CDSimpleReferenceBuilder name(String name) {
    super.setNameList(Lists.newArrayList(name));
    return this;
  }

  public CDSimpleReferenceBuilder name(List<String> name) {
    super.setNameList(name);
    return this;
  }

  public CDSimpleReferenceBuilder typeArguments(String typeArgument) {
    ASTTypeArguments typeArguments = TypesNodeFactory.createASTTypeArguments();
    typeArguments.getTypeArgumentList().add(
        new CDSimpleReferenceBuilder().name(typeArgument).build());
    super.setTypeArguments(typeArguments);

    return this;
  }

  /**
   * A list of predefined collection types
   *
   */
  public static class CollectionTypes {

    private CollectionTypes() {
      throw new IllegalStateException("CollectionTypes");
    }

    public final static HookPoint SET_VALUE = new StringHookPoint("= new HashSet<>();");

    public final static HookPoint MAP_VALUE = new StringHookPoint("= new HashMap<>();");

    public final static HookPoint MULTIMAP_VALUE = new StringHookPoint("= HashMultimap.create();");

    public final static HookPoint LIST_VALUE = new StringHookPoint("= new LinkedList<>();");

    public final static HookPoint MULTIMAP_ORDERED_VALUE = new StringHookPoint("=LinkedListMultimap.create();");

    public static ASTSimpleReferenceType createCollectionType(String type) {
      return createCollectionType(new CDSimpleReferenceBuilder().name(type)
          .build());
    }

    public static ASTSimpleReferenceType createCollectionType(ASTType type) {

      return genericCollection(
          Lists.newArrayList("Collection"), type);
    }

    public static ASTSimpleReferenceType createSet(String type) {
      return createSet(new CDSimpleReferenceBuilder().name(type).build());
    }

    public static ASTSimpleReferenceType createSet(ASTType type) {

      return genericCollection(Lists.newArrayList("Set"), type);
    }

    public static ASTSimpleReferenceType createList(String type) {
      return createList(new CDSimpleReferenceBuilder().name(type).build());
    }

    public static ASTSimpleReferenceType createList(ASTType type) {
      return genericCollection(Lists.newArrayList("List"), type);
    }

    public static ASTSimpleReferenceType createIterator(ASTType type) {

      return genericCollection(Lists.newArrayList("Iterator"),
          type);
    }

    public static ASTSimpleReferenceType createListIterator(ASTType type) {

      return genericCollection(
          Lists.newArrayList("ListIterator"), type);
    }

    public static ASTSimpleReferenceType createWeakReference(String type) {
      return createWeakReference(new CDSimpleReferenceBuilder().name(type)
          .build());
    }

    public static ASTSimpleReferenceType createWeakReference(ASTType type) {

      return genericCollection(
          Lists.newArrayList("WeakReference"), type);
    }

    public static ASTSimpleReferenceType createMultimap(ASTType keyType,
                                                        ASTType valueType) {
      return genericCollection(
          Lists.newArrayList("Multimap"),
          keyType, valueType);
    }

    public static ASTSimpleReferenceType createBiMap(ASTType keyType,
                                                     ASTType valueType) {
      return genericCollection(
          Lists.newArrayList("BiMap"),
          keyType, valueType);
    }

    public static ASTSimpleReferenceType createMap(String key, String value) {
      ASTType keyType = new CDSimpleReferenceBuilder().name(key).build();
      ASTType valueType = new CDSimpleReferenceBuilder().name(value).build();
      return createMap(keyType, valueType);
    }

    public static ASTSimpleReferenceType createListMultimap(ASTType keyType,
                                                            ASTType valueType) {
      return genericCollection(Lists.newArrayList("LinkedListMultimap"), keyType, valueType);
    }

    public static ASTSimpleReferenceType createMap(String key, ASTType type) {
      ASTType keyType = new CDSimpleReferenceBuilder().name(key).build();
      return createMap(keyType, type);
    }

    public static ASTSimpleReferenceType createLinkedMap(ASTType keyType,
                                                         ASTType valueType) {
      return genericCollection(
          Lists.newArrayList("LinkedHashMap"), keyType,
          valueType);
    }

    public static ASTSimpleReferenceType createMap(ASTType keyType,
                                                   ASTType valueType) {
      return genericCollection(Lists.newArrayList("Map"),
          keyType, valueType);
    }

    private static ASTSimpleReferenceType genericCollection(List<String> name,
                                                            ASTType type) {
      return new CDSimpleReferenceBuilder().name(name)
          .setTypeArguments(createTypeArguments(type)).build();
    }

    private static ASTSimpleReferenceType genericCollection(List<String> name,
                                                            ASTType keyType, ASTType valueType) {
      return new CDSimpleReferenceBuilder().name(name)
          .setTypeArguments(createTypeArguments(keyType, valueType)).build();
    }

    private static ASTTypeArguments createTypeArguments(ASTType type) {
      ASTTypeArguments typeArguments = TypesNodeFactory
          .createASTTypeArguments();
      typeArguments.getTypeArgumentList().add(type);

      return typeArguments;
    }

    private static ASTTypeArguments createTypeArguments(ASTType keyType,
                                                        ASTType valueType) {
      ASTTypeArguments typeArguments = TypesNodeFactory
          .createASTTypeArguments();
      typeArguments.getTypeArgumentList().add(keyType);
      typeArguments.getTypeArgumentList().add(valueType);

      return typeArguments;
    }
  }

  /**
   * Create primitive data types
   *
   */
  public static class PrimitiveTypes {

    private PrimitiveTypes() {
      throw new IllegalStateException("PrimitiveTypes");
    }

    public static ASTSimpleReferenceType createBoolean() {
      return new CDSimpleReferenceBuilder().name("boolean").build();
    }

    public static ASTSimpleReferenceType createVoid() {
      return new CDSimpleReferenceBuilder().name("void").build();
    }

    public static ASTSimpleReferenceType createInt() {
      return new CDSimpleReferenceBuilder().name("int").build();
    }
    public static ASTSimpleReferenceType createLong() {
      return new CDSimpleReferenceBuilder().name("long").build();
    }
  }

  /**
   * Create data types
   *
   */
  public static class DataTypes {

    private DataTypes() {
      throw new IllegalStateException("DataTypes");
    }

    public final static HookPoint OPTIONAL_ABSENT = new StringHookPoint(
        "= Optional.empty();");

    public static ASTSimpleReferenceType createString() {
      return new CDSimpleReferenceBuilder().name("String").build();
    }

    public static ASTSimpleReferenceType createInteger() {
      return new CDSimpleReferenceBuilder().name("Integer").build();
    }

    public static ASTSimpleReferenceType createLong() {
      return new CDSimpleReferenceBuilder().name("Long").build();
    }

    public static ASTSimpleReferenceType createOptional(String type) {
      return new CDSimpleReferenceBuilder()
          .typeArguments(type)
          .name(
              Lists.newArrayList("Optional"))
          .build();
    }

  }
}
