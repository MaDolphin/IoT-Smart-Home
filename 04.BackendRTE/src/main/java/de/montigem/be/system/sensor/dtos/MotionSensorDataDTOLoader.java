package de.montigem.be.system.sensor.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

import java.util.ArrayList;
import java.util.List;

public class MotionSensorDataDTOLoader extends MotionSensorDataDTOLoaderTOP {

    public MotionSensorDataDTOLoader() {
        super();
    }

    public MotionSensorDataDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
        List<MotionSensorEntryDTO> entries = new ArrayList<>();
        entries.add(new MotionSensorEntryDTO(0, "Backend Sensor Bad", 1590544020));
        entries.add(new MotionSensorEntryDTO(1, "Backend Sensor Bad", 1590550200));
        entries.add(new MotionSensorEntryDTO(2, "Backend Sensor Bad", 1590568200));
        entries.add(new MotionSensorEntryDTO(3, "Backend Sensor Bad", 1590570360));
        entries.add(new MotionSensorEntryDTO(4, "Backend Sensor Bad", 1590575280));
        entries.add(new MotionSensorEntryDTO(5, "Backend Störung", 1590544920));
        entries.add(new MotionSensorEntryDTO(6, "Backend Störung", 1590546360));
        entries.add(new MotionSensorEntryDTO(7, "Backend Störung", 1590548040));
        entries.add(new MotionSensorEntryDTO(8, "Backend Störung", 1590547500));
        entries.add(new MotionSensorEntryDTO(9, "Backend Störung", 1590545880));
        entries.add(new MotionSensorEntryDTO(10, "Backend Störung", 1590545760));
        setDTO(new MotionSensorDataDTO(0, entries));
    }

    public MotionSensorDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }
}