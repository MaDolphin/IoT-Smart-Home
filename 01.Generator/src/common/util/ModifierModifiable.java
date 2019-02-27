/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package common.util;

public interface ModifierModifiable<T> {

  public T Public();

  public T Private();

  public T Protected();

  public T Static();

  public T Final();

  public T Package();

  public T Abstract();
}
