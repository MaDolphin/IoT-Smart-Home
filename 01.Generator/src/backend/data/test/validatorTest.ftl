<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The init method in a unit test

-->
${signature("identifier")}
<#assign creator = identifier?uncap_first + "Creator">
<#assign object = identifier?uncap_first>
{
  ${identifier}DummyCreator ${creator} = get${identifier}DummyCreator();
  ${identifier} ${object} = ${creator}.get${identifier}();

  Optional<String> validationMessages = ${identifier}Validator.getValidator().getValidationErrors(${object});

  if(validationMessages.isPresent()){
     fail(validationMessages.toString());
  }
}
