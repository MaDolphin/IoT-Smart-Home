/* (c) https://github.com/MontiCore/monticore */
package backend.commands;

import backend.common.CoreTemplate;
import backend.dtos.FullDtoCreator;
import common.util.CDAttributeBuilder;
import common.util.CDMethodBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;

public class CommandCreateCreator extends CommandCreator {

  public CommandCreateCreator() {
    super(TransformationUtils.CREATE_CMD);
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    ASTCDAttribute identifier = new CDAttributeBuilder().Protected().type(domainClass.getName() + FullDtoCreator.DTO).setName("dto")
        .build();
    attributes.add(identifier);

    return attributes;
  }

  @Override
  protected void setTemplate(ASTCDMethod method, ASTCDClass domainClass, String className) {
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.commands.Create", domainClass.getName(), className));
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = super.createMethods(extendedClass, domainClass, typeSymbol);

    String identifier = domainClass.getName();
    String className = extendedClass.getName();
    methods.add(getDomainObjectMethod(identifier, className));
    methods.add(getPermissionCheckMethod(identifier, className, "CREATE"));
    methods.add(getDoActionMethod(identifier, className));

    return methods;
  }

  protected ASTCDMethod getDomainObjectMethod(String identifier, String className) {
    ASTCDMethod method = new CDMethodBuilder().Protected()
        .addParameter("SecurityHelper", "securityHelper")
        .addParameter("DAOLib", "daoLib")
        .returnType("Result<" + identifier + ", ErrorDTO>")
        .name("getDomainObject").build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.commands.GetDomainObjectResultFromDTO", className, "dto"));

    return method;
  }

  protected ASTCDMethod getDoActionMethod(String identifier, String className) {
    ASTCDMethod method = new CDMethodBuilder().Protected()
        .addParameter(identifier, "object")
        .addParameter("SecurityHelper", "securityHelper")
        .addParameter("DAOLib", "daoLib")
        .returnType("DTO")
        .name("doAction").build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.commands.DoActionCreate", identifier, className));

    return method;
  }
}
