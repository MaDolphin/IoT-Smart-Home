
package de.montigem.be.system.sensor.dtos;

import java.time.ZonedDateTime;
import java.util.*;
import com.google.common.collect.*;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
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
    List<SensorValue> values = daoLib.getSensorDAO().getListOfSensorValueById("1");
    for(SensorValue ausgabe : values)
    {
      entries1.add(new RidgelineDataEntryDTO(1, ausgabe.getTimestamp().toInstant().toEpochMilli(), ausgabe.getValue()));

    }
    RidgelineDataDTO ridgeline1 = new RidgelineDataDTO(1, "Row 1", entries1);

    List<RidgelineDataDTO> ridgelines = new ArrayList<>();
    ridgelines.add(ridgeline1);

    RidgelineChartDataDTO chart = new RidgelineChartDataDTO(1, ridgelines);
    setDTO(chart);
  }
  
  public RidgelineChartDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper)
  {
    super(daoLib, id, securityHelper);
  }
  
}
