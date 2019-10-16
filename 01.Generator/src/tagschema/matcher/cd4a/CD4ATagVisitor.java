/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package tagschema.matcher.cd4a;

import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.se_rwth.commons.Joiners;

public class CD4ATagVisitor extends CD4ATagVisitorTOP {
  public CD4ATagVisitor(String identifierPath) {
    super(identifierPath);
  }

  @Override
  public void handle(ASTCDCompilationUnit node) {
    this.getRealThis().visit(node);
    String name = Joiners.DOT.join(node.getPackageList());
    if (this.match(name)) {
      this.matches.add(node);
    }
    else {
      this.getRealThis().traverse(node);
    }
    this.getRealThis().endVisit(node);
  }

  @Override
  public void handle(ASTCDDefinition node) {
    this.getRealThis().visit(node);
    if (this.match(node.getName())) {
      this.matches.add(node);
    }
    else {
      this.getRealThis().traverse(node);
    }
    this.getRealThis().endVisit(node);
  }

  @Override
  public void handle(ASTCDClass node) {
    this.getRealThis().visit(node);
    if (this.match(node.getName())) {
      this.matches.add(node);
    }
    else {
      this.getRealThis().traverse(node);
    }
    this.getRealThis().endVisit(node);
  }

  @Override
  public void handle(ASTCDAttribute node) {
    this.getRealThis().visit(node);
    if (this.match(node.getName())) {
      this.matches.add(node);
    }
    else {
      this.getRealThis().traverse(node);
    }
    this.getRealThis().endVisit(node);
  }

  @Override
  public void handle(ASTCDAssociation node) {
    this.getRealThis().visit(node);
    boolean found = false;
    if (node.getLeftToRightSymbol().isPresent()) {
      String name = node.getLeftToRightSymbol().get().getDerivedName();
      if (this.match(name)) {
        this.matches.add(node);
        found = true;
      }
    }
    if (node.getRightToLeftSymbol().isPresent()) {
      String name = node.getRightToLeftSymbol().get().getDerivedName();
      if (this.match(name)) {
        this.matches.add(node);
        found = true;
      }
    }
    if (!found) {
      this.getRealThis().traverse(node);
    }
    this.getRealThis().endVisit(node);
  }

}
