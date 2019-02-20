/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package backend.dtos;

import com.google.common.collect.Lists;
import common.DexTransformation;
import common.util.CDClassBuilder;
import common.util.TransformationUtils;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

  public abstract class AggregateTrafo extends DexTransformation {
  
  protected Collection<ASTCDCompilationUnit> cdList = Lists.newArrayList();
  
  protected Optional<ASTCDCompilationUnit> domainUnit = Optional.empty();
  
  private String packageName;
  
  private String className;
  
  public AggregateTrafo(String packageName, String className) {
    super();
    this.packageName = packageName;
    this.className = className;
  }
  
  /**
   * @see common.DexTransformation#transform()
   */
  @Override
  protected void transform() {
    checkArgument(!cdList.isEmpty());
    checkArgument(domainUnit.isPresent());
    
    ASTCDClass genClass = createClass(packageName, className);
    createImports(genClass);
    // Add attributes and methods for aggregated models
    for (ASTCDCompilationUnit compUnit : cdList) {
      String unitPackage = TransformationUtils.getPackageName(compUnit);
      for (ASTCDClass clazz : compUnit.getCDDefinition().getCDClassList()) {
        createAggregateImports(genClass, clazz, unitPackage);
        createAggregateAttributes(genClass, clazz);
        createAggregateMethods(genClass, clazz);
      }
    }
    
    String unitPackage = TransformationUtils.getPackageName(domainUnit.get());
    // Add attributes and methods for doamin model
    for (ASTCDClass clazz : domainUnit.get().getCDDefinition().getCDClassList()) {
      createDomainImports(genClass, clazz, unitPackage);
      createDomainAttributes(genClass, clazz);
      createDomainMethods(genClass, clazz);
    }
    
  }
  
  protected ASTCDClass createClass(String packageName, String className) {
    if (generateTOP && TransformationUtils
        .existsHandwrittenFile(className, packageName, handcodePath.get(),
            TransformationUtils.JAVA_FILE_EXTENSION)) {
      className += TransformationUtils.TOP_EXTENSION;
    }
    
    // Generate class
    ASTCDClass clazz = new CDClassBuilder().Public().setName(className).build();
    getOutputAst().addCDClass(clazz);
    return clazz;
  }
  
  protected void createImports(ASTCDClass clazz) {
  }
  
  protected void createAggregateImports(ASTCDClass clazz, ASTCDClass aggregateClass,
      String packageName) {
  }
  
  protected void createAggregateAttributes(ASTCDClass clazz, ASTCDClass aggregateClass) {
  }
  
  protected void createAggregateMethods(ASTCDClass clazz, ASTCDClass aggregateClass) {
  }
  
  protected void createDomainImports(ASTCDClass clazz, ASTCDClass domainClass, String packageName) {
  }
  
  protected void createDomainAttributes(ASTCDClass clazz, ASTCDClass domainClass) {
  }
  
  protected void createDomainMethods(ASTCDClass clazz, ASTCDClass domainClass) {
  }
  
  public void transform(ASTCDCompilationUnit cmpUnit, ASTCDCompilationUnit domainUnit,
      Collection<ASTCDCompilationUnit> cdList) {
    this.cdList = cdList;
    this.domainUnit = Optional.ofNullable(domainUnit);
    transform(cmpUnit);
  }

}
