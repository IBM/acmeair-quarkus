/*******************************************************************************
* Copyright (c) 2017 IBM Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/

package com.acmeair.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.acmeair.service.CustomerService;

@Path("/config")
public class CustomerConfiguration {

	Logger logger = Logger.getLogger(CustomerConfiguration.class.getName());

	@Inject
	CustomerService customerService;

	/**
	 * Return count of customer from the db.
	 */
	@GET
	@Path("/countCustomers")
	@Produces(MediaType.APPLICATION_JSON)
	public String countCustomer() {
		try {
			String customerCount = customerService.count().toString();

			return customerCount;
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}
	}

	/**
	 * Return active db impl.
	 */
	@GET
	@Path("/activeDataService")
	@Produces(MediaType.APPLICATION_JSON)
	public String getActiveDataServiceInfo() {
		try {
			logger.fine("Get active Data Service info");
			return customerService.getServiceType();
		} catch (Exception e) {
			e.printStackTrace();
			return "Unknown";
		}
	}

	/**
	 * Return runtim info.
	 */
	@GET
	@Path("/runtime")
    @Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, String>> getRuntimeInfo() {
		List<Map<String, String>> list = new ArrayList<>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "Runtime");
		map.put("description", "Java");
		list.add(map);
		map.clear();

		map.put("name", "Version");
		map.put("description", System.getProperty("java.version"));
		list.add(map);
		map.clear();

		map.put("name", "Vendor");
		map.put("description", System.getProperty("java.vendor"));
		list.add(map);

		return list;
	}
}
