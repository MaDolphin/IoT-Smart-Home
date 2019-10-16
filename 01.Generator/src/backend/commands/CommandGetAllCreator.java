/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

/* (c) https://github.com/MontiCore/monticore */
package backend.commands;

import backend.common.CoreTemplate;
import common.util.CDMethodBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.List;
import java.util.Optional;

public class CommandGetAllCreator extends CommandCreator {

  private static final String DTOType = "FullDTOList";

  public CommandGetAllCreator() {
    super(TransformationUtils.GETALL_CMD);
  }

  @Override
  protected String getSuperclass() {
    return "GetAllCommandDTO";
  }

  @Override
  protected void setTemplate(ASTCDMethod method, ASTCDClass domainClass, String className) {
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.commands.GetAll", domainClass.getName(), className));
  }

  @Override
  protected Optional<ASTCDConstructor> createFullConstructor(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    return Optional.empty();
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = super.getImports(typeSymbol);
    imports.add("de.montigem.be.domain.rte.dao.QueryRestriction");
    imports.add("de.montigem.be.command.rte.general.GetAllCommandDTO");
    return imports;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = super.createMethods(extendedClass, domainClass, typeSymbol);

    String identifier = domainClass.getName();
    String className = extendedClass.getName();
    methods.add(getDomainObjectMethod(identifier, className));

    return methods;
  }

  protected ASTCDMethod getDomainObjectMethod(String identifier, String className) {
    ASTCDMethod method = new CDMethodBuilder().Protected()
        .addParameter("SecurityHelper", "securityHelper")
        .addParameter("DAOLib", "daoLib")
        .returnType(identifier + DTOType)
        .name("getDomainObjects").build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.commands.GetDomainObjectLoadAllWithRestriction", identifier, className));

    return method;
  }
}
