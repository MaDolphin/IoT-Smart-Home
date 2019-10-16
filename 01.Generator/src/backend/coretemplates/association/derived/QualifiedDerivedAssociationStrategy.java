/* (c) https://github.com/MontiCore/monticore */

package backend.coretemplates.association.derived;

import backend.coretemplates.association.AssociationStrategy;
import backend.coretemplates.association.qualified.QualifiedAssociationStrategy;
import com.google.common.collect.Lists;
import backend.common.CoreTemplate;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;

import java.util.List;
import java.util.Optional;

public class QualifiedDerivedAssociationStrategy implements AssociationStrategy {
  private QualifiedAssociationStrategy qual;

  public QualifiedDerivedAssociationStrategy(CDAssociationSymbol symbol, GlobalExtensionManagement glex) {
    this.qual = new QualifiedAssociationStrategy(symbol, glex);
    this.qual.setHookPoint(new TemplateHookPoint(CoreTemplate.THROW_DERIVED_NOTIMPLEMENTED_EXCEPTION.toString(), ""));
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
    return this.qual.getGetMethods();
  }

  @Override
  public List<ASTCDMethod> getRemoveMethods() {
    return Lists.newArrayList();
  }

  @Override
  public List<ASTCDMethod> getCommonMethods() {
    return this.qual.getCommonMethods();
  }

  @Override
  public void setHookPoint(HookPoint hp) {
    this.qual.setHookPoint(hp);
  }

  @Override
  public List<ASTCDMethod> getRawAddMethods() {
    return this.qual.getRawAddMethods();
  }

  @Override
  public List<ASTCDMethod> getRawRemoveMethods() {
    return this.qual.getRawRemoveMethods();
  }

  @Override
  public void setAddMethodCall(String callMethod) {
    this.qual.setAddMethodCall(callMethod);
  }

  @Override
  public void setRemoveMethodCall(String callMethod) {
    this.qual.setRemoveMethodCall(callMethod);
  }
}
