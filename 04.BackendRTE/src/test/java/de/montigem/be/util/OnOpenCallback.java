/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.util;

@FunctionalInterface
public interface OnOpenCallback<S> {
  void onOpen(S session);
}
