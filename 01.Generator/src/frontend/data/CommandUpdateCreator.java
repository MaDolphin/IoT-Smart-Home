/* (c) https://github.com/MontiCore/monticore */
package frontend.data;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.util.CDAttributeBuilder;
import common.util.CDConstructorBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import frontend.common.FrontendTransformationUtils;

import java.util.List;
import java.util.Optional;

import static frontend.common.FrontendTransformationUtils.createJsonMemberStereotype;


public class CommandUpdateCreator extends CommandCreator {

  public static final String FILEEXTENSION = "update";

  public CommandUpdateCreator() {
    super(FILEEXTENSION, TransformationUtils.UPDATE_CMD);
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass extendedClass, ASTCDClass domainClass,
                                                  CDTypeSymbol typeSymbol) {
    String typeName = domainClass.getName() + "FullDTO";
    ASTCDAttribute idAttr = new CDAttributeBuilder().Public()
            .type(typeName).setName("dto").build();
    idAttr.getModifier().setStereotype(
            createJsonMemberStereotype(idAttr.getName(), typeName, true));

    return Lists.newArrayList(idAttr);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDConstructor constructor = new CDConstructorBuilder()
            .addParameter(domainClass.getName() + "FullDTO", "dto?")
            .Package()
            .setName(extendedClass.getName()).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new TemplateHookPoint("frontend.data.CommandConstructorParam", extendedClass.getName(), Lists.newArrayList("dto")));
    return Lists.newArrayList(constructor);
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = super.getImports(typeSymbol);
    imports.add(FrontendTransformationUtils
            .getImportCheckHWC(typeSymbol.getName() + "FullDTO", handcodePath, FullDTOCreator.FILEEXTENION,
                TransformationUtils.DTOS_PACKAGE, Optional.of(typeSymbol.getName().toLowerCase())));
    return imports;
  }

}
