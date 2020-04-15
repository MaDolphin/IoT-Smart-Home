/*Generated file from MCGenerator*/
package de.montigem.be.domain.cdmodelhwc.classes.smokesensorsmoke_adapter;

import de.montigem.be.domain.rte.interfaces.IObject;
import org.hibernate.envers.Audited;

import de.montigem.be.error.JsonException;
import de.montigem.be.marshalling.JsonMarshal;
import javax.persistence.*;
import static org.apache.webbeans.util.Asserts.assertNotNull;

import de.montigem.be.domain.cdmodelhwc.classes.alarmctrl.AlarmCtrl;
import de.montigem.be.domain.cdmodelhwc.classes.smokesensor.SmokeSensor;

/**
 * Entity for writing and reading the attribute SmokeSensor.smoke of the
 * associated entity.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
@Audited
@Entity
public class SmokeSensorSmoke_Adapter extends SmokeSensorSmoke_AdapterTOP {

	/** class uses auto identification */
	public static final boolean AUTOID = false;

	/** class name and attribute name that is accessed of the associated entity */
	public static final String attribute = "SmokeSensor.smoke";

	/** Empty constructor. Do not use, but exists for compatibility reasons. */
	public SmokeSensorSmoke_Adapter() {
	}

	/**
	 * Constructor for assigning unique identifier, group and associated attribute
	 * entity.
	 * 
	 * @identifier Unique name of the Adapter
	 * @group group to find the identifying class
	 * @smokeSensor entity that is used for attribute access
	 */
	public SmokeSensorSmoke_Adapter(String identifier, String group, SmokeSensor smokeSensor) {
		this(identifier, group);
		assertNotNull(smokeSensor);
		setSmokeSensor(smokeSensor);
	}

	/**
	 * Constructor for assigning unique identifier and group.
	 * 
	 * @identifier Unique name of the Adapter
	 * @group group to find the identifying class
	 */
	public SmokeSensorSmoke_Adapter(String identifier, String group) {
		assertNotNull(identifier);
		assertNotNull(group);
		this.identifier = identifier;
		this.groupName = group;
		super.attributeName = SmokeSensorSmoke_Adapter.attribute;
	}

	@Override
	public String getGroupName() {
		return getAlarmCtrl().getSerial();
	}

	@Override
	public boolean setJson(String json) {
		if (!getAllowWrite()) {
			return false;
		}
		try {
			setData(JsonMarshal.getInstance().unmarshal(json, Data.class).data);
			return true;
		} catch (JsonException e) {
			return false;
		}
	}

	@Override
	public String getJson() {
		if (!getAllowRead()) {
			return null;
		}
		return JsonMarshal.getInstance().marshal(new Data(getData()));
	}

	/**
	 * Gets smoke from the associated smokeSensor.
	 *
	 * @return d(param.attr)» value
	 */
	public int getData() {
		return getSmokeSensor().getSmoke();
	}

	/**
	 * Sets smoke in associated smokeSensor.
	 *
	 * @data value to set for d(param.attr)»
	 */
	public void setData(int data) {
		getSmokeSensor().setSmoke(data);
	}

	@Override
	public IObject getAttributeClass() {
		return getSmokeSensor();
	}

	@Override
	public void removeAssociations() {
		setSmokeSensor(null);
		setAlarmCtrl(null);
	}

	/**
	 * Object for easy JSON Object creation and value retrieval.
	 */
	static class Data {
		public int data;

		public Data(int data) {
			this.data = data;
		}
	}
}
