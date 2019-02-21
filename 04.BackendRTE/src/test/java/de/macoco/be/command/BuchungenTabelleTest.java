package de.montigem.be.command;

import main.java.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.buchung.Buchung;
import de.montigem.be.domain.cdmodelhwc.classes.buchung.BuchungBuilder;
import de.montigem.be.domain.cdmodelhwc.classes.buchungsstatus.BuchungsStatus;
import de.montigem.be.domain.cdmodelhwc.classes.budget.Budget;
import de.montigem.be.domain.commands.Buchung_create;
import de.montigem.be.domain.dtos.BuchungFullDTO;
import main.java.be.dtos.rte.DTO;
import main.java.be.dtos.rte.IdDTO;
import de.montigem.be.system.finanzen.commands.BuchungenTabelle_getById;
import de.montigem.be.system.finanzen.dtos.BuchungenTabelleDTO;
import de.montigem.be.system.finanzen.dtos.BuchungenTabelleEntryDTO;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.ValidationException;
import java.util.Collections;
import java.util.Optional;

import static main.java.be.util.DomainHelper.date;
import static java.time.ZonedDateTime.now;
import static org.junit.Assert.*;

public class BuchungenTabelleTest extends DummyValuesforAggregates {
  @Inject
  private SecurityHelper securityHelper;

  @Inject
  private DatabaseReset databaseReset;

  @Inject
  private DatabaseDummies databaseDummies;

  private static long kontoId;

  private final String resource = "TestDB";

  @Before
  public void init() {
    try {
      databaseReset.removeDatabaseEntries(false);
      databaseDummies.createDatabaseDummies();
    } catch (Exception e) {
      fail("Couldn´t login bootstrapuser: " + e.getMessage());
    }

    long buchungId = daoLib.getBuchungDAO().getAll(resource).get(0).getId();
    Buchung b = daoLib.getBuchungDAO().findAndLoad(buchungId, daoLib, resource).orElseThrow(EntityNotFoundException::new);

    kontoId = b.getBudget().getKonto().getId();
  }

  private Buchung buildBuchung(Budget budget) throws ValidationException {
    return new BuchungBuilder()
        .belegdatum(date(2016, 2, 1))
        .belegnummern(Collections.singletonList("1710000001"))
        .betragCent(10000)
        .kreditorDebitor(Optional.of("Debitor"))
        .zahlungsgrund("Personal")
        .buchungseintragText(Optional.of("handwritten"))
        .datum(now())
        .status(BuchungsStatus.SAP)
        .istAktiv(true).budget(budget).build();
  }

  @Test
  public void getTable() {
    DTO dto = new BuchungenTabelle_getById(kontoId).doRun(securityHelper, daoLib);
    assertTrue(dto instanceof BuchungenTabelleDTO);
  }

  @Test
  public void getTableAndCreateNewEntry() throws ValidationException {
    DTO dto = new BuchungenTabelle_getById(kontoId).doRun(securityHelper, daoLib);
    assertTrue(dto instanceof BuchungenTabelleDTO);
    BuchungenTabelleDTO tab = (BuchungenTabelleDTO) dto;

    long buchungenCount = tab.getBuchungen().size();

    assertTrue(buchungenCount > 0);
    BuchungenTabelleEntryDTO entry = tab.getBuchungen().get(0);

    long budgetId = entry.getBudget().getBudgetId();
    Optional<Budget> budget = daoLib.getBudgetDAO().findAndLoad(budgetId, daoLib, securityHelper.getSessionCompliantResource());

    assertTrue(budget.isPresent());

    Buchung b = buildBuchung(budget.get());
    BuchungFullDTO fullDTO = new BuchungFullDTO(b);

    DTO result = new Buchung_create(fullDTO).doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);

    dto = new BuchungenTabelle_getById(kontoId).doRun(securityHelper, daoLib);
    assertTrue(dto instanceof BuchungenTabelleDTO);
    tab = (BuchungenTabelleDTO) dto;

    assertEquals(buchungenCount + 1, tab.getBuchungen().size());
  }
}
