/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.command.commands;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.system.common.dtos.StringContainerDTO;
import de.montigem.be.util.DAOLib;

import static de.montigem.be.command.commands.Info_buildTime.buildTime;
import static de.montigem.be.command.commands.Info_version.version;

public class Info_general extends Info_generalTOP {
  @Override
  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) {
    return new StringContainerDTO(0, general());
  }

  public static String general() {
    return version() + ", " + buildTime();
  }
}
