<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The init method in a unit test

-->
${signature("identifier", "assocs", "callParam")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
{
  <#list assocs as assoc>
    <#assign type = assoc.getType().getName() >
    <#assign name = assoc.getName() >
    ${type} ${name?uncap_first} = new ${type}DummyCreator().get${type}();
  </#list>
  this.obj = new ${identifier}().rawInitAttrs(${callParam});
}
