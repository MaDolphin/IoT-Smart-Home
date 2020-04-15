
package de.montigem.be.system.info.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.alarmctrl.AlarmCtrl;
import de.montigem.be.domain.dtos.AlarmCtrlDTO;
import de.montigem.be.util.DAOLib;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates DTO for AlarmCtrlTable with all AlarmCtrls from the database
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
public class AlarmCtrlTableDTOLoader extends AlarmCtrlTableDTOLoaderTOP {

	public AlarmCtrlTableDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
		AlarmCtrlTableDTO dto = new AlarmCtrlTableDTO();
		List<AlarmCtrl> temps = daoLib.getAlarmCtrlDAO().getAll(securityHelper.getSessionCompliantResource());
		dto.setAlarmCtrl(temps.stream().map(AlarmCtrlTableDTOLoader::createAlarmCtrlDTO).collect(Collectors.toList()));
		setDTO(dto);
	}

	public static AlarmCtrlDTO createAlarmCtrlDTO(AlarmCtrl temp) {
		AlarmCtrlDTO dto = new AlarmCtrlDTO();
		dto.setAlarm(temp.isAlarm());
		dto.setSerial(temp.getSerial());
		dto.setSmokeCtrlMax(temp.getSmokeCtrlMax());
		dto.setSmokeCtrlMin(temp.getSmokeCtrlMin());
		dto.setTempCtrlMax(temp.getTempCtrlMax());
		dto.setTempCtrlMin(temp.getTempCtrlMin());
		dto.setId(temp.getId());
		dto.setLabels(temp.getLabels());
		return dto;
	}

	public AlarmCtrlTableDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper)

	{
		super(daoLib, id, securityHelper);
	}

}
