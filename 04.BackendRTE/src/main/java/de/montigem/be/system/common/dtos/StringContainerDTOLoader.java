
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved. http://www.se-rwth.de/
 */
package de.montigem.be.system.common.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.dtos.rte.DTOLoader;
import de.montigem.be.util.DAOLib;

public class StringContainerDTOLoader
    
    extends DTOLoader<StringContainerDTO>

{
  
  public StringContainerDTOLoader()
  
  {
    // default empty body
  }
  
  public StringContainerDTOLoader(DAOLib daoLib, SecurityHelper securityHelper)
  
  {
    super(daoLib, securityHelper);
  }
  
  public StringContainerDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper)
  
  {
    super(daoLib, id, securityHelper);
  }
  
}
