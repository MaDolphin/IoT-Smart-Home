package backend.data.validator;

import common.CDSymbolTable;
import common.util.CDAssociationUtil;
import common.util.CDSimpleReferenceBuilder;
import de.montigem.ocl._ast.ASTErrorExpression;
import de.montigem.ocl._ast.ASTShortErrorExpression;
import de.montigem.ocl._ast.ASTWarningExpression;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.oclexpressions._ast.*;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;

import java.util.*;

public class OCL2JavaPrettyPrinter extends OCLExpressionsPrettyPrinter {

  private CDSymbolTable symTab;

  private String domainName;

  private List<ASTCDParameter> parameters;

  private List<ASTCDParameter> parametersForAssocs;

  // for loops and if statements do not need to be wrapped by an if block with an following
  // error or warning message
  private boolean isStatement;

  public OCL2JavaPrettyPrinter(IndentPrinter printer, CDSymbolTable symTab, String domainName) {
    super(printer);
    this.symTab = symTab;
    this.domainName = domainName;
    this.parameters = new LinkedList<>();
    this.parametersForAssocs = new LinkedList<>();
    this.isStatement = false;
  }

  public String prettyprint(ASTExpression node) {
    String result = super.prettyprint(node);
    if (!isStatement && !isMessageExpression(node)) {
      result = wrapWithIfBlock(result);
    }
    return result;
  }

  private boolean isMessageExpression(ASTExpression node) {
    return node instanceof ASTErrorExpression || node instanceof ASTWarningExpression || node instanceof ASTShortErrorExpression;
  }

  public List<ASTCDParameter> getParameters() {
    return parameters;
  }

  public List<ASTCDParameter> getParametersForAssocs() {
    return parametersForAssocs;
  }

  @Override
  public void handle(ASTForallExpr node) {
    this.isStatement = true;
    getPrinter().print("for (");
    node.getInExpr(0).accept(this.getRealThis());
    getPrinter().print(") {");

    handleStatementExpression(node.getExpression());

    getPrinter().print("}");
  }

  @Override
  public void handle(ASTOCLQualifiedPrimary node) {
    List<String> names = new ArrayList<>(node.getNameList());
    if (node.isPresentPostfixQualification()) {
      names.remove(names.size() - 1);
    }
    this.checkNamesForParameters(names);
    super.handle(node);
  }

  private void checkNamesForParameters(List<String> names) {
    for (String name : names) {
      Optional<CDFieldSymbol> field = this.symTab.getVisibleAttributesInHierarchy(domainName).stream()
          .filter(a -> a.getName().equals(name))
          .findFirst();
      if (field.isPresent()) {
        ASTType type = new CDSimpleReferenceBuilder().name(field.get().getType().getStringRepresentation()).build();
        ASTCDParameter parameter = CD4AnalysisMill.cDParameterBuilder()
            .setName(field.get().getName())
            .setType(type).build();
        if (!this.parameters.stream().anyMatch(p -> p.getName().equals(parameter.getName()))) {
          this.parameters.add(parameter);
        }
      }

      Optional<CDAssociationSymbol> assoc = this.symTab.getAllAssociationsForType(domainName).stream()
          .filter(a -> a.getTargetRole().isPresent() && a.getTargetRole().get().equals(name))
          .findFirst();

      if (assoc.isPresent()) {
        ASTCDParameter parameter = CDAssociationUtil.associationToCDParameter(assoc.get());
        if (!this.parametersForAssocs.stream().anyMatch(p -> p.getName().equals(parameter.getName()))) {
          this.parametersForAssocs.add(parameter);
        }
      }
    }
  }

  @Override
  public void handle(ASTErrorExpression node) {
    this.setMessage(MessageFormatter.formatMessageJava(node.getMessage()));
  }

  @Override
  public void handle(ASTWarningExpression node) {
    // do nothing
  }

  @Override
  public void handle(ASTShortErrorExpression node) {
    // do nothing
  }

}
