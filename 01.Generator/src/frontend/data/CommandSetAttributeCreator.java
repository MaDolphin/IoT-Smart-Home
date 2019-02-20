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
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import frontend.common.FrontendTransformationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static common.util.CompilationUnit.*;
import static common.util.TransformationUtils.addPropertyValue;
import static common.util.TransformationUtils.isDTOType;
import static frontend.common.FrontendTransformationUtils.*;

public class CommandSetAttributeCreator extends CommandCreator {

  public static final String FILEEXTENSION = "set";

  private TypeHelper typeHelper = new TypeHelper();

  public CommandSetAttributeCreator() {
    super(FILEEXTENSION, TransformationUtils.GETALL_CMD);
  }

  protected List<ASTCDClass> getOrCreateExtendedClassList(ASTCDClass domainClass,
                                                          CDTypeSymbol typeSymbol) {
    String typeName = typeSymbol.getName();
    List<ASTCDClass> classList = Lists.newArrayList();

    List<ASTCDAttribute> attrList = symbolTable.get().getVisibleNotDerivedAttributesInHierarchyAsCDAttribute(domainClass.getName());
    Map<ASTCDAttribute, CDAssociationSymbol> assocAttrList = symbolTable.get().getVisibleAssociationsInHierarchy(domainClass);
    attrList.addAll(assocAttrList.keySet());

    for (ASTCDAttribute attr : attrList) {
      ASTCDClass addedClass = new CDClassBuilder().superclass("CommandDTO")
              .setName(typeName + TransformationUtils.SET_CMD + TransformationUtils.capitalize(attr.getName())).build();
      TransformationUtils.setProperty(addedClass, SUBPACKAGE_PROPERTY, SUBPACKAGE);
      TransformationUtils.setProperty(addedClass, FILEEXTENSION_PROPERTY, FILEEXTENSION + attr.getName().toLowerCase());
      TransformationUtils.setProperty(addedClass, FILENAME_PROPERTY, typeName.toLowerCase());
      addedClass.addAllCDAttributes(createAttributes(addedClass, domainClass, typeSymbol, attr));
      addedClass.addAllCDConstructors(createConstructors(addedClass, domainClass, typeSymbol, attr));
      getOutputAst().getCDClassList().add(addedClass);
      classList.add(addedClass);
    }
    return classList;
  }

  @Override
  protected void addImports(ASTCDClass extendedClass, ASTCDClass domainClass,
                            CDTypeSymbol typeSymbol) {
    getImports(typeSymbol).forEach(i -> addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, i));
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("{JsonMember, JsonObject} from '@upe/typedjson'");
    imports.add(
            "{CommandDTO} from '../../../src/app/shared/architecture/command/rte/command.dto'");
    return imports;
  }

  protected List<ASTCDAttribute> createAttributes(ASTCDClass extendedClass, ASTCDClass domainClass,
                                                  CDTypeSymbol typeSymbol, ASTCDAttribute origAttr) {
    List<ASTCDAttribute> attrList = new ArrayList<ASTCDAttribute>();
    // Create attribute obejctId
    ASTCDAttribute attr = new CDAttributeBuilder().Public()
            .type(NUMERIC_FRONTEND).setName("objectId").build();
    attr.getModifier().setStereotype(
            createJsonMemberStereotype(attr.getName(), NUMERIC_FRONTEND, true));
    attrList.add(attr);

    // Create attribute
    String type = typeHelper.printType(origAttr.getType());
    String frontendType;
    boolean required = !typeHelper.isGenericOptional(type);
    boolean isList = typeHelper.isGenericList(type);
    Optional<CDTypeSymbol> attrTypeSymbol = domainClass.getEnclosingScope().resolve(typeHelper.printType(origAttr.getType()), CDTypeSymbol.KIND);
    if (attrTypeSymbol.isPresent() && attrTypeSymbol.get().isEnum()) {
      frontendType = FrontendTransformationUtils.STRING_FRONTEND;
    } else {
      frontendType = FrontendTransformationUtils.getFrontendType(type, Optional.of(typeSymbol), symbolTable.get());
    }
    String fileName;
    if (isList) {
      fileName = typeHelper.getTypeFromTypeWithSuffix(typeHelper.getTypeFromTypeWithSuffix(frontendType, ARRAY), DTOCreator.DTO);
    } else {
      fileName = typeHelper.getTypeFromTypeWithSuffix(frontendType, DTOCreator.DTO);
    }
    if (isDTOType(symbolTable.get(), fileName)) {
      addPropertyValue(extendedClass, CompilationUnit.IMPORTS_PROPERTY, FrontendTransformationUtils
              .getImportCheckHWC(typeHelper.getTypeFromTypeWithSuffix(frontendType, ARRAY), handcodePath, "dto",
                      "dtos", Optional.of(fileName.toLowerCase())));
    }
    String paramName = TransformationUtils.uncapitalize(origAttr.getName());
    attr = new CDAttributeBuilder().Public()
            .type(frontendType).setName(paramName).build();
    attr.getModifier().setStereotype(
            createJsonMemberStereotype(attr.getName(), frontendType, required, isList));
    attrList.add(attr);

    return attrList;
  }

  protected List<ASTCDConstructor> createConstructors(ASTCDClass extendedClass, ASTCDClass domainClass,
                                                      CDTypeSymbol typeSymbol, ASTCDAttribute origAttr) {
    String paramName = TransformationUtils.uncapitalize(origAttr.getName());
    String paramType;
    Optional<CDTypeSymbol> s = domainClass.getEnclosingScope().resolve(typeHelper.printType(origAttr.getType()), CDTypeSymbol.KIND);
    if (s.isPresent() && s.get().isEnum()) {
      paramType = FrontendTransformationUtils.STRING_FRONTEND;
    } else {
      paramType = FrontendTransformationUtils.getFrontendType(typeHelper.printType(origAttr.getType()),
              Optional.of(typeSymbol), symbolTable.get());
    }
    ASTCDConstructor constructor = new CDConstructorBuilder()
            .addParameter(NUMERIC_FRONTEND, "objectId?")
            .addParameter(paramType, paramName + "?")
            .Package()
            .setName(extendedClass.getName()).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constructor,
            new TemplateHookPoint("frontend.data.CommandConstructorParam",
                    extendedClass.getName(), Lists.newArrayList("objectId", paramName)));
    return Lists.newArrayList(constructor);
  }

  @Override
  protected void extendDomainClass(ASTCDClass domainClass) {
    CDTypeSymbol typeSymbol = symbolTable.get().resolve(domainClass.getName()).get();
    for (ASTCDClass extendedClass : getOrCreateExtendedClassList(domainClass, typeSymbol)) {
      addImports(extendedClass, domainClass, typeSymbol);
      addTypeAnnotations(extendedClass);
    }
  }

}
