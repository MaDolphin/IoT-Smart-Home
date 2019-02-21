package de.montigem.be.command.rte.send;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.command.rte.general.CommandDTO;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.error.MontiGemError;
import de.se_rwth.commons.logging.Log;
import de.montigem.be.util.DAOLib;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * used to execute commands from the backend
 */
@Stateless
public class CommandCaller {

  @Inject
  private DAOLib daoLib;

  @Inject
  private SecurityHelper securityHelper;

  private long id;

  private List<CommandDTO> commands;

  private List<CommandPromise> promises;

  public CommandCaller() {
    this.id = 0;
    this.commands = new ArrayList<>();
    this.promises = new ArrayList<>();
  }

  /**
   * add any command to the list
   *
   * @param cmd
   * @param method
   */
  public CommandCaller add(CommandDTO cmd, CommandCallback<DTO> method) {
    long id = this.id;
    this.id++;

    cmd.setCommandId(id);
    this.commands.add(cmd);
    this.promises.add(new CommandPromise(id, method));

    return this;
  }

  public void clear() {
    this.commands.clear();
    this.promises.clear();
    this.id = 0;
  }

  private Optional<CommandCallback<DTO>> getById(long id) {
    return promises.stream().filter(promise -> promise.getId() == id).map(CommandPromise::getMethod)
        .findAny();
  }

  /**
   * execute all given commands locally
   *
   * @throws RuntimeException
   */
  public void execute() throws RuntimeException {
    this.commands.forEach(cmd -> {
      Optional<CommandCallback<DTO>> method = getById(cmd.getCommandId());
      if (!method.isPresent()) {
        Log.warn("MAB0xA100: could not find callback method for id " + cmd.getCommandId());
        throw new RuntimeException("MAB0xA100: could not find callback method for id " + cmd.getCommandId());
      }

      DTO d = cmd.doRun(securityHelper, daoLib);
      method.get().onOk(d);
    });

    clear();
  }

  public <D extends DTO> D runCommand(CommandDTO commandDTO) throws ClassCastException, MontiGemError {
    DTO dto = commandDTO.doRun(securityHelper, daoLib);
    return (D) dto;
  }
}

