package de.montigem.be.system.sensor.dtos;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.system.einstellungen.dtos.*;
import de.montigem.be.util.DAOLib;

import java.util.ArrayList;
import java.util.List;

public class MotionSensorDataDTOLoader extends MotionSensorDataDTOLoaderTOP {

    public MotionSensorDataDTOLoader() {
        super();
    }

    public MotionSensorDataDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
        MotionSensorDataDTO dto = new MotionSensorDataDTO();
        List<Sensor> sensors = daoLib.getSensorDAO().getListOfSensorsForType(securityHelper.getSessionCompliantResource(), SensorType.MOTION);
        sensors.forEach((sensor) -> {
            List<SensorValue> entryValues = sensor.getValues();
            entryValues.forEach((entryValue)->{
                MotionSensorEntryDTO entryDTO = new MotionSensorEntryDTO();
                entryDTO.setTimestamp((int) entryValue.getTimestamp().toEpochSecond());
                entryDTO.setName(sensor.getSensorId());
                entryDTO.setId(entryValue.getId());
                dto.getEntries().add(entryDTO);
            });
        });
        setDTO(dto);

    }

    public MotionSensorDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }
}