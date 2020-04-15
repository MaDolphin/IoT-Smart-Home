package de.montigem.be.domain.cdmodelhwc.classes.sirenctrl;
import de.montigem.be.domain.cdmodelhwc.classes.alarmctrl.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * SirenCtrl entity
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
@Audited
@Entity
public class SirenCtrl extends SirenCtrlTOP

{
	/**
	 * Activates or deactivates the siren. The siren can only be activated if it is
	 * enabled. The siren is always active if it is enabled and the control
	 * overriden
	 *
	 * @param bool
	 *            if the siren should be activated
	 */
	@Override
	public void setSirenActive(boolean bool) {
		if (super.isEnabled() && (bool || super.isSirenOverride())) {
			super.setSirenActive(true);
		} else {
			super.setSirenActive(false);
		}
	}
}
