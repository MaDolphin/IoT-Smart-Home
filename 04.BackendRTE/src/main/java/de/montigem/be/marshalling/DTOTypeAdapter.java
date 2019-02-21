
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved. http://www.se-rwth.de/
 */
package de.montigem.be.marshalling;

import de.montigem.be.dtos.rte.*;
import de.montigem.be.system.common.dtos.LongValueDTO;
import de.montigem.be.system.common.dtos.StringContainerDTO;

public class DTOTypeAdapter extends TypeAdapterResolver<DTO> {

  public static DTOTypeAdapter INSTANCE;

  protected DTOTypeAdapter() {
    super();
    init();
  }


  private void init() {
    this.registerSubtype(OkDTO.class).registerSubtype(IdDTO.class).registerSubtype(ErrorDTO.class).registerSubtype(NotImplementedDTO.class).registerSubtype(StringContainerDTO.class).registerSubtype(LongValueDTO.class);
  }

  public RuntimeTypeAdapterFactory<DTO> getFactory() {
    return getFactory(DTO.class);
  }

  public static DTOTypeAdapter getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DTOTypeAdapter();
    }
    return INSTANCE;
  }

}
