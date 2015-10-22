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

import static com.chiralbehaviors.northwind.Northwind.NORTHWIND_WORKSPACE;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.models.ModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.chiralbehaviors.northwind.Northwind;
import com.chiralbehaviors.northwind.agency.Customer;
import com.chiralbehaviors.northwind.product.ItemDetail;
import com.chiralbehaviors.northwind.product.Order;
import com.chiralbehaviors.northwind.product.PricedProduct;

/**
 * @author hhildebrand
 *
 */
public class DemoScenarioTest {
    private static final String           TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/scenario";

    protected static EntityManager        em;
    protected static EntityManagerFactory emf;
    protected static Kernel               kernel;
    protected static Model                model;

    @AfterClass
    public static void closeEntityManager() {
        if (em != null) {
            if (em.getTransaction()
                  .isActive()) {
                try {
                    em.getTransaction()
                      .rollback();
                    em.close();
                } catch (Throwable e) {
                    LoggerFactory.getLogger(AbstractModelTest.class)
                                 .warn(String.format("Had a bit of trouble cleaning up after %s",
                                                     e.getMessage()),
                                       e);
                }
            }
        }
    }

    @BeforeClass
    public static void createEMF() throws IOException, SQLException {
        if (em != null) {
            if (em.isOpen()) {
                em.close();
            }
        }
        if (emf == null) {
            InputStream is = ModelTest.class.getResourceAsStream("/jpa.properties");
            assertNotNull("jpa properties missing", is);
            Properties properties = new Properties();
            properties.load(is);
            System.out.println(String.format("Database URL: %s",
                                             properties.getProperty("javax.persistence.jdbc.url")));
            emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                         properties);
        }
        model = new ModelImpl(emf);
        kernel = model.getKernel();
        em = model.getEntityManager();
    }

    @SuppressWarnings("unused")
    private Northwind    scenario;
    private TestScenario testScenario;

    public void addItem(PricedProduct product, Order order, int quantity,
                        double discount,
                        double taxRate) throws InstantiationException {
        ItemDetail item = model.construct(ItemDetail.class, "an item",
                                          "a real item");
        item.setProduct(product);
        item.setUnitPrice(product.getUnitPrice());
        item.setDiscount(BigDecimal.valueOf(discount));
        item.setQuantity(quantity);
        item.setTaxRate(BigDecimal.valueOf(taxRate));
        order.addItemDetail(item);
    }

    @Before
    public void initializeScenario() throws Exception {
        em.getTransaction()
          .begin();
        WorkspaceSnapshot.load(em,
                               Arrays.asList(getClass().getResource("/northwind.1.json"),
                                             getClass().getResource("/scenario.1.json")));
        scenario = model.getWorkspaceModel()
                        .getScoped(WorkspaceAccessor.uuidOf(NORTHWIND_WORKSPACE))
                        .getWorkspace()
                        .getAccessor(Northwind.class);
        testScenario = model.getWorkspaceModel()
                            .getScoped(WorkspaceAccessor.uuidOf(TEST_SCENARIO_URI))
                            .getWorkspace()
                            .getAccessor(TestScenario.class);
    }

    @Test
    public void loadScenario() throws Exception {
        Customer cafleurBon = model.wrap(Customer.class,
                                         testScenario.getCafleurBon());
        Customer gu = model.wrap(Customer.class,
                                 testScenario.getGeorgetownUniversity());
        gu.setCustomerName("George Washington University");
        Customer orgA = model.wrap(Customer.class, testScenario.getOrgA());
        orgA.setCustomerName("Der Org A");

        PricedProduct computer = model.wrap(PricedProduct.class,
                                            testScenario.getAbc486());
        computer.setUnitPrice(BigDecimal.valueOf(1250.10));
        PricedProduct chemB = model.wrap(PricedProduct.class,
                                         testScenario.getChemB());
        chemB.setUnitPrice(BigDecimal.valueOf(10.25));

        Order order = model.construct(Order.class, "Cafluer Bon Order",
                                      "emergency order");
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        addItem(computer, order, 2, 0, 0.07);
        addItem(chemB, order, 50, 0, 0.05);
        cafleurBon.addOrder(order);
        cafleurBon.setCustomerName("Cafleur Bon");

        order = model.construct(Order.class, "GU Order", "monthly ship");
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        addItem(computer, order, 20, 0.05, 0);
        addItem(chemB, order, 050, 0.05, 0);
        gu.addOrder(order);

        order = model.construct(Order.class, "Org A", "computer!  STAT!");
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        addItem(computer, order, 500, 0.10, 0);
        orgA.addOrder(order);

        em.getTransaction()
          .commit();
    }

    @After
    public void rollback() {
        if (em.getTransaction()
              .isActive()) {
            em.getTransaction()
              .rollback();
        }
    }
}
