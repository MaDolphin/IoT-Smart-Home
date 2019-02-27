/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package backend.data.persistence.dao;

import backend.common.CoreTemplate;
import backend.coretemplates.association.AssociationNameUtil;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;
import static common.util.TransformationUtils.setProperty;

public class DaoCreator extends CreateTrafo {

  public static final String DAO = "DAO";

  public DaoCreator() {
    super(DAO);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass builderClass, ASTCDClass clazz,
                                                      CDTypeSymbol type) {
    return Lists
            .newArrayList(new CDConstructorBuilder().Public().setName(builderClass.getName()).build());
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass daoClass, ASTCDClass clazz,
                                            CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    methods.add(getGetDomainClassMethod(typeSymbol, daoClass));
    methods.addAll(getGetSetMethods(typeSymbol, clazz));
    methods.add(getLoadEagerMethod(typeSymbol, clazz));
    return methods;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("javax.ejb.Lock");
    imports.add("javax.ejb.LockType");
    imports.add("javax.ejb.Stateless");
    imports.add("de.montigem.be.domain.rte.dao.AbstractDomainDAO");
    imports.add("javax.persistence.EntityNotFoundException");
    imports.add("javax.transaction.Transactional");
    imports.add("de.montigem.be.util.DAOLib");
    imports.add("org.hibernate.Hibernate");
    return imports;
  }

  @Override
  // This trafo creates an additional class DAO for the existing domain class
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
                                                          CDTypeSymbol typeSymbol) {
    String domainClassPackage = TransformationUtils.getPackageName(getOutputAstRoot()) + "."
            + TransformationUtils.CLASSES_PACKAGE + "." +
            domainClass.getName().toLowerCase() + ".*";

    String className = typeSymbol.getName() + suffix;
    CDClassBuilder clazzBuilder = new CDClassBuilder().Public()
            .subpackage(TransformationUtils.DAO_PACKAGE).additionalImport(domainClassPackage)
            .superclass("AbstractDomainDAO<" + domainClass.getName() + ">");

    // Add annotations if a handwritten class doesn't exist
    if (!generateTOP || !TransformationUtils
            .existsHandwrittenFile(className, TransformationUtils.DAO_PACKAGE, handcodePath.get(),
                    TransformationUtils.JAVA_FILE_EXTENSION)) {
      ASTCDStereotype type = CD4AnalysisMill.cDStereotypeBuilder().build();
      type.getValueList().add(CD4AnalysisMill.cDStereoValueBuilder().setName("@Stateless").build());
      type.getValueList()
              .add(CD4AnalysisMill.cDStereoValueBuilder().setName("@Lock(LockType.READ)").build());
      clazzBuilder.setModifier(
              CD4AnalysisMill.modifierBuilder().setPublic(true).setStereotype(type).build());
    }

    ASTCDClass addedClass = clazzBuilder.setName(className).build();
    addedClass.setSymbol(typeSymbol);
    setProperty(addedClass, SUBPACKAGE_PROPERTY, TransformationUtils.DAO_PACKAGE);
    getOutputAst().getCDClassList().add(addedClass);

    return Optional.of(addedClass);
  }

  // ----------- PRIVATE METHODS -------------------------------------

  private ASTCDMethod getGetDomainClassMethod(CDTypeSymbol typeSymbol, ASTCDClass daoClass) {
    ASTCDMethod method = new CDMethodBuilder().Public()
            .returnType("Class<" + typeSymbol.getName() + ">")
            .name("getDomainClass")
            .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("backend.coretemplates.GetDomainClass", typeSymbol));
    return method;
  }

  private Collection<ASTCDMethod> getGetSetMethods(CDTypeSymbol typeSymbol,
                                                   ASTCDClass domainClass) {
    List<ASTCDMethod> methodList = Lists.newArrayList();

    // Attributes
    for (CDFieldSymbol attr : typeSymbol.getAllVisibleFields()) {
      if (!attr.isDerived()) {
        String methodName = GetterSetterHelper.getPlainSetter(attr);

        ASTCDStereotype type = CD4AnalysisMill.cDStereotypeBuilder().build();
        type.getValueList().add(CD4AnalysisMill.cDStereoValueBuilder().setName("@Transactional").setValue(TransformationUtils.METHOD_ANNOTATION).build());

        ASTCDMethod method = new CDMethodBuilder()
                .modifier(CD4AnalysisMill.modifierBuilder().setStereotype(type).build())
                .Public()
                .addParameter("long", "id")
                .addParameter(attr.getType().getStringRepresentation(), attr.getName())
                .name(methodName)
                .exceptions("EntityNotFoundException")
                .build();

        getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
                new TemplateHookPoint("backend.data.persistence.dao.Set", typeSymbol.getName(),
                        methodName, attr.getName()));
        methodList.add(method);
      }
    }

    //Associations
    for (CDAssociationSymbol assoc : typeSymbol.getAllAssociations()) {
      if (!assoc.isDerived()) {
        String methodName = "set" + TransformationUtils.capitalize(assoc.getDerivedName());
        String paramType = assoc.getTargetType().getStringRepresentation();
        if (CDAssociationUtil.isMultiple(assoc)) {
          methodName = AssociationNameUtil.getSetAllMethodName(assoc).orElse("");
          paramType = "List<" + paramType + ">";
        }
        if (CDAssociationUtil.isOptional(assoc)) {
          paramType = "Optional<" + paramType + ">";
        }

        ASTCDStereotype type = CD4AnalysisMill.cDStereotypeBuilder().build();
        type.getValueList().add(CD4AnalysisMill.cDStereoValueBuilder().setName("@Transactional").setValue(TransformationUtils.METHOD_ANNOTATION).build());

        ASTCDMethod method = new CDMethodBuilder()
                .modifier(CD4AnalysisMill.modifierBuilder().setStereotype(type).build())
                .Public()
                .addParameter("long", "id")
                .addParameter(paramType, "obj")
                .name(methodName)
                .exceptions("EntityNotFoundException")
                .build();

        getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
                new TemplateHookPoint("backend.data.persistence.dao.Set", typeSymbol.getName(),
                        methodName, "obj"));
        methodList.add(method);
      }
    }
    return methodList;
  }

  private ASTCDMethod getLoadEagerMethod(CDTypeSymbol typeSymbol,
                                         ASTCDClass domainClass) {

    ASTCDStereotype type = CD4AnalysisMill.cDStereotypeBuilder().build();
    type.getValueList().add(CD4AnalysisMill.cDStereoValueBuilder().setName("@Transactional").setValue(TransformationUtils.METHOD_ANNOTATION).build());
    ASTCDMethod method = new CDMethodBuilder()
            .modifier(CD4AnalysisMill.modifierBuilder().setStereotype(type).build())
            .Public()
            .addParameter(typeSymbol.getName(), TransformationUtils.uncapitalize(typeSymbol.getName()))
            .addParameter("DAOLib", "daoLib")
            .addParameter("String", "resource")
            .returnType(typeSymbol.getName())
            .name("loadEager")
            .build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("backend.data.persistence.dao.LoadEager",
                    TransformationUtils.uncapitalize(typeSymbol.getName()), typeSymbol.getAllAssociations()));
    return method;

  }

}
