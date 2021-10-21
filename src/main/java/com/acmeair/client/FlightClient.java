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
public class FlightClient {
    
//    private static RestTemplate restTemplate = new RestTemplate();
//    private static Client client = ClientBuilder.newClient();
//  private static Client client = new ResteasyClientBuilderImpl().build();
  private static Client client = new ResteasyClientBuilderImpl()
//                                      .httpEngine(new VertxClientHttpEngine)
                                      .httpEngine(new URLConnectionEngine())
                                      .build();
    private static final String GET_REWARD_PATH = "/getrewardmiles";

    @Value("${flight.service:localhost:6379/customer}")
    protected String FLIGHT_SERVICE_LOC;
    
//    @Autowired
    @Inject
    private SecurityUtils secUtils; 
    
	/**
	 * See com.acmeair.client.FlightClient#getRewardMiles(java.lang.String,
	 * java.lang.String, boolean)
	 */
	public Long getRewardMiles(String customerId, String flightSegId, boolean add) {


		String flightUrl = "http://" + FLIGHT_SERVICE_LOC + GET_REWARD_PATH;
		String flightParameters = "flightSegment=" + flightSegId;
		
//		HttpHeaders headers = new HttpHeaders();
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap();

//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		if (secUtils.secureServiceCalls()) {

			Date date = new Date();
			String sigBody;
			String signature;
			try {
				sigBody = secUtils.buildHash(flightParameters);
				signature = secUtils.buildHmac("POST", GET_REWARD_PATH, customerId, date.toString(), sigBody);
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
		map.add("flightSegment", flightSegId);

//		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		Entity<Form> request = Entity.form(map);

//		FlightServiceGetRewardsResult result = restTemplate.postForObject(flightUrl, request,
//				FlightServiceGetRewardsResult.class);
		FlightServiceGetRewardsResult result = client.target(flightUrl)
		                                             .request()
													 .headers(headers)
													 .post(request, FlightServiceGetRewardsResult.class);

		Long miles = result.miles;
		
		if (!add) {
			miles = miles * -1;
		}

		return miles;
	}
}
