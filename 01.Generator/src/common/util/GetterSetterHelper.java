/* (c) https://github.com/MontiCore/monticore */

package common.util;

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;

/**
 * TODO: Write me!
 *
 */
public class GetterSetterHelper {

  public final static String GET_PREFIX = "get";

  public final static String GET_PREFIX_BOOLEAN = "is";

  public final static String SET_PREFIX = "set";

  public static final String getPlainSetter(ASTCDAttribute attr) {
    return TransformationUtils.makeCamelCase(SET_PREFIX, getNameSuffix(attr));
  }

  public static final String getPlainSetter(CDFieldSymbol field) {
    return getPlainSetter((ASTCDAttribute) field
        .getAstNode().get());
  }

  public static final String getPlainGetter(CDFieldSymbol field) {
    return getPlainGetter((ASTCDAttribute) field
        .getAstNode().get());
  }

  public static final String getPlainGetter(ASTCDAttribute attr) {
    return TransformationUtils.makeCamelCase(getGetNamePrefix(attr),
        getNameSuffix(attr));
  }

  public static final String getPlainGetter(ASTCDParameter param) {
    return TransformationUtils.makeCamelCase(getGetNamePrefix(param),
        getNameSuffix(param));
  }

  public static final String getGetter(ASTCDAttribute attr) {
    if (attr.getModifier() != null && attr.getModifierOpt().isPresent()
        && (attr.getModifier().isProtected() || attr.getModifier().isPrivate())) {
      return TransformationUtils.makeCamelCase("raw", getPlainGetter(attr));
    }
    return getPlainGetter(attr);
  }

  public static final String getSetter(ASTCDAttribute attr) {
    if (attr.getModifier() != null && attr.getModifierOpt().isPresent()
        && (attr.getModifier().isProtected() || attr.getModifier().isPrivate())) {
      return TransformationUtils.makeCamelCase("raw", getPlainSetter(attr));
    }
    return getPlainSetter(attr);
  }

  public static final String getGetter(CDFieldSymbol attr) {
    return getGetter((ASTCDAttribute) attr.getAstNode().get());
  }

  public static final String getSetter(CDFieldSymbol attr) {
    return getSetter((ASTCDAttribute) attr.getAstNode().get());
  }

  public static ASTCDParameter attributeToCDParameter(CDFieldSymbol symbol) {
    return CD4AnalysisMill.cDParameterBuilder().setName(symbol.getName()).setType(
        new CDSimpleReferenceBuilder().name(symbol.getType().getStringRepresentation()).build())
        .build();
  }

  public static final String getNameSuffix(ASTCDAttribute attr) {
    return getNameSuffix(attr.getType(), attr.getName());
  }

  public static final String getNameSuffix(ASTCDParameter param) {
    return getNameSuffix(param.getType(), param.getName());
  }

  public static final String getNameSuffix(ASTType type, String name) {
    if ("boolean".equals(TypesPrinter.printType(type))) {
      return name.replace("is", "").replace("IS", "")
          .replace("Is", "").replace("iS", "");
    }
    else {
      return name;
    }
  }

  public static final String getGetNamePrefix(ASTCDAttribute attr) {
    return getGetNamePrefix(attr.getType());
  }

  public static final String getGetNamePrefix(ASTCDParameter param) {
    return getGetNamePrefix(param.getType());
  }

  public static final String getGetNamePrefix(ASTType type) {
    if ("boolean".equals(TypesPrinter.printType(type))) {
      return GET_PREFIX_BOOLEAN;
    }
    else {
      return GET_PREFIX;
    }
  }

  public static boolean isGetMethod(ASTCDMethod method) {
    return
        (method.getName().startsWith(GET_PREFIX) || method.getName().startsWith(GET_PREFIX_BOOLEAN))
            && !(method.getReturnType() instanceof ASTVoidType)
            && method.getCDParameterList().size() == 0;
  }

  /**
   * @return true if the attribute has no representation in the data class
   */
  public boolean ignoreAttribute(CDFieldSymbol ast) {
    return false;
  }

}
