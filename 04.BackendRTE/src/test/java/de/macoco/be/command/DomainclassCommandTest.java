package de.montigem.be.command;

import main.java.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.budget.Budget;
import de.montigem.be.domain.cdmodelhwc.classes.budget.BudgetBuilder;
import de.montigem.be.domain.cdmodelhwc.classes.haushaltskonto.Haushaltskonto;
import de.montigem.be.domain.cdmodelhwc.classes.stellenzuweisung.Stellenzuweisung;
import de.montigem.be.domain.cdmodelhwc.classes.stellenzuweisungstatus.StellenzuweisungStatus;
import de.montigem.be.domain.cdmodelhwc.classes.zahlentyp.ZahlenTyp;
import de.montigem.be.domain.cdmodelhwc.classes.zahlenwert.ZahlenWert;
import de.montigem.be.domain.dtos.BudgetDTO;
import de.montigem.be.domain.dtos.HaushaltskontoDTO;
import de.montigem.be.domain.dtos.StellenzuweisungDTO;
import main.java.be.dtos.rte.DTO;
import main.java.be.dtos.rte.ErrorDTO;
import main.java.be.dtos.rte.IdDTO;
import de.montigem.be.util.TestUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import javax.xml.bind.ValidationException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class DomainclassCommandTest extends DummyValuesforAggregates {

  @Inject
  private DatabaseReset databaseReset;

  @Inject
  private DatabaseDummies databaseDummies;

  @Inject
  private SecurityHelper securityHelper;

  private final String resource = "TestDB";



  @Before
  public void init() {
    try {
      databaseReset.removeDatabaseEntries(false);
      databaseDummies.createDatabaseDummies();
    } catch (Exception e) {
      fail("Couldn´t login bootstrapuser: " + e.getMessage());
    }
    commandCaller.clear();
  }

  @Ignore
  @Test
  public void create() {
    // TODO SVa, can be done, when create-cmds use DTOs instead of Proxies
  }

  @Test
  public void delete() throws ValidationException {
    Budget budgets = daoLib.getBudgetDAO().getAll(resource).get(0);

    Budget budget= daoLib.getBudgetDAO().create(new BudgetBuilder().elternBudget(Optional.of(budgets)).budgetRahmenCent(Optional.of(1000L)).konto(budgets.getKonto()).typ("ohneBuchung").build(),resource);
    DTO result = new Budget_delete(budget.getId()).doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);
  }

  //Wenn ein Budget Buchungen enthält
  @Test
  public void deleteFail() {
    Budget budgets = daoLib.getBudgetDAO().getAll(resource).get(0);
    DTO result = new Budget_delete(budgets.getId()).doRun(securityHelper, daoLib);
    assertTrue(result instanceof ErrorDTO);
  }

  // TODO SVa/GV: add setId to toBuilder-methods of dto
  @Ignore
  @Test
  public void getById() throws ValidationException {
    Budget b = daoLib.getBudgetDAO().getAll(resource).get(0);
    Optional<Budget> budgetOpt = daoLib.getBudgetDAO()
        .findAndLoad(b.getId(), daoLib, resource);

    assertTrue(budgetOpt.isPresent());
    Budget budget = budgetOpt.get();

    DTO result = new Budget_getById(budget.getId()).doRun(securityHelper, daoLib);
    assertTrue(result instanceof BudgetDTO);

    BudgetDTO btdo = (BudgetDTO) result;

    Budget budget2 = btdo.toBuilder()
        .konto(budget.getKonto())
        .unterBudget(budget.getUnterBudgets())
        .buchungseintrag(budget.getBuchungseintrags())
        .stellenzuweisung(budget.getStellenzuweisungs())
        .build(false);
    assertEquals(budget, budget2);
  }

  /**
   * long
   *
   * @throws ValidationException
   */
  @Test
  public void setBudgetRahmen() throws ValidationException {
    Budget b = daoLib.getBudgetDAO().getAll(resource).get(0);
    Optional<Budget> budgetOpt = daoLib.getBudgetDAO()
        .findAndLoad(b.getId(), daoLib, resource);

    assertTrue(budgetOpt.isPresent());
    Budget budget = budgetOpt.get();

    DTO result = new Budget_setBudgetRahmenCent(budget.getId(), Optional.of(32L)).doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);

    DTO result2 = new Budget_getById(budget.getId()).doRun(securityHelper, daoLib);
    assertTrue(result2 instanceof BudgetDTO);
    BudgetDTO btdo = (BudgetDTO) result2;

    Budget budget2 = btdo.toBuilder()
        .konto(budget.getKonto())
        .unterBudget(budget.getUnterBudgets())
        .buchungseintrag(budget.getBuchungseintrags())
        .stellenzuweisung(budget.getStellenzuweisungs())
        .build(false);
    assertEquals(Optional.of(32L), budget2.getBudgetRahmenCent());
  }

  /**
   * Optional&lt;ZonedDateTime>
   *
   * @param endDatum
   * @throws ValidationException
   */
  private void setEndDatum(Optional<ZonedDateTime> endDatum)
      throws ValidationException {
    Budget b = daoLib.getBudgetDAO().getAll(resource).get(0);
    Optional<Budget> budgetOpt = daoLib.getBudgetDAO()
        .findAndLoad(b.getId(), daoLib, resource);

    assertTrue(budgetOpt.isPresent());
    Budget budget = budgetOpt.get();

    DTO result = new Budget_setEndDatum(budget.getId(), endDatum).doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);

    DTO result2 = new Budget_getById(budget.getId()).doRun(securityHelper, daoLib);
    assertTrue(result2 instanceof BudgetDTO);
    BudgetDTO btdo = (BudgetDTO) result2;

    Budget budget2 = btdo.toBuilder()
        .konto(budget.getKonto())
        .unterBudget(budget.getUnterBudgets())
        .buchungseintrag(budget.getBuchungseintrags())
        .stellenzuweisung(budget.getStellenzuweisungs())
        .build(false);

    assertTrue(TestUtil.compareDates(endDatum, budget2.getEndDatum()));
  }

  /**
   * Optional&lt;ZonedDateTime>
   *
   * @throws ValidationException
   */
  @Test
  public void setEndDatum() throws ValidationException {
    setEndDatum(Optional.of(ZonedDateTime.now()));
  }

  /**
   * empty Optional&lt;ZonedDateTime>
   *
   * @throws ValidationException
   */
  @Test
  public void setEndDatumEmpty() throws ValidationException {
    setEndDatum(Optional.empty());
  }

  /**
   * Optional&lt;String>
   *
   * @param kommentar
   * @throws ValidationException
   */
  private void setKommentar(Optional<String> kommentar) throws ValidationException {
    Budget b = daoLib.getBudgetDAO().getAll(resource).get(0);
    Optional<Budget> budgetOpt = daoLib.getBudgetDAO()
        .findAndLoad(b.getId(), daoLib, resource);

    assertTrue(budgetOpt.isPresent());
    Budget budget = budgetOpt.get();

    DTO result = new Budget_setKommentar(budget.getId(), kommentar).doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);

    DTO result2 = new Budget_getById(budget.getId()).doRun(securityHelper, daoLib);
    assertTrue(result2 instanceof BudgetDTO);
    BudgetDTO btdo = (BudgetDTO) result2;

    Budget budget2 = btdo.toBuilder()
        .konto(budget.getKonto())
        .unterBudget(budget.getUnterBudgets())
        .buchungseintrag(budget.getBuchungseintrags())
        .stellenzuweisung(budget.getStellenzuweisungs())
        .build(false);
    assertEquals(kommentar, budget2.getKommentar());
  }

  /**
   * Optional&lt;String>
   *
   * @throws ValidationException
   */
  @Test
  public void setKommentar() throws ValidationException {
    setKommentar(Optional.of("Kommentar"));
  }

  /**
   * empty Optional&lt;String>
   *
   * @throws ValidationException
   */
  @Test
  public void setKommentarEmpty() throws ValidationException {
    setKommentar(Optional.empty());
  }

  /**
   * List&lt;String>
   *
   * @param labels
   * @throws ValidationException
   */
  private void setLabels(List<String> labels) throws ValidationException {
    Budget b = daoLib.getBudgetDAO().getAll(resource).get(0);
    Optional<Budget> budgetOpt = daoLib.getBudgetDAO()
        .findAndLoad(b.getId(), daoLib, resource);

    assertTrue(budgetOpt.isPresent());
    Budget budget = budgetOpt.get();

    DTO result = new Budget_setLabels(budget.getId(), labels).doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);

    DTO result2 = new Budget_getById(budget.getId()).doRun(securityHelper, daoLib);
    assertTrue(result2 instanceof BudgetDTO);
    BudgetDTO btdo = (BudgetDTO) result2;

    Budget budget2 = btdo.toBuilder()
        .konto(budget.getKonto())
        .unterBudget(budget.getUnterBudgets())
        .buchungseintrag(budget.getBuchungseintrags())
        .stellenzuweisung(budget.getStellenzuweisungs())
        .build(false);
    assertEquals(labels.size(), budget2.getLabels().size());
    if (!labels.isEmpty()) {
      assertEquals(labels.get(0), budget2.getLabels().get(0));
    }
  }

  /**
   * List&lt;String>
   *
   * @throws ValidationException
   */
  @Test
  public void setLabels() throws ValidationException {
    setLabels(Arrays.asList("Label1", "Label2"));
  }

  /**
   * empty List&lt;String>
   *
   * @throws ValidationException
   */
  @Test
  public void setLabelsEmpty() throws ValidationException {
    setLabels(new ArrayList<>());
  }

  /**
   * boolean
   *
   * @param isPlanKonto
   * @throws ValidationException
   */
  private void istPlanKonto(boolean isPlanKonto) throws ValidationException {
    Haushaltskonto k = daoLib.getHaushaltskontoDAO().getAll(resource).get(0);
    Optional<Haushaltskonto> kontoOpt = daoLib.getHaushaltskontoDAO()
        .findAndLoad(k.getId(), daoLib, resource);

    assertTrue(kontoOpt.isPresent());
    Haushaltskonto konto = kontoOpt.get();

    DTO result = new Konto_setIstPlanKonto(konto.getId(), isPlanKonto).doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);

    DTO result2 = new Haushaltskonto_getById(konto.getId()).doRun(securityHelper, daoLib);
    assertTrue(result2 instanceof HaushaltskontoDTO);
    HaushaltskontoDTO ktdo = (HaushaltskontoDTO) result2;

    Haushaltskonto b = ktdo.toBuilder().gesamtBudget(Optional.of(konto.getGesamtBudget())).build(false);
    assertEquals(isPlanKonto, b.isTPlanKonto());
  }

  /**
   * boolean - true
   *
   * @throws ValidationException
   */
  @Test
  public void istPlanKontoTrue() throws ValidationException {
    istPlanKonto(true);
  }

  /**
   * boolean - false
   *
   * @throws ValidationException
   */
  @Test
  public void istPlanKontoFalse() throws ValidationException {
    istPlanKonto(true);
  }

  /**
   * String
   *
   * @throws ValidationException
   */
  @Test
  public void setName() throws ValidationException {
    Haushaltskonto k = daoLib.getHaushaltskontoDAO().getAll(resource).get(0);
    Optional<Haushaltskonto> kontoOpt = daoLib.getHaushaltskontoDAO()
        .findAndLoad(k.getId(), daoLib, resource);

    assertTrue(kontoOpt.isPresent());
    Haushaltskonto konto = kontoOpt.get();

    String name = "TestName";

    DTO result = new Konto_setName(konto.getId(), name).doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);

    DTO result2 = new Haushaltskonto_getById(konto.getId()).doRun(securityHelper, daoLib);
    assertTrue(result2 instanceof HaushaltskontoDTO);
    HaushaltskontoDTO ktdo = (HaushaltskontoDTO) result2;

    Haushaltskonto b = ktdo.toBuilder().gesamtBudget(Optional.of(konto.getGesamtBudget())).build(false);
    assertEquals(name, b.getName());
  }

  /**
   * Enum
   *
   * @throws ValidationException
   */
  @Test
  public void setStatus() throws ValidationException {
    Stellenzuweisung s = daoLib.getStellenzuweisungDAO().getAll(resource).get(0);
    Optional<Stellenzuweisung> stellenzuweisungOpt = daoLib.getStellenzuweisungDAO()
        .findAndLoad(s.getId(), daoLib, resource);

    assertTrue(stellenzuweisungOpt.isPresent());
    Stellenzuweisung stellenzuweisung = stellenzuweisungOpt.get();

    StellenzuweisungStatus status = StellenzuweisungStatus.BESETZT;

    DTO result = new Stellenzuweisung_setStatus(stellenzuweisung.getId(), status).doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);

    DTO result2 = new Stellenzuweisung_getById(stellenzuweisung.getId()).doRun(securityHelper, daoLib);
    assertTrue(result2 instanceof StellenzuweisungDTO);
    StellenzuweisungDTO stdo = (StellenzuweisungDTO) result2;

    Stellenzuweisung b = stdo.toBuilder().budget(stellenzuweisung.getBudget())
        .stellenumfang(stellenzuweisung.getStellenumfang()).build(false);
    assertEquals(status, b.getStatus());
  }

  /**
   * ZahlenWert (Association)
   *
   * @throws ValidationException
   */
  // TODO SVa: new assocs need to be created before set
  @Ignore
  @Test
  public void setStellenumfang() throws ValidationException {
    Stellenzuweisung s = daoLib.getStellenzuweisungDAO().getAll(resource).get(0);
    Optional<Stellenzuweisung> stellenzuweisungOpt = daoLib.getStellenzuweisungDAO()
        .findAndLoad(s.getId(), daoLib, resource);

    assertTrue(stellenzuweisungOpt.isPresent());
    Stellenzuweisung stellenzuweisung = stellenzuweisungOpt.get();

    ZahlenWert stellenumfang = new ZahlenWert(ZahlenTyp.STUNDE, 20);
    DTO result = new Stellenzuweisung_setStellenumfang(stellenzuweisung.getId(), stellenumfang).doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);

    DTO result2 = new Stellenzuweisung_getById(stellenzuweisung.getId()).doRun(securityHelper, daoLib);
    assertTrue(result2 instanceof StellenzuweisungDTO);
    StellenzuweisungDTO stdo = (StellenzuweisungDTO) result2;

    Stellenzuweisung b = stdo.toBuilder().budget(stellenzuweisung.getBudget()).stellenumfang(stellenzuweisung.getStellenumfang()).build(false);
    assertEquals(stellenumfang, b.getStellenumfang());
  }
}
