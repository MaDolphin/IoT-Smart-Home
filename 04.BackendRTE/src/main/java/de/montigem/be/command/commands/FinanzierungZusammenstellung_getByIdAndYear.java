package de.montigem.be.command.commands;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.system.beispiele.dtos.FinanzierungZusammenstellungDTOLoader;
import de.montigem.be.util.DAOLib;

import java.util.List;

public class FinanzierungZusammenstellung_getByIdAndYear extends FinanzierungZusammenstellung_getByIdAndYearTOP {

  public FinanzierungZusammenstellung_getByIdAndYear(long personId, List<String> years) {
    super(personId, years);
  }

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) {
    if (years.isEmpty()) {
      return new FinanzierungZusammenstellungDTOLoader(daoLib, id, securityHelper).getDTO();
    }
    return new FinanzierungZusammenstellungDTOLoader(daoLib, id, securityHelper, Integer.parseInt(years.get(0))).getDTO();
  }

}
