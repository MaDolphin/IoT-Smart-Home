
package de.montigem.be.system.sensor.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.DAOLib;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GaugeChartDataDTOLoader extends GaugeChartDataDTOLoaderTOP {

    public GaugeChartDataDTOLoader() {
        super();
    }

    public GaugeChartDataDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
        String resource = securityHelper.getSessionCompliantResource();
        ZonedDateTime currentTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        SensorType sensorType1 = SensorType.TEMPERATURE;
        SensorType sensorType2 = SensorType.CO2;

        List<GaugeChartDataEntryDTO> values1 = daoLib.getSensorDAO().getListOfSensorIdsForType(resource, sensorType1).parallelStream()
                .map(sId -> daoLib.getSensorDAO().getValueInTimeById_GaugeChart(resource, currentTime, 3*60, sId, sensorType1))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
        //System.out.println("values1 length: " + values1.size());
        List<GaugeChartDataEntryDTO> values2 = daoLib.getSensorDAO().getListOfSensorIdsForType(resource, sensorType2).parallelStream()
                .map(sId -> daoLib.getSensorDAO().getValueInTimeById_GaugeChart(resource, currentTime, 3*60, sId, sensorType2))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
        //System.out.println("values2 length: " + values2.size());
        values1.addAll(values2);
        setDTO(new GaugeChartDataDTO(0, values1));

    }

    public GaugeChartDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }

}
