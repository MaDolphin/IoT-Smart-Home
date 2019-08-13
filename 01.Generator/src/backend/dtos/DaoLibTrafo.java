/* (c) https://github.com/MontiCore/monticore */
package backend.dtos;

import backend.common.CoreTemplate;
import common.util.CDAttributeBuilder;
import common.util.CDMethodBuilder;
import common.util.CompilationUnit;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.se_rwth.commons.Joiners;

import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;
import static common.util.TransformationUtils.setProperty;

public class DaoLibTrafo extends AggregateTrafo {
  
  private static final String DAOLIB = "DAOLib";
  
  private static final String DAOPACKAGE = TransformationUtils.MONTIGEM_BASE + "." + TransformationUtils.UTIL_PACKAGE;
  
  /**
   * Constructor for backend.dtos.DaoLibTrafo.
   */
  public DaoLibTrafo() {
    super(DAOPACKAGE, DAOLIB);
  }
  
  @Override
  protected ASTCDClass createClass(String packageName, String className) {
    ASTCDClass clazz = super.createClass(packageName, className);
    setProperty(clazz, SUBPACKAGE_PROPERTY, TransformationUtils.UTIL_PACKAGE);
    TransformationUtils.addAnnos(clazz.getModifier(), "@Stateless");
    return clazz;
  }
  
  @Override
  protected void createImports(ASTCDClass clazz) {
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
        "javax.inject.Inject");
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
        "javax.ejb.Stateless");
  }
  
  @Override
  protected void createDomainImports(ASTCDClass clazz, ASTCDClass domainClass, String packageName) {
    String name = domainClass.getName() + TransformationUtils.DAO_EXTENSION;
    String importStr = Joiners.DOT.join(packageName, TransformationUtils.DAO_PACKAGE, name);
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);
  }
  
  protected void createDomainAttributes(ASTCDClass clazz, ASTCDClass domainClass) {
    String name = domainClass.getName() + TransformationUtils.DAO_EXTENSION;
    ASTCDAttribute attr = new CDAttributeBuilder().Private().type(name)
        .setName(TransformationUtils.uncapitalize(name)).build();
    TransformationUtils.addAttributeAnnos(attr.getModifier(), "@Inject");
    clazz.addCDAttribute(attr);
  }
  
  protected void createDomainMethods(ASTCDClass clazz, ASTCDClass domainClass) {
    String name = domainClass.getName() + TransformationUtils.DAO_EXTENSION;
    ASTCDMethod method = new CDMethodBuilder().Public()
        .name("get" + domainClass.getName() + TransformationUtils.DAO_EXTENSION).returnType(name)
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new StringHookPoint("{ return " + TransformationUtils.uncapitalize(name) + "; }"));
    clazz.addCDMethod(method);
  }
  
}
