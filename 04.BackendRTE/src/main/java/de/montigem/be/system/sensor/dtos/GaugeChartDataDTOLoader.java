
package de.montigem.be.system.sensor.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

import java.util.ArrayList;
import java.util.List;

public class GaugeChartDataDTOLoader extends GaugeChartDataDTOLoaderTOP {

    public GaugeChartDataDTOLoader() {
        super();
    }

    public GaugeChartDataDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
        //super(daoLib, securityHelper);
        List<GaugeChartDataEntryDTO> entries = new ArrayList<>();
        entries.add(new GaugeChartDataEntryDTO(0, "Kitchen", 17));
        entries.add(new GaugeChartDataEntryDTO(1, "Bathroom", 25));
        entries.add(new GaugeChartDataEntryDTO(2, "Bedroom", 10));
        entries.add(new GaugeChartDataEntryDTO(3, "Office", 13));
        setDTO(new GaugeChartDataDTO(0, entries));
    }

    public GaugeChartDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }

}
