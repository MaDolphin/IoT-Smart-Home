/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package backend.scripts


import backend.commands.CommandDTOTypeAdapterTrafo
import backend.commands.DTOTypeAdapterTrafo
import backend.common.*
import backend.coretemplates.association.DefaultAssociationCardinality
import backend.dtos.*
import configure.ConfigureDexGenerator

debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("Test output dir: " + testOut.getAbsolutePath())
debug("Handcoded path : " + _configuration.getHandcodedPathAsStrings())
debug("Template path  : " + _configuration.getTemplatePathAsStrings())

while (models.hasNext()) {
  aggregate = models.next()
  debug("Backend Aggregates: Start parsing " + aggregate)

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

// check CoCos on input aggregate
  checkMontiGemCoCos(cdAst)

  debug("Starting transformations")

/***********************************************************************************************************************
 * Do some pretransformations to adapt the input aggregate
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
  new TypesConverter().updateSymbolTable().transform(cdAst)

// clone ast
  clonedCD = cdAst.deepClone()
  clonedCDSym = ConfigureDexGenerator.createCDSymbolTable(clonedCD, modelPath)
  topFlag = true

/*==================================================*
 * Generate dtos infrastructure
 *==================================================*/
  debug("\tAggregate DTOs creation")
  new DtoForAggregatesCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

  debug("\tAggregate DTO classes creation")
  new DTOLoadersCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

  /*==================================================*
 * Generate Commands
 *==================================================*/
  debug("\tCommands creation")
    new CommandDTOGetByIdCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)
    new CommandDTOGetAllCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(clonedCD).transform(cdAst)

  debug("Exclude domain classes for aggregate models")
  new ExcludeDomainTrafo().transform(cdAst)

/***********************************************************************************************************************
 * Rename domain if handwritten classes are present: It should be the last trafo
 **********************************************************************************************************************/
  debug("Renaming")

  new RenameDomainTrafo().handcodedPath(handcodedPath).generateTOPClasses(topFlag).transform(cdAst)

/***********************************************************************************************************************
 * Finished transforming. Now start generating code
 **********************************************************************************************************************/
  debug("Generating files")

  aggregateCD(clonedCD)
  generateJavaFiles(cdAst, ConfigureDexGenerator.getGlex(), modelPath, out, handcodedPath, templatePath)


  info("Model " + aggregate + " processed successfully!")

}

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
genCmdAst = commandAst.get().deepClone()
genCmdAst.getCDDefinition().clearCDClasss()
new CommandOfModelCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(commandAst.get()).transform(genCmdAst)

generateJavaFiles(genCmdAst, ConfigureDexGenerator.getGlex(), modelPath, out, handcodedPath, templatePath)


debug("\tStart parsing domain " + model)
// parse the CD
domainAst = parseClassDiagram(model)
if (!domainAst.isPresent()) {
    error("Failed to parse " + model)
    return
}
info("Model " + model + " processed successfully!")
domainCDSym = ConfigureDexGenerator.createCDSymbolTable(domainAst.get(), modelPath)

debug("Generate classdiagram for aggregates")
aggregateCD = createClassDiagram()
cdList = getParsedCds()

debug("\tGenerate DAOLib")
new DaoLibTrafo().handcodedPath(handcodedPath).generateTOPClasses(topFlag).transform(aggregateCD, domainAst.get(), cdList)

debug("\tGenerate CommandDTOTypeAdapter")
new CommandDTOTypeAdapterTrafo().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(domainAst.get(), domainCDSym).transform(aggregateCD, domainAst.get(), genCmdAst, cdList)

debug("\tGenerate DTOTypeAdapter")
new DTOTypeAdapterTrafo().handcodedPath(handcodedPath).generateTOPClasses(topFlag).input(domainAst.get(), domainCDSym).transform(aggregateCD, domainAst.get(), cdList)

debug("Generate files")
generateJavaFiles(aggregateCD, ConfigureDexGenerator.getGlex(), modelPath, out, handcodedPath, templatePath)
