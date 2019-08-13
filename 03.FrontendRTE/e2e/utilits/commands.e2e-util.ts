/* (c) https://github.com/MontiCore/monticore */
import 'rxjs/add/operator/concatMap';
import { E2ECommandCaller } from "./command-manager.e2e-util";
import { IDTO } from "@shared/architecture";
import { Person_create } from "../../target/generated-sources/commands/person.create";
import { PersonFullDTO } from "@freitext-dto/person.fulldto";
import { TypedJSON } from "@upe/typedjson";

export class Commands {
  static async createPerson() {
    let person = JSON.stringify({
      "nachname": "Panda",
      "vorname": "Peter",
      "personalnummer": null,
      "stellenumfang": null,
      "beschBeginn": null,
      "beschEnde": null,
      "gebDatum": null,
      "kuerzel": "PANPE",
      "labels": null,
      "anstellungsarten": []
    });

    let p: PersonFullDTO = new PersonFullDTO(TypedJSON.deserialize(person, PersonFullDTO));

    return E2ECommandCaller.commandManager
      .addCommand(new Person_create(p), (model: IDTO) => {
        return model
      })
      .sendCommands()
  }
}


