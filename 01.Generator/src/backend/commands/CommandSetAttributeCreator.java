/* (c) https://github.com/MontiCore/monticore */
package backend.commands;

import backend.common.CoreTemplate;
import backend.coretemplates.association.AssociationNameUtil;
import com.google.common.collect.Lists;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class CommandSetAttributeCreator extends CommandCreator {

  public CommandSetAttributeCreator() {
    super("");
  }

  protected List<ASTCDClass> getOrCreateExtendedClassList(ASTCDClass domainClass,
                                                          CDTypeSymbol typeSymbol) {
    String typeName = typeSymbol.getName();
    List<ASTCDClass> classList = Lists.newArrayList();

    List<ASTCDAttribute> attrList = symbolTable.get().getVisibleNotDerivedAttributesInHierarchyAsCDAttribute(domainClass.getName());
    Map<ASTCDAttribute, CDAssociationSymbol> assocAttrList = symbolTable.get().getVisibleAssociationsInHierarchy(domainClass);
    attrList.addAll(assocAttrList.keySet());

    for (ASTCDAttribute attr : attrList) {
      ASTCDClass addedClass = new CDClassBuilder().Public().superclass(COMMAND)
              .subpackage(TransformationUtils.COMMANDS_PACKAGE)
              .setName(typeName + TransformationUtils.SET_CMD + TransformationUtils.capitalize(attr.getName())).build();
      getOutputAst().getCDClassList().add(addedClass);
      classList.add(addedClass);

      String setterName;
      CDAssociationSymbol assoc = assocAttrList.get(attr);
      if (assoc != null) {
        setterName = AssociationNameUtil.getSetAllMethodName(assoc).orElse(GetterSetterHelper.getPlainSetter(attr));
      } else {
        setterName = GetterSetterHelper.getPlainSetter(attr);
      }
      addImport(addedClass, attr);
      createDoRunMethod(addedClass, domainClass, attr, setterName);

      addedClass.getCDAttributeList()
          .addAll(createAttributes(addedClass, domainClass, typeSymbol, attr));
      addedClass.getCDConstructorList()
          .addAll(createConstructors(addedClass, domainClass, typeSymbol, attr));
      addedClass.getCDMethodList().addAll(createMethods(addedClass, domainClass, typeSymbol, attr));
    }

    return classList;
  }

  private void addImport(ASTCDClass clazz, ASTCDAttribute attr) {
    String type = attr.printType();
    TypeHelper helper = new TypeHelper();
    if (helper.isGenericList(type)) {
      type = helper.getFirstTypeArgumentOfList(type);
    } else if (helper.isGenericOptional(type)) {
      type = helper.getFirstTypeArgumentOfOptional(type);
    }
    Optional<CDTypeSymbol> typeSymbol = symbolTable.get().getTypeSymbolIfDefinedInModel(type);
    if (typeSymbol.isPresent()) {
      TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
              TransformationUtils.getQualifiedNameForDomainOrDtoType(typeSymbol.get().getPackageName(),
                      type, TransformationUtils.isDomainType(typeSymbol.get())));
    }

  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = super.getImports(typeSymbol);

    String p = Joiners.DOT.join(typeSymbol.getPackageName(), TransformationUtils.DTOS_PACKAGE,
            typeSymbol.getName() + "DTOLoader");
    imports.add(p);
    return imports;
  }

  protected List<ASTCDAttribute> createAttributes(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol, ASTCDAttribute attr) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    ASTCDAttribute identifier = new CDAttributeBuilder().Protected().type("Long").setName("objectId")
        .build();
    attributes.add(identifier);

    attr = new CDAttributeBuilder().Protected().type(new TypeHelper().getClassType(attr.printType())).setName(attr.getName())
        .build();
    attributes.add(attr);

    return attributes;
  }

  protected List<ASTCDConstructor> createConstructors(ASTCDClass clazz, ASTCDClass domainClass,
                                                      CDTypeSymbol typeSymbol, ASTCDAttribute attr) {
    List<ASTCDConstructor> constructors = Lists.newArrayList();

    // add empty constructor for deserializing
    ASTCDConstructor emptyConstructor = new CDConstructorBuilder().Package().Protected().setName(clazz.getName()).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), emptyConstructor,
        new StringHookPoint("{this.typeName = \"" + TransformationUtils.removeTOPExtensionIfExistent(clazz.getName()) + "\";}"));

    constructors.add(emptyConstructor);
    constructors.add(createFullConstructor(clazz, domainClass, typeSymbol, attr));
    return constructors;
  }

  protected ASTCDConstructor createFullConstructor(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol, ASTCDAttribute attr) {
    List<ASTCDAttribute> attributes = createAttributes(extendedClass, domainClass, typeSymbol, attr);

    List<ASTCDParameter> paramList = new ArrayList<>();
    attributes.forEach(a -> paramList.add(
        CD4AnalysisMill.cDParameterBuilder().setName(TransformationUtils.uncapitalize(a.getName()))
            .setType(new CDSimpleReferenceBuilder().name(TypesPrinter.printType(a.getType())).build()).build()));

    ASTCDConstructor fullConstructor = new CDConstructorBuilder().Public().setName(extendedClass.getName())
        .setCDParameterList(paramList).build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), fullConstructor,
        new TemplateHookPoint("backend.commands.FullConstructor", attributes));

    return fullConstructor;
  }

  /**
   * @see common.ExtendTrafo#createMethods(ASTCDClass,
   * ASTCDClass,
   * CDTypeSymbol)
   */
  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass, ASTCDClass domainClass,
                                            CDTypeSymbol typeSymbol) {
    return new ArrayList<>();
  }

  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass, ASTCDClass domainClass,
                                            CDTypeSymbol typeSymbol, ASTCDAttribute attr) {
    List<ASTCDMethod> methodList = Lists.newArrayList();

    List<ASTCDAttribute> attrs = createAttributes(extendedClass, domainClass, typeSymbol, attr);
    for (ASTCDAttribute a : attrs) {
      methodList.add(createGetMethod(extendedClass.getName(), a));
    }

    return methodList;
  }

  @Override
  protected void setTemplate(ASTCDMethod method, ASTCDClass domainClass, String className) {
    // Do nothing
  }

  private ASTCDMethod createDoRunMethod(ASTCDClass clazz, ASTCDClass domainClass, ASTCDAttribute attr, String setterName) {
    ASTCDMethod method = new CDMethodBuilder().Public().name("doRun")
        .addParameter("SecurityHelper", "securityHelper")
        .addParameter("DAOLib", "daoLib")
        .returnType("DTO").build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.commands.Set", symbolTable.get(), domainClass.getName(), attr,
            clazz.getName(), setterName));
    clazz.addCDMethod(method);
    return method;
  }

  /**
   * @see common.ExtendTrafo#extendDomainClass(de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass)
   */
  @Override
  protected void extendDomainClass(ASTCDClass domainClass) {
    CDTypeSymbol typeSymbol = symbolTable.get().resolve(domainClass.getName()).get();
    for (ASTCDClass extendedClass : getOrCreateExtendedClassList(domainClass, typeSymbol)) {
      addImports(extendedClass, domainClass, typeSymbol);
      extendedClass.getInterfaceList().addAll(createInterfaces(domainClass, typeSymbol));
      getSuperclass(domainClass, typeSymbol).ifPresent(extendedClass::setSuperclass);
      /*extendedClass.getCDAttributeList()
              .addAll(createAttributes(extendedClass, domainClass, typeSymbol));*/
      extendedClass.getCDAttributeList()
              .addAll(createStaticAttributes(extendedClass, domainClass, typeSymbol));
      replaceAttributes(extendedClass, domainClass, typeSymbol);
      /*extendedClass.getCDConstructorList()
              .addAll(createConstructors(extendedClass, domainClass, typeSymbol));*/
      /*extendedClass.getCDMethodList().addAll(createMethods(extendedClass, domainClass, typeSymbol));*/
      extendedClass.getCDMethodList()
              .addAll(createStaticMethods(extendedClass, domainClass, typeSymbol));
      replaceMethods(extendedClass, domainClass, typeSymbol);
      // Add annotations to class
      addTypeAnnotations(extendedClass);
    }
  }

}
