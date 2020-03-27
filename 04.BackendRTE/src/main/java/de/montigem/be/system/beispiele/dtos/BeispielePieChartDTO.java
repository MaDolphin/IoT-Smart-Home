/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.system.beispiele.dtos;

import com.google.common.collect.Lists;
import de.montigem.be.domain.cdmodelhwc.classes.zahlentyp.ZahlenTyp;
import de.montigem.be.system.common.dtos.PieChartEntryDTO;

public  class BeispielePieChartDTO extends BeispielePieChartDTOTOP {
  public BeispielePieChartDTO() {
    PieChartEntryDTO entry1 = new PieChartEntryDTO(0, 5,"entry1");
    PieChartEntryDTO entry2 = new PieChartEntryDTO(1, 10,"entry2");
    PieChartEntryDTO entry3 = new PieChartEntryDTO(2, 15,"entry3");
    this.total = 100;
    this.entries = Lists.newArrayList(entry1, entry2, entry3);
    this.typ = ZahlenTyp.EURO;
    this.title = "title";
  }
}
