/*Generated file from MCGenerator*/
package de.montigem.be.domain.cdmodelhwc.classes.adapter;

import de.montigem.be.domain.cdmodelhwc.classes.adapter.Adapter;
import de.montigem.be.domain.rte.interfaces.IObject;
import de.montigem.be.util.DAOLib;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import de.montigem.be.domain.cdmodelhwc.classes.smokesensorsmoke_adapter.SmokeSensorSmoke_Adapter;
import de.montigem.be.domain.cdmodelhwc.classes.smokesensor.SmokeSensor;

import de.montigem.be.domain.cdmodelhwc.classes.alarmctrl.AlarmCtrl;
import de.montigem.be.domain.cdmodelhwc.classes.sirenctrlsirenactive_adapter.SirenCtrlSirenActive_Adapter;
import de.montigem.be.domain.cdmodelhwc.classes.sirenctrl.SirenCtrl;

import de.montigem.be.domain.cdmodelhwc.classes.alarmctrl.AlarmCtrl;
import de.montigem.be.domain.cdmodelhwc.classes.tempsensortemp_adapter.TempSensorTemp_Adapter;
import de.montigem.be.domain.cdmodelhwc.classes.tempsensor.TempSensor;

import de.montigem.be.domain.cdmodelhwc.classes.alarmctrl.AlarmCtrl;

/**
 * Factory for Adapter.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
@Stateless
// @Lock(LockType.WRITE)
public class AdapterFactory {

	/**
	 * Creates Adapter and assigns associated entities. Creates associations between
	 * attribute class entity and identifying class entity. If an attribute entity
	 * is given it will be associated to the Adapter entity and to the identifying
	 * class entity, if necessary. Otherwise it is created, if necessary and
	 * associated. The identifying entity is found by group, if necessary.
	 *
	 * @port unique component identification in CP-system
	 * @identifier unique identification of new Adapter
	 * @group unique group of identifying entity
	 * @daoLib dao Library
	 * @resource database name
	 * @iObject optional attribute entity to use by new Adapter
	 * @return created Adapter
	 */
	@Transactional
	public static Adapter createAdapter(String port, String identifier, String group, DAOLib daoLib, String resource,
			Optional<IObject> iObject) {
		// decide which Adapter is needed
		switch (port) {

			case "alarm.Alarm.smokeSensor.smokeOut0s" :

				// is attribute entity given?
				if (iObject.isPresent() && iObject.get() instanceof SmokeSensor) {

					// find identifying entity
					AlarmCtrl alarmCtrl;
					List<AlarmCtrl> identifierClass = daoLib.getAdapterDAO().findByGroup(group, "serial", resource,
							AlarmCtrl.class);

					// if found use identifying entity, else create it
					if (identifierClass.size() == 1) {
						alarmCtrl = identifierClass.get(0);
					} else {
						alarmCtrl = new AlarmCtrl();
						alarmCtrl.setSerial(group);
					}

					SmokeSensor smokeSensor = (SmokeSensor) iObject.get();

					// create associations between identifying and attribute entity: idClass to
					// attrClass

					if (alarmCtrl.getSmokeSensors() == null) {
						alarmCtrl.setSmokeSensors(Collections.singletonList(smokeSensor));
					} else if (!alarmCtrl.containsSmokeSensor(smokeSensor)) {
						alarmCtrl.addSmokeSensor(smokeSensor);
					}

					// create association attrClass to idClass

					if (smokeSensor.getAlarmCtrl() == null || smokeSensor.getAlarmCtrl() != alarmCtrl) {
						smokeSensor.setAlarmCtrl(alarmCtrl);
					}

					// create Adapter
					SmokeSensorSmoke_Adapter smokeSensorSmoke_Adapter = new SmokeSensorSmoke_Adapter(identifier, group,
							smokeSensor);
					smokeSensorSmoke_Adapter.setAlarmCtrl(alarmCtrl);
					return daoLib.getAdapterDAO().create(smokeSensorSmoke_Adapter, resource);

				}

				// no attribute entity is given
				else {

					// find identifying entity
					AlarmCtrl alarmCtrl;
					List<AlarmCtrl> identifierClass = daoLib.getAdapterDAO().findByGroup(group, "serial", resource,
							AlarmCtrl.class);

					// if found use identifying entity, else create it
					if (identifierClass.size() == 1) {
						alarmCtrl = identifierClass.get(0);
					} else {
						alarmCtrl = new AlarmCtrl();
						alarmCtrl.setSerial(group);
					}

					SmokeSensor smokeSensor;

					// create attribute class
					smokeSensor = new SmokeSensor();

					// create association between identifying and attribute entity: idClass to
					// attrClass

					if (alarmCtrl.getSmokeSensors() == null) {
						alarmCtrl.setSmokeSensors(Collections.singletonList(smokeSensor));
					} else if (!alarmCtrl.containsSmokeSensor(smokeSensor)) {
						alarmCtrl.addSmokeSensor(smokeSensor);
					}

					// create association between identifying and attribute entity: attrClass to
					// idClass
					if (smokeSensor.getAlarmCtrl() == null || smokeSensor.getAlarmCtrl() != alarmCtrl) {
						smokeSensor.setAlarmCtrl(alarmCtrl);
					}

					// create Adapter
					SmokeSensorSmoke_Adapter smokeSensorSmoke_Adapter = new SmokeSensorSmoke_Adapter(identifier, group,
							smokeSensor);
					smokeSensorSmoke_Adapter.setAlarmCtrl(alarmCtrl);
					return daoLib.getAdapterDAO().create(smokeSensorSmoke_Adapter, resource);
				}
			case "alarm.Alarm.sirenCtrl.sirenIn0i" :

				// is attribute entity given?
				if (iObject.isPresent() && iObject.get() instanceof SirenCtrl) {

					// find identifying entity
					AlarmCtrl alarmCtrl;
					List<AlarmCtrl> identifierClass = daoLib.getAdapterDAO().findByGroup(group, "serial", resource,
							AlarmCtrl.class);

					// if found use identifying entity, else create it
					if (identifierClass.size() == 1) {
						alarmCtrl = identifierClass.get(0);
					} else {
						alarmCtrl = new AlarmCtrl();
						alarmCtrl.setSerial(group);
					}

					SirenCtrl sirenCtrl = (SirenCtrl) iObject.get();

					// create associations between identifying and attribute entity: idClass to
					// attrClass

					if (alarmCtrl.getSirenCtrls() == null) {
						alarmCtrl.setSirenCtrls(Collections.singletonList(sirenCtrl));
					} else if (!alarmCtrl.containsSirenCtrl(sirenCtrl)) {
						alarmCtrl.addSirenCtrl(sirenCtrl);
					}

					// create association attrClass to idClass

					// create Adapter
					SirenCtrlSirenActive_Adapter sirenCtrlSirenActive_Adapter = new SirenCtrlSirenActive_Adapter(
							identifier, group, sirenCtrl);
					sirenCtrlSirenActive_Adapter.setAlarmCtrl(alarmCtrl);
					return daoLib.getAdapterDAO().create(sirenCtrlSirenActive_Adapter, resource);

				}

				// no attribute entity is given
				else {

					// find identifying entity
					AlarmCtrl alarmCtrl;
					List<AlarmCtrl> identifierClass = daoLib.getAdapterDAO().findByGroup(group, "serial", resource,
							AlarmCtrl.class);

					// if found use identifying entity, else create it
					if (identifierClass.size() == 1) {
						alarmCtrl = identifierClass.get(0);
					} else {
						alarmCtrl = new AlarmCtrl();
						alarmCtrl.setSerial(group);
					}

					SirenCtrl sirenCtrl;

					// create attribute class
					sirenCtrl = new SirenCtrl();

					// create association between identifying and attribute entity: idClass to
					// attrClass

					if (alarmCtrl.getSirenCtrls() == null) {
						alarmCtrl.setSirenCtrls(Collections.singletonList(sirenCtrl));
					} else if (!alarmCtrl.containsSirenCtrl(sirenCtrl)) {
						alarmCtrl.addSirenCtrl(sirenCtrl);
					}

					// create association between identifying and attribute entity: attrClass to
					// idClass

					// create Adapter
					SirenCtrlSirenActive_Adapter sirenCtrlSirenActive_Adapter = new SirenCtrlSirenActive_Adapter(
							identifier, group, sirenCtrl);
					sirenCtrlSirenActive_Adapter.setAlarmCtrl(alarmCtrl);
					return daoLib.getAdapterDAO().create(sirenCtrlSirenActive_Adapter, resource);
				}
			case "temp.Temp.tempCtrl.outPort0s" :

				// is attribute entity given?
				if (iObject.isPresent() && iObject.get() instanceof TempSensor) {

					// find identifying entity
					AlarmCtrl alarmCtrl;
					List<AlarmCtrl> identifierClass = daoLib.getAdapterDAO().findByGroup(group, "serial", resource,
							AlarmCtrl.class);

					// if found use identifying entity, else create it
					if (identifierClass.size() == 1) {
						alarmCtrl = identifierClass.get(0);
					} else {
						alarmCtrl = new AlarmCtrl();
						alarmCtrl.setSerial(group);
						alarmCtrl = daoLib.getAlarmCtrlDAO().create(alarmCtrl, resource);
					}

					TempSensor tempSensor = (TempSensor) iObject.get();

					// create associations between identifying and attribute entity: idClass to
					// attrClass

					if (alarmCtrl.getTempSensors() == null) {
						alarmCtrl.setTempSensors(Collections.singletonList(tempSensor));
					} else if (!alarmCtrl.containsTempSensor(tempSensor)) {
						alarmCtrl.addTempSensor(tempSensor);
					}

					// create association attrClass to idClass
					if (tempSensor.getAlarmCtrls() == null) {
						tempSensor.setAlarmCtrls(Collections.singletonList(alarmCtrl));
					} else if (!tempSensor.containsAlarmCtrl(alarmCtrl)) {
						tempSensor.addAlarmCtrl(alarmCtrl);
					}

					// create Adapter
					TempSensorTemp_Adapter tempSensorTemp_Adapter = new TempSensorTemp_Adapter(identifier, group,
							tempSensor);
					tempSensorTemp_Adapter.setAlarmCtrl(alarmCtrl);
					return daoLib.getAdapterDAO().create(tempSensorTemp_Adapter, resource);

				}

				// no attribute entity is given
				else {

					// find identifying entity
					AlarmCtrl alarmCtrl;
					List<AlarmCtrl> identifierClass = daoLib.getAdapterDAO().findByGroup(group, "serial", resource,
							AlarmCtrl.class);

					// if found use identifying entity, else create it
					if (identifierClass.size() == 1) {
						alarmCtrl = identifierClass.get(0);
					} else {
						alarmCtrl = new AlarmCtrl();
						alarmCtrl.setSerial(group);
						alarmCtrl = daoLib.getAlarmCtrlDAO().create(alarmCtrl, resource);
					}

					TempSensor tempSensor;

					// create attribute class
					tempSensor = daoLib.getTempSensorDAO().create(new TempSensor(), resource);

					// create association between identifying and attribute entity: idClass to
					// attrClass

					if (alarmCtrl.getTempSensors() == null) {
						alarmCtrl.setTempSensors(Collections.singletonList(tempSensor));
					} else if (!alarmCtrl.containsTempSensor(tempSensor)) {
						alarmCtrl.addTempSensor(tempSensor);
					}

					// create association between identifying and attribute entity: attrClass to
					// idClass
					if (tempSensor.getAlarmCtrls() == null) {
						tempSensor.setAlarmCtrls(Collections.singletonList(alarmCtrl));
					} else if (!tempSensor.containsAlarmCtrl(alarmCtrl)) {
						tempSensor.addAlarmCtrl(alarmCtrl);
					}

					// create Adapter
					TempSensorTemp_Adapter tempSensorTemp_Adapter = new TempSensorTemp_Adapter(identifier, group,
							tempSensor);
					tempSensorTemp_Adapter.setAlarmCtrl(alarmCtrl);
					return daoLib.getAdapterDAO().create(tempSensorTemp_Adapter, resource);
				}
			default :
				return null;
		}
	}
}
