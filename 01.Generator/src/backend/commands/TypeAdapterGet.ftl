<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz")}
<#assign transformationUtils = tc.instantiate("common.util.TransformationUtils")>
{
  if (INSTANCE==null) {
    INSTANCE = new ${transformationUtils.removeTOPExtensionIfExistent(clazz.getName())}();
  }
  return INSTANCE;
}
