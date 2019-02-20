/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package configure;

import common.CDSymbolTable;
import common.util.CDAssociationUtil;
import common.util.TransformationUtils;
import common.util.TypeHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.DateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class designed for initial configurations together with the starter
 * template (This class is specific! to the project and can be updated) Operates
 * on: ASTCDDefinition Uses: AST Effects: produces a number of TemplateValues,
 * mainly helpers
 *
 * @author Rumpe
 */
public class ConfigureDexGenerator {

  public static final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ConfigureDexGenerator() {
  }

  public static GlobalExtensionManagement getGlex() {
    return glex;
  }

  public static CDSymbolTable createCDSymbolTable(ASTCDCompilationUnit compUnit,
      List<File> modelPaths) {
    CDSymbolTable symTab = new CDSymbolTable(compUnit, modelPaths);
    // AR quick fix: a couple of common Java types (and primitives) for use in
    // CDs (4analysis)
    symTab.defineType("java.lang.Boolean");
    symTab.defineType("java.lang.Character");
    symTab.defineType("java.lang.Double");
    symTab.defineType("java.lang.Integer");
    symTab.defineType("java.lang.String");
    symTab.defineType("java.lang.Byte");
    symTab.defineType("java.lang.Short");
    symTab.defineType("java.lang.Long");
    symTab.defineType("java.lang.Float");
    symTab.defineType("java.util.Date");
    symTab.defineType("java.util.List");
    symTab.defineType("java.time.ZonedDateTime"); // TODO: Kann vermutlich raus!
    symTab.defineType("java.util.Set");
    symTab.defineType("java.util.Optional");

    symTab.defineType("boolean");
    symTab.defineType("char");
    symTab.defineType("double");
    symTab.defineType("int");
    symTab.defineType("byte");
    symTab.defineType("short");
    symTab.defineType("long");
    symTab.defineType("float");

    // Add all java-Types (imports without *)
    for (String imp : TransformationUtils.getJavaImports(compUnit)) {
      symTab.defineType(imp);
    }

    return symTab;
  }

  public static CDSymbolTable createCDSymbolTableIfNecessary(ASTCDCompilationUnit compUnit,
      List<File> modelPaths) {
    if (compUnit.getCDDefinition().getEnclosingScopeOpt().isPresent()) {
      return new CDSymbolTable(compUnit);
    }
    else {
      return createCDSymbolTable(compUnit, modelPaths);
    }
  }

  public static CDSymbolTable buildCDSymbolTableFromAST(ASTCDCompilationUnit compUnit) {
    checkArgument(compUnit.getCDDefinition().getEnclosingScopeOpt().isPresent());
    return new CDSymbolTable(compUnit);
  }

  /**
   * Initialize type helper
   *
   * @return TypeHelper
   */
  public static TypeHelper getTypeHelper() {
    return new TypeHelper();
  }

  public static CDAssociationUtil getCDAssociationUtil() {
    return new CDAssociationUtil();
  }

  /**
   * Initialize the date helper
   */
  public static DateHelper getDateHelper() {
    return DateHelper.getInstance();
  }

  /**
   * retrieve the user interfaceName (the user who runs the generator) from
   * System.getProperty
   *
   * @return
   */
  public static String getUserName() {
    return System.getProperty("user.interfaceName");
  }

  static final SimpleDateFormat formatter = new SimpleDateFormat(
      "dd.MM.yyyy HH:mm");

  static String generationTime = null;

  /**
   * retrieve the generation time (it is repeatedly the same value)
   *
   * @return
   */
  public static String getGenerationTime() {
    if (generationTime == null) {
      generationTime = formatter.format(new Date());
    }
    return generationTime;
  }

  /**
   * get the prefix generally used for methods that need to be public, but
   * should not be used by developers
   *
   * @return
   */
  public static String getGenPrefix() {
    return "raw";
  }
}
