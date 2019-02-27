/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package common.util;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;

public class CDAttributeUtil {
  /*public static ASTCDAttribute fromCDFieldSymbol(CDFieldSymbol s) {
    return (ASTCDAttribute) s.getAstNode().get();
  }*/

  public static ASTCDAttribute fromCDFieldSymbol(CDFieldSymbol s) {
    return new CDAttributeBuilder()
        .setName(s.getName())
        .setType(
            (new CDSimpleReferenceBuilder()).name(s.getType().getStringRepresentation()).build()
        )
        .setModifier(CD4AnalysisMill.modifierBuilder().build())
        .build();
  }
}
