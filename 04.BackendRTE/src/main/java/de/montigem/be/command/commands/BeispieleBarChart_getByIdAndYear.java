/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.command.commands;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.system.beispiele.dtos.BeispieleBarChartDTOLoader;
import de.montigem.be.util.DAOLib;

import java.util.List;

public class BeispieleBarChart_getByIdAndYear extends BeispieleBarChart_getByIdAndYearTOP {

  public BeispieleBarChart_getByIdAndYear(long personId, List<String> years) {
    super(personId, years);
  }

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) {
    if (years.isEmpty()) {
      return new BeispieleBarChartDTOLoader(daoLib, id, securityHelper).getDTO();
    }
    return new BeispieleBarChartDTOLoader(daoLib, id, securityHelper, Integer.parseInt(years.get(0))).getDTO();
  }

}
