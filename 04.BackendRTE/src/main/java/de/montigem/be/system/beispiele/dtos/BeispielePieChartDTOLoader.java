/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.system.beispiele.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

public  class BeispielePieChartDTOLoader extends BeispielePieChartDTOLoaderTOP {

  public BeispielePieChartDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    super(daoLib, securityHelper);
  }

  public BeispielePieChartDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    setDTO(new BeispielePieChartDTO());
  }

}
