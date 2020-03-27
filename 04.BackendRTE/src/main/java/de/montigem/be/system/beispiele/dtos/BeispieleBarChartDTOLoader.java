/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.system.beispiele.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;
import java.util.ArrayList;
import java.util.List;

public class BeispieleBarChartDTOLoader extends BeispieleBarChartDTOLoaderTOP {

  public BeispieleBarChartDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    super(daoLib, securityHelper);
  }

  public BeispieleBarChartDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    List<BeispieleBarChartEntryDTO> entries = new ArrayList<>();
    entries.add(new BeispieleBarChartEntryDTO(0, 2019));
    entries.add(new BeispieleBarChartEntryDTO(1, 2019));
    entries.add(new BeispieleBarChartEntryDTO(2, 2019));
    entries.add(new BeispieleBarChartEntryDTO(3, 2019));
    setDTO(new BeispieleBarChartDTO(0, entries));
  }

  public BeispieleBarChartDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper, int year) {
    List<BeispieleBarChartEntryDTO> entries = new ArrayList<>();
    entries.add(new BeispieleBarChartEntryDTO(0, year));
    entries.add(new BeispieleBarChartEntryDTO(1, year));
    entries.add(new BeispieleBarChartEntryDTO(2, year));
    entries.add(new BeispieleBarChartEntryDTO(3, year));
    setDTO(new BeispieleBarChartDTO(0, entries));
  }

}
