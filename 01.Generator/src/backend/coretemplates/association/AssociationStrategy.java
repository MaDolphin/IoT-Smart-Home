/* (c) https://github.com/MontiCore/monticore */

package backend.coretemplates.association;

import java.util.List;
import java.util.Optional;

import de.monticore.generating.templateengine.HookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

/**
 * A general interface to define a strategy for handling associations
 *
 */
public interface AssociationStrategy {

  Optional<ASTCDAttribute> getAssociationAttribute();

  List<ASTCDMethod> getAddMethods();

  List<ASTCDMethod> getRawAddMethods();

  List<ASTCDMethod> getGetMethods();

  List<ASTCDMethod> getRemoveMethods();

  List<ASTCDMethod> getRawRemoveMethods();

  List<ASTCDMethod> getCommonMethods();

  void setHookPoint(HookPoint hp);

  void setAddMethodCall(String callMethod);

  void setRemoveMethodCall(String callMethod);
}
