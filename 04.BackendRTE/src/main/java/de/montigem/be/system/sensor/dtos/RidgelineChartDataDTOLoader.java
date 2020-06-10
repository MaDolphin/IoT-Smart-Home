
package de.montigem.be.system.sensor.dtos;

import java.time.ZonedDateTime;
import java.util.*;
import com.google.common.collect.*;
import de.montigem.be.system.sensor.dtos.RidgelineDataDTO;
import de.montigem.be.dtos.rte.DTOLoader;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.authz.Permissions;

public class RidgelineChartDataDTOLoader extends RidgelineChartDataDTOLoaderTOP {
  public RidgelineChartDataDTOLoader()
  {
    super();
  }
  
  public RidgelineChartDataDTOLoader(DAOLib daoLib, SecurityHelper securityHelper)
  {
    // Example implementation with two ridgelines each with three data points
    ZonedDateTime date = ZonedDateTime.now();
    double seconds = date.toInstant().toEpochMilli();
    System.out.println("Seconds:"+seconds);

    List<RidgelineDataEntryDTO> entries1 = new ArrayList<>();
    entries1.add(new RidgelineDataEntryDTO(0, seconds, 0));
    entries1.add(new RidgelineDataEntryDTO(1, seconds+1000, 3));
    entries1.add(new RidgelineDataEntryDTO(2, seconds+2000, 1));
    RidgelineDataDTO ridgeline1 = new RidgelineDataDTO(0, "Row 1", entries1);

    List<RidgelineDataEntryDTO> entries2 = new ArrayList<>();
    entries2.add(new RidgelineDataEntryDTO(0, seconds+0, 5));
    entries2.add(new RidgelineDataEntryDTO(1, seconds+1000, 3));
    entries2.add(new RidgelineDataEntryDTO(2, seconds+2000, 4));
    RidgelineDataDTO ridgeline2 = new RidgelineDataDTO(1, "Row 2", entries2);

    List<RidgelineDataDTO> ridgelines = new ArrayList<>();
    ridgelines.add(ridgeline1);
    ridgelines.add(ridgeline2);

    RidgelineChartDataDTO chart = new RidgelineChartDataDTO(0, ridgelines);
    setDTO(chart);
  }
  
  public RidgelineChartDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper)
  {
    super(daoLib, id, securityHelper);
  }
  
}
