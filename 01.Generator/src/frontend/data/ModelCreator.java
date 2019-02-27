/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package frontend.data;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.ExtendTrafo;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static common.util.CompilationUnit.FILEEXTENSION_PROPERTY;
import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;
import static common.util.TransformationUtils.addPropertyValue;
import static common.util.TransformationUtils.isDTOType;
import static frontend.common.FrontendTransformationUtils.*;

public class ModelCreator extends ExtendTrafo {

  public static final String SUBPACKAGE = "models";

  public static final String FILEEXTENSION = "model";

  private TypeHelper typeHelper = new TypeHelper();

  @Override
  protected void replaceAttributes(ASTCDClass extendedClass, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    attributes.addAll(getAttributesForFields(domainClass, typeSymbol));
    attributes.addAll(getAttributesForAssocs(domainClass, typeSymbol));
    for (ASTCDAttribute attr: attributes) {
      String typeName = typeHelper.printType(attr.getType());
      String fileName = typeHelper.getTypeFromTypeWithSuffix(typeName, DTOCreator.DTO);
      if (isDTOType(symbolTable.get(), fileName)) {
        addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY,
                getImportCheckHWC(typeName, handcodePath, "dto",
                        "dtos", Optional.of(fileName.toLowerCase())));
      }
    }
    extendedClass.setCDAttributeList(attributes);
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("{IModel, Model} from '../../../src/app/shared/architecture/data/models/model'");
    imports.add("{JsonMember, JsonObject} from '@upe/typedjson'");

    String fileName = Joiners.DOT.join(typeSymbol.getName().toLowerCase(), FILEEXTENSION);
    imports.add("{I" + typeSymbol.getName() + "} from './i" + fileName + "'");
    if (TransformationUtils
        .existsHandwrittenDotFile(fileName, SUBPACKAGE, handcodePath.get(),
            TransformationUtils.DOT_TYPESCRIPT_FILE_EXTENSION)) {
      imports.add(
          "{" + typeSymbol.getName() + "} from '../../../src/app/shared/architecture/data/models/"
              + typeSymbol.getName().toLowerCase() + "." + FILEEXTENSION + "'");
    }
    return imports;
  }

  @Override
  protected Optional<ASTCDStereotype> getStereotype(ASTCDType type) {
    // Add annotation JsonObject()
    ASTCDStereotype stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
    ASTCDStereoValue value = CD4AnalysisMill.cDStereoValueBuilder().setName("@JsonObject()").build();
    stereotype.getValueList().add(value);
    return Optional.of(stereotype);
  }

  @Override
  protected List<String> getInterfaceNames(CDTypeSymbol typeSymbol) {
    return Lists.newArrayList("I" + typeSymbol.getName());
  }

  @Override
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    ASTSimpleReferenceType superClass = new CDSimpleReferenceBuilder()
        .name("Model<" + typeSymbol.getName() + ">").build();
    domainClass.setSuperclass(superClass);
    domainClass.setModifier(CD4AnalysisMill.modifierBuilder().build());
    TransformationUtils.setProperty(domainClass, SUBPACKAGE_PROPERTY, SUBPACKAGE);
    TransformationUtils.setProperty(domainClass, FILEEXTENSION_PROPERTY, FILEEXTENSION);
    return Optional.of(domainClass);
  }

  @Override
  protected Optional<ASTCDEnum> getOrCreateExtendedEnum(ASTCDEnum domainEnum,
      CDTypeSymbol typeSymbol) {
    TransformationUtils.setProperty(domainEnum, SUBPACKAGE_PROPERTY, SUBPACKAGE);
    TransformationUtils.setProperty(domainEnum, FILEEXTENSION_PROPERTY, FILEEXTENSION);
    return Optional.of(domainEnum);
  }

  @Override
  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    checkArgument(symbolTable.isPresent());
    // Clear all CD imports
    this.getOutputAstRoot().setImportStatementList(new ArrayList<>());

    getImports(typeSymbol).forEach(i -> TransformationUtils
        .addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, i));

  }

  //----------- PRIVATE  METHODS -------------------------------------

  private List<ASTCDAttribute> getAttributesForFields(ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {

    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleAttributesInHierarchy(clazz.getName());

    // get all visible parameters
    for (CDFieldSymbol field : fields) {
      String typeName = field.getType().getStringRepresentation();
      Optional<CDTypeSymbol> cdType = symbolTable.get()
          .resolve(typeName);
      String frontendName = "";
      if (cdType.isPresent() && cdType.get().isEnum()) {
        frontendName = STRING_FRONTEND;
      }
      else {
        frontendName = getFrontendType(typeName, cdType, symbolTable.get());
      }
      boolean isRequired = isRequired(typeName) && !field.isDerived();
      boolean isList = typeHelper.isGenericList(typeName);

      ASTCDAttribute attr = new CDAttributeBuilder().Public()
          .type(frontendName).setName(field.getName()).build();

      Optional<ASTValue> value = TransformationUtils.getAttributeValue(field);
      if (value.isPresent()) {
        attr.setValue(value.get());
      }
      else {
        String defaultValue = getDefaultValueForType(frontendName);
        getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
            new StringHookPoint(
                "= " + defaultValue + ";"));
      }
      attr.getModifier().setStereotype(createJsonMemberStereotype(field.getName(), frontendName, isRequired, isList));

      attrList.add(attr);
    }
    return attrList;
  }

  private List<ASTCDAttribute> getAttributesForAssocs(ASTCDClass clazz, CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attrList = Lists.newLinkedList();
    // handle associations
    for (CDAssociationSymbol association : symbolTable.get()
        .getAllNonDerivedAssociationsForClass(clazz.getName())) {

      CDAttributeBuilder builder = new CDAttributeBuilder().Public();
      String typeName = "";
      String name = "";
      if (CDAssociationUtil.isMultiple(association)) {
        typeName = NUMERICARRAY_FRONTEND;
        name = TransformationUtils
            .removeTrailingS(
                TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association)))
            + "Ids";
      }
      else {
        typeName = NUMERIC_FRONTEND;
        name = TransformationUtils
            .removeTrailingS(
                TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association)))
            + "Id";
      }
      ASTCDAttribute attr = builder.type(typeName).setName(name).build();
      String defaultValue = getDefaultValueForType(typeName);
      getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), attr,
          new StringHookPoint(
              "= " + defaultValue + ";"));

      attr.getModifier().setStereotype(createJsonMemberStereotype(name, typeName, false));
      attrList.add(attr);
    }
    return attrList;

  }

}
