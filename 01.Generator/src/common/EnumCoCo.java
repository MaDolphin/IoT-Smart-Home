/* (c) https://github.com/MontiCore/monticore */

package common;

import de.monticore.literals.literals._ast.ASTStringLiteral;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumParameter;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDEnumConstantCoCo;
import de.se_rwth.commons.logging.Log;

public class EnumCoCo implements CD4AnalysisASTCDEnumConstantCoCo {

  @Override
  public void check(ASTCDEnumConstant astcdEnumConstant) {
    boolean isAllowed = true;
    if (astcdEnumConstant.getCDEnumParameterList().size()==1) {
      ASTCDEnumParameter param = astcdEnumConstant.getCDEnumParameterList().get(0);
      if (!(param.getValue().getSignedLiteral() instanceof ASTStringLiteral)) {
        isAllowed = false;
      }
    } else if (astcdEnumConstant.getCDEnumParameterList().size()>1) {
      isAllowed = false;
    }
    if (!isAllowed) {
      Log.error(
              String.format("0xC4A74 EnumConstant %s has invalid parameter.", astcdEnumConstant.getName()),
              astcdEnumConstant.get_SourcePositionStart());
    }
  }
}
