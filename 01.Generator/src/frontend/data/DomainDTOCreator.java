/* (c) https://github.com/MontiCore/monticore */

package frontend.data;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.util.CDMethodBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import frontend.common.FrontendTransformationUtils;

import java.util.List;
import java.util.Optional;

import static frontend.common.FrontendTransformationUtils.getFrontendType;

public class DomainDTOCreator extends DTOCreator {

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass extendedClass,
                                            ASTCDClass domainClass,
                                            CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = super.createMethods(extendedClass, domainClass, typeSymbol);
    methods.addAll(getSetCmdMethods(domainClass, typeSymbol));
    methods.add(getDeleteMethod(domainClass, typeSymbol));
    return methods;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = super.getImports(typeSymbol);
    imports.add(FrontendTransformationUtils
        .getImportCheckHWC(typeSymbol.getName() + TransformationUtils.DELETE_CMD, handcodePath, "delete", CommandCreator.SUBPACKAGE,
            Optional.of(typeSymbol.getName().toLowerCase())));

    List<ASTCDAttribute> attrList = symbolTable.get().getVisibleNotDerivedAttributesInHierarchyAsCDAttribute(typeSymbol.getName());
    for (ASTCDAttribute attr : attrList) {
      imports.add(FrontendTransformationUtils
              .getImportCheckHWC(typeSymbol.getName() + TransformationUtils.SET_CMD + TransformationUtils.capitalize(attr.getName()), handcodePath, CommandSetAttributeCreator.FILEEXTENSION+attr.getName().toLowerCase(), CommandCreator.SUBPACKAGE,
                      Optional.of(typeSymbol.getName().toLowerCase())));
    }
    return imports;
  }

  private List<ASTCDMethod> getSetCmdMethods(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    List<CDFieldSymbol> attrList = symbolTable.get().getVisibleNotDerivedAttributesInHierarchy(domainClass.getName());
    for (CDFieldSymbol attr : attrList) {
      String paramType;
      if (attr.getType().isEnum()) {
        paramType = FrontendTransformationUtils.STRING_FRONTEND;
      } else {
        paramType = getFrontendType(attr.getType().getStringRepresentation(), Optional.of(typeSymbol), symbolTable.get());
      }
      ASTCDMethod method = new CDMethodBuilder()
              .name("set" + TransformationUtils.capitalize(attr.getName()))
              .addParameter(paramType, attr.getName())
          .addParameter("CommandManager", "commandManager")
              .returnType("Promise<null>").Public().build();
      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
              new TemplateHookPoint("frontend.data.DTOSet",
                      typeSymbol.getName(), attr.getName()));
      methods.add(method);
    }
    return methods;
  }

  private ASTCDMethod getDeleteMethod(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDMethod method = new CDMethodBuilder()
        .name("delete")
        .addParameter("CommandManager", "commandManager")
        .returnType("Promise<IdDTO>").Public().build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("frontend.data.DTODelete",
            typeSymbol.getName()));
    return method;
  }
}
