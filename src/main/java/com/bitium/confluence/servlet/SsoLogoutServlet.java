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
package com.bitium.confluence.servlet;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;

import com.bitium.confluence.config.SAMLConfluenceConfig;
import com.bitium.saml.SAMLContext;


public class SsoLogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(SsoLogoutServlet.class);

	private SAMLConfluenceConfig saml2Config;

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			SAMLContext context = new SAMLContext(request, saml2Config);
			SAMLMessageContext messageContext = context.createSamlMessageContext(request, response);

			SAMLCredential credential = (SAMLCredential)request.getSession().getAttribute("SAMLCredential");

			// Send request
	        SingleLogoutProfileImpl profile = new SingleLogoutProfileImpl();
	        profile.setMetadata(context.getMetadataManager());
	        profile.setProcessor(context.getSamlProcessor());
	        profile.sendLogoutRequest(messageContext, credential);
		} catch (Exception e) {
		    log.error("saml plugin error + " + e.getMessage());
		    response.sendRedirect("/confluence/login.action?samlerror=general");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {

	}

	public void setSaml2Config(SAMLConfluenceConfig saml2Config) {
		this.saml2Config = saml2Config;
	}
}
