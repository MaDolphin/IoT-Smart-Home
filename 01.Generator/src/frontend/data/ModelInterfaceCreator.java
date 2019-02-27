/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package frontend.data;

import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Joiners;
import frontend.common.FrontendTransformationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static common.util.CompilationUnit.FILEEXTENSION_PROPERTY;
import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;
import static common.util.TransformationUtils.addPropertyValue;
import static common.util.TransformationUtils.isDTOType;

public class ModelInterfaceCreator extends CreateTrafo {

  private static final String prefix = "I";

  public static final String SUBPACKAGE = "models";

  public static final String FILEEXTENION = "model";

  private TypeHelper typeHelper = new TypeHelper();

  public ModelInterfaceCreator(String suffix) {
    super("", prefix);
  }

  @Override
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Optional.empty();
  }

  @Override
  protected List<ASTCDAttribute> createStaticAttributes(ASTCDInterface extendedInterface,
      ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    attributes.addAll(getAttributesForFields(domainClass, typeSymbol));
    attributes.addAll(getAttributesForAssocs(domainClass, typeSymbol));
    for (ASTCDAttribute attr: attributes) {
      String typeName = typeHelper.printType(attr.getType());
      String fileName = typeHelper.getTypeFromTypeWithSuffix(typeName, DTOCreator.DTO);
      if (isDTOType(symbolTable.get(), fileName)) {
        addPropertyValue(extendedInterface, CompilationUnit.IMPORTS_PROPERTY, FrontendTransformationUtils
                .getImportCheckHWC(typeName, handcodePath, "dto",
                        "dtos", Optional.of(fileName.toLowerCase())));
      }
    }
    return attributes;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    String fileName = Joiners.DOT.join(prefix + typeSymbol.getName().toLowerCase(), "model");
    if (typeSymbol.getSuperClass().isPresent()) {
      imports.add(
          "{I" + typeSymbol.getSuperClass().get().getName()
              + "} from './i"
              + typeSymbol.getSuperClass().get().getName().toLowerCase() + "." + FILEEXTENION
              + "'");
    }
    else {
      imports.add("{IModel} from '../../../src/app/shared/architecture/data/models/model'");
    }
    for (CDTypeSymbolReference superInterface : typeSymbol.getInterfaces()) {
      imports.add(
          "{" + superInterface.getName()
              + "} from '../../../src/app/shared/architecture/data/models/"
              + superInterface.getName().toLowerCase() + "." + FILEEXTENION + "'");
    }
    return imports;
  }

  @Override
  // This trafo creates an additional interface for the existing domain interface
  protected Optional<ASTCDInterface> getOrCreateExtendedInterface(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    String interfaceName = prefix + typeSymbol.getName() + suffix;
    ASTCDInterface addedInterface = new CDInterfaceBuilder().setName(interfaceName).build();
    if (typeSymbol.getSuperClass().isPresent()) {
      ASTSimpleReferenceType superInterf = new CDSimpleReferenceBuilder()
          .name(prefix + typeSymbol.getSuperClass().get().getName()).build();
      addedInterface.setInterfaceList(Lists.newArrayList(superInterf));
    }
    else {
      ASTSimpleReferenceType superInterf = new CDSimpleReferenceBuilder()
          .name("IModel").build();
      addedInterface.setInterfaceList(Lists.newArrayList(superInterf));
    }
    TransformationUtils.setProperty(addedInterface, SUBPACKAGE_PROPERTY, SUBPACKAGE);
    TransformationUtils.setProperty(addedInterface, FILEEXTENSION_PROPERTY, FILEEXTENION);
    addedInterface.setSymbol(typeSymbol);
    getOutputAst().getCDInterfaceList().add(addedInterface);
    return Optional.of(addedInterface);
  }

  //----------- PRIVATE  METHODS -------------------------------------

  private List<ASTCDAttribute> getAttributesForFields(ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attrList = new ArrayList<>();
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleNotDerivedAttributesInHierarchy(clazz.getName());
    fields.addAll(symbolTable.get().getDerivedAttributes(clazz.getName()));
    // get all visible parameters
    for (CDFieldSymbol field : fields) {
      Optional<CDTypeSymbol> cdType = symbolTable.get()
          .resolve(field.getType().getStringRepresentation());
      String typeName = "";
      if (cdType.isPresent() && cdType.get().isEnum()) {
        typeName = FrontendTransformationUtils.STRING_FRONTEND;
      }
      else {
        typeName = FrontendTransformationUtils.getFrontendType(field.getType().getStringRepresentation(), cdType, symbolTable.get());
      }
      boolean isRequired = FrontendTransformationUtils
          .isRequired(field.getType().getStringRepresentation());

      ASTCDAttribute attr = new CDAttributeBuilder()
          .type(typeName).setName(field.getName() + (isRequired ? "" : "?")).build();

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
        typeName = FrontendTransformationUtils.NUMERICARRAY_FRONTEND;
        name = TransformationUtils
            .removeTrailingS(
                TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association)))
            + "Ids";
      }
      else {
        typeName = FrontendTransformationUtils.NUMERIC_FRONTEND;
        name = TransformationUtils
            .removeTrailingS(
                TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(association)))
            + "Id";
      }
      ASTCDAttribute attr = new CDAttributeBuilder()
          .type(typeName).setName(name + "?").build();
      attrList.add(attr);
    }
    return attrList;

  }

}
