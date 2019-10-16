/* (c) https://github.com/MontiCore/monticore */
package backend.dtos;

import backend.commands.CommandCreator;
import backend.common.CoreTemplate;
import common.util.CDAttributeBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;

public class CommandDTOGetAllCreator extends CommandCreator {

  public CommandDTOGetAllCreator() {
    super(TransformationUtils.GETALL_CMD);
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    ASTCDAttribute identifier = new CDAttributeBuilder().Protected().type("long").setName("objectId")
        .build();
    attributes.add(identifier);

    return attributes;
  }

  @Override
  protected List<String> additionalImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();

    // TODO SVa: read from somewhere
    imports.add(Joiners.DOT.join(TransformationUtils.getPackageName(getOutputAstRoot()),
            TransformationUtils.DTOS_PACKAGE, typeSymbol.getName() + "DTOLoader"));
    imports.add("org.apache.commons.lang.NotImplementedException");
    imports.add("de.montigem.be.system.common.dtos.*");
    imports.add("de.montigem.be.dtos.rte.*");

    return imports;
  }

  @Override
  protected void setTemplate(ASTCDMethod method, ASTCDClass domainClass, String className) {
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.dtos.GetAll", domainClass.getName(), className));
  }
}
