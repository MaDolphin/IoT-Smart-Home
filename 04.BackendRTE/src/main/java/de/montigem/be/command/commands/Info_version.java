/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.command.commands;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.config.Config;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.system.common.dtos.StringContainerDTO;
import de.montigem.be.util.DAOLib;

public class Info_version extends Info_versionTOP {
  @Override
  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) {
    return new StringContainerDTO(0, version());
  }

  public static String version() {
    return "MaCoCo V" + Config.VERSION;
  }
}
