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

package backend.scripts

import backend.commands.*
import backend.common.*
import backend.coretemplates.association.AssociationSupportTrafo
import backend.coretemplates.association.DefaultAssociationCardinality
import backend.data.builder.BuilderCreator
import backend.data.dataclass.AnnotationTrafo
import backend.data.dataclass.DataClassTrafo
import backend.data.dataclass.accessmethods.AccessMethodTrafo
import backend.data.persistence.dao.DaoCreator
import backend.data.proxy.ProxyCreator
import backend.data.test.BuilderTestClassCreator
import backend.data.test.DummyTestClassCreator
import backend.data.test.TestClassCreator
import backend.data.test.ValidatorTestClassCreator
import backend.data.validator.ValidatorCreator
import backend.dtos.DTOLoadersForDataclassCreator
import backend.dtos.DtoForDataclassAggregatesCreator
import backend.dtos.FullDTOLoadersForDataclassCreator
import backend.dtos.FullDtoCreator
import common.PackageTrafo
import configure.ConfigureDexGenerator

debug("Input file     : " + model.getAbsolutePath())
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("Test output dir: " + testOut.getAbsolutePath())
debug("Handcoded path : " + _configuration.getHandcodedPathAsStrings())
debug("Template path  : " + _configuration.getTemplatePathAsStrings())

debug("Start parsing " + model)
// parse the CD
cdAst = parseClassDiagram(model)
if (!cdAst.isPresent()) {
    error("Failed to parse " + model)
    return
}

def generateTests = false

// get ast 
cdAst = cdAst.get()

debug("Finished parsing [" + cdAst.getCDDefinition().getName() + "]")

// get symbol table
ConfigureDexGenerator.createCDSymbolTable(cdAst, modelPath)

// check CoCos on input model
checkMaCoCoCoCos(cdAst)

debug("Starting transformations")

/***********************************************************************************************************************
 * Do some pretransformations to adapt the input model
 **********************************************************************************************************************/
debug("\tPre transformations")

// remove attributes with the same name in a hierarchy
new DuplicateAttributesRemoverTrafo().transform(cdAst)

// set the default visibility to public
new DefaultVisibilitySetter().transform(cdAst)

// wrap primitive data types to data type objects
new CollectionTypesWrapper().transform(cdAst)

// set the default association cardinality to [*]
new DefaultAssociationCardinality().transform(cdAst)

// add default types
new TypesConverter().transform(cdAst)

// add universal attributes
new AdditionalAttributeTrafo().updateSymbolTable().transform(cdAst)

// clone ast
clonedCD = cdAst.deepClone()
clonedCDSym = ConfigureDexGenerator.createCDSymbolTable(clonedCD, modelPath)
topFlag = true

/*==================================================*
 * Generate Dataclass
 *==================================================*/

// modify class
new DataClassTrafo().transform(cdAst)

// add annotations to attributes
new AnnotationTrafo().handcodedPath(handcodedPath).generateTOPClasses(topFlag).transform(cdAst)

// generate access methods for attributes
new AccessMethodTrafo().transform(cdAst)

// generate methods and variables to handle associations
new AssociationSupportTrafo().updateSymbolTable().transform(cdAst)

/*==================================================*
 * Clone transformed AST
 *==================================================*/
transformedDomainAst = cdAst.deepClone();
transformedSymTab = ConfigureDexGenerator.createCDSymbolTable(transformedDomainAst, modelPath)

/*==================================================*
 * Generate Builder
 *==================================================*/
debug("\tBuilder creation")

// generate builders for domain classes
new BuilderCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

/*==================================================*
 * Generate Validator
 *==================================================*/
debug("\tValidator creation")

debug ("\t\tParse OCL File")
// get ocl
oclAst = parseOCLFile(ocl, modelPath)

debug("\t\tParsed " + ocl + " successfully!")

// generate validators for domain classes
new ValidatorCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).input(oclAst).transform(cdAst)

/*==================================================*
* Generate Proxy
*==================================================*/
debug("\tProxy creation")

// generate proxy objects
new ProxyCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

/*==================================================*
 * Set package for all generated classes
 *==================================================*/
debug("Setting package")

new PackageTrafo().transform(cdAst)

/*==================================================*
 * Generate Persistence
 *==================================================*/
debug("\tDAOs creation")

// generate DAOs for domain classes
new DaoCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

debug("\tAggregate DTOs creation")
new DtoForDataclassAggregatesCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

// generate FullDtos for domain classes
new FullDtoCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

debug("\tDTOLoader classes creation")
new DTOLoadersForDataclassCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

debug("\tFullDTOLoader classes creation")
new FullDTOLoadersForDataclassCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

/*==================================================*
 * Generate Commands
 *==================================================*/
debug("\tCommands creation")
new CommandGetByIdCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)
new CommandCreateCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)
new CommandDeleteCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)
new CommandUpdateCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)
new CommandSetAttributeCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

/***********************************************************************************************************************
 * Rename domain if handwritten classes are present: It should be the last trafo
 **********************************************************************************************************************/
debug("Renaming the domain")

new RenameDomainTrafo().handcodedPath(handcodedPath).generateTOPClasses(topFlag).transform(cdAst)

/***********************************************************************************************************************
 * Finished transforming. Now start generating code
 **********************************************************************************************************************/
debug("Generating files")

generateJavaFiles(cdAst, ConfigureDexGenerator.getGlex(), modelPath, out, handcodedPath, templatePath)

/***********************************************************************************************************************
 * Generate Tests
 **********************************************************************************************************************/
debug("Starting tests")

// create ast for tests
testAst = createNewAst("testAst", "de.macoco.be.domain")

// get symbol table
ConfigureDexGenerator.createCDSymbolTable(cdAst, modelPath)

if (generateTests) {
    debug("\tCreate tests for data classes")
    new TestClassCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(testAst)

    debug("\tCreate tests for builder classes")
    new BuilderTestClassCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(testAst)

    debug("\tCreate tests for validator classes")
    new ValidatorTestClassCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(testAst)

}

new DummyTestClassCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(testAst, transformedDomainAst)

/***********************************************************************************************************************
 * Start generating test code
 **********************************************************************************************************************/
// set package
new PackageTrafo().transform(testAst)

debug("Generating test files")
generateJavaFiles(testAst, ConfigureDexGenerator.getGlex(), modelPath, testOut, handcodedPath, templatePath)

info("Model " + model + " processed successfully!")
