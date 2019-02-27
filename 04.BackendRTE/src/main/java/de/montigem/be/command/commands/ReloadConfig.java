/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.command.commands;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.config.ConfigInitializer;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.system.common.dtos.StringContainerDTO;
import de.montigem.be.util.DAOLib;

public class ReloadConfig extends ReloadConfigTOP {

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) {
    // TODO SVa: user specific role (settings)
    if (securityHelper.doesUserHaveRole(Roles.ADMIN, ObjectClasses.USER)) {
      return new StringContainerDTO(0, ConfigInitializer.initializeConstants());
    }
    else {
      return new ErrorDTO("0xB1560", MontiGemErrorFactory.forbidden("Config"));
    }
  }
}
