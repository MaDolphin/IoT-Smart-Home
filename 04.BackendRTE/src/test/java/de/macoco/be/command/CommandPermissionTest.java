package de.montigem.be.command;

import de.montigem.be.domain.cdmodelhwc.classes.macocouser.MacocoUser;
import de.montigem.be.domain.cdmodelhwc.classes.macocouser.MacocoUserBuilder;
import main.java.be.authz.ObjectClasses;
import main.java.be.authz.Roles;
import main.java.be.authz.util.SecurityHelper;
import main.java.be.command.rte.general.CommandDTO;
import de.montigem.be.domain.cdmodelhwc.classes.macocouseractivationstatus.MacocoUserActivationStatus;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignmentBuilder;
import de.montigem.be.domain.cdmodelhwc.classes.buchung.Buchung;
import de.montigem.be.domain.commands.Buchung_getById;
import de.montigem.be.domain.commands.Buchung_setBetragCent;
import main.java.be.dtos.rte.DTO;
import main.java.be.dtos.rte.ErrorDTO;
import main.java.be.dtos.rte.IdDTO;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.ValidationException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class CommandPermissionTest extends DummyValuesforAggregates {

  @Inject
  private DatabaseReset databaseReset;

  @Inject
  private DatabaseDummies databaseDummies;

  @Inject
  private SecurityHelper securityHelper;

  private static long kontoId;

  private static long buchungId;

  private static List<MacocoUser> users = new ArrayList<>();

  private final String resource = "TestDB";



  @Before
  public void init() throws Exception {
    try {
      databaseReset.removeDatabaseEntries(true);
      databaseDummies.createDatabaseDummies();
    } catch (Exception e) {
      fail("Couldn´t login bootstrapuser: " + e.getMessage());
    }

    buchungId = daoLib.getBuchungDAO().getAll(resource).get(0).getId();
    Buchung b = daoLib.getBuchungDAO().findAndLoad(buchungId, daoLib, resource).orElseThrow(EntityNotFoundException::new);

    kontoId = b.getBudget().getKonto().getId();

    MacocoUser user = createUser("0");
    assertNotNull(user);
    assertTrue(user.getId() > 0);
    createPermissions1(user);
    users.add(user);

    MacocoUser user2 = createUser("1");
    createPermissions2(user2);
    users.add(user2);

    MacocoUser user3 = createUser("2");
    users.add(user3);
  }

  private MacocoUser createUser(String suffix) throws Exception {
    MacocoUserBuilder macocoUserBuilder = new MacocoUserBuilder()
        .username("User" + suffix)
        .email("test@hoffentlichgibtsdieseadressenicht.äää" + suffix)
        .initials(Optional.of("TP"))
        .activated(MacocoUserActivationStatus.AKTIVIERT)
        .enabled(true)
        .encodedPassword(Optional.of("123456789"))
        .passwordSaltBase64("123456789")
        .registrationDate(ZonedDateTime.now());
    return daoLib.getMacocoUserDAO().create(macocoUserBuilder.build(), resource);
  }

  private void createPermissions1(MacocoUser user) throws ValidationException {
    RoleAssignmentBuilder builder = new RoleAssignmentBuilder();
    RoleAssignment ra = builder.user(user).objClass(ObjectClasses.ACCOUNT).role(Roles.LESER).objId(kontoId).build();
    ra = daoLib.getRoleAssignmentDAO().create(ra, securityHelper.getSessionCompliantResource());
    assertTrue(ra.getId() > 0);
  }

  private void createPermissions2(MacocoUser user) throws ValidationException {
    RoleAssignmentBuilder builder = new RoleAssignmentBuilder();
    RoleAssignment ra = builder.user(user).objClass(ObjectClasses.ACCOUNT).role(Roles.ACCOUNT_MANAGER).objId(kontoId).build();
    ra = daoLib.getRoleAssignmentDAO().create(ra, securityHelper.getSessionCompliantResource());
    assertTrue(ra.getId() > 0);
  }

  @Test
  public void user1_Buchung_read() throws NamingException {
    securityManagerInit.init(users.get(0).getUsername());

    CommandDTO cmd = new Buchung_getById(buchungId);

    DTO result = cmd.doRun(securityHelper, daoLib);
    assertTrue(result instanceof BuchungDTO);

    assertEquals(buchungId, result.getId());
  }

  @Test
  public void user1_Buchung_update() throws NamingException {
    securityManagerInit.init(users.get(0).getUsername());

    long betragCent = 123456;
    CommandDTO cmd = new Buchung_setBetragCent(buchungId, betragCent);

    DTO result = cmd.doRun(securityHelper, daoLib);
    assertTrue(result instanceof ErrorDTO);
  }

  @Test
  public void user2_Buchung_read() throws NamingException {
    securityManagerInit.init(users.get(1).getUsername());

    CommandDTO cmd = new Buchung_getById(buchungId);

    DTO result = cmd.doRun(securityHelper, daoLib);
    assertTrue(result instanceof BuchungDTO);

    assertEquals(buchungId, result.getId());
  }

  @Test
  public void user2_Buchung_update() throws NamingException {
    securityManagerInit.init(users.get(1).getUsername());

    long betragCent = 123456;
    CommandDTO cmd = new Buchung_setBetragCent(buchungId, betragCent);

    DTO result = cmd.doRun(securityHelper, daoLib);
    assertTrue(result instanceof IdDTO);
    assertEquals(buchungId, result.getId());

    Optional<Buchung> b = daoLib.getBuchungDAO().find(result.getId(), resource);
    assertTrue(b.isPresent());

    assertEquals(betragCent, b.get().getBetragCent());
  }

  @Test
  public void user3_Buchung_read() throws NamingException {
    securityManagerInit.init(users.get(2).getUsername());

    CommandDTO cmd = new Buchung_getById(buchungId);

    DTO result = cmd.doRun(securityHelper, daoLib);
    assertTrue(result instanceof ErrorDTO);
  }

  @Test
  public void user3_Buchung_update() throws NamingException {
    securityManagerInit.init(users.get(2).getUsername());

    long betragCent = 123456;
    CommandDTO cmd = new Buchung_setBetragCent(buchungId, betragCent);

    DTO result = cmd.doRun(securityHelper, daoLib);
    assertTrue(result instanceof ErrorDTO);
  }
}
