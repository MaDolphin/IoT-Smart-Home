package de.montigem.be.system.sensor.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

import java.util.ArrayList;
import java.util.List;

public class DensityChartDataDTOLoader extends DensityChartDataDTOLoaderTOP {

    public DensityChartDataDTOLoader() {
        super();
    }

    public DensityChartDataDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
        List<DensityChartEntryDTO> entries = new ArrayList<>();
        entries.add(new DensityChartEntryDTO(0, "Bob", 5));
        entries.add(new DensityChartEntryDTO(1, "Anna", 3));
        setDTO(new DensityChartDataDTO(0, entries));
    }

    public DensityChartDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }
}