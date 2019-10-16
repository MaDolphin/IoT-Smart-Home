/* (c) https://github.com/MontiCore/monticore */

package frontend.data.validator;

import backend.data.validator.OCLExpressionsPrettyPrinter;
import de.monticore.oclexpressions._ast.ASTInExpr;
import de.monticore.oclexpressions._ast.ASTOCLQualifiedPrimary;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.setexpressions._ast.ASTSetInExpression;
import de.se_rwth.commons.logging.Log;

public class OCLTypeScriptExpressionsPrettyPrinter extends OCLExpressionsPrettyPrinter {

  private static final String GET = "get";

  private static final String IS_PRESENT = "isPresent";

  private static final String IS_NOT_NULL = "!== null";

  private static final String LENGTH = "length";

  private static final String SIZE = "size";

  private static final String IS_BEFORE = "isBefore";

  private static final String IS_AFTER = "isAfter";

  private static final String PARENTHESIS = "()";

  public OCLTypeScriptExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTInExpr node) {
    if (node.isPresentType() && !isMessage) {
      getPrinter().print("let");
    }
    if (node.sizeVarNames() == 1) {
      getPrinter().print(" " + node.getVarName(0));
    }
    else {
      Log.error("Too many var names found", node.get_SourcePositionStart());
    }
    if (node.isPresentExpression()) {
      getPrinter().print(" of ");
      node.getExpression().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(ASTSetInExpression node) {
    node.getRightExpression().accept(this.getRealThis());  // belegnummern
    getPrinter().print(".filter(");
    node.getLeftExpression().accept(this.getRealThis());   // String b
    getPrinter().print(" => (" + filterExpression + "))");
  }

  @Override
  public void handle(ASTOCLQualifiedPrimary node) {
    IndentPrinter current = getPrinter();
    setPrinter(new IndentPrinter());
    super.handle(node);
    String content = getPrinter().getContent();
    setPrinter(current);
    getPrinter().print(removeJavaLikeExpressions(content));
  }

  private String removeJavaLikeExpressions(String content) {
    if (content.contains(IS_PRESENT + PARENTHESIS)) {
      content = removeJavaLikeExpressions(replaceIsPresentWithNullCheck(content));
    }
    if (content.contains(GET + PARENTHESIS)) {
      content = removeJavaLikeExpressions(removeGet(content));
    }
    if (content.contains(LENGTH + PARENTHESIS)) {
      content = removeJavaLikeExpressions(removeLengthParenthesis(content));
    }
    if (content.contains(SIZE + PARENTHESIS)) {
      content = removeJavaLikeExpressions(replaceSizeWithLength(content));
    }
    if (content.contains(DOT + IS_BEFORE)) {
      content = removeJavaLikeExpressions(replaceZonedDateTimeMethod(content, IS_BEFORE));
    }
    if (content.contains(DOT + IS_AFTER)) {
      content = removeJavaLikeExpressions(replaceZonedDateTimeMethod(content, IS_AFTER));
    }
    return content;
  }

  private String replaceIsPresentWithNullCheck(String content) {
    int begin = content.indexOf(DOT + IS_PRESENT);
    int end = begin + DOT.length() + IS_PRESENT.length() + PARENTHESIS.length();
    StringBuilder builder = new StringBuilder(content);
    builder.delete(begin, end);
    builder.insert(begin, IS_NOT_NULL);
    return builder.toString();
  }

  private String removeGet(String content) {
    int begin = content.indexOf(DOT + GET);
    int end = begin + DOT.length() + GET.length() + PARENTHESIS.length();
    StringBuilder builder = new StringBuilder(content);
    builder.delete(begin, end);
    return builder.toString();
  }

  private String removeLengthParenthesis(String content) {
    int begin = content.indexOf(LENGTH) + LENGTH.length();
    int end = begin + PARENTHESIS.length();
    StringBuilder builder = new StringBuilder(content);
    builder.delete(begin, end);
    return builder.toString();
  }

  private String replaceSizeWithLength(String content) {
    int begin = content.indexOf(SIZE);
    int end = begin + SIZE.length() + PARENTHESIS.length();
    StringBuilder builder = new StringBuilder(content);
    builder.delete(begin, end);
    builder.insert(begin, LENGTH);
    return builder.toString();
  }

  private String replaceZonedDateTimeMethod(String content, String methodName) {
    int begin = content.indexOf(DOT + methodName);
    int end = begin + DOT.length() + methodName.length() + OPENING_PARENTHESIS.length();
    StringBuilder builder = new StringBuilder(content);
    builder.delete(begin, end);
    builder.insert(begin, COMMA);
    builder.insert(0, methodName + OPENING_PARENTHESIS);
    return builder.toString();
  }
}
