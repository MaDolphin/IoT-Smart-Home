import { ElementType } from '@upe/typedjson/dist/untils';
import { IDTO } from '@shared/architecture';
import { DTOTypeResolver } from '@targetrte/dtotype.resolver';

export class DTOTypeResolverCustom {
  private static initialized: boolean = false;

  public static resolve(
    typeName: string | undefined
  ): ElementType<IDTO> {
    if (!this.initialized) {
      this.init();
    }
    return DTOTypeResolver.resolve(typeName);
  }

  public static init() {

    this.initialized = true;
  }
}