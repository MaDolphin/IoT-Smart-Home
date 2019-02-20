/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package backend.dtos;

import backend.commands.CommandCreator;
import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.CDSymbolTable;
import common.util.CDAttributeBuilder;
import common.util.CompilationUnit;
import common.util.TransformationUtils;
import common.util.TypeHelper;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandOfModelCreator extends CommandCreator {

  public CommandOfModelCreator() {
    super();
  }

  protected TypeHelper typeHelper = new TypeHelper();

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass extendedClass, ASTCDClass domainClass, CDTypeSymbol typeSymbol) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (CDFieldSymbol field: typeSymbol.getFields()) {
      String typeName = TransformationUtils
              .getStringRepresentationForCdOrDtoType(field.getType(), symbolTable.get());
      if (typeHelper.isOptional(field.getType())) {
        // Here we really need optionals
        typeName = "Optional<" + typeName + ">";
      }

      ASTCDAttribute identifier = new CDAttributeBuilder().Protected().type(typeName).setName(field.getName())
              .build();
      attributes.add(identifier);
      addImport(extendedClass, field);

    }

    return attributes;
  }

  @Override
  protected void setTemplate(ASTCDMethod method, ASTCDClass domainClass, String className) {
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("backend.dtos.CmdModel", domainClass.getName(), className));
  }

  @Override
  protected List<String> additionalImports(CDTypeSymbol typeSymbol) {
    return Lists.newArrayList();
  }

  private void addImport(ASTCDClass clazz, CDFieldSymbol field) {
    String type = field.getType().getStringRepresentation();
    TypeHelper helper = new TypeHelper();
    if (helper.isGenericList(type)) {
      type = helper.getFirstTypeArgumentOfList(type);
    } else if (helper.isGenericOptional(type)) {
      type = helper.getFirstTypeArgumentOfOptional(type);
    }
    Optional<CDTypeSymbol> typeSymbol = symbolTable.get().getTypeSymbolIfDefinedInModel(type);
    if (typeSymbol.isPresent()) {
      TransformationUtils.addPropertyValue(clazz, CompilationUnit.IMPORTS_PROPERTY,
              TransformationUtils.getQualifiedNameForDomainOrDtoType(typeSymbol.get().getPackageName(),
                      type, false));
    }
  }


}
