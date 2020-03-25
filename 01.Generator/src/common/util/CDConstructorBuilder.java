/* (c) https://github.com/MontiCore/monticore */


package common.util;

import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructorBuilder;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

/**
 * This builder is only a decorator for the underlying builder of CDConstructor.
 * The decoration is concerned with using default values for optional fields, if
 * nothing is given.
 *
 */
public class CDConstructorBuilder extends ASTCDConstructorBuilder implements
    ModifierModifiable<CDConstructorBuilder> {

  private ConcreteModifierDelegate modDelegator = new ConcreteModifierDelegate();

  public CDConstructorBuilder Public() {
    this.modifier = modDelegator.Public(this.modifier);
    return this;
  }

  public CDConstructorBuilder Private() {
    this.modifier = modDelegator.Private(this.modifier);
    return this;
  }

  public CDConstructorBuilder Protected() {
    this.modifier = modDelegator.Protected(this.modifier);
    return this;
  }

  public CDConstructorBuilder Static() {
    this.modifier = modDelegator.Static(this.modifier);
    return this;
  }

  public CDConstructorBuilder Final() {
    this.modifier = modDelegator.Final(this.modifier);
    return this;
  }

  @Override
  public CDConstructorBuilder Package() {
    this.modifier = modDelegator.Package(this.modifier);
    return this;
  }

  public CDConstructorBuilder addParameter(ASTType type, String name) {
    this.cDParameters.add(CD4AnalysisMill.cDParameterBuilder().setType(type).setName(name)
        .build());
    return this;
  }

  public CDConstructorBuilder addParameter(String type, String name) {
    addParameter(new CDSimpleReferenceBuilder().name(type).build(), name);
    return this;
  }

  @Override public CDConstructorBuilder Abstract() {
    this.modifier = modDelegator.Abstract(this.modifier);
    return this;
  }

  // use default values if not given
  public ASTCDConstructor build() {
    if (this.modifier == null) {
      Public();
    }
    return super.build();
  }
}
