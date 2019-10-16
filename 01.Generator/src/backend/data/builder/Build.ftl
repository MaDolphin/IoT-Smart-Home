<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.

  @author: SE RWTH Aachen
-->
${tc.signature("class", "paramList", "allAssociations")}

<#assign assocUtil = tc.instantiate("common.util.CDAssociationUtil")>
<#assign transformationUtils = tc.instantiate("common.util.TransformationUtils")>
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
{
<#if class.isAbstract()>
throw new org.apache.commons.lang.NotImplementedException("${transformationUtils.getErrorCode()}: Cannot create ${class.name} object, because it is abstract.");
<#else>
    <#assign constructorArguments = []>

    <#list paramList as attr>
        <#assign constructorArguments = constructorArguments + ["${attr.getName()}"]>
    </#list>

// Create the actual object
${class.getName()} newObj = new ${class.getName()}().rawInitAttrs( ${constructorArguments?join(",")} );

// Set the id
newObj.setId(id);

if (addToBidirectionalAssociation) {
<#list allAssociations as assoc>
    <#if assoc.isBidirectional()>
        <#if assocUtil.isSourceOne(assoc) || assocUtil.isSourceOptional(assoc)>
            <#if assocUtil.isTargetOne(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.rawSet${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
            <#elseif assocUtil.isTargetOptional(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.ifPresent(o -> o.rawSet${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj));
            <#elseif assocUtil.isTargetMultiple(assoc)>
                for (${typeHelper.getFirstTypeArgumentOfGeneric(assocUtil.getAssociationType(assoc))} o : ${assocUtil.getAssociationName(assoc)}) {
                    o.rawSet${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
                }
            </#if>
        <#elseif assocUtil.isSourceMultiple(assoc)>
            <#if assocUtil.isTargetOne(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.add${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
            <#elseif assocUtil.isTargetOptional(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.ifPresent(o -> o.add${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj));
            <#elseif assocUtil.isTargetMultiple(assoc)>
            for (${typeHelper.getFirstTypeArgumentOfGeneric(assocUtil.getAssociationType(assoc))} o : ${assocUtil.getAssociationName(assoc)}) {
                o.add${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
            }
            </#if>
        </#if>
    </#if>
</#list>
}

Optional<String> validationMessages = ${class.getName()}Validator.getValidator().getValidationErrors(newObj);
if (validationMessages.isPresent()) {
  throw new ValidationException(validationMessages.get());
}
return newObj;
</#if>
}
