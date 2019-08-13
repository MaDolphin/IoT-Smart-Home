/* (c) https://github.com/MontiCore/monticore */

package frontend.data;

import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;
import frontend.common.FrontendTransformationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static common.util.CompilationUnit.*;

public class ViewModelInterfaceCreator extends CreateTrafo {

  private static final String prefix = "I";

  public static final String SUBPACKAGE = "viewmodels";

  public static final String FILEEXTENION = "viewmodel";

  public static final String VIEWMODEL = "ViewModel";

  public ViewModelInterfaceCreator(String suffix) {
    super(VIEWMODEL, prefix);
  }

  @Override
  protected Optional<ASTCDClass> getOrCreateExtendedClass(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    return Optional.empty();
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDInterface extendedInterface,
      ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = new ArrayList<>();
    //  methods.addAll(getGetterAndSetter(domainClass, typeSymbol));
    methods.addAll(getGetterAndSetterForAssocs(domainClass, typeSymbol));
    return methods;
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();

    imports.add(
        "{IViewModel} from '../../../src/app/shared/architecture/data/viewmodels/viewmodel'");
    imports.add("{ Observable } from 'rxjs/Observable';");

    // Import model interface
    imports.addAll(
        FrontendTransformationUtils.getImportsForInterface(typeSymbol.getName(), handcodePath,
            ModelInterfaceCreator.FILEEXTENION,
            ModelInterfaceCreator.SUBPACKAGE, ""));

    // Import view model interfaces of target assoc-types
    for (CDAssociationSymbol association : symbolTable.get()
        .getAllNonDerivedAssociationsForClass(typeSymbol.getName())) {
      String targetType = association.getTargetType().getName();
      imports.addAll(
          FrontendTransformationUtils.getImportsForInterface(targetType, handcodePath,
              FILEEXTENION, SUBPACKAGE, "ViewModel"));
    }
    return imports;
  }

  @Override
  // This trafo creates an additional interface for the existing domain interface
  protected Optional<ASTCDInterface> getOrCreateExtendedInterface(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    String interfaceName = prefix + typeSymbol.getName() + suffix;
    ASTCDInterface addedInterface = new CDInterfaceBuilder().setName(interfaceName).build();
    ASTSimpleReferenceType superInterf1 = new CDSimpleReferenceBuilder()
        .name("IViewModel<I" + typeSymbol.getName() + ">").build();
    ASTSimpleReferenceType superInterf2 = new CDSimpleReferenceBuilder()
        .name("I" + typeSymbol.getName()).build();
    addedInterface.setInterfaceList(Lists.newArrayList(superInterf1, superInterf2));
    TransformationUtils.setProperty(addedInterface, SUBPACKAGE_PROPERTY, SUBPACKAGE);
    TransformationUtils.setProperty(addedInterface, FILEEXTENSION_PROPERTY, FILEEXTENION);
    TransformationUtils.setProperty(addedInterface, FILENAME_PROPERTY,
        (prefix + typeSymbol.getName()).toLowerCase());
    TransformationUtils.setProperty(addedInterface, HWC_PROPERTY,
        Joiners.DOT.join(typeSymbol.getName().toLowerCase(), FILEEXTENION));
    addedInterface.setSymbol(typeSymbol);
    getOutputAst().getCDInterfaceList().add(addedInterface);
    return Optional.of(addedInterface);
  }

  //----------- PRIVATE  METHODS -------------------------------------

  private List<ASTCDMethod> getGetterAndSetter(ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = new ArrayList<>();
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleAttributes(typeSymbol.getName());
    fields.addAll(symbolTable.get().getDerivedAttributes(typeSymbol.getName()));

    // get all visible attributes
    for (CDFieldSymbol field : fields) {
      Optional<CDTypeSymbol> cdType = symbolTable.get()
          .resolve(field.getType().getStringRepresentation());
      String typeName = "";
      if (cdType.isPresent() && cdType.get().isEnum()) {
        typeName = FrontendTransformationUtils.NUMERIC_FRONTEND;
      }
      else {
        typeName = FrontendTransformationUtils
            .getFrontendType(field.getType().getStringRepresentation());
      }

      // add getter
      ASTCDMethod getMethod = new CDMethodBuilder()
          .name(GetterSetterHelper.getPlainGetter(field))
          .returnType(typeName).build();
      // add getter
      ASTCDMethod setMethod = new CDMethodBuilder()
          .addParameter(typeName, field.getName())
          .name(GetterSetterHelper.getPlainSetter(field))
          .returnType("void").build();
      methods.add(getMethod);
      methods.add(setMethod);
    }
    return methods;
  }

  private List<ASTCDMethod> getGetterAndSetterForAssocs(ASTCDClass domainClass,
      CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = Lists.newLinkedList();
    // handle associations
    for (CDAssociationSymbol association : symbolTable.get()
        .getAllNonDerivedAssociationsForClass(domainClass.getName())) {
      String targetType = association.getTargetType().getName();
      String name = TransformationUtils.capitalize(TransformationUtils
          .removeTrailingS(
              CDAssociationUtil.getAssociationName(association)));
      String typeName = "";
      if (CDAssociationUtil.isMultiple(association)) {
        typeName = "Observable<I" + targetType + "ViewModel[]>";
        methods.add(new CDMethodBuilder()
            .name("get" + name + "s")
            .returnType(typeName).build());
        // add getter
        methods.add(new CDMethodBuilder()
            .addParameter("I" + targetType + "ViewModel", targetType.toLowerCase())
            .name("add" + name)
            .returnType("void").build());
        methods.add(new CDMethodBuilder()
            .addParameter("I" + targetType + "ViewModel", targetType.toLowerCase())
            .name("contains" + name)
            .returnType("boolean").build());
        methods.add(new CDMethodBuilder()
            .addParameter("I" + targetType + "ViewModel", targetType.toLowerCase())
            .name("remove" + name)
            .returnType("void").build());
      }
      else {
        typeName = "Observable<I" + targetType + "ViewModel | null>";
        methods.add(new CDMethodBuilder()
            .name("get" + name)
            .returnType(typeName).build());
        // add getter
        methods.add(new CDMethodBuilder()
            .addParameter("I" + targetType + "ViewModel", targetType.toLowerCase())
            .name("set" + name)
            .returnType("void").build());
      }
    }
    return methods;

  }

}
