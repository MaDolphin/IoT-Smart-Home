package de.montigem.be.system.beispiele.dtos;

import de.montigem.be.domain.cdmodelhwc.classes.zahlentyp.ZahlenTyp;
import de.montigem.be.domain.cdmodelhwc.classes.zahlenwert.ZahlenWert;
import de.montigem.be.domain.dtos.ZahlenWertDTO;

import java.util.ArrayList;
import java.util.Random;

public class FinanzierungZusammenstellungEntryDTO extends FinanzierungZusammenstellungEntryDTOTOP {

  public FinanzierungZusammenstellungEntryDTO(long id, int year) {
    ZahlenWert zahlenWert = new ZahlenWert();
    zahlenWert.rawInitAttrs(new ArrayList<>(), ZahlenTyp.PROZENT, new Random().nextInt(11)*100);
    ZahlenWertDTO zahlenWertDTO = new ZahlenWertDTO(zahlenWert);

    setPlanUmfang(zahlenWertDTO);
    setUmfang(zahlenWertDTO);
    setJahr(year);
    setMonat(new Random().nextInt(12) + 1);
    setId(id);
  }

}
