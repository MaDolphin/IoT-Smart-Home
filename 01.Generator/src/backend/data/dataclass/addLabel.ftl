<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the constructor for objects.

-->

${tc.signature("newLabel")}
{
    if (this.labels == null)
    {
        this.labels = new Vector<String>();
    }
    if (!this.labels.contains(${newLabel})){
        this.labels.add(${newLabel});
    }
}
