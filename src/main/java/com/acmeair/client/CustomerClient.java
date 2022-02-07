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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

import com.acmeair.securityutils.SecurityUtils;

@ApplicationScoped
public class CustomerClient {

	/**
	 * Accepts environment variable override CUSTOMER_SERVICE
	 */
    @ConfigProperty(name = "customer.service", defaultValue = "localhost:6379/customer")
	String CUSTOMER_SERVICE_LOC;

	private static final String VALIDATE_PATH = "/validateid";

	@Inject
	private SecurityUtils secUtils;

	// cache this for perf reasons
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
	
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<String, Object>();

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

            headers.putSingle("acmeair-id", login);
            headers.putSingle("acmeair-date", date.toString());
            headers.putSingle("acmeair-sig-body", sigBody);
            headers.putSingle("acmeair-signature", signature);
		}

        MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
		map.add("login", login);
		map.add("password", password);

        Entity<Form> request = Entity.form(map);

		CustomerResult result = client.target(url).request()
                                                  .headers(headers)
                                                  .post(request, CustomerResult.class);
						
		return result.validCustomer;
	}
}
