/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.system.beispiele.dtos;

import de.montigem.be.domain.cdmodelhwc.classes.zahlentyp.ZahlenTyp;
import de.montigem.be.domain.cdmodelhwc.classes.zahlenwert.ZahlenWert;
import de.montigem.be.domain.dtos.ZahlenWertDTO;

import java.util.ArrayList;
import java.util.Random;

public class BeispieleBarChartEntryDTO extends BeispieleBarChartEntryDTOTOP {

  public BeispieleBarChartEntryDTO(long id, int year) {
    ZahlenWert zahlenWert = new ZahlenWert();
    zahlenWert.rawInitAttrs(new ArrayList<>(), ZahlenTyp.PROZENT, new Random().nextInt(11) * 1000L);
    ZahlenWertDTO zahlenWertDTO = new ZahlenWertDTO(zahlenWert);

    setPlanUmfang(zahlenWertDTO);
    setUmfang(zahlenWertDTO);
    setJahr(year);
    setMonat(new Random().nextInt(12) + 1);
    setId(id);
  }

}
