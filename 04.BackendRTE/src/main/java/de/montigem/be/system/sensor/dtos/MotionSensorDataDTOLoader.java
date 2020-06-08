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
        entries.add(new MotionSensorEntryDTO(0, "Bob", 5));
        entries.add(new MotionSensorEntryDTO(1, "Anna", 3));
        setDTO(new MotionSensorDataDTO(0, entries));
    }

    public MotionSensorDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }
}