/* (c) https://github.com/MontiCore/monticore */

package backend.coretemplates.association;

import backend.coretemplates.association.derived.DerivedAssociationStrategy;
import backend.coretemplates.association.derived.DerivedOrderedAssociationStrategy;
import backend.coretemplates.association.derived.QualifiedDerivedAssociationStrategy;
import backend.coretemplates.association.derived.QualifiedDerivedOrderedAssociationStrategy;
import backend.coretemplates.association.ordered.OrderedAssociationStrategy;
import backend.coretemplates.association.ordinary.DefaultAssociationStrategy;
import backend.coretemplates.association.qualified.QualifiedAssociationStrategy;
import backend.coretemplates.association.qualifiedordered.QualifiedOrderedAssociationStrategy;
import com.google.common.collect.Lists;
import common.DexTransformation;
import common.util.CDAssociationUtil;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDQualifier;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


public class AssociationSupportTrafo extends DexTransformation {

  @Override
  protected void transform() {

    checkArgument(symbolTable.isPresent());

    // handle classes
    for (ASTCDClass clazz : getAst().getCDClassList()) {
      iterateAndHandle(clazz.getName());
    }

    // get interface
    for (ASTCDInterface interf : getAst().getCDInterfaceList()) {
      iterateAndHandle(interf.getName());
    }

    getAst().getCDAssociationList().clear();
  }

  private void iterateAndHandle(String name) {
    Optional<CDTypeSymbol> cdType = symbolTable.get().resolve(name);
    checkNotNull(cdType);
    checkArgument(cdType.isPresent());

    for (CDAssociationSymbol symbol : cdType.get().getAssociations()) {
      String callMethodName = null;
      String removeExpression = null;

      if (symbol.isBidirectional()) {
        callMethodName = AssociationNameUtil.getCallMethod(symbol);
        removeExpression = AssociationNameUtil.getRemoveMethodCall(symbol);
      }
      handleAssociation(symbol, callMethodName, removeExpression);
    }
  }

  private void handleAssociation(CDAssociationSymbol symbol, String callMethod, String removeExpression) {
    Optional<ASTCDQualifier> revQualifier = CDAssociationUtil.getOppositeQualifier(symbol);

    Optional<CDTypeSymbol> cdType = symbolTable.get().resolve(symbol.getSourceType().getName());
    checkArgument(cdType.isPresent());

    // get all classes where the methods for handling the association have to be added
    List<CDTypeSymbol> symbolsToRegard = Lists.newArrayList();
    if (cdType.get().isInterface()) {
      symbolsToRegard.addAll(symbolTable.get().getImplementingSubclasses(symbol.getSourceType().getName()));
    }
    symbolsToRegard.add(cdType.get());

    for (CDTypeSymbol regardingSymbol : symbolsToRegard) {
      AssociationStrategy assocStrategy = getAssociationStrategy(symbol);

      // set call method name to handle association consistency
      assocStrategy.setAddMethodCall(callMethod);
      assocStrategy.setRemoveMethodCall(removeExpression);

      // now add all created attributes and methods to all classes involved in
      // this association
      Optional<ASTNode> astNode = regardingSymbol.getAstNode();
      checkArgument(astNode.isPresent());

      if (regardingSymbol.isInterface()) {
        checkArgument(astNode.get() instanceof ASTCDInterface);
        assocStrategy.setHookPoint(new StringHookPoint(";"));

        ASTCDInterface interf = (ASTCDInterface) astNode.get();

        // add methods
        interf.getCDMethodList().addAll(assocStrategy.getGetMethods());
        interf.getCDMethodList().addAll(assocStrategy.getCommonMethods());

        if (symbol.isBidirectional() && !CDAssociationUtil.isDerived(symbol)) {
          interf.getCDMethodList().addAll(assocStrategy.getRawAddMethods());
          interf.getCDMethodList().addAll(assocStrategy.getRawRemoveMethods());
        }

        if (!(revQualifier.isPresent() && symbol.isBidirectional())) {
          interf.getCDMethodList().addAll(assocStrategy.getAddMethods());
          interf.getCDMethodList().addAll(assocStrategy.getRemoveMethods());
        }
      }
      else {
        checkArgument(astNode.get() instanceof ASTCDClass);

        ASTCDClass clazz = (ASTCDClass) astNode.get();

        Optional<ASTCDAttribute> assocAtt = assocStrategy.getAssociationAttribute();

        if (assocAtt.isPresent()) {
          clazz.getCDAttributeList().add(assocAtt.get());
        }

        // add methods
        if (!(revQualifier.isPresent() && symbol.isBidirectional())) {
          clazz.getCDMethodList().addAll(assocStrategy.getRemoveMethods());
          clazz.getCDMethodList().addAll(assocStrategy.getAddMethods());
        }

        clazz.getCDMethodList().addAll(assocStrategy.getGetMethods());
        clazz.getCDMethodList().addAll(assocStrategy.getCommonMethods());

        if (symbol.isBidirectional() && !CDAssociationUtil.isDerived(symbol)) {
          clazz.getCDMethodList().addAll(assocStrategy.getRawAddMethods());
          clazz.getCDMethodList().addAll(assocStrategy.getRawRemoveMethods());
        }
      }
    }
  }

  private AssociationStrategy getAssociationStrategy(CDAssociationSymbol symbol) {
    AssociationStrategy assocStrategy; // check for derived association

    boolean ordered = CDAssociationUtil.isOrdered(symbol);
    Optional<ASTCDQualifier> qualifier = CDAssociationUtil.getQualifier(symbol);

    if (CDAssociationUtil.isDerived(symbol) && !qualifier.isPresent() && !ordered) {
      assocStrategy = new DerivedAssociationStrategy(symbol, getGlex());
    }
    // check for qualified derived association
    // TODO AR <- GV:: Name. Type
    else if (CDAssociationUtil.isDerived(symbol) && qualifier.isPresent()
        && qualifier.get().getTypeOpt().isPresent()
        && qualifier.get().getNameOpt().isPresent() && !ordered) {
      assocStrategy = new QualifiedDerivedAssociationStrategy(symbol, getGlex());
    }
    // check for ordered derived association
    else if (CDAssociationUtil.isDerived(symbol) && !qualifier.isPresent() && ordered) {
      assocStrategy = new DerivedOrderedAssociationStrategy(symbol, getGlex());
    }
    // check for qualified ordered derived association
    // TODO AR <- GV:: Name. Type
    else if (CDAssociationUtil.isDerived(symbol) && qualifier.isPresent()
        && qualifier.get().getTypeOpt().isPresent()
        && qualifier.get().getNameOpt().isPresent() && ordered) {
      assocStrategy = new QualifiedDerivedOrderedAssociationStrategy(symbol, getGlex());
    }
    // check for qualified ordered association
    // TODO AR <- GV:: Name. Type
    else if (qualifier.isPresent() && qualifier.get().getTypeOpt().isPresent()
        && qualifier.get().getNameOpt().isPresent() && ordered) {
      assocStrategy = new QualifiedOrderedAssociationStrategy(symbol, getGlex());
    }
    // check for qualified association
    // TODO AR <- GV:: Name. Type
    else if (qualifier.isPresent() && qualifier.get().getTypeOpt().isPresent()
        && qualifier.get().getNameOpt().isPresent()) {
      assocStrategy = new QualifiedAssociationStrategy(symbol, getGlex());
    }
    // check for ordered association
    else if (ordered) {
      assocStrategy = new OrderedAssociationStrategy(symbol, getGlex());
    }
    // check for ordinary association
    else {
      assocStrategy = new DefaultAssociationStrategy(symbol, getGlex());
    }
    return assocStrategy;
  }
}
