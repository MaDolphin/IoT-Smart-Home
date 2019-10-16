/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.command.rte.general;

public abstract class GetAllCommandDTO extends CommandDTO {
  protected int limit = -1;

  public GetAllCommandDTO limit(int limit) {
    this.limit = limit;
    return this;
  }
}
