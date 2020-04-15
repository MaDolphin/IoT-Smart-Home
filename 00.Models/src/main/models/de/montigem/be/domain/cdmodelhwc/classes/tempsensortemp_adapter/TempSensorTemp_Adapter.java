/*Generated file from MCGenerator*/
package de.montigem.be.domain.cdmodelhwc.classes.tempsensortemp_adapter;

import de.montigem.be.domain.rte.interfaces.IObject;
import org.hibernate.envers.Audited;

import de.montigem.be.error.JsonException;
import de.montigem.be.marshalling.JsonMarshal;
import javax.persistence.*;
import static org.apache.webbeans.util.Asserts.assertNotNull;

import de.montigem.be.domain.cdmodelhwc.classes.alarmctrl.AlarmCtrl;
import de.montigem.be.domain.cdmodelhwc.classes.tempsensor.TempSensor;

/**
 * Entity for writing and reading the attribute TempSensor.temp of the
 * associated entity.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
@Audited
@Entity
public class TempSensorTemp_Adapter extends TempSensorTemp_AdapterTOP {

	/** class uses auto identification */
	public static final boolean AUTOID = false;

	/** class name and attribute name that is accessed of the associated entity */
	public static final String attribute = "TempSensor.temp";

	/** Empty constructor. Do not use, but exists for compatibility reasons. */
	public TempSensorTemp_Adapter() {
	}

	/**
	 * Constructor for assigning unique identifier, group and associated attribute
	 * entity.
	 * 
	 * @identifier Unique name of the Adapter
	 * @group group to find the identifying class
	 * @tempSensor entity that is used for attribute access
	 */
	public TempSensorTemp_Adapter(String identifier, String group, TempSensor tempSensor) {
		this(identifier, group);
		assertNotNull(tempSensor);
		setTempSensor(tempSensor);
	}

	/**
	 * Constructor for assigning unique identifier and group.
	 * 
	 * @identifier Unique name of the Adapter
	 * @group group to find the identifying class
	 */
	public TempSensorTemp_Adapter(String identifier, String group) {
		assertNotNull(identifier);
		assertNotNull(group);
		this.identifier = identifier;
		this.groupName = group;
		super.attributeName = TempSensorTemp_Adapter.attribute;
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
	 * Gets temp from the associated tempSensor.
	 *
	 * @return d(param.attr)» value
	 */
	public Float getData() {
		return getTempSensor().getTemp();
	}

	/**
	 * Sets temp in associated tempSensor.
	 *
	 * @data value to set for d(param.attr)»
	 */
	public void setData(Float data) {
		getTempSensor().setTemp(data);
	}

	@Override
	public IObject getAttributeClass() {
		return getTempSensor();
	}

	@Override
	public void removeAssociations() {
		setTempSensor(null);
		setAlarmCtrl(null);
	}

	/**
	 * Object for easy JSON Object creation and value retrieval.
	 */
	static class Data {
		public Float data;

		public Data(Float data) {
			this.data = data;
		}
	}
}
