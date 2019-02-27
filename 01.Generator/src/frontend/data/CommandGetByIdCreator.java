/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
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

import java.util.List;

import static frontend.common.FrontendTransformationUtils.NUMERIC_FRONTEND;
import static frontend.common.FrontendTransformationUtils.createJsonMemberStereotype;


public class CommandGetByIdCreator extends CommandCreator {

  public static final String FILEEXTENSION = "getbyid";

  public CommandGetByIdCreator() {
    super(FILEEXTENSION, TransformationUtils.GETBYID_CMD);
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass extendedClass, ASTCDClass domainClass,
                                                  CDTypeSymbol typeSymbol) {
    ASTCDAttribute id = new CDAttributeBuilder().Public()
            .type(NUMERIC_FRONTEND).setName("objectId").build();
    id.getModifier().setStereotype(
            createJsonMemberStereotype(id.getName(), NUMERIC_FRONTEND, true));
    return Lists.newArrayList(id);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDConstructor constructor = new CDConstructorBuilder()
        .addParameter("number", "objectId?")
            .Package()
            .setName(extendedClass.getName()).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
            new TemplateHookPoint("frontend.data.CommandConstructorParam", extendedClass.getName(), Lists.newArrayList("objectId")));
    return Lists.newArrayList(constructor);
  }

}
