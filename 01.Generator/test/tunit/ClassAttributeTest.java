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

//import mc.grammar.attributes._ast.ASTAttributeDefinition;
//TODO AR, LB
//@SuppressWarnings("deprecation")
//@RunWith(de.se.rwth.tunit.TUnitRunner.class)
//@TemplateUnderTest(templateName = "core.templates.ClassAttribute", type = ASTCDAttribute.class)
//@Output(path = "target/tunit/gen", fileExtension = "java")
//@TemplateSubstitutionPolicy(Policy = TemplateBehavior.REPLACE_WITH_EMPTY)
//
public class ClassAttributeTest {
//
//  @InitHelpers
//  /*
//  public Map<String, Object> mockVariables(ASTNode ast,
//      SymbolTableInterface symtab) {
//    Map<String, Object> myVars = Maps.newHashMap();
//
//    // tc
//    TemplateControllerLoggingAndErrorHandler errHandler = new TemplateControllerLoggingAndErrorHandler();
//    TemplateControllerConfiguration tcg = new TemplateControllerConfiguration(
//        new GlobalExtensionManagement(errHandler), errHandler, ast.get_root());
//    TemplateController tc = TemplateControllerFactory.getInstance().create(tcg,
//        "core.templates.ClassAttribute");
//
//    myVars.put("tc", tc);
//
//    return myVars;
//  }
//*/
//  @Test
//  @InputModel(fileName = "testmodels/tunit/ClassAttributeTest/Attributes.cd")
//  public void CheckJavaAttributes() {
//    Optional<String> s1 = Result.getGeneratedASTClassAttribte("TestInteger",
//        "attrPublic");
//    String s1Expected = "testmodels/tunit/ClassAttributeTest/publicAttr.java";
//
//    Optional<String> s2 = Result.getGeneratedASTClassAttribte("TestInteger",
//        "attrPrivate");
//    String s2Expected = "testmodels/tunit/ClassAttributeTest/privateAttr.java";
//
//    Optional<String> s3 = Result.getGeneratedASTClassAttribte("TestInteger",
//        "attrPublicInitialized");
//    String s3Expected = "testmodels/tunit/ClassAttributeTest/publicAttrWithInit.java";
//
//    // check that all addressed elements have been used to generate files
//    assertTrue(s1.isPresent());
//    assertTrue(s2.isPresent());
//    assertTrue(s3.isPresent());
//
//    // parse every output
//    try {
//      // class A
//      ASTFieldDeclaration attrPublic = JavaDSLFieldDeclarationMCConcreteParser
//          .createSimpleParser(s1.get()).parse();
//
//      ASTFieldDeclaration attrPublicExpected = JavaDSLFieldDeclarationMCConcreteParser
//          .createSimpleParser(s1Expected).parse();
//
//      // class B
//      ASTFieldDeclaration attrPrivate = JavaDSLFieldDeclarationMCConcreteParser
//          .createSimpleParser(s2.get()).parse();
//
//      ASTFieldDeclaration attrPrivateExpected = JavaDSLFieldDeclarationMCConcreteParser
//          .createSimpleParser(s2Expected).parse();
//
//      // // class C
//      ASTFieldDeclaration attrPublicWithInit = JavaDSLFieldDeclarationMCConcreteParser
//          .createSimpleParser(s3.get()).parse();
//
//      ASTFieldDeclaration attrPublicWithInitExpected = JavaDSLFieldDeclarationMCConcreteParser
//          .createSimpleParser(s3Expected).parse();
//
//      // assert that all generated classes are okay
//      ASTAssert.assertDeepEquals(attrPublic, attrPublicExpected);
//
//      ASTAssert.assertDeepEquals(attrPrivate, attrPrivateExpected);
//
//      ASTAssert
//          .assertDeepEquals(attrPublicWithInit, attrPublicWithInitExpected);
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
