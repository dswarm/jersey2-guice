package com.squarespace.jersey2.guice.resource;

import com.google.inject.Provider;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("hello")
public final class HelloConstructorResource {

  final Provider<HelloService> guiceProvider;

  final javax.inject.Provider<HelloService> threeThirtyProvider;

  @Inject
  public HelloConstructorResource(
      Provider<HelloService> guiceProvider,
      javax.inject.Provider<HelloService> threeThirtyProvider) {

    this.guiceProvider = guiceProvider;
    this.threeThirtyProvider = threeThirtyProvider;
  }

  @GET
  @Path("guice")
  public String hello() {
    return guiceProvider.get().hello();
  }

  @GET
  @Path("330")
  public String helloNamed() {
    return threeThirtyProvider.get().hello();
  }
}
