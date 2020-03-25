/* (c) https://github.com/MontiCore/monticore */

package common;

import com.google.common.collect.Lists;
import common.util.CDSimpleReferenceBuilder;
import common.util.CompilationUnit;
import common.util.TransformationUtils;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The common transformation to extend data classes
 *
 */
public abstract class ExtendTrafo extends DexTransformation {

  @Override
  protected void transform() {
    checkArgument(symbolTable.isPresent());
    // Extend existing domain classes
    for (ASTCDClass domainClass : getAst().getCDClassList()) {
      extendDomainClass(domainClass);
    }
    // Extend existing domain classes
    for (ASTCDInterface domainInterface : getAst().getCDInterfaceList()) {
      extendDomainInterface(domainInterface);
    }
    // Extend existing domain classes
    for (ASTCDEnum domainEnum : getAst().getCDEnumList()) {
      extendDomainEnum(domainEnum, Optional.empty());
    }
  }

  // Extends the domain class by the additional class, additional interface,
  // extended interfaces, implemented constructors, methods and attributes
  protected void extendDomainClass(ASTCDClass domainClass) {
    CDTypeSymbol typeSymbol = symbolTable.get().resolve(domainClass.getName()).get();
    Optional<ASTCDClass> clazz = getOrCreateExtendedClass(domainClass, typeSymbol);
    if (clazz.isPresent()) {
      ASTCDClass extendedClass = clazz.get();
      addImports(extendedClass, domainClass, typeSymbol);
      extendedClass.getInterfaceList().addAll(createClassInterfaces(domainClass, typeSymbol));
      getSuperclass(domainClass, typeSymbol).ifPresent(extendedClass::setSuperclass);
      extendedClass.getCDAttributeList()
          .addAll(createAttributes(extendedClass, domainClass, typeSymbol));
      extendedClass.getCDAttributeList()
          .addAll(createStaticAttributes(extendedClass, domainClass, typeSymbol));
      replaceAttributes(extendedClass, domainClass, typeSymbol);
      extendedClass.getCDConstructorList()
          .addAll(createConstructors(extendedClass, domainClass, typeSymbol));
      extendedClass.getCDMethodList().addAll(createMethods(extendedClass, domainClass, typeSymbol));
      extendedClass.getCDMethodList()
          .addAll(createStaticMethods(extendedClass, domainClass, typeSymbol));
      replaceMethods(extendedClass, domainClass, typeSymbol);
      // Add annotations to class
      addTypeAnnotations(extendedClass);
    }

    Optional<ASTCDInterface> interf = getOrCreateExtendedInterface(domainClass, typeSymbol);
    if (interf.isPresent()) {
      ASTCDInterface extendedInterface = interf.get();
      addImports(extendedInterface, domainClass, typeSymbol);
      extendedInterface.getInterfaceList().addAll(createInterfaceInterfaces(domainClass, typeSymbol));
      extendedInterface.getCDAttributeList()
          .addAll(createStaticAttributes(extendedInterface, domainClass, typeSymbol));
      replaceAttributes(extendedInterface, domainClass, typeSymbol);
      extendedInterface.getCDMethodList()
          .addAll(createMethods(extendedInterface, domainClass, typeSymbol));
      addTypeAnnotations(extendedInterface);
    }
  }

  // Extends the domain interface by the additional interface, implemented methods and static attributes
  protected void extendDomainInterface(ASTCDInterface domainInterface) {
    CDTypeSymbol typeSymbol = symbolTable.get().resolve(domainInterface.getName()).get();
    Optional<ASTCDInterface> interf = getOrCreateExtendedInterface(domainInterface, typeSymbol);
    if (interf.isPresent()) {
      ASTCDInterface extendedInterface = interf.get();
      addImports(extendedInterface, domainInterface, typeSymbol);
      extendedInterface.getInterfaceList().addAll(createInterfaceInterfaces(domainInterface, typeSymbol));
      extendedInterface.getCDAttributeList()
          .addAll(createStaticAttributes(extendedInterface, domainInterface, typeSymbol));
      extendedInterface.getCDMethodList()
          .addAll(createMethods(extendedInterface, domainInterface, typeSymbol));
      replaceMethods(extendedInterface, domainInterface, typeSymbol);
      addTypeAnnotations(extendedInterface);
    }
  }

  // Extends the domain enum by the additional enum, implemented interfaces and methods
  protected void extendDomainEnum(ASTCDEnum domainEnum, Optional<CDTypeSymbol> symbol) {
    CDTypeSymbol typeSymbol = symbol.isPresent() ?
        symbol.get() :
        symbolTable.get().resolve(domainEnum.getName()).get();
    Optional<ASTCDEnum> enumeration = getOrCreateExtendedEnum(domainEnum, typeSymbol);
    if (enumeration.isPresent()) {
      ASTCDEnum extendedEnum = enumeration.get();
      addImports(extendedEnum, domainEnum, typeSymbol);
      extendedEnum.getCDConstructorList().addAll(createConstructors(extendedEnum, typeSymbol));
      extendedEnum.getCDAttributeList()
          .addAll(createAttributes(extendedEnum, domainEnum, typeSymbol));
      extendedEnum.getInterfaceList().addAll(createEnumInterfaces(domainEnum, typeSymbol));
      extendedEnum.getCDMethodList()
          .addAll(createMethods(extendedEnum, domainEnum, typeSymbol));
      replaceMethods(extendedEnum, domainEnum, typeSymbol);
      extendedEnum.getCDEnumConstantList().addAll(createConstants(domainEnum, typeSymbol));
      addTypeAnnotations(extendedEnum);
    }
  }

  // This trafo gets or creates the class to be extended
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Optional.of(domainClass);
  }

  // This trafo gets or creates the interface to be extended
  protected Optional<ASTCDInterface> getOrCreateExtendedInterface(ASTCDInterface domainInterface,
      CDTypeSymbol typeSymbol) {
    return Optional.of(domainInterface);
  }

  // This trafo gets or creates the enum to be extended
  protected Optional<ASTCDEnum> getOrCreateExtendedEnum(ASTCDEnum domainEnum,
      CDTypeSymbol typeSymbol) {
    return Optional.of(domainEnum);
  }

  // This trafo extends the existing domain class by an interface
  protected Optional<ASTCDInterface> getOrCreateExtendedInterface(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Optional.empty();
  }

  //----------- IMPORTS -------------------------------------

  protected void addImports(ASTCDInterface extendedInterface, ASTCDInterface domainInterface,
      CDTypeSymbol typeSymbol) {
    checkArgument(symbolTable.isPresent());

    extendedInterface.getInterfaceList().forEach(interf -> TransformationUtils
        .addImportForCDType(extendedInterface, interf, getAstRoot(), symbolTable.get()));

    // Add imports for all associations
    symbolTable.get()
        .getAllAssociationsForType(typeSymbol.getName()).forEach(assoc -> TransformationUtils
        .addStarImportForCDType(extendedInterface, assoc.getTargetType().getName(), getAstRoot()));

    domainInterface.getCDAttributeList().forEach(attr -> TransformationUtils
        .addImportForCDType(extendedInterface, attr.getType(), getAstRoot(), symbolTable.get()));

    getImports(typeSymbol).forEach(i -> TransformationUtils
        .addPropertyValue(extendedInterface, CompilationUnit.IMPORTS_PROPERTY, i));
  }

  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    checkArgument(symbolTable.isPresent());

    if (typeSymbol.getSuperClass().isPresent()) {
      String superClassName = typeSymbol.getSuperClass().get().getName();
      TransformationUtils
          .addStarImportForCDType(extendedClass, superClassName,
              getAstRoot());
    }

    domainClass.getInterfaceList().forEach(interf -> TransformationUtils
        .addImportForCDType(extendedClass, interf, getAstRoot(), symbolTable.get()));

    // Add imports for all associations
    symbolTable.get()
        .getAllAssociationsForType(typeSymbol.getName()).forEach(assoc -> TransformationUtils
        .addStarImportForCDType(extendedClass, assoc.getTargetType().getName(), getAstRoot()));

    typeSymbol.getAllVisibleFields().forEach(attr -> TransformationUtils
        .addImportForCDType(extendedClass, attr.getType(), getAstRoot(), symbolTable.get()));

    getImports(typeSymbol).forEach(i -> TransformationUtils
        .addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, i));

    // Add general imports
    getGeneralImpoerts().forEach(i -> TransformationUtils
        .addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, i));

  }

  private List<String> getGeneralImpoerts() {
    return Collections.singletonList("de.montigem.be.error.DataConsistencyException");
  }

  protected void addImports(ASTCDEnum extendedEnum, ASTCDEnum domainEnum, CDTypeSymbol typeSymbol) {
    domainEnum.getInterfaceList().forEach(interf -> TransformationUtils
        .addImportForCDType(extendedEnum, interf, getAstRoot(), symbolTable.get()));

    getImports(typeSymbol).forEach(i -> TransformationUtils
        .addPropertyValue(extendedEnum, CompilationUnit.IMPORTS_PROPERTY, i));
  }

  protected void addImports(ASTCDInterface extendedInterface, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    getImports(typeSymbol).forEach(i -> TransformationUtils
        .addPropertyValue(extendedInterface, CompilationUnit.IMPORTS_PROPERTY, i));
  }

  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  //----------- INTERFACES -------------------------------------

  protected List<ASTSimpleReferenceType> createClassInterfaces(ASTCDType extendedType,
      CDTypeSymbol typeSymbol) {
    List<ASTSimpleReferenceType> interfaces = new ArrayList<>();
    getClassInterfaceNames(typeSymbol)
        .forEach(i -> interfaces.add(new CDSimpleReferenceBuilder().name(i).build()));
    return interfaces;
  }

  protected List<ASTSimpleReferenceType> createInterfaceInterfaces(ASTCDType extendedType,
      CDTypeSymbol typeSymbol) {
    List<ASTSimpleReferenceType> interfaces = new ArrayList<>();
    getInterfaceInterfaceNames(typeSymbol)
        .forEach(i -> interfaces.add(new CDSimpleReferenceBuilder().name(i).build()));
    return interfaces;
  }

  protected List<ASTSimpleReferenceType> createEnumInterfaces(ASTCDType extendedType,
      CDTypeSymbol typeSymbol) {
    List<ASTSimpleReferenceType> interfaces = new ArrayList<>();
    getEnumInterfaceNames(typeSymbol)
        .forEach(i -> interfaces.add(new CDSimpleReferenceBuilder().name(i).build()));
    return interfaces;
  }

  protected List<String> getClassInterfaceNames(CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected List<String> getInterfaceInterfaceNames(CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected List<String> getEnumInterfaceNames(CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected Optional<ASTReferenceType> getSuperclass(
      ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    if (getSuperclassName(domainClass, typeSymbol).isPresent()) {
      return Optional.of(new CDSimpleReferenceBuilder().name(getSuperclassName(domainClass, typeSymbol).get()).build());
    }
    else {
      return Optional.empty();
    }
  }

  protected Optional<String> getSuperclassName(
      ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    return Optional.empty();
  }

  //----------- ATTRIBUTES -------------------------------------

  protected List<ASTCDAttribute> createAttributes(ASTCDClass extendedClass, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected List<ASTCDAttribute> createStaticAttributes(ASTCDClass extendedClass,
      ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected List<ASTCDAttribute> createStaticAttributes(ASTCDInterface extendedInterface,
      ASTCDInterface domainInterface,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected List<ASTCDAttribute> createStaticAttributes(ASTCDInterface extendedInterface,
      ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected void replaceAttributes(ASTCDClass extendedClass, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
  }

  protected void replaceAttributes(ASTCDInterface extendedInterf, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
  }

  //----------- METHODS -------------------------------------

  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected List<ASTCDMethod> createMethods(ASTCDInterface extendedInterface,
      ASTCDInterface domainInterface,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected List<ASTCDMethod> createMethods(ASTCDInterface extendedInterface,
      ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected Collection<ASTCDMethod> createMethods(ASTCDEnum extendedEnum,
      ASTCDEnum domainEnum, CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected Collection<ASTCDAttribute> createAttributes(ASTCDEnum extendedEnum,
      ASTCDEnum domainEnum, CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected List<ASTCDMethod> createStaticMethods(ASTCDClass extendedClass, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected void replaceMethods(ASTCDClass extendedClass, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
  }

  protected void replaceMethods(ASTCDInterface extendedInterface, ASTCDInterface domainInterface,
      CDTypeSymbol typeSymbol) {
  }

  protected void replaceMethods(ASTCDEnum extendedEnum, ASTCDEnum domainEnum,
      CDTypeSymbol typeSymbol) {
  }

  //----------- Annotations -------------------------------------

  protected void addTypeAnnotations(ASTCDClass clazz) {
    Optional<ASTCDStereotype> stereotype = getStereotype(clazz);
    if (!stereotype.isPresent()) {
      return;
    }
    if (!clazz.getModifierOpt().isPresent()) {
      clazz.setModifier(CD4AnalysisMill.modifierBuilder().build());
    }
    clazz.getModifier().setStereotype(stereotype.get());
  }

  /**
   * Add specific annotations to the type
   *
   * @param interf type for transformation
   */
  protected void addTypeAnnotations(ASTCDInterface interf) {
    Optional<ASTCDStereotype> stereotype = getStereotype(interf);
    if (!stereotype.isPresent()) {
      return;
    }
    if (!interf.getModifierOpt().isPresent()) {
      interf.setModifier(CD4AnalysisMill.modifierBuilder().build());
    }
    interf.getModifier().setStereotype(stereotype.get());
  }

  /**
   * Add specific annotations to the type
   *
   * @param enumeration type for transformation
   */
  protected void addTypeAnnotations(ASTCDEnum enumeration) {
    Optional<ASTCDStereotype> stereotype = getStereotype(enumeration);
    if (!stereotype.isPresent()) {
      return;
    }
    if (!enumeration.getModifierOpt().isPresent()) {
      enumeration.setModifier(CD4AnalysisMill.modifierBuilder().build());
    }
    enumeration.getModifier().setStereotype(stereotype.get());
  }

  protected Optional<ASTCDStereotype> getStereotype(ASTCDType type) {
    return Optional.empty();
  }

  //----------- Constructors -------------------------------------

  protected List<ASTCDConstructor> createConstructors(ASTCDClass extendedClass,
      ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  protected List<ASTCDConstructor> createConstructors(ASTCDEnum extendedEnum,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  //----------- Enum Constants -------------------------------------

  protected List<ASTCDEnumConstant> createConstants(ASTCDEnum domainEnum,
      CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

}
