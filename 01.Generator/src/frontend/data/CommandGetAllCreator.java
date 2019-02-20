/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package frontend.data;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.util.CDConstructorBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.List;


public class CommandGetAllCreator extends CommandCreator {


  public static final String FILEEXTENSION = "getall";

  public CommandGetAllCreator() {
    super(FILEEXTENSION, TransformationUtils.GETALL_CMD);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    ASTCDConstructor constructor = new CDConstructorBuilder()
            .Package()
            .setName(extendedClass.getName()).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
            new TemplateHookPoint("frontend.data.GetAllConstructor", extendedClass.getName()));
    return Lists.newArrayList(constructor);
  }

}
