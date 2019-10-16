/* (c) https://github.com/MontiCore/monticore */

package de.montigem.ocl._symboltable;

import de.montigem.ocl._visitor.OCLVisitor;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

public class OCLSymbolTableCreator extends ocl.monticoreocl.ocl._symboltable.OCLSymbolTableCreator implements OCLVisitor {

  private OCLVisitor realThis;


  public OCLSymbolTableCreator(ResolvingConfiguration resolverConfig, MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
    this.realThis = this;
  }

  @Override
  public void setRealThis(OCLVisitor realThis) {
    this.realThis = this;
  }

  @Override
  public OCLVisitor getRealThis() {
    return this.realThis;
  }
}
