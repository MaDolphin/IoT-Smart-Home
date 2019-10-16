/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.command.rte.general;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.command.response.CommandResponseDTO;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.NotImplementedDTO;
import de.montigem.be.error.JsonException;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.marshalling.JsonMarshal;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang.NotImplementedException;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class CommandManager {

  @Inject
  private DAOLib daoLib;

  @Inject
  private SecurityHelper securityHelper;

  /**
   * run all given commands
   *
   * @param commandDTOList
   * @return
   */
  public CommandResponseDTO runCommands(CommandDTOList commandDTOList) {
    // create an empty response with the id to identify the response on the other end
    CommandResponseDTO response = new CommandResponseDTO(commandDTOList.getId());
    // EntityTransaction transaction = em.getTransaction();

    Log.debug("MAB0xA003: Start transaction", getClass().getName());

    long cmdId = -1;

    try {
      // iterate all the commands
      for (CommandDTO command : commandDTOList.getCommands()) {
        cmdId = command.getCommandId();
        DTO result;
        try {
          result = command.doRun(securityHelper, daoLib);
        }
        catch (NotImplementedException e) {
          result = new NotImplementedDTO(command.typeName);
        }
        response.addResponse(command.getCommandId(), result);
      }

      // transaction.commit();
      Log.debug("MAB0xA004: Transaction commited", getClass().getName());
    }
    catch (NullPointerException np) {
      response.clear();
      response.addResponse(cmdId, MontiGemErrorFactory.unknown("" + np + ": " + np.getStackTrace()[0]));
      Log.debug("MAB0xA005: Error in transaction: " + np.getMessage(), getClass().getName());
    }
    catch (MontiGemError e) {
      // transaction.rollback();
      response.clear();
      response.addResponse(cmdId, e);
      Log.debug("MAB0xA006: Error in transaction: " + e.getMessage(), getClass().getName());
    }
    catch (Exception e) {
      // transaction.rollback();
      response.clear();
      response.addResponse(cmdId, MontiGemErrorFactory.exceptionCaught(e));
      Log.debug("MAB0xA007: Error in transaction: " + e.getMessage(), getClass().getName());
    }

    return response;
  }

  /**
   * deserialize and run commands
   *
   * @param jsonCmd
   * @return
   */
  public CommandResponseDTO manage(String jsonCmd) {
    CommandDTOList commandList;
    Log.debug("MAB0xA000: Manage commands: '" + jsonCmd + "'", getClass().getName());

    try {
      // deserialize the command
      commandList = JsonMarshal.getInstance().unmarshal(jsonCmd, CommandDTOList.class);
    }
    catch (JsonException e) {
      Log.debug("MAB0xA001: Error: " + e.getMessage(), getClass().getName());
      return new CommandResponseDTO(-1, MontiGemErrorFactory.deserializeError(e.getMessage() + ": " + jsonCmd));
    }

    if (commandList == null) {
      Log.error("MAB0xA002: Empty Command List");
      return new CommandResponseDTO(-1, MontiGemErrorFactory.resolverError("Empty command list: " + jsonCmd));
    }

    return runCommands(commandList);
  }
}
