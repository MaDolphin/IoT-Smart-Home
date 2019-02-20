/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package frontend.data;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.util.*;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import frontend.common.FrontendTransformationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static frontend.common.FrontendTransformationUtils.createJsonMemberStereotype;


public class CommandOfModelCreator extends CommandCreator {

  public static final String FILEEXTENSION = "";

  private TypeHelper typeHelper = new TypeHelper();

  public CommandOfModelCreator() {
    super(FILEEXTENSION ,FILEEXTENSION);
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (CDFieldSymbol field: typeSymbol.getFields()) {
      String paramName = TransformationUtils.uncapitalize(field.getName());
      String type = field.getType().getStringRepresentation();
      boolean required = true;
      boolean isList = false;
      if (typeHelper.isGenericOptional(type)) {
        required = false;
        paramName += "?";
      }
      if (typeHelper.isGenericList(type)) {
        isList = true;
      }
      if (field.getType().isEnum()) {
        type = FrontendTransformationUtils.STRING_FRONTEND;
      } else {
        type = FrontendTransformationUtils.getFrontendType(type, Optional.of(field.getType()), symbolTable.get());
      }
      ASTCDAttribute attr = new CDAttributeBuilder().Public()
              .type(type).setName(paramName).build();
      attr.getModifier().setStereotype(
              createJsonMemberStereotype(attr.getName(), type, required, isList));
      attributes.add(attr);
      addImport(extendedClass, field);
    }

    return attributes;
  }
  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    CDConstructorBuilder constructor = new CDConstructorBuilder();
    List<String> attributes = Lists.newArrayList();
    for (CDFieldSymbol field: typeSymbol.getFields()) {
      String paramName = TransformationUtils.uncapitalize(field.getName());
      String type = field.getType().getStringRepresentation();
      if (field.getType().isEnum()) {
        type = FrontendTransformationUtils.STRING_FRONTEND;
      } else {
        type = FrontendTransformationUtils.getFrontendType(type, Optional.of(field.getType()), symbolTable.get());
      }
      constructor.addParameter(type, paramName + "?");

      attributes.add(paramName);
      addImport(extendedClass, field);
    }
    ASTCDConstructor ast = constructor.Package()
            .setName(extendedClass.getName()).build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), ast,
            new TemplateHookPoint("frontend.data.CommandConstructorParam", extendedClass.getName(), attributes));
    return Lists.newArrayList(ast);
  }

  @Override
  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass,
                            CDTypeSymbol typeSymbol) {
    getImports(typeSymbol).forEach(i -> TransformationUtils
            .addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, i));
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("{JsonMember, JsonObject} from '@upe/typedjson'");
    imports.add(
            "{CommandDTO} from '../../../src/app/shared/architecture/command/rte/command.dto'");
    return imports;
  }

  protected void addImport(ASTCDClass clazz, CDFieldSymbol field) {
    String fieldType = field.getType().getStringRepresentation();
    if (!field.getType().getActualTypeArguments().isEmpty()) {
      fieldType = field.getType().getActualTypeArguments().get(0).getType().getName();
    }
    if (symbolTable.get().isTypeDefinedInModel(fieldType)) {
      String type = fieldType + DTOCreator.DTO;
      String importStr = (FrontendTransformationUtils
              .getImportCheckHWC(type, handcodePath, DTOCreator.FILEEXTENION,
                      TransformationUtils.DTOS_PACKAGE, Optional.of(fieldType.toLowerCase())));
      TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY, importStr);
    }
  }

}
