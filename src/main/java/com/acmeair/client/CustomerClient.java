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

package com.acmeair.client;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
//import org.jboss.resteasy.client.jaxrs.vertx.VertxClientHttpEngine;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;

import com.acmeair.securityutils.SecurityUtils;

@Component
public class CustomerClient {
    
//    private static RestTemplate restTemplate = new RestTemplate();
//    private static Client client = ClientBuilder.newClient();
//    private static Client client = new ResteasyClientBuilderImpl().build();
    private static Client client = new ResteasyClientBuilderImpl()
//                                        .httpEngine(new VertxClientHttpEngine)
                                        .httpEngine(new URLConnectionEngine())
                                        .build();
    private static final String UPDATE_REWARD_PATH = "/updateCustomerTotalMiles";
    
    /**
     * Accepts environment variable override CUSTOMER_SERVICE
     */
    @Value("${customer.service:localhost:6379/customer}")
    protected String CUSTOMER_SERVICE_LOC;
  
//    @Autowired
    @Inject
    private SecurityUtils secUtils;   

	/**
	 * call customer to update reward miles.
	 */
	public Long updateTotalMiles(String customerId, String miles) {
		String customerUrl = "http://" + CUSTOMER_SERVICE_LOC + UPDATE_REWARD_PATH;
		String customerParameters = "miles=" + miles + "&customerid=" + customerId;
		
//		HttpHeaders headers = new HttpHeaders();
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap();

//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		// //headers.setAccept(java.util.Arrays.asList(MediaType.APPLICATION_JSON));

		if (secUtils.secureServiceCalls()) {

			Date date = new Date();
			String sigBody;
			String signature;
			try {
				sigBody = secUtils.buildHash(customerParameters);
				signature = secUtils.buildHmac("POST", UPDATE_REWARD_PATH, customerId, date.toString(), sigBody);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			} catch (InvalidKeyException e) {
				throw new RuntimeException(e);
			}

//			headers.set("acmeair-id", customerId);
//			headers.set("acmeair-date", date.toString());
//			headers.set("acmeair-sig-body", sigBody);
//			headers.set("acmeair-signature", signature);
			headers.putSingle("acmeair-id", customerId);
			headers.putSingle("acmeair-date", date.toString());
			headers.putSingle("acmeair-sig-body", sigBody);
			headers.putSingle("acmeair-signature", signature);
		}

//		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
		map.add("miles", miles);
		map.add("customerid", customerId);

//		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		Entity<Form> request = Entity.form(map);

//		UpdateTotalMilesResult result = restTemplate.postForObject(customerUrl, request, UpdateTotalMilesResult.class);
		UpdateTotalMilesResult result = client.target(customerUrl)
		                                      .request()
											  .headers(headers)
											  .post(request, UpdateTotalMilesResult.class);
		return result.total_miles;
	}
}
