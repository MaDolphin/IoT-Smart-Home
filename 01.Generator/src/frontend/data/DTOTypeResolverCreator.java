/* (c) https://github.com/MontiCore/monticore */
package frontend.data;

import backend.common.CoreTemplate;
import backend.dtos.AggregateTrafo;
import com.google.common.collect.Lists;
import common.util.CDAttributeBuilder;
import common.util.CDMethodBuilder;
import common.util.CompilationUnit;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import frontend.common.FrontendTransformationUtils;

import java.util.Collection;
import java.util.Optional;

import static common.util.CompilationUnit.*;
import static common.util.TransformationUtils.setProperty;

public class DTOTypeResolverCreator extends AggregateTrafo {

  private static final String FILENAME = "DTOTypeResolver";

  private static final String PACKAGE = "";

  public DTOTypeResolverCreator() {
    super(PACKAGE, FILENAME);
  }

  @Override
  protected ASTCDClass createClass(String packageName, String className) {
    ASTCDClass clazz = super.createClass(packageName, className);
    setProperty(clazz, SUBPACKAGE_PROPERTY, TransformationUtils.RTE_PACKAGE);
    TransformationUtils.setProperty(clazz, FILEEXTENSION_PROPERTY, "resolver");
    TransformationUtils.setProperty(clazz, FILENAME_PROPERTY, "dtotype");

    clazz.getModifier().setPublic(false);

    ASTCDAttribute attr = new CDAttributeBuilder().Public().Static()
            //.val("DTOTypeResolver.init()")
        .type("Map<string, ElementType<IDTO>>")
            .setName("types").build();
    clazz.addCDAttribute(attr);
    getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
            new StringHookPoint(" = DTOTypeResolver.init()"));

    // Generate additional methods
    createResolveMethod(clazz);
    createInitMethod(clazz);

    return clazz;
  }

  private void createResolveMethod(ASTCDClass clazz) {
    ASTCDMethod method = new CDMethodBuilder().Public().Static().name("resolve")
            .addParameter(FrontendTransformationUtils.STRING_OR_UNDEFINED, "typeName")
        .returnType("ElementType<IDTO>")
            .build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("frontend.data.DTOResolve"));
    clazz.addCDMethod(method);
  }

  private void createInitMethod(ASTCDClass clazz) {
    ASTCDMethod method = new CDMethodBuilder().Static().name("init")
        .returnType("Map<string, ElementType<IDTO>>")
            .build();
    Collection<String> attrList = Lists.newArrayList();
    for (ASTCDCompilationUnit compUnit : cdList) {
      compUnit.getCDDefinition().getCDClassList().forEach(c -> attrList.add(c.getName()));
    }
    domainUnit.get().getCDDefinition().getCDClassList().forEach(c -> {
      attrList.add(c.getName());
      attrList.add(c.getName() + "Full");
    });
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("frontend.data.DTOInit", attrList));
    clazz.addCDMethod(method);
  }

  @Override
  protected void createAggregateImports(ASTCDClass clazz, ASTCDClass aggregateClass,
                                        String packageName) {
    String importStr = (FrontendTransformationUtils
            .getImportCheckHWC(aggregateClass.getName() + "DTO", handcodePath, DTOCreator.FILEEXTENION,
                    TransformationUtils.DTOS_PACKAGE, Optional.of(aggregateClass.getName().toLowerCase())));
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);
  }

  @Override
  protected void createDomainImports(ASTCDClass clazz, ASTCDClass domainClass, String packageName) {
    // DTOs
    String importStr = (FrontendTransformationUtils
            .getImportCheckHWC(domainClass.getName() + "DTO", handcodePath, DTOCreator.FILEEXTENION,
                    TransformationUtils.DTOS_PACKAGE, Optional.of(domainClass.getName().toLowerCase())));
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);

    // FullDTOs
    importStr = (FrontendTransformationUtils
            .getImportCheckHWC(domainClass.getName() + "FullDTO", handcodePath, FullDTOCreator.FILEEXTENION,
                    TransformationUtils.DTOS_PACKAGE, Optional.of(domainClass.getName().toLowerCase())));
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);
  }


  @Override
  protected void createImports(ASTCDClass clazz) {
    String importStr = "{ IDTO, DTO } from '@shared/architecture/data/aggregates/dto'";
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);

    importStr = "{ IdDTO } from '@shared/architecture/command/aggregate/id.dto'";
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);

    importStr = "{ NotImplementedDTO } from '@shared/architecture/command/aggregate/notimplemented.dto'";
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);

    importStr = "{ OkDTO } from '@shared/architecture/command/aggregate/ok.dto'";
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);

    importStr = "{ ErrorDTO } from '@shared/architecture/command/aggregate/error.dto'";
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);

    importStr = "{ MontiGemError } from '@shared/utils/montigem.error'";
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);

    importStr = "{ ElementType } from '@upe/typedjson/dist/untils'";
    //importStr = "{ElementType} from '@shared/architecture/data/utils/element.type'";
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);

  }

}
