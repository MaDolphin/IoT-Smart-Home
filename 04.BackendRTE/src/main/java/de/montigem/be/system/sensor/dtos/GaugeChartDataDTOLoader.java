
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
        SensorType sensorType = SensorType.TEMPERATURE;

        List<GaugeChartDataEntryDTO> values = daoLib.getSensorDAO().getListOfSensorIdsForType(resource, sensorType).parallelStream()
                .map(sId -> daoLib.getSensorDAO().getValueInTimeById_GaugeChart(resource, currentTime, 5, sId))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());
        setDTO(new GaugeChartDataDTO(0, values));

    }

    public GaugeChartDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }

}
