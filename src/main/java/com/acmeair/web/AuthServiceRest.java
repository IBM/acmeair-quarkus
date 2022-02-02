/*******************************************************************************
* Copyright (c) 2016 IBM Corp.
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

package com.acmeair.web;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.acmeair.client.CustomerClient;
import com.acmeair.securityutils.ForbiddenException;
import com.acmeair.securityutils.SecurityUtils;

//@RestController
//@RequestMapping("/")
@Path("/")
public class AuthServiceRest {

	private static final Logger logger = Logger.getLogger(AuthServiceRest.class.getName());

	public static final String JWT_COOKIE_NAME = "jwt_token";
	public static final String USER_COOKIE_NAME = "loggedinuser";

	

//	@Autowired
	@Inject
	private CustomerClient customerClient;

//	@Autowired
	@Inject
	private SecurityUtils secUtils;

	/**
	 * Login with username/password.
	 * 
	 */
//	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//  public ModelAndView login(@RequestParam("login") String login, @RequestParam("password") String password) {
    @POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
//    public ModelAndView login(@FormParam("login") String login, @FormParam("password") String password) {
    public Map<String, String> login(@FormParam("login") String login, @FormParam("password") String password) {
		// Test: curl -d 'login=user1' -d 'password=letmein' http://localhost:8080/login
		try {
			
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("attempting to login : login " + login + " password " + password);
			}

			if (!validateCustomer(login, password)) {
				throw new ForbiddenException("Invalid username or password for " + login);
			}
			
			// Generate simple JWT with login as the Subject
			String token = "";
			if (secUtils.secureUserCalls()) {
				token = secUtils.generateJwt(login);
			}
			
			// We need to pass the login and token to the CookieInterceptor so that it can
			// set response headers:
			Map<String, String> model = new HashMap<>();
			model.put("token", token);
			model.put("login", login);
									
//			return new ModelAndView(new View() {
//
//				@Override
//				public String getContentType() {
//					return "text/plain";
//				}
//
//				@Override
//				public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
//						throws Exception {
//					response.getWriter().print("logged in");
//				}
//			}, model);
			return model;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ForbiddenException("Error: " + e.getLocalizedMessage());
		}
	}

//	@RequestMapping("/")
    @Path("/")
	public String checkStatus() {
		return "OK";
	}

	private boolean validateCustomer(String login, String password) {
		return customerClient.validateCustomer(login, password);
	}
}
