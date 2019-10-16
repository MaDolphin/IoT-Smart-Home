/* (c) https://github.com/MontiCore/monticore */

package backend.common;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import common.DexTransformation;
import common.util.CDSimpleReferenceBuilder;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

import static com.google.common.base.Preconditions.checkArgument;

// TODO: extend this class to search for predefined types List,Set, and Optional.
// if the types are found, then include the import
public class TypesConverter extends DexTransformation {

  private static final String OPTIONAL_FULL = "java.util.Optional";

  private static final String OPTIONAL = "Optional";

  @Override
  public void transform() {

    if (foundOptional(getAst())) {
      // remove import and make it java.util.Optional
      ASTImportStatement opt = null;
      for (ASTImportStatement statement : getAstRoot().getImportStatementList()) {
        if (OPTIONAL_FULL.equals(Joiner.on(".").join(statement.getImportList()))) {
          opt = statement;
        }
      }

      if (opt != null) {
        getAstRoot().getImportStatementList().remove(opt);
      }

      // add Optional
      getAstRoot().getImportStatementList().add(
          CD4AnalysisMill.importStatementBuilder()
              .setImportList(Lists.newArrayList("java", "util", "Optional")).build());

    }
  }

  /**
   * find all optional attributes in classes and convert them to
   * java.util.Optional
   *
   * @param ast
   * @return
   */
  private boolean foundOptional(ASTCDDefinition ast) {
    boolean foundOptional = false;

    for (ASTCDClass clazz : ast.getCDClassList()) {

      for (ASTCDAttribute attr : clazz.getCDAttributeList()) {

        if (TypesPrinter.printType(attr.getType()).startsWith(OPTIONAL)
            || TypesPrinter.printType(attr.getType()).startsWith(OPTIONAL_FULL)) {
          foundOptional = true;
          // find the generic type
          ASTSimpleReferenceType ref = (ASTSimpleReferenceType) attr.getType();
          checkArgument(ref.getTypeArgumentsOpt().isPresent());

          attr.setType(new CDSimpleReferenceBuilder()
              .name(Lists.newArrayList(OPTIONAL))
              .setTypeArguments(ref.getTypeArguments()).build());

        }
      }
    }
    return foundOptional;
  }

}
