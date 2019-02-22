/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package backend.commands;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.util.CDMethodBuilder;
import common.util.CompilationUnit;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.se_rwth.commons.Joiners;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class CommandDTOTypeAdapterTrafo extends TypeAdapterTrafo {

  public static final String CLASSNAME = "CommandDTOTypeAdapter";

  public static final String SUPERTYPE = "CommandDTO";

  protected Optional<ASTCDCompilationUnit> cmdUnit = Optional.empty();

  public CommandDTOTypeAdapterTrafo() {
    super(CLASSNAME, SUPERTYPE);
  }

  @Override
  protected void transform() {
    checkArgument(cmdUnit.isPresent());
    super.transform();
  }

  protected void createInitMethod(ASTCDClass clazz) {
    ASTCDMethod method = new CDMethodBuilder().Private().name("init")
            .build();
    Collection<String> cmdList = Lists.newArrayList();
    for (ASTCDCompilationUnit compUnit : cdList) {
      for (ASTCDClass c : compUnit.getCDDefinition().getCDClassList()) {
        String className = c.getName();
        cmdList.add(className + TransformationUtils.GETALL_CMD);
        cmdList.add(className + TransformationUtils.GETBYID_CMD);
      }
    }

    for (ASTCDClass c : domainUnit.get().getCDDefinition().getCDClassList()) {
      String className = c.getName();
      cmdList.add(className + TransformationUtils.CREATE_CMD);
      cmdList.add(className + TransformationUtils.DELETE_CMD);
      cmdList.add(className + TransformationUtils.GETBYID_CMD);
      cmdList.add(className + TransformationUtils.UPDATE_CMD);
      for (ASTCDAttribute attr : symbolTable.get().getVisibleNotDerivedAttributesInHierarchyAsCDAttribute(className)) {
        cmdList.add(className + TransformationUtils.SET_CMD + TransformationUtils.capitalize(attr.getName()));
      }
    }

    for (ASTCDClass c : cmdUnit.get().getCDDefinition().getCDClassList()) {
      cmdList.add(TransformationUtils.removeTOPExtensionIfExistent(c.getName()));
    }

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("backend.commands.TypeAdapterInit", cmdList));
    clazz.addCDMethod(method);
  }

  @Override
  protected void createAggregateImports(ASTCDClass clazz, ASTCDClass aggregateClass,
                                        String packageName) {
    String importStr = Joiners.DOT.join(packageName, TransformationUtils.COMMANDS_PACKAGE, "*");
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);
  }

  @Override
  protected void createDomainImports(ASTCDClass clazz, ASTCDClass domainClass, String packageName) {
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
            "de.montigem.be.domain.commands.*");
  }

  @Override
  protected void createImports(ASTCDClass clazz) {
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
            "de.montigem.be.command.rte.general.CommandDTO");
    TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
            "de.montigem.be.command.commands.*");
  }

  public void transform(ASTCDCompilationUnit cmpUnit, ASTCDCompilationUnit domainUnit,
                        ASTCDCompilationUnit cmdUnit, Collection<ASTCDCompilationUnit> cdList) {
    this.cmdUnit = Optional.ofNullable(cmdUnit);
    transform(cmpUnit, domainUnit, cdList);
  }

}
