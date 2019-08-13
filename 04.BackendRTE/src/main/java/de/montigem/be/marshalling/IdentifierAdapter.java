/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.marshalling;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.montigem.be.domain.rte.Identifier;

import java.io.IOException;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class IdentifierAdapter extends TypeAdapter<Identifier> {
  
  /**
   * @see com.google.gson.TypeAdapter#write(com.google.gson.stream.JsonWriter, java.lang.Object)
   */
  @Override
  public void write(JsonWriter out, Identifier value) throws IOException {
    out.beginObject();
    if (null != value) {
      out.name("id").value(value.getId());
      out.name("revision").value(value.getRevision());
    }
    out.endObject();
  }
  
  /**
   * @see com.google.gson.TypeAdapter#read(com.google.gson.stream.JsonReader)
   */
  @Override
  public Identifier read(JsonReader in) throws IOException {
    in.beginObject();
    if (in.hasNext()) {
      long id = -1;
      long revision = -1;
      if (in.nextName().equals("id")) {
        id = in.nextLong();
      }
      if (in.nextName().equals("revision")) {
        revision = in.nextLong();
      }
      in.endObject();
      return new Identifier(id, revision);
    }
    in.endObject();
    return null;
  }
  
}
