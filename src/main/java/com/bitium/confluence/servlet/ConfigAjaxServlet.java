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

import com.bitium.confluence.config.SAMLConfluenceConfig;


/**
 * For now it does only one function: returns idpRequired Field
 * 
 * This might probably get expanded to return json with other fields as well
 *
 */
public class ConfigAjaxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private SAMLConfluenceConfig saml2Config;
	
	public void setSaml2Config(SAMLConfluenceConfig saml2Config) {
		this.saml2Config = saml2Config;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String parameter = request.getParameter("param");
		if (parameter != null) {
			if (parameter.equals("idpRequired")) {
				response.getOutputStream().write(saml2Config.getIdpRequired().getBytes());
			} else if (parameter.equals("logoutUrl")) {
				response.getOutputStream().write(saml2Config.getLogoutUrl().getBytes());
			}				
		} 
		
	}
	    
}
