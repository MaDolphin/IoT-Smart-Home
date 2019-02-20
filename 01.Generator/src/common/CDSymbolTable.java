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

package common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import common.util.CDAssociationUtil;
import common.util.CDAttributeBuilder;
import common.util.CDAttributeUtil;
import common.util.GetterSetterHelper;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.*;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class helps in creating, editing, and managing the symboltable for the
 * CD4Analysis language
 *
 * @author Alexander Roth
 */
// TODO GV, AR: use cd4analisis#CDSymbolTable after the next releasing of
// cd4analisis
public class CDSymbolTable {

  private final CD4AnalysisLanguage cd4AnalysisLang = new CD4AnalysisLanguage();

  private final ResolvingConfiguration resolverConfiguration = new ResolvingConfiguration();

  private MutableScope cdScope;

  private GlobalScope globalScope;

  private ArtifactScope artifactScope;

  public CDSymbolTable(ASTCDCompilationUnit ast, List<File> modelPaths) {
    checkNotNull(modelPaths);

    resolverConfiguration.addDefaultFilters(cd4AnalysisLang.getResolvingFilters());

    this.globalScope = createSymboltable(ast, modelPaths);
    this.artifactScope = (ArtifactScope) this.globalScope.getSubScopes()
        .iterator().next();
    this.cdScope = artifactScope.getSubScopes().iterator().next();
  }

  /**
   * Create new CDSymbolTable Helper from the symbol table of the passed ast
   *
   * @param ast
   */
  public CDSymbolTable(ASTCDCompilationUnit ast) {
    checkArgument(ast.getCDDefinition().getEnclosingScopeOpt().isPresent());
    checkArgument(ast.getCDDefinition().getEnclosingScope().getEnclosingScope().isPresent());

    this.globalScope = (GlobalScope) ast.getCDDefinition().getEnclosingScope()
        .getEnclosingScope().get();
    this.artifactScope = (ArtifactScope) ast.getCDDefinition().getEnclosingScope();
    this.cdScope = artifactScope.getSubScopes().iterator().next();
  }

  /**
   * Create a new symbol table from a given ASTCompilation unit
   *
   * @param ast
   * @param modelPaths
   * @return
   */
  private GlobalScope createSymboltable(ASTCDCompilationUnit ast,
                                        List<File> modelPaths) {

    // convert paths
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    ModelPath modelPath = new ModelPath(p);
    GlobalScope scope = new GlobalScope(modelPath,
        cd4AnalysisLang, resolverConfiguration);

    Optional<CD4AnalysisSymbolTableCreator> stc = cd4AnalysisLang
        .getSymbolTableCreator(resolverConfiguration, scope);

    if (stc.isPresent()) {
      stc.get().createFromAST(ast);
    }

    return scope;
  }

  /**
   * Retrieve the CDTypeSymbol from a class diagram scope, i.e., address Symbols
   * without qualification. For instance, given classdiagram D { class A; }. To
   * resolve the class A simply call resolve("A") instead of resolve("D.A")
   *
   * @param className the interfaceName of the class
   * @return a list of all visible attributes
   */
  public Optional<CDTypeSymbol> resolve(String className) {
    return this.cdScope.<CDTypeSymbol>resolve(className, CDTypeSymbol.KIND);
  }

  public Optional<CDAssociationSymbol> resolveAssoc(String assocName) {
    return this.cdScope.<CDAssociationSymbol>resolve(assocName, CDAssociationSymbol.KIND);
  }

  public List<CDFieldSymbol> getInheritedVisibleAttributesInHierarchy(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    List<CDFieldSymbol> visibleAttr = new ArrayList<>();

    if (cdType.get().getSuperClass().isPresent()) {
      visibleAttr.addAll(getVisibleNotDerivedAttributesInHierarchy(cdType.get()
          .getSuperClass().get().getName()));
    }

    return visibleAttr;
  }

  public List<CDFieldSymbol> getVisibleNotDerivedAttributesInHierarchy(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    List<CDFieldSymbol> visibleAttr = cdType.get().getFields().stream().filter(field ->
        !field.isDerived() && !field.isFinal() && !field.isStatic()).collect(Collectors.toList());

    if (cdType.get().getSuperClass().isPresent()) {
      visibleAttr.addAll(getVisibleNotDerivedAttributesInHierarchy(cdType.get()
          .getSuperClass().get().getName()));
    }

    return visibleAttr;
  }

  public List<ASTCDAttribute> getVisibleNotDerivedAttributesInHierarchyAsCDAttribute(String className) {
    return getVisibleNotDerivedAttributesInHierarchy(className).stream().map(CDAttributeUtil::fromCDFieldSymbol).collect(Collectors.toList());
  }

  public List<CDFieldSymbol> getVisibleAttributesInHierarchy(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    List<CDFieldSymbol> visibleAttr = cdType.get().getFields().stream().filter(field ->
        !field.isFinal() && !field.isStatic()).collect(Collectors.toList());

    if (cdType.get().getSuperClass().isPresent()) {
      visibleAttr.addAll(getVisibleAttributesInHierarchy(cdType.get()
          .getSuperClass().get().getName()));
    }

    return visibleAttr;
  }

  public List<ASTCDAttribute> getVisibleAttributesInHierarchyAsCDAttribute(String className) {
    return getVisibleAttributesInHierarchy(className).stream().map(CDAttributeUtil::fromCDFieldSymbol).collect(Collectors.toList());
  }

  public List<CDFieldSymbol> getVisibleAttributes(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());

    return cdType.get().getFields().stream().filter(field ->
        !field.isDerived() && !field.isFinal() && !field.isStatic()).collect(Collectors.toList());
  }

  public List<CDFieldSymbol> getDerivedAttributes(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());

    return cdType.get().getFields().stream().filter(field ->
        field.isDerived() && !field.isFinal() && !field.isStatic()).collect(Collectors.toList());
  }

  public List<CDFieldSymbol> getDerivedAttributesInHierarchy(String name) {
    Optional<CDTypeSymbol> cdType = resolve(name);
    checkArgument(cdType.isPresent());
    return getDerivedAttributesInHierarchy(cdType.get());
  }

  public List<CDFieldSymbol> getDerivedAttributesInHierarchy(CDTypeSymbol symbol) {
    List<CDFieldSymbol> visibleAttr = symbol.getFields().stream().filter(field ->
        field.isDerived()).collect(Collectors.toList());

    if (symbol.getSuperClass().isPresent()) {
      visibleAttr.addAll(getDerivedAttributesInHierarchy(symbol.getSuperClass().get()));
    }

    return visibleAttr;
  }

  public List<CDFieldSymbol> getNonVisibleAttributesInHierarchy(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());

    List<CDFieldSymbol> visibleAttr = cdType.get().getFields().stream().filter(field ->
        field.isDerived() && field.isFinal() && field.isStatic() || field.isPrivate())
        .collect(Collectors.toList());

    if (cdType.get().getSuperClass().isPresent()) {
      visibleAttr.addAll(getNonVisibleAttributesInHierarchy(cdType.get().getSuperClass().get().getName()));
    }

    return visibleAttr;
  }

  public List<ASTCDAttribute> getNonVisibleAttributesInHierarchyAsCDAttribute(String className) {
    return getNonVisibleAttributesInHierarchy(className).stream().map(CDAttributeUtil::fromCDFieldSymbol).collect(Collectors.toList());
  }

  public List<ASTCDParameter> getConstructorParameter(ASTCDClass clazz) {
    List<ASTCDParameter> paramList = new ArrayList<>();
    List<CDFieldSymbol> localAttributes = getVisibleAttributes(clazz.getName());
    List<CDAssociationSymbol> localAssociations = CDAssociationUtil.getLocalAssociations(clazz, this);
    List<CDFieldSymbol> superAttributes = getInheritedVisibleAttributesInHierarchy(clazz.getName());
    List<CDAssociationSymbol> superAssociations = CDAssociationUtil.getInheritedAssociations(clazz, this);

    localAttributes.stream().map(GetterSetterHelper::attributeToCDParameter).forEach(paramList::add);
    superAttributes.stream().map(GetterSetterHelper::attributeToCDParameter).forEach(paramList::add);
    localAssociations.stream().map(CDAssociationUtil::associationToCDParameter).forEach(paramList::add);
    superAssociations.stream().map(CDAssociationUtil::associationToCDParameter).forEach(paramList::add);

    return paramList;
  }

  public List<ASTCDParameter> getSuperConstructorParameter(ASTCDClass clazz) {
    List<ASTCDParameter> superArguments = new ArrayList<>();

    List<CDFieldSymbol> superAttributes = getInheritedVisibleAttributesInHierarchy(clazz.getName());
    List<CDAssociationSymbol> superAssociations = CDAssociationUtil.getInheritedAssociations(clazz, this);

    superAttributes.stream().map(GetterSetterHelper::attributeToCDParameter).forEach(superArguments::add);
    superAssociations.stream().map(CDAssociationUtil::associationToCDParameter).forEach(superArguments::add);

    return superArguments;
  }

  public List<CDTypeSymbol> getAllSuperTypes(String className) {
    List<CDTypeSymbol> superTypes = getAllSuperClasses(className);
    superTypes.addAll(getAllSuperInterfaces(className));
    return superTypes;
  }

  public List<CDTypeSymbol> getAllSuperClasses(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());
    return getSuperClassesRecursively(cdType.get(), Lists.newArrayList());
  }

  public List<CDTypeSymbol> getAllSuperInterfaces(String className) {
    Optional<CDTypeSymbol> cdType = resolve(className);
    checkArgument(cdType.isPresent());
    return getAllSuperClasses(className)
        .stream()
        .map(CDTypeSymbol::getInterfaces)
        .flatMap(Collection::stream)
        .flatMap(
            superInterface -> getSuperInterfacesRecursively(superInterface, new ArrayList<>())
                .stream())
        .collect(Collectors.toList());
  }

  private List<CDTypeSymbol> getSuperClassesRecursively(CDTypeSymbol cdType,
                                                        final List<CDTypeSymbol> symbols) {
    checkNotNull(symbols);

    symbols.add(cdType);
    if (cdType.getSuperClass().isPresent()) {
      getSuperClassesRecursively(cdType.getSuperClass().get(), symbols);
    }

    return symbols;
  }

  private List<CDTypeSymbol> getSuperInterfacesRecursively(CDTypeSymbol cdType,
                                                           final List<CDTypeSymbol> symbols) {
    checkNotNull(symbols);

    symbols.add(cdType);
    cdType.getInterfaces().forEach(i -> getSuperInterfacesRecursively(i, symbols));

    return symbols;
  }

  /**
   * Recursively get all subclasses. This means if the interfaceName of a class is passed
   * all subclasses are returned. If the interfaceName of an interface is passed, then
   * all sub interfaces and implementing classes are returned.
   *
   * @param name interfaceName of a class or interface
   * @return list of subclasses & subinterfaces
   */
  // TODO: Test class
  public List<CDTypeSymbol> getSubclasses(String name) {
    return getSubsRecusively(name, Lists.newArrayList()).stream().filter(x -> !x.isEnum())
        .collect(Collectors.toList());
  }

  public List<CDTypeSymbol> getImplementingSubclasses(String name) {
    return getSubsRecusively(name, Lists.newArrayList()).stream().filter(x -> !x.isInterface() && !x.isEnum())
        .collect(Collectors.toList());
  }

  public List<CDTypeSymbol> getImplementingSubclassesWithCurrent(String name) {
    List<CDTypeSymbol> list = getSubsRecusively(name, Lists.newArrayList()).stream().filter(x -> !x.isInterface() && !x.isEnum())
        .collect(Collectors.toList());
    CDTypeSymbol cdType = (CDTypeSymbol) cdScope.resolveLocally(name, CDTypeSymbol.KIND).get();
    if (!cdType.isInterface() && !cdType.isAbstract()) {
      list.add(cdType);
    }
    return list;
  }

  private List<CDTypeSymbol> getSubsRecusively(String name, final List<CDTypeSymbol> lst) {

    // get all sub classes and interfaces
    final List<CDTypeSymbol> concreteSubs = getSubclassesAndInterfaces(name);

    if (concreteSubs.isEmpty()) {
      return Lists.newArrayList();
    }

    for (CDTypeSymbol sym : concreteSubs) {
      if (!lst.contains(sym)) {
        lst.add(sym);
      }
      getSubsRecusively(sym.getName(), lst);
    }

    return lst;
  }

  public Map<ASTCDAttribute, CDAssociationSymbol> getVisibleAssociationsInHierarchy(ASTCDClass clazz) {
    Map<ASTCDAttribute, CDAssociationSymbol> attrMap = Maps.newHashMap();
    for (CDAssociationSymbol assoc: CDAssociationUtil.getAllAssociations(clazz, this)) {
      String attrType = assoc.getTargetType().getName();
      if (CDAssociationUtil.isMultiple(assoc)) {
        attrType = "List<" + attrType + ">";
      } else if (CDAssociationUtil.isOptional(assoc)) {
        attrType = "Optional<" + attrType + ">";
      }
      attrMap.put(new CDAttributeBuilder()
              .type(attrType)
              .Private()
              .setName(CDAssociationUtil.getAssociationName(assoc))
              .build(), assoc);
    }
    return attrMap;
  }

  public List<CDAssociationSymbol> getSuperClassAssociationsInHierarchy(String className) {
    List<CDTypeSymbol> types = getAllSuperClasses(className);
    List<CDAssociationSymbol> allAssociations = Lists.newArrayList();

    for (CDTypeSymbol type : types) {
      for (CDAssociationSymbol symbol : type.getAssociations()) {
        if (!allAssociations.contains(symbol)) {
          allAssociations.add(symbol);
        }
      }
    }

    return allAssociations;
  }

  public List<CDAssociationSymbol> getSuperInterfacesAssocsiationsInHierarchy(String className) {
    List<CDTypeSymbol> types = getAllSuperInterfaces(className);
    List<CDAssociationSymbol> allAssociations = Lists.newArrayList();

    for (CDTypeSymbol type : types) {
      for (CDAssociationSymbol symbol : type.getAssociations()) {
        if (!allAssociations.contains(symbol)) {
          allAssociations.add(symbol);
        }
      }
    }

    return allAssociations;
  }

  public boolean isTypeDefinedInModel(CDTypeSymbol type) {
    return !(type.getEnclosingScope() instanceof GlobalScope);
  }

  public boolean isTypeDefinedInModel(String typeName) {
    Optional<CDTypeSymbol> type = this.cdScope.resolve(typeName, CDTypeSymbol.KIND);
    return type.isPresent() && isTypeDefinedInModel(type.get());
  }

  public Optional<CDTypeSymbol> getTypeSymbolIfDefinedInModel(String typeName) {
    Optional<CDTypeSymbol> type = this.cdScope.resolve(typeName, CDTypeSymbol.KIND);
    if (type.isPresent() && isTypeDefinedInModel(type.get())) {
      return type;
    }
    return Optional.empty();
  }

  private List<CDTypeSymbol> getSubclassesAndInterfaces(String name) {
    List<CDTypeSymbol> cdTypes = Lists.newArrayList();

    for (Symbol symbol : this.cdScope.resolveLocally(CDTypeSymbol.KIND)) {
      CDTypeSymbol cdType = (CDTypeSymbol) symbol;

      // check if the given class is a super class
      if (cdType.getSuperClass().isPresent()
          && cdType.getSuperClass().get().getName().equals(name)) {
        cdTypes.add(cdType);
      }

      // check if the given class is an interface
      if (!cdType.getInterfaces().isEmpty()) {
        for (CDTypeSymbol a : cdType.getInterfaces()) {
          if (a.getName().equals(name)) {
            cdTypes.add(cdType);
          }
        }
      }
    }
    return cdTypes;
  }

  public List<CDAssociationSymbol> getRelevantAssociationsForClass(String className) {
    CDTypeSymbol symbol = resolve(className).get();
    List<CDAssociationSymbol> list = Lists.newArrayList();
    list.addAll(symbol.getAssociations());
    if (!symbol.getSuperClass().isPresent()) {
      List<CDTypeSymbol> types = getAllSuperInterfaces(className);
      for (CDTypeSymbol type : types) {
        list.addAll(type.getAssociations());
      }
    }
    return list;
  }

  public List<CDAssociationSymbol> getAllAssociationsForType(String className) {
    return getAllSuperTypes(className).stream().map(CDTypeSymbol::getAssociations).flatMap(Collection::stream)
        .distinct().collect(Collectors.toList());
  }

  public List<CDAssociationSymbol> getAllNonDerivedAssociationsForClass(String className) {
    return getAllSuperTypes(className).stream().map(CDTypeSymbol::getAssociations).flatMap(Collection::stream)
        .distinct().filter(x -> !x.isDerived()).collect(Collectors.toList());
  }

  public List<CDAssociationSymbol> getInheritedAssociations(ASTCDClass clazz) {
    List<CDAssociationSymbol> localAssociations = getLocalAssociations(clazz);
    return getAllAssociationsForType(clazz.getName()).stream()
        .filter(association -> !localAssociations.contains(association)).collect(Collectors.toList());
  }

  public List<CDAssociationSymbol> getInheritedMandatoryAssociations(ASTCDClass clazz) {
    return getInheritedAssociations(clazz).stream().filter(
        association -> (CDAssociationUtil.isOneToMany(association) || CDAssociationUtil.isOne(association))
            && !CDAssociationUtil.isQualified(association) && !CDAssociationUtil.isOppositeQualified(association)
            && !CDAssociationUtil.isDerived(association))
        .collect(Collectors.toList());
  }

  public List<CDAssociationSymbol> getLocalAssociations(ASTCDClass clazz) {
    return resolve(clazz.getName())
        .map(CDTypeSymbol::getAssociations)
        .map(Collection::stream)
        .orElse(Stream.empty())
        .collect(Collectors.toList());
  }

  public List<CDAssociationSymbol> getLocalAssociations(CDTypeSymbol type) {
    return resolve(type.getName())
        .map(CDTypeSymbol::getAssociations)
        .map(Collection::stream)
        .orElse(Stream.empty())
        .collect(Collectors.toList());
  }

  public List<CDAssociationSymbol> getLocalMandatoryAssociations(ASTCDClass clazz) {
    return getLocalAssociations(clazz).stream().filter(association ->
        (CDAssociationUtil.isOneToMany(association) || CDAssociationUtil.isOne(association))
            && !CDAssociationUtil.isQualified(association) && !CDAssociationUtil.isOppositeQualified(association)
            && !CDAssociationUtil.isDerived(association))
        .collect(Collectors.toList());
  }

  public List<CDFieldSymbol> getInheritedAttributes(ASTCDClass clazz) {
    List<CDFieldSymbol> localAttributes = getVisibleAttributes(clazz.getName());
    List<CDFieldSymbol> inheritedAttributes = getVisibleNotDerivedAttributesInHierarchy(clazz.getName());
    inheritedAttributes.removeAll(localAttributes);
    return inheritedAttributes;
  }

  /**
   * Adds the symbol for the type with the given qualified or simple interfaceName to the
   * global scope
   *
   * @param typeName - type's interfaceName
   */
  public void defineType(String typeName) {
    String simpleName = Names.getSimpleName(typeName);
    if (!resolve(simpleName).isPresent()) {
      CommonSymbol type = new CDTypeSymbol(simpleName);
      type.setPackageName(Names.getQualifier(typeName));
      this.globalScope.add(type);
    }
  }

  public List<CDAssociationSymbol> getAllMandatoryAssociations(ASTCDClass clazz) {
    return Stream.concat(getLocalMandatoryAssociations(clazz).stream(),
        getInheritedMandatoryAssociations(clazz).stream()).collect(Collectors.toList());
  }

  public List<CDAssociationSymbol> getAllNonMandatoryAssociations(ASTCDClass clazz) {
    return Stream.concat(getLocalAssociations(clazz).stream(),
        getInheritedAssociations(clazz).stream())
        .filter(x -> !CDAssociationUtil.isOne(x) && !CDAssociationUtil.isOneToMany(x)).collect(Collectors.toList());
  }

  public boolean isEnum(String realType) {
    Optional<CDTypeSymbol> cd = resolve(realType);
    if (cd.isPresent()) {
      return cd.get().isEnum();
    }
    return false;
  }
}
