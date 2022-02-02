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

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpFilter;

import java.io.IOException;
import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.acmeair.web.AuthServiceRest;

//public class CookieInterceptor extends HandlerInterceptorAdapter {
@Provider
public class CookieInterceptor implements ContainerResponseFilter {
	@Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//            ModelAndView modelAndView) throws Exception {
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
//		super.postHandle(request, response, handler, modelAndView);

        if(!request.getUriInfo().getPath().equals("/login")) {
            return;
        }

//        ModelAndView modelAndView = (ModelAndView)response.getEntity();
//		ModelMap modelMap = modelAndView.getModelMap();
        @SuppressWarnings("unchecked")
        Map<String, String> modelMap = (Map<String, String>)response.getEntity();
		String token = (String) modelMap.getOrDefault("token", "");
		String login = (String) modelMap.getOrDefault("login", "");

//      response.addHeader("Set-Cookie", AuthServiceRest.JWT_COOKIE_NAME + "=" + token + "; Path=/");
//      response.addHeader("Set-Cookie", AuthServiceRest.USER_COOKIE_NAME + "=" + login + "; Path=/");
      MultivaluedMap<String, Object> resHeaders = response.getHeaders();
      resHeaders.add("Set-Cookie", AuthServiceRest.JWT_COOKIE_NAME + "=" + token + "; Path=/");
      resHeaders.add("Set-Cookie", AuthServiceRest.USER_COOKIE_NAME + "=" + login + "; Path=/");

      // Rewrite response entity here as the View.render() of the ModelAndView instance returned from AuthServiceRest
      // would do because Quarkus'es dispatcher does not invoke render()
      response.setEntity("logged in", null, MediaType.TEXT_PLAIN_TYPE);
	}
}
