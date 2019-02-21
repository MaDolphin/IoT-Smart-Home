package de.montigem.be.command;

import main.java.be.command.rte.send.CommandCallback;
import main.java.be.command.rte.send.CommandCaller;
import de.montigem.be.domain.commands.Budget_setBudgetRahmenCent;
import de.montigem.be.domain.commands.Budget_setTyp;
import main.java.be.dtos.rte.DTO;
import main.java.be.dtos.rte.IdDTO;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class __CommandCallerTest extends DummyValuesforAggregates {
  @Inject
  protected CommandCaller commandCaller;

  @Inject
  private DatabaseReset databaseReset;

  @Inject
  private DatabaseDummies databaseDummies;

  private static long budgetId;

  private final String resource = "TestDB";



  @Before
  public void init() {
    try {
      databaseReset.removeDatabaseEntries(false);
      databaseDummies.createDatabaseDummies();
    } catch (Exception e) {
      fail("Couldn´t login bootstrapuser: " + e.getMessage());
    }

    budgetId = daoLib.getBudgetDAO().getAll(resource).stream().filter(b -> b.getBudgetDepth() == 3).findAny().orElseThrow(() -> new RuntimeException("No Totalbudget available")).getId();
  }

  @Test
  public void test1cmd() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);

    commandCaller.add(new Budget_setTyp(budgetId, "JsonTest"), new CommandCallback<DTO>() {
      @Override
      public void onOk(DTO dto) {
        assertTrue(dto instanceof IdDTO);
        assertEquals(budgetId, dto.getId());
        latch.countDown();
      }

      @Override
      public void onError(Exception e) {
        fail("should call method onOk, but was onError: " + e);
      }
    }).execute();

    if (!latch.await(1, TimeUnit.SECONDS)) {
      fail("should call method onOk");
    }
  }

  @Test
  public void test2cmd() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(2);

    commandCaller.add(new Budget_setTyp(budgetId, "JsonTest"), new CommandCallback<DTO>() {
      @Override
      public void onOk(DTO dto) {
        assertTrue(dto instanceof IdDTO);
        assertEquals(budgetId, dto.getId());
        latch.countDown();
      }

      @Override
      public void onError(Exception e) {
        fail("should call method onOk, but was onError: " + e);
      }
    }).add(new Budget_setBudgetRahmenCent(budgetId, Optional.of(123L)), new CommandCallback<DTO>() {
      @Override
      public void onOk(DTO dto) {
        assertTrue(dto instanceof IdDTO);
        assertEquals(budgetId, dto.getId());
        latch.countDown();
      }

      @Override
      public void onError(Exception e) {
        fail("should call method onOk, but was onError: " + e);
      }
    }).execute();

    if (!latch.await(1, TimeUnit.SECONDS)) {
      fail("should call method onOk");
    }
  }
}
