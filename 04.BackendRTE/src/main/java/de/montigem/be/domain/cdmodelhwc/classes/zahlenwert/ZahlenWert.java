package de.montigem.be.domain.cdmodelhwc.classes.zahlenwert;

import de.montigem.be.domain.cdmodelhwc.classes.zahlentyp.ZahlenTyp;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import java.util.ArrayList;

@Audited
@Entity
public class ZahlenWert extends ZahlenWertTOP {

  public ZahlenWert() {
    super();
    rawInitAttrs(new ArrayList<>(), ZahlenTyp.NONE, -1);
  }

  public ZahlenWert(ZahlenTyp zahlenTyp, long wert) {
    super();
    rawInitAttrs(new ArrayList<>(), zahlenTyp, wert);
  }

}
