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

package tunit;


//TODO AR, LB
//@SuppressWarnings("deprecation")
//@RunWith(de.se.rwth.tunit.TUnitRunner.class)
//@TemplateUnderTest(templateName = "core.templates.EnumContent", type = ASTCDEnum.class)
//@Output(path = "target/tunit/gen", fileExtension = "java")
//@TemplateSubstitutionPolicy(Policy = TemplateBehavior.REPLACE_WITH_EMPTY)
public class EnumContentTest {

//  @InitHelpers
//  public Map<String, Object> mockVariables(ASTNode ast,
//      SymbolTableInterface symtab) {
//    Map<String, Object> myVars = Maps.newHashMap();
//
//    // tc
//    TemplateControllerLoggingAndErrorHandler errHandler = new TemplateControllerLoggingAndErrorHandler();
//    TemplateControllerConfiguration tcg = new TemplateControllerConfiguration(
//        new GlobalExtensionManagement(errHandler), errHandler, ast.get_root());
//    TemplateController tc = TemplateControllerFactory.getInstance().create(tcg,
//        "core.templates.EnumContent");
//
//    myVars.put("tc", tc);
//
//    return myVars;
//  }
//
//  @Test
//  @InputModel(fileName = "testmodels/tunit/EnumContentTest/Enums.cd")
//  public void CheckJavaClass() {
//    
////    Optional<String> s1 = Result.getGeneratedASTEnum("Enum2");
////    String s1Expected = "testmodels/tunit/EnumContentTest/Enum1.java";
////
////    // check that all addressed elements have been used to generate files
////    assertTrue(s1.isPresent());
////
////    // parse every output
////    try {
////      // enum Enum1
////      ASTEnumDeclaration attrPublic = JavaDSLEnumDeclarationMCConcreteParser
////          .createSimpleParser(s1.get()).parse();
////
////      ASTEnumDeclaration attrPublicExpected = JavaDSLEnumDeclarationMCConcreteParser
////          .createSimpleParser(s1Expected).parse();
////
////      // assert that all generated classes are okay
////      ASTAssert.assertDeepEquals(attrPublic, attrPublicExpected);
////
////    } catch (RecognitionException e) {
////      e.printStackTrace();
////    } catch (TokenStreamException e) {
////      e.printStackTrace();
////    } catch (FileNotFoundException e) {
////      e.printStackTrace();
////    }
//  }
}
