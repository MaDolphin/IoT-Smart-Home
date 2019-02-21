
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved. http://www.se-rwth.de/
 */
package de.montigem.be.system.common.commands;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.command.rte.general.CommandDTO;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.system.common.dtos.StringContainerDTOLoader;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;

;

public class StringContainer_getAll
    
    extends CommandDTO

{
  
  protected long objectId;
  
  public StringContainer_getAll()
  
  {
    this.typeName = "StringContainer_getAll";
  }
  
  public StringContainer_getAll(long objectId)
  
  {
    this();
    
    this.objectId = objectId;
  }
  
  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError
  
  {
    Log.info("MAB0x9020: StringContainer_getAll.doRun: objectId: " + objectId, "StringContainer_getAll");
    return new StringContainerDTOLoader(daoLib, securityHelper).getDTO();
  }
  
  public long getObjectId() {
    return this.objectId;
  }
  
}
