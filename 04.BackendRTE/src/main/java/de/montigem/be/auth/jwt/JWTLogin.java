package de.montigem.be.auth.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JWTLogin {

  private String username;

  private String password;

  private String resource;

  public JWTLogin(@JsonProperty("username") String username,
      @JsonProperty("password") String password, @JsonProperty("resource") String resource) {
    this.username = username;
    this.password = password;
    this.resource = resource;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getResource() {
    return resource;
  }
}
