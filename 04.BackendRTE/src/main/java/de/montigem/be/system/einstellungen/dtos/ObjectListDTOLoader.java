/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.system.einstellungen.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

public class ObjectListDTOLoader extends ObjectListDTOLoaderTOP {

  public ObjectListDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    ObjectListDTO dto = new ObjectListDTO();
    dto.getObjects().add(new ObjectListEntryDTO());

    setDTO(dto);
  }

  public ObjectListDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    super(daoLib, id, securityHelper);
  }

}
