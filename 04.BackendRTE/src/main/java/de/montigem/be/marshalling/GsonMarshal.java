/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.marshalling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.montigem.be.command.rte.general.CommandDTO;
import de.montigem.be.domain.rte.Identifier;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.error.JsonException;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * An implementation of {@link IConcreteJsonMarshal} that uses the Google GSON API
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class GsonMarshal implements IConcreteJsonMarshal {

  protected static Gson gson;

  public static Gson getGson() {
    if (gson == null) {
      final RuntimeTypeAdapterFactory<CommandDTO> typeFactory = CommandDTOTypeAdapter
          .getInstance()
          .getFactory();

      final RuntimeTypeAdapterFactory<DTO> typeFactory2 = DTOTypeAdapter
          .getInstance()
          .getFactory();


      GsonBuilder gb = new GsonBuilder()
          .serializeNulls()
          .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
          .disableHtmlEscaping()
          .registerTypeAdapterFactory(typeFactory)
          .registerTypeAdapterFactory(typeFactory2)
          .registerTypeAdapter(Identifier.class, new IdentifierAdapter())
          .registerTypeAdapter(new TypeToken<Identifier>() {
          }.getType(), new IdentifierAdapter())
          .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
          .registerTypeAdapter(new TypeToken<ZonedDateTime>() {
          }.getType(), new ZonedDateTimeAdapter());
      gson = gb.create();
    }
    return gson;
  }

  /**
   * @see IConcreteJsonMarshal#marshal(java.lang.Object)
   */
  @Override
  public String marshal(Object o) {
    return getGson().toJson(o);
  }

  /**
   * @see IConcreteJsonMarshal#unmarshal(java.lang.String, java.lang.reflect.Type)
   */
  @Override
  public <T> T unmarshal(String s, Type type) throws JsonException {
    try {
      return getGson().fromJson(s, type);
    } catch (Exception jse) {
      throw new JsonException(jse.getMessage());
    }
  }

  /**
   * @see IConcreteJsonMarshal#unmarshal(java.lang.String, java.lang.Class)
   */
  @Override
  public <T> T unmarshal(Object s, Type type) throws JsonException {
    if (s == null) {
      throw new JsonException("object is null");
    }
    return unmarshal(s.toString(), type);
  }

  /**
   * @see IConcreteJsonMarshal#unmarshal(java.lang.String, java.lang.Class)
   */
  @Override
  public <T> T unmarshal(String s, Class<T> clazz) throws JsonException {
    try {
      return getGson().fromJson(s, clazz);
    } catch (Exception jse) {
      throw new JsonException(jse.getMessage());
    }
  }

  /**
   * @see IConcreteJsonMarshal#unmarshal(java.lang.Object, java.lang.Class)
   */
  @Override
  public <T> T unmarshal(Object s, Class<T> clazz) throws JsonException {
    if (s == null) {
      throw new JsonException("object is null");
    }
    return unmarshal(s.toString(), clazz);
  }

  /**
   * @see IConcreteJsonMarshal#unmarshalOptional(java.lang.String,
   * java.lang.reflect.Type)
   */
  @Override
  public <T> Optional<T> unmarshalOptional(String s, Type type) {
    try {
      return Optional.ofNullable(getGson().fromJson(s, type));
    } catch (Exception jse) {
      return Optional.empty();
    }
  }

  /**
   * @see IConcreteJsonMarshal#unmarshalOptional(java.lang.Object,
   * java.lang.reflect.Type)
   */
  @Override
  public <T> Optional<T> unmarshalOptional(Object s, Type type) {
    if (s == null) {
      return Optional.empty();
    }
    return unmarshalOptional(s, type);
  }

  /**
   * @see IConcreteJsonMarshal#unmarshalOptional(java.lang.String,
   * java.lang.Class)
   */
  @Override
  public <T> Optional<T> unmarshalOptional(String s, Class<T> clazz) {
    try {
      return Optional.ofNullable(getGson().fromJson(s, clazz));
    } catch (Exception jse) {
      return Optional.empty();
    }
  }

  /**
   * @see IConcreteJsonMarshal#unmarshalOptional(java.lang.Object,
   * java.lang.Class)
   */
  @Override
  public <T> Optional<T> unmarshalOptional(Object s, Class<T> clazz) {
    if (s == null) {
      return Optional.empty();
    }
    return unmarshalOptional(s.toString(), clazz);
  }
}
