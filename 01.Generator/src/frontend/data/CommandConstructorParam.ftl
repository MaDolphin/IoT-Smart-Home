<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("typeName", "varList")}
{
  super('${typeName}');
  <#list varList as varName>
    this.${varName} = ${varName};
  </#list>
}
