/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package backend.commands;

import backend.common.CoreTemplate;
import backend.dtos.AggregateTrafo;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import static common.util.CompilationUnit.SUBPACKAGE_PROPERTY;
import static common.util.TransformationUtils.setProperty;

public abstract class TypeAdapterTrafo extends AggregateTrafo {

  protected static final String PACKAGE = TransformationUtils.MACOCO_BASE
          + "." + TransformationUtils.MARSHALLING_PACKAGE;

  protected String superClassName;

  public TypeAdapterTrafo(String className, String superClassName) {
    super(PACKAGE, className);
    this.superClassName = superClassName;
  }

  @Override
  protected ASTCDClass createClass(String packageName, String className) {
    if (generateTOP && TransformationUtils
            .existsHandwrittenFile(className, packageName, handcodePath.get(),
                    TransformationUtils.JAVA_FILE_EXTENSION)) {
      className += TransformationUtils.TOP_EXTENSION;
    }

    // Generate class
    String superName = "TypeAdapterResolver<" + superClassName + ">";
    ASTCDClass clazz = new CDClassBuilder().Public().superclass(superName)
            .setName(className).build();
    getOutputAst().addCDClass(clazz);

    setProperty(clazz, SUBPACKAGE_PROPERTY, TransformationUtils.MARSHALLING_PACKAGE);

    // Generate constructor
    createConstructor(clazz, clazz.getName());

    // Generate attributes
    ASTCDAttribute attr = new CDAttributeBuilder().Static()
        .type(TransformationUtils.removeTOPExtensionIfExistent(clazz.getName()))
            .setName("INSTANCE")
            .build();
    clazz.addCDAttribute(attr);

    // Generate additional methods
    createInitMethod(clazz);
    createGetFactoryMethod(clazz);
    createGetMethod(clazz);

    return clazz;
  }

  protected void createGetFactoryMethod(ASTCDClass clazz) {
    String returnType = "RuntimeTypeAdapterFactory<" + superClassName + ">";
    ASTCDMethod method = new CDMethodBuilder().Public().name("getFactory")
            .returnType(returnType).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new StringHookPoint("{return getFactory(" + superClassName + ".class);}"));
    clazz.addCDMethod(method);
  }

  protected abstract void createInitMethod(ASTCDClass clazz);


  private void createGetMethod(ASTCDClass clazz) {
    ASTCDMethod method = new CDMethodBuilder().Public().Static().name("getInstance")
        .returnType(TransformationUtils.removeTOPExtensionIfExistent(clazz.getName())).build();
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
            new TemplateHookPoint("backend.commands.TypeAdapterGet", clazz));
    clazz.addCDMethod(method);
  }

  protected void createConstructor(ASTCDClass clazz, String className) {
    ASTCDConstructor constr = new CDConstructorBuilder().Protected().setName(className).build();
    clazz.addCDConstructor(constr);
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), constr,
            new TemplateHookPoint("backend.commands.TypeAdapterConstructor"));
  }

}
