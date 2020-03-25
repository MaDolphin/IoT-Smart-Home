<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the constructor for objects.

-->
${tc.signature("identifier")}
{
   if (o == this) {
     return true;
   }
   if (!(o instanceof ${identifier})) {
     return false;
   }
   ${identifier} ${identifier?lower_case} = (${identifier}) o;
   return ${identifier?lower_case}.getId() == id;
}
