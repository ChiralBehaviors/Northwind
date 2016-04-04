/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.northwind;

import static com.chiralbehaviors.CoRE.jooq.Tables.JOB;
import static com.chiralbehaviors.northwind.Northwind.NORTHWIND_WORKSPACE;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.meta.InferenceMap;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * @author hhildebrand
 *
 */
public class NorthwindTest extends AbstractModelTest {
    private JobModel     jobModel;
    private Northwind    scenario;
    private final String TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/scenario";
    private TestScenario testScenario;

    @Before
    public void initializeScenario() throws Exception {
        WorkspaceSnapshot.load(model.create(),
                               Arrays.asList(getClass().getResource("/northwind.1.json"),
                                             getClass().getResource("/scenario.1.json")));
        jobModel = model.getJobModel();
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
    public void testEuOrder() throws Exception {
        JobRecord order = model.getJobModel()
                               .newInitializedJob(scenario.getDeliver());
        order.setAssignTo(testScenario.getOrderFullfillment()
                                      .getId());
        order.setProduct(testScenario.getAbc486()
                                     .getId());
        order.setDeliverTo(testScenario.getRc31()
                                       .getId());
        order.setDeliverFrom(testScenario.getFactory1()
                                         .getId());
        order.setRequester(testScenario.getCafleurBon()
                                       .getId());
        order.update();
        List<MetaProtocolRecord> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<ProtocolRecord, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        model.flush();
        jobModel.changeStatus(order, scenario.getAvailable(),
                              "transition during test");
        model.flush();
        jobModel.changeStatus(order, scenario.getActive(),
                              "transition during test");
        model.flush();
        List<JobRecord> jobs = jobModel.getAllChildren(order);
        assertEquals(6, jobs.size());
    }

    @Test
    public void testNonExemptOrder() throws Exception {
        JobRecord order = model.getJobModel()
                               .newInitializedJob(scenario.getDeliver());
        order.setAssignTo(testScenario.getOrderFullfillment()
                                      .getId());
        order.setProduct(testScenario.getAbc486()
                                     .getId());
        order.setDeliverTo(testScenario.getBht37()
                                       .getId());
        order.setDeliverFrom(testScenario.getFactory1()
                                         .getId());
        order.setRequester(testScenario.getOrgA()
                                       .getId());
        order.update();
        List<MetaProtocolRecord> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<ProtocolRecord, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        model.flush();
        jobModel.changeStatus(order, scenario.getAvailable(),
                              "transition during test");
        model.flush();
        jobModel.changeStatus(order, scenario.getActive(),
                              "transition during test");
        model.flush();
        List<JobRecord> jobs = jobModel.getAllChildren(order);
        assertEquals(6, jobs.size());
    }

    @Test
    public void testOrder() throws Exception {
        JobRecord order = model.getJobModel()
                               .newInitializedJob(scenario.getDeliver());
        order.setAssignTo(testScenario.getOrderFullfillment()
                                      .getId());
        order.setProduct(testScenario.getAbc486()
                                     .getId());
        order.setDeliverTo(testScenario.getRsb225()
                                       .getId());
        order.setDeliverFrom(testScenario.getFactory1()
                                         .getId());
        order.setRequester(testScenario.getGeorgetownUniversity()
                                       .getId());
        order.update();
        List<MetaProtocolRecord> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<ProtocolRecord, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        model.flush();
        jobModel.changeStatus(order, scenario.getAvailable(),
                              "transition during test");
        model.flush();
        jobModel.changeStatus(order, scenario.getActive(),
                              "transition during test");
        model.flush();
        List<JobRecord> jobs = jobModel.getAllChildren(order);
        assertEquals(5, jobs.size());

        JobRecord creditCheck = model.create()
                                     .selectFrom(JOB)
                                     .where(JOB.SERVICE.equal(scenario.getCheckCredit()
                                                                      .getId()))
                                     .fetchOne();
        assertEquals(scenario.getAvailable()
                             .getId(),
                     creditCheck.getStatus());
        jobModel.changeStatus(creditCheck, scenario.getActive(),
                              "transition during test");
        model.flush();
        jobModel.changeStatus(creditCheck, scenario.getCompleted(),
                              "transition during test");
        model.flush();
        JobRecord pick = model.create()
                              .selectFrom(JOB)
                              .where(JOB.SERVICE.equal(scenario.getPick()
                                                               .getId()))
                              .fetchOne();
        assertEquals(scenario.getAvailable()
                             .getId(),
                     pick.getStatus());
        jobModel.changeStatus(pick, scenario.getActive(),
                              "transition during test");
        model.flush();
        jobModel.changeStatus(pick, scenario.getCompleted(),
                              "transition during test");
        model.flush();
        pick = model.create()
                    .selectFrom(JOB)
                    .where(JOB.SERVICE.equal(scenario.getPick()
                                                     .getId()))
                    .fetchOne();
        JobRecord ship = model.create()
                              .selectFrom(JOB)
                              .where(JOB.SERVICE.equal(scenario.getShip()
                                                               .getId()))
                              .fetchOne();
        List<JobRecord> pickSiblings = jobModel.getActiveSubJobsForService(model.records()
                                                                                .resolveJob(pick.getParent()),
                                                                           scenario.getShip());
        assertEquals(1, pickSiblings.size());
        assertEquals(scenario.getWaitingOnPurchaseOrder()
                             .getId(),
                     ship.getStatus());
        JobRecord fee = model.create()
                             .selectFrom(JOB)
                             .where(JOB.SERVICE.equal(scenario.getFee()
                                                              .getId()))
                             .fetchOne();
        jobModel.changeStatus(fee, scenario.getActive(),
                              "transition during test");
        model.flush();
        jobModel.changeStatus(fee, scenario.getCompleted(),
                              "transition during test");
        model.flush();
        JobRecord printPO = model.create()
                                 .selectFrom(JOB)
                                 .where(JOB.SERVICE.equal(scenario.getPrintPurchaseOrder()
                                                                  .getId()))
                                 .fetchOne();
        assertEquals(scenario.getAvailable()
                             .getId(),
                     printPO.getStatus());
        jobModel.changeStatus(printPO, scenario.getActive(),
                              "transition during test");
        model.flush();
        jobModel.changeStatus(printPO, scenario.getCompleted(),
                              "transition during test");
        model.flush();
        ship = model.create()
                    .selectFrom(JOB)
                    .where(JOB.SERVICE.equal(scenario.getShip()
                                                     .getId()))
                    .fetchOne();
        assertEquals(scenario.getAvailable()
                             .getId(),
                     ship.getStatus());
        jobModel.changeStatus(ship, scenario.getActive(),
                              "transition during test");
        model.flush();
        jobModel.changeStatus(ship, scenario.getCompleted(),
                              "transition during test");
        model.flush();
        JobRecord deliver = model.create()
                                 .selectFrom(JOB)
                                 .where(JOB.SERVICE.equal(scenario.getDeliver()
                                                                  .getId()))
                                 .fetchOne();
        assertEquals(scenario.getCompleted(), deliver.getStatus());
    }

}
