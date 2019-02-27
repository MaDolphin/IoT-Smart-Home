/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package backend.data.test;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.CreateTrafo;
import common.util.*;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDConstructor;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;

import static common.util.TransformationUtils.addAnnos;

public class TestClassCreator extends CreateTrafo {
  private TypeHelper typeHelper = new TypeHelper();

  public TestClassCreator() {
    super(TestClassManager.getTestConfiguration().get("DataTest"));
  }

  @Override
  protected List<ASTCDAttribute> createStaticAttributes(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    ArrayList<ASTCDAttribute> attributes = Lists.newArrayList();
    ASTCDAttribute attribute = new CDAttributeBuilder().Private().type(clazz.getName())
        .setName("clazz").build();
    attributes.add(attribute);
    return attributes;
  }

  private ASTCDMethod createInitMethod(String clazz) {
    ASTCDMethod method;
    method = new CDMethodBuilder().Public().returnType("void").exceptions("Exception")
        .name("init").build();
    addAnnos(method.getModifier(), "@Before");
    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), method,
        new TemplateHookPoint("backend.data.test.init", clazz));
    return method;
  }

  @Override
  protected List<ASTCDMethod> createMethods(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    ArrayList<ASTCDMethod> methods = Lists.newArrayList();
    methods.add(createInitMethod(clazz.getName()));
    addMethodsForAttributes(methods, clazz);
    return methods;
  }

  @Override
  protected List<ASTCDAttribute> createAttributes(ASTCDClass handledClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    return super.createAttributes(handledClass, clazz, typeSymbol);
  }

  private void addMethodsForAttributes(ArrayList<ASTCDMethod> methods, ASTCDClass clazz) {
    List<CDFieldSymbol> fields = symbolTable.get().getVisibleNotDerivedAttributesInHierarchy(clazz.getName());
    for (CDFieldSymbol field : fields) {
      if ((new TypeHelper()).isCollection(field.getType().getName())) {
        ASTCDMethod meth = new CDMethodBuilder().Public()
            .name("test" + TransformationUtils.capitalize(field.getName()))
            .returnType("void").build();
        addAnnos(meth.getModifier(), "@Test");
        getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), meth,
            new TemplateHookPoint("backend.data.test.testListAttribute", field));
        methods.add(meth);
      }
      else {
        ASTCDMethod meth = new CDMethodBuilder().Public()
            .name("test" + TransformationUtils.capitalize(field.getName()))
            .returnType("void").build();
        addAnnos(meth.getModifier(), "@Test");
        getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), meth,
            new TemplateHookPoint("backend.data.test.testAttribute", field,
                typeHelper.getValueForType(field.getType().getName())));
        methods.add(meth);
      }
    }
  }

  @Override
  protected List<String> getImports(CDTypeSymbol typeSymbol) {
    List<String> imports = new ArrayList<>();
    imports.add("de.se_rwth.commons.logging.Log");
    imports.add("java.util.List");
    imports.add("java.util.Optional");
    imports.add("org.junit.Test");
    imports.add("org.junit.Assert");
    imports.add("org.junit.Before");
    imports.add("java.time.ZonedDateTime");
    imports.add("java.time.ZoneId");

    imports.add("static org.junit.Assert.assertEquals");
    return imports;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected List<ASTCDConstructor> createConstructors(ASTCDClass addedClass, ASTCDClass clazz,
      CDTypeSymbol typeSymbol) {
    ArrayList constructors = Lists.newArrayList();
    constructors.add(new CDConstructorBuilder().Public().setName(addedClass.getName()).build());
    return constructors;
  }

}
