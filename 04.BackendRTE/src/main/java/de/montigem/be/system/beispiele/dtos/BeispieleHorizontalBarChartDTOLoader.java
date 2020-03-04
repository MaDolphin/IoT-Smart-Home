package de.montigem.be.system.beispiele.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.zahlentyp.ZahlenTyp;
import de.montigem.be.domain.cdmodelhwc.classes.zahlenwert.ZahlenWert;
import de.montigem.be.domain.dtos.ZahlenWertDTO;
import de.montigem.be.util.DAOLib;

import java.util.*;

public class BeispieleHorizontalBarChartDTOLoader extends BeispieleHorizontalBarChartDTOLoaderTOP {

  public BeispieleHorizontalBarChartDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    super(daoLib, id, securityHelper);
  }

  public BeispieleHorizontalBarChartDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    setDTO(createDTO(daoLib, securityHelper));
  }

  protected BeispieleHorizontalBarChartDTO createDTO(DAOLib daoLib, SecurityHelper securityHelper) {
    BeispieleHorizontalBarChartDTO dto = new BeispieleHorizontalBarChartDTO();

    // labels
    dto.getLabels().addAll(Arrays.asList("Label 1", "Label 2", "Label 3"));

    // data
    dto.getDaten().add(new BeispieleBarDataDTO());
    dto.getDaten().add(new BeispieleBarDataDTO());
    dto.getDaten().add(new BeispieleBarDataDTO());

    dto.getDaten().get(0).kategorie = "Kategorie 1";
    dto.getDaten().get(1).kategorie = "Kategorie 2";
    dto.getDaten().get(2).kategorie = "Kategorie 3";

    List<ZahlenWertDTO> dataForKategorie1 = Arrays.asList(
        new ZahlenWertDTO(new ZahlenWert(ZahlenTyp.EURO, 100)),
        new ZahlenWertDTO(new ZahlenWert(ZahlenTyp.EURO, 200)),
        new ZahlenWertDTO(new ZahlenWert(ZahlenTyp.EURO, 300))
    );

    List<ZahlenWertDTO> dataForKategorie2 = Arrays.asList(
        new ZahlenWertDTO(new ZahlenWert(ZahlenTyp.EURO, 400)),
        new ZahlenWertDTO(new ZahlenWert(ZahlenTyp.EURO, 500)),
        new ZahlenWertDTO(new ZahlenWert(ZahlenTyp.EURO, 600))
    );

    List<ZahlenWertDTO> dataForKategorie3 = Arrays.asList(
        new ZahlenWertDTO(new ZahlenWert(ZahlenTyp.EURO, 700)),
        new ZahlenWertDTO(new ZahlenWert(ZahlenTyp.EURO, 800)),
        new ZahlenWertDTO(new ZahlenWert(ZahlenTyp.EURO, 900))
    );

    dto.getDaten().get(0).werte.addAll(dataForKategorie1);
    dto.getDaten().get(1).werte.addAll(dataForKategorie2);
    dto.getDaten().get(2).werte.addAll(dataForKategorie3);

    return dto;
  }

}
