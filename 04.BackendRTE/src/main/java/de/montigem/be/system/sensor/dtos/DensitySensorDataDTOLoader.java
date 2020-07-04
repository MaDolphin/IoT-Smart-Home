package de.montigem.be.system.sensor.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.util.DAOLib;

import java.util.List;

public class DensitySensorDataDTOLoader extends DensitySensorDataDTOLoaderTOP {

    public DensitySensorDataDTOLoader() {
        super();
    }

    public DensitySensorDataDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
        DensitySensorDataDTO dto = new DensitySensorDataDTO();
        List<Sensor> sensors = daoLib.getSensorDAO().getListOfSensorsForType(securityHelper.getSessionCompliantResource(), SensorType.CO2);
        List<Sensor> sensors2 = daoLib.getSensorDAO().getListOfSensorsForType(securityHelper.getSessionCompliantResource(), SensorType.TEMPERATURE);
        sensors.forEach((sensor) -> {
            List<SensorValue> entryValues = sensor.getValues();
            entryValues.forEach((entryValue)->{
                DensitySensorEntryDTO entryDTO = new DensitySensorEntryDTO();
                entryDTO.setValue(entryValue.getValue());
                entryDTO.setName(sensor.getSensorId());
                entryDTO.setId(entryValue.getId());
                dto.getEntries().add(entryDTO);
            });
        });
        sensors2.forEach((sensor) -> {
            List<SensorValue> entryValues = sensor.getValues();
            entryValues.forEach((entryValue)->{
                DensitySensorEntryDTO entryDTO = new DensitySensorEntryDTO();
                entryDTO.setValue(entryValue.getValue());
                entryDTO.setName(sensor.getSensorId());
                entryDTO.setId(entryValue.getId());
                dto.getEntries().add(entryDTO);
            });
        });
        setDTO(dto);
    }

    public DensitySensorDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }
}
