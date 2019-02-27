/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package backend.coretemplates.association.ordered;

import backend.coretemplates.association.AssociationAttributeFactory;
import backend.coretemplates.association.AssociationMethodFactory;
import backend.coretemplates.association.AssociationStrategy;
import backend.coretemplates.association.ordinary.DefaultAssociationStrategy;
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

public class OrderedAssociationStrategy implements AssociationStrategy {

  private String type;

  private String name;

  private GlobalExtensionManagement glex;

  private HookPoint hp;

  private DefaultAssociationStrategy def;

  private String callMethod;

  private CDAssociationSymbol symbol;

  public OrderedAssociationStrategy(CDAssociationSymbol symbol,
                                    GlobalExtensionManagement glex) {
    this.type = symbol.getTargetType().getName();
    this.name = CDAssociationUtil.getAssociationName(symbol);
    this.glex = glex;
    this.symbol = symbol;

    this.def = new DefaultAssociationStrategy(symbol, glex);
  }

  @Override
  public Optional<ASTCDAttribute> getAssociationAttribute() {
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      ASTCDAttribute attr = AssociationAttributeFactory.createOrdered(name,
          new CDSimpleReferenceBuilder().name(type).build());

      glex.replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(),
          attr, CDSimpleReferenceBuilder.CollectionTypes.LIST_VALUE);

      return Optional.ofNullable(attr);
    } else if (CDAssociationUtil.isOne(symbol)) {
      ASTCDAttribute attr = AssociationAttributeFactory.createOne(name,
          new CDSimpleReferenceBuilder().name(type).build());
      return Optional.ofNullable(attr);
    } else {
      ASTCDAttribute attr = AssociationAttributeFactory.createOne(name,
          CDSimpleReferenceBuilder.DataTypes.createOptional(type));
      glex.replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(),
          attr, CDSimpleReferenceBuilder.DataTypes.OPTIONAL_ABSENT);
      return Optional.ofNullable(attr);
    }
  }

  @Override
  public List<ASTCDMethod> getAddMethods() {
    List<ASTCDMethod> methods = this.def.getAddMethods();

    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      // create ordered add
      ASTCDMethod orderedAdd = AssociationMethodFactory.createOrderedAddMethod(type, symbol);
      // create ordered add all
      ASTCDMethod orderedAddAll = AssociationMethodFactory.createOrderedAddAllMethod(type, symbol);
      // create orderedSet
      ASTCDMethod orderedSet = AssociationMethodFactory.createOrderedSetMethod(type, symbol);
      // handle hook points
      HookPoint orderedAddHp, orderedAddAllHp, orderedSetHp;

      if (this.hp != null) {
        orderedAddHp = orderedAddAllHp = orderedSetHp = hp;
      } else {
        orderedAddHp = new TemplateHookPoint(AssociationMethodFactory.Templates.ORDERED_ADD_METHOD_BODY.valueOf(), name,
            name, this.callMethod);

        orderedAddAllHp = new TemplateHookPoint(AssociationMethodFactory.Templates.ORDERED_ADDALL_METHOD_BODY.valueOf(),
            type, name, this.callMethod);

        orderedSetHp = new TemplateHookPoint(AssociationMethodFactory.Templates.ORDERED_SET_METHOD_BODY.valueOf(), type,
            name);
      }

      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), orderedAdd, orderedAddHp);
      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), orderedAddAll, orderedAddAllHp);
      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), orderedSet, orderedSetHp);

      methods.add(orderedAdd);
      methods.add(orderedAddAll);
      methods.add(orderedSet);
    }

    return methods;
  }

  @Override
  public List<ASTCDMethod> getRawAddMethods() {
    return this.def.getRawAddMethods();
  }

  @Override
  public List<ASTCDMethod> getGetMethods() {
    List<ASTCDMethod> methods = this.def.getGetMethods();
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      // create ordered get
      ASTCDMethod orderedGet = AssociationMethodFactory.createOrderedGetMethod(type, symbol);
      HookPoint orderedGetHp;

      if (this.hp != null) {
        orderedGetHp = hp;
      } else {
        orderedGetHp = new TemplateHookPoint(AssociationMethodFactory.Templates.ORDERED_GET_METHOD_BODY.valueOf(), name,
            CDAssociationUtil.isOneToMany(symbol));
      }

      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), orderedGet, orderedGetHp);

      methods.add(orderedGet);
    }
    return methods;

  }

  @Override
  public List<ASTCDMethod> getRemoveMethods() {
    return this.def.getRemoveMethods();
  }

  @Override
  public List<ASTCDMethod> getCommonMethods() {
    List<ASTCDMethod> methods = this.def.getCommonMethods();
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      methods.addAll(createCommon());
    }
    return methods;
  }

  @Override
  public void setHookPoint(HookPoint hp) {
    this.hp = hp;
    this.def.setHookPoint(hp);
  }

  private List<ASTCDMethod> createCommon() {
    // create list iterator
    ASTCDMethod listIterator = AssociationMethodFactory.createListIteratorMethod(type, symbol);
    // create parameterized list iterator
    ASTCDMethod parameterizedListIterator = AssociationMethodFactory
        .createParameterizedListIteratorMethod(type, symbol);
    // create indexOf
    ASTCDMethod indexOf = AssociationMethodFactory.createIndexOfMethod(type, symbol);
    // create lastIndexOf
    ASTCDMethod lastIndexOf = AssociationMethodFactory.createLastIndexOfMethod(type, symbol);
    // create subList
    ASTCDMethod subList = AssociationMethodFactory.createSubListMethod(type, symbol);

    if (CDAssociationUtil.isOneToMany(symbol)) {
      listIterator.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
          "backend/data", "backend.data.onsistencyViolationException")));
      parameterizedListIterator.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
          "backend/data", "backend.data.onsistencyViolationException")));
      indexOf.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
          "backend/data", "backend.data.onsistencyViolationException")));
      lastIndexOf.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
          "backend/data", "backend.data.onsistencyViolationException")));
      subList.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
          "backend/data", "backend.data.onsistencyViolationException")));
    }

    // handle hook points
    HookPoint listIteratorHp, paramListIteratorHp, indexOfHp, lastIndexOfHp, subListHp;

    if (this.hp != null) {
      listIteratorHp = paramListIteratorHp = indexOfHp = lastIndexOfHp = subListHp = hp;
    } else {
      listIteratorHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.ORDERED_LISTITERATOR_METHOD_BODY.valueOf(), name);

      paramListIteratorHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.ORDERED_LISTITERATOR_INDEX_METHOD_BODY.valueOf(), name);

      indexOfHp = new TemplateHookPoint(AssociationMethodFactory.Templates.ORDERED_INDEXOF_METHOD_BODY.valueOf(), name);

      lastIndexOfHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.ORDERED_LASTINDEXOF_METHOD_BODY.valueOf(), name);

      subListHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.ORDERED_SUBLIST_METHOD_BODY.valueOf(), name);
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), listIterator, listIteratorHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), parameterizedListIterator, paramListIteratorHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), indexOf, indexOfHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), lastIndexOf, lastIndexOfHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), subList, subListHp);

    return Lists.newArrayList(listIterator, parameterizedListIterator, indexOf, lastIndexOf, subList);
  }

  @Override
  public List<ASTCDMethod> getRawRemoveMethods() {
    return this.def.getRawRemoveMethods();
  }

  @Override
  public void setAddMethodCall(String callMethod) {
    this.callMethod = callMethod;
    this.def.setAddMethodCall(callMethod);
  }

  @Override
  public void setRemoveMethodCall(String callMethod) {
    this.def.setRemoveMethodCall(callMethod);
  }
}
