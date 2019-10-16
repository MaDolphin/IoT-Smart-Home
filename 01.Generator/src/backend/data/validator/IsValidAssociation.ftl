<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The core method in a builder to validate if an object is consistent.

  @author: SE RWTH Aachen
-->
${signature("className", "assoc", "type", "oclConstraints")}
<#assign cdassoc = tc.instantiate("common.util.CDAssociationUtil")>
<#assign assocName = tc.instantiate("backend.coretemplates.association.AssociationNameUtil")>
<#assign attr = assoc.getTargetType()?uncap_first>

{
  // handle associations
  <#if cdassoc.isOptional(assoc) >
    ${type} ${attr} = value.get${assoc.getDerivedName()?cap_first}Optional();
  <#elseif cdassoc.isMultiple(assoc) >
    ${type} ${attr} = value.${assocName.getGetAllMethodName(assoc).orElse("")}();
  <#else>
    ${type} ${attr} = value.get${assoc.getDerivedName()?cap_first}();
  </#if>

  if (${attr} == null){
    String msg = "Assoziation " + ${className}.getHumanNameForAssociation${assoc.getDerivedName()?cap_first}() + " muss gesetzt werden.\n";
    Log.debug(msg, getClass().getName());
    return Optional.of(msg);
  }

  // ocl constraints
  Optional<String> oclMessage = Optional.empty();
    <#list oclConstraints as method>
  oclMessage = ${method.getName()}(${attr});
  if (oclMessage.isPresent()) {
    Log.debug("Validator: " + oclMessage.get(), getClass().getName());
    return oclMessage;
  }
    </#list>

  <#if cdassoc.isOptional(assoc) >
  if (${attr}.isPresent()) {
    return ${attr}.get().getValidator().getValidationErrors(${attr}.get());
  }
  return Optional.empty();
  
  <#elseif cdassoc.isMultiple(assoc) >
  if (${attr}.size() > 0) {
    StringBuilder msgs = new StringBuilder();
    ${attr}.stream().map(o -> o.getValidator().getValidationErrors(o)).forEach(o -> o.ifPresent(msgs::append));
    if (msgs.length() != 0) {
      return Optional.of(msgs.toString());
    }
  }
  return Optional.empty();
  
  <#else>
  return ${attr}.getValidator().getValidationErrors(${attr});
  </#if>
}
