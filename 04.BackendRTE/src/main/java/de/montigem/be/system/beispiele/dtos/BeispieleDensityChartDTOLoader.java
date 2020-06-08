package de.montigem.be.system.beispiele.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeispieleDensityChartDTOLoader
    extends BeispieleDensityChartDTOLoaderTOP {

  public BeispieleDensityChartDTOLoader() {
    super();
  }

  public BeispieleDensityChartDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    List<BeispieleDensityChartEntryDTO> entries = new ArrayList<>();
    entries.add(new BeispieleDensityChartEntryDTO(0, "Bob", Arrays.asList(new BeispieleDensityChartEntryDataDTO(0, 5, 6), new BeispieleDensityChartEntryDataDTO(1, 6, 2))));
    entries.add(new BeispieleDensityChartEntryDTO(1, "Anna", Arrays.asList(new BeispieleDensityChartEntryDataDTO(0, 3, 5), new BeispieleDensityChartEntryDataDTO(1, 8, 2))));
    setDTO(new BeispieleDensityChartDTO(0, entries));
  }

  public BeispieleDensityChartDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    super(daoLib, id, securityHelper);
  }
}