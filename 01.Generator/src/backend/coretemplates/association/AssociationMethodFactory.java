/* (c) https://github.com/MontiCore/monticore */

package backend.coretemplates.association;

import common.util.CDMethodBuilder;
import common.util.CDSimpleReferenceBuilder;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;

public class AssociationMethodFactory {
  
  public static enum Templates {
    // default association methods
    CONTAINS_METHOD_BODY("backend.coretemplates.association.ordinary.ContainsMethodBody"),
    CONTAINSALL_METHOD_BODY("backend.coretemplates.association.ordinary.ContainsAllMethodBody"),
    ISEMPTY_METHOD_BODY("backend.coretemplates.association.ordinary.IsEmptyMethodBody"),
    SIZE_METHOD_BODY("backend.coretemplates.association.ordinary.SizeMethodBody"),
    GET_METHOD_BODY("backend.coretemplates.association.ordinary.GetMethodBody"),
    ITERATOR_METHOD_BODY("backend.coretemplates.association.ordinary.IteratorMethodBody"),
    ADD_METHOD_BODY("backend.coretemplates.association.ordinary.AddMethodBody"),
    RAWADD_METHOD_BODY("backend.coretemplates.association.ordinary.RawAddMethodBody"),
    ADDALL_METHOD_BODY("backend.coretemplates.association.ordinary.AddAllMethodBody"),
    RAWADDALL_METHOD_BODY("backend.coretemplates.association.ordinary.RawAddAllMethodBody"),
    REMOVE_METHOD_BODY("backend.coretemplates.association.ordinary.RemoveMethodBody"),
    RAWREMOVE_METHOD_BODY("backend.coretemplates.association.ordinary.RawRemoveMethodBody"),
    REMOVEALL_METHOD_BODY("backend.coretemplates.association.ordinary.RemoveAllMethodBody"),
    RETAINALL_METHOD_BODY("backend.coretemplates.association.ordinary.RetainAllMethodBody"),
    CLEAR_METHOD_BODY("backend.coretemplates.association.ordinary.ClearMethodBody"),
    SET_METHOD_BODY("backend.coretemplates.association.ordinary.SetMethodBody"),
    SET_ALL_METHOD_BODY("backend.coretemplates.association.ordinary.SetAllMethodBody"),
    OPTIONAL_SET_METHOD_BODY("backend.coretemplates.association.ordinary.OptionalSetMethodBody"),
    RAWSET_METHOD_BODY("backend.coretemplates.association.ordinary.RawSetMethodBody"),
    OPTIONAL_RAWSET_METHOD_BODY("backend.coretemplates.association.ordinary.OptionalRawSetMethodBody"),
    
    // qualified association methods
    QUALIFIED_ADD_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedSet"),
    QUALIFIED_PUT_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedPutMethodBody"),
    QUALIFIED_CONTAINS_KEY_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedContainsKeyMethodBody"),
    QUALIFIED_CONTAINS_OBJECT_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedContainsObjectMethodBody"),
    QUALIFIED_REMOVE_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedRemoveMethodBody"),
    QUALIFIED_REMOVE_MULTIMAP_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedRemoveMultiMapMethodBody"),
    QUALIFIED_MULTIMAP_REMOVE_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedMultimapRemoveMethodBody"),
    QUALIFIED_CLEAR_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedClearMethodBody"),
    QUALIFIED_MULTIMAP_CLEAR_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedMultimapClearMethodBody"),
    QUALIFIED_ITERATORKEY_METHOD_BODY("backend.coretemplates.association.qualified.IteratorKeyMethodBody"),
    QUALIFIED_ITERATORVALUE_METHOD_BODY("backend.coretemplates.association.qualified.IteratorValueMethodBody"),
    QUALIFIED_GET_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedGetMethodBody"),
    QUALIFIED_ISEMPTY_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedIsEmptyMethodBody"),
    QUALIFIED_SIZE_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedSizeMethodBody"),
    QUALIFIED_CONTAINS_VALUE_METHOD_BODY("backend.coretemplates.association.qualified.QualifiedContainsValueMethodBody"),
    OPTIONAL_GET_METHOD_BODY("backend.coretemplates.association.ordinary.OptionalGetMethodBody"),
    OPTIONAL_GET_OPTIONAL_METHOD_BODY("backend.coretemplates.association.ordinary.OptionalGetOptionalMethodBody"),
    OPTIONAL_QUALIFIED_CLEAR_METHOD_BODY("backend.coretemplates.association.qualified.OptionalQualifiedClearMethodBody"),
    OPTIONAL_QUALIFIED_GET_METHOD_BODY("backend.coretemplates.association.qualified.OptionalQualifiedGet"),
    
    // ordered association methods
    ORDERED_LISTITERATOR_METHOD_BODY("backend.coretemplates.association.ordered.ListIteratorMethodBody"),
    ORDERED_LISTITERATOR_INDEX_METHOD_BODY("backend.coretemplates.association.ordered.ListIteratorIndexMethodBody"),
    ORDERED_ADD_METHOD_BODY("backend.coretemplates.association.ordered.OrderedAddMethodBody"),
    ORDERED_ADDALL_METHOD_BODY("backend.coretemplates.association.ordered.OrderedAddAllMethodBody"),
    ORDERED_GET_METHOD_BODY("backend.coretemplates.association.ordered.OrderedGetMethodBody"),
    ORDERED_INDEXOF_METHOD_BODY("backend.coretemplates.association.ordered.IndexOfMethodBody"),
    ORDERED_LASTINDEXOF_METHOD_BODY("backend.coretemplates.association.ordered.LastIndexOfMethodBody"),
    ORDERED_SET_METHOD_BODY("backend.coretemplates.association.ordered.OrderedSetMethodBody"),
    ORDERED_SUBLIST_METHOD_BODY("backend.coretemplates.association.ordered.SubListMethodBody"),
    
    // qualified ordered
    QUALIFIED_ORDERED_LISTITERATOR_MANY_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedListIteratorMethodBody"),
    QUALIFIED_ORDERED_LISTITERATOR_INDEX_MANY_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedListIteratorIndexMethodBody"),
    QUALIFIED_ORDERED_GET_MANY_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedOrderedGet"),
    QUALIFIED_ORDERED_INDEXOF_MANY_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedIndexOfMethodBody"),
    QUALIFIED_ORDERED_LASTINDEXOF_MANY_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedLastIndexOfMethodBody"),
    QUALIFIED_ORDERED_SET_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedOrderedSetMethodBody"),
    QUALIFIED_ORDERED_SUBLIST_MANY_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedOrderedSubListMethodBody"),
    QUALIFIED_ORDERED_GET_ONE_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedOrderedGetOneMethodBody"),
    QUALIFIED_ORDERED_LASTINDEXOF_ONE_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedOrderedLastIndexOfMethodBody"),
    QUALIFIED_ORDERED_INDEXOF_ONE_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedOrderedIndexOfMethodBody"),
    QUALIFIED_ORDERED_SUBLIST_ONE_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedOrderedSubListMethodBody"),
    QUALIFIED_ORDERED_SET_ONE_METHOD_BODY("backend.coretemplates.association.qualifiedordered.QualifiedOrderedSetOneMethodBody");
    
    private String name;
    
    private Templates(String name) {
      this.name = name;
    }
    
    public String valueOf() {
      return this.name;
    }
  }
  
  private AssociationMethodFactory() {
    // only a factory
  }
  
  /****************************************************************************
   * methods for [1] or [0..1]
   ***************************************************************************/
  public static ASTCDMethod createGetMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder().name(AssociationNameUtil.getGetValueMethodName(symbol).get())
        .returnType(targetType).build();
  }
  
  public static ASTCDMethod createGetOptionalMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder().name(AssociationNameUtil.getGetValueMethodName(symbol).get()+"Optional")
        .returnType(targetType).build();
  }
  public static ASTCDMethod createSetMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder().addParameter(targetType, "o")
        .name(AssociationNameUtil.getSetValueMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createRawSetMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder().Final()
        .addParameter(targetType, "o")
        .name(AssociationNameUtil.getRawSetOrAddValueMethodName(symbol).get())
        .build();
  }
  
  public static ASTCDMethod createRawUnSetMethod(CDAssociationSymbol symbol) {
    return new CDMethodBuilder().name(AssociationNameUtil.getUnSetMethodName(symbol).get())
        .build();
  }
  
  /****************************************************************************
   * methods for [*] or [1..*]
   ***************************************************************************/
  public static ASTCDMethod createContainsMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(targetType, "o")
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getContainsMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createContainsAllMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(
            CDSimpleReferenceBuilder.CollectionTypes.createCollectionType(
                new CDSimpleReferenceBuilder().name(targetType).build()), "o")
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getContainsAllMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createIsEmptyMethod(CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getIsEmptyMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createSizeMethod(CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createInt())
        .setName(AssociationNameUtil.getSizeMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createIteratorMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .name(AssociationNameUtil.getIteratorMethodName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.CollectionTypes.createIterator(
            new CDSimpleReferenceBuilder().name(targetType).build())).build();
  }
  
  public static ASTCDMethod createGetAllMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .name(AssociationNameUtil.getGetAllMethodName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.CollectionTypes.createList(
            new CDSimpleReferenceBuilder().name(targetType).build())).build();
  }

  public static ASTCDMethod createAddMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(targetType, "o")
        //.returnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .name(AssociationNameUtil.getSetValueMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createRawAddMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder().Final()
        .addParameter(targetType, "o")
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getRawAddMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createAddAllMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(
            CDSimpleReferenceBuilder.CollectionTypes.createList(
                new CDSimpleReferenceBuilder().name(targetType).build()), "o")
       // .returnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .name(AssociationNameUtil.getAddAllMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createSetAllMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(
            CDSimpleReferenceBuilder.CollectionTypes.createList(
                new CDSimpleReferenceBuilder().name(targetType).build()), "o")
       // .returnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .name(AssociationNameUtil.getSetAllMethodName(symbol).get()).build();
  }

  public static ASTCDMethod createRawAddAllMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder().Final()
        .addParameter(
            CDSimpleReferenceBuilder.CollectionTypes.createCollectionType(
                new CDSimpleReferenceBuilder().name(targetType).build()), "o")
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getRawAddAllMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createRemoveMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(targetType, "o")
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getRemoveMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createRawRemoveMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder().Final()
        .addParameter(targetType, "o")
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getRawRemoveMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createRemoveAllMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(
            CDSimpleReferenceBuilder.CollectionTypes.createCollectionType(
                new CDSimpleReferenceBuilder().name(targetType).build()), "o")
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getRemoveAllMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createRetainAllMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(CDSimpleReferenceBuilder.CollectionTypes.createCollectionType(
            new CDSimpleReferenceBuilder().name(targetType).build()), "o")
        .name(AssociationNameUtil.getRetainMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createClearMethod(CDAssociationSymbol symbol) {
    return new CDMethodBuilder().name(AssociationNameUtil.getClearMethodName(symbol).get()).build();
  }
  
  /****************************************************************************
   * Qualified methods
   ***************************************************************************/
  public static ASTCDMethod createQualifiedIsEmptyMethod(CDAssociationSymbol symbol,
      String qualifierType, String qualifierName) {
    return new CDMethodBuilder()
        .addParameter(qualifierType, qualifierName)
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getIsEmptyMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createQualifiedSizeMethod(CDAssociationSymbol symbol,
      String qualifierType, String qualifierName) {
    return new CDMethodBuilder()
        .addParameter(qualifierType, qualifierName)
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createInt())
        .setName(AssociationNameUtil.getSizeMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createQualifiedAddMethod(String targetType,
      String qualifierType, String qualifierName,
      CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(qualifierType, qualifierName)
        .addParameter(targetType, "o")
        .name(AssociationNameUtil.getSetValueMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createQualifiedPutMethod(String targetType,
      String qualifierType, String qualifierName,
      CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(qualifierType, qualifierName)
        .addParameter(targetType, "o")
        .returnType("boolean")
        .name(AssociationNameUtil.getSetValueMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createQualifiedContainsKeyMethod(String targetType,
      CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(targetType, "key")
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getContainsKeyMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createQualifiedContainsObjectMethod(String targetType,
      CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(targetType, "o")
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getContainsObjectMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createQualifiedContainsValueMethod(String targetType,
      String qualifierType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(qualifierType, "key")
        .addParameter(targetType, "o")
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createBoolean())
        .setName(AssociationNameUtil.getContainsValueMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createQualifiedRemoveMethod(String qualifierType,
      CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(qualifierType, "key")
        .returnType("boolean")
        .name(AssociationNameUtil.getRemoveMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createQualifiedMultimapRemoveMethod(String qualifierType,
      String targetType, String qualifierName, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(qualifierType, qualifierName)
        .addParameter(targetType, "o")
        .returnType("boolean")
        .name(AssociationNameUtil.getRemoveMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createIteratorKeyMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .name(AssociationNameUtil.getIteratorKeyMethodName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.CollectionTypes.createIterator(
            new CDSimpleReferenceBuilder().name(targetType).build())).build();
  }
  
  public static ASTCDMethod createIteratorValueMethod(String targetType,
      String qualifierType, String qualifierName, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(qualifierType, qualifierName)
        .name(AssociationNameUtil.getIteratorValueMethodName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.CollectionTypes.createIterator(
            new CDSimpleReferenceBuilder().name(targetType).build())).build();
  }
  
  public static ASTCDMethod createQualifiedListIteratorMethod(String targetType,
      String qualifierType, String qualifierName, CDAssociationSymbol symbol) {
    return new CDMethodBuilder().addParameter(qualifierType, qualifierName)
        .name(AssociationNameUtil.getListIteratorName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.CollectionTypes.createListIterator(
            new CDSimpleReferenceBuilder().name(targetType).build())).build();
  }
  
  public static ASTCDMethod createQualifiedParameterizedListIterator(
      String targetType, String qualifierType, String qualifierName, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "index")
        .addParameter(qualifierType, qualifierName)
        .name(AssociationNameUtil.getParameterizedListIteratorName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.CollectionTypes.createListIterator(
            new CDSimpleReferenceBuilder().name(targetType).build())).build();
  }
  
  public static ASTCDMethod createQualifiedGetMethod(String targetType, String qualifierType,
      CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(qualifierType, "key")
        .name(AssociationNameUtil.getGetValueMethodName(symbol).get())
        .returnType(targetType).build();
  }
  
  public static ASTCDMethod createQualifiedIndexOfMethod(String targetType,
      String qualifierType, String qualifierName, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(targetType, "o")
        .addParameter(qualifierType, qualifierName)
        .name(AssociationNameUtil.getIndexOfMethodName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createInt()).build();
  }
  
  public static ASTCDMethod createQualifiedLastIndexOf(String targetType,
      String qualifierType, String qualifierName, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(targetType, "o")
        .addParameter(qualifierType, qualifierName)
        .name(AssociationNameUtil.getLastIndexOfMethodName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createInt()).build();
  }
  
  public static ASTCDMethod createQualifiedSubListMethod(String targetType,
      String qualifierType, String qualifierName, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "start")
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "end")
        .addParameter(qualifierType, qualifierName)
        .setReturnType(CDSimpleReferenceBuilder.CollectionTypes.createList(
            new CDSimpleReferenceBuilder().name(targetType).build()))
        .setName(AssociationNameUtil.getSubListMethodName(symbol).get()).build();
  }
  
  /****************************************************************************
   * Ordered association methods
   ***************************************************************************/
  public static ASTCDMethod createListIteratorMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .name(AssociationNameUtil.getListIteratorName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.CollectionTypes.createListIterator(
            new CDSimpleReferenceBuilder().name(targetType).build())).build();
  }
  
  public static ASTCDMethod createParameterizedListIteratorMethod(String targetType,
      CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "index")
        .name(AssociationNameUtil.getParameterizedListIteratorName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.CollectionTypes.createListIterator(
            new CDSimpleReferenceBuilder().name(targetType).build())).build();
  }
  
  public static ASTCDMethod createOrderedAddMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder().addParameter(targetType, "o")
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "index")
        .name(AssociationNameUtil.getSetValueMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createOrderedAddAllMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(CDSimpleReferenceBuilder.CollectionTypes.createCollectionType(
            new CDSimpleReferenceBuilder().name(targetType).build()), "o")
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "index")
        .name(AssociationNameUtil.getAddAllMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createOrderedGetMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .name(AssociationNameUtil.getGetValueMethodName(symbol).get())
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "index")
        .returnType(targetType).build();
  }
  
  public static ASTCDMethod createIndexOfMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(targetType, "o")
        .name(AssociationNameUtil.getIndexOfMethodName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createInt()).build();
  }
  
  public static ASTCDMethod createLastIndexOfMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(targetType, "o")
        .name(AssociationNameUtil.getLastIndexOfMethodName(symbol).get())
        .setReturnType(CDSimpleReferenceBuilder.PrimitiveTypes.createInt()).build();
  }
  
  public static ASTCDMethod createOrderedSetMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(targetType, "o")
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "index")
        .name(AssociationNameUtil.getOrderedSetValueMethodName(symbol).get()).build();
  }
  
  public static ASTCDMethod createSubListMethod(String targetType, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "start")
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "end")
        .setReturnType(CDSimpleReferenceBuilder.CollectionTypes.createList(
            new CDSimpleReferenceBuilder().name(targetType).build()))
        .setName(AssociationNameUtil.getSubListMethodName(symbol).get()).build();
  }
  
  /****************************************************************************
   * Ordered qualified association methods
   ***************************************************************************/
  public static ASTCDMethod createOrderedQualifiedGetMethod(String targetType,
      String qualifierType, String qualifierName, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "index")
        .addParameter(qualifierType, qualifierName)
        .name(AssociationNameUtil.getGetValueMethodName(symbol).get())
        .returnType(targetType).build();
  }
  
  public static ASTCDMethod createQualifiedOrderedSetMethod(String targetType,
      String qualifierType, String qualifierName, CDAssociationSymbol symbol) {
    return new CDMethodBuilder()
        .addParameter(qualifierType, qualifierName)
        .addParameter(targetType, "o")
        .addParameter(CDSimpleReferenceBuilder.PrimitiveTypes.createInt(), "index")
        .name(AssociationNameUtil.getSetValueMethodName(symbol).get()).build();
  }
}
