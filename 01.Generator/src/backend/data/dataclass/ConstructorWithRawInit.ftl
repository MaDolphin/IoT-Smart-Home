<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the constructor for objects.

-->
${tc.signature("constructorArgumentNames")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign assocUtil = tc.instantiate("common.util.CDAssociationUtil")>

{

  super();
  rawInitAttrs( ${constructorArgumentNames?join(",")} );

}
