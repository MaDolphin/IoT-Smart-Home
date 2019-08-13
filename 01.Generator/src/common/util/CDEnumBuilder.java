/* (c) https://github.com/MontiCore/monticore */


package common.util;

import com.google.common.collect.Lists;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

public class CDEnumBuilder extends ASTCDEnumBuilder implements CompilationUnit<CDEnumBuilder> {

  private ConcreteModifierDelegate modDelegator = new ConcreteModifierDelegate();

  private String packageName = "";

  private Collection<String> imports = new LinkedHashSet<>();

  public CDEnumBuilder Public() {
    this.modifier = Optional.of(modDelegator.Public(this.modifier.orElse(null)));
    return this;
  }

  public CDEnumBuilder Package() {
    this.modifier = Optional.of(modDelegator.Package(this.modifier.orElse(null)));
    return this;
  }

  public CDEnumBuilder interfaces(String interfaceName) {
    this.interfaces.add(new CDSimpleReferenceBuilder().name(interfaceName).build());
    return this;
  }

  @Override
  public CDEnumBuilder subpackage(String packageName) {
    this.packageName = packageName;
    return this;
  }

  @Override
  public CDEnumBuilder additionalImport(String imports) {
    this.imports.add(imports);
    return this;
  }

  @Override
  public CDEnumBuilder imports(Collection<String> imports) {
    this.imports = imports;
    return this;
  }

  @Override
  public ASTCDEnum build() {
    ASTCDEnum enumeration = super.build();
    TransformationUtils.setProperty(enumeration, IMPORTS_PROPERTY, this.imports);
    TransformationUtils.setProperty(enumeration, SUBPACKAGE_PROPERTY, this.packageName);
    return enumeration;
  }

  public void setAnnotation(String name){//}, String value){
    ASTCDStereotypeBuilder b = CD4AnalysisMill.cDStereotypeBuilder();
    ASTCDStereoValueBuilder bc = CD4AnalysisMill.cDStereoValueBuilder();
    bc.setName(name);
    //bc.value(value);
    b.setValueList(Lists.newArrayList(bc.build())).build();
  }
}
