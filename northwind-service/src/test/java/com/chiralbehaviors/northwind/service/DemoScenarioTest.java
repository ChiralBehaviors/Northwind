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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;

import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.workspace.StateSnapshot;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.chiralbehaviors.northwind.agency.Customer;
import com.chiralbehaviors.northwind.product.ItemDetail;
import com.chiralbehaviors.northwind.product.Order;
import com.chiralbehaviors.northwind.product.PricedProduct;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class DemoScenarioTest extends AbstractModelTest {
    private static final String TARGET_CLASSES_DEMO_DATA_JSON = "target/classes/demo-data.json";

    private static final String TEST_SCENARIO_URI             = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/scenario";

    private TestScenario        testScenario;

    @Test
    public void loadScenario() throws Exception {
        try (Model myModel = new ModelImpl(newConnection())) {
            WorkspaceSnapshot.load(myModel.create(),
                                   Arrays.asList(getClass().getResource("/northwind.1.json"),
                                                 getClass().getResource("/scenario.1.json")));
            testScenario = myModel.getWorkspaceModel()
                                  .getScoped(WorkspaceAccessor.uuidOf(TEST_SCENARIO_URI))
                                  .getWorkspace()
                                  .getAccessor(TestScenario.class);
            loadState(myModel);
            WorkspaceSnapshot snap = myModel.snapshot();
            try (OutputStream os = new FileOutputStream(TARGET_CLASSES_DEMO_DATA_JSON)) {
                new ObjectMapper().registerModule(new CoREModule())
                                  .writeValue(os, snap);
            }
        }
        try (Model myModel = new ModelImpl(newConnection())) {
            StateSnapshot snapshot;
            try (InputStream os = new FileInputStream(TARGET_CLASSES_DEMO_DATA_JSON)) {
                snapshot = new ObjectMapper().registerModule(new CoREModule())
                                             .readValue(os,
                                                        StateSnapshot.class);
            }
            snapshot.load(myModel.create());
        }
    }

    private void loadState(Model model) throws InstantiationException {
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

        Order order = model.construct(Order.class, ExistentialDomain.Product,
                                      "Cafluer Bon Order", "emergency order");
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        addItem(computer, order, 2, 0, 0.07, model);
        addItem(chemB, order, 50, 0, 0.05, model);
        cafleurBon.addOrder(order);
        cafleurBon.setCustomerName("Cafleur Bon");

        order = model.construct(Order.class, ExistentialDomain.Product,
                                "GU Order", "monthly ship");
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        addItem(computer, order, 20, 0.05, 0, model);
        addItem(chemB, order, 050, 0.05, 0, model);
        gu.addOrder(order);

        order = model.construct(Order.class, ExistentialDomain.Product, "Org A",
                                "computer!  STAT!");
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        addItem(computer, order, 500, 0.10, 0, model);
        orgA.addOrder(order);
    }

    private void addItem(PricedProduct product, Order order, int quantity,
                         double discount, double taxRate,
                         Model model) throws InstantiationException {
        ItemDetail item = model.construct(ItemDetail.class,
                                          ExistentialDomain.Product, "an item",
                                          "a real item");
        item.setProduct(product);
        item.setUnitPrice(product.getUnitPrice());
        item.setDiscount(BigDecimal.valueOf(discount));
        item.setQuantity(quantity);
        item.setTaxRate(BigDecimal.valueOf(taxRate));
        order.addItemDetail(item);
    }
}
