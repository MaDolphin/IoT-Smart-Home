/* (c) https://github.com/MontiCore/monticore */
package frontend.data;

import common.ExtendTrafo;
import common.util.CDClassBuilder;
import common.util.CompilationUnit;
import common.util.TransformationUtils;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static common.util.CompilationUnit.*;


public abstract class CommandCreator extends ExtendTrafo {

  public static final String SUBPACKAGE = "commands";

  protected String fileExtension;

  protected String commandName;

  public CommandCreator(String fileExtension, String commandName) {
    this.fileExtension = fileExtension;
    this.commandName = commandName;
  }

  @Override
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
                                                          CDTypeSymbol typeSymbol) {
    String typeName = typeSymbol.getName();
    ASTCDClass addedClass = new CDClassBuilder().superclass("CommandDTO")
            .setName(typeName + commandName).build();
    TransformationUtils.setProperty(addedClass, SUBPACKAGE_PROPERTY, SUBPACKAGE);
    TransformationUtils.setProperty(addedClass, FILEEXTENSION_PROPERTY, fileExtension);
    TransformationUtils.setProperty(addedClass, FILENAME_PROPERTY, typeName.toLowerCase());
    getOutputAst().getCDClassList().add(addedClass);
    return Optional.of(addedClass);
  }

  @Override
  protected Optional<ASTCDStereotype> getStereotype(ASTCDType type) {
    // Add annotation JsonObject()
    ASTCDStereotype stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
    ASTCDStereoValue value = CD4AnalysisMill.cDStereoValueBuilder().setName("@JsonObject()").build();
    stereotype.getValueList().add(value);
    return Optional.of(stereotype);
  }

  @Override
  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    getImports(typeSymbol).forEach(i -> TransformationUtils
            .addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, i));
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("{JsonMember, JsonObject} from '@upe/typedjson'");
    imports.add(
            "{CommandDTO} from '../../../src/app/shared/architecture/command/rte/command.dto'");
    return imports;
  }

}
