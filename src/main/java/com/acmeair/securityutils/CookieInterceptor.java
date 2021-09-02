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

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

//import org.springframework.ui.ModelMap;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedMap;

import com.acmeair.web.AuthServiceRest;

//public class CookieInterceptor extends HandlerInterceptorAdapter {
public class CookieInterceptor implements ContainerResponseFilter {
	@Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//            ModelAndView modelAndView) throws Exception {
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
//		super.postHandle(request, response, handler, modelAndView);
		
//		ModelMap modelMap = modelAndView.getModelMap();
//		String token = (String) modelMap.getOrDefault("token", "");
//		String login = (String) modelMap.getOrDefault("login", "");
        String token = (String) request.getProperty("token");
        String login = (String) request.getProperty("login");
        if(token == null)  token = "";
        if(login == null)  login = "";

        MultivaluedMap<String,Object>headers = response.getHeaders();
//		response.addHeader("Set-Cookie", AuthServiceRest.JWT_COOKIE_NAME + "=" + token + "; Path=/");
//      response.addHeader("Set-Cookie", AuthServiceRest.USER_COOKIE_NAME + "=" + login + "; Path=/");
        headers.add("Set-Cookie", AuthServiceRest.JWT_COOKIE_NAME + "=" + token + "; Path=/");
        headers.add("Set-Cookie", AuthServiceRest.USER_COOKIE_NAME + "=" + login + "; Path=/");
	}
}
