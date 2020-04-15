package de.montigem.be.domain.cdmodelhwc.classes.tempsensor;
import de.montigem.be.domain.cdmodelhwc.classes.alarmctrl.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * TempSensor entity
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
@Audited
@Entity
public class TempSensor extends TempSensorTOP

{
	/**
	 * Sets temperature and updates associated AlarmCtrl entity
	 *
	 * @param f
	 *            temperature in °C
	 */
	@Override
	public void setTemp(Float f) {
		super.setTemp(f);
		for (AlarmCtrl a : super.getAlarmCtrls()) {
			a.updateAlarm();
		}
	}
}
