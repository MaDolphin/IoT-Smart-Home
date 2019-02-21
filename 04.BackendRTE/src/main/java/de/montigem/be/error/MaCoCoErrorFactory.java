/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.error;

import javax.ws.rs.core.Response.Status;
import java.util.List;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class MaCoCoErrorFactory {

  public static MaCoCoError notImplemented(String name) {
    return new MaCoCoError(MaCoCoErrorCode.NOT_IMPLEMENTED, Status.FORBIDDEN,
            name + " not implemented");
  }

  public static MaCoCoError unauthorized() {
    return new MaCoCoError(MaCoCoErrorCode.UNAUTHORIZED, Status.UNAUTHORIZED,
            "You are not authorized");
  }

  public static MaCoCoError forbidden() {
    return new MaCoCoError(MaCoCoErrorCode.FORBIDDEN, Status.FORBIDDEN,
            "You do not have permission");
  }

  public static MaCoCoError forbiddenSelf() {
    return new MaCoCoError(MaCoCoErrorCode.FORBIDDEN, Status.FORBIDDEN,
            "You can not change your own admin assignment");
  }

  public static MaCoCoError forbiddenKonto(long id) {
    return new MaCoCoError(MaCoCoErrorCode.FORBIDDEN, Status.FORBIDDEN, "You do not have permission for Konto with id " + id);
  }

  public static MaCoCoError forbidden(String className, long id) {
    return new MaCoCoError(MaCoCoErrorCode.FORBIDDEN, Status.FORBIDDEN, "Sie haben nicht die Rechte für diese Aktion");
  }

  public static MaCoCoError forbidden(String className) {
    return new MaCoCoError(MaCoCoErrorCode.FORBIDDEN, Status.FORBIDDEN, "You do not have permission for " + className);
  }

  public static MaCoCoError forbiddenPersonal() {
    return forbidden("Personal");
  }

  public static MaCoCoError forbiddenKonto() {
    return forbidden("Konto");
  }

  public static MaCoCoError deserializeError(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.DESERIALIZE, Status.BAD_REQUEST,
            "Failed to deserialize object: " + o);
  }

  public static MaCoCoError unknownTypeError(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.UNKNOWN_TYPE, Status.BAD_REQUEST,
            "Failed to find type: " + o);
  }

  public static MaCoCoError loadIDError(String className, long id) {
    return new MaCoCoError(MaCoCoErrorCode.LOAD_FROM_DB, Status.BAD_REQUEST,
            "Failed to find " + className + " with id " + id);
  }

  public static MaCoCoError loadIDError(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.LOAD_FROM_DB, Status.BAD_REQUEST,
            "Failed to find object: " + o);
  }

  public static MaCoCoError storeObjectError(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.STORE_TO_DB, Status.NOT_ACCEPTABLE,
            "Failed to store object: " + o);
  }

  public static MaCoCoError builderError(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.BUILDER, Status.NOT_ACCEPTABLE,
            "Failed to build object: " + o);
  }

  public static MaCoCoError resolverError(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.RESOLVER, Status.NOT_ACCEPTABLE,
            "Failed to resolve associations for object: " + o);
  }

  public static MaCoCoError validationError(String validationMessage) {
    return new MaCoCoError(MaCoCoErrorCode.NOT_VALID, Status.NOT_ACCEPTABLE,
            validationMessage);
  }

  public static MaCoCoError exceptionCaught(Exception e) {
    return new MaCoCoError(MaCoCoErrorCode.RESOLVER, Status.NOT_ACCEPTABLE,
            "The following exception occured: " + e.getMessage());
  }

  public static MaCoCoError forbiddenInMode(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.FORBIDDEN_OPERATION, Status.FORBIDDEN,
            "The operation is forbidden. It is only available in developer mode: " + o);
  }

  public static MaCoCoError nameIsNotUnique(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.NON_UNIQUE_NAME, Status.NOT_ACCEPTABLE,
            "The name is not unique and is already in use: " + o);
  }

  public static MaCoCoError accountIsExternal(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.IS_EXTERNAL_ACCOUNT, new CanNotDeleteAccountStatusType(),
            "The account is used as an externalAccount and cannot be deleted: " + o);
  }

  public static MaCoCoError accountIsKostenstelle(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.IS_KOSTENSTELLE, new CanNotDeleteAccountStatusType(),
            "The account is used as a Kostenstelle and cannot be deleted: " + o);
  }

  public static MaCoCoError missingField(Object o) {
    return new MaCoCoError(MaCoCoErrorCode.MISSING_FIELD, Status.EXPECTATION_FAILED,
            "A field was missing: " + o);
  }

  public static MaCoCoError errorByExcelImport(String o, String description) {
    return new MaCoCoError(MaCoCoErrorCode.EXCEL_IMPORT_ERROR, Status.NOT_ACCEPTABLE,
            description + ": " + o);
  }

  public static MaCoCoError SapImportDateiFehler(String o, String description) {
    return new MaCoCoError(MaCoCoErrorCode.SAP_IMPORT_DATEI_FEHLER, Status.NOT_ACCEPTABLE,
        description + ": " + o);
  }

  public static MaCoCoError SapImportDateiEncoding(String o, String description) {
    return new MaCoCoError(MaCoCoErrorCode.SAP_IMPORT_DATEI_ENCODING, Status.NOT_ACCEPTABLE,
        description + ": " + o);
  }

  public static MaCoCoError SapImportHeaderFehler(String o, String description) {
    return new MaCoCoError(MaCoCoErrorCode.SAP_IMPORT_HEADER_FEHLER, Status.NOT_ACCEPTABLE,
        description + ": " + o);
  }

  public static MaCoCoError SapImportInhaltFormatFehler(String o, String description) {
    return new MaCoCoError(MaCoCoErrorCode.SAP_IMPORT_INHALT_FORMAT_FEHLER, Status.NOT_ACCEPTABLE,
            description + ": " + o);
  }

  public static MaCoCoError unknown(String msg) {
    return new MaCoCoError(MaCoCoErrorCode.UNKNOWN, Status.NOT_ACCEPTABLE, msg);
  }

  public static MaCoCoError conflict() {
    return new MaCoCoError(MaCoCoErrorCode.CONFLICT, Status.SERVICE_UNAVAILABLE, "The Database is not available");
  }

  public static MaCoCoError budgetHasBuchungen(String Budget){
    return new MaCoCoError(MaCoCoErrorCode.BUDGET_HAS_BUCHUNGEN, Status.NOT_ACCEPTABLE, "Das Budget "+Budget+" kann nicht gelöscht werden, da es Buchungen enthält");
  }

  public static MaCoCoError kontohatBuchungen(String Konto){
    return new MaCoCoError(MaCoCoErrorCode.KONTO_NOT_DELETABLE, Status.NOT_ACCEPTABLE, "Das Konto "+ Konto+" kann nicht gelöscht werden, da es Buchungen enthält");
  }

}
