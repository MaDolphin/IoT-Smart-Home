package de.montigem.be.system.beispiele.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

import java.util.Arrays;

public class BeispieleLineChartDTOLoader extends BeispieleLineChartDTOLoaderTOP {

  public BeispieleLineChartDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    super(daoLib, id, securityHelper);
  }

  public BeispieleLineChartDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    setDTO(createDTO());
  }

  private BeispieleLineChartDTO createDTO() {
    BeispieleLineChartDTO dto = new BeispieleLineChartDTO();

    BeispieleLineChartEntryDataDTO[] data1 = new BeispieleLineChartEntryDataDTO[] {
        new BeispieleLineChartEntryDataDTO(0, 0, 0),
        new BeispieleLineChartEntryDataDTO(1, 1, 1),
        new BeispieleLineChartEntryDataDTO(2, 2, 2)
    };

    BeispieleLineChartEntryDataDTO[] data2 = new BeispieleLineChartEntryDataDTO[] {
        new BeispieleLineChartEntryDataDTO(0, 0, 2),
        new BeispieleLineChartEntryDataDTO(1, 1, 1),
        new BeispieleLineChartEntryDataDTO(2, 2, 0)
    };

    BeispieleLineChartEntryDTO[] entries = new BeispieleLineChartEntryDTO[] {
        new BeispieleLineChartEntryDTO(0, "Label 1", Arrays.asList(data1)),
        new BeispieleLineChartEntryDTO(0, "Label 2", Arrays.asList(data2)),
    };

    dto.getEntries().addAll(Arrays.asList(entries));

    return dto;
  }

}
