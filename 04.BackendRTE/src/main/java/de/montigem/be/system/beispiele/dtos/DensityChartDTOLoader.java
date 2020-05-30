
package de.montigem.be.system.sensor.dtos;

import java.time.ZonedDateTime;
import java.util.*;
import com.google.common.collect.*;
import de.montigem.be.system.sensor.dtos.DensityChartEntryDTO;
import de.montigem.be.dtos.rte.DTOLoader;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.authz.Permissions;

public class DensityChartDTOLoader
    
    extends DTOLoader<DensityChartDTO>

{
  
  public DensityChartDTOLoader()
  
  {
    // default empty body
  }
  
  public DensityChartDTOLoader(DAOLib daoLib, SecurityHelper securityHelper)
  
  {
    super(daoLib, securityHelper);
  }
  
  public DensityChartDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper)
  
  {
    super(daoLib, id, securityHelper);
  }
  
}
