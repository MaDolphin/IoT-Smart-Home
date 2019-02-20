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
//@TemplateUnderTest(templateName = "core.templates.InterfaceContent", type = ASTCDInterface.class)
//@Output(path = "target/tunit/gen", fileExtension = "java")
//@TemplateSubstitutionPolicy(Policy = TemplateBehavior.REPLACE_WITH_EMPTY)
public class InterfaceContentTest {

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
//        "core.templates.InterfaceContent");
//
//    myVars.put("tc", tc);
//
//    return myVars;
//  }
//
//  @Test
//  @InputModel(fileName = "testmodels/tunit/InterfaceContentTest/Interfaces.cd")
//  public void CheckJavaClass() {
////    Optional<String> s1 = Result.getGeneratedASTInterace("IFace1");
////    String s1Expected = "testmodels/tunit/InteraceContentTest/IFace1.java";
////    Optional<String> s2 = Result.getGeneratedASTInterace("IFace2");
////    String s2Expected = "testmodels/tunit/InteraceContentTest/IFace2.java";
//
//    // check that all addressed elements have been used to generate files
////    assertTrue(s1.isPresent());
////    assertTrue(s2.isPresent());
//
//    // parse every output
////    try {
//      // enum Enum1
////      ASTInterfaceDeclaration iface1 = JavaDSLInterfaceDeclarationMCConcreteParser
////          .createSimpleParser(s1.get()).parse();
////
////      ASTInterfaceDeclaration iface1Expected = JavaDSLInterfaceDeclarationMCConcreteParser
////          .createSimpleParser(s1Expected).parse();
////
////      ASTInterfaceDeclaration iface2 = JavaDSLInterfaceDeclarationMCConcreteParser
////          .createSimpleParser(s2.get()).parse();
////
////      ASTInterfaceDeclaration iface2Expected = JavaDSLInterfaceDeclarationMCConcreteParser
////          .createSimpleParser(s2Expected).parse();
////
////      // assert that all generated classes are okay
////      ASTAssert.assertDeepEquals(iface1, iface1Expected);
////      ASTAssert.assertDeepEquals(iface2, iface2Expected);
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
