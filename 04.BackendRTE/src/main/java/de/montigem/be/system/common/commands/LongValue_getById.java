
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved. http://www.se-rwth.de/
 */
package de.montigem.be.system.common.commands;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.command.rte.general.CommandDTO;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.system.common.dtos.LongValueDTOLoader;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;

;

public class LongValue_getById
    
    extends CommandDTO

{
  
  protected long objectId;
  
  public LongValue_getById()
  
  {
    this.typeName = "LongValue_getById";
  }
  
  public LongValue_getById(long objectId)
  
  {
    this();
    
    this.objectId = objectId;
  }
  
  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError
  
  {
    Log.info("MAB0x9020: LongValue_getById.doRun: objectId: " + objectId, "LongValue_getById");
    return new LongValueDTOLoader(daoLib, objectId, securityHelper).getDTO();
  }
  
  public long getObjectId() {
    return this.objectId;
  }
  
}
