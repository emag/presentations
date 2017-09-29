package shibuyajava.artemis;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ListenerInfo;
import org.apache.activemq.artemis.rest.integration.ActiveMQBootstrapListener;
import org.apache.activemq.artemis.rest.integration.RestMessagingBootstrapListener;
import org.jboss.resteasy.plugins.server.servlet.FilterDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;

import javax.servlet.DispatcherType;

public class ArtemisRestServer {

  private static final String REST_MESSAGING_FILTER_NAME = "Rest-Messaging";

  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_PORT = "8080";
  private static final String DEFAULT_CONTEXT_PATH = "/";

  public static void main(String[] args) throws Exception {
    final String host = System.getProperty("host", DEFAULT_HOST);
    final int port = Integer.parseInt(System.getProperty("port", DEFAULT_PORT));
    final String contextPath = System.getProperty("context.path", DEFAULT_CONTEXT_PATH);

    DeploymentInfo deploymentInfo = Servlets.deployment()
      .setClassLoader(ArtemisRestServer.class.getClassLoader())
      .setContextPath(contextPath)
      .setDeploymentName("artemis-rest-server.war")
      .addFilter(new FilterInfo(REST_MESSAGING_FILTER_NAME, FilterDispatcher.class))
      .addFilterUrlMapping(REST_MESSAGING_FILTER_NAME, "*", DispatcherType.REQUEST)
      .addListeners(
        new ListenerInfo(ResteasyBootstrap.class),
        new ListenerInfo(ActiveMQBootstrapListener.class),
        new ListenerInfo(RestMessagingBootstrapListener.class));

    DeploymentManager manager = Servlets.defaultContainer().addDeployment(deploymentInfo);
    manager.deploy();

    HttpHandler servletHandler = manager.start();
    PathHandler path = Handlers.path(Handlers.redirect(contextPath))
      .addPrefixPath(contextPath, servletHandler);

    Undertow server = Undertow.builder()
      .addHttpListener(port, host)
      .setHandler(path)
      .build();

    server.start();
  }

}
