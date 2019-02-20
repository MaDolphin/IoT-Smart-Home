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
//@TemplateUnderTest(templateName = "core.templates.ClassMethod", type = ASTCDMethod.class)
//@Output(path = "target/tunit/gen", fileExtension = "java")
//@TemplateSubstitutionPolicy(Policy = TemplateBehavior.REPLACE_WITH_EMPTY)
public class ClassMethodTest {
//
//  @InitHelpers
//  public Map<String, Object> mockVariables(ASTNode ast,
//      SymbolTableInterface symtab) {
//    Map<String, Object> myVars = Maps.newHashMap();
//
//    // tc
//    TemplateControllerLoggingAndErrorHandler errHandler = new TemplateControllerLoggingAndErrorHandler();
//    TemplateControllerConfiguration tcg = new TemplateControllerConfiguration(
//        new GlobalExtensionManagement(errHandler), errHandler, ast.get_root());
//     TemplateController tc =
//     TemplateControllerFactory.getInstance().create(tcg,
//     "core.templates.ClassMethod");
//
//
//
//    myVars.put("tc", tc);
//
//    myVars.put("helper", new CDCallableHelper());
//
//    return myVars;
//  }
//
//  @Test
//  @InputModel(fileName = "testmodels/tunit/ClassMethodTest/Methods.cd")
//  public void CheckJavaMethod() {
//    Optional<String> s1 = Result.getGeneratedASTClassMethod("TestMethods",
//        "method1");
//    String s1Expected = "testmodels/tunit/ClassMethodTest/Method1.java";
//
//    Optional<String> s2 = Result.getGeneratedASTClassMethod("TestMethods",
//        "method2", "String", "TestMethods");
//    String s2Expected = "testmodels/tunit/ClassMethodTest/Method2.java";
//
//    // check that all addressed elements have been used to generate files
//    assertTrue(s1.isPresent());
//    assertTrue(s2.isPresent());
//
//    // parse every output
//    try {
//      // method1
//      ASTMethodDeclaration method1 = JavaDSLMethodDeclarationMCConcreteParser.createSimpleParser(s1.get()).parse();
//      
//
//      ASTMethodDeclaration method1Expected = JavaDSLMethodDeclarationMCConcreteParser.createSimpleParser(s1Expected).parse();
//
//   // method2
//      ASTMethodDeclaration method2 = JavaDSLMethodDeclarationMCConcreteParser.createSimpleParser(s2.get()).parse();
//      
//
//      ASTMethodDeclaration method2Expected = JavaDSLMethodDeclarationMCConcreteParser.createSimpleParser(s2Expected).parse();
//
//      // assert that all generated classes are okay
//      ASTAssert.assertDeepEquals(method1, method1Expected);
//
//      ASTAssert.assertDeepEquals(method2, method2Expected);
//      
//
//    } catch (RecognitionException e) {
//      e.printStackTrace();
//    } catch (TokenStreamException e) {
//      e.printStackTrace();
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    }
//  }
}
