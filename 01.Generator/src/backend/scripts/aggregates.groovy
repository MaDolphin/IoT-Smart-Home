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
  checkMaCoCoCoCos(cdAst)

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
checkMaCoCoCoCos(commandAst.get())
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
