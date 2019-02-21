
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved. http://www.se-rwth.de/
 */
package de.montigem.be.system.common.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.dtos.rte.DTOLoader;
import de.montigem.be.util.DAOLib;

public class LongValueDTOLoader
    
    extends DTOLoader<LongValueDTO>

{
  
  public LongValueDTOLoader()
  
  {
    // default empty body
  }
  
  public LongValueDTOLoader(DAOLib daoLib, SecurityHelper securityHelper)
  
  {
    super(daoLib, securityHelper);
  }
  
  public LongValueDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper)
  
  {
    super(daoLib, id, securityHelper);
  }
  
}
