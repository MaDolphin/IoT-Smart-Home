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

package frontend.scripts

import backend.common.CollectionTypesWrapper
import backend.common.DefaultVisibilitySetter
import backend.common.DuplicateAttributesRemoverTrafo
import backend.common.TypesConverter
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

aggregateCD = createClassDiagram()
cdList = getParsedCds()

debug("\tGenerate DTOTypeResolverCreator")
new DTOTypeResolverCreator().handcodedPath(handcodedPath).generateTOPClasses(topFlag).transform(aggregateCD, domainAst.get(), cdList)

generateTypeScriptFiles(aggregateCD, ConfigureDexGenerator.getGlex(), modelPath, out, handcodedPath, templatePath)
