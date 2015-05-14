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

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public interface Northwind {

    static String NORTHWIND_WORKSPACE = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/northwind/v1";

    StatusCode getAbandoned();

    Product getAbc486();

    StatusCode getActive();

    StatusCode getAvailable();

    Location getBht37();

    Agency getBillingComputer();

    Agency getCafleurBon();

    Product getCheckCredit();

    StatusCode getCompleted();

    Product getDeliver();

    Location getFactory1();

    Agency getFactory1Agency();

    Product getFee();

    Agency getGeorgetownUniversity();

    Agency getOrderFullfillment();

    Agency getOrgA();

    Product getPick();

    Product getPrintPurchaseOrder();

    Location getRc31();

    Location getRsb225();

    Product getShip();

    StatusCode getWaitingOnPurchaseOrder();

}
