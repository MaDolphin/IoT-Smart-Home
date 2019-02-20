package backend.data.validator;

import de.montigem.ocl._visitor.OCLVisitor;
import de.monticore.commonexpressions._ast.*;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.literals._ast.ASTDoubleLiteral;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTStringLiteral;
import de.monticore.oclexpressions._ast.*;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.setexpressions._ast.ASTSetInExpression;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class OCLExpressionsPrettyPrinter implements OCLVisitor {

  protected static final String DOT = ".";

  protected static final String COMMA = ",";

  protected static final String EMPTY_STRING = "";

  protected static final String OPENING_PARENTHESIS = "(";

  protected static final String CLOSING_PARENTHESIS = ")";

  private OCLVisitor realThis;

  private IndentPrinter printer;

  private Optional<String> message;

  public OCLExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    this.realThis = this;
    this.message = Optional.empty();
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  protected void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public String getMessage() {
    return this.message.orElse("\"Invalide Eingabe!\"");
  }

  public void setMessage(String message) {
    this.message = Optional.ofNullable(message);
  }

  public boolean isMessagePresent() {
    return this.message.isPresent();
  }

  @Override
  public OCLVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(OCLVisitor realThis) {
    this.realThis = realThis;
  }

  public String prettyprint(ASTExpression node) {
    getPrinter().clearBuffer();
    node.accept(this.getRealThis());
    return getPrinter().getContent();
  }

  protected void handleStatementExpression(ASTExpression node) {
    IndentPrinter current = getPrinter();
    setPrinter(new IndentPrinter());
    node.accept(this.getRealThis());
    String content = wrapWithIfBlock(getPrinter().getContent());
    setPrinter(current);
    getPrinter().print(content);
  }

  protected String wrapWithIfBlock(String expression) {
    return "if ( !(" + expression + ") ) { constraintFailed = true; }";
  }

    /*-----------------------------------------------------------
                       OCL EXPRESSIONS
     ------------------------------------------------------------*/
  @Override
  public void handle(ASTImpliesExpression node) {
    getPrinter().print("(!(");
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(")||");
    node.getRightExpression().accept(this.getRealThis());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTInExpr node) {
    if (node.isPresentType() && !isMessage) {
      getPrinter().print(TypesPrinter.printType(node.getType()));
    }
    if (node.sizeVarNames() == 1) {
      getPrinter().print(" " + node.getVarName(0));
    }
    else {
      Log.error("Too many var names found", node.get_SourcePositionStart());
    }
    if (node.isPresentExpression()) {
      getPrinter().print(" : ");
      node.getExpression().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(ASTOCLQualifiedPrimary node) {
    getPrinter().print(String.join(DOT, node.getNameList()));
    if (node.isPresentPostfixQualification()) {
      node.getPostfixQualification().accept(this.getRealThis());
    }
    if (node.isPresentOCLQualifiedPrimary()) {
      if (!node.getOCLQualifiedPrimary().isEmptyNames()) {
        getPrinter().print(DOT);
      }
      node.getOCLQualifiedPrimary().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(ASTOCLArgumentQualification node) {
    getPrinter().print("(");
    handle(node.getExpressionList());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTOCLArrayQualification node) {
    for (ASTExpression expression : node.getArgumentsList()) {
      getPrinter().print("[");
      expression.accept(this.getRealThis());
      getPrinter().print("]");
    }
  }

  /*
    special case for messages: transform
     FROM: { String b in belegnummern | b.length() <= 10 }
     TO:    belegnummern.stream().filter(b -> !(b.length() <= 10)).collect(Collectors.toList())
   */

  // this will be inserted for the filter, default is "false"
  protected boolean isMessage = false;
  protected String filterExpression = "false";

  // first, the filterExpression for the filter has to be evaluated
  // then, the full stream-expression can be printed
  @Override
  public void handle(ASTOCLComprehensionExpressionStyle node) {
    isMessage = true;
    node.getOCLComprehensionItem(0).accept(this.getRealThis());
    filterExpression = getPrinter().getContent();
    getPrinter().clearBuffer();
    node.getExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTSetInExpression node) {
    node.getRightExpression().accept(this.getRealThis());  // belegnummern
    getPrinter().print(".stream().filter(");
    node.getLeftExpression().accept(this.getRealThis());   // String b
    getPrinter().print(" -> (" + filterExpression + ")).collect(Collectors.toList())");
  }

  /*-----------------------------------------------------------
                       COMMON EXPRESSIONS
   ------------------------------------------------------------*/

  @Override
  public void handle(ASTMultExpression node) {
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" * ");
    node.getRightExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTDivideExpression node) {
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" / ");
    node.getRightExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTPlusExpression node) {
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" + ");
    node.getRightExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTMinusExpression node) {
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" - ");
    node.getRightExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTLessEqualExpression node) {
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" <= ");
    node.getRightExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTGreaterEqualExpression node) {
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" >= ");
    node.getRightExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTLessThanExpression node) {
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" < ");
    node.getRightExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTGreaterThanExpression node) {
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" > ");
    node.getRightExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTBooleanAndOpExpression node) {
    getPrinter().print("(");
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" && ");
    node.getRightExpression().accept(this.getRealThis());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTBooleanOrOpExpression node) {
    getPrinter().print("(");
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" || ");
    node.getRightExpression().accept(this.getRealThis());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTSimpleAssignmentExpression node) {
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" += ");
    node.getRightExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTEqualsExpression node) {
    getPrinter().print("(");
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" == ");
    node.getRightExpression().accept(this.getRealThis());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTNotEqualsExpression node) {
    getPrinter().print("(");
    node.getLeftExpression().accept(this.getRealThis());
    getPrinter().print(" != ");
    node.getRightExpression().accept(this.getRealThis());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTConditionalExpression node) {
    getPrinter().print("(");
    node.getCondition().accept(this.getRealThis());
    getPrinter().print(") ? (");
    node.getTrueExpression().accept(this.getRealThis());
    getPrinter().print(") : (");
    node.getFalseExpression().accept(this.getRealThis());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTBracketExpression node) {
    getPrinter().print("(");
    node.getExpression().accept(this.getRealThis());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTBooleanNotExpression node) {
    getPrinter().print("~");
    node.getExpression().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTLogicalNotExpression node) {
    getPrinter().print("!(");
    node.getExpression().accept(this.getRealThis());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTCallExpression node) {
    node.getExpression().accept(this.getRealThis());
    node.getArguments().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTArguments node) {
    getPrinter().print("(");
    handle(node.getExpressionList());
    getPrinter().print(")");
  }

  /*-----------------------------------------------------------
                             LITERALS
   ------------------------------------------------------------*/

  @Override
  public void visit(ASTIntLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void visit(ASTDoubleLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void visit(ASTStringLiteral node) {
    getPrinter().print("\"" + node.getSource() + "\"");
  }

  protected void handle(List<ASTExpression> nodes) {
    boolean first = true;
    for (ASTExpression node : nodes) {
      if (first) {
        first = false;
      }
      else {
        getPrinter().print(COMMA);
      }
      node.accept(this.getRealThis());
    }
  }
}
