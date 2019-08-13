/* (c) https://github.com/MontiCore/monticore */
package backend.commands;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.util.CDMethodBuilder;
import common.util.CompilationUnit;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.Collection;

public class DTOTypeAdapterTrafo extends TypeAdapterTrafo {

  public static final String CLASSNAME = "DTOTypeAdapter";

  public static final String SUPERCLASSTYPE = "DTO";

  public DTOTypeAdapterTrafo() {
    super(CLASSNAME, SUPERCLASSTYPE);
  }

  protected void createInitMethod(ASTCDClass clazz) {
    ASTCDMethod method = new CDMethodBuilder().Private().name("init")
            .build();
    Collection<String> cList = Lists.newArrayList();
    cList.add("OkDTO");
    cList.add("IdDTO");
    cList.add("ErrorDTO");
    cList.add("NotImplementedDTO");
    for (ASTCDCompilationUnit compUnit : cdList) {
      for (ASTCDClass c : compUnit.getCDDefinition().getCDClassList()) {
        String className = c.getName();
        cList.add(className + "DTO");
      }
    }

    for (ASTCDClass c : domainUnit.get().getCDDefinition().getCDClassList()) {
      String className = c.getName();
      cList.add(className + "DTO");
      cList.add(className + "FullDTO");
     }

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("backend.commands.TypeAdapterInit", cList));
    clazz.addCDMethod(method);
  }

  @Override
  protected void createAggregateImports(ASTCDClass clazz, ASTCDClass aggregateClass,
                                        String packageName) {
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, packageName + ".dtos.*");
  }

  @Override
  protected void createDomainImports(ASTCDClass clazz, ASTCDClass domainClass, String packageName) {
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
            "de.montigem.be.domain.dtos.*");
  }

  @Override
  protected void createImports(ASTCDClass clazz) {
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
            "de.montigem.be.dtos.rte.*");
  }

}
