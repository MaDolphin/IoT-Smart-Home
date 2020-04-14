/* (c) https://github.com/MontiCore/monticore */
package backend.dtos;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.TrafoForAggregateModels;
import common.util.CDConstructorBuilder;
import common.util.CDMethodBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DTOListLoadersCreator extends TrafoForAggregateModels {

  public static final String DTOLoader = "FullDTOListLoader";

  public DTOListLoadersCreator() {
    super(DTOLoader);
  }

  public DTOListLoadersCreator(String suffix) {
    super(suffix);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass clazz, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDConstructor> constructors = Lists.newArrayList();
    ASTCDConstructor astcdConstructor = new CDConstructorBuilder().Package().Public()
        .setName(clazz.getName()).build();
    constructors.add(astcdConstructor);
    constructors.add(createConstructorWithDTOList(typeSymbol));
    constructors.add(createConstructorWithDTOListAsList(typeSymbol));
    constructors.add(createConstructor(typeSymbol));
    return constructors;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass proxyClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newArrayList();
    methods.add(this.createLoadDTOListAll(typeSymbol));
    methods.add(this.createLoadDTOListWithRestriction(typeSymbol));

    return methods;
  }

  @Override
  protected Optional<String> getSuperclassName(
      ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    return Optional.of("DTOLoader<" + typeSymbol.getName() + "FullDTOList>");
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("de.montigem.be.dtos.rte.DTOLoader");
    imports.add("de.montigem.be.util.DAOLib");
    imports.add("de.se_rwth.commons.logging.Log");
    imports.add("de.montigem.be.authz.util.SecurityHelper");
    imports.add("de.montigem.be.authz.Permissions");
    imports.add("de.montigem.be.domain.rte.dao.QueryRestriction");
    imports.add(Joiners.DOT.join(TransformationUtils.getPackageName(getOutputAstRoot()),
        TransformationUtils.CLASSES_PACKAGE, typeSymbol.getName().toLowerCase(),
        typeSymbol.getName()));
    return imports;
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

  protected ASTCDConstructor createConstructorWithDTOList(CDTypeSymbol type) {
    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .addParameter(type.getName() + DTOListCreator.DTOLIST, "dtoList")
        .setName(type.getName() + DTOLoader).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new StringHookPoint(
            "{ super(dtoList); }"));
    return constructor;
  }

  protected ASTCDConstructor createConstructorWithDTOListAsList(CDTypeSymbol type) {
    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
        .addParameter("List<" + type.getName() + ">", "dtoList")
        .setName(type.getName() + DTOLoader).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
        new StringHookPoint(
            "{ super(new " + type.getName() + DTOListCreator.DTOLIST + "(dtoList)); }"));
    return constructor;
  }

  protected ASTCDMethod createLoadDTOListAll(CDTypeSymbol type) {
    ASTCDMethod load = new CDMethodBuilder().Public()
        .name("loadAll").returnType(type.getName() + DTOListCreator.DTOLIST)
        .addParameter("DAOLib", "daoLib")
        .addParameter("SecurityHelper", "securityHelper")
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), load,
        new TemplateHookPoint("backend.dtos.DTOListLoaderLoadDTOAll",
            type.getName(), ""));
    return load;
  }

  protected ASTCDMethod createLoadDTOListWithRestriction(CDTypeSymbol type) {
    ASTCDMethod load = new CDMethodBuilder().Public()
        .name("loadAllWithRestriction").returnType(type.getName() + DTOListCreator.DTOLIST)
        .addParameter("DAOLib", "daoLib")
        .addParameter("QueryRestriction", "restriction")
        .addParameter("SecurityHelper", "securityHelper")
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), load,
        new TemplateHookPoint("backend.dtos.DTOListLoaderLoadDTOWithRestriction",
            type.getName(), ""));
    return load;
  }

}
