/* (c) https://github.com/MontiCore/monticore */
package backend.dtos;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.util.CDConstructorBuilder;
import common.util.CDMethodBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;
import frontend.data.DTOCreator;

import java.util.List;

public class DTOLoadersForDataclassCreator extends DTOLoadersCreator {

  public DTOLoadersForDataclassCreator() {
    super();
  }

  public DTOLoadersForDataclassCreator(String suffix) {
    super(suffix);
  }

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass clazz, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDConstructor> constructors = Lists.newArrayList();
    ASTCDConstructor astcdConstructor = new CDConstructorBuilder().Package().Public()
        .setName(clazz.getName()).build();
    constructors.add(astcdConstructor);
    constructors.add(createConstructorWithId(typeSymbol));
    return constructors;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass proxyClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = super.createMethods(proxyClass, clazz, typeSymbol);
    methods.add(createLoadDTOIdMethod(typeSymbol));
    return methods;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = super.getImports(typeSymbol);
    imports.add(Joiners.DOT.join(TransformationUtils.getPackageName(getOutputAstRoot()),
        TransformationUtils.CLASSES_PACKAGE, typeSymbol.getName().toLowerCase(),
        typeSymbol.getName()));
    return imports;
  }

  //----------- PROTECTED AND PRIVATE METHODS -------------------------------------

  protected ASTCDMethod createLoadDTOIdMethod(CDTypeSymbol type) {
    ASTCDMethod load = new CDMethodBuilder().Public()
        .name("loadDTO").returnType(type.getName() + DTOCreator.DTO)
        .addParameter("DAOLib", "daoLib")
        .addParameter("long", "id")
        .addParameter("SecurityHelper", "securityHelper")
        .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), load,
        new TemplateHookPoint("backend.dtos.DTOLoaderLoadDTOId",
            type.getName(), ""));
    return load;
  }

}
