/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package backend.coretemplates.association.ordinary;

import backend.common.CoreTemplate;
import backend.coretemplates.association.AssociationAttributeFactory;
import backend.coretemplates.association.AssociationMethodFactory;
import backend.coretemplates.association.AssociationNameUtil;
import backend.coretemplates.association.AssociationStrategy;
import com.google.common.collect.Lists;
import common.util.CDAssociationUtil;
import common.util.CDSimpleReferenceBuilder;
import common.util.TransformationUtils;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;

import java.util.List;
import java.util.Optional;

import static de.monticore.types.types._ast.TypesNodeFactory.createASTQualifiedName;

public class DefaultAssociationStrategy implements AssociationStrategy {

  protected String type;

  protected String name;

  protected CDAssociationSymbol symbol;

  protected HookPoint hp;

  protected GlobalExtensionManagement glex;

  private String addMethodCall;

  private String removeMethodCall;

  public DefaultAssociationStrategy(CDAssociationSymbol symbol,
                                    GlobalExtensionManagement glex) {
    this.name = TransformationUtils.uncapitalize(CDAssociationUtil.getAssociationName(symbol));
    this.type = symbol.getTargetType().getName();
    this.symbol = symbol;
    this.glex = glex;
  }

  public void setHookPoint(HookPoint hp) {
    this.hp = hp;
  }

  private ASTCDStereoValue createStereotype(String content) {
    return CD4AnalysisMill.cDStereoValueBuilder().setName(content).setValue(TransformationUtils.ATTRIBUTE_ANNOATION).build();
  }

  private void createAndAddStereotype(ASTCDAttribute attr, String content) {
    ASTCDStereoValue value = createStereotype(content);
    if (!attr.getModifier().getStereotypeOpt().isPresent()) {
      ASTCDStereotype stereoType = CD4AnalysisMill.cDStereotypeBuilder().build();
      attr.getModifier().setStereotype(stereoType);
    }
    attr.getModifier().getStereotype().getValueList().add(value);
  }

  private String getMappedName() {
    Optional<CDAssociationSymbol> oppositeSymbol = CDAssociationUtil.findOppositeSymbol(symbol);
    return TransformationUtils.uncapitalize(oppositeSymbol.isPresent() ? CDAssociationUtil.getAssociationName(oppositeSymbol.get()) : "");
  }

  @Override
  public Optional<ASTCDAttribute> getAssociationAttribute() {
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      ASTCDAttribute attr = AssociationAttributeFactory
          .createMany(name, new CDSimpleReferenceBuilder().name(type).build());

      String mappedBy = symbol.isBidirectional()? ", mappedBy = \"" + getMappedName() + "\"": "";

      boolean isManyToMany =  CDAssociationUtil.isSourceMultiple(symbol); // TODO GV, SVa: check

      String cardinality = isManyToMany? "@ManyToMany": "@OneToMany";
      String val = cardinality + "(targetEntity = "
          + type + ".class" + (isManyToMany? ")": ", cascade = CascadeType.ALL" + mappedBy + ", orphanRemoval=true)");

      if(symbol.containsStereotype(TransformationUtils.STEREOTYPE_NOCASCADE, TransformationUtils.STEREOTYPE_NOCASCADE)) {
        val = "@OneToMany(targetEntity = " + type + ".class" + mappedBy + ")";
      }
      createAndAddStereotype(attr, val);
      createAndAddStereotype(attr, "@Fetch(value = FetchMode.SUBSELECT)");

      if (!symbol.isBidirectional()) {
        createAndAddStereotype(attr, "@JoinTable(name = \"" + symbol.getSourceType() + "_" + CDAssociationUtil.getAssociationName(symbol) + "\")");
      }

      return Optional.ofNullable(attr);
    } else {
      ASTCDAttribute attr = AssociationAttributeFactory.createOne(name,
          new CDSimpleReferenceBuilder().name(type).build());
      String val = "";
      if (CDAssociationUtil.isSourceMultiple(symbol)) {
        val = "@ManyToOne";
      } else {
        val = "@OneToOne";

        // check if is RightToLeft
        ASTCDAssociation assoc = ((ASTCDAssociation) symbol.getAstNode().get());
        boolean useCascade = true;
        if (assoc.isBidirectional() && assoc.getRightToLeftSymbol().isPresent()) {
          useCascade = assoc.getLeftToRightSymbol().get() == symbol;
        }
        if(symbol.containsStereotype(TransformationUtils.STEREOTYPE_NOCASCADE, TransformationUtils.STEREOTYPE_NOCASCADE)) {
          useCascade = false;
        }
        if (CDAssociationUtil.isTargetOptional(symbol) || CDAssociationUtil.isSourceOne(symbol) && CDAssociationUtil.isTargetOne(symbol) && useCascade) {
          val += "(cascade = CascadeType.ALL, orphanRemoval=true)";
        }
      }
      createAndAddStereotype(attr, val);
      return Optional.ofNullable(attr);
    }
  }

  @Override
  public List<ASTCDMethod> getAddMethods() {
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      return createAddMany();
    } else {
      return createAddOne();
    }
  }

  @Override
  public List<ASTCDMethod> getGetMethods() {
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      return createGetMany();
    } else {
      if (CDAssociationUtil.isOne(symbol)) {
        return createGetOne();
      } else {
        return createOptionalGetOne();
      }
    }
  }

  @Override
  public List<ASTCDMethod> getRemoveMethods() {
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      return createRemoveMany();
    } else {
      return Lists.newArrayList();
    }
  }

  @Override
  public List<ASTCDMethod> getCommonMethods() {
    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      return createCommonMany();
    } else {
      return Lists.newArrayList();
    }
  }

  @Override
  public List<ASTCDMethod> getRawAddMethods() {
    List<ASTCDMethod> methods = Lists.newArrayList();

    if (CDAssociationUtil.isOne(symbol) || CDAssociationUtil.isOptional(symbol)) {
      // get the correct type
      String localType = this.type;

      // create contains
      ASTCDMethod rawSet = AssociationMethodFactory.createRawSetMethod(localType, symbol);

      // handle hook points
      HookPoint rawSetHp;

      if (this.hp != null) {
        rawSetHp = this.hp;
      } else if (CDAssociationUtil.isOptional(symbol)) {
        rawSetHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.OPTIONAL_RAWSET_METHOD_BODY.valueOf(), this.name,
            "o", CDAssociationUtil.isOppositeQualified(symbol) ? null : this.removeMethodCall);
      } else {
        rawSetHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.RAWSET_METHOD_BODY.valueOf(), this.name, "o",
            this.removeMethodCall, CDAssociationUtil.isOppositeQualified(symbol));
      }

      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), rawSet, rawSetHp);
      methods.add(rawSet);

      ASTCDMethod rawUnSet = AssociationMethodFactory.createRawUnSetMethod(symbol);

      // handle hook points
      HookPoint rawUnsetHp;

      if (this.hp != null) {
        rawUnsetHp = this.hp;
      } else {
        if (CDAssociationUtil.isOne(symbol)) {
          rawUnsetHp = new TemplateHookPoint(
              CoreTemplate.THROW_DATASTRUCTUREVIOLATION_EXCEPTION_METHOD
                  .toString(), symbol.getSourceType().getName() + "." + rawUnSet.getName(), true);
        } else {
          rawUnsetHp = new StringHookPoint("{ this." + this.name + " = null;}");
        }
      }

      glex.replaceTemplate(
          CoreTemplate.EMPTY_METHOD.toString(),
          rawUnSet, rawUnsetHp);
      methods.add(rawUnSet);

    }

    // cardinality [1..*] and [*]
    else {
      // create rawAdd
      ASTCDMethod rawAdd = AssociationMethodFactory.createRawAddMethod(this.type, symbol);

      // create rawAddAll
      ASTCDMethod rawAddAll = AssociationMethodFactory.createRawAddAllMethod(this.type, symbol);

      // handle hook points
      HookPoint rawAddHp, rawAddAllHp;

      if (this.hp != null) {
        rawAddHp = rawAddAllHp = this.hp;
      } else {
        rawAddHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.RAWADD_METHOD_BODY.valueOf(), this.name, "o");

        rawAddAllHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.RAWADDALL_METHOD_BODY.valueOf(), this.name, "o");
      }

      glex.replaceTemplate(
          CoreTemplate.EMPTY_METHOD.toString(), rawAdd,
          rawAddHp);

      glex.replaceTemplate(
          CoreTemplate.EMPTY_METHOD.toString(),
          rawAddAll, rawAddAllHp);

      methods.add(rawAddAll);
      methods.add(rawAdd);
    }

    return methods;
  }

  protected List<ASTCDMethod> createAddOne() {
    // get the correct type
    String localType = CDAssociationUtil.isOptional(symbol) ? "Optional<" + this.type + ">" : this.type;
    // create contains
    ASTCDMethod set = AssociationMethodFactory.createSetMethod(localType, symbol);
    // handle hook points
    HookPoint setHp;

    boolean isMultiply = CDAssociationUtil.isSourceMultiple(symbol);
    String containsMethodName = AssociationNameUtil.getContainsMethodNameOfOppositeAssoc(symbol).orElse("");
    if (this.hp != null) {
      setHp = this.hp;
    } else if (CDAssociationUtil.isOptional(symbol)) {
      setHp = new TemplateHookPoint(AssociationMethodFactory.Templates.OPTIONAL_SET_METHOD_BODY.valueOf(), this.name, "o",
          this.addMethodCall, containsMethodName, symbol.getSourceType().getName(), isMultiply);
    } else {
      setHp = new TemplateHookPoint(AssociationMethodFactory.Templates.SET_METHOD_BODY.valueOf(),
          this.name, "o", this.addMethodCall, containsMethodName, symbol.getSourceType().getName(), isMultiply);
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), set, setHp);
    List<ASTCDMethod> lst = Lists.newArrayList(set);
    // handle additional method for optional
    if (CDAssociationUtil.isOptional(symbol)) {
      ASTCDMethod optSet = AssociationMethodFactory.createSetMethod(this.type, symbol);
      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), optSet,
          this.hp != null ? this.hp : (new StringHookPoint("{" + AssociationNameUtil.getSetValueMethodName(symbol).get() + "(Optional.ofNullable(o));}")));
      lst.add(optSet);
    }
    return lst;
  }

  protected List<ASTCDMethod> createAddMany() {
    // create add
    ASTCDMethod add = AssociationMethodFactory.createAddMethod(this.type, symbol);
    // create addAll
    ASTCDMethod addAll = AssociationMethodFactory.createAddAllMethod(this.type, symbol);
    // create getList
    ASTCDMethod setAll = AssociationMethodFactory.createSetAllMethod(this.type, symbol);

    // handle hook points
    HookPoint addHp, addAllHp, setAllHp;

    if (this.hp != null) {
      addHp = addAllHp = setAllHp = this.hp;
    } else {
      addHp = new TemplateHookPoint(AssociationMethodFactory.Templates.ADD_METHOD_BODY.valueOf(),
          this.name, "o");

      addAllHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.ADDALL_METHOD_BODY.valueOf(), this.name, "o");

      setAllHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.SET_ALL_METHOD_BODY.valueOf(), this.name, "o");
    }

    glex.replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(), add, addHp);

    glex.replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(), addAll,
        addAllHp);

    glex.replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(), setAll,
        setAllHp);

    return Lists.newArrayList(add, addAll, setAll);
  }

  protected List<ASTCDMethod> createGetOne() {
    // create contains
    ASTCDMethod get = AssociationMethodFactory.createGetMethod(this.type, symbol);
    // handle hook points
    HookPoint getHp;

    if (this.hp != null) {
      getHp = this.hp;
    } else {
      getHp = new TemplateHookPoint(AssociationMethodFactory.Templates.GET_METHOD_BODY.valueOf(),
          this.name);
    }
    glex.replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(), get, getHp);

    return Lists.newArrayList(get);
  }

  protected List<ASTCDMethod> createOptionalGetOne() {
    // create contains
    ASTCDMethod getOptional = AssociationMethodFactory.createGetOptionalMethod(
        "Optional<" + this.type + ">", symbol);

    ASTCDMethod get = AssociationMethodFactory.createGetMethod(
        this.type, symbol);

    // handle hook points
    HookPoint getHp, getOptionalHp;

    if (this.hp != null) {
      getHp = getOptionalHp = this.hp;
    } else {
      getHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.OPTIONAL_GET_METHOD_BODY.valueOf(), this.name);
      getOptionalHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.OPTIONAL_GET_OPTIONAL_METHOD_BODY.valueOf(), this.name);
    }
    glex.replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(), get, getHp);
    glex.replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(), getOptional, getOptionalHp);

    return Lists.newArrayList(get, getOptional);
  }

  protected List<ASTCDMethod> createGetMany() {

    // create contains
    ASTCDMethod contains = AssociationMethodFactory.createContainsMethod(this.type, symbol);

    // create containsAll
    ASTCDMethod containsAll = AssociationMethodFactory.createContainsAllMethod(this.type, symbol);

    // handle hook points
    HookPoint containsHp, containsAllHp;

    if (this.hp != null) {
      containsHp = containsAllHp = this.hp;
    } else {

      containsHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.CONTAINS_METHOD_BODY.valueOf(), this.name, "o",
          CDAssociationUtil.isOneToMany(symbol));

      containsAllHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.CONTAINSALL_METHOD_BODY.valueOf(), this.name, "o",
          CDAssociationUtil.isOneToMany(symbol));
    }

    glex.replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(), contains,
        containsHp);

    glex.replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(),
        containsAll, containsAllHp);

    return Lists.newArrayList(contains, containsAll);
  }

  protected List<ASTCDMethod> createRemoveMany() {
    List<ASTCDMethod> methods = Lists.newArrayList();

    // create remove
    ASTCDMethod remove = AssociationMethodFactory.createRemoveMethod(this.type, symbol);
    ASTCDMethod retainAll = AssociationMethodFactory.createRetainAllMethod(this.type, symbol);
    // create removeAll
    ASTCDMethod removeAll = AssociationMethodFactory.createRemoveAllMethod(this.type, symbol);

    if (CDAssociationUtil.isOneToMany(symbol)) {
      remove.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
          "backend/data", "backend.data.onsistencyViolationException")));
      removeAll.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
          "backend/data", "backend.data.onsistencyViolationException")));
      retainAll.getExceptionList().add(createASTQualifiedName(Lists.newArrayList("dex",
          "backend/data", "backend.data.onsistencyViolationException")));
    }

    // handle hook points
    HookPoint removeHp, removeAllHp, retainAllHp;

    if (this.hp != null) {
      removeHp = removeAllHp = retainAllHp = this.hp;
    } else {
      removeAllHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.REMOVEALL_METHOD_BODY.valueOf(), this.type,
          TransformationUtils.makeCamelCase("remove", this.name), "o");
      removeHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.REMOVE_METHOD_BODY.valueOf(), this.name, "o",
          this.removeMethodCall, CDAssociationUtil.isOneToMany(symbol), symbol.getSourceType()
          .getName() + "." + remove.getName(), CDAssociationUtil.isComposition(symbol));
      retainAllHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.RETAINALL_METHOD_BODY.valueOf(), this.name);
    }

    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), removeAll, removeAllHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), remove, removeHp);
    glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), retainAll, retainAllHp);

    methods.add(remove);
    methods.add(retainAll);
    methods.add(removeAll);

    if (!CDAssociationUtil.isOneToMany(symbol)) {
      // create clear
      ASTCDMethod clear = AssociationMethodFactory.createClearMethod(symbol);

      // handle hook points
      HookPoint clearHp;

      if (this.hp != null) {
        clearHp = this.hp;
      } else {
        clearHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.CLEAR_METHOD_BODY.valueOf(), this.type, this.name,
            TransformationUtils.makeCamelCase("remove", this.name));
      }

      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), clear, clearHp);

      methods.add(clear);
    }
    // add additional method to avoid thrown exception
    else {
      ASTCDMethod removeIfNotLast = AssociationMethodFactory.createRemoveMethod(this.type, symbol);
      removeIfNotLast.setName(removeIfNotLast.getName() + "IfNotLast");

      HookPoint remHP;
      if (this.hp != null) {
        remHP = hp;
      } else {
        remHP = new StringHookPoint("{return " + CDAssociationUtil.getAssociationName(symbol) + ".size() > 1? remove" +
            TransformationUtils.capitalize(CDAssociationUtil.getAssociationName(symbol)) + "(o):false;}");
      }
      glex.replaceTemplate(CoreTemplate.EMPTY_METHOD.toString(), removeIfNotLast, remHP);

      methods.add(removeIfNotLast);
    }

    return methods;
  }

  protected List<ASTCDMethod> createCommonMany() {
    List<ASTCDMethod> methods = Lists.newArrayList();

    // create size
    ASTCDMethod size = AssociationMethodFactory.createSizeMethod(symbol);

    // create getList
    ASTCDMethod getAll = AssociationMethodFactory.createGetAllMethod(this.type, symbol);

    // handle hook points
    HookPoint sizeHp, getAllHp;

    if (this.hp != null) {
      sizeHp = getAllHp = this.hp;
    } else {
      sizeHp = new TemplateHookPoint(AssociationMethodFactory.Templates.SIZE_METHOD_BODY.valueOf(),
          this.name);

      getAllHp = new TemplateHookPoint(
          AssociationMethodFactory.Templates.GET_METHOD_BODY.valueOf(), this.name);
    }

    glex.replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(), size,
        sizeHp);

    glex.replaceTemplate(
        CoreTemplate.EMPTY_METHOD.toString(), getAll,
        getAllHp);

    methods.add(size);
    methods.add(getAll);

    // create isEmpty
    if (!CDAssociationUtil.isOneToMany(symbol)) {
      ASTCDMethod isEmpty = AssociationMethodFactory.createIsEmptyMethod(symbol);
      // handle hook points
      HookPoint isEmptyHp;

      if (this.hp != null) {
        isEmptyHp = this.hp;
      } else {
        isEmptyHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.ISEMPTY_METHOD_BODY.valueOf(), this.name);
      }

      glex.replaceTemplate(
          CoreTemplate.EMPTY_METHOD.toString(), isEmpty,
          isEmptyHp);

      methods.add(isEmpty);
    }

    return methods;
  }

  @Override
  public List<ASTCDMethod> getRawRemoveMethods() {

    List<ASTCDMethod> methods = Lists.newArrayList();

    if (CDAssociationUtil.isMultiple(symbol) || CDAssociationUtil.isOneToMany(symbol)) {
      // create remove
      ASTCDMethod rawRemove = AssociationMethodFactory.createRawRemoveMethod(this.type, symbol);

      // handle hook points
      HookPoint removeHp;

      if (this.hp != null) {
        removeHp = this.hp;
      } else {
        removeHp = new TemplateHookPoint(
            AssociationMethodFactory.Templates.RAWREMOVE_METHOD_BODY.valueOf(), this.name, "o",
            CDAssociationUtil.isOneToMany(symbol), symbol.getSourceType().getName() + "."
            + rawRemove.getName(), CDAssociationUtil.isComposition(symbol));
      }

      glex.replaceTemplate(
          CoreTemplate.EMPTY_METHOD.toString(),
          rawRemove, removeHp);

      methods.add(rawRemove);
    }
    return methods;
  }

  @Override
  public void setAddMethodCall(String callMethod) {
    this.addMethodCall = callMethod;
  }

  @Override
  public void setRemoveMethodCall(String callMethod) {
    this.removeMethodCall = callMethod;
  }
}
