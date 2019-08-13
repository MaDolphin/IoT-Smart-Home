/* (c) https://github.com/MontiCore/monticore */

package frontend.common;

import common.DexTransformation;
import common.util.TransformationUtils;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.se_rwth.commons.Joiners;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.*;

public class FrontendRenameDomainTrafo extends DexTransformation {

  public static final String MODEL_EXTENSION = "model";

  @Override
  protected void transform() {
    checkArgument(symbolTable.isPresent());
    checkArgument(handcodePath.isPresent());
    if (!generateTOP) {
      return;
    }

    for (ASTCDClass clazz : getAst().getCDClassList()) {
      Collection<String> packageProperty = TransformationUtils
          .getProperty(clazz, SUBPACKAGE_PROPERTY);
      String packageName = packageProperty.size() == 1 ?
          packageProperty.iterator().next() : "";
      Collection<String> fileNameProperty = TransformationUtils
          .getProperty(clazz, FILENAME_PROPERTY);
      String fileName = fileNameProperty.size() == 1 ?
          fileNameProperty.iterator().next() : clazz.getName().toLowerCase();
      Collection<String> fileExtensionProperty = TransformationUtils
          .getProperty(clazz, FILEEXTENSION_PROPERTY);
      String fileExtension = fileExtensionProperty.size() == 1 ?
          fileExtensionProperty.iterator().next() : MODEL_EXTENSION;
      // Check if a handwritten class exists
      String completeName = Joiners.DOT.join(fileName, fileExtension);
      if (TransformationUtils
          .existsHandwrittenDotFile(completeName, packageName, handcodePath.get(),
              TransformationUtils.DOT_TYPESCRIPT_FILE_EXTENSION)) {
        // Rename class
        clazz.setName(clazz.getName() + TransformationUtils.TOP_EXTENSION);
        TransformationUtils.setProperty(clazz, FILENAME_PROPERTY,
            fileName + TransformationUtils.TOP_EXTENSION.toLowerCase());
      }
    }
    for (ASTCDInterface interf : getAst().getCDInterfaceList()) {
      Collection<String> packageProperty = TransformationUtils
          .getProperty(interf, SUBPACKAGE_PROPERTY);
      String packageName = packageProperty.size() == 1 ?
          packageProperty.iterator().next() : "";
      String hwcName = TransformationUtils
          .getSingleProperty(interf, HWC_PROPERTY).orElse(interf.getName().toLowerCase());
      // Check if a handwritten class exists
      if (TransformationUtils
          .existsHandwrittenDotFile(hwcName, packageName, handcodePath.get(),
              TransformationUtils.DOT_TYPESCRIPT_FILE_EXTENSION)) {
        // Rename class
        interf.setName(interf.getName() + TransformationUtils.TOP_EXTENSION);
        Optional<String> fileProperty = TransformationUtils
            .getSingleProperty(interf, FILENAME_PROPERTY);
        if (fileProperty.isPresent()) {
          TransformationUtils.setProperty(interf, FILENAME_PROPERTY,
              fileProperty.get() + TransformationUtils.TOP_EXTENSION.toLowerCase());
        }
      }
    }
  }

}
