/*
 * Copyright 2014 Squarespace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.squarespace.jersey2.guice.resource;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;
import com.squarespace.jersey2.guice.BootstrapUtils;
import com.squarespace.jersey2.guice.utils.HttpServer;
import com.squarespace.jersey2.guice.utils.HttpServerUtils;
import org.glassfish.hk2.api.ServiceLocator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.squarespace.jersey2.guice.resource.HelloServiceImpl.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ResourceWithProviderWithoutBindingInjectionTest {

  private static HttpServer SERVER;

  @BeforeClass
  public void setUp() throws IOException {
    ServiceLocator locator = BootstrapUtils.newServiceLocator();
    List<Module> modules = new ArrayList<>();
    modules.add(new ServletModule());
    modules.add(new AbstractModule() {
      @Override
      protected void configure() {
        // no call to bind(HelloConstructorResource.class);

        bind(HelloService.class).toInstance(new HelloServiceImpl(DEFAULT_HELLO));
      }
    });

    @SuppressWarnings("unused")
    Injector injector = BootstrapUtils.newInjector(locator, modules);

    BootstrapUtils.install(locator);

    SERVER = HttpServerUtils.newHttpServer(HelloConstructorResource.class);
  }

  @AfterClass
  public void tearDown() throws IOException {
    SERVER.close();
    BootstrapUtils.reset();
  }

  private Client client;

  @AfterTest
  public void closeClient() {
    client.close();
  }

  private WebTarget getWebTarget() {
    String url = "http://localhost:" + HttpServerUtils.PORT;
    client = ClientBuilder.newClient();
    return client.target(url).path(UriBuilder.fromResource(HelloResource.class).toString());
  }

  @Test
  public void baseTest() throws IOException {
    WebTarget target = getWebTarget();
    String value = target.path("guice").request(MediaType.TEXT_PLAIN).get(String.class);
    assertNotNull(value);
    assertEquals(value, DEFAULT_HELLO);
  }

  @Test
  public void providedTest() throws IOException {
    WebTarget target = getWebTarget();
    String value = target.path("330").request(MediaType.TEXT_PLAIN).get(String.class);
    assertNotNull(value);
    assertEquals(value, DEFAULT_HELLO);
  }
}
