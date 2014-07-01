/**
 * Confluence SAML Plugin - a confluence plugin to allow SAML 2.0
 *	authentication. 
 *
 *	Copyright (C) 2014 Bitium, Inc.
 *	
 *	This file is part of Confluence SAML Plugin.
 *	
 *	Confluence SAML Plugin is free software: you can redistribute it 
 *	and/or modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation, either version 3 of
 *	the License, or (at your option) any later version.
 *	
 *	Confluence SAML Plugin is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bitium.confluence.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.bitium.confluence.config.SAMLConfig;

public class LoginFilter implements Filter {
	
	private SAMLConfig config;
	private LoginUriProvider loginUriProvider;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
        boolean idpRequired = config.getIdpRequiredFlag();
        HttpServletRequest req = (HttpServletRequest)request;
    	HttpServletResponse res = (HttpServletResponse)response;
    	
        if (idpRequired == true) {
        	try {
				res.sendRedirect(loginUriProvider.getLoginUri((new URI(req.getRequestURI().toString()))).toString() + "&samlerror=general");
			} catch (URISyntaxException e) {
			}        	
        } else {
        	chain.doFilter(request, response);
        }
	}

	@Override
	public void destroy() {
	}

	public void setConfig(SAMLConfig config) {
		this.config = config;
	}

	public void setLoginUriProvider(LoginUriProvider loginUriProvider) {
		this.loginUriProvider = loginUriProvider;
	}

}
