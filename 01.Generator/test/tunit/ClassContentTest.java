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
//@TemplateUnderTest(templateName = "core.templates.ClassContent", type = ASTCDClass.class)
//@Output(path = "target/tunit/gen", fileExtension = "java")
//@TemplateSubstitutionPolicy(Policy = TemplateBehavior.REPLACE_WITH_EMPTY)
public class ClassContentTest {
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
//    TemplateController tc = TemplateControllerFactory.getInstance().create(tcg,
//        "core.templates.ClassContent");
//
//    myVars.put("tc", tc);
//
//    return myVars;
//  }
//
//  @Test
//  @InputModel(fileName = "testmodels/correct/AbstractHierarchy.cd")
//  public void CheckJavaClass() {
//    Optional<String> s1 = Result.getGeneratedASTClassPath("A");
//    String s1Expected = "testmodels/tunit/AbstractHierarchy/A.java";
//
//    Optional<String> s2 = Result.getGeneratedASTClassPath("B");
//    String s2Expected = "testmodels/tunit/AbstractHierarchy/B.java";
//
//    Optional<String> s3 = Result.getGeneratedASTClassPath("C");
//    String s3Expected = "testmodels/tunit/AbstractHierarchy/C.java";
//
//    // check that all addressed elements have been used to generate files
//    assertTrue(s1.isPresent());
//    assertTrue(s2.isPresent());
//    assertTrue(s3.isPresent());
//
//    // parse every output
//    try {
//      // class A
//      ASTCompilationUnit clazzA = JavaDSLCompilationUnitMCConcreteParser
//          .createSimpleParser(s1.get()).parse();
//
//      ASTCompilationUnit clazzAExpected = JavaDSLCompilationUnitMCConcreteParser
//          .createSimpleParser(s1Expected).parse();
//
//      // class B
//      ASTCompilationUnit clazzB = JavaDSLCompilationUnitMCConcreteParser
//          .createSimpleParser(s2.get()).parse();
//
//      ASTCompilationUnit clazzBExpected = JavaDSLCompilationUnitMCConcreteParser
//          .createSimpleParser(s2Expected).parse();
//
//      // class C
//      ASTCompilationUnit clazzC = JavaDSLCompilationUnitMCConcreteParser
//          .createSimpleParser(s3.get()).parse();
//
//      ASTCompilationUnit clazzCExpected = JavaDSLCompilationUnitMCConcreteParser
//          .createSimpleParser(s3Expected).parse();
//
//      // assert that all generated classes are okay
//      ASTAssert.assertDeepEquals(clazzA, clazzAExpected);
//
//      ASTAssert.assertDeepEquals(clazzB, clazzBExpected);
//
//      ASTAssert.assertDeepEquals(clazzC, clazzCExpected);
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
