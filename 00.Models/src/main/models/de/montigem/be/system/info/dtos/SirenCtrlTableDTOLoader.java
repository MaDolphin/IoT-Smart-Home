
package de.montigem.be.system.info.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.sirenctrl.SirenCtrl;
import de.montigem.be.domain.dtos.SirenCtrlDTO;
import de.montigem.be.util.DAOLib;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates DTO for SirenCtrlTable with all SirenCtrls from the database
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
public class SirenCtrlTableDTOLoader

		extends
			SirenCtrlTableDTOLoaderTOP

{

	public SirenCtrlTableDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
		SirenCtrlTableDTO dto = new SirenCtrlTableDTO();
		List<SirenCtrl> temps = daoLib.getSirenCtrlDAO().getAll(securityHelper.getSessionCompliantResource());
		dto.setSirenCtrl(temps.stream().map(SirenCtrlTableDTOLoader::createSirenCtrlDTO).collect(Collectors.toList()));
		setDTO(dto);
	}

	public static SirenCtrlDTO createSirenCtrlDTO(SirenCtrl temp) {
		SirenCtrlDTO dto = new SirenCtrlDTO();
		dto.setSirenActive(temp.isSirenActive());
		dto.setEnabled(temp.isEnabled());
		dto.setSirenOverride(temp.isSirenOverride());
		dto.setId(temp.getId());
		dto.setLabels(temp.getLabels());
		return dto;
	}

	public SirenCtrlTableDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper)

	{
		super(daoLib, id, securityHelper);
	}

}
