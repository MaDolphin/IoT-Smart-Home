/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package backend.data.dataclass.accessmethods;

import backend.common.CoreTemplate;
import com.google.common.collect.Lists;
import common.DexTransformation;
import common.util.*;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.List;
import java.util.Optional;

/**
 * @formatter:off A transformation to add access methods for each attribute. By default the attribute is defined to be
 * private and the added access methods are public.
 * @formatter:on
 */
public class AccessMethodTrafo extends DexTransformation {

  private TypeHelper typeHelper = new TypeHelper();

  /* Main method to start transformation */
  @Override
  protected void transform() {

    for (ASTCDClass clazz : getAst().getCDClassList()) {
      List<ASTCDAttribute> removeAttr = Lists.newArrayList();
      List<ASTCDAttribute> attrToAdd = Lists.newArrayList();
      boolean hasDerivedAttributes = false;

      for (ASTCDAttribute attr : clazz.getCDAttributeList()) {
        String attrName = attr.getName();

        // handle derived attribute
        if (attr.getModifierOpt().isPresent()
            && attr.getModifierOpt().get().isDerived()) {

          // add get method and mark attribute to be removed because it is derived
          clazz.getCDMethodList().addAll(createDerivedGet(clazz, attr));
          removeAttr.add(attr);
          hasDerivedAttributes = true;
        }
        else {
          clazz.getCDMethodList().addAll(createDefaultGet(attr, attrName));
          clazz.getCDMethodList().addAll(createDefaultSet(attr, attrName));

          // if attr is final add an additional attribute
          if (attr.getModifierOpt().isPresent() && attr.getModifier().isFinal()) {
            attrToAdd.add(handleFinalAttr(attr, "$" + attrName + "initialized"));
            attr.getModifier().setFinal(false);
          }
          if (!attr.getName().equals("id")) {
            setPrivateModifier(attr);
          }
        }

        // Change type of optional attributes
        if (typeHelper.isOptional(attr.getType())) {
          Optional<ASTSimpleReferenceType> simpleType = typeHelper
              .getFirstTypeArgumentOfGenericType(attr.getType(), "Optional");
          if (simpleType.isPresent()) {
            ASTSimpleReferenceType type = new CDSimpleReferenceBuilder()
                .name(TypesPrinter.printSimpleReferenceType(simpleType.get())).build();
            attr.setType(type);
          }
        }
      }

      clazz.getCDAttributeList().removeAll(removeAttr);
      clazz.getCDAttributeList().addAll(attrToAdd);
      if (hasDerivedAttributes) {
        ASTModifier modifier = clazz.getModifierOpt().isPresent() ?
            clazz.getModifier() : CD4AnalysisNodeFactory.createASTModifier();
        modifier.setAbstract(true);
        clazz.setModifier(modifier);
      }
    }
  }

  /* Create default set methods */
  private List<ASTCDMethod> createDefaultSet(ASTCDAttribute attr, String attrName) {
    List<ASTCDMethod> createdMethods = Lists.newArrayList();

    // add setter
    if ((attr.getModifierOpt().isPresent() && !attr.getModifier().isFinal())
        || !attr.getModifierOpt().isPresent()) {

      CDMethodBuilder builder = new CDMethodBuilder()
          .addParameter((ASTType) attr.getType().deepClone(), "o");
      if (attr.getName().equals("id")) {
        builder.Public();
      }
      else {
        builder.modifier(TransformationUtils.getMethodModifier(attr));
      }
      ASTCDMethod setMethod = builder.name(GetterSetterHelper.getPlainSetter(attr)).build();

      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), setMethod,
          new TemplateHookPoint("backend.data.dataclass.accessmethods.Set",
              attr.getType(), attrName,
              "o", this.typeHelper.isOptional(attr.getType())));

      createdMethods.add(setMethod);

      // check if the attribute is static
      if (attr.getModifierOpt().isPresent() && attr.getModifier().isStatic()) {
        setMethod.getModifier().setStatic(true);
      }
    }
    // handle final attributes
    else {
      // add set method
      ASTCDMethod setMethod = new CDMethodBuilder()
          .addParameter((ASTType) attr.getType().deepClone(), "o")// attrName
          .name(GetterSetterHelper.getPlainSetter(attr))
          .modifier(TransformationUtils.getMethodModifier(attr)).build();

      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(),
          setMethod,
          new TemplateHookPoint("backend.data.dataclass.accessmethods.FinalSet", "$"
              + attrName + "initialized", attrName, "o"));

      createdMethods.add(setMethod);

      // check if the attribute is static
      if (attr.getModifierOpt().isPresent() && attr.getModifier().isStatic()) {
        setMethod.getModifier().setStatic(true);
      }
    }

    if (attr.getModifierOpt().isPresent() && (attr.getModifier().isProtected()
        || attr.getModifier().isPrivate())) {
      ASTCDMethod rawSetMethod = new CDMethodBuilder().Final()
          .addParameter((ASTType) attr.getType().deepClone(), "o").Public()
          .name(GetterSetterHelper.getSetter(attr)).build();

      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(),
          rawSetMethod,
          new StringHookPoint("{ " + GetterSetterHelper.getPlainSetter(attr) + "(o);}"));
      createdMethods.add(rawSetMethod);
    }

    return createdMethods;
  }

  /* create default get methods */
  private List<ASTCDMethod> createDefaultGet(ASTCDAttribute attr, String attrName) {
    List<ASTCDMethod> createdMethods = Lists.newArrayList();

    // add getter
    CDMethodBuilder builder = new CDMethodBuilder().name(GetterSetterHelper.getPlainGetter(attr));
    if (attr.getName().equals("id")) {
      builder.Public();
    }
    else {
      builder.modifier(TransformationUtils.getMethodModifier(attr));
    }

    ASTCDMethod getMethod = builder.setReturnType((ASTReturnType) attr.getType().deepClone())
        .build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), getMethod,
        new TemplateHookPoint("backend.data.dataclass.accessmethods.Get", attrName,
            this.typeHelper.isOptional(attr.getType())));

    createdMethods.add(getMethod);

    if (getMethod.getModifier().isProtected() || getMethod.getModifier().isPrivate()) {
      // add getter
      ASTCDMethod rawGetMethod = new CDMethodBuilder().name(GetterSetterHelper.getGetter(attr))
          .Public()
          .setReturnType((ASTReturnType) attr.getType().deepClone()).build();

      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), rawGetMethod,
          new StringHookPoint("{ return " + GetterSetterHelper.getPlainGetter(attr) + "();}"));
      createdMethods.add(rawGetMethod);
    }
    return createdMethods;
  }

  /* create get method for derived attributes */
  private List<ASTCDMethod> createDerivedGet(ASTCDClass clazz, ASTCDAttribute attr) {
    List<ASTCDMethod> createdMethods = Lists.newArrayList();

    // add getter
    ASTCDMethod getMethod = new CDMethodBuilder()
        .name(GetterSetterHelper.getPlainGetter(attr))
        .modifier(TransformationUtils.getMethodModifier(attr)).Abstract()
        .setReturnType((ASTReturnType) attr.getType().deepClone()).build();

    getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), getMethod,
        new StringHookPoint(";"));

    createdMethods.add(getMethod);

    if (getMethod.getModifier().isProtected() || getMethod.getModifier().isPrivate()) {

      // add getter
      ASTCDMethod rawGetMethod = new CDMethodBuilder().Public()
          .name(GetterSetterHelper.getGetter(attr))
          .setReturnType((ASTReturnType) attr.getType().deepClone()).build();

      getGlex().replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), rawGetMethod,
          new StringHookPoint("{ return " + GetterSetterHelper.getPlainGetter(attr) + "();}"));
      createdMethods.add(rawGetMethod);
    }
    return createdMethods;
  }

  /**
   * Handles final attributes by add adding a new attribute to keep track of the original final
   * attribute, i.e. if a value is set to the attribute, then the new attribute is set to true such
   * that the value cannot be changed again.
   *
   * @param attr          the attribute to handle
   * @param finalAttrName the name of the final attribute
   */
  private ASTCDAttribute handleFinalAttr(ASTCDAttribute attr, String finalAttrName) {

    // add attribute to handle final
    CDAttributeBuilder finalAttrBuilder = (CDAttributeBuilder) new CDAttributeBuilder()
        .setName(finalAttrName).setType(new CDSimpleReferenceBuilder().name("boolean").build());

    if (attr.getModifierOpt().isPresent() && attr.getModifier().isStatic()) {
      finalAttrBuilder.Static();
    }

    ASTCDAttribute finalAttr = finalAttrBuilder.build();
    getGlex().replaceTemplate(CoreTemplate.CLASS_ATTRIBUTE_VALUE.toString(), finalAttr,
        new StringHookPoint("=false;"));

    return finalAttr;
  }

  /**
   * Sets the modifier of an attribute to private
   *
   * @param attr the currently regarded attribute
   */
  private void setPrivateModifier(ASTCDAttribute attr) {
    ASTModifier modifier;

    if (!attr.getModifierOpt().isPresent()) {
      modifier = CD4AnalysisNodeFactory.createASTModifier();
      attr.setModifier(modifier);
    }

    // make attribute private
    attr.getModifierOpt().get().setPrivate(true);
    attr.getModifierOpt().get().setPublic(false);
    attr.getModifierOpt().get().setProtected(false);
  }

}
