/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package de.montigem.be.marshalling;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TODO: Write me!
 *
 *
 * @author  (last commit) $Author$
 * @version $Date$<br>
 *          $Revision$
 */
public class ZonedDateTimeAdapter extends TypeAdapter<ZonedDateTime> {

  /**
   * @see com.google.gson.TypeAdapter#write(com.google.gson.stream.JsonWriter, java.lang.Object)
   */
  @Override
  public void write(JsonWriter out, ZonedDateTime value) throws IOException {
    if(value == null) {
      out.nullValue();
    }
    else {
      out.value(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
    }
  }

  /**
   * @see com.google.gson.TypeAdapter#read(com.google.gson.stream.JsonReader)
   */
  @Override
  public ZonedDateTime read(JsonReader in) throws IOException {
    if(in.hasNext()) {
      try {
        String res = in.nextString();
        if (!res.isEmpty()) {
          return ZonedDateTime.parse(res, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX['['VV']']"));
        }
      }
      catch(IllegalStateException e) {
        in.nextNull();
        return null;
      }
    }
    return null;
  }
}

