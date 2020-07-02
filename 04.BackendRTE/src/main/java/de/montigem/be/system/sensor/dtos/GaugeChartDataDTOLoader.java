
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
        //super(daoLib, securityHelper);
//        List<GaugeChartDataEntryDTO> entries = new ArrayList<>();
//        entries.add(new GaugeChartDataEntryDTO(0, "Kitchen", 17));
//        entries.add(new GaugeChartDataEntryDTO(1, "Bathroom", 25));
//        entries.add(new GaugeChartDataEntryDTO(2, "Bedroom", 10));
//        entries.add(new GaugeChartDataEntryDTO(3, "Office", 13));
//        setDTO(new GaugeChartDataDTO(0, entries));

        String resource = securityHelper.getSessionCompliantResource();
        ZonedDateTime currentTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        SensorType sensorType1 = SensorType.TEMPERATURE;
        SensorType sensorType2 = SensorType.CO2;

        List<GaugeChartDataEntryDTO> values1 = daoLib.getSensorDAO().getListOfSensorIdsForType(resource, sensorType1).parallelStream()
                .map(sId -> daoLib.getSensorDAO().getValueInTimeById_GaugeChart(resource, currentTime, 1, sId, sensorType1))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
        List<GaugeChartDataEntryDTO> values2 = daoLib.getSensorDAO().getListOfSensorIdsForType(resource, sensorType2).parallelStream()
                .map(sId -> daoLib.getSensorDAO().getValueInTimeById_GaugeChart(resource, currentTime, 1, sId, sensorType2))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
        values1.addAll(values2);
        setDTO(new GaugeChartDataDTO(0, values1));

    }

    public GaugeChartDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }

}
