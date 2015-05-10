/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.northwind;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceImporter;

/**
 * @author hhildebrand
 *
 */
public class NorthwindTest extends AbstractModelTest {

    @BeforeClass
    public static void loadOntology() throws IOException {

        em.getTransaction().begin();
        WorkspaceImporter.creatWorkspace(NorthwindTest.class.getResourceAsStream("/northwind.wsp"),
                                         model);
        em.getTransaction().commit();
    }

    @Test
    public void testNorthwind() {
        assertNotNull("foo");
    }
}
