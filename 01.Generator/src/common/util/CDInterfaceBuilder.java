/* (c) https://github.com/MontiCore/monticore */


package common.util;

import java.util.Collection;
import java.util.Optional;

import com.google.common.collect.Sets;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterfaceBuilder;

public class CDInterfaceBuilder extends ASTCDInterfaceBuilder implements
    ModifierModifiable<CDInterfaceBuilder>, CompilationUnit<CDInterfaceBuilder> {

  private ConcreteModifierDelegate modDelegator = new ConcreteModifierDelegate();

  private String packageName = "";

  private Collection<String> imports = Sets.newHashSet();

  @Override
  public CDInterfaceBuilder Public() {
    this.modifier = Optional.of(modDelegator.Public(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDInterfaceBuilder Private() {
    this.modifier = Optional.of(modDelegator.Private(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDInterfaceBuilder Protected() {
    this.modifier = Optional.of(modDelegator.Protected(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDInterfaceBuilder Static() {
    this.modifier = Optional.of(modDelegator.Static(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDInterfaceBuilder Final() {
    this.modifier = Optional.of(modDelegator.Final(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDInterfaceBuilder Package() {
    this.modifier = Optional.of(modDelegator.Package(this.modifier.orElse(null)));
    return this;
  }

  @Override public CDInterfaceBuilder Abstract() {
    this.modifier = Optional.of(modDelegator.Abstract(this.modifier.orElse(null)));
    return this;
  }

  public CDInterfaceBuilder interfaces(String interfaceName) {
    this.interfaces.add(new CDSimpleReferenceBuilder().name(interfaceName)
        .build());
    return this;
  }

  @Override
  public CDInterfaceBuilder subpackage(String packageName) {
    this.packageName = packageName;
    return this;
  }

  @Override
  public CDInterfaceBuilder additionalImport(String imports) {
    this.imports.add(imports);
    return this;
  }

  @Override
  public CDInterfaceBuilder imports(Collection<String> imports) {
    this.imports = imports;
    return this;
  }

  @Override
  public ASTCDInterface build() {
    ASTCDInterface interf = super.build();
    TransformationUtils.setProperty(interf, IMPORTS_PROPERTY, this.imports);
    TransformationUtils.setProperty(interf, SUBPACKAGE_PROPERTY, this.packageName);
    return interf;
  }

}
