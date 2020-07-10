package de.montigem.be.system.sensor.dtos;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.sensor.Sensor;
import de.montigem.be.domain.cdmodelhwc.classes.sensortype.SensorType;
import de.montigem.be.domain.cdmodelhwc.classes.sensorvalue.SensorValue;
import de.montigem.be.system.einstellungen.dtos.*;
import de.montigem.be.util.DAOLib;

import java.util.ArrayList;
import java.util.List;

public class TemperatureDataDTOLoader extends TemperatureDataDTOLoaderTOP {

    public TemperatureDataDTOLoader() {
        super();
    }

    public TemperatureDataDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
        List<TemperatureDataEntryDTO> entries = new ArrayList<>();
        entries.add(new TemperatureDataEntryDTO(0, "Bad",18, 1590328881));
        entries.add(new TemperatureDataEntryDTO(101, "Bad",17, 1590385672));
        entries.add(new TemperatureDataEntryDTO(102, "Bad",18, 1590401234));
        entries.add(new TemperatureDataEntryDTO(103, "Bad",20, 1590465800));
        entries.add(new TemperatureDataEntryDTO(104, "Bad",24, 1590469004));
        entries.add(new TemperatureDataEntryDTO(105, "Bad",26, 1590474247));
        entries.add(new TemperatureDataEntryDTO(106, "Bad",33, 1590478535));
        entries.add(new TemperatureDataEntryDTO(107, "Bad",27, 1590481954));
        entries.add(new TemperatureDataEntryDTO(108, "Bad",25, 1590483023));
        entries.add(new TemperatureDataEntryDTO(109, "Bad",23, 1590485458));
        entries.add(new TemperatureDataEntryDTO(110, "Bad",18, 1590487562));
        entries.add(new TemperatureDataEntryDTO(111, "Bad",17, 1590504824));
        entries.add(new TemperatureDataEntryDTO(112, "Bad",18, 1590550200));
        entries.add(new TemperatureDataEntryDTO(2, "Bad",19, 1590568200));
        entries.add(new TemperatureDataEntryDTO(3, "Bad",18, 1590570360));
        entries.add(new TemperatureDataEntryDTO(4, "Bad",17, 1590575280));
        entries.add(new TemperatureDataEntryDTO(401, "Bad",17, 1590580252));
        entries.add(new TemperatureDataEntryDTO(402, "Bad",15, 1590587324));
        entries.add(new TemperatureDataEntryDTO(403, "Bad",13, 1590595023));
        entries.add(new TemperatureDataEntryDTO(404, "Bad",15, 1590613847));
        entries.add(new TemperatureDataEntryDTO(405, "Bad",19, 1590623822));
        entries.add(new TemperatureDataEntryDTO(5, "Störung",0, 1590328881));
        entries.add(new TemperatureDataEntryDTO(6, "Störung",0, 1590546360));
        entries.add(new TemperatureDataEntryDTO(7, "Störung",40, 1590548040));
        entries.add(new TemperatureDataEntryDTO(8, "Störung",40, 1590547500));
        entries.add(new TemperatureDataEntryDTO(9, "Störung",0, 1590548100));
        entries.add(new TemperatureDataEntryDTO(10, "Störung",0, 1590623822));
        entries.add(new TemperatureDataEntryDTO(110, "Garage",17, 1590328881));
        entries.add(new TemperatureDataEntryDTO(11, "Garage",18, 1590391594));
        entries.add(new TemperatureDataEntryDTO(12, "Garage",20, 1590347631));
        entries.add(new TemperatureDataEntryDTO(13, "Garage",21, 1590374577));
        entries.add(new TemperatureDataEntryDTO(14, "Garage",19, 1590514432));
        entries.add(new TemperatureDataEntryDTO(15, "Garage",22, 1590387865));
        entries.add(new TemperatureDataEntryDTO(16, "Garage",23, 1590430491));
        entries.add(new TemperatureDataEntryDTO(17, "Garage",22, 1590481763));
        entries.add(new TemperatureDataEntryDTO(18, "Garage",19, 1590517663));
        entries.add(new TemperatureDataEntryDTO(19, "Garage",18, 1590469073));
        entries.add(new TemperatureDataEntryDTO(20, "Garage",18, 1590424897));
        entries.add(new TemperatureDataEntryDTO(201, "Garage",16, 1590502345));
        entries.add(new TemperatureDataEntryDTO(202, "Garage",15, 1590559237));
        entries.add(new TemperatureDataEntryDTO(203, "Garage",13, 1590591287));
        entries.add(new TemperatureDataEntryDTO(204, "Garage",11, 1590612837));
        entries.add(new TemperatureDataEntryDTO(205, "Garage",10, 1590623822));
        entries.add(new TemperatureDataEntryDTO(21, "Küche",19, 1590328881));
        entries.add(new TemperatureDataEntryDTO(22, "Küche",19, 1590448110));
        entries.add(new TemperatureDataEntryDTO(23, "Küche",20, 1590338159));
        entries.add(new TemperatureDataEntryDTO(24, "Küche",19, 1590391037));
        entries.add(new TemperatureDataEntryDTO(25, "Küche",22, 1590457270));
        entries.add(new TemperatureDataEntryDTO(26, "Küche",23, 1590508689));
        entries.add(new TemperatureDataEntryDTO(27, "Küche",20, 1590406677));
        entries.add(new TemperatureDataEntryDTO(271, "Küche",24, 1590526677));
        entries.add(new TemperatureDataEntryDTO(272, "Küche",23, 1590586677));
        entries.add(new TemperatureDataEntryDTO(273, "Küche",20, 1590623822));
        entries.add(new TemperatureDataEntryDTO(28, "Haustür",19, 1590493807));
        entries.add(new TemperatureDataEntryDTO(281, "Haustür",18, 1590328881));
        entries.add(new TemperatureDataEntryDTO(282, "Haustür",17, 1590330578));
        entries.add(new TemperatureDataEntryDTO(283, "Haustür",19, 1590353852));
        entries.add(new TemperatureDataEntryDTO(29, "Haustür",19, 1590434755));
        entries.add(new TemperatureDataEntryDTO(30, "Haustür",19, 1590387718));
        entries.add(new TemperatureDataEntryDTO(31, "Haustür",20, 1590422037));
        entries.add(new TemperatureDataEntryDTO(32, "Haustür",20, 1590449989));
        entries.add(new TemperatureDataEntryDTO(33, "Haustür",20, 1590487063));
        entries.add(new TemperatureDataEntryDTO(34, "Haustür",23, 1590399267));
        entries.add(new TemperatureDataEntryDTO(35, "Haustür",20, 1590387587));
        entries.add(new TemperatureDataEntryDTO(361, "Haustür",19, 1590482037));
        entries.add(new TemperatureDataEntryDTO(362, "Haustür",14, 1590513864));
        entries.add(new TemperatureDataEntryDTO(363, "Haustür",10, 1590536954));
        entries.add(new TemperatureDataEntryDTO(364, "Haustür",4, 1590568947));
        entries.add(new TemperatureDataEntryDTO(365, "Haustür",0, 1590581239));
        entries.add(new TemperatureDataEntryDTO(366, "Haustür",-4, 1590623822));
        entries.add(new TemperatureDataEntryDTO(37, "Garten",25, 1590385807));
        entries.add(new TemperatureDataEntryDTO(38, "Garten",25, 1590500255));
        entries.add(new TemperatureDataEntryDTO(39, "Garten",22, 1590623822));
        entries.add(new TemperatureDataEntryDTO(40, "Garten",22, 1590566227));
        entries.add(new TemperatureDataEntryDTO(41, "Garten",23, 1590581596));
        entries.add(new TemperatureDataEntryDTO(42, "Garten",21, 1590344625));
        entries.add(new TemperatureDataEntryDTO(43, "Garten",20, 1590328881));
        entries.add(new TemperatureDataEntryDTO(44, "Garten",19, 1590476225));
        setDTO(new TemperatureDataDTO(0, entries));


    }

    public TemperatureDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }
}