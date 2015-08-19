/** 
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.chiralbehaviors.northwind.service;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.server.Server;

import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.phantasm.resources.FacetResource;
import com.chiralbehaviors.CoRE.phantasm.resources.GraphQlResource;
import com.chiralbehaviors.CoRE.phantasm.resources.RuleformResource;
import com.chiralbehaviors.CoRE.phantasm.resources.WorkspaceMediatedResource;
import com.chiralbehaviors.CoRE.phantasm.resources.WorkspaceResource;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Application server for REST API for Northwind
 * 
 * @author hhildebrand
 * 
 */
public class NorthwindApplication
        extends Application<NorthwindApplicationConfiguration> {

    public static void main(String[] argv) throws Exception {
        new NorthwindApplication().run(argv);
    }

    private Environment          environment;
    private Server               jettyServer;
    private EntityManagerFactory emf;

    public NorthwindApplication() {
    }

    public int getPort() {
        return ((AbstractNetworkConnector) environment.getApplicationContext()
                                                      .getServer()
                                                      .getConnectors()[0]).getLocalPort();
    }

    /* (non-Javadoc)
     * @see io.dropwizard.AbstractService#initialize(io.dropwizard.config.Configuration, io.dropwizard.config.Environment)
     */
    @Override
    public void run(NorthwindApplicationConfiguration configuration,
                    Environment environment) throws Exception {
        if (configuration.isRandomPort()) {
            ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getApplicationConnectors()
                                                                                             .get(0)).setPort(0);
            ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getAdminConnectors()
                                                                                             .get(0)).setPort(0);
        }
        this.environment = environment;
        environment.lifecycle()
                   .addServerLifecycleListener(server -> jettyServer = server);
        JpaConfiguration jpaConfig = configuration.getCrudServiceConfiguration();

        String unit = jpaConfig.getPersistenceUnit();
        Map<String, String> properties = jpaConfig.getProperties();
        emf = Persistence.createEntityManagerFactory(unit, properties);
        environment.jersey()
                   .register(new FacetResource(emf));
        environment.jersey()
                   .register(new WorkspaceResource(emf));
        environment.jersey()
                   .register(new RuleformResource(emf));
        environment.jersey()
                   .register(new WorkspaceMediatedResource(emf));
        environment.jersey()
                   .register(new GraphQlResource(emf));
        environment.healthChecks()
                   .register("EMF Health", new EmfHealthCheck(emf));
    }

    public void stop() {
        emf.close();
        if (jettyServer != null) {
            try {
                jettyServer.setStopTimeout(100);
                jettyServer.stop();
            } catch (Throwable e) {
                // ignore
            }
        }
    }

    @Override
    public void initialize(Bootstrap<NorthwindApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/angular", "/angular", null,
                                             "phantasm"));
        bootstrap.addBundle(new AssetsBundle("/ui", "/ui", null, "northwind"));
        ObjectMapper objMapper = bootstrap.getObjectMapper();
        objMapper.registerModule(new CoREModule());
        bootstrap.addBundle(new AssetsBundle("/favicon.ico"));
    }
}
