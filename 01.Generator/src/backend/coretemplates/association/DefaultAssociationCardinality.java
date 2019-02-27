/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package backend.coretemplates.association;

import common.CDSymbolTable;
import common.DexTransformation;
import common.util.CDSimpleReferenceBuilder;
import common.util.TypeHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class DefaultAssociationCardinality extends DexTransformation {

  @Override
  public void transform() {
    checkArgument(symbolTable.isPresent());

    for (ASTCDAssociation association : getAst().getCDAssociationList()) {
      fixCardinalityIfNecessary(association);
      fixQualifierIfNecessary(association, symbolTable.get());
      fixAssociationDirectionIfNecessary(association);
    }
  }

  private void fixAssociationDirectionIfNecessary(ASTCDAssociation association) {
    // If A -- B is given, then make it A<->B
    TypeHelper t = new TypeHelper();
    if (association.isUnspecified()) {
      // handle external
      association.setUnspecified(false);
      if (t.isExternalJavaType(association.getLeftReferenceName().toString())) {
        association.setBidirectional(false);
        association.setLeftToRight(true);
      } else if (t.isExternalJavaType(association.getRightReferenceName().toString())) {
        association.setRightToLeft(true);
        association.setBidirectional(false);
      } else if (association.isComposition()){
        makeBidirectional(association);
      } else {
        makeBidirectional(association);
      }
    }
    // check for unidirectional compositions
    if (association.isComposition() && !association.isBidirectional()){
      makeBidirectional(association);
    }
  }
  
  private void makeBidirectional(ASTCDAssociation association) {
    association.setBidirectional(true);
    association.setLeftToRight(true);
    association.setRightToLeft(true);
  }
  
  // s

  /**
   * Fixes cardinalities if they are not explicitly stated in the CD
   *
   * @param association the association that needs to be fixed
   */
  private void fixCardinalityIfNecessary(ASTCDAssociation association) {
    if (!association.getLeftCardinalityOpt().isPresent()) {
      // composition A -- B; (left to right) becomes composition [0..1] A <-> B
      if (association.isComposition()) {
        association.setLeftCardinality(CD4AnalysisMill.cardinalityBuilder().setOne(true).build());
      } else {
        association.setLeftCardinality(CD4AnalysisMill.cardinalityBuilder().setMany(true).build());
      }
    }

    if (!association.getRightCardinalityOpt().isPresent()) {
      // composition A -- B; (left to right) becomes composition A <-> B [*]
      association.setRightCardinality(CD4AnalysisMill.cardinalityBuilder().setMany(true).build());
    }
  }

  /**
   * Fix the qualifier is necessary, i.e., - if the qualifier is a Type, then
   * the qualifier name is set to qualifier - if the qualifier is an attribute
   * name, then try to find the type of the attribute
   *
   * @param association
   */
  private void fixQualifierIfNecessary(ASTCDAssociation association, CDSymbolTable symTab) {
    Optional<ASTCDQualifier> leftQualifier = association.getLeftQualifierOpt();
    Optional<ASTCDQualifier> rightQualifier = association.getRightQualifierOpt();

    // fix left qualifier
    if (leftQualifier.isPresent()) {
      fixQualifier(leftQualifier.get(),
          symTab.resolve(association.getRightReferenceName().toString()));
    }

    // fix right qualifier
    if (rightQualifier.isPresent()) {
      fixQualifier(rightQualifier.get(),
          symTab.resolve(association.getLeftReferenceName().toString()));
    }
  }

  /**
   * Fix the qualifier is necessary. This method is called by
   * <code>fixQualifierIfNecessary</code>
   *
   * @param qualifier
   * @param clazz
   */
  private void fixQualifier(ASTCDQualifier qualifier, Optional<CDTypeSymbol> clazz) {
    checkArgument(clazz.isPresent());

    if (qualifier.getNameOpt().isPresent()) {
      // try to find an attribute with the given Name
      Optional<CDFieldSymbol> field = clazz.get().getSpannedScope().resolve(qualifier.getName(), CDFieldSymbol.KIND);

      TypeHelper typeHelper = new TypeHelper();
      if (typeHelper.isPrimitive(field.get().getType())) {
        qualifier.setType(new CDSimpleReferenceBuilder().name(typeHelper.getWrapperTypeWithoutPackage(field.get().getType().getName())).build());
      } else {
        qualifier.setType(new CDSimpleReferenceBuilder().name(field.get().getType().getName()).build());
      }
    } else if (qualifier.getTypeOpt().isPresent()) {
      qualifier.setName("key");
    }
  }

}
