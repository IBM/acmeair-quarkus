/*******************************************************************************
* Copyright (c) 2019 IBM Corp.
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
package com.acmeair.securityutils;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.acmeair.web.AuthServiceRest;

@Provider
public class CookieInterceptor implements ContainerResponseFilter {
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {

      if(!request.getUriInfo().getPath().equals("/login")) {
          return;
      }
      if(response.getStatus() != 200) {
          return;
      }

      @SuppressWarnings("unchecked")
      Map<String, String> modelMap = (Map<String, String>)response.getEntity();
      String token = (String) modelMap.getOrDefault("token", "");
      String login = (String) modelMap.getOrDefault("login", "");
      
      MultivaluedMap<String, Object> resHeaders = response.getHeaders();
      resHeaders.add("Set-Cookie", AuthServiceRest.JWT_COOKIE_NAME + "=" + token + "; Path=/");
      resHeaders.add("Set-Cookie", AuthServiceRest.USER_COOKIE_NAME + "=" + login + "; Path=/");

      // Rewrite response entity here
      response.setEntity("logged in", null, MediaType.TEXT_PLAIN_TYPE);
	}
}
