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

package generation;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class GeneratorTest extends GeneratorTestBase {

  @Test
  public void testCorrectQAssociationTest() {
    testCorrect("testmodels/correct/QAssociationTests.cd");
  }

  @Test
  public void testCorrectAssociationTest() {
    testCorrect("testmodels/correct/AssociationTest.cd");
  }

  @Test
  public void testCorrectAssociationQualifiedTest() {
    testCorrect("testmodels/correct/AssociationTestQualified.cd");
  }

  @Test
  public void testCorrect11() {
    testCorrect("testmodels/correct/DEv1.cd");
  }

  @Test
  public void testFalse1() {
    testFalse("testmodels/incorrect/CD2.cd");// EtsErrorCode.QualificationFailed
  }

  @Test
  public void testFalse2() {
    testFalse("testmodels/incorrect/CD2Alloy1multi.cd");// CD4AnalysisErrorCodes.MultipleInheritance);
  }

  @Test
  public void testGUIGenerationNamesGuiSubpackage() {
    testFalse("testmodels/incorrect/generatednameschecking/gui");// CD4AnalysisErrorCodes.GeneratedElementNameClash);
  }

  @Test
  public void testGUIGenerationNamesImplSubpackage() {
    testFalse("testmodels/incorrect/generatednameschecking/impl");// CD4AnalysisErrorCodes.GeneratedElementNameClash);
  }

  @Test
  public void testPersistenceGenerationNamesImplSubpackage() {
    testFalse(
        "testmodels/incorrect/generatednameschecking/persistence");// CD4AnalysisErrorCodes.GeneratedElementNameClash);
  }

  @Test
  public void testDataGenerationNames() {
    testFalse("testmodels/incorrect/generatednameschecking/data");// CD4AnalysisErrorCodes.GeneratedElementNameClash);
  }

  @Test
  public void testUnsupportedAttributeTypes() {
    testFalse("testmodels/incorrect/unsupportedattributetypes");// CD4AnalysisErrorCodes.UnsupportedAttributeTypes);
  }

  @Test
  public void testSupportedAttributeTypes() {
    testCorrect("testmodels/correct/SupportedAttributeTypes.cd");
  }

  @Test
  public void testHandwrittenMethods1() {
    testFalse("testmodels/incorrect/HandwrittenMethods1.cd");// CDStandardErrorCodes.IncompatibleReturnType);
  }

  @Test
  public void testHandwrittenMethods2() {
    testCorrect("testmodels/correct/HandwrittenMethods2.cd");
  }

  @Test
  public void testCorrect1() {
    testCorrect("testmodels/correct/Associations.cd");
  }

  @Test
  public void testCorrect2() {
    testCorrect("testmodels/correct/CascadeAndFetchStrategies.cd");
  }

  @Test
  public void testRandomAssocCombinations() {
    testCorrect("testmodels/correct/RandomAssocCombinations.cd");
  }

  @Test
  public void testCorrect3() {
    testCorrect("testmodels/correct/CascadeTypes.cd");
  }

  // Ticket #824(fixed), #877
  @Test
  public void testCorrect4() {
    testCorrect("testmodels/correct/CD2Alloy1_noOCL.cd");
  }

  @Test
  public void testCorrect5() {
    testCorrect("testmodels/correct/CD2Alloy1a.cd");
  }

  @Test
  public void testCorrect6() {
    testCorrect("testmodels/correct/CD2Alloy2_1.cd");
  }

  @Test
  public void testCorrect7() {
    testCorrect("testmodels/correct/CD2Alloy3_1.cd");
  }

  @Test
  public void testCorrect8() {
    testCorrect("testmodels/correct/CD2Alloy3.cd");
  }

  @Test
  public void testCorrect9() {
    testCorrect("testmodels/correct/DEC.cd");
  }

  @Test
  public void testCorrect10() {
    testCorrect("testmodels/correct/DeepEquals.cd");
  }

  @Test
  public void testCorrect12() {
    testCorrect("testmodels/correct/DML_test.cd");
  }

  @Test
  public void testCorrect13() {
    testCorrect("testmodels/correct/EAv2.cd");
  }

  @Test
  public void testCorrect14() {
    testCorrect("testmodels/correct/EllipsisType.cd");
  }

  @Test
  public void testCorrect15() {
    testCorrect("testmodels/correct/Enums.cd");
  }

  @Test
  public void testCorrect16() {
    testCorrect("testmodels/correct/IdGeneration.cd");
  }

  @Test
  public void testCorrect17() {
    testCorrect("testmodels/correct/IdGenerationStrategies.cd");
  }

  @Test
  public void testCorrect18() {
    testCorrect("testmodels/correct/InheritanceStrategy.cd");
  }

  @Test
  public void testCorrect19() {
    testCorrect("testmodels/correct/SelfAssociations.cd");
  }

  // TODO: failed; Ticket #533, #651(fixed)
  // @Test
  public void testCorrect21() {
    testCorrect("testmodels/correct/TripleLogo.cd");
  }

  // TODO: failed; Ticket #563(fixed), #564(fixed), 897
  // @Test
  public void testCorrect22() {
    testCorrect("testmodels/correct/AbstractSuperClassWithGenericInterfaces.cd");
  }

  @Test
  public void testCorrect23() {
    testCorrect("testmodels/correct/Embeddable.cd");
  }

  // TODO: failed; Ticket #891(fixed), #939(fixed), #940(fixed), #1149
  // @Test
  public void testCorrect24() {
    testCorrect("testmodels/correct/DeepRename.cd");
  }

  public void testCorrect25() {
    testCorrect("testmodels/correct/CinemaSystem.cd");
  }

  public void testCorrect26() {
    testCorrect("testmodels/correct/CD2Alloy1.cd");
  }

  // TODO: failed; Ticket #1138
  // @Test
  public void testCorrect27() {
    testCorrect("testmodels/correct/LibraryV5.cd");
  }

  @Test
  public void testCorrect28() {
    testCorrect("testmodels/correct/DEv2.cd");
  }

  // TODO: failed; Ticket #1140, #947
  // @Test
  public void testCorrect29() {
    testCorrect("testmodels/correct/BaseClasses.cd");
  }

  // TODO: failed; Ticket #1141
  // @Test
  public void testCorrect30() {
    testCorrect("testmodels/correct/Ordered.cd");
  }

  @Test
  public void testAttributes() {
    testCorrect("testmodels/correct/Attributes.cd");
  }

  @Test
  public void testFinalAttributes() {
    testCorrect("testmodels/correct/FinalAttributes.cd");
  }

  @Test
  public void testStaticAttributes() {
    testCorrect("testmodels/correct/StaticAttributes.cd");
  }

  @Test
  public void testStaticDerivedAttributes() {
    testCorrect("testmodels/correct/StaticDerivedAttributes.cd");
  }

  @Test
  public void testDerivedAttributes() {
    testCorrect("testmodels/correct/DerivedAttributes.cd");
  }

  @Test
  public void testFinalStaticAttributes() {
    testCorrect("testmodels/correct/FinalStaticAttributes.cd");
  }

  @Test
  public void testBaseClasses1() {
    testCorrect("testmodels/correct/BaseClasses1.cd");
  }

  @Test
  public void testMultipleMethodsWithoutBody() {
    testCorrect("testmodels/correct/MultipleMethodsWithoutBody.cd");
  }

  @Test
  public void testAttributeModifier() {
    testCorrect("testmodels/correct/AttributeModifier.cd");
  }

  @Test
  public void testInheritance() {
    testCorrect("testmodels/correct/Inheritance.cd");
  }

  @Test
  public void testInheritance2() {
    testCorrect("testmodels/correct/Inheritance2.cd");
  }

  @Test
  public void testTwoAssocsWithRole() {
    testCorrect("testmodels/correct/TwoAssocsWithRole.cd");
  }

  @Test
  public void testAssociationsToOrFromInterface() {
    testCorrect("testmodels/correct/AssociationsToOrFromInterface.cd");
  }

  @Test
  public void testInheritance3() {
    testCorrect("testmodels/correct/Inheritance3.cd");
  }

  // TODO: failed; Ticket #1149
  // @Test
  public void testInheritance4() {
    testCorrect("testmodels/correct/Inheritance4.cd");
  }

  @Test
  public void testAbstractHierarchy() {
    testCorrect("testmodels/correct/AbstractHierarchy.cd");
  }

  @Test
  public void testHandwrittenMethods() {
    testCorrect("testmodels/correct/HandwrittenMethods.cd");
  }

  @Test
  public void testQAssociations() {
    testCorrect("testmodels/correct/QAssociations.cd");
  }

  @Test
  public void testDerivedAssociations() {
    testCorrect("testmodels/correct/DerivedAssociations.cd");
  }

  @Test
  public void testExternalDataType() {
    testCorrect("testmodels/correct/ExternalDataType.cd"); // TODO
  }

  @Test
  public void testInterfaceMethod() {
    testCorrect("testmodels/correct/InterfaceMethod.cd");
  }

  @Test
  public void testAssocsFromAndToInterface() {
    testCorrect("testmodels/correct/AssocsFromAndToInterface.cd");
  }

  @Test
  public void testDummyValues1() {
    testCorrect("testmodels/correct/DummyValues1.cd");
  }

  // TODO: failed; Ticket #1134, #1135
  // @Test
  public void testHandWrittenConstructors() {
    testCorrect("testmodels/correct/DeepRename1.cd");
  }

  // @Test
  // public void testCoCoJavaKeywords() {
  // Set<IErrorCode> errorCodes = newHashSet();
  // errorCodes.add(ErrorCodeMontiCoreRE.DefaultErrorCode);
  // testFalse("testmodels/incorrect/LibraryV1.cd", 2, errorCodes, false);
  // }

  // TODO: failed; Ticket #1139
  // @Test
  public void testOcl1() {
    testCorrect("testmodels/correct/EM_WithOCL.cd");
  }

  // TODO: failed; Ticket #1139
  // @Test
  public void testOcl2() {
    testCorrect("testmodels/correct/CD2Alloy1_OCL.cd");
  }

  // @Test
  // public void tesImportedCDTypes() {
  // testCorrect("testmodels/correct/testimported",
  // "testmodels/correct/testimported");
  // }

  @Test
  public void testForTicket1221() {
    testCorrect("testmodels/correct/TestForTicket1221.cd");
  }

  @Test
  public void testForListTypesInModel() {
    testCorrect("testmodels/correct/ListTypesInModel.cd");
  }

  @Test
  public void testForSetTypesInModel() {
    testCorrect("testmodels/correct/SetTypesInModel.cd");
  }

  @Test
  public void testForSetWrapperTypes() {
    testCorrect("testmodels/correct/SetWrapperTypes.cd");
  }

  @Test
  public void testForSetPrimitiveTypes() {
    testCorrect("testmodels/correct/SetPrimitiveTypes.cd");
  }

  @Test
  public void testForListPrimitiveTypes() {
    testCorrect("testmodels/correct/ListPrimitiveTypes.cd");
  }

  // TODO: failed;
  // @Test
  public void testForOptionalType() {
    testCorrect("testmodels/correct/OptionalType.cd");
  }

  @Test
  public void testFinalStaticDerivedAttributes() {
    testFalse(
        "testmodels/correct/FinalStaticDerivedAttributes.cd");// CD4AnalysisErrorCodes.UnsupportedCombinationOfModifiers));
  }

  @Test
  public void testForbiddenAttributeModifier() {
    testFalse(
        "testmodels/correct/ForbiddenAttributeModifier.cd");// CD4AnalysisErrorCodes.UnsupportedCombinationOfModifiers));
  }

  @Test
  public void testFinalDerivedAttributes() {
    testFalse(
        "testmodels/correct/FinalDerivedAttributes.cd");// CD4AnalysisErrorCodes.UnsupportedCombinationOfModifiers));
  }

  @Test
  public void testCD4Acoreexamples() {
    // Set, List, Optional are not required to include as they are considered as built-in types
    testCorrect("testmodels/correct/CD4Acoreexamples.cd");
  }

  @Test
  public void testCD4Acoreexamples2() {
    // Set, List, Optional are not required to include as they are considered as built-in types
    testCorrect("testmodels/correct/CD4Acoreexamples2.cd");
  }

  @Test
  public void testCD4Acoreexamples3() {
    // Set, List, Optional are not required to include as they are considered as built-in types
    testCorrect("testmodels/correct/CD4Acoreexamples3.cd");
  }

  @Test
  public void testMyBlog() {
    testCorrect("testmodels/correct/MyBlog.cd");
  }

  @Test
  public void testCD4AOptionalPrimitive() {
    testCorrect("testmodels/correct/CD4AOptionalPrimitive.cd");
  }

  @Test
  public void testAssociationsToWrapperTypes() {
    testCorrect("testmodels/correct/AssociationsToWrapperTypes.cd"); // TODO
  }

  @Test
  public void testDexFlix() {
    testCorrect("testmodels/correct/DexFlix.cd");
  }

  @Test
  public void testDerivedBugged() {
    testCorrect("testmodels/correct/DerivedBugged.cd");
  }

  @Test
  public void testBugged() {
    testCorrect("testmodels/correct/Bugged.cd"); // TODO
  }

  @Test
  public void testTestCoverage() {
    testCorrect("testmodels/correct/Testcoverage.cd");
  }

  @Test
  public void testInterfaceBug() {
    testCorrect("testmodels/correct/InterfaceBug.cd");
  }

  @Test
  public void testQualifierAssocWithSetQualifier() {
    // should not be supported because the key is a list
    testFalse("testmodels/correct/QualifiedAssocWithSetQualifier.cd");
  }

  @Test
  public void testWB2DB() {
    testCorrect("testmodels/correct/WB2DB.cd");
  }

  //-- design patterns
  @Test
  public void testAbstractFactoryDiagram() {
    testCorrect("testmodels/designpatterns/AbstractFactoryDiagram.cd");
  }

  @Test
  public void testAdapterDiagram() {
    testCorrect("testmodels/designpatterns/AdapterDiagram.cd");
  }

  @Test
  public void testBridgeDiagram() {
    testCorrect("testmodels/designpatterns/BridgeDiagram.cd");
  }

  @Test
  public void testBuilderDiagram() {
    testCorrect("testmodels/designpatterns/BuilderDiagram.cd");
  }

  @Test
  public void testCommandDiagram() {
    testCorrect("testmodels/designpatterns/CommandDiagram.cd");
  }

  @Test
  public void testCommandProcessorDiagram() {
    testCorrect("testmodels/designpatterns/CommandProcessorDiagram.cd");
  }

  @Test
  public void testCompositeDiagram() {
    testCorrect("testmodels/designpatterns/CompositeDiagram.cd");
  }

  @Test
  public void testDecoratorDiagram() {
    testCorrect("testmodels/designpatterns/DecoratorDiagram.cd");
  }

  @Test
  public void testFactoryMethodDiagram() {
    testCorrect("testmodels/designpatterns/FactoryMethodDiagram.cd");
  }

  @Test
  public void testFlyweightDiagram() {
    testCorrect("testmodels/designpatterns/FlyweightDiagram.cd");
  }

  @Test
  public void testIteratorDiagram() {
    testCorrect("testmodels/designpatterns/IteratorDiagram.cd");
  }

  @Test
  public void testObjectPoolDiagram() {
    testCorrect("testmodels/designpatterns/ObjectPoolDiagram.cd");
  }

  @Test
  public void testObserverDiagram() {
    testCorrect("testmodels/designpatterns/ObserverDiagram.cd");
  }

  @Test
  public void testProxyDiagram() {
    testCorrect("testmodels/designpatterns/ProxyDiagram.cd");
  }

  @Test
  public void testStrategyDiagram() {
    testCorrect("testmodels/designpatterns/StrategyDiagram.cd");
  }

  @Test
  public void testVisitorDiagram() {
    testCorrect("testmodels/designpatterns/VisitorDiagram.cd");
  }

  @Test
  public void testArchitectureDiagram() {
    testCorrect("testmodels/diagramsforevaluation/Architecture.cd");
  }

  @Test
  public void testBankDiagram() {
    testCorrect("testmodels/diagramsforevaluation/BankDiagram.cd");
  }

  @Test
  public void testCreditDiagram() {
    testCorrect("testmodels/diagramsforevaluation/Credit.cd");
  }

  @Test
  public void testIsembardOSDiagram() {
    testCorrect("testmodels/diagramsforevaluation/IsembardOS.cd");
  }

  @Test
  public void testIPetShopDiagram() {
    testCorrect("testmodels/diagramsforevaluation/PetShop.cd");
  }

  @Test
  public void testPrivateBankDiagram() {
    testCorrect("testmodels/diagramsforevaluation/PrivateBank.cd");
  }

  // Fix this test, because it is a bug!
  @Test
  public void testRestaurantDiagram() {
    testCorrect("testmodels/diagramsforevaluation/Restaurant.cd");
  }

  // Fix this test, because it is a bug!
  @Test
  public void testSchoolDiagram() {
    testCorrect("testmodels/diagramsforevaluation/School.cd");
  }

  @Test
  public void testSocialNetworkDiagram() {
    testCorrect("testmodels/diagramsforevaluation/SocialNetwork.cd");
  }

  // Fix this test, because it is a bug!
  @Test
  public void testSpaceshipDiagram() {
    testCorrect("testmodels/diagramsforevaluation/Spaceship.cd");
  }

  @Test
  public void testStudentDiagram() {
    testCorrect("testmodels/diagramsforevaluation/Student.cd");
  }
}
