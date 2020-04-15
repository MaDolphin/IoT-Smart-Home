package de.montigem.be.domain.commands;

import java.util.*;
import javax.xml.bind.ValidationException;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.util.DAOLib;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.IdDTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.se_rwth.commons.logging.Log;
import de.montigem.be.domain.cdmodelhwc.classes.sirenctrl.*;

/**
 * Overrides standard SirenCtrl_update implementation. Main difference is
 * granting SirenCtrl update permission to all users.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 * @see de.montigem.be.domain.commands.SirenCtrl_updateTOP
 */
public class SirenCtrl_update

		extends
			SirenCtrl_updateTOP {

	@Override
	public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError {
		Log.debug("MAB0x9030: SirenCtrl_update.doRun: dto: " + dto, "SirenCtrl_update");

		if (this.dto == null) {
			Log.info("0xB9030: given type is null", "SirenCtrl_update");
			return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("dto"));
		}

		if (!securityHelper.doesUserHavePermission(Permissions.UPDATE, ObjectClasses.USER)) {
			Log.warn("SirenCtrl_update MAB0x9011: User doesn't have permission for " + Permissions.UPDATE);
			return new ErrorDTO("MAB0x90011", MontiGemErrorFactory.forbidden());
		}

		Optional<SirenCtrl> o = daoLib.getSirenCtrlDAO().findAndLoad(dto.getId(), daoLib, securityHelper);

		if (!o.isPresent()) {
			Log.warn(getClass().getName() + " MAB0x9031: Cannot find SirenCtrl with objectId " + dto.getId());
			return new ErrorDTO("MAB0x9031", MontiGemErrorFactory.loadIDError("SirenCtrl", dto.getId()));
		}

		SirenCtrl object;
		try {
			object = dto.toBuilder(daoLib, securityHelper).build();
		} catch (ValidationException e) {
			Log.warn("SirenCtrl_update MAB0x9033: Object not valid, " + e);
			return new ErrorDTO("MAB0x9033", MontiGemErrorFactory.validationError(e.getMessage()));
		}

		daoLib.getSirenCtrlDAO().update(object, securityHelper.getSessionCompliantResource());
		Log.debug("MAB0x9034: SirenCtrl_update.doRun: update of object with id: " + object.getId() + " to " + object,
				"SirenCtrl_update");
		return new IdDTO(dto.getId());
	}

}
