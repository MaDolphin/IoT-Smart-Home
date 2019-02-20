/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package backend.dtos;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.TrafoForAggregateModels;
import common.util.CDConstructorBuilder;
import common.util.CDMethodBuilder;
import common.util.GetterSetterHelper;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DTOLoadersCreator extends TrafoForAggregateModels {

  public static final String DTOLoader = "DTOLoader";

  public DTOLoadersCreator() {
    super(DTOLoader);
  }

  public DTOLoadersCreator(String suffix) {
    super(suffix);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass clazz, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDConstructor> constructors = Lists.newArrayList();
    ASTCDConstructor astcdConstructor = new CDConstructorBuilder().Package().Public()
        .setName(clazz.getName()).build();
    constructors.add(astcdConstructor);
    constructors.add(createConstructor(typeSymbol));
    constructors.add(createConstructorWithId(typeSymbol));
    return constructors;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass proxyClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    for (ASTCDAttribute attr : proxyClass.getCDAttributeList()) {
      methods.addAll(createDefaultGet(attr));
    }
    return methods;
  }

  @Override
  protected Optional<String> getSuperclassName(
      ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    return Optional.of("DTOLoader<" + typeSymbol.getName() + "DTO>");
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("de.macoco.be.dtos.rte.DTOLoader");
    imports.add("de.macoco.be.util.DAOLib");
    imports.add("de.se_rwth.commons.logging.Log");
    imports.add("de.macoco.be.authz.util.SecurityHelper");
    imports.add("de.macoco.be.authz.Permissions");
      return imports;
  }

  //----------- PRIVATE  METHODS -------------------------------------

  protected List<ASTCDMethod> createDefaultGet(ASTCDAttribute attr) {
    List<ASTCDMethod> createdMethods = new ArrayList<>();
    String attrName = attr.getName();

    // add getter
    ASTCDMethod getMethod = new CDMethodBuilder().Public()
        .name(GetterSetterHelper.getPlainGetter(attr))
        .setReturnType((ASTReturnType) attr.getType().deepClone()).build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), getMethod,
        new TemplateHookPoint("backend.dtos.Get", attrName));

    createdMethods.add(getMethod);

    return createdMethods;
  }

  protected ASTCDConstructor createConstructorWithId(CDTypeSymbol type) {
    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .addParameter("DAOLib", "daoLib")
        .addParameter("long", "id")
        .addParameter("SecurityHelper", "securityHelper")
        .setName(type.getName() + DTOLoader).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new StringHookPoint(
            "{ super(daoLib, id, securityHelper); }"));
    return constructor;
  }

  protected ASTCDConstructor createConstructor(CDTypeSymbol type) {
    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .addParameter("DAOLib", "daoLib")
        .addParameter("SecurityHelper", "securityHelper")
        .setName(type.getName() + DTOLoader).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new StringHookPoint(
            "{ super(daoLib, securityHelper); }"));
    return constructor;
  }


}
