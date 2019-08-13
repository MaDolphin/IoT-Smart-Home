/* (c) https://github.com/MontiCore/monticore */


package common.util;

import com.google.common.collect.Lists;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

public class CDClassBuilder extends ASTCDClassBuilder implements
    ModifierModifiable<CDClassBuilder>, CompilationUnit<CDClassBuilder> {

  private ConcreteModifierDelegate modDelegator = new ConcreteModifierDelegate();

  private String packageName = "";

  private Collection<String> imports = new LinkedHashSet<>();

  @Override
  public CDClassBuilder Public() {
    this.modifier = Optional.of(modDelegator.Public(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDClassBuilder Private() {
    this.modifier = Optional.of(modDelegator.Private(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDClassBuilder Protected() {
    this.modifier = Optional.of(modDelegator.Protected(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDClassBuilder Static() {
    this.modifier = Optional.of(modDelegator.Static(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDClassBuilder Final() {
    this.modifier = Optional.of(modDelegator.Final(this.modifier.orElse(null)));
    return this;
  }

  @Override
  public CDClassBuilder Package() {
    this.modifier = Optional.of(modDelegator.Package(this.modifier.orElse(null)));
    return this;
  }

  @Override public CDClassBuilder Abstract() {
    this.modifier = Optional.of(modDelegator.Abstract(this.modifier.orElse(null)));
    return this;
  }

  public CDClassBuilder superclass(String name) {
    setSuperclass(new CDSimpleReferenceBuilder().name(name).build());
    return this;
  }

  public CDClassBuilder interfaces(String interfaceName) {
    this.interfaces.add(new CDSimpleReferenceBuilder().name(interfaceName).build());
    return this;
  }

  @Override
  public CDClassBuilder subpackage(String packageName) {
    this.packageName = packageName;
    return this;
  }

  @Override
  public CDClassBuilder additionalImport(String imports) {
    this.imports.add(imports);
    return this;
  }

  @Override
  public CDClassBuilder imports(Collection<String> imports) {
    this.imports = imports;
    return this;
  }

  @Override
  public ASTCDClass build() {
    if (this.modifier == null) {
      Public();
    }
    ASTCDClass clazz = super.build();
    TransformationUtils.setProperty(clazz, IMPORTS_PROPERTY, this.imports);
    TransformationUtils.setProperty(clazz, SUBPACKAGE_PROPERTY, this.packageName);
    return clazz;
  }

  public void setAnnotation(String name){//}, String value){
    ASTCDStereotypeBuilder b = CD4AnalysisMill.cDStereotypeBuilder();
    ASTCDStereoValueBuilder bc = CD4AnalysisMill.cDStereoValueBuilder();
    bc.setName(name);
    b.setValueList(Lists.newArrayList(bc.build())).build();
  }
}
