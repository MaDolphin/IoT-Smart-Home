/* (c) https://github.com/MontiCore/monticore */

package frontend.scripts

import backend.common.CollectionTypesWrapper
import backend.common.DefaultVisibilitySetter
import backend.common.DuplicateAttributesRemoverTrafo
import backend.common.TypesConverter
import common.util.TransformationUtils
import configure.ConfigureDexGenerator
import frontend.common.FrontendRenameDomainTrafo
import frontend.data.AggregateDTOCreator
import frontend.data.CommandGetByIdCreator
import frontend.data.CommandGetAllCreator
import frontend.data.CommandOfModelCreator
import frontend.data.DTOTypeResolverCreator


debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("Handcoded path : " + _configuration.getHandcodedPathAsStrings())
debug("Template path  : " + _configuration.getTemplatePathAsStrings())

pathMap = createImportMap()

while (models.hasNext()) {
  aggregate = models.next()
  debug("Frontend Aggregates: Start parsing " + aggregate)

  // parse the CD
  cdAst = parseClassDiagram(aggregate)
  if (!cdAst.isPresent()) {
    error("Failed to parse " + aggregate)
    return
  }

// get ast
  cdAst = cdAst.get()

  debug("Finished parsing [" + cdAst.getCDDefinition().getName() + "]")

// get symbol table
  ConfigureDexGenerator.createCDSymbolTable(cdAst, modelPath)

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

// add default types
  new TypesConverter().updateSymbolTable().transform(cdAst)

/***********************************************************************************************************************
 * Generate the core of DEx
 **********************************************************************************************************************/
// clone ast
  clonedCD = cdAst.deepClone()
  ConfigureDexGenerator.createCDSymbolTable(clonedCD, modelPath)
  topFlag = true
  clearClassDiagram(cdAst)

  getClassesFromAst(pathMap, clonedCD, TransformationUtils.DTOS_PACKAGE)

/*==================================================*
 * Generate Dataclass
 *==================================================*/
  debug("\tDataclass transformations")

 // new ModelCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).transform(cdAst)

 // new ModelInterfaceCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

  new AggregateDTOCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

  new CommandGetByIdCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

  new CommandGetAllCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

/***********************************************************************************************************************
 * Rename domain if handwritten classes are present: It should be the last trafo
 **********************************************************************************************************************/
  debug("Renaming the domain")

  new FrontendRenameDomainTrafo().handcodedPath(handcodedPath).generateTOPClasses(topFlag).transform(cdAst)

/***********************************************************************************************************************
 * Finished transforming. Now start generating code
 **********************************************************************************************************************/
  debug("Generating frontend files")

  aggregateCD(clonedCD)
  generateTypeScriptFiles(cdAst, ConfigureDexGenerator.getGlex(), modelPath, out, handcodedPath, templatePath)

  info("Model " + aggregate + " processed successfully!")

}

/***********************************************************************************************************************
 * Start generation for aggregated classes
 **********************************************************************************************************************/

debug("\tStart parsing command model " + cmdmodel)
// parse the CD
commandAst = parseClassDiagram(cmdmodel)
if (!commandAst.isPresent()) {
  error("Failed to parse " + cmdmodel)
  return
}
info("Command model " + cmdmodel + " processed successfully!")
commandSym = ConfigureDexGenerator.createCDSymbolTable(commandAst.get(), modelPath)
checkMontiGemCoCos(commandAst.get())
getClassesFromAst(pathMap, commandAst.get(), TransformationUtils.COMMANDS_PACKAGE)
genCmdAst = createClassDiagram()
new CommandOfModelCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(commandAst.get()).transform(genCmdAst)

debug("\tGenerating code for command model " + cmdmodel)
generateTypeScriptFiles(genCmdAst, ConfigureDexGenerator.getGlex(), modelPath, out, handcodedPath, templatePath)


debug("\tStart parsing domain " + model)
// parse the CD
domainAst = parseClassDiagram(model)
if (!domainAst.isPresent()) {
  error("Failed to parse " + model)
  return
}
info("Model " + model + " processed successfully!")

getClassesFromAst(pathMap, domainAst.get(), TransformationUtils.CLASSES_PACKAGE)

aggregateCD = createClassDiagram()
cdList = getParsedCds()

debug("\tGenerate DTOTypeResolverCreator")
new DTOTypeResolverCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).transform(aggregateCD, domainAst.get(), cdList)

generateTypeScriptFiles(aggregateCD, ConfigureDexGenerator.getGlex(), modelPath, out, handcodedPath, templatePath)

generateTSConfigFile(pathMap, out, handcodedPath)