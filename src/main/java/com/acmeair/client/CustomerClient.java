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

import com.acmeair.client.CustomerClient;
import com.acmeair.securityutils.SecurityUtils;

@Component
public class CustomerClient {

	/**
	 * Accepts environment variable override CUSTOMER_SERVICE
	 */
	@Value("${customer.service:localhost:6379/customer}")
	String CUSTOMER_SERVICE_LOC;

	private static final String VALIDATE_PATH = "/validateid";

//	@Autowired
	@Inject
	private SecurityUtils secUtils;

	// cache this for perf reasons
//	private static RestTemplate restTemplate = new RestTemplate();
//    private static Client client = ClientBuilder.newClient();
//  private static Client client = new ResteasyClientBuilderImpl().build();
    private static Client client = new ResteasyClientBuilderImpl()
//                                      .httpEngine(new VertxClientHttpEngine)
                                      .httpEngine(new URLConnectionEngine())
                                      .build();
	
	/**
	 * Calls the customer service to validate the login/password.
	 */
	public boolean validateCustomer(String login, String password) {
	 
		// RestTemplate uses SimpleClientHttpRequestFactory which uses the JDK's
		// HttpURLConnection

		// Set maxConnections - this seems to help with keepalives/running out of
		// sockets.
		/*
		 * if (System.getProperty("http.maxConnections") == null) {
		 * System.setProperty("http.maxConnections", "50"); }
		 */

		String url = "http://" + CUSTOMER_SERVICE_LOC + VALIDATE_PATH;
		String urlParameters = "login=" + login + "&password=" + password;
	
//		HttpHeaders headers = new HttpHeaders();
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<String, Object>();

//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		if (secUtils.secureServiceCalls()) {

			Date date = new Date();
			String sigBody;
			String signature;
			try {
				sigBody = secUtils.buildHash(urlParameters);
				signature = secUtils.buildHmac("POST", VALIDATE_PATH, login, date.toString(), sigBody);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			} catch (InvalidKeyException e) {
				throw new RuntimeException(e);
			}

//			headers.set("acmeair-id", login);
//			headers.set("acmeair-date", date.toString());
//			headers.set("acmeair-sig-body", sigBody);
//			headers.set("acmeair-signature", signature);
            headers.putSingle("acmeair-id", login);
            headers.putSingle("acmeair-date", date.toString());
            headers.putSingle("acmeair-sig-body", sigBody);
            headers.putSingle("acmeair-signature", signature);
		}

//		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
		map.add("login", login);
		map.add("password", password);

//		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        Entity<Form> request = Entity.form(map);

//		CustomerResult result = restTemplate.postForObject(url, request, CustomerResult.class);
		CustomerResult result = client.target(url).request()
                                                  .headers(headers)
                                                  .post(request, CustomerResult.class);
						
		return result.validCustomer;
	}
}
