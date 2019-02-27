/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package common;

import common.DexTransformation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;
import static common.util.TransformationUtils.CLASSES_PACKAGE;
import static common.util.TransformationUtils.setProperty;

public class PackageTrafo extends DexTransformation {

  @Override
  protected void transform() {
    // checkArgument(symbolTable.isPresent());
    List<ASTCDType> types = new ArrayList<>();
    types.addAll(getAst().getCDClassList());
    types.addAll(getAst().getCDInterfaceList());
    types.addAll(getAst().getCDEnumList());
    types.forEach(t -> setProperty(t, SUBPACKAGE_PROPERTY,
        Joiners.DOT.join(CLASSES_PACKAGE, (t.getSymbolOpt().isPresent() ?
            t.getSymbol().getName().toLowerCase() :
            t.getName().toLowerCase()))));
  }
}
