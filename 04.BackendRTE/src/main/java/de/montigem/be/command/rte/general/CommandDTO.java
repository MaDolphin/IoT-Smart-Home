/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.command.rte.general;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang.NotImplementedException;

import java.util.Optional;

public abstract class CommandDTO {

  protected String typeName;

  private long commandId = -1;

  // Diese Methode muss immer überschrieben werden.
  // Sie kann aber hier nicht abstrakt sein, weil generierte Subklassen eine Impmentierung brauchen
  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib)
      throws MontiGemError {
    String res = "MAB0x9001: No implementation for operation " + getClass().getName();
    Log.warn(res);
    throw new NotImplementedException(res);
  }

  public DTO undoRun(SecurityHelper securityHelper, DAOLib daoLib) {
    String res = "MAB0x9000: No undo for operation " + getClass().getName() + "available";
    Log.warn(res);
    throw new NotImplementedException(res);
  }

  public long getCommandId() {
    return commandId;
  }

  public void setCommandId(long commandId) {
    this.commandId = commandId;
  }

  protected abstract Optional<DTO> checkContract();
}
