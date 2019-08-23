<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  @param CDTypeSymbol ast
-->
${tc.signature("paramName", "assocList")}
<#assign assocHelper=tc.instantiate("common.util.CDAssociationUtil")>

{
  <#list assocList as assoc>
    <#assign getter=assocHelper.getAssociationName(assoc)?cap_first>
    <#if assocHelper.isOptional(assoc)>
      if (${paramName}.get${getter}Optional().isPresent() && !Hibernate.isInitialized(${paramName}.get${getter}())) {
        Hibernate.initialize(${paramName}.get${getter}());
        daoLib.get${assoc.getTargetType().getName()}DAO().loadEager(${paramName}.get${getter}(), daoLib, resource);
      }
    <#elseif assocHelper.isMultiple(assoc)>
      <#assign getter=getter+"s">
      if (!Hibernate.isInitialized(${paramName}.get${getter}())) {
        Hibernate.initialize(${paramName}.get${getter}());
        ${paramName}.get${getter}().forEach(a -> daoLib.get${assoc.getTargetType().getName()}DAO().loadEager(a, daoLib, resource));
      }
    <#else>
      if (!Hibernate.isInitialized(${paramName}.get${getter}())) {
        Hibernate.initialize(${paramName}.get${getter}());
        daoLib.get${assoc.getTargetType().getName()}DAO().loadEager(${paramName}.get${getter}(), daoLib, resource);
      }
    </#if>
  </#list>
  return ${paramName};
}
