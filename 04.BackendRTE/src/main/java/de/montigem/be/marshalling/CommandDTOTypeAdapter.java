
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved. http://www.se-rwth.de/
 */
package de.montigem.be.marshalling;

import de.montigem.be.command.rte.general.CommandDTO;
import de.montigem.be.system.common.commands.LongValue_getAll;
import de.montigem.be.system.common.commands.LongValue_getById;
import de.montigem.be.system.common.commands.StringContainer_getAll;
import de.montigem.be.system.common.commands.StringContainer_getById;

public class CommandDTOTypeAdapter
    
    extends TypeAdapterResolver<CommandDTO>

{
  
  public static CommandDTOTypeAdapter INSTANCE;
  
  protected CommandDTOTypeAdapter()
  
  {
    super();
    init();
  }
  
  private void init()
  
  {
    this.registerSubtype(StringContainer_getAll.class).registerSubtype(StringContainer_getById.class).registerSubtype(LongValue_getAll.class).registerSubtype(LongValue_getById.class);
  }
  
  public RuntimeTypeAdapterFactory<CommandDTO> getFactory() {
    return getFactory(CommandDTO.class);
  }
  
  public static CommandDTOTypeAdapter getInstance()
  
  {
    if (INSTANCE == null) {
      INSTANCE = new CommandDTOTypeAdapter();
    }
    return INSTANCE;
  }
  
}
