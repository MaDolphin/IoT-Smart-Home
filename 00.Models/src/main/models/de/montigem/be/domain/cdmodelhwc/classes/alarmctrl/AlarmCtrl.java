package de.montigem.be.domain.cdmodelhwc.classes.alarmctrl;
import de.montigem.be.domain.cdmodelhwc.classes.sirenctrl.*;
import de.montigem.be.domain.cdmodelhwc.classes.smokesensor.*;
import de.montigem.be.domain.cdmodelhwc.classes.tempsensor.*;
import de.montigem.be.domain.cdmodelhwc.rest.AdapterService;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.naming.NamingException;

/**
 * The AlarmCtrl holds the information of the maximum and minimum temperature,
 * aswell as the maximum and minimum smoke values of all associated sensors. It
 * decides if all associated sirens should be activated.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
@Audited
@Entity
public class AlarmCtrl extends AlarmCtrlTOP

{
	@Override
	public void setSmokeCtrlMax(int i) {
		updateAlarm();
	}
	@Override
	public void setSmokeCtrlMin(int i) {
		updateAlarm();
	}
	@Override
	public void setTempCtrlMax(Float f) {
		updateAlarm();
	}
	@Override
	public void setTempCtrlMin(Float f) {
		updateAlarm();
	}
	@Override
	public Float getTempCtrlMax() {
		updateAlarm();
		return super.getTempCtrlMax();
	}
	@Override
	public Float getTempCtrlMin() {
		updateAlarm();
		return super.getTempCtrlMin();
	}
	@Override
	public int getSmokeCtrlMax() {
		updateAlarm();
		return super.getSmokeCtrlMax();
	}
	@Override
	public int getSmokeCtrlMin() {
		updateAlarm();
		return super.getSmokeCtrlMin();
	}

	/**
	 * Updates all AlarmCtrlTOP attributes. If the maximum temperature is considered
	 * critical(>70) or the maximum smoke value is considered critical(>200), the
	 * alarm is set to true and all associated sirens activate. As long as the alarm
	 * is active, Adapters are prohibited from disabling the alarm.
	 *
	 */
	public void updateAlarm() {
		if (getSmokeSensors() != null && getTempSensors() != null) {

			super.setSmokeCtrlMax(getSmokeSensors().stream().mapToInt(SmokeSensor::getSmoke).max().orElse(0));
			super.setTempCtrlMax(
					getTempSensors().stream().map(TempSensor::getTemp).max(Float::compare).orElse((float) 0));
			super.setSmokeCtrlMin(getSmokeSensors().stream().mapToInt(SmokeSensor::getSmoke).min().orElse(0));
			super.setTempCtrlMin(
					getTempSensors().stream().map(TempSensor::getTemp).min(Float::compare).orElse((float) 0));

			if (super.getTempCtrlMax() > 70 || super.getSmokeCtrlMax() > 200) {
				super.setAlarm(true);
				for (SirenCtrl siren : super.getSirenCtrls()) {
					try {
						AdapterService.lockWrite(siren.getId(), "SirenCtrl.sirenActive");
					} catch (NamingException e) {
						e.printStackTrace();
					}
					siren.setSirenActive(true);
				}
			} else {
				super.setAlarm(false);
				for (SirenCtrl siren : super.getSirenCtrls()) {
					siren.setSirenActive(false);
					try {
						AdapterService.unlockWrite(siren.getId(), "SirenCtrl.sirenActive");
					} catch (NamingException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			super.setAlarm(false);
		}
	}
}
