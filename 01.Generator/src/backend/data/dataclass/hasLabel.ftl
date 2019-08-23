<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the constructor for objects.

  @author: SE RWTH Aachen
-->
${tc.signature("targetLabel")}
{
    if (this.labels == null) {
        return false;
    }
     for (String currentLabel : this.labels)
     {
         if (currentLabel.equals(${targetLabel}))
         {
             return true;
         }
     }
     return false;
}
