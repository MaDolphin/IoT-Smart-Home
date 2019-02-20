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

package backend.coretemplates.association.qualified;

import backend.coretemplates.association.AssociationAttributeFactory;
import backend.coretemplates.association.AssociationMethodFactory;
import backend.coretemplates.association.AssociationStrategy;
import com.google.common.collect.Lists;
import common.util.CDAssociationUtil;
import common.util.TransformationUtils;
import common.util.CDSimpleReferenceBuilder;
import backend.common.CoreTemplate;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;

import java.util.List;
import java.util.Optional;

import static de.monticore.types.types._ast.TypesNodeFactory.createASTQualifiedName;

public class QualifiedAssociationStrategy implements AssociationStrategy {

  private HookPoint hp;

  private String name;

  private String type;

  private String qualifierType;

  private CDAssociationSymbol symbol;

  private String qualifierName;

  private GlobalExtensionManagement glex;

  private String callMethod;

  private String removeMethodCall;

  public QualifiedAssociationStrategy(CDAssociationSymbol symbol, GlobalExtensionManagement glex) {
    this.symbol = symbol;
    this.name = CDAssociationUtil.getAssociationName(symbol);
    this.type = symbol.getTargetType().getName();
    this.glex = glex;
    this.qualifierName = CDAssociationUtil.getQualifierName(symbol).get();
    this.qualifierType = CDAssociationUtil.getQualifierType(symbol).get();
  }

  @Override
  public Optional<ASTCDAttribute> getAssociationAttribute() {

    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {

      ASTCDAttribute attr = AssociationAttributeFactory.createQualifiedMany(
          this.name, new CDSimpleReferenceBuilder().name(this.qualifierType)
              .build(), new CDSimpleReferenceBuilder().name(type).build());

      glex.replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(),
          attr, CDSimpleReferenceBuilder.CollectionTypes.MULTIMAP_VALUE);

      return Optional.ofNullable(attr);

    }
    else {
      ASTCDAttribute attr;

      if (CDAssociationUtil.isOptional(symbol)) {
        attr = AssociationAttributeFactory.createQualifiedOne(this.name,
            new CDSimpleReferenceBuilder().name(this.qualifierType).build(),
            new CDSimpleReferenceBuilder().name("java.util.Optional<" + this.type + ">").build());
      }
      else {
        attr = AssociationAttributeFactory.createQualifiedOne(this.name,
            new CDSimpleReferenceBuilder().name(this.qualifierType).build(),
            new CDSimpleReferenceBuilder().name(type).build());
      }

      glex.replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(),
          attr, CDSimpleReferenceBuilder.CollectionTypes.MAP_VALUE);

      return Optional.ofNullable(attr);
    }
  }

  @Override
  public List<ASTCDMethod> getAddMethods() {
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      return createAddMany();
    }
    else {
      return createAddOne();
    }
  }

  @Override
  public List<ASTCDMethod> getGetMethods() {
    List<ASTCDMethod> methods = Lists.newArrayList();

    if (CDAssociationUtil.isOne(symbol) || CDAssociationUtil.isOptional(symbol)) {
      ASTCDMethod get;

      if (CDAssociationUtil.isOne(symbol)) {
        get = AssociationMethodFactory.createQualifiedGetMethod(type, qualifierType, symbol);
      }
      else {
        get = AssociationMethodFactory.createQualifiedGetMethod("Optional<" + type + ">", qualifierType, symbol);
      }

      // handle hook points
      HookPoint getHp;

      if (this.hp != null) {
        getHp = this.hp;
      }
      else {
        if (CDAssociationUtil.isOne(symbol)) {
          getHp = new TemplateHookPoint(AssociationMethodFactory.Templates.QUALIFIED_GET_METHOD_BODY.valueOf(),
              this.name, this.qualifierName);
        }
        else {
          getHp = new TemplateHookPoint(AssociationMethodFactory.Templates.OPTIONAL_QUALIFIED_GET_METHOD_BODY.valueOf(),
              this.name, this.qualifierName);
        }
      }

      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), get, getHp);
      methods.add(get);
    }

    // create contains key
    ASTCDMethod containsKey = AssociationMethodFactory.createQualifiedContainsKeyMethod(qualifierType, symbol);

    // create contains object
    ASTCDMethod containsObject = AssociationMethodFactory.createQualifiedContainsObjectMethod(type, symbol);

    // handle hook points
    HookPoint containsKeyHp, containsObjectHp;

    if (this.hp != null) {
      containsKeyHp = containsObjectHp = this.hp;
    }
    else {
      containsKeyHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_CONTAINS_KEY_METHOD_BODY.valueOf(), this.name,
          this.qualifierName);
      containsObjectHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_CONTAINS_OBJECT_METHOD_BODY.valueOf(), this.name,
          this.qualifierName);
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), containsKey, containsKeyHp);
    methods.add(containsKey);

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), containsObject, containsObjectHp);
    methods.add(containsObject);

    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      // create contains
      ASTCDMethod containsValue = AssociationMethodFactory
          .createQualifiedContainsValueMethod(type, qualifierType, symbol);

      // handle hook points
      HookPoint containsValueHp;

      if (this.hp != null) {
        containsValueHp = this.hp;
      }
      else {
        containsValueHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.QUALIFIED_CONTAINS_VALUE_METHOD_BODY.valueOf(),
            this.name, this.qualifierName, "o");
      }

      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), containsValue, containsValueHp);
      methods.add(containsValue);
    }

    return methods;
  }

  @Override
  public List<ASTCDMethod> getRemoveMethods() {
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      return createRemoveMany();
    }
    else {
      return createRemoveOne();
    }
  }

  @Override
  public List<ASTCDMethod> getCommonMethods() {
    List<ASTCDMethod> methods = Lists.newArrayList();

    // create isEmpty
    ASTCDMethod isEmpty = AssociationMethodFactory.createIsEmptyMethod(symbol);

    // create size
    ASTCDMethod size = AssociationMethodFactory.createSizeMethod(symbol);

    // create iteratorKey
    ASTCDMethod iteratorKey = AssociationMethodFactory.createIteratorKeyMethod(qualifierType, symbol);

    // handle hook points
    HookPoint isEmptyHp, sizeHp, iteratorKeyHp;
    if (this.hp != null) {
      isEmptyHp = sizeHp = iteratorKeyHp = this.hp;
    }
    else {

      isEmptyHp = new TemplateHookPoint(AssociationMethodFactory.Templates.ISEMPTY_METHOD_BODY.valueOf(), this.name);

      sizeHp = new TemplateHookPoint(AssociationMethodFactory.Templates.SIZE_METHOD_BODY.valueOf(), this.name);

      iteratorKeyHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_ITERATORKEY_METHOD_BODY.valueOf(), this.name, false);
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), isEmpty, isEmptyHp);

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), size, sizeHp);

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), iteratorKey, iteratorKeyHp);

    methods.add(isEmpty);
    methods.add(size);
    methods.add(iteratorKey);

    if (CDAssociationUtil.isOneToMany(symbol) || CDAssociationUtil.isMultiple(symbol)) {
      // create iteratorValue
      ASTCDMethod iteratorValue;

      // create isEmpty
      ASTCDMethod isEmptyValue = AssociationMethodFactory
          .createQualifiedIsEmptyMethod(symbol, qualifierType, qualifierName);

      // create size
      ASTCDMethod sizeValue = AssociationMethodFactory.createQualifiedSizeMethod(symbol, qualifierType, qualifierName);

      if (CDAssociationUtil.isOptional(symbol)) {
        iteratorValue = AssociationMethodFactory.createIteratorValueMethod(
            "Optional<" + type + ">", qualifierType, qualifierName, symbol);
      }
      else {
        iteratorValue = AssociationMethodFactory.createIteratorValueMethod(type, qualifierType, qualifierName, symbol);
      }

      if (CDAssociationUtil.isOneToMany(symbol)) {
        iteratorValue.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
            "backend/data",
            "backend.data.onsistencyViolationException")));
      }

      // handle hook points
      HookPoint iteratorValueHp, qualIsEmtpyHp, qualSizeHp;
      if (this.hp != null) {
        iteratorValueHp = qualIsEmtpyHp = qualSizeHp = this.hp;
      }
      else {
        iteratorValueHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.QUALIFIED_ITERATORVALUE_METHOD_BODY.valueOf(),
            this.name, CDAssociationUtil.isOneToMany(symbol), this.qualifierName);
        qualIsEmtpyHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.QUALIFIED_ISEMPTY_METHOD_BODY.valueOf(), this.name,
            this.qualifierName, CDAssociationUtil.isOneToMany(symbol));
        qualSizeHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.QUALIFIED_SIZE_METHOD_BODY.valueOf(), this.name,
            this.qualifierName, CDAssociationUtil.isOneToMany(symbol));
      }
      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), iteratorValue, iteratorValueHp);
      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), sizeValue, qualSizeHp);
      methods.add(iteratorValue);
      methods.add(sizeValue);

      if (!CDAssociationUtil.isOneToMany(symbol)) {
        glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), isEmptyValue, qualIsEmtpyHp);
        methods.add(isEmptyValue);
      }
    }

    return methods;
  }

  @Override
  public void setHookPoint(HookPoint hp) {
    this.hp = hp;
  }

  private List<ASTCDMethod> createAddOne() {
    // create add
    ASTCDMethod add;

    if (CDAssociationUtil.isOptional(symbol)) {
      add = AssociationMethodFactory.createQualifiedAddMethod("Optional<" + type + ">", qualifierType, qualifierName,
          symbol);
    }
    else {
      add = AssociationMethodFactory.createQualifiedAddMethod(type, qualifierType, qualifierName, symbol);
    }

    // handle hook points
    HookPoint addHp;

    if (this.hp != null) {
      addHp = this.hp;
    }
    else {
      addHp = new TemplateHookPoint(AssociationMethodFactory.Templates.QUALIFIED_ADD_METHOD_BODY.valueOf(), this.name,
          this.qualifierName, "o",
          this.removeMethodCall != null ? (CDAssociationUtil.isOptional(symbol) ? "get()." : "")
              + this.callMethod : null);
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), add, addHp);

    return Lists.newArrayList(add);
  }

  private List<ASTCDMethod> createAddMany() {
    // create add
    ASTCDMethod put = AssociationMethodFactory.createQualifiedPutMethod(type, qualifierType, qualifierName, symbol);

    // handle hook points
    HookPoint putHp;

    if (this.hp != null) {
      putHp = this.hp;
    }
    else {
      putHp = new TemplateHookPoint(AssociationMethodFactory.Templates.QUALIFIED_PUT_METHOD_BODY.valueOf(), this.name,
          this.qualifierName, "o", this.callMethod);
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), put, putHp);

    return Lists.newArrayList(put);
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
    this.callMethod = callMethod;
  }

  @Override
  public void setRemoveMethodCall(String callMethod) {
    this.removeMethodCall = callMethod;
  }

  private List<ASTCDMethod> createRemoveOne() {

    // create remove
    ASTCDMethod remove = AssociationMethodFactory.createQualifiedRemoveMethod(qualifierType, symbol);

    // create clear
    ASTCDMethod clear = AssociationMethodFactory.createClearMethod(symbol);

    // handle hook points
    HookPoint removeHp, clearHp;

    if (this.hp != null) {
      removeHp = clearHp = this.hp;
    }
    else {
      removeHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_REMOVE_METHOD_BODY.valueOf(),
          this.qualifierName, this.name,
          this.removeMethodCall != null ? (CDAssociationUtil.isOptional(symbol) ? "get()." : "")
              + this.removeMethodCall : null);

      if (CDAssociationUtil.isOptional(symbol)) {
        clearHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.OPTIONAL_QUALIFIED_CLEAR_METHOD_BODY.valueOf(),
            this.type, this.qualifierType, this.name,
            TransformationUtils.makeCamelCase("remove", this.name));
      }
      else {
        clearHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.QUALIFIED_CLEAR_METHOD_BODY.valueOf(), this.type,
            this.qualifierType, this.name, TransformationUtils.makeCamelCase(
            "remove", this.name));
      }
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), remove, removeHp);

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), clear, clearHp);

    return Lists.newArrayList(remove, clear);
  }

  private List<ASTCDMethod> createRemoveMany() {
    // create remove
    ASTCDMethod remove = AssociationMethodFactory.createQualifiedMultimapRemoveMethod(qualifierType, type,
        qualifierName, symbol);

    // create remove
    ASTCDMethod removeOne = AssociationMethodFactory.createQualifiedRemoveMethod(qualifierType, symbol);

    if (CDAssociationUtil.isOneToMany(symbol)) {
      remove.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
          "backend/data", "backend.data.onsistencyViolationException")));
    }

    // create clear
    ASTCDMethod clear = AssociationMethodFactory.createClearMethod(symbol);

    // handle hook points
    HookPoint removeHp, clearHp, removeOneHp;

    if (this.hp != null) {
      removeHp = clearHp = removeOneHp = this.hp;
    }
    else {
      removeHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_MULTIMAP_REMOVE_METHOD_BODY.valueOf(),
          this.qualifierName, this.name, this.type, "o", this.removeMethodCall,
          CDAssociationUtil.isOneToMany(symbol));

      clearHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_MULTIMAP_CLEAR_METHOD_BODY.valueOf(),
          this.type, this.qualifierType, this.name, TransformationUtils.makeCamelCase("remove", this.name));

      removeOneHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.QUALIFIED_REMOVE_MULTIMAP_METHOD_BODY.valueOf(),
          this.qualifierName, this.type, this.name, this.removeMethodCall);
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), remove, removeHp);

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), clear, clearHp);

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), removeOne, removeOneHp);

    return Lists.newArrayList(remove, clear, removeOne);
  }

}
