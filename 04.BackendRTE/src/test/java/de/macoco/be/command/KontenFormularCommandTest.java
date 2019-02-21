package de.montigem.be.command;

import de.montigem.be.AbstractDomainTest;
import main.java.be.authz.Roles;
import main.java.be.authz.util.SecurityHelper;
import main.java.be.command.response.CommandResponseDTO;
import main.java.be.command.response.CommandResultDTO;
import main.java.be.command.rte.general.CommandDTO;
import main.java.be.command.rte.general.CommandDTOList;
import main.java.be.command.rte.general.CommandManager;
import de.montigem.be.domain.cdmodelhwc.classes.budget.Budget;
import de.montigem.be.domain.cdmodelhwc.classes.drittmittelprojekt.Drittmittelprojekt;
import de.montigem.be.domain.cdmodelhwc.classes.konto.Konto;
import de.montigem.be.domain.cdmodelhwc.classes.konto.KontoTOP;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import main.java.be.dtos.rte.DTO;
import main.java.be.dtos.rte.IdDTO;
import main.java.be.marshalling.GsonMarshal;
import de.montigem.be.system.finanzen.commands.BudgetFlatList_getById;
import de.montigem.be.system.finanzen.commands.KontenFormular_getById;
import de.montigem.be.system.finanzen.dtos.BudgetFlatListDTO;
import de.montigem.be.system.finanzen.dtos.IndustrieprojektFormularDTO;
import de.montigem.be.system.finanzen.dtos.KontenFormularDTO;
import de.montigem.be.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.naming.NamingException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class KontenFormularCommandTest extends AbstractDomainTest {

  @Inject
  protected CommandManager commandManager;

  @Inject
  private DatabaseReset databaseReset;

  @Inject
  private DatabaseDummies databaseDummies;

  @Inject
  private SecurityHelper securityHelper;

  @Inject
  public DAOLib daoLib;

  private final String resource = "TestDB";

  @Before
  public void init() {
    try {
      databaseReset.removeDatabaseEntries(false);
      databaseDummies.createDatabaseDummies();
    }
    catch (Exception e) {
      fail("Couldn´t login bootstrapuser: " + e.getMessage());
    }
  }

  @Test
  public void createHaushaltTest() {
    KontenFormularDTO dto = TestUtil.createHaushaltskontoFormularDTO();
    CommandDTO cmd = new KontenFormular_create(dto);
    DTO result = cmd.doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);
    IdDTO idDTO = (IdDTO) result;

    Optional<Konto> kontoOpt = daoLib.getKontoDAO().findAndLoad(idDTO.getId(), daoLib, securityHelper.getSessionCompliantResource());
    assertTrue(kontoOpt.isPresent());
    Konto konto = kontoOpt.get();
    assertEquals("TestKonto", konto.getName());
    assertTrue(konto.getGesamtBudgetOptional().isPresent());
    assertEquals(30000, konto.getGesamtBudget().getBudgetRahmenCent().get().longValue());
  }

  @Test
  public void createRoleAssigned() {
    String username = securityHelper.getCurrentUser().getUsername();

    List<RoleAssignment> roleAssignmentsBefore = daoLib.getRoleAssignmentDAO().getRoleAssignmentsByUsername(username, securityHelper.getSessionCompliantResource());

    KontenFormularDTO dto = TestUtil.createHaushaltskontoFormularDTO();
    CommandDTO cmd = new KontenFormular_create(dto);
    DTO result = cmd.doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);
    IdDTO idDTO = (IdDTO) result;

    Optional<Konto> kontoOpt = daoLib.getKontoDAO().findAndLoad(idDTO.getId(), daoLib, securityHelper.getSessionCompliantResource());
    assertTrue(kontoOpt.isPresent());

    List<RoleAssignment> roleAssignmentsAfter = daoLib.getRoleAssignmentDAO().getRoleAssignmentsByUsername(username, securityHelper.getSessionCompliantResource());
    roleAssignmentsAfter.removeAll(roleAssignmentsBefore);

    assertEquals(1, roleAssignmentsAfter.size());

    assertEquals(Roles.ACCOUNT_MANAGER.getIdentifier(), roleAssignmentsAfter.get(0).getRole());
    assertEquals(kontoOpt.get().getId(), roleAssignmentsAfter.get(0).getObjId().longValue());
  }

  @Test
  public void createDummyHaushaltskonto() {
    String json = "{\"id\":2,\"commands\":[\n" +
        "{\n" +
        "    \"commandId\": 0,\n" +
        "    \"typeName\": \"KontenFormular_create\",\n" +
        "    \"dto\": {\n" +
        "        \"typeName\": \"HaushaltskontoFormularDTO\",\n" +
        "        \"id\": -1,\n" +
        "        \"gesamtbudget\": null,\n" +
        "        \"konto\": {\n" +
        "            \"typeName\": \"HaushaltskontoDTO\",\n" +
        "            \"id\": -1,\n" +
        "            \"labels\": [],\n" +
        "            \"name\": \"DF Test\",\n" +
        "            \"pspElement\": \"213123123123123\",\n" +
        "            \"kontotyp\": \"EU\",\n" +
        "            \"sapDatum\": null,\n" +
        "            \"internesAktenzeichen\": \"\",\n" +
        "            \"istPlanKonto\": false,\n" +
        "            \"istVerbuchungsKonto\": false,\n" +
        "            \"optStartDatum\": null,\n" +
        "            \"optEndDatum\": null,\n" +
        "            \"gueltigerGeschaeftsvorgang\": \"\",\n" +
        "            \"startDatum\": \"2018-10-02T00:00:00.000Z\",\n" +
        "            \"endDatum\": \"2018-10-22T00:00:00.000Z\"\n" +
        "        }\n" +
        "    }\n" +
        "}]}";

    CommandResponseDTO response = commandManager.manage(json);
    assertEquals(1, response.getResults().size());

    List<DTO> dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());
    assertTrue(dtos.get(0) instanceof IdDTO);
  }

  @Test
  public void getKontoFormularbyId() {
    Drittmittelprojekt d = daoLib.getDrittmittelprojektDAO().getAll(resource).get(0);

    String json = "{\"id\":2,\"commands\":[\n" +
        "{\n" +
        "    \"commandId\": 0,\n" +
        "    \"typeName\": \"KontenFormular_getById\",\n" +
        "    \"objectId\": " + d.getId() + "\n" +
        "}]}";

    CommandResponseDTO response = commandManager.manage(json);
    assertEquals(1, response.getResults().size());

    List<DTO> dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());
    assertTrue(dtos.get(0) instanceof KontenFormularDTO);
  }

  @Test
  public void getBudgetFlatListById() {
    Konto konto = daoLib.getKontoDAO().getAll(resource).get(0);
    Budget budget = konto.getGesamtBudget();

    CommandDTO cmd = new BudgetFlatList_getById(budget.getId());
    DTO result = cmd.doRun(securityHelper, daoLib);
    assertTrue(result instanceof BudgetFlatListDTO);
    BudgetFlatListDTO resultDTO = (BudgetFlatListDTO) result;
    assertEquals(12, resultDTO.getBudgets().size());
  }

  @Test
  public void getBudgetFlatListByKontoId() {
    Konto konto = daoLib.getKontoDAO().getAll(resource).get(0);
    Budget budget = konto.getGesamtBudget();

    CommandDTO cmd = new BudgetFlatList_byKontoId(konto.getId());
    DTO result = cmd.doRun(securityHelper, daoLib);
    assertTrue(result instanceof BudgetFlatListDTO);
    BudgetFlatListDTO resultDTO = (BudgetFlatListDTO) result;
    assertEquals(12, resultDTO.getBudgets().size());
  }

  private static String industrieprojektExternKonto(Long externesKonto) {
    return "{\n" +
        "    \"id\": 2,\n" +
        "    \"commands\": [\n" +
        "        {\n" +
        "            \"commandId\": 0,\n" +
        "            \"typeName\": \"KontenFormular_create\",\n" +
        "            \"dto\": {\n" +
        "                \"typeName\": \"IndustrieprojektFormularDTO\",\n" +
        "                \"id\": 0,\n" +
        "                \"gesamtbudget\": {\n" +
        "                    \"typeName\": \"FormularGesamtbudgetDTO\",\n" +
        "                    \"id\": -1,\n" +
        "                    \"budget\": {\n" +
        "                        \"typeName\": \"BudgetDTO\",\n" +
        "                        \"id\": 0,\n" +
        "                        \"labels\": [],\n" +
        "                        \"typ\": \"Gesamtbudget\",\n" +
        "                        \"startDatum\": null,\n" +
        "                        \"endDatum\": null,\n" +
        "                        \"kommentar\": \"\",\n" +
        "                        \"budgetRahmenCent\": 0,\n" +
        "                        \"budgetDepth\": 1\n" +
        "                    },\n" +
        "                    \"hauptbudgets\": []\n" +
        "                },\n" +
        "                \"konto\": {\n" +
        "                    \"typeName\": \"IndustrieprojektDTO\",\n" +
        "                    \"id\": 0,\n" +
        "                    \"labels\": [],\n" +
        "                    \"name\": \"Projekt1 " + (externesKonto != null ? "mit" : "ohne") + " Extern\",\n" +
        "                    \"pspElement\": \"123456789234567\",\n" +
        "                    \"kontotyp\": \"AIF\",\n" +
        "                    \"sapDatum\": \"\",\n" +
        "                    \"internesAktenzeichen\": \"\",\n" +
        "                    \"istPlanKonto\": false,\n" +
        "                    \"istVerbuchungsKonto\": false,\n" +
        "                    \"optStartDatum\": null,\n" +
        "                    \"optEndDatum\": null,\n" +
        "                    \"gueltigerGeschaeftsvorgang\": \"\",\n" +
        "                    \"bewilligungsdatum\": \"\",\n" +
        "                    \"programmpauschale\": 0,\n" +
        "                    \"gemeinkostensatz\": 0,\n" +
        "                    \"startDatum\": \"2018-10-10T00:00:00.000Z\",\n" +
        "                    \"endDatum\": \"2018-10-24T00:00:00.000Z\",\n" +
        "                    \"auftraggeber\": \"Du\",\n" +
        "                    \"projektArt\": \"Die Projektart 45 existiert nicht. Bitte überprüfen Sie das PSP-Element.\",\n" +
        "                    \"aktenzeichenTraeger\": null,\n" +
        "                    \"aktenzeichenRWTH\": null,\n" +
        "                    \"laufzeit\": null,\n" +
        "                    \"bewilligungsSumme\": 0,\n" +
        "                    \"komplettSumme\": 0,\n" +
        "                    \"anualStart\": \"\",\n" +
        "                    \"anualEnd\": \"\",\n" +
        "                    \"verbuchungskonto\": \"278\"\n" +
        "                },\n" +
        "                \"externesKontoId\": " + (externesKonto != null ? externesKonto : 0) + "\n" +
        "            }\n" +
        "        }\n" +
        "    ]\n" +
        "}";
  }

  @Test
  public void createWithoutExternKonto() {
    CommandResponseDTO response = commandManager.manage(industrieprojektExternKonto(null));
    assertEquals(1, response.getResults().size());

    List<DTO> dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());
    assertTrue(dtos.get(0) instanceof IdDTO);
  }

  @Test
  public void createWithExternKonto() {
    long externKontoId = daoLib.getDrittmittelprojektDAO().getAll(resource).stream().filter(KontoTOP::isTVerbuchungsKonto).collect(Collectors.toList()).get(0).getId();

    CommandResponseDTO response = commandManager.manage(industrieprojektExternKonto(externKontoId));
    assertEquals(1, response.getResults().size());

    List<DTO> dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());
    assertTrue(dtos.get(0) instanceof IdDTO);
    IdDTO dto = (IdDTO) dtos.get(0);
    assertTrue(dto.getId() > 0);
  }

  @Test
  public void updateWithoutExternKonto() {
    CommandResponseDTO response = commandManager.manage(industrieprojektExternKonto(null));
    List<DTO> dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());

    assertTrue(dtos.get(0) instanceof IdDTO);
    IdDTO dto = (IdDTO) dtos.get(0);

    response = commandManager.manage(
        "{\"id\":2,\"commands\":[\n" +
            "{\n" +
            "    \"commandId\": 0,\n" +
            "    \"typeName\": \"KontenFormular_getById\",\n" +
            "    \"objectId\": " + dto.getId() + "\n" +
            "}]}");
    dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());

    assertTrue(dtos.get(0) instanceof KontenFormularDTO);
    KontenFormularDTO fdto = (KontenFormularDTO) dtos.get(0);

    String json = "{\n" +
        "    \"id\": 2,\n" +
        "    \"commands\": [\n" +
        "        {\n" +
        "            \"commandId\": 0,\n" +
        "            \"typeName\": \"KontenFormular_update\",\n" +
        "            \"dto\": " +
        new GsonMarshal().marshal(fdto) +
        "            \n" +
        "        }\n" +
        "    ]\n" +
        "}";

    response = commandManager.manage(json);
    assertEquals(1, response.getResults().size());

    dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());
    assertTrue(dtos.get(0) instanceof IdDTO);
    assertEquals(dto.getId(), dtos.get(0).getId());
  }

  @Test
  public void updateWithoutExternKonto2() throws NamingException {
    securityManagerInit.init("admin5");

    CommandResponseDTO response = commandManager.manage(industrieprojektExternKonto(null));
    List<DTO> dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());

    assertTrue(dtos.get(0) instanceof IdDTO);
    IdDTO dto = (IdDTO) dtos.get(0);

    response = commandManager.manage(
        "{\"id\":2,\"commands\":[\n" +
            "{\n" +
            "    \"commandId\": 0,\n" +
            "    \"typeName\": \"KontenFormular_getById\",\n" +
            "    \"objectId\": " + dto.getId() + "\n" +
            "}]}");
    dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());

    assertTrue(dtos.get(0) instanceof KontenFormularDTO);
    KontenFormularDTO fdto = (KontenFormularDTO) dtos.get(0);

    daoLib.getRoleAssignmentDAO().removeRoleFromUser(securityHelper.getCurrentUser().getId(), Roles.ADMIN);

    String json = "{\n" +
        "    \"id\": 2,\n" +
        "    \"commands\": [\n" +
        "        {\n" +
        "            \"commandId\": 0,\n" +
        "            \"typeName\": \"KontenFormular_update\",\n" +
        "            \"dto\": " +
        new GsonMarshal().marshal(fdto) +
        "            \n" +
        "        }\n" +
        "    ]\n" +
        "}";

    response = commandManager.manage(json);
    assertEquals(1, response.getResults().size());

    dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());
    assertTrue(dtos.get(0) instanceof IdDTO);
    assertEquals(dto.getId(), dtos.get(0).getId());
  }

  @Test
  public void updateWithExternKonto() {
    long externKontoId = daoLib.getKontoDAO().getAll(resource).stream().filter(KontoTOP::isTVerbuchungsKonto).collect(Collectors.toList()).get(0).getId();

    CommandResponseDTO response = commandManager.manage(industrieprojektExternKonto(externKontoId));
    List<DTO> dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());

    assertTrue(dtos.get(0) instanceof IdDTO);
    IdDTO dto = (IdDTO) dtos.get(0);

    response = commandManager.runCommands(new CommandDTOList(Collections.singletonList(new KontenFormular_getById(dto.getId()))));
    dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());

    assertTrue(dtos.get(0) instanceof IndustrieprojektFormularDTO);
    IndustrieprojektFormularDTO kfdto = (IndustrieprojektFormularDTO) dtos.get(0);

    String json = "{\n" +
        "    \"id\": 2,\n" +
        "    \"commands\": [\n" +
        "        {\n" +
        "            \"commandId\": 0,\n" +
        "            \"typeName\": \"KontenFormular_update\",\n" +
        "            \"dto\": " +
        new GsonMarshal().marshal(kfdto) +
        "            \n" +
        "        }\n" +
        "    ]\n" +
        "}";

    response = commandManager.manage(json);
    assertEquals(1, response.getResults().size());

    dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());
    assertTrue(dtos.get(0) instanceof IdDTO);
    assertEquals(dto.getId(), dtos.get(0).getId());
  }

  @Test
  public void updateWithoutToWithExternKonto() {
    long externKontoId = daoLib.getDrittmittelprojektDAO().getAll(resource).stream().filter(KontoTOP::isTVerbuchungsKonto).collect(Collectors.toList()).get(0).getId();

    CommandResponseDTO response = commandManager.manage(industrieprojektExternKonto(null));
    List<DTO> dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());

    assertTrue(dtos.get(0) instanceof IdDTO);
    IdDTO dto = (IdDTO) dtos.get(0);

    response = commandManager.runCommands(new CommandDTOList(Collections.singletonList(new KontenFormular_getById(dto.getId()))));
    dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());

    assertTrue(dtos.get(0) instanceof IndustrieprojektFormularDTO);
    IndustrieprojektFormularDTO kfdto = (IndustrieprojektFormularDTO) dtos.get(0);
    kfdto.setExternesKontoId(externKontoId);

    String json = "{\n" +
        "    \"id\": 2,\n" +
        "    \"commands\": [\n" +
        "        {\n" +
        "            \"commandId\": 0,\n" +
        "            \"typeName\": \"KontenFormular_update\",\n" +
        "            \"dto\": " +
        new GsonMarshal().marshal(kfdto) +
        "            \n" +
        "        }\n" +
        "    ]\n" +
        "}";

    response = commandManager.manage(json);
    assertEquals(1, response.getResults().size());

    dtos = response.getResults().stream().map(CommandResultDTO::getDto).collect(Collectors.toList());
    assertTrue(dtos.get(0) instanceof IdDTO);
    assertEquals(dto.getId(), dtos.get(0).getId());
  }
}
