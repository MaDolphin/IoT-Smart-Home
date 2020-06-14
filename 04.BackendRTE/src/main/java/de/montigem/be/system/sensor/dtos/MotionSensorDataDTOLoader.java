package de.montigem.be.system.sensor.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

import java.util.ArrayList;
import java.util.List;

public class MotionSensorDataDTOLoader extends MotionSensorDataDTOLoaderTOP {

    public MotionSensorDataDTOLoader() {
        super();
    }

    public MotionSensorDataDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
        List<MotionSensorEntryDTO> entries = new ArrayList<>();
        entries.add(new MotionSensorEntryDTO(0, "Backend Bad", 1590544020));
        entries.add(new MotionSensorEntryDTO(1, "Backend Bad", 1590550200));
        entries.add(new MotionSensorEntryDTO(2, "Backend Bad", 1590568200));
        entries.add(new MotionSensorEntryDTO(3, "Backend Bad", 1590570360));
        entries.add(new MotionSensorEntryDTO(4, "Backend Bad", 1590575280));
        entries.add(new MotionSensorEntryDTO(5, "Backend Störung", 1590544920));
        entries.add(new MotionSensorEntryDTO(6, "Backend Störung", 1590546360));
        entries.add(new MotionSensorEntryDTO(7, "Backend Störung", 1590548040));
        entries.add(new MotionSensorEntryDTO(8, "Backend Störung", 1590547500));
        entries.add(new MotionSensorEntryDTO(9, "Backend Störung", 1590545880));
        entries.add(new MotionSensorEntryDTO(10, "Backend Störung", 1590545760));
        entries.add(new MotionSensorEntryDTO(11, "Backend Garage", 1590391594));
        entries.add(new MotionSensorEntryDTO(12, "Backend Garage", 1590347631));
        entries.add(new MotionSensorEntryDTO(13, "Backend Garage", 1590374577));
        entries.add(new MotionSensorEntryDTO(14, "Backend Garage", 1590514432));
        entries.add(new MotionSensorEntryDTO(15, "Backend Garage", 1590387865));
        entries.add(new MotionSensorEntryDTO(16, "Backend Garage", 1590430491));
        entries.add(new MotionSensorEntryDTO(17, "Backend Garage", 1590481763));
        entries.add(new MotionSensorEntryDTO(18, "Backend Garage", 1590517663));
        entries.add(new MotionSensorEntryDTO(19, "Backend Garage", 1590469073));
        entries.add(new MotionSensorEntryDTO(20, "Backend Garage", 1590424897));
        entries.add(new MotionSensorEntryDTO(21, "Backend Küche", 1590502858));
        entries.add(new MotionSensorEntryDTO(22, "Backend Küche", 1590448110));
        entries.add(new MotionSensorEntryDTO(23, "Backend Küche", 1590338159));
        entries.add(new MotionSensorEntryDTO(24, "Backend Küche", 1590391037));
        entries.add(new MotionSensorEntryDTO(25, "Backend Küche", 1590457270));
        entries.add(new MotionSensorEntryDTO(26, "Backend Küche", 1590508689));
        entries.add(new MotionSensorEntryDTO(27, "Backend Küche", 1590406677));
        entries.add(new MotionSensorEntryDTO(28, "Backend Haustür", 1590538079));
        entries.add(new MotionSensorEntryDTO(29, "Backend Haustür", 1590434755));
        entries.add(new MotionSensorEntryDTO(30, "Backend Haustür", 1590387718));
        entries.add(new MotionSensorEntryDTO(31, "Backend Haustür", 1590612037));
        entries.add(new MotionSensorEntryDTO(32, "Backend Haustür", 1590529989));
        entries.add(new MotionSensorEntryDTO(33, "Backend Haustür", 1590487063));
        entries.add(new MotionSensorEntryDTO(34, "Backend Haustür", 1590399267));
        entries.add(new MotionSensorEntryDTO(35, "Backend Haustür", 1590387587));
        entries.add(new MotionSensorEntryDTO(36, "Backend Haustür", 1590562527));
        entries.add(new MotionSensorEntryDTO(37, "Backend Garten", 1590385807));
        entries.add(new MotionSensorEntryDTO(38, "Backend Garten", 1590500255));
        entries.add(new MotionSensorEntryDTO(39, "Backend Garten", 1590623822));
        entries.add(new MotionSensorEntryDTO(40, "Backend Garten", 1590566227));
        entries.add(new MotionSensorEntryDTO(41, "Backend Garten", 1590581596));
        entries.add(new MotionSensorEntryDTO(42, "Backend Garten", 1590344625));
        entries.add(new MotionSensorEntryDTO(43, "Backend Garten", 1590328881));
        entries.add(new MotionSensorEntryDTO(44, "Backend Garten", 1590476225));
        setDTO(new MotionSensorDataDTO(0, entries));
    }

    public MotionSensorDataDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
        super(daoLib, id, securityHelper);
    }
}