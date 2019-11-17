package de.montigem.be.system.beispiele.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

import java.util.Calendar;

public class FinanzierungsJahreDTOLoader extends FinanzierungsJahreDTOLoaderTOP {

  private static int getPreviousYear() {
    Calendar prevYear = Calendar.getInstance();
    prevYear.add(Calendar.YEAR, -1);
    return prevYear.get(Calendar.YEAR);
  }

  private static int getNextYear() {
    Calendar prevYear = Calendar.getInstance();
    prevYear.add(Calendar.YEAR, 1);
    return prevYear.get(Calendar.YEAR);
  }

  public FinanzierungsJahreDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    setDTO(new FinanzierungsJahreDTO(0, getPreviousYear(), getNextYear()));
  }

  public FinanzierungsJahreDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    setDTO(new FinanzierungsJahreDTO(0, getPreviousYear(), getNextYear()));
  }

}
