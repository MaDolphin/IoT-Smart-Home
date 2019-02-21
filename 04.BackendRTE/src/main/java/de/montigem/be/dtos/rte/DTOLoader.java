package de.montigem.be.dtos.rte;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.error.MaCoCoErrorCode;
import de.montigem.be.util.DAOLib;
import org.apache.commons.lang.NotImplementedException;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Container for DTO
 * <p>
 * used as base class
 *
 * @param <T>
 */
public class DTOLoader<T extends DTO> {

  protected Optional<T> dto = Optional.empty();

  public DTOLoader() {
  }

  public DTOLoader(DAOLib daoLib, SecurityHelper securityHelper) throws NoSuchElementException {
    setDTO(loadDTO(daoLib, securityHelper));
  }

  public DTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) throws NoSuchElementException {
    setDTO(loadDTO(daoLib, id, securityHelper));
  }

  public DTOLoader(T dto) {
    this.dto = Optional.ofNullable(dto);
  }

  public Optional<T> getDTOOptional() {
    return dto;
  }

  public boolean iDTOPresent() {
    return dto.isPresent();
  }

  public T getDTO() {
    return dto.orElseThrow(() -> new RuntimeException("dto is absent"));
  }

  public void setDTO(T dto) {
    this.dto = Optional.ofNullable(dto);
  }

  public Optional<String> getData() {
    return Optional.empty();
  }

  public String getErrorCode() {
    return MaCoCoErrorCode.OK.getCode();
  }

  public String getMessage() {
    return MaCoCoErrorCode.OK.name();
  }

  public T loadDTO(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    throw new NotImplementedException("getById not implemented");
  }

  public T loadDTO(DAOLib daoLib, SecurityHelper securityHelper) {
    throw new NotImplementedException("getAll not implemented");
  }

}

