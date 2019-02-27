/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { ComponentFactory, ComponentFactoryResolver, ComponentRef, Injectable, Type, ViewContainerRef, } from '@angular/core';

@Injectable()
export class ComponentInjectorService {

  constructor(private resolver: ComponentFactoryResolver) {
  }

  public create<C>(
    container: ViewContainerRef,
    componentConstructor: Type<C>,
    clear: boolean = true
  ): ComponentRef<C> {
    if (clear) {
      container.clear();
    }
    const factory: ComponentFactory<C> = this.resolver.resolveComponentFactory(componentConstructor);
    const componentRef: ComponentRef<C> = factory.create(container.parentInjector);

    return componentRef;
  }

  public loadComponent<C>(container: ViewContainerRef, componentConstructor: Type<C>): ComponentRef<C> {

    let componentFactory = this.resolver.resolveComponentFactory(componentConstructor);

    let viewContainerRef = container;
    viewContainerRef.clear();

    let componentRef = viewContainerRef.createComponent(componentFactory);

    return componentRef;
  }

}
