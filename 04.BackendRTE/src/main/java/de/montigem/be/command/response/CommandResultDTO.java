/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.command.response;

import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.dtos.rte.OkDTO;
import de.montigem.be.error.MontiGemError;

/**
 * single result of a executed command
 */
public class CommandResultDTO {
  private long id;
  private DTO dto;

  public CommandResultDTO(long id, MontiGemError error) {
    this.id = id;
    this.dto = new ErrorDTO(error);
  }

  public CommandResultDTO(long id, DTO dto) {
    this.id = id;
    this.dto = dto;
  }

  public CommandResultDTO(long id) {
    this.id = id;
    this.dto = new OkDTO();
  }

  public DTO getDto() {
    return dto;
  }

  public void setDto(DTO dto) {
    this.dto = dto;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
