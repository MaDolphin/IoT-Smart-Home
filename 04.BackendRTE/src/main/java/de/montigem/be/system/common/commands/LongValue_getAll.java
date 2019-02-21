
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved. http://www.se-rwth.de/
 */
package de.montigem.be.system.common.commands;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.command.rte.general.CommandDTO;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.error.MaCoCoError;
import de.montigem.be.system.common.dtos.LongValueDTOLoader;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;

;

public class LongValue_getAll
    
    extends CommandDTO

{
  
  protected long objectId;
  
  public LongValue_getAll()
  
  {
    this.typeName = "LongValue_getAll";
  }
  
  public LongValue_getAll(long objectId)
  
  {
    this();
    
    this.objectId = objectId;
  }
  
  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MaCoCoError
  
  {
    Log.info("MAB0x9020: LongValue_getAll.doRun: objectId: " + objectId, "LongValue_getAll");
    return new LongValueDTOLoader(daoLib, securityHelper).getDTO();
  }
  
  public long getObjectId() {
    return this.objectId;
  }
  
}
