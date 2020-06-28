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
        entries.add(new TemperatureDataEntryDTO(0, "Bad",17, 1590544020));
        entries.add(new TemperatureDataEntryDTO(1, "Bad",18, 1590550200));
        entries.add(new TemperatureDataEntryDTO(2, "Bad",19, 1590568200));
        entries.add(new TemperatureDataEntryDTO(3, "Bad",18, 1590570360));
        entries.add(new TemperatureDataEntryDTO(4, "Bad",17, 1590575280));
        entries.add(new TemperatureDataEntryDTO(5, "Störung",0, 1590544920));
        entries.add(new TemperatureDataEntryDTO(6, "Störung",0, 1590546360));
        entries.add(new TemperatureDataEntryDTO(7, "Störung",127, 1590548040));
        entries.add(new TemperatureDataEntryDTO(8, "Störung",127, 1590547500));
        entries.add(new TemperatureDataEntryDTO(9, "Störung",0, 1590545880));
        entries.add(new TemperatureDataEntryDTO(10, "Störung",0, 1590545760));
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
        entries.add(new TemperatureDataEntryDTO(21, "Küche",19, 1590502858));
        entries.add(new TemperatureDataEntryDTO(22, "Küche",19, 1590448110));
        entries.add(new TemperatureDataEntryDTO(23, "Küche",20, 1590338159));
        entries.add(new TemperatureDataEntryDTO(24, "Küche",19, 1590391037));
        entries.add(new TemperatureDataEntryDTO(25, "Küche",19, 1590457270));
        entries.add(new TemperatureDataEntryDTO(26, "Küche",20, 1590508689));
        entries.add(new TemperatureDataEntryDTO(27, "Küche",19, 1590406677));
        entries.add(new TemperatureDataEntryDTO(28, "Haustür",19, 1590538079));
        entries.add(new TemperatureDataEntryDTO(29, "Haustür",19, 1590434755));
        entries.add(new TemperatureDataEntryDTO(30, "Haustür",19, 1590387718));
        entries.add(new TemperatureDataEntryDTO(31, "Haustür",20, 1590612037));
        entries.add(new TemperatureDataEntryDTO(32, "Haustür",20, 1590529989));
        entries.add(new TemperatureDataEntryDTO(33, "Haustür",20, 1590487063));
        entries.add(new TemperatureDataEntryDTO(34, "Haustür",23, 1590399267));
        entries.add(new TemperatureDataEntryDTO(35, "Haustür",20, 1590387587));
        entries.add(new TemperatureDataEntryDTO(36, "Haustür",19, 1590562527));
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