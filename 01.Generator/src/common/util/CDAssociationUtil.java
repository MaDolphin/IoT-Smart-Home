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

import common.CDSymbolTable;
import de.monticore.ast.ASTNode;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

public class CDAssociationUtil {

  private boolean leftToRight = true;

  public void setDirection(boolean leftToRight) {
    this.leftToRight = leftToRight;
  }

  public boolean isReflexive(ASTCDAssociation ast) {
    Optional<CDAssociationSymbol> symbol = leftToRight ?
        ast.getLeftToRightSymbol() :
        ast.getRightToLeftSymbol();
    checkArgument(symbol.isPresent());
    return isReflexiveAssociation(symbol.get());
  }

  public boolean isOrdered(ASTCDAssociation ast) {
    Optional<CDAssociationSymbol> symbol = leftToRight ?
        ast.getLeftToRightSymbol() :
        ast.getRightToLeftSymbol();
    checkArgument(symbol.isPresent());
    return isOrdered(symbol.get());
  }

  public String getAssociationName(ASTCDAssociation ast) {
    Optional<CDAssociationSymbol> symbol = leftToRight ?
        ast.getLeftToRightSymbol() :
        ast.getRightToLeftSymbol();
    checkArgument(symbol.isPresent());
    return getAssociationName(symbol.get());
  }

  public Optional<ASTCDQualifier> getOppositeQualifier(ASTCDAssociation ast) {
    Optional<CDAssociationSymbol> symbol = leftToRight ?
        ast.getLeftToRightSymbol() :
        ast.getRightToLeftSymbol();
    checkArgument(symbol.isPresent());
    return getOppositeQualifier(symbol.get());
  }

  public Optional<String> getQualifierType(ASTCDAssociation ast) {
    Optional<CDAssociationSymbol> symbol = leftToRight ?
        ast.getLeftToRightSymbol() :
        ast.getRightToLeftSymbol();
    checkArgument(symbol.isPresent());
    return getQualifierType(symbol.get());
  }

  public Optional<String> getQualifierName(ASTCDAssociation ast) {
    Optional<CDAssociationSymbol> symbol = leftToRight ?
        ast.getLeftToRightSymbol() :
        ast.getRightToLeftSymbol();
    checkArgument(symbol.isPresent());
    return getQualifierName(symbol.get());
  }

  public Optional<ASTCDAttribute> getQualifierReferencedAttribute(ASTCDAssociation ast) {
    Optional<CDAssociationSymbol> symbol = leftToRight ?
        ast.getLeftToRightSymbol() :
        ast.getRightToLeftSymbol();
    checkArgument(symbol.isPresent());
    Optional<CDFieldSymbol> f = getQualifierReferencedAttribute(symbol.get());
    checkArgument(f.isPresent());
    return Optional.ofNullable((ASTCDAttribute) f.get().getAstNode().get());
  }

  /* from here on the symbol table is used */
  public static boolean isPrimaryAssociation(CDAssociationSymbol ast) {
    return (ast.getSourceCardinality() == null || !ast.getSourceCardinality()
        .isMultiple())
        && (ast.getTargetCardinality() == null || !ast.getTargetCardinality()
        .isMultiple());
  }

  public static boolean isReflexiveAssociation(CDAssociationSymbol ast) {
    return ast.getSourceType().getName().equals(ast.getTargetType().getName());
  }

  public static boolean isLeftToRightsss(ASTCDAssociation ast) {
    return ast.isLeftToRight() || ast.isBidirectional() || ast.isUnspecified();
  }

  public static boolean isRightToLeft(ASTCDAssociation ast) {
    return ast.isRightToLeft() || ast.isBidirectional() || ast.isUnspecified();
  }

  public static String getLeftRole(CDAssociationSymbol ast) {
    if (ast.getAstNode().isPresent()
        && ast.getAstNode().get() instanceof ASTCDAssociation) {
      Optional<String> leftRole = ((ASTCDAssociation) ast.getAstNode().get())
          .getLeftRoleOpt();
      if (leftRole.isPresent()) {
        return leftRole.get();
      }
    }
    return "";
  }

  public static String getRightRole(CDAssociationSymbol ast) {
    if (ast.getAstNode().isPresent()
        && ast.getAstNode().get() instanceof ASTCDAssociation) {
      Optional<String> rightRole = ((ASTCDAssociation) ast.getAstNode().get())
          .getRightRoleOpt();
      if (rightRole.isPresent()) {
        return rightRole.get();
      }
    }
    return "";
  }

  public static Optional<ASTCDQualifier> getLeftQualifier(
      CDAssociationSymbol ast) {
    if (ast.getAstNode().isPresent()
        && ast.getAstNode().get() instanceof ASTCDAssociation) {
      return ((ASTCDAssociation) ast.getAstNode().get()).getLeftQualifierOpt();
    }
    return Optional.empty();
  }

  public static Optional<ASTCDQualifier> getRightQualifier(
      CDAssociationSymbol ast) {
    if (ast.getAstNode().isPresent()
        && ast.getAstNode().get() instanceof ASTCDAssociation) {
      return ((ASTCDAssociation) ast.getAstNode().get()).getRightQualifierOpt();
    }
    return Optional.empty();
  }

  public static Optional<ASTCDQualifier> getQualifier(CDAssociationSymbol symbol) {
    if (symbol.getQualifier().isPresent()) {
      Optional<ASTNode> n = symbol.getQualifier().get().getAstNode();
      if (n.isPresent()) {
        return Optional.ofNullable((ASTCDQualifier) n.get());
      }
    }
    return Optional.empty();
  }

  public static Optional<ASTCDQualifier> getOppositeQualifier(CDAssociationSymbol symbol) {
    Optional<CDAssociationSymbol> s = findOppositeSymbol(symbol);
    if (s.isPresent()) {
      if (s.get().getQualifier().isPresent()) {
        return Optional
            .ofNullable((ASTCDQualifier) s.get().getQualifier().get().getAstNode().get());
      }
    }
    return Optional.empty();
  }

  public static Optional<CDAssociationSymbol> findOppositeSymbol(CDAssociationSymbol symbol) {
    if (symbol.isBidirectional()) {
      for (CDAssociationSymbol as : symbol.getTargetType().getAllAssociations()) {
        if (as.getTargetType().getName().equals(symbol.getSourceType().getName()) && symbol
            .getAstNode()
            .equals(as.getAstNode())) {
          // if is self loop
          if (symbol.getTargetType().getName().equals(symbol.getSourceType().getName())) {
            if (!getAssociationName(symbol).equals(getAssociationName(as))) {
              return Optional.ofNullable(as);
            }
          }
          else {
            return Optional.ofNullable(as);
          }
        }
      }
    }
    return Optional.empty();
  }

  public static String getAssociationName(CDAssociationSymbol symbol) {
    StringBuilder name = new StringBuilder();
    if (symbol.getTargetRole().isPresent()) {
      name.append(symbol.getTargetRole().get());
    }
    else if (symbol.getSourceRole().isPresent()) {
      name.append(symbol.getSourceRole().get());
    }
    else if (symbol.getAssocName().isPresent()) {
      name.append(symbol.getAssocName().get());
    }
    else {
      name.append(symbol.getTargetType().getName());
    }

    return TransformationUtils.uncapitalize(name.toString());
  }

  public static Optional<String> getOppositeAssociationNameOpt(CDAssociationSymbol symbol) {
    Optional<CDAssociationSymbol> s = findOppositeSymbol(symbol);
    if (s.isPresent()) {
      return Optional.of(getAssociationName(s.get()));
    }
    return Optional.empty();
  }

  public static String getOppositeAssociationName(CDAssociationSymbol symbol) {
    return getOppositeAssociationNameOpt(symbol).orElse("");
  }

  public static ASTModifier removeModifierByName(ASTModifier modifier, List<String> names) {
    if (modifier.getStereotypeOpt().isPresent()) {
      ASTModifier newModifier = modifier.deepClone();
      newModifier.getStereotype().getValueList().clear();
      newModifier.getStereotype().getValueList().addAll(
          modifier.getStereotype().getValueList().stream()
              .filter(value -> !names.contains(value.getName()))
              .collect(Collectors.toList()));
      for (ASTCDStereoValue value : modifier.getStereotype().getValueList()) {
        if (!names.contains(value.getName())) {
          newModifier.getStereotype().getValueList().add(value);
        }
      }

      return newModifier;
    }
    else {
      return modifier;
    }
  }

  public static boolean isOrdered(Optional<ASTModifier> modifier) {
    // check if the given Modifier has the <<ordered>> stereotype

    if (modifier.isPresent() && modifier.get().getStereotypeOpt().isPresent()) {
      return hasOrderedStereotype(modifier.get().getStereotypeOpt());
    }

    return false;
  }

  public static boolean isOrdered(CDAssociationSymbol symbol) {
    return symbol.getStereotype("ordered").isPresent();
  }

  public static boolean isQualified(CDAssociationSymbol symbol) {
    return symbol.getQualifier().isPresent();
  }

  public final static boolean hasOrderedStereotype(
      Optional<ASTCDStereotype> stereotype) {
    // check if the given Modifier has the <<ordered>> stereotype
    if (stereotype.isPresent()) {

      for (ASTCDStereoValue stereo : stereotype.get().getValueList()) {
        if ("ordered".equals(stereo.getName())) {
          return true;
        }
      }

    }

    return false;
  }

  public static Optional<String> getQualifierType(CDAssociationSymbol symbol) {
    if (symbol.getQualifier().isPresent()) {
      ASTCDQualifier q = (ASTCDQualifier) symbol.getQualifier().get().getAstNode().get();
      return Optional.ofNullable(TypesPrinter.printType(q.getType()));
    }
    return Optional.empty();
  }

  public static Optional<String> getQualifierName(CDAssociationSymbol symbol) {

    if (symbol.getQualifier().isPresent()) {
      ASTCDQualifier q = (ASTCDQualifier) symbol.getQualifier().get().getAstNode().get();
      return Optional.ofNullable(q.getName());
    }
    return Optional.empty();
  }

  public Optional<CDFieldSymbol> getQualifierReferencedAttribute(CDAssociationSymbol symbol) {
    CDFieldSymbol fs = null;
    Optional<String> optQual = getQualifierName(symbol);
    if (!optQual.isPresent()) {
      return Optional.ofNullable(fs);
    }
    String name = optQual.get();
    Set<CDFieldSymbol> set = symbol.getTargetType().getAllVisibleFields()
        .stream().filter(field -> name.equals(field.getName())).collect(Collectors.toSet());
    if (set == null || set.isEmpty() || set.size() > 1) {
      return Optional.ofNullable(fs);
    }
    else {
      fs = set.iterator().next();
      return Optional.ofNullable(fs);
    }
  }

  public static Optional<CDFieldSymbol> toCDFieldSymbol(CDAssociationSymbol symbol) {
    String name = getAssociationName(symbol);
    CDTypeSymbolReference ts;
    if (symbol.getTargetType() instanceof CDTypeSymbolReference) {
      ts = (CDTypeSymbolReference) symbol.getTargetType();
    }
    else {
      return Optional.empty();
    }
    return Optional.of(new CDFieldSymbol(name, ts));
  }

  public static boolean isOppositeQualified(CDAssociationSymbol symbol) {
    return getOppositeQualifier(symbol).isPresent();
  }

  public static boolean isMultiple(CDAssociationSymbol symbol) {
    return symbol.getTargetCardinality().isMultiple();
  }

  public static boolean isOneToMany(CDAssociationSymbol symbol) {
    return symbol.getTargetCardinality().isMultiple()
        && symbol.getTargetCardinality().getMin() == 1;
  }

  public static boolean isOptional(CDAssociationSymbol symbol) {
    return symbol.getTargetCardinality().getMin() == 0
        && symbol.getTargetCardinality().getMax() == 1;
  }

  public static boolean isOne(CDAssociationSymbol symbol) {
    return symbol.getTargetCardinality().getMin() == 1
        && !symbol.getTargetCardinality().isMultiple();
  }

  public static boolean isSourceMultiple(CDAssociationSymbol symbol) {
    return symbol.getSourceCardinality().isMultiple();
  }

  public static boolean isSourceOneToMany(CDAssociationSymbol symbol) {
    return symbol.getSourceCardinality().isMultiple()
        && symbol.getSourceCardinality().getMin() == 1;
  }

  public static boolean isSourceOptional(CDAssociationSymbol symbol) {
    return symbol.getSourceCardinality().getMin() == 0
        && symbol.getSourceCardinality().getMax() == 1;
  }

  public static boolean isTargetOptional(CDAssociationSymbol symbol) {
    return symbol.getTargetCardinality().getMin() == 0
        && symbol.getTargetCardinality().getMax() == 1;
  }

  public static boolean isTargetOne(CDAssociationSymbol symbol) {
    return symbol.getTargetCardinality().getMin() == 1
        && !symbol.getTargetCardinality().isMultiple();
  }

  public static boolean isTargetMultiple(CDAssociationSymbol symbol) {
    return symbol.getTargetCardinality().isMultiple();
  }

  public static boolean isSourceOne(CDAssociationSymbol symbol) {
    return symbol.getSourceCardinality().getMin() == 1
        && !symbol.getSourceCardinality().isMultiple();
  }

  public static boolean isDerived(CDAssociationSymbol symbol) {
    ASTCDAssociation assoc = (ASTCDAssociation) symbol.getAstNode().get();
    return assoc.isDerived();
  }

  public static List<CDAssociationSymbol> getAssocsToCheckMultiply(
      Collection<CDAssociationSymbol> allAssociations) {
    // multiple cardinality [1..*]
    return allAssociations.stream()
        .filter(a -> a.getTargetCardinality().isMultiple())
        .filter(a -> a.getTargetCardinality().getMin() == 1)
        .collect(Collectors.toList());
  }

  public static List<CDAssociationSymbol> getAssocsToCheckSingle(
      Collection<CDAssociationSymbol> allAssociations) {
    // [1] cardinality
    return allAssociations.stream()
        .filter(a -> !a.getTargetCardinality().isMultiple())
        .filter(a -> a.getTargetCardinality().getMin() == 1)
        .collect(Collectors.toList());
  }

  public static boolean isComposition(CDAssociationSymbol symbol) {
    checkArgument(symbol.getAstNode().isPresent());
    return ((ASTCDAssociation) symbol.getAstNode().get()).isComposition();
  }

  public static ASTSimpleReferenceType getAssociationType(CDAssociationSymbol symbol) {
    ASTSimpleReferenceType type = null;

    String targetType = symbol.getTargetType().getName();

    if (CDAssociationUtil.isQualified(symbol) && !CDAssociationUtil.isOrdered(symbol)) {
      if (CDAssociationUtil.isOne(symbol) || CDAssociationUtil.isOptional(symbol)) {
        type = CDSimpleReferenceBuilder.CollectionTypes.createMap(
            new CDSimpleReferenceBuilder().name(CDAssociationUtil.getQualifierType(symbol).get())
                .build(),
            new CDSimpleReferenceBuilder().name(CDAssociationUtil.isOptional(symbol)
                ? "Optional<" + targetType + ">" : targetType).build());
      }
      else {
        type = CDSimpleReferenceBuilder.CollectionTypes.createMultimap(
            new CDSimpleReferenceBuilder().name(CDAssociationUtil.getQualifierType(symbol).get())
                .build(),
            new CDSimpleReferenceBuilder().name(CDAssociationUtil.isOptional(symbol)
                ? "Optional<" + targetType + ">" : targetType).build());
      }
    }
    else if (CDAssociationUtil.isQualified(symbol) && CDAssociationUtil.isOrdered(symbol)) {
      if (CDAssociationUtil.isOne(symbol) || CDAssociationUtil.isOptional(symbol)) {
        type = CDSimpleReferenceBuilder.CollectionTypes.createMap(
            new CDSimpleReferenceBuilder().name(CDAssociationUtil.getQualifierType(symbol).get())
                .build(),
            new CDSimpleReferenceBuilder().name(CDAssociationUtil.isOptional(symbol)
                ? "Optional<" + targetType + ">" : targetType).build());
      }
      else {
        type = CDSimpleReferenceBuilder.CollectionTypes.createListMultimap(
            new CDSimpleReferenceBuilder().name(CDAssociationUtil.getQualifierType(symbol).get())
                .build(),
            new CDSimpleReferenceBuilder().name(CDAssociationUtil.isOptional(symbol)
                ? "Optional<" + targetType + ">" : targetType).build());
      }
    }
    else if (CDAssociationUtil.isMultiple(symbol)) {
      type = CDSimpleReferenceBuilder.CollectionTypes.createList(targetType);
    }
    else if (CDAssociationUtil.isOptional(symbol)) {
      type = CDSimpleReferenceBuilder.DataTypes.createOptional(targetType);
    }
    else {
      type = new CDSimpleReferenceBuilder().name(targetType).build();
    }
    return type;
  }

  public static List<CDAssociationSymbol> getLocalAssociations(ASTCDClass clazz,
      CDSymbolTable symbolTable) {
    return symbolTable.getLocalAssociations(clazz).stream()
        .filter(x -> !x.isDerived()).collect(Collectors.toList());
  }

  public static List<CDAssociationSymbol> getInheritedMandatoryAssociations(ASTCDClass clazz,
      CDSymbolTable symbolTable) {
    return symbolTable.getInheritedMandatoryAssociations(clazz).stream()
        .filter(x -> !x.isDerived()).collect(Collectors.toList());
  }

  public static List<CDAssociationSymbol> getInheritedAssociations(ASTCDClass clazz,
      CDSymbolTable symbolTable) {
    return symbolTable.getInheritedAssociations(clazz).stream()
        .filter(x -> !x.isDerived()).collect(Collectors.toList());
  }

  public static List<CDAssociationSymbol> getAllAssociations(ASTCDClass clazz,
      CDSymbolTable symbolTable) {
    List<CDAssociationSymbol> localAssociations = getLocalAssociations(clazz, symbolTable);
    localAssociations.addAll(getInheritedAssociations(clazz, symbolTable));
    return localAssociations;
  }

  public static List<CDAssociationSymbol> getAllMandatoryAssociations(ASTCDClass clazz,
      CDSymbolTable symbolTable) {
    return Stream
        .concat(symbolTable.getLocalMandatoryAssociations(clazz).stream(),
            getInheritedMandatoryAssociations(clazz, symbolTable).stream())
        .collect(Collectors.toList()).stream()
        .filter(x -> !x.isDerived()).collect(Collectors.toList());
  }

  public static Stream<String> getInheritedMandatoryAssociationNames(ASTCDClass clazz,
      CDSymbolTable symbolTable) {
    return getInheritedMandatoryAssociations(clazz, symbolTable).stream()
        .filter(x -> requiredBySuper(clazz, x, symbolTable))
        .map(CDAssociationUtil::getAssociationName);
  }

  public static Stream<String> getInheritedAssociationNames(ASTCDClass clazz,
      CDSymbolTable symbolTable) {
    return getInheritedAssociations(clazz, symbolTable).stream()
        .filter(x -> requiredBySuper(clazz, x, symbolTable))
        .map(CDAssociationUtil::getAssociationName);
  }

  public static boolean requiredBySuper(ASTCDClass clazz, CDAssociationSymbol association,
      CDSymbolTable symbolTable) {
    CDTypeSymbol srcType = association.getSourceType();
    Optional<CDTypeSymbol> cdType = symbolTable.resolve(clazz.getName());
    checkArgument(cdType.isPresent());
    return !(srcType.isInterface() && cdType.get().getInterfaces().stream().map(x -> x.getName())
        .collect(Collectors.toList()).contains(srcType.getName()));
  }

  public static boolean hasQualifiedOrderedAssociations(
      List<CDAssociationSymbol> allVisibleAssocs) {
    for (CDAssociationSymbol symbol : allVisibleAssocs) {
      if (CDAssociationUtil.isQualified(symbol) && CDAssociationUtil.isOrdered(symbol)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasQualifiedNotOrderedAssociations(
      List<CDAssociationSymbol> allVisibleAssocs) {
    for (CDAssociationSymbol symbol : allVisibleAssocs) {
      if (CDAssociationUtil.isQualified(symbol) && !CDAssociationUtil.isOrdered(symbol)) {
        return true;
      }
    }
    return false;
  }

  public static ASTCDParameter associationToCDParameter(CDAssociationSymbol symbol) {
    return CD4AnalysisMill.cDParameterBuilder()
        .setName(TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(symbol)))
        .setType(getAssociationType(symbol)).build();
  }
}
