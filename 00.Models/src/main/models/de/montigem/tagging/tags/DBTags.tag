/*
*  (c) https://github.com/MontiCore/monticore
*/

package de.montigem.be.domain;

conforms to de.montigem.tagging.tagschema.DBSchema;

tags DBTags for Domain {
  tag DomainUser.username, DomainUser.email with UniqueDBColumn;
}