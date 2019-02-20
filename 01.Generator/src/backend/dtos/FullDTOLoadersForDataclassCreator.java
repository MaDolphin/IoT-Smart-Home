/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package backend.dtos;

import backend.common.CoreTemplate;
import common.util.CDConstructorBuilder;
import common.util.CDMethodBuilder;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import frontend.data.DTOCreator;

import java.util.Optional;

public class FullDTOLoadersForDataclassCreator extends DTOLoadersForDataclassCreator {

  public static final String FULL = "Full";

  public static final String FULLDTOLOADER = FULL + DTOLoader;

  public FullDTOLoadersForDataclassCreator() {
    super(FULLDTOLOADER);
  }

  @Override
  protected Optional<String> getSuperclassName(
      ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    return Optional.of("DTOLoader<" + typeSymbol.getName() + "FullDTO>");
  }

  //----------- PROTECTED AND PRIVATE METHODS -------------------------------------

  @Override
  protected ASTCDConstructor createConstructorWithId(CDTypeSymbol type) {
    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .addParameter("DAOLib", "daoLib")
        .addParameter("long", "id")
        .addParameter("SecurityHelper", "securityHelper")
        .setName(type.getName() + FULLDTOLOADER).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new StringHookPoint(
            "{ super(daoLib, id, securityHelper); }"));
    return constructor;
  }


  @Override
  protected ASTCDMethod createLoadDTOIdMethod(CDTypeSymbol type) {
    ASTCDMethod load = new CDMethodBuilder().Public()
        .name("loadDTO").returnType(type.getName() + FULL + DTOCreator.DTO)
        .addParameter("DAOLib", "daoLib")
        .addParameter("long", "id")
        .addParameter("SecurityHelper", "securityHelper")
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), load,
        new TemplateHookPoint("backend.dtos.DTOLoaderLoadDTOId",
            type.getName(), FULL));
    return load;
  }
}
