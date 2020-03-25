/* (c) https://github.com/MontiCore/monticore */

package backend.coretemplates.association.derived;

import com.google.common.collect.Lists;
import backend.common.CoreTemplate;
import backend.coretemplates.association.AssociationStrategy;
import backend.coretemplates.association.ordinary.DefaultAssociationStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;

import java.util.List;
import java.util.Optional;

/**
 * Strategy to handle derived attributes. By default only get methods are
 * generated.
 *
 */
public class DerivedAssociationStrategy implements AssociationStrategy {

  private DefaultAssociationStrategy def;

  public DerivedAssociationStrategy(CDAssociationSymbol symbol,
                                    GlobalExtensionManagement glex) {
    this.def = new DefaultAssociationStrategy(symbol, glex);
    this.def.setHookPoint(new TemplateHookPoint(
        CoreTemplate.THROW_DERIVED_NOTIMPLEMENTED_EXCEPTION.toString(), ""));
  }

  @Override
  public Optional<ASTCDAttribute> getAssociationAttribute() {
    return Optional.empty();
  }

  @Override
  public List<ASTCDMethod> getAddMethods() {
    return Lists.newArrayList();
  }

  @Override
  public List<ASTCDMethod> getGetMethods() {
    // add throws declarations
    return this.def.getGetMethods();
  }

  @Override
  public List<ASTCDMethod> getRemoveMethods() {
    return Lists.newArrayList();
  }

  @Override
  public List<ASTCDMethod> getCommonMethods() {
    return this.def.getCommonMethods();
  }

  @Override
  public void setHookPoint(HookPoint hp) {
    this.def.setHookPoint(hp);
  }

  @Override
  public List<ASTCDMethod> getRawAddMethods() {
    return this.def.getRawAddMethods();
  }

  @Override
  public List<ASTCDMethod> getRawRemoveMethods() {
    return this.def.getRawRemoveMethods();
  }

  @Override
  public void setAddMethodCall(String callMethod) {
    this.def.setAddMethodCall(callMethod);
  }

  @Override
  public void setRemoveMethodCall(String callMethod) {
    this.def.setRemoveMethodCall(callMethod);
  }
}
