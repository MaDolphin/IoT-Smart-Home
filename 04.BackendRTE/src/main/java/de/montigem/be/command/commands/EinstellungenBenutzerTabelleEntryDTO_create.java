/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.command.commands;


import de.montigem.be.auth.jwt.MontiGemSecurityUtils;
import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.database.DatabaseDataSourceUtil;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUserBuilder;
import de.montigem.be.domain.cdmodelhwc.classes.domainuseractivationstatus.DomainUserActivationStatus;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.dtos.rte.IdDTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorCode;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.system.einstellungen.dtos.EinstellungenBenutzerTabelleEntryDTO;
import de.montigem.be.util.DAOLib;

import javax.mail.MessagingException;
import javax.xml.bind.ValidationException;
import java.time.ZonedDateTime;
import java.util.Optional;


public class EinstellungenBenutzerTabelleEntryDTO_create extends EinstellungenBenutzerTabelleEntryDTO_createTOP {

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError {
    String resource = securityHelper.getSessionCompliantResource();
    if (!securityHelper.doesUserHavePermission(Permissions.USER_CREATE, ObjectClasses.USER)) {
      return new ErrorDTO(MontiGemErrorCode.FORBIDDEN.getCode(), MontiGemErrorFactory.forbidden(ObjectClasses.USER.getName()));
    }

    EinstellungenBenutzerTabelleEntryDTO dto = getDto();

    DomainUserBuilder builder = new DomainUserBuilder();
    if (daoLib.getDomainUserDAO().noUserExistWithUserName(dto.getUsername(), resource)) {
      builder.username(dto.getUsername());
    } else {
      return new ErrorDTO(MontiGemErrorCode.NON_UNIQUE_NAME.getCode(), MontiGemErrorFactory.nameIsNotUnique(dto.getUsername()));
    }

    if (daoLib.getDomainUserDAO().noUserExistWithEmail(dto.getEmail(), resource)) {
      builder.email(dto.getEmail());
    } else {
      return new ErrorDTO(MontiGemErrorCode.NON_UNIQUE_NAME.getCode(), MontiGemErrorFactory.nameIsNotUnique(dto.getEmail()));
    }

    if (daoLib.getDomainUserDAO().noUserExistWithInitials(dto.getInitials(), resource)) {
      builder.initials(Optional.of(dto.getInitials()));
    } else {
      return new ErrorDTO(MontiGemErrorCode.NON_UNIQUE_NAME.getCode(), MontiGemErrorFactory.nameIsNotUnique(dto.getInitials()));
    }

    builder.registrationDate(ZonedDateTime.now());
    builder.activated(DomainUserActivationStatus.MAIL_NICHT_GESENDET);
    builder.enabled(true);
    builder.passwordSaltBase64(MontiGemSecurityUtils.generateSaltAsBase64());

    DomainUser newUser;
    try {
      newUser = daoLib.getDomainUserDAO().create(builder.build(), resource);
    } catch (ValidationException e) {
      return new ErrorDTO(MontiGemErrorCode.NOT_VALID.getCode(), MontiGemErrorFactory.validationError(e.getMessage()));
    }

    if (newUser == null) {
      return new ErrorDTO(MontiGemErrorCode.STORE_TO_DB.getCode(), MontiGemErrorFactory.storeObjectError(newUser));
    }

    try {
      securityHelper.getUserActivationManager().sendActivationEmail(newUser.getEmail(), newUser.getUsername(), resource, DatabaseDataSourceUtil.getDatenbankBezeichner(resource));

    } catch (MessagingException e) {

    }
    daoLib.getDomainUserDAO().updateWithoutAssociations(newUser, resource);
    return new IdDTO(newUser.getId());

  }

}
