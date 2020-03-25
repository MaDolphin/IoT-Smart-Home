/* (c) https://github.com/MontiCore/monticore */


package common.util;

import com.google.common.collect.Lists;

import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethodBuilder;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

import static de.monticore.types.types._ast.TypesNodeFactory.createASTQualifiedName;
import static de.monticore.types.types._ast.TypesNodeFactory.createASTVoidType;

/**
 * This builder is only a decorator for the underlying builder of CDMethods. The
 * decoration is concerned with using default values for optional fields, if
 * nothing is given.
 *
 */
public class CDMethodBuilder extends ASTCDMethodBuilder implements
    ModifierModifiable<CDMethodBuilder> {

  private ConcreteModifierDelegate modDelegator = new ConcreteModifierDelegate();

  public CDMethodBuilder Public() {
    this.modifier = modDelegator.Public(this.modifier);
    return this;
  }

  public CDMethodBuilder Private() {
    this.modifier = modDelegator.Private(this.modifier);
    return this;
  }

  public CDMethodBuilder Protected() {
    this.modifier = modDelegator.Protected(this.modifier);
    return this;
  }

  public CDMethodBuilder Static() {
    this.modifier = modDelegator.Static(this.modifier);
    return this;
  }

  public CDMethodBuilder Final() {
    this.modifier = modDelegator.Final(this.modifier);
    return this;
  }

  public CDMethodBuilder Package() {
    this.modifier = modDelegator.Package(this.modifier);
    return this;
  }

  @Override public CDMethodBuilder Abstract() {
    this.modifier = modDelegator.Abstract(this.modifier);
    return this;
  }

  public CDMethodBuilder modifier(ASTModifier modifier) {
    this.modifier = modifier;
    return this;
  }

  public CDMethodBuilder exceptions(String exception) {
    if (this.exceptions == null) {
      this.exceptions = Lists.newArrayList();
    }
    this.exceptions.add(createASTQualifiedName(Lists.newArrayList(exception)));
    return this;
  }

  public CDMethodBuilder addParameter(ASTType type, String name) {
    if (this.cDParameters == null) {
      this.cDParameters = Lists.newArrayList();
    }
    this.cDParameters.add(CD4AnalysisMill.cDParameterBuilder().setType(type).setName(name)
        .build());
    return this;
  }

  public CDMethodBuilder addParameter(String type, String name) {
    return addParameter(new CDSimpleReferenceBuilder().name(type).build(), name);
  }

  public CDMethodBuilder returnType(String type) {
    return (CDMethodBuilder) setReturnType(new CDSimpleReferenceBuilder().name(
        type).build());
  }
  
  public CDMethodBuilder addException(String exception) {
    if (this.exceptions == null) {
      this.exceptions = Lists.newArrayList();
    }
    this.exceptions.add(CD4AnalysisMill.qualifiedNameBuilder().setPartList(TypesHelper.createListFromDotSeparatedString(exception))
        .build());
    return this;
   
  }

  public CDMethodBuilder name(String name) {
    return (CDMethodBuilder) setName(name);
  }

  // use default values if not given
  public ASTCDMethod build() {
    if (this.modifier == null) {
      Public();
    }

    if (this.returnType == null) {
      this.returnType = createASTVoidType();

    }

    if (this.cDParameters == null) {
      this.cDParameters = Lists.newArrayList();
    }

    if (this.exceptions == null) {
      this.exceptions = Lists.newArrayList();
    }
    ASTCDMethod method = super.build();
    init();
    return method;
  }

  private void init() {
    this.modifier = null;
    this.returnType = null;
    this.name = null;
    this.cDParameters = null;
    this.exceptions = null;
  }
}
