package backend.dtos;

import backend.common.CoreTemplate;
import common.util.*;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;

public class DtoForDataclassAggregatesCreator extends DtoForAggregatesCreator {

  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass dtoClass, ASTCDClass clazz,
                                                      CDTypeSymbol type) {
    List<ASTCDConstructor> constructors = super.createConstructors(dtoClass, clazz, type);
    constructors.addAll(getConstructors(dtoClass, type));

    return constructors;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass dtoClass, ASTCDClass clazz,
                                            CDTypeSymbol typeSymbol) {
    List<ASTCDMethod> methods = super.createMethods(dtoClass, clazz, typeSymbol);

    methods.add(createToBuilderMethod(dtoClass, clazz));

    return methods;
  }

  @Override
  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass,
                            CDTypeSymbol typeSymbol) {
    super.addImports(extendedClass, domainClass, typeSymbol);

    TransformationUtils
            .addStarImportForCDType(extendedClass, domainClass.getName(), getAstRoot());
    TransformationUtils.addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, "javax.xml.bind.ValidationException");
  }

  private List<ASTCDConstructor> getConstructors(ASTCDClass dtoClass,
                                                 CDTypeSymbol type) {
    List<ASTCDConstructor> constructors = new ArrayList<>();

    // prepare for constructor
    List<ASTCDParameter> paramList = new ArrayList<>();
    // add all mandatory fields
    String origName = type.getName();
    paramList.add(
            CD4AnalysisMill.cDParameterBuilder().setName(TransformationUtils.uncapitalize(origName))
                    .setType(new CDSimpleReferenceBuilder().name(origName).build()).build());

    List<CDFieldSymbol> fields = symbolTable.get().getVisibleAttributes(origName);
    fields.addAll(symbolTable.get().getDerivedAttributes(origName));

    boolean hasSuperclass = dtoClass.getSuperclassOpt().isPresent() && !TypesPrinter.printType(dtoClass.getSuperclass()).equals("DTO");

    List<ASTCDAttribute> additionalAttributes = new ArrayList<>();

    if (!dtoClass.getSuperclassOpt().isPresent()) {
      ASTCDAttribute attrId = new CDAttributeBuilder().type("long").setName("id").build();
      additionalAttributes.add(attrId);
    }

    // build constructor
    ASTCDConstructor constructor = new CDConstructorBuilder().Public()
            .setName(dtoClass.getName())
            .setCDParameterList(paramList).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
            new TemplateHookPoint("backend.dtos.ConstructorFromDataobject", symbolTable.get(), TransformationUtils.removeTOPExtensionIfExistent(dtoClass.getName()), origName,
                    hasSuperclass, fields, additionalAttributes));
    constructors.add(constructor);
    return constructors;
  }

  private ASTCDMethod createToBuilderMethod(ASTCDClass dtoClass, ASTCDClass clazz) {
    ASTCDMethod toStringMethod = new CDMethodBuilder().Public()
            .name("toBuilder")
            .exceptions("ValidationException")
            .returnType(clazz.getName() + "Builder").build();
    List<CDFieldSymbol> attributes = new ArrayList<>();
    attributes.addAll(symbolTable.get().getInheritedVisibleAttributesInHierarchy(clazz.getName()));
    attributes.addAll(symbolTable.get().getVisibleAttributes(clazz.getName()));

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), toStringMethod,
            new TemplateHookPoint("backend.dtos.ToBuilder", symbolTable.get(), dtoClass.getName(), clazz.getName(),
                    attributes
            ));
    return toStringMethod;
  }

}
