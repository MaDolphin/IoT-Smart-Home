/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package common.util;

import com.google.common.base.Defaults;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.*;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.CDTypes;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Names;

import java.util.Optional;

/**
 * TypeHelper: lots of funtions to examine CD'types. The typeHelper is
 * stateless: only a single copy is needed and therefore instantiated at
 * DexConfigure
 *
 * @author Achim Lindt
 */

public abstract class AbstractTypeHelper {

  public static final String OPTIONAL = "Optional";

  public static final String LIST = "List";

  /**
   * is the attribute of type Boolean?
   *
   * @param type
   * @return
   */
  public boolean isBoolean(CDTypeSymbol type) {
    return CDTypes.isBoolean(type.getName());
  }

  public boolean isBoolean(String typeName) {
    return CDTypes.isBoolean(typeName);
  }
  
  public boolean isBoolean(ASTType typeName) {
    return isBoolean(TypesPrinter.printType(typeName));
  }

  /**
   * is the attribute of type Integer?
   *
   * @param type
   * @return
   */
  public boolean isInt(CDTypeSymbol type) {
    return CDTypes.isInteger(type.getName());
  }

  public boolean isInt(String typeName) {
    return CDTypes.isInteger(typeName);
  }
  
  public boolean isInt(ASTType typeName) {
    return isInt(TypesPrinter.printType(typeName));
  }

  public boolean isByte(CDTypeSymbol type) {
    return CDTypes.isByte(type.getName());
  }

  public boolean isByte(String typeName) {
    return CDTypes.isByte(typeName);
  }
  
  public boolean isByte(ASTType typeName) {
    return isByte(TypesPrinter.printType(typeName));
  }

  public boolean isShort(CDTypeSymbol type){
    return CDTypes.isShort(type.getName());
  }
  
  public boolean isShort(String typeName){
    return CDTypes.isShort(typeName);
  }
  
  public boolean isShort(ASTType typeName) {
    return isShort(TypesPrinter.printType(typeName));
  }
  
  public boolean isLong(CDTypeSymbol type) {
    return CDTypes.isLong(type.getName());
  }

  public boolean isLong(String typeName) {
    return CDTypes.isLong(typeName);
  }
  
  public boolean isLong(ASTType typeName) {
    return isLong(TypesPrinter.printType(typeName));
  }

  public boolean isDouble(CDTypeSymbol type) {
    return CDTypes.isDouble(type.getName());
  }

  public boolean isDouble(String typeName) {
    return CDTypes.isDouble(typeName);
  }
  
  public boolean isDouble(ASTType typeName) {
    return isDouble(TypesPrinter.printType(typeName));
  }

  public boolean isFloat(CDTypeSymbol type) {
    return CDTypes.isFloat(type.getName());
  }

  public boolean isFloat(String typeName) {
    return CDTypes.isFloat(typeName);
  }
  
  public boolean isFloat(ASTType typeName) {
    return isFloat(TypesPrinter.printType(typeName));
  }

  public boolean isChar(CDTypeSymbol type) {
    return CDTypes.isCharacter(type.getName());
  }

  /**
   * @param typeName the interfaceName of a type
   * @return whether the given type interfaceName denotes the java character type
   */
  public boolean isChar(String typeName) {
    return CDTypes.isCharacter(typeName);
  }
  
  public boolean isChar(ASTType typeName) {
    return isChar(TypesPrinter.printType(typeName));
  }

  /**
   * is the attribute a Wrapper (of a primitive type?)
   *
   * @param type
   * @return
   */
  public boolean isWrapperType(CDTypeSymbol type) {
    return CDTypes.isWrapperType(type.getName());
  }

  public boolean isWrapperType(String typeName) {
    return CDTypes.isWrapperType(typeName);
  }
  
  public boolean isWrapperType(ASTType typeName) {
    return isWrapperType(TypesPrinter.printType(typeName));
  }

  /**
   * is the type of the given field a numeric wrapper type
   *
   * @param type entry
   * @return
   */
  public boolean isWrapperNumeric(CDTypeSymbol type) {
    return CDTypes.isWrapperType(type.getName()) && !isBoolean(type) && !isChar(type);
  }

  public boolean isWrapperNumeric(String name){
    return CDTypes.isWrapperType(name) && !isBoolean(name) && !isChar(name);
  }
  
  public boolean isWrapperNumeric(ASTType typeName) {
    return isWrapperNumeric(TypesPrinter.printType(typeName));
  }

  /**
   * is the attribute of a primitive type?
   *
   * @param type
   * @return
   */
  public boolean isPrimitive(ASTType type) {
    return type instanceof ASTPrimitiveType;
  }

  /**
   * is the attribute of a primitive type?
   */
  public boolean isPrimitive(CDTypeSymbol type) {
    return CDTypes.isPrimitiveType(type.getName());
  }

  public boolean isPrimitive(String typeName) {
    return CDTypes.isPrimitiveType(typeName);
  }

  public boolean isOptional(CDTypeSymbol type) {
    return isOptional(type.getName());
  }

  public boolean isOptional(String typeName) {
    return "java.util.Optional".equals(typeName)
        || (typeName.indexOf('.') == -1 && OPTIONAL.equals(typeName));
  }
  
  public boolean isOptional(ASTType typeName) {
    return isOptional(TypesPrinter.printTypeWithoutTypeArguments(typeName));
  }

  /**
   * is the type a String type
   *
   * @param type entry
   * @return
   */
  public boolean isString(CDTypeSymbol type) {
    return "String".equals(type.getName())
        || "java.lang.String".equals(type.getName());
  }

  public boolean isString(String type) {
    return "String".equals(type)
        || "java.lang.String".equals(type);
  }
  
  public boolean isString(ASTType typeName) {
    return isString(TypesPrinter.printType(typeName));
  }

  /**
   * is the attribute a reference to another type (no wrapper types, no String,
   * no primitive type)?
   *
   * @param type
   * @return
   */
  public boolean isReference(CDTypeSymbol type) {
    return (!isPrimitive(type) && !isWrapperType(type) && !isString(type));
  }

  /**
   * is the attribute a reference to another type (no wrapper types, no String,
   * no primitive type)?
   *
   * @param type
   * @return
   */
  public boolean isReference(String type) {
    return (!isPrimitive(type) && !isWrapperType(type) && !isString(type));
  }
  
  public boolean isReference(ASTType typeName) {
    return isBoolean(TypesPrinter.printType(typeName));
  }

  /**
   * is the attribute of an array type?
   *
   * @param ast attribute
   * @return
   */
  public boolean isArray(ASTCDAttribute ast) {
    return ast.getType() instanceof ASTArrayType;
  }
  
  /**
   * is the attribute of type Boolean?
   *
   * @param entry attribute from symtab
   * @return
   */
  public boolean isBoolean(CDFieldSymbol entry) {
    return CDTypes.isBoolean(entry.getType().getName());
  }

  /**
   * is the type a java.util.List type
   *
   * @param type type entry
   * @return
   */
  public boolean isList(CDTypeSymbol type) {
    return "java.util.List".equals(type.getFullName())
        || (type.getName().indexOf('.') == -1 && "List".equals(type.getName()));
  }

  public boolean isList(ASTType type) {
    String typeName = TypesPrinter.printType(type);
    return typeName.length() > 0 && ("java.util.List".equals(typeName)
        || (typeName.indexOf('.') == -1 && ("List".equals(typeName) || (typeName.startsWith("List<")
        && typeName.endsWith(">")))));
  }

  public boolean isList(String type) {
    return "java.util.List".equals(type) || (type.indexOf('.') == -1 && "List".equals(type));
  }

  /**
   * is the type a java.util.Set type
   *
   * @param type type entry
   * @return
   */
  public boolean isSet(CDTypeSymbol type) {
    return "java.util.Set".equals(type.getFullName())
        || (type.getName().indexOf('.') == -1 && "Set".equals(type.getName()));
  }

  public boolean isSet(String type) {
    return "java.util.Set".equals(type) || (type.indexOf('.') == -1 && "Set".equals(type));
  }

  public boolean isMap(CDTypeSymbol type) {
    return "java.util.Map".equals(type.getFullName());
  }

  public boolean isMap(String type) {
    return "java.util.Map".equals(type) || (type.indexOf('.') == -1 && "Map".equals(type));
  }

  public boolean isEnum(CDTypeSymbol type) {
    return type.isEnum();
  }
  

  /**
   * is the attribute of a primitive and numeric type?
   *
   * @param field attribute
   * @return
   */
  public boolean isPrimitiveNumeric(CDTypeSymbol field) {

    return isByte(field) || isShort(field)|| isInt(field) || isLong(field) || isDouble(field)
        || isFloat(field);
  }

  public boolean isPrimitiveNumeric(String typeName) {
    boolean result = false;
    result = isByte(typeName) || isShort(typeName) || isInt(typeName) || isLong(typeName);
    return result;
  }

  /**
   * derives the Wrapper to a given primitive type, Integer for integer. If the
   * param s no primitive type, the result is "java.lang.String" (but should
   * better be an exception) // TODO see issue #1562
   *
   * @param type
   * @return the wrapped version
   */
  public String getWrapperType(String type) {
    if (isPrimitive(type)) {
      return CDTypes.primitiveToQualifiedWrapperOrDefault(type, "java.lang.String");
    } else {
      return type;
    }
  }

  public String getWrapperTypeWithoutPackage(String type) {
    String str = getWrapperType(type);
    str = str.replace("java.lang.", "");
    return str;
  }

  public String getUnqualifiedWrapperType(String primitiveType) {
    String result = getWrapperType(primitiveType);
    int index = result.lastIndexOf(".") + 1;
    result = result.substring(index);
    return result;
  }

  public String getDefaultValue(String type) {
    if (CDTypes.isInteger(type)) {
      return String.valueOf(Defaults.defaultValue(int.class));
    }
    else if (CDTypes.isDouble(type)) {
      return String.valueOf(Defaults.defaultValue(double.class));
    }
    else if (CDTypes.isCharacter(type)) {
      return "\'\\" + "u0000\'";
    }
    else if (CDTypes.isShort(type)) {
      return String.valueOf(Defaults.defaultValue(short.class));
    }
    else if (CDTypes.isFloat(type)) {
      return String.valueOf(Defaults.defaultValue(float.class)) + "f";
    }
    else if (CDTypes.isLong(type)) {
      return String.valueOf(Defaults.defaultValue(long.class)) + "L";
    }
    else if (CDTypes.isBoolean(type)) {
      return String.valueOf(Defaults.defaultValue(boolean.class));
    }
    else if (CDTypes.isByte(type)) {
      return String.valueOf(Defaults.defaultValue(byte.class));
    }
    else if ("Date".equals(type)) {
      return "new java.util.Date()";
    }
    else if ("String".equals(type)) {
      return "\"\" ";
    }

    return "null";
  }
  
  public String getDefaultValue(ASTType typeName) {
    return getDefaultValue(TypesPrinter.printType(typeName));
  }

  public boolean isCollection(CDTypeSymbol type) {
    return isList(type) || isSet(type) || isMap(type);
  }

  public boolean isCollection(String type) {
    return isList(type) || isSet(type) || isMap(type);
  }

  // This method does not work since the symbol table does not set the generic
  // types
  public CDTypeSymbol getGenericCollectionElement(CDTypeSymbol type) {
    if (isCollection(type) && type.getFormalTypeParameters().size() == 1) {
      return type.getFormalTypeParameters().iterator().next();
    }
    return null; // TODO: throw exception
  }

  // dirty hack
  public Optional<String> getGenericCollectionType(CDTypeSymbol type) {
    if (!(isCollection(type) || isOptional(type))) {
      return Optional.empty();
    }

    String name = type.getStringRepresentation();
    name = name.substring(name.indexOf("<") + 1);
    name = name.substring(0, name.length() - 1);

    return Optional.ofNullable(name);
  }
  
  public String getOptionalType(CDTypeSymbolReference type) {
    String name = type.getStringRepresentation();
    if (isOptional(name)) {
      name = name.substring(name.indexOf("<") + 1);
      name = name.substring(0, name.length() - 1);
      return name;
    }
    return type.getName();
  }

  public Optional<ASTSimpleReferenceType> getFirstTypeArgumentOfGenericType(ASTType astType,
      String typeName) {
    if (!(astType instanceof ASTSimpleReferenceType)) {
      return Optional.empty();
    }
    ASTSimpleReferenceType type = (ASTSimpleReferenceType) astType;
    if (!Names.getSimpleName(type.getNameList()).equals(typeName) ||
        !type.getTypeArgumentsOpt().isPresent() ||
        type.getTypeArguments().getTypeArgumentList().size() != 1) {
      return Optional.empty();
    }

    ASTTypeArgument typeArgument = type
        .getTypeArguments().getTypeArgumentList().get(0);
    if (!(typeArgument instanceof ASTSimpleReferenceType)) {
      return Optional.empty();
    }

    return Optional.of((ASTSimpleReferenceType) typeArgument);
  }
}
