/* (c) https://github.com/MontiCore/monticore */

package backend.common;

import common.DexTransformation;
import common.util.CDAttributeBuilder;
import common.util.CDSimpleReferenceBuilder;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class AdditionalAttributeTrafo extends DexTransformation {

  @Override
  public void transform() {
    checkArgument(symbolTable.isPresent());
    // Extend existing domain classes
    for (ASTCDClass domainClass : getAst().getCDClassList()) {
      extendDomainClass(domainClass);
    }
  }

  // Extends the domain class by additional attributes
  private void extendDomainClass(ASTCDClass domainClass) {
    domainClass.getCDAttributeList().addAll(createAttributes(domainClass));
  }

  protected Collection<ASTCDAttribute> createAttributes(ASTCDClass domainClass) {
    List<ASTCDAttribute> attributes = new ArrayList<>();

    if (!domainClass.getSuperclassOpt().isPresent()) {
      ASTCDAttribute labels = new CDAttributeBuilder().Public().setType(CDSimpleReferenceBuilder.CollectionTypes.createList("String")).setName("labels").build();

      getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), labels,
          CDSimpleReferenceBuilder.CollectionTypes.LIST_VALUE);

      attributes.add(labels);
    }

    return attributes;
  }
}
