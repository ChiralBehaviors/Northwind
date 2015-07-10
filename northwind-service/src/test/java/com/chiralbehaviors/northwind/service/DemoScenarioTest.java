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

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.northwind.Northwind;
import com.chiralbehaviors.northwind.agency.Customer;
import com.chiralbehaviors.northwind.product.ItemDetail;
import com.chiralbehaviors.northwind.product.Order;
import com.chiralbehaviors.northwind.product.PricedProduct;

/**
 * @author hhildebrand
 *
 */
public class DemoScenarioTest extends AbstractModelTest {
    private static final String TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/scenario/v1";

    @BeforeClass
    public static void loadOntology() throws IOException {

        em.getTransaction().begin();
        WorkspaceImporter.createWorkspace(DemoScenarioTest.class.getResourceAsStream("/northwind.wsp"),
                                          model);
        WorkspaceImporter.createWorkspace(DemoScenarioTest.class.getResourceAsStream("/scenario.wsp"),
                                          model);
        TestScenario testScenario = model.getWorkspaceModel().getScoped(Workspace.uuidOf(TEST_SCENARIO_URI)).getWorkspace().getAccessor(TestScenario.class);
        PricedProduct computer = model.wrap(PricedProduct.class,
                                            testScenario.getAbc486());
        PricedProduct chemB = model.wrap(PricedProduct.class,
                                         testScenario.getChemB());

        computer.setUnitPrice(BigDecimal.valueOf(2295.0));
        chemB.setUnitPrice(BigDecimal.valueOf(2396.0));
        em.getTransaction().commit();
    }

    private Northwind    scenario;
    private TestScenario testScenario;

    @Before
    public void initializeScenario() {
        scenario = model.getWorkspaceModel().getScoped(Workspace.uuidOf(NORTHWIND_WORKSPACE)).getWorkspace().getAccessor(Northwind.class);
        testScenario = model.getWorkspaceModel().getScoped(Workspace.uuidOf(TEST_SCENARIO_URI)).getWorkspace().getAccessor(TestScenario.class);
    }

    @Test
    public void loadScenario() throws Exception {
        em.getTransaction().begin();
        Customer cafleurBon = model.wrap(Customer.class,
                                         testScenario.getCafleurBon());
        Customer gu = model.wrap(Customer.class,
                                 testScenario.getGeorgetownUniversity());
        Customer orgA = model.wrap(Customer.class, testScenario.getOrgA());

        PricedProduct computer = model.wrap(PricedProduct.class,
                                            testScenario.getAbc486());
        PricedProduct chemB = model.wrap(PricedProduct.class,
                                         testScenario.getChemB());

        Order order = model.construct(Order.class, "Cafluer Bon Order",
                                      "emergency order");
        addItem(computer, order, 2, 0, 0.07);
        addItem(chemB, order, 50, 0, 0.05);
        cafleurBon.addOrder(order);

        order = model.construct(Order.class, "GU Order", "monthly ship");
        addItem(computer, order, 20, 0.05, 0);
        addItem(chemB, order, 050, 0.05, 0);
        gu.addOrder(order);

        order = model.construct(Order.class, "Org A", "computer!  STAT!");
        addItem(computer, order, 500, 0.10, 0);
        orgA.addOrder(order);

        em.getTransaction().commit();
    }

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
}
