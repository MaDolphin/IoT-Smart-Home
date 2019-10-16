/* (c) https://github.com/MontiCore/monticore */

package backend.coretemplates.association;

import backend.data.validator.ValidatorCreator;
import common.util.CDAssociationUtil;
import common.util.TransformationUtils;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.Cardinality;

import java.util.Optional;

public class AssociationNameUtil {
  
  /* ************************************************************************* <p/> Methods used for
   * generating the remove or add call inside the association transformations. <p/>
   * ************************************************************************ */
  public static String getCallMethod(CDAssociationSymbol s) {
    StringBuilder result = new StringBuilder();
    Optional<CDAssociationSymbol> symbol = CDAssociationUtil.findOppositeSymbol(s);
    
    if (symbol.isPresent()) {
      // retrieve all values
      String name = CDAssociationUtil.getAssociationName(symbol.get());
      
      if (CDAssociationUtil.isMultiple(symbol.get())
          || CDAssociationUtil.isOneToMany(symbol.get())) {
        result.append(Prefixes.RAW.with(Prefixes.ADD.valueOf(), name));
      }
      else {
        result.append(Prefixes.RAW.with(Prefixes.SET.valueOf(), name));
      }
      
      result.append("(");
      
      result.append("(");
      
      result.append(s.getSourceType().getName());
      
      result.append(")this");
      
      result.append(")");
      
      return result.toString();
    }
    else {
      return null;
    }
  }
  
  public static String getRemoveMethodCall(CDAssociationSymbol s) {
    StringBuilder result = new StringBuilder();
    
    Optional<CDAssociationSymbol> symbol = CDAssociationUtil.findOppositeSymbol(s);
    
    if (symbol.isPresent()) {
      
      // retrieve all values
      String name = CDAssociationUtil.getAssociationName(symbol.get());
      
      if (CDAssociationUtil.isMultiple(symbol.get())
          || CDAssociationUtil.isOneToMany(symbol.get())) {
        result.append(Prefixes.RAW.with(Prefixes.REMOVE.valueOf(), name));
      }
      else {
        result.append(Prefixes.RAW.with(Prefixes.UNSET.valueOf(), name));
      }
      
      result.append("(");
      if (CDAssociationUtil.isMultiple(symbol.get())
          || CDAssociationUtil.isOneToMany(symbol.get())) {
        result.append("(");        
        result.append(s.getSourceType().getName());
        result.append(")this");
      }
      result.append(")");
      
      return result.toString();
    }
    else {
      return null;
    }
  }
  
  /* ************************************************************************* <p/> Methods that are
   * used to compute the interfaceName for the generated association methods, e.g. setA, getA, addAllA
   * <p/> ************************************************************************ */
  
  public static Optional<String> getOrderedSetValueMethodName(CDAssociationSymbol symbol) {
    if (CDAssociationUtil.isOrdered(symbol) && CDAssociationUtil.isMultiple(symbol)) {
      return Optional.ofNullable(Prefixes.SET.with(CDAssociationUtil.getAssociationName(symbol)));
    }
    return Optional.empty();
  }
  
  public static Optional<String> getSetValueMethodName(CDAssociationSymbol symbol) {
    boolean isQualified = CDAssociationUtil.isQualified(symbol);
    
    boolean isOrdered = CDAssociationUtil.isOrdered(symbol);
    
    boolean isOne = CDAssociationUtil.isOne(symbol);
    
    boolean isOptional = CDAssociationUtil.isOptional(symbol);
    
    String assocName = CDAssociationUtil.getAssociationName(symbol);
    
    boolean oppositeQualified = CDAssociationUtil.isOppositeQualified(symbol);
    
    if (symbol.isDerived() && !isQualified && !isOrdered) {
      return Optional.empty();
    }
    else if (symbol.isDerived() && isQualified && !isOrdered) {
      return Optional.empty();
    }
    else if (symbol.isDerived() && !isQualified && isOrdered) {
      return Optional.empty();
    }
    else if (symbol.isDerived() && isQualified && isOrdered) {
      return Optional.empty();
    }
    else if (oppositeQualified && symbol.isBidirectional()) {
      if (isOne || isOptional) {
        return Optional.ofNullable(Prefixes.RAW.with(Prefixes.SET.valueOf(), assocName));
      }
      else {
        return Optional.ofNullable(Prefixes.RAW.with(Prefixes.ADD.valueOf(), assocName));
      }
    }
    else if (isQualified) {
      if (isOne || isOptional) {
        return Optional.ofNullable(Prefixes.ADD.with(assocName));
      }
      else {
        return Optional.ofNullable(Prefixes.PUT.with(assocName));
      }
    }
    else {
      if (isOne || isOptional) {
        return Optional.ofNullable(Prefixes.SET.with(assocName));
      }
      else {
        return Optional.ofNullable(Prefixes.ADD.with(assocName));
      }
    }
  }
  
  public static Optional<String> getRawSetOrAddValueMethodName(CDAssociationSymbol symbol) {
    if (symbol.isBidirectional()) {
      if (!CDAssociationUtil.isMultiple(symbol)) {
        return Optional.ofNullable(Prefixes.RAW.with(Prefixes.SET.valueOf(),
            CDAssociationUtil.getAssociationName(symbol)));
      }
      if (CDAssociationUtil.isMultiple(symbol)) {
        return Optional.ofNullable(Prefixes.RAW.with(Prefixes.ADD.valueOf(),
            CDAssociationUtil.getAssociationName(symbol)));
      }
    }
    return Optional.empty();
  }
  
  public static Optional<String> getAddAllMethodName(CDAssociationSymbol symbol) {
    boolean isSourceQualified = CDAssociationUtil.isQualified(symbol);
    boolean isMultiple = CDAssociationUtil.isMultiple(symbol);
    boolean isOneToMany = CDAssociationUtil.isOneToMany(symbol);
    String assocName = CDAssociationUtil.getAssociationName(symbol);
    
    if ((isMultiple || isOneToMany) && !(isSourceQualified && symbol.isBidirectional())
        && !symbol.isDerived()) {
      return Optional.ofNullable(Prefixes.ADDALL.with(assocName) + "s");
    }
    
    return Optional.empty();
  }
  
  public static Optional<String> getRawAddAllMethodName(CDAssociationSymbol symbol) {
    Optional<String> addAllName = getAddAllMethodName(symbol);
    
    if (symbol.isBidirectional() && addAllName.isPresent()
        || CDAssociationUtil.isOppositeQualified(symbol)) {
      return Optional.ofNullable(Prefixes.RAW.with(Prefixes.ADDALL.valueOf(),
          CDAssociationUtil.getAssociationName(symbol)) + "s");
    }
    
    return Optional.empty();
  }
  
  public static Optional<String> getRawAddMethodName(CDAssociationSymbol symbol) {
    if (symbol.isBidirectional() && CDAssociationUtil.isMultiple(symbol)) {
      return Optional
          .ofNullable(Prefixes.RAW.with(Prefixes.ADD.valueOf(),
              CDAssociationUtil.getAssociationName(symbol)));
    }
    return Optional.empty();
  }
  
  public static String getSetValueMethodNameOrEmptyString(CDAssociationSymbol symbol) {
    Optional<String> setMethod = getSetValueMethodName(symbol);
    if (setMethod.isPresent()) {
      return setMethod.get();
    }
    return "";
  }
  
  /* ************************************************************************* <p/> Methods for
   * retrieving elements from a data structure.<p/>
   * ************************************************************************ */
  public static final  Optional<String> getGetValueMethodName(CDAssociationSymbol assoc) {
    if (!CDAssociationUtil.isMultiple(assoc) && !CDAssociationUtil.isOneToMany(assoc)
        || ((CDAssociationUtil.isMultiple(assoc) || CDAssociationUtil.isOneToMany(assoc))
            && CDAssociationUtil.isOrdered(assoc))) {
      return Optional.ofNullable(Prefixes.GET.with(CDAssociationUtil.getAssociationName(assoc)));
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getGetAllMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc)) {
      return Optional.ofNullable(Prefixes.GET.with(CDAssociationUtil.getAssociationName(assoc)) + "s");
    }
    return Optional.empty();
  }

  public static final Optional<String> getSetAllMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc)) {
      return Optional.ofNullable(Prefixes.SET.with(CDAssociationUtil.getAssociationName(assoc)) + "s");
    }
    return Optional.empty();
  }

  public static final Optional<String> getIteratorKeyMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isQualified(assoc)) {
      return Optional
          .ofNullable(Prefixes.ITERATORKEY.with(CDAssociationUtil.getAssociationName(assoc)) + "s");
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getIteratorValueMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isQualified(assoc)) {
      if (CDAssociationUtil.isOrdered(assoc)) {
        return Optional.ofNullable(
            Prefixes.LISTITERATOR.with(CDAssociationUtil.getAssociationName(assoc)) + "s");
      }
      else if (CDAssociationUtil.isMultiple(assoc)) {
        return Optional.ofNullable(
            Prefixes.ITERATORVALUE.with(CDAssociationUtil.getAssociationName(assoc)) + "s");
      }
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getIteratorMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc)) {
      return Optional
          .ofNullable(Prefixes.ITERATOR.with(CDAssociationUtil.getAssociationName(assoc)) + "s");
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getListIteratorName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isOrdered(assoc) && CDAssociationUtil.isMultiple(assoc)) {
      return Optional.ofNullable(
          Prefixes.LISTITERATOR.with(CDAssociationUtil.getAssociationName(assoc)) + "s");
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getParameterizedListIteratorName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isOrdered(assoc) && CDAssociationUtil.isMultiple(assoc)
        && CDAssociationUtil.isOrdered(assoc)) {
      return Optional.ofNullable(
          Prefixes.LISTITERATOR.with(CDAssociationUtil.getAssociationName(assoc)) + "s");
    }
    return Optional.empty();
  }
  
  /* ************************************************************************* <p/> Methods for
   * retaining elements. <p/>
   * ************************************************************************ */
  public static final Optional<String> getRetainMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc)) {
      return Optional.ofNullable(Prefixes.RETAINALL.with(CDAssociationUtil
          .getAssociationName(assoc)) + "s");
    }
    return Optional.empty();
  }
  
  /* ************************************************************************* <p/> Methods for
   * checking if an element is contained. <p/>
   * ************************************************************************ */
  public static final Optional<String> getContainsMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc) || CDAssociationUtil.isQualified(assoc) || CDAssociationUtil.isSourceMultiple(assoc)) {
      return Optional
          .ofNullable(Prefixes.CONTAINS.with(CDAssociationUtil.getAssociationName(assoc)));
    }
    return Optional.empty();
  }

  public static final Optional<String> getContainsMethodNameOfOppositeAssoc(CDAssociationSymbol assoc) {
    Optional<String> oppositeAssoc = CDAssociationUtil.getOppositeAssociationNameOpt(assoc);
    return oppositeAssoc.isPresent()? Optional
        .ofNullable(Prefixes.CONTAINS.with(oppositeAssoc.get())): Optional.empty();
  }
  
  public static final Optional<String> getContainsAllMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc)) {
      return Optional.ofNullable(Prefixes.CONTAINSALL.with(CDAssociationUtil
          .getAssociationName(assoc) + "s"));
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getContainsKeyMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isQualified(assoc)) {
      return Optional
          .ofNullable(Prefixes.CONTAINSKEY.with(CDAssociationUtil.getAssociationName(assoc)));
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getContainsValueMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isQualified(assoc)) {
      return Optional
          .ofNullable(Prefixes.CONTAINSVALUE.with(CDAssociationUtil.getAssociationName(assoc)));
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getContainsObjectMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isQualified(assoc)) {
      return Optional
          .ofNullable(Prefixes.CONTAINSOBJECT.with(CDAssociationUtil.getAssociationName(assoc)));
    }
    return Optional.empty();
  }
  
  /* ************************************************************************* <p/> Methods for
   * indexing elements. <p/>
   * ************************************************************************ */
  public static final Optional<String> getIndexOfMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc) && CDAssociationUtil.isOrdered(assoc)) {
      return Optional
          .ofNullable(Prefixes.INDEXOF.with(CDAssociationUtil.getAssociationName(assoc)));
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getLastIndexOfMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc) && CDAssociationUtil.isOrdered(assoc)) {
      return Optional.ofNullable(Prefixes.LASTINDEXOF.with(CDAssociationUtil
          .getAssociationName(assoc)));
    }
    
    return Optional.empty();
  }
  
  /* ************************************************************************* <p/> Common method
   * names. <p/> ************************************************************************ */
  public static final Optional<String> getSizeMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc) || CDAssociationUtil.isQualified(assoc)) {
      return Optional.ofNullable(Prefixes.SIZE.with(CDAssociationUtil.getAssociationName(assoc))
          + "s");
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getIsEmptyMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc) && !CDAssociationUtil.isOneToMany(assoc)
        || CDAssociationUtil.isQualified(assoc)) {
      return Optional
          .ofNullable(Prefixes.ISEMPTY.with(CDAssociationUtil.getAssociationName(assoc)) + "s");
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getSubListMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc) && CDAssociationUtil.isMultiple(assoc)) {
      return Optional
          .ofNullable(Prefixes.SUBLIST.with(CDAssociationUtil.getAssociationName(assoc)) + "s");
    }
    return Optional.empty();
  }
  
  /* ************************************************************************* <p/> Methods for
   * removing elements. <p/>
   * ************************************************************************ */
  public static final Optional<String> getRemoveMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc) || CDAssociationUtil.isQualified(assoc)) {
      return Optional.ofNullable(Prefixes.REMOVE.with(CDAssociationUtil.getAssociationName(assoc)));
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getRawRemoveMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc) && assoc.isBidirectional()) {
      return Optional.ofNullable(Prefixes.RAW.with(Prefixes.REMOVE.valueOf(),
          CDAssociationUtil.getAssociationName(assoc)));
    }
    return Optional.empty();
  }
  
  public static final Optional<String> getRemoveAllMethodName(CDAssociationSymbol assoc) {
    if (CDAssociationUtil.isMultiple(assoc)) {
      return Optional.ofNullable(Prefixes.REMOVEALL.with(CDAssociationUtil
          .getAssociationName(assoc)) + "s");
    }
    return Optional.empty();
  }
  
  public static Optional<String> getClearMethodName(CDAssociationSymbol symbol) {
    
    String suffix = CDAssociationUtil.getAssociationName(symbol);
    
    Optional<ASTCDQualifier> qualifier = CDAssociationUtil.getQualifier(symbol);
    
    Cardinality cardinality = symbol.getTargetCardinality();
    
    // check for derived association
    if (symbol.isDerived()) {
      return Optional.empty();
    }
    // check for qualified ordered association
    else if (qualifier.isPresent() || cardinality.isMultiple()) {
      return Optional.ofNullable(Prefixes.CLEAR.with(suffix) + "s");
    }
    // check for ordered association
    else {
      if (cardinality.getMin() == 1 && cardinality.getMax() == 1) {
        return Optional.ofNullable(Prefixes.RAW.with(Prefixes.UNSET.valueOf(), suffix));
      }
      else {
        return Optional.ofNullable(Prefixes.SET.with(suffix));
      }
    }
  }
  
  public static final Optional<String> getUnSetMethodName(CDAssociationSymbol assoc) {
    if (assoc.isBidirectional() && !CDAssociationUtil.isMultiple(assoc)) {
      return Optional.ofNullable(Prefixes.RAW.with(Prefixes.UNSET.valueOf(),
          CDAssociationUtil.getAssociationName(assoc)));
    }
    return Optional.empty();
  }

  /* ************************************************************************* <p/> Methods for
   * associated classes. <p/>
   * ************************************************************************ */
  public static final String getValidatorClassName(CDAssociationSymbol assoc) {
    return assoc.getTargetType() + ValidatorCreator.VALIDATOR;
  }
  
  enum Prefixes {
    // method prefixes for adding elements
    ADD("add"),
    ADDALL("addAll"),
    SET("set"),
    PUT("put"),
    
    // method prefixes for retaining elements
    RETAINALL("retainAll"),
    
    // method prefixes for getting elements
    ITERATORKEY("iteratorKey"),
    ITERATORVALUE("iteratorValue"),
    ITERATOR("iterator"),
    LISTITERATOR("listIterator"),
    GET("get"),
    
    // method prefix for contains methods
    CONTAINS("contains"),
    CONTAINSKEY("containsKey"),
    CONTAINSVALUE("containsValue"),
    // doesn't clash with CONTAINSVALUE because of overloading
    CONTAINSOBJECT("containsValue"),
    CONTAINSALL("containsAll"),
    
    // method prefix for indexing elements
    INDEXOF("indexOf"),
    LASTINDEXOF("lastIndexOf"),
    
    // method prefixes for common methods
    SIZE("size"),
    ISEMPTY("isEmpty"),
    SUBLIST("subList"),
    
    // method prefix for raw methods
    RAW("raw"),
    
    // method prefixes for removing elements
    REMOVE("remove"),
    REMOVEALL("removeAll"),
    CLEAR("clear"),
    UNSET("unset");
    
    // handling strings
    private String prefix;
    
    private Prefixes(String prefix) {
      this.prefix = prefix;
    }
    
    public String valueOf() {
      return this.prefix;
    }
    
    public String with(String... suffix) {
      StringBuilder str = new StringBuilder();
      for (String s : suffix) {
        str.append(TransformationUtils.capitalize(s));
      }
      return TransformationUtils.makeCamelCase(this.prefix, str.toString());
    }
    
  }
  
}
