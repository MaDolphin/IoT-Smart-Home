<#-- (c) https://github.com/MontiCore/monticore -->
{
  <#assign returnStatement = false>
  <#assign methodName = "${ast.name}">

  <#if !( (methodName?starts_with("add") && ast.getCDParameters()?size gt 1)
    || methodName?starts_with("clear") || methodName?starts_with("set") || methodName?starts_with("rawSet")
    || methodName?starts_with("retainAll") || (methodName?starts_with("put") && ast.getCDParameters()?size == 3)
    || methodName?starts_with("rawUnset"))>
    <#assign returnStatement = true>
  </#if>

  <#if methodName?starts_with("raw")>
    if (this.object.isPresent()){
    <#if returnStatement>
      return
    </#if>
      this.object.get().${methodName}(
        <#list ast.getCDParameters() as p>
          ${p.name} <#if p_has_next>,</#if>
        </#list>
      );
    } else {
      <#if returnStatement>
        return
      </#if>
        super.${methodName}(
          <#list ast.getCDParameters() as p>
          ${p.name} <#if p_has_next>,</#if>
          </#list>
        );
    }

  <#else>
    <#if methodName?starts_with("add") || methodName?starts_with("set") || methodName?starts_with("put")>
      if (initiated){
    </#if>

    loadObjectIfNecessary();
    <#if returnStatement>
      return
    </#if>

    this.object.get().${ast.getName()}(<#list ast.getCDParameters() as param>${param.getName()}<#if param_has_next>, </#if></#list>);

    <#if methodName?starts_with("add") || methodName?starts_with("set") || methodName?starts_with("put")>
      } else {
      <#if returnStatement>
        return
      </#if>
      super.${methodName}(
        <#list ast.getCDParameters() as p>
          ${p.name} <#if p_has_next>,</#if>
        </#list>
      );
      }
    </#if>
  </#if>
}
