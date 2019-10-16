/* (c) https://github.com/MontiCore/monticore */

package frontend.scripts

import backend.common.*
import backend.coretemplates.association.DefaultAssociationCardinality
import configure.ConfigureDexGenerator
import frontend.common.FrontendRenameDomainTrafo
import frontend.data.*
import frontend.data.validator.FrontendValidatorCreator

debug("Frontend nput file     : " + model.getAbsolutePath())
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("Handcoded path : " + _configuration.getHandcodedPathAsStrings())
debug("Template path  : " + _configuration.getTemplatePathAsStrings())

debug("Frontend: Start parsing " + model)
// parse the CD
cdAst = parseClassDiagram(model)
if (!cdAst.isPresent()) {
  error("Failed to parse " + model)
  return
}

// get ast 
cdAst = cdAst.get()

debug("Finished parsing [" + cdAst.getCDDefinition().getName() + "]")

// get symbol table
ConfigureDexGenerator.createCDSymbolTable(cdAst, modelPath)

debug("Checking CoCos");
// check CoCos on input model
checkMontiGemCoCos(cdAst)

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

/***********************************************************************************************************************
 * Generate the core of DEx
 **********************************************************************************************************************/
// clone ast
clonedCD = cdAst.deepClone()
ConfigureDexGenerator.createCDSymbolTable(clonedCD, modelPath)
topFlag = true

/*==================================================*
 * Generate Dataclass
 *==================================================*/
debug("\tDataclass transformations")

//new ModelCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).transform(cdAst)

//new ModelInterfaceCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

new DomainDTOCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

new FullDTOCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

new FullDTOListCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

new CommandCreateCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

new CommandDeleteCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

new CommandGetByIdCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

new CommandGetAllCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

new CommandUpdateCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

new CommandSetAttributeCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

debug ("\t\tParse OCL File: " + ocl)
// get ocl
oclAst = parseOCLFile(ocl, modelPath)

debug("\t\tParsed " + ocl + " successfully!")

new FrontendValidatorCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).input(oclAst).transform(cdAst)


/***********************************************************************************************************************
 * Rename domain if handwritten classes are present: It should be the last trafo
 **********************************************************************************************************************/
debug("Renaming the domain")

new FrontendRenameDomainTrafo().handcodedPath(handcodedPath).generateTOPClasses(topFlag).transform(cdAst)

/***********************************************************************************************************************
 * Finished transforming. Now start generating code
 **********************************************************************************************************************/
debug("Generating frontend files")

generateTypeScriptFiles(cdAst, ConfigureDexGenerator.getGlex(), modelPath, out, handcodedPath, templatePath)

info("Model " + model + " processed successfully!")
