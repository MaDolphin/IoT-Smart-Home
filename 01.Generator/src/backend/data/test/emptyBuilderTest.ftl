<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The init method in a unit test

-->
${signature("identifier")}
{
  ${identifier}Builder builder = new ${identifier}Builder();
  ${identifier} ${identifier?uncap_first}=null;

 try {
    ${identifier?uncap_first} = builder.build();
    assertNotNull(${identifier?uncap_first});
 }
 catch (ValidationException e) {
    assertNull(${identifier?uncap_first});
 }
}
