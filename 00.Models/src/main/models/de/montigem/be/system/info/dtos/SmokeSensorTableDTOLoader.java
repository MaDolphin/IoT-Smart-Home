
package de.montigem.be.system.info.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.smokesensor.SmokeSensor;
import de.montigem.be.domain.dtos.SmokeSensorDTO;
import de.montigem.be.util.DAOLib;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates DTO for SmokeSensorTable with all SmokeSensors from the database
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
public class SmokeSensorTableDTOLoader

		extends
			SmokeSensorTableDTOLoaderTOP

{

	public SmokeSensorTableDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
		SmokeSensorTableDTO dto = new SmokeSensorTableDTO();
		List<SmokeSensor> temps = daoLib.getSmokeSensorDAO().getAll(securityHelper.getSessionCompliantResource());
		dto.setSmokeSensor(
				temps.stream().map(SmokeSensorTableDTOLoader::createSmokeSensorDTO).collect(Collectors.toList()));
		setDTO(dto);
	}

	public static SmokeSensorDTO createSmokeSensorDTO(SmokeSensor temp) {
		SmokeSensorDTO dto = new SmokeSensorDTO();
		dto.setSmoke(temp.getSmoke());
		dto.setId(temp.getId());
		dto.setLabels(temp.getLabels());
		return dto;
	}

	public SmokeSensorTableDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper)

	{
		super(daoLib, id, securityHelper);
	}

}
