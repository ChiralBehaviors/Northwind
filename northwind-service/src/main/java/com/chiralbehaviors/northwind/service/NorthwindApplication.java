/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.northwind.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.northwind.service.config.JpaConfiguration;
import com.chiralbehaviors.northwind.service.config.NorthwindConfiguration;
import com.chiralbehaviors.northwind.service.health.EmfHealthCheck;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;

/**
 * @author hhildebrand
 *
 */

public class NorthwindApplication extends Application<NorthwindConfiguration> {

    public static void main(String[] argv) throws Exception {
        new NorthwindApplication().run(argv);
    }

    @Override
    public void initialize(Bootstrap<NorthwindConfiguration> bootstrap) {
        bootstrap.getObjectMapper().registerModule(new CoREModule());
        Hibernate4Module module = new Hibernate4Module();
        module.enable(Feature.FORCE_LAZY_LOADING);
        bootstrap.getObjectMapper().registerModule(module);
        super.initialize(bootstrap);
    }

    /* (non-Javadoc)
     * @see io.dropwizard.AbstractService#initialize(io.dropwizard.config.Configuration, io.dropwizard.config.Environment)
     */
    @Override
    public void run(NorthwindConfiguration configuration,
                    Environment environment) throws Exception {
        JpaConfiguration jpaConfig = configuration.getCrudServiceConfiguration();

        String unit = jpaConfig.getPersistenceUnit();
        Map<String, String> properties = jpaConfig.getProperties();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(unit,
                                                                          properties);
        environment.healthChecks().register("EMF Health",
                                            new EmfHealthCheck(emf));
    }
}