import { loadArchitectureModule } from './utilities';
import { A } from './mocking';
import { JsonMember, JsonObject, TypedJSON } from '@upe/typedjson';

describe('JsonTest', () => {

  @JsonObject()
  class Test {
    @JsonMember({isRequired: true})
    public name: string;
  }

  beforeEach(loadArchitectureModule);

  it('parse', A(async () => {
    let s = TypedJSON.stringify({name: 'blub'});

    TypedJSON.parse(s, Test);
  }));

});