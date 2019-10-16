/* (c) https://github.com/MontiCore/monticore */

package frontend.data;

import backend.common.CoreTemplate;
import common.util.CDMethodBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import frontend.common.FrontendTransformationUtils;

import java.util.List;
import java.util.Optional;

public class AggregateDTOCreator extends DTOCreator {

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass,
      ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = super.createMethods(extendedClass, domainClass, typeSymbol);
    methods.add(getGetAllMethod(domainClass, typeSymbol));
    return methods;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = super.getImports(typeSymbol);
    imports.add(FrontendTransformationUtils
            .getImportCheckHWC(typeSymbol.getName() + TransformationUtils.GETALL_CMD, handcodePath, CommandGetAllCreator.FILEEXTENSION, CommandCreator.SUBPACKAGE,
                    Optional.of(typeSymbol.getName().toLowerCase())));
    return imports;
  }

  private ASTCDMethod getGetAllMethod(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder()
            .name("getAll")
        .addParameter("CommandManager", "commandManager")
            .returnType("Promise<" + typeSymbol.getName() + "DTO>").Public().Static().build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("frontend.data.DTOGetAll",
                    typeSymbol.getName()));
    return method;
  }

}
