/*
*  (c) https://github.com/MontiCore/monticore
*/

package de.montigem.be.domain;

conforms to de.montigem.tagging.tagschema.AuthSchema;

tags AuthTags for Domain {
  tag DomainUser with PermissionClass = USER;
  tag RoleAssignment with PermissionClass = NONE;
}