package de.montigem.be.system.info.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.tempsensor.TempSensor;
import de.montigem.be.domain.dtos.TempSensorDTO;
import de.montigem.be.util.DAOLib;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates DTO for TempSensorTable with all TempSensors from the database
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
public class TempSensorTableDTOLoader

		extends
			TempSensorTableDTOLoaderTOP

{

	public TempSensorTableDTOLoader(DAOLib daoLib, SecurityHelper securityHelper)

	{
		TempSensorTableDTO dto = new TempSensorTableDTO();
		List<TempSensor> temps = daoLib.getTempSensorDAO().getAll(securityHelper.getSessionCompliantResource());
		dto.setTempSensor(
				temps.stream().map(TempSensorTableDTOLoader::createTempSensorDTO).collect(Collectors.toList()));
		setDTO(dto);
	}

	public static TempSensorDTO createTempSensorDTO(TempSensor temp) {
		TempSensorDTO dto = new TempSensorDTO();
		dto.setTemp(temp.getTemp());
		dto.setUnit(temp.getUnit());
		dto.setId(temp.getId());
		dto.setLabels(temp.getLabels());
		return dto;
	}

	public TempSensorTableDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper)

	{
		super(daoLib, id, securityHelper);
	}

}
