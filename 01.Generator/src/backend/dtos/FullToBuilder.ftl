<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalName", "assocs")}
<#assign assocHelper = tc.instantiate("common.util.CDAssociationUtil")>
<#assign objName=originalName?uncap_first>
{
  ${originalName}Builder builder = super.toBuilder();
<#list assocs as assoc>
  <#assign assocName=assocHelper.getAssociationName(assoc)>
  <#assign assocType=assoc.getTargetType().getName()>
  <#if assocHelper.isOptional(assoc)>
    if (get${assocName?cap_first}().isPresent()) {
      Optional<${assocType}> _${assocName} = daoLib.get${assocType}DAO().findAndLoad(${assocName}, daoLib, securityHelper);
      if (!_${assocName}.isPresent()) {
        throw new EntityNotFoundException("Cannot find ${assocName?cap_first} with id " + ${assocName});
      }
      builder.${assocName}(_${assocName});
    }
  <#elseif assocHelper.isMultiple(assoc)>
    List<${assocType}> _${assocName} = daoLib.get${assocType}DAO().loadAll(daoLib, securityHelper.getSessionCompliantResource()).stream().filter(o -> ${assocName}List.contains(o.getId())).collect(Collectors.toList());
    if (_${assocName}.size() != ${assocName}List.size()) {
      throw new EntityNotFoundException("Cannot find all ${assocName} from list:" + ${assocName}List.toString());
    }
    builder.${assocName}(_${assocName});
  <#else>
    builder.${assocName}(daoLib.get${assocType}DAO().findAndLoad(${assocName}, daoLib, securityHelper).orElseThrow(EntityNotFoundException::new));
  </#if>
</#list>
  return builder;
}
