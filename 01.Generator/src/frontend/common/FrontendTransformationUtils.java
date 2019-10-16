/* (c) https://github.com/MontiCore/monticore */

package frontend.common;

import common.CDSymbolTable;
import common.util.TransformationUtils;
import common.util.TypeHelper;
import de.monticore.io.paths.IterablePath;
import de.monticore.literals.literals._ast.ASTStringLiteral;
import de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumParameter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDStereotype;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Joiners;
import frontend.data.DTOCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static common.util.TransformationUtils.isDTOType;

public class FrontendTransformationUtils {

  private static TypeHelper typeHelper = new TypeHelper();

  public static final String STRING_FRONTEND = "string";

  public static final String NUMERIC_FRONTEND = "number";

  public static final String NUMERICARRAY_FRONTEND = "number[]";

  public static final String BOOLEAN_FRONTEND = "boolean";

  public static final String STRINGARRAY_FRONTEND = "string[]";

  public static final String STRING_OR_NULL = "string | null";

  public static final String DATE = "Date | string | null";

  public static final String STRING_OR_UNDEFINED = "string | undefined";

  public static final String NULL = "null";

  public static final String ARRAY = "[]";

  public static final String HWC = "src/app/shared/architecture/data/";

  public static final String FromTargetToHWC = "../../";

  public static final String Up = "../";

  public static final String Quote = "'";

  public static String getFrontendType(String typeName) {
    if (typeHelper.isOptionalZonedDateTime(typeName)) {
      return STRING_OR_NULL;
    }
    if (typeHelper.isOptionalString(typeName)) {
      return STRING_FRONTEND;
    }
    if (typeHelper.isOptionalNumeric(typeName)) {
      return NUMERIC_FRONTEND;
    }
    if (typeHelper.isGenericOptional(typeName)) {
      return STRING_FRONTEND;
    }
    if (typeHelper.isString(typeName)) {
      return STRING_FRONTEND;
    }
    if (typeHelper.isZonedDateTime(typeName)) {
      return STRING_FRONTEND;
    }
    if (typeHelper.isPrimitiveNumeric(typeName) || typeHelper.isFloat(typeName) || typeHelper.isDouble(typeName)) {
      return NUMERIC_FRONTEND;
    }
    if (typeHelper.isGenericStringList(typeName)) {
      return STRINGARRAY_FRONTEND;
    }
    if (typeHelper.isGenericNotStringList(typeName)) {
      return typeHelper.getFirstTypeArgumentOfList(typeName) + ARRAY;
    }
    if (typeHelper.isBoolean(typeName)) {
      return BOOLEAN_FRONTEND;
    }
    return typeName;
  }

  public static String getFrontendType(String typeName,
      Optional<CDTypeSymbol> cdType,
      CDSymbolTable symbolTable) {
    if (typeHelper.isOptionalZonedDateTime(typeName)) {
      return STRING_OR_NULL;
    }
    if (typeHelper.isOptionalString(typeName)) {
      return STRING_FRONTEND;
    }
    if (typeHelper.isOptionalNumeric(typeName)) {
      return NUMERIC_FRONTEND;
    }
    if (typeHelper.isGenericOptional(typeName)) {
      String typeArgument = typeHelper
          .getFirstTypeArgumentOfOptional(typeName);
      if (isDTOType(symbolTable, typeArgument)) {
        typeArgument += DTOCreator.DTO;
      }
      return typeArgument;
    }
    if (typeHelper.isString(typeName)) {
      return STRING_FRONTEND;
    }
    if (typeHelper.isZonedDateTime(typeName)) {
      return STRING_FRONTEND;
    }
    if (typeHelper.isPrimitiveNumeric(typeName) || typeHelper.isFloat(typeName) || typeHelper.isDouble(typeName)) {
      return NUMERIC_FRONTEND;
    }
    if (typeHelper.isGenericStringList(typeName)) {
      return STRINGARRAY_FRONTEND;
    }
    if (typeHelper.isGenericNotStringList(typeName)) {
      String typeArgument = typeHelper.getFirstTypeArgumentOfList(typeName);
      if (isDTOType(symbolTable, typeArgument)) {
        typeArgument += DTOCreator.DTO;
      } else if (typeHelper.isPrimitiveNumeric(typeArgument) || typeHelper.isFloat(typeName) || typeHelper.isDouble(typeName)) {
        typeArgument = NUMERIC_FRONTEND;
      }
      return typeArgument + ARRAY;
    }
    if (typeHelper.isBoolean(typeName)) {
      return BOOLEAN_FRONTEND;
    }
    if (cdType.isPresent() && TransformationUtils.isDTOType(symbolTable, cdType.get())) {
      typeName += DTOCreator.DTO;
    }
    return typeName;
  }

  public static String getFrontendTypeForInterface(String typeName) {
    if (typeHelper.isOptionalZonedDateTime(typeName)) {
      return DATE;
    }
    if (typeHelper.isGenericOptional(typeName)) {
      return STRING_FRONTEND;
    }
    if (typeHelper.isString(typeName)) {
      return STRING_FRONTEND;
    }
    if (typeHelper.isZonedDateTime(typeName)) {
      return DATE;
    }
    if (typeHelper.isPrimitiveNumeric(typeName) || typeHelper.isFloat(typeName) || typeHelper.isDouble(typeName)) {
      return NUMERIC_FRONTEND;
    }
    if (typeHelper.isGenericStringList(typeName)) {
      return STRINGARRAY_FRONTEND;
    }
    if (typeHelper.isBoolean(typeName)) {
      return BOOLEAN_FRONTEND;
    }
    return typeName;
  }

  public static String getDefaultValueForType(String frontendType) {
    switch (frontendType) {
      case STRING_FRONTEND:
        return "''";
      case NUMERIC_FRONTEND:
        return "0";
      case BOOLEAN_FRONTEND:
        return "false";
      case STRINGARRAY_FRONTEND:
      case NUMERICARRAY_FRONTEND:
        return ARRAY;
      case STRING_OR_NULL:
        return NULL;
      default:
        return NULL;
    }
  }

  public static boolean isRequired(String typeName) {
    return !typeHelper.isGenericOptional(typeName);
  }

  public static String getJsonMemberType(String frontendType, boolean isElementType) {
    if (isElementType) {
      return "elements: " + TransformationUtils.capitalize(frontendType);
    }
    switch (frontendType) {
      case STRING_FRONTEND:
      case NUMERIC_FRONTEND:
      case BOOLEAN_FRONTEND:
        return "type: " + TransformationUtils.capitalize(frontendType);
      case STRING_OR_NULL:
        return "type: String";
      case STRINGARRAY_FRONTEND:
        return "elements: String";
      case NUMERICARRAY_FRONTEND:
        return "elements: Number";
      default:
        return "type: " + frontendType;
    }
  }

  public static List<String> getImportsForInterface(String typeName,
      Optional<IterablePath> handcodePath, String fileExtension, String subpackage, String suffix) {
    List<String> imports = new ArrayList<>();
    String fileName = Joiners.DOT
        .join(typeName.toLowerCase(), fileExtension);
    String from =
        TransformationUtils
            .existsHandwrittenDotFile(fileName, subpackage,
                handcodePath.get(),
                TransformationUtils.DOT_TYPESCRIPT_FILE_EXTENSION) ?
            Quote + "../../../src/app/shared/architecture/data/" + subpackage + "/"
                + typeName.toLowerCase() + "."
                + fileExtension + Quote :
            Quote + Up + subpackage + "/i" + fileName + Quote;
    imports.add("{I" + typeName + suffix + "} from " + from);

    return imports;
  }

  public static ASTCDStereotype createJsonMemberStereotype(String name, String typeName,
      boolean isRequired) {
    return createJsonMemberStereotype(name, typeName, isRequired, false);
  }

  public static ASTCDStereotype createJsonMemberStereotype(String name, String typeName,
      boolean isRequired, boolean isElementType) {
    // Add annotation JsonMember
    ASTCDStereotype stereotype = CD4AnalysisMill.cDStereotypeBuilder().build();
    if (isElementType) {
      typeName = typeHelper.getTypeFromTypeWithSuffix(typeName, ARRAY);
    }
    String type = FrontendTransformationUtils.getJsonMemberType(typeName, isElementType);
    String value =
        "@JsonMember({ isRequired: " + isRequired + ", name: " + Quote + name + Quote + ", " + type
            + " })";
    ASTCDStereoValue stereotypeValue = CD4AnalysisMill.cDStereoValueBuilder().setName(value).build();
    stereotype.getValueList().add(stereotypeValue);
    return stereotype;
  }

  /**
   * Returns if there is a handwritten file
   *
   * @param simpleName
   * @param handcodedPath
   * @param fileExtension
   * @param subpackage
   * @param fileName
   * @return
   */
  public static boolean existsHandwrittenFile(String simpleName,
                                              Optional<IterablePath> handcodedPath, String fileExtension, String subpackage,
                                              Optional<String> fileName) {
    String file = Joiners.DOT.join(fileName.orElse(simpleName.toLowerCase()), fileExtension);
    return TransformationUtils
        .existsHandwrittenDotFile(file, subpackage, handcodedPath.get(),
            TransformationUtils.DOT_TYPESCRIPT_FILE_EXTENSION);
  }

  /**
   * Returns an import statement for the specified handwritten or generated type
   *
   * @param simpleName    - type name
   * @param handcodedPath - handcoded Path
   * @param fileExtension - file name suffix (e.g. "aggregate" or "dto")
   * @param subpackage    - subpackage (e.g. "dtos" or "dtos")
   * @param fileName      - file name if known
   * @return an import statement for the specified handwritten or generated type
   */
  public static String getImportCheckHWC(String simpleName,
      Optional<IterablePath> handcodedPath, String fileExtension, String subpackage,
      Optional<String> fileName) {
    String file = Joiners.DOT.join(fileName.orElse(simpleName.toLowerCase()), fileExtension);
    String path = Up;
    if (TransformationUtils
        .existsHandwrittenDotFile(file, subpackage, handcodedPath.get(),
            TransformationUtils.DOT_TYPESCRIPT_FILE_EXTENSION)) {
      path += FromTargetToHWC + HWC;
    }
    return "{" + simpleName + "} from " + Quote + path + subpackage + "/" + file + Quote;
  }

  /**
   * Returns an import statement for the specified handwritten or generated type
   *
   * @param simpleName    - type name
   * @param handcodedPath - handcoded Path
   * @param fileExtension - file name suffix (e.g. "aggregate" or "dto")
   * @param subpackage    - subpackage (e.g. "dtos" or "dtos")
   * @param fileName      - file name if known
   * @return an import statement for the specified handwritten or generated type
   */
  public static String getImportPathCheckHWC(String simpleName,
      Optional<IterablePath> handcodedPath, String fileExtension, String subpackage,
      Optional<String> fileName) {
    String file = fileName.orElse(simpleName.toLowerCase());
    if (!fileExtension.equals(TransformationUtils.CLASSES_PACKAGE)) file = Joiners.DOT.join(file, fileExtension);

    String path = Up;

    if (TransformationUtils
        .existsHandwrittenDotFile(file, subpackage, handcodedPath.get(),
            TransformationUtils.DOT_TYPESCRIPT_FILE_EXTENSION)) {
      path += FromTargetToHWC + HWC;
    } else if (fileExtension.equals(TransformationUtils.DTOS_PACKAGE)) {
      return "target/generated-sources/dtos/*";
    }
    return path + subpackage + "/" + file;
  }

  /**
   * Returns an import statement for the specified type if HWC exists
   *
   * @param simpleName    - type name
   * @param handcodedPath - handcoded Path
   * @param fileExtension - file name suffix (e.g. "aggregate" or "dto")
   * @param subpackage    - subpackage (e.g. "dtos" or "dtos")
   * @param fileName      - file name if known
   * @return an import statement for the specified type if HWC exists
   */
  public static Optional<String> getImportForHWC(String simpleName,
      Optional<IterablePath> handcodedPath, String fileExtension, String subpackage,
      Optional<String> fileName) {
    String file = Joiners.DOT.join(fileName.orElse(simpleName.toLowerCase()), fileExtension);
    if (TransformationUtils
        .existsHandwrittenDotFile(file, subpackage, handcodedPath.get(),
            TransformationUtils.DOT_TYPESCRIPT_FILE_EXTENSION)) {
      String path = Up + FromTargetToHWC + HWC;
      return Optional
          .of("{" + simpleName + "} from " + Quote + path + subpackage + "/" + file + Quote);
    }
    return Optional.empty();
  }

  public static String printCDEnumParameters(List<ASTCDEnumParameter> parameters) {
    if (parameters.isEmpty()) {
      return "";
    }
    StringBuilder output = new StringBuilder(" = ");
    LiteralsPrettyPrinterConcreteVisitor printer = new LiteralsPrettyPrinterConcreteVisitor(new IndentPrinter());
    ASTCDEnumParameter param = parameters.get(0);
      if (param.getValue().getSignedLiteral() instanceof ASTStringLiteral) {
        // TODO Entferne dies, wenn die Values komplexere Werte annehmen können
        output.append(((ASTStringLiteral) param.getValue().getSignedLiteral()).getSource());
      } else {
        output.append(printer.prettyprint(param.getValue().getSignedLiteral()));
      }
    return output.toString();
  }

}
