/* (c) https://github.com/MontiCore/monticore */

package backend.data.dataclass;

import backend.common.DefaultVisibilitySetter;
import common.DexTransformation;
import common.util.CDClassBuilder;
import common.util.TransformationUtils;
import common.util.TypeHelper;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class AnnotationTrafo extends DexTransformation {

  @Override
  protected void transform() {
    checkArgument(symbolTable.isPresent());

    for (ASTCDClass clazz : getAst().getCDClassList()) {
      // Add annotations to class
      addClassAnnos(clazz);

      // Add annotations to attributes
      addAttributeAnnos(clazz);
    }
  }

  /**
   * Add specific annotations to the class
   *
   * @param clazz class for transformation
   */
  private void addClassAnnos(ASTCDClass clazz) {
    // Add annotation Audited
    TransformationUtils.addPropertyValue(clazz, CDClassBuilder.IMPORTS_PROPERTY,
        "org.hibernate.envers.Audited");
    ASTCDStereotype type = CD4AnalysisMill.cDStereotypeBuilder().build();
    ASTCDStereoValue value = CD4AnalysisMill.cDStereoValueBuilder().setName("@Audited").build();
    type.getValueList().add(value);

    // Add annotation Import
    TransformationUtils.addPropertyValue(clazz, CDClassBuilder.IMPORTS_PROPERTY,
        "javax.persistence.*");
    TransformationUtils.addPropertyValue(clazz, CDClassBuilder.IMPORTS_PROPERTY,
        "org.hibernate.annotations.LazyCollection");
    TransformationUtils.addPropertyValue(clazz, CDClassBuilder.IMPORTS_PROPERTY,
        "org.hibernate.annotations.LazyCollectionOption");
    TransformationUtils.addPropertyValue(clazz, CDClassBuilder.IMPORTS_PROPERTY,
        "org.hibernate.annotations.Fetch");
    TransformationUtils.addPropertyValue(clazz, CDClassBuilder.IMPORTS_PROPERTY,
        "org.hibernate.annotations.FetchMode");

    String packageName = TransformationUtils.CLASSES_PACKAGE + "."
        + clazz.getName().toLowerCase();
    if (isTopClassExistend(generateTOP, clazz.getName(), packageName,
        handcodePath)) { // if this is a TOP class
      value = CD4AnalysisMill.cDStereoValueBuilder().setName("@MappedSuperclass").build();
    }
    else {
      value = CD4AnalysisMill.cDStereoValueBuilder().setName("@Entity").build();
    }
    type.getValueList().add(value);

    // TODO SVa: add inheritance strategy

    if (!clazz.getModifierOpt().isPresent()) {
      clazz.setModifier(CD4AnalysisMill.modifierBuilder().build());
    }
    clazz.getModifier().setStereotype(type);
  }

  /**
   * Add annotations to attributes
   *
   * @param clazz class with attributes
   */
  private void addAttributeAnnos(ASTCDClass clazz) {
    for (ASTCDAttribute attribute : clazz.getCDAttributeList()) {
      if (attribute.getType() instanceof ASTSimpleReferenceType) {
        List<String> typeNames = ((ASTSimpleReferenceType) attribute.getType()).getNameList();
        if (attribute.getName().equals("id")) {
          ASTCDStereotype stereotype;
          ASTModifier modifier;
          if (attribute.getModifierOpt().isPresent()) {
            modifier = attribute.getModifier();
            if (modifier.getStereotypeOpt().isPresent()) {
              stereotype = modifier.getStereotype();
            }
            else {
              stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
              modifier.setStereotype(stereotype);
            }
          }
          else {
            stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
            modifier = CD4AnalysisMill.modifierBuilder().setStereotype(stereotype).build();
          }

          getIdModifier().forEach(value -> stereotype.getValueList()
              .add(CD4AnalysisMill.cDStereoValueBuilder().setName(value).setValue(TransformationUtils.ATTRIBUTE_ANNOATION).build()));
          modifier = DefaultVisibilitySetter.createModifierIfNecessary(Optional.of(modifier));
          attribute.setModifier(modifier);
        }
        else if (attribute.getName().equals("labels")){
          ASTCDStereotype stereotype;
          ASTModifier modifier;
          if (attribute.getModifierOpt().isPresent()) {
            modifier = attribute.getModifier();
            if (modifier.getStereotypeOpt().isPresent()) {
              stereotype = modifier.getStereotype();
            }
            else {
              stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
              modifier.setStereotype(stereotype);
            }
          }
          else {
            stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
            modifier = CD4AnalysisMill.modifierBuilder().setStereotype(stereotype).build();
          }

          getLabelModifier().forEach(value -> stereotype.getValueList()
                  .add(CD4AnalysisMill.cDStereoValueBuilder().setName(value).setValue(TransformationUtils.ATTRIBUTE_ANNOATION).build()));
          modifier = DefaultVisibilitySetter.createModifierIfNecessary(Optional.of(modifier));
          attribute.setModifier(modifier);
        }
        else if (TransformationUtils.isDTOType(symbolTable.get(), attribute.printType())) {
          ASTCDStereoValue value = CD4AnalysisMill.cDStereoValueBuilder().setName("@OneToOne(cascade = CascadeType.ALL, orphanRemoval=true, fetch = FetchType.EAGER)").setValue(TransformationUtils.ATTRIBUTE_ANNOATION).build();
          if (!attribute.getModifier().getStereotypeOpt().isPresent()) {
            ASTCDStereotype stereoType = CD4AnalysisMill.cDStereotypeBuilder().build();
            attribute.getModifier().setStereotype(stereoType);
          }
          attribute.getModifier().getStereotype().getValueList().add(value);
        }
        else if (TransformationUtils.getColumDefinitionAnnotation(attribute).isPresent()) {
          ASTModifier modifier;
          ASTCDStereotype stereotype;
          if (attribute.getModifierOpt().isPresent()) {
             modifier = attribute.getModifier();
            if (modifier.getStereotypeOpt().isPresent()) {
              stereotype = modifier.getStereotype();
            } else {
              stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
              modifier.setStereotype(stereotype);
            }
          }
          else {
            stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
            modifier = CD4AnalysisMill.modifierBuilder().setStereotype(stereotype).build();
          }
          getDBColumnDefinitionAnnotation(attribute).map(value -> stereotype.getValueList()
                  .add(CD4AnalysisMill.cDStereoValueBuilder().setName(value).setValue(TransformationUtils.ATTRIBUTE_ANNOATION).build()));
          modifier = DefaultVisibilitySetter.createModifierIfNecessary(Optional.of(modifier));
          attribute.setModifier(modifier);
        }
        else if (TransformationUtils.getDBColumnAnnotation(attribute).isPresent()) {
          ASTModifier modifier;
          ASTCDStereotype stereotype;
          if (attribute.getModifierOpt().isPresent()) {
            modifier = attribute.getModifier();
            if (modifier.getStereotypeOpt().isPresent()) {
              stereotype = modifier.getStereotype();
            } else {
              stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
              modifier.setStereotype(stereotype);
            }
          }
          else {
            stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
            modifier = CD4AnalysisMill.modifierBuilder().setStereotype(stereotype).build();
          }
          getDBColumnUniqueAnnotation(attribute).map(value -> stereotype.getValueList()
                  .add(CD4AnalysisMill.cDStereoValueBuilder().setName(value).setValue(TransformationUtils.ATTRIBUTE_ANNOATION).build()));
          modifier = DefaultVisibilitySetter.createModifierIfNecessary(Optional.of(modifier));
          attribute.setModifier(modifier);
        }
        else if (typeNames.size() > 0 && (new TypeHelper()).isCollection(typeNames.get(0))) {
          ASTCDStereotype type = CD4AnalysisMill.cDStereotypeBuilder().build();
          getLabelModifier().forEach(value -> type.getValueList()
              .add(CD4AnalysisMill.cDStereoValueBuilder().setName(value).setValue(TransformationUtils.ATTRIBUTE_ANNOATION).build()));
          if (attribute.getModifierOpt().isPresent()) {
            attribute.getModifier().setStereotype(type);
          }
        }
      }
    }
  }

  /**
   * @return modifiers for the id attributes
   */
  private static List<String> getIdModifier() {
    return Arrays.asList("@Id", "@GeneratedValue(strategy = GenerationType.TABLE)");
  }

  private static List<String> getLabelModifier() {
    return Arrays.asList("@LazyCollection(LazyCollectionOption.FALSE)", "@ElementCollection");
  }

  private static Optional<String> getDBColumnDefinitionAnnotation(ASTCDAttribute attr) {
    return TransformationUtils.getColumDefinitionAnnotation(attr).map(s -> "@Column(columnDefinition  = \"" + s + "\")");
  }

  private static Optional<String> getDBColumnUniqueAnnotation(ASTCDAttribute attr) {
    return TransformationUtils.getDBColumnAnnotation(attr).map(s -> "@Column(" + s + ")");
  }

  private static List<String> getDBColumnDefinitionModifier() {
    return Arrays.asList("@Column(columnDefinition  = \"TEXT\")");
  }

  /**
   * @return modifiers for any collection attributes
   */
  private static List<String> getCollectionModifier() {
    return Collections.singletonList("@ElementCollection");
  }
}
