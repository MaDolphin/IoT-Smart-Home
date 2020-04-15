package de.montigem.be.domain.cdmodelhwc.classes.smokesensor;
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
public class SmokeSensor extends SmokeSensorTOP

{
	/**
	 * Sets smoke value and updates associated AlarmCtrl entity
	 *
	 * @param i
	 *            smoke value
	 */
	@Override
	public void setSmoke(int i) {
		super.setSmoke(i);
		super.getAlarmCtrl().updateAlarm();
	}
}
