<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the default method body for every get method of an attribute.

  @author: SE RWTH Aachen
-->
${tc.signature("type", "attributes")}
<#assign astPrinter = tc.instantiate("de.monticore.umlcd4a.prettyprint.AstPrinter")>
{
      Query query = getEntityManager().createNativeQuery("");
    /*  query.unwrap(SQLQuery.class)
<#list attributes as attribute>
        .addScalar("${attribute.getName()}", ${attribute.getType()}Type.INSTANCE)
</#list>
        .setResultTransformer(Transformers.aliasToBean(${type}DTO.class)); */

    List<${type}DTO> queryResultList = query.getResultList();

  return queryResultList;
}
