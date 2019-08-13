/* (c) https://github.com/MontiCore/monticore */

package backend.coretemplates.association.qualifiedordered;

import backend.coretemplates.association.AssociationAttributeFactory;
import backend.coretemplates.association.AssociationMethodFactory;
import backend.coretemplates.association.AssociationStrategy;
import backend.coretemplates.association.qualified.QualifiedAssociationStrategy;
import com.google.common.collect.Lists;
import common.util.CDAssociationUtil;
import backend.common.CoreTemplate;
import common.util.CDSimpleReferenceBuilder;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;

import java.util.List;
import java.util.Optional;

import static de.monticore.types.types._ast.TypesNodeFactory.createASTQualifiedName;

public class QualifiedOrderedAssociationStrategy implements AssociationStrategy {

  private String type;

  private String qualifierName;

  private String qualifierType;

  private GlobalExtensionManagement glex;

  private HookPoint hp;

  private QualifiedAssociationStrategy qual;

  private CDAssociationSymbol symbol;

  public QualifiedOrderedAssociationStrategy(
      CDAssociationSymbol symbol,
      GlobalExtensionManagement glex) {

    this.type = symbol.getTargetType().getName();
    this.glex = glex;
    this.symbol = symbol;
    this.qualifierName = CDAssociationUtil.getQualifierName(symbol).get();
    this.qualifierType = CDAssociationUtil.getQualifierType(symbol).get();
    this.qual = new QualifiedAssociationStrategy(symbol, glex);
  }

  @Override
  public Optional<ASTCDAttribute> getAssociationAttribute() {

    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      ASTCDAttribute attr = AssociationAttributeFactory
          .createQualifiedOrderedMany(CDAssociationUtil.getAssociationName(symbol),
              new CDSimpleReferenceBuilder()
                  .name(this.qualifierType).build(), new CDSimpleReferenceBuilder()
                  .name(type).build());

      glex.replaceTemplate(
          CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(),
          attr, CDSimpleReferenceBuilder.CollectionTypes.MULTIMAP_ORDERED_VALUE);

      return Optional.ofNullable(attr);

    } else {
      return this.qual.getAssociationAttribute();
    }
  }

  @Override
  public List<ASTCDMethod> getAddMethods() {
    List<ASTCDMethod> methods = this.qual.getAddMethods();

    if (CDAssociationUtil.isOne(symbol) || CDAssociationUtil.isOptional(symbol)) {
      // create ordered qualified add
      ASTCDMethod orderedAdd = AssociationMethodFactory
          .createQualifiedOrderedSetMethod(type, qualifierType, qualifierName, symbol);

      HookPoint orderedAddHp;

      if (this.hp != null) {
        orderedAddHp = hp;
      } else {
        orderedAddHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.QUALIFIED_ORDERED_SET_ONE_METHOD_BODY.valueOf(),
            type, CDAssociationUtil.getAssociationName(symbol), qualifierType, qualifierName);
      }
      glex.replaceTemplate(
          CoreTemplate.EMPTY_METHOD.toString(),
          orderedAdd, orderedAddHp);

      return methods;
    } else {
      // create ordered qualified add
      ASTCDMethod orderedAdd = AssociationMethodFactory
          .createQualifiedOrderedSetMethod(type, qualifierType, qualifierName, symbol);
      methods.add(orderedAdd);

      HookPoint orderedAddHp;

      if (this.hp != null) {
        orderedAddHp = hp;
      } else {
        orderedAddHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.QUALIFIED_ORDERED_SET_METHOD_BODY.valueOf(), type,
            CDAssociationUtil.getAssociationName(symbol), qualifierName,
            CDAssociationUtil.getAssociationName(symbol));
      }
      glex.replaceTemplate(
          CoreTemplate.EMPTY_METHOD.toString(),
          orderedAdd, orderedAddHp);

      return methods;
    }
  }

  @Override
  public List<ASTCDMethod> getGetMethods() {
    List<ASTCDMethod> lst = Lists.newArrayList();// this.qual.getGetMethods(cardinality);
    if (CDAssociationUtil.isOne(symbol) || CDAssociationUtil.isOptional(symbol)) {
      lst.addAll(this.qual.getGetMethods());
    } else {
      lst.addAll(createGetMany());
    }
    return lst;
  }

  @Override
  public List<ASTCDMethod> getRemoveMethods() {
    return this.qual.getRemoveMethods();
  }

  @Override
  public List<ASTCDMethod> getCommonMethods() {
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      return createCommonsMany();
    }
    return this.qual.getCommonMethods();
  }

  private List<ASTCDMethod> createGetMany() {
    // create ordered get
    ASTCDMethod orderedGet = AssociationMethodFactory.createOrderedQualifiedGetMethod(type, qualifierType,
        qualifierName, symbol);

    orderedGet.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex", "backend/data",
        "backend.data.onsistencyViolationException")));

    // create contains key
    ASTCDMethod containsValue = AssociationMethodFactory
        .createQualifiedContainsValueMethod(type, qualifierType, symbol);

    // create contains object
    ASTCDMethod containsObject = AssociationMethodFactory.createQualifiedContainsObjectMethod(type, symbol);

    // handle hook points
    HookPoint orderedGetHp, containsValueHp, containsObjectHp;

    if (this.hp != null) {
      orderedGetHp = containsValueHp = containsObjectHp = hp;
    } else {
      orderedGetHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_ORDERED_GET_MANY_METHOD_BODY.valueOf(),
          CDAssociationUtil.getAssociationName(symbol), qualifierName);

      containsValueHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_CONTAINS_VALUE_METHOD_BODY.valueOf(),
          CDAssociationUtil.getAssociationName(symbol), this.qualifierName, "o");

      containsObjectHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_CONTAINS_OBJECT_METHOD_BODY.valueOf(),
          CDAssociationUtil.getAssociationName(symbol), this.qualifierName);
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), orderedGet, orderedGetHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), containsValue, containsValueHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), containsObject, containsObjectHp);

    return Lists.newArrayList(orderedGet, containsValue, containsObject);
  }

  public List<ASTCDMethod> createCommonsMany() {
    // create list iterator
    ASTCDMethod listIterator = AssociationMethodFactory
        .createQualifiedListIteratorMethod(type, qualifierType, qualifierName, symbol);
    listIterator.getExceptionList()
        .add(createASTQualifiedName(Lists.newArrayList("dex", "backend/data", "backend.data.onsistencyViolationException")));

    // create parameterized list iterator
    ASTCDMethod parameterizedListIterator = AssociationMethodFactory
        .createQualifiedParameterizedListIterator(type, qualifierType, qualifierName, symbol);
    parameterizedListIterator.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
        "backend/data", "backend.data.onsistencyViolationException")));

    // create indexOf
    ASTCDMethod indexOf = AssociationMethodFactory.createQualifiedIndexOfMethod(type, qualifierType, "index", symbol);
    indexOf.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex", "backend/data",
        "backend.data.onsistencyViolationException")));

    // create lastIndexOf
    ASTCDMethod lastIndexOf = AssociationMethodFactory.createQualifiedLastIndexOf(type, qualifierType, qualifierName,
        symbol);
    lastIndexOf.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex", "backend/data",
        "backend.data.onsistencyViolationException")));

    // create subList
    ASTCDMethod subList = AssociationMethodFactory.createQualifiedSubListMethod(type, qualifierType, qualifierName,
        symbol);
    subList.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex", "backend/data",
        "backend.data.onsistencyViolationException")));

    // create indexOf
    ASTCDMethod iteratorKeys = AssociationMethodFactory.createIteratorKeyMethod(qualifierType, symbol);
    iteratorKeys.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
        "backend/data",
        "backend.data.onsistencyViolationException")));

    // create size
    ASTCDMethod size = AssociationMethodFactory.createSizeMethod(symbol);

    // create size
    ASTCDMethod sizeValue = AssociationMethodFactory.createQualifiedSizeMethod(symbol, qualifierType, qualifierName);

    // handle hook points
    HookPoint listIteratorHp, paramListIteratorHp, indexOfHp, lastIndexOfHp, subListHp, iteratorKeyHp, sizeHp, sizeValueHp;

    if (this.hp != null) {
      listIteratorHp = paramListIteratorHp = indexOfHp = lastIndexOfHp = subListHp = iteratorKeyHp = sizeHp = sizeValueHp = hp;
    } else {
      listIteratorHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_ORDERED_LISTITERATOR_MANY_METHOD_BODY.valueOf(),
          CDAssociationUtil.getAssociationName(symbol), qualifierName);

      paramListIteratorHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_ORDERED_LISTITERATOR_INDEX_MANY_METHOD_BODY.valueOf(),
          CDAssociationUtil.getAssociationName(symbol), qualifierName);

      indexOfHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_ORDERED_INDEXOF_MANY_METHOD_BODY.valueOf(),
          CDAssociationUtil.getAssociationName(symbol), qualifierName);

      lastIndexOfHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_ORDERED_LASTINDEXOF_MANY_METHOD_BODY.valueOf(),
          CDAssociationUtil.getAssociationName(symbol), qualifierName);

      subListHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_ORDERED_SUBLIST_MANY_METHOD_BODY.valueOf(),
          type, CDAssociationUtil.getAssociationName(symbol), qualifierName);

      iteratorKeyHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_ITERATORKEY_METHOD_BODY.valueOf(),
          CDAssociationUtil.getAssociationName(symbol), false);

      sizeHp = new TemplateHookPoint(AssociationMethodFactory.Templates.SIZE_METHOD_BODY.valueOf(),
          CDAssociationUtil.getAssociationName(symbol));

      sizeValueHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_SIZE_METHOD_BODY.valueOf(),
          CDAssociationUtil.getAssociationName(symbol), this.qualifierName, CDAssociationUtil.isOneToMany(symbol));
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), listIterator, listIteratorHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), parameterizedListIterator, paramListIteratorHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), indexOf, indexOfHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), lastIndexOf, lastIndexOfHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), subList, subListHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), iteratorKeys, iteratorKeyHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), size, sizeHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), sizeValue, sizeValueHp);

    return Lists.newArrayList(listIterator, parameterizedListIterator, indexOf, lastIndexOf, subList, iteratorKeys,
        size, sizeValue);
  }

  @Override
  public void setHookPoint(HookPoint hp) {
    this.hp = hp;
    this.qual.setHookPoint(hp);
  }

  @Override
  public List<ASTCDMethod> getRawAddMethods() {
    return Lists.newArrayList();
  }

  @Override
  public List<ASTCDMethod> getRawRemoveMethods() {
    return Lists.newArrayList();
  }

  @Override
  public void setAddMethodCall(String callMethod) {
    this.qual.setAddMethodCall(callMethod);
  }

  @Override
  public void setRemoveMethodCall(String callMethod) {
    this.qual.setRemoveMethodCall(callMethod);
  }
}
