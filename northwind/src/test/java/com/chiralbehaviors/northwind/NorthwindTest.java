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

import static com.chiralbehaviors.northwind.Northwind.NORTHWIND_WORKSPACE;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.job.Job;
import com.chiralbehaviors.CoRE.job.MetaProtocol;
import com.chiralbehaviors.CoRE.job.Protocol;
import com.chiralbehaviors.CoRE.meta.InferenceMap;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;

/**
 * @author hhildebrand
 *
 */
public class NorthwindTest extends AbstractModelTest {

    private static final String TEST_SCENARIO_URI = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/scenario";

    private JobModel            jobModel = model.getJobModel();
    private static Northwind    scenario;
    private static TestScenario testScenario;

    @BeforeClass
    public static void loadOntology() throws IOException {

        em.getTransaction()
          .begin();
        WorkspaceImporter.manifest(NorthwindTest.class.getResourceAsStream("/northwind.wsp"),
                                   model);
        WorkspaceImporter.manifest(NorthwindTest.class.getResourceAsStream("/scenario.wsp"),
                                   model);
        scenario = model.getWorkspaceModel()
                        .getScoped(WorkspaceAccessor.uuidOf(NORTHWIND_WORKSPACE))
                        .getWorkspace()
                        .getAccessor(Northwind.class);
        testScenario = model.getWorkspaceModel()
                            .getScoped(WorkspaceAccessor.uuidOf(TEST_SCENARIO_URI))
                            .getWorkspace()
                            .getAccessor(TestScenario.class);
        em.getTransaction()
          .commit();
    }

    @Test
    public void testEuOrder() throws Exception {
        Job order = model.getJobModel()
                         .newInitializedJob(scenario.getDeliver(),
                                            kernel.getCore());
        order.setAssignTo(testScenario.getOrderFullfillment());
        order.setProduct(testScenario.getAbc486());
        order.setDeliverTo(testScenario.getRc31());
        order.setDeliverFrom(testScenario.getFactory1());
        order.setRequester(testScenario.getCafleurBon());
        em.persist(order);
        em.flush();
        jobModel.changeStatus(order, scenario.getAvailable(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(order, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<Job> jobs = jobModel.getAllChildren(order);
        assertEquals(6, jobs.size());
    }

    @Test
    public void testNonExemptOrder() throws Exception {
        Job order = model.getJobModel()
                         .newInitializedJob(scenario.getDeliver(),
                                            kernel.getCore());
        order.setAssignTo(testScenario.getOrderFullfillment());
        order.setProduct(testScenario.getAbc486());
        order.setDeliverTo(testScenario.getBht37());
        order.setDeliverFrom(testScenario.getFactory1());
        order.setRequester(testScenario.getOrgA());
        em.persist(order);
        em.flush();
        jobModel.changeStatus(order, scenario.getAvailable(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(order, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<Job> jobs = jobModel.getAllChildren(order);
        assertEquals(6, jobs.size());
    }

    @Test
    public void testOrder() throws Exception {
        Job order = model.getJobModel()
                         .newInitializedJob(scenario.getDeliver(),
                                            kernel.getCore());
        order.setAssignTo(testScenario.getOrderFullfillment());
        order.setProduct(testScenario.getAbc486());
        order.setDeliverTo(testScenario.getRsb225());
        order.setDeliverFrom(testScenario.getFactory1());
        order.setRequester(testScenario.getGeorgetownUniversity());
        em.persist(order);
        em.flush();
        jobModel.changeStatus(order, scenario.getAvailable(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(order, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<Job> jobs = jobModel.getAllChildren(order);
        assertEquals(5, jobs.size());

        TypedQuery<Job> query = em.createQuery("select j from Job j where j.service = :service",
                                               Job.class);
        query.setParameter("service", scenario.getCheckCredit());
        Job creditCheck = query.getSingleResult();
        assertEquals(scenario.getAvailable(), creditCheck.getStatus());
        jobModel.changeStatus(creditCheck, scenario.getActive(),
                              kernel.getCore(), "transition during test");
        em.flush();
        jobModel.changeStatus(creditCheck, scenario.getCompleted(),
                              kernel.getCore(), "transition during test");
        em.flush();
        query.setParameter("service", scenario.getPick());
        Job pick = query.getSingleResult();
        assertEquals(scenario.getAvailable(), pick.getStatus());
        jobModel.changeStatus(pick, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(pick, scenario.getCompleted(), kernel.getCore(),
                              "transition during test");
        em.flush();
        query.setParameter("service", scenario.getPick());
        pick = query.getSingleResult();
        query.setParameter("service", scenario.getShip());
        Job ship = query.getSingleResult();
        List<Job> pickSiblings = jobModel.getActiveSubJobsForService(pick.getParent(),
                                                                     scenario.getShip());
        assertEquals(1, pickSiblings.size());
        assertEquals(scenario.getWaitingOnPurchaseOrder(), ship.getStatus());
        query.setParameter("service", scenario.getFee());
        Job fee = query.getSingleResult();
        jobModel.changeStatus(fee, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(fee, scenario.getCompleted(), kernel.getCore(),
                              "transition during test");
        em.flush();
        query.setParameter("service", scenario.getPrintPurchaseOrder());
        Job printPO = query.getSingleResult();
        assertEquals(scenario.getAvailable(), printPO.getStatus());
        jobModel.changeStatus(printPO, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(printPO, scenario.getCompleted(),
                              kernel.getCore(), "transition during test");
        em.flush();
        query.setParameter("service", scenario.getShip());
        ship = query.getSingleResult();
        assertEquals(scenario.getAvailable(), ship.getStatus());
        jobModel.changeStatus(ship, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(ship, scenario.getCompleted(), kernel.getCore(),
                              "transition during test");
        em.flush();
        query.setParameter("service", scenario.getDeliver());
        Job deliver = query.getSingleResult();
        assertEquals(scenario.getCompleted(), deliver.getStatus());
    }

}
