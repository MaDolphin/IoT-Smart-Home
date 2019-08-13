/* (c) https://github.com/MontiCore/monticore */

package common.util;

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.List;

/**
 * This extension provides support for external java types.
 * Currently, only "java.util.Date" is supported.
 *
 * @author JLN
 */
public class TypeHelper extends AbstractTypeHelper {

  public static final String ZonedDateTime = "ZonedDateTime";

  public boolean isNumber(String type) {
    return isByte(type) || isShort(type) || isInt(type) || isLong(type) || isFloat(type) || isDouble(type);
  }

  public boolean isExternalJavaType(String type) {
    return isDate(type);
  }

  public boolean isExternalJavaType(CDTypeSymbol type) {
    return isExternalJavaType(type.getName());
  }

  public boolean isDate(CDTypeSymbol type) {
    return "java.util.Date".equals(type.getName())
        || (type.getName().indexOf('.') == -1 && "Date".equals(type.getName()));
  }

  public boolean isDate(String type) {
    return "java.util.Date".equals(type)
        || (type.indexOf('.') == -1 && "Date".equals(type));
  }

  public boolean isReference(CDTypeSymbol type) {
    return (!isPrimitive(type) && !isWrapperType(type) && !isString(type) && !isDate(type));
  }

  public boolean isCollection(String type) {
    switch (type) {
      case "List":
      case "Set":
      case "Collection":
        return true;
      default:
        return false;
    }
  }

  public boolean isGeneric(String type) {
    return type.contains("<") && type.endsWith(">");
  }

  public String getGenericType(String type) {
    if (!isGeneric(type)) {
      return "";
    }
    return type.substring(0, type.indexOf('<'));
  }

  public boolean isGenericOptional(String type) {
    return type.startsWith(OPTIONAL) && type.endsWith(">") && type.indexOf("<") == OPTIONAL
        .length();
  }

  public boolean isZonedDateTime(String type) {
    return ZonedDateTime.equals(type);
  }

  public boolean isList(String type) {
    return "java.util.List".equals(type)
        || (type.indexOf('.') == -1 && "List".equals(type));
  }

  public boolean isGenericList(String type) {
    return type.startsWith(LIST) && type.endsWith(">") && type.indexOf("<") == LIST.length();
  }

  public boolean isGenericNotStringList(String type) {
    return type.startsWith(LIST) && type.endsWith(">") && type.indexOf("<") == LIST.length() && !type
        .substring(5, type.length() - 1).equals("String");
  }

  public boolean isGenericStringList(String type) {
    return type.startsWith(LIST) && type.endsWith(">") && type.indexOf("<") == LIST.length() && type
        .substring(5, type.length() - 1).equals("String");
  }

  public String getTypeFromTypeWithSuffix(String type, String suffix) {
    if (type.length() > (suffix.length() + 1) && type.endsWith(suffix)) {
      return type.substring(0, type.length() - suffix.length());
    }
    return type;
  }

  public boolean isOptionalString(String type) {
    return isGenericOptional(type) && isString(
        getFirstTypeArgumentOfOptional(type));
  }

  public boolean isOptionalNumeric(String type) {
    return isGenericOptional(type) && isWrapperNumeric(
        getFirstTypeArgumentOfOptional(type));
  }

  public boolean isOptionalZonedDateTime(String type) {
    return isGenericOptional(type) && isZonedDateTime(
        getFirstTypeArgumentOfOptional(type));
  }

  public boolean isListOfStrings(String type) {
    return isGenericList(type) && isString(getFirstTypeArgumentOfGeneric(type, LIST.length()));
  }

  public String printType(ASTType type) {
    return TypesPrinter.printType(type);
  }

  public String getFirstTypeArgumentOfGeneric(String type, int typeNameLength) {
    if (type.length() < typeNameLength + 3) {
      return "";
    }
    if (!type.endsWith(">")) {
      return "";
    }
    String typeArg = type.substring(typeNameLength + 1);
    int index = typeArg.indexOf("<") != -1 ? typeArg.indexOf("<") : typeArg.length() - 1;
    return typeArg.substring(0, index);
  }

  public String getFirstTypeArgumentOfOptional(String type) {
    return getFirstTypeArgumentOfGeneric(type, OPTIONAL.length());
  }

  public String getFirstTypeArgumentOfList(String type) {
    return getFirstTypeArgumentOfGeneric(type, LIST.length());
  }

  public String getFirstTypeArgumentOfGeneric(ASTSimpleReferenceType type) {
    if (!type.getTypeArgumentsOpt().isPresent()) {
      return "";
    }

    List<ASTTypeArgument> typeArgs = type.getTypeArguments().getTypeArgumentList();
    if (typeArgs.size() < 1) {
      return "";
    }

    return TypesPrinter.printTypeArgument(typeArgs.get(0));
  }

  public String getClassType(String type) {
    switch (type) {
      case "int":
        return "Integer";
      case "long":
        return "Long";
      case "boolean":
        return "Boolean";
      default:
        return type;
    }
  }

  public String getDefaultValue(String type, String name, String suffix) {
    if (isBoolean(type)) {
      return "true";
    }
    if (isLong(type)) {
      return "" + Math.abs(name.hashCode()) + "L";
    }
    if (isPrimitiveNumeric(type)) {
      return "" + Math.abs(name.hashCode());
    }
    if (isFloat(type)) {
      return "new Float(" + Math.abs(name.hashCode()) + ")";
    }
    if (isDouble(type)) {
      return "new Double(" + Math.abs(name.hashCode()) + ")";
    }
    if (isString(type)) {
      return "\"testOf" + TransformationUtils.capitalize(name) + "\"";
    }
    if (isListOfStrings(type)) {
      return "Lists.newArrayList(\"" + suffix + TransformationUtils.capitalize(name) + "\")";
    }
    if (isOptionalString(type)) {
      return "Optional.of(\"" + suffix + TransformationUtils.capitalize(name) + "\")";
    }
    if (isZonedDateTime(type)) {
      return "ZonedDateTime.now()";
    }
    if (isOptionalZonedDateTime(type)) {
      return "Optional.of(ZonedDateTime.now())";
    }
    if (isGenericOptional(type)) {
      return "Optional.empty()";
    }
    if (isGenericList(type)) {
      return "new ArrayList<>()";
    }
    return "null";
  }

  public String getValueForType(String type) {
    if (isBoolean(type)) {
      return "true";
    }
    if (isPrimitiveNumeric(type)) {
      return "1000";
    }
    if (isFloat(type)) {
      return "5.0f";
    }
    if (isDouble(type)) {
      return "10.0";
    }
    if (isZonedDateTime(type)) {
      return "ZonedDateTime.of(2018, 4, 16, 10, 0, 0, 0, ZoneId.of(\"Z\"))";
    }
    if (isOptionalZonedDateTime(type)) {
      return "Optional.of(ZonedDateTime.of(2018, 4, 16, 10, 0, 0, 0, ZoneId.of(\"Z\")))";
    }
    if (isOptional(type)) {
      return "Optional.empty()";
    }
    if (isGenericList(type)) {
      return "new ArrayList<>()";
    }
    return "null";
  }
}
