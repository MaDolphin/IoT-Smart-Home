package de.montigem.be.system.beispiele.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;
import java.util.ArrayList;
import java.util.List;

public class FinanzierungZusammenstellungDTOLoader extends FinanzierungZusammenstellungDTOLoaderTOP {

  public FinanzierungZusammenstellungDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    super(daoLib, securityHelper);
  }

  public FinanzierungZusammenstellungDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    List<FinanzierungZusammenstellungEntryDTO> entries = new ArrayList<>();
    entries.add(new FinanzierungZusammenstellungEntryDTO(0, 2019));
    entries.add(new FinanzierungZusammenstellungEntryDTO(1, 2019));
    entries.add(new FinanzierungZusammenstellungEntryDTO(2, 2019));
    entries.add(new FinanzierungZusammenstellungEntryDTO(3, 2019));
    setDTO(new FinanzierungZusammenstellungDTO(0, entries));
  }

  public FinanzierungZusammenstellungDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper, int year) {
    List<FinanzierungZusammenstellungEntryDTO> entries = new ArrayList<>();
    entries.add(new FinanzierungZusammenstellungEntryDTO(0, year));
    entries.add(new FinanzierungZusammenstellungEntryDTO(1, year));
    entries.add(new FinanzierungZusammenstellungEntryDTO(2, year));
    entries.add(new FinanzierungZusammenstellungEntryDTO(3, year));
    setDTO(new FinanzierungZusammenstellungDTO(0, entries));
  }

}
