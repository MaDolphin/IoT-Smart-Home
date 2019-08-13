/* (c) https://github.com/MontiCore/monticore */

package backend.data.validator;

import de.montigem.ocl._ast.ASTMessage;
import de.montigem.ocl._ast.ASTMessageFragment;
import de.monticore.prettyprint.IndentPrinter;
import frontend.data.validator.OCLTypeScriptExpressionsPrettyPrinter;

public class MessageFormatter {

  private MessageFormatter() {
  }

  public static String formatMessageJava(ASTMessage message) {
    OCLExpressionsPrettyPrinter prettyPrinter = new OCLExpressionsPrettyPrinter(new IndentPrinter());
    return formatMessage(message, prettyPrinter);
  }

  public static String formatMessageTypeScript(ASTMessage message) {
    OCLTypeScriptExpressionsPrettyPrinter prettyPrinter = new OCLTypeScriptExpressionsPrettyPrinter(new IndentPrinter());
    return formatMessage(message, prettyPrinter);
  }

  private static String formatMessage(ASTMessage message, OCLExpressionsPrettyPrinter prettyPrinter) {
    StringBuilder formattedMessage = new StringBuilder();
    formattedMessage.append("\"").append(message.getStringLiteral().getValue()).append("\"");
    for (ASTMessageFragment messageFragment : message.getMessageFragmentList()) {
      formattedMessage
              .append("+")
              .append(prettyPrinter.prettyprint(messageFragment.getExpression()))
              .append("+")
              .append("\"")
              .append(messageFragment.getStringLiteral().getValue())
              .append("\"");
    }
    return formattedMessage.toString();
  }



}
