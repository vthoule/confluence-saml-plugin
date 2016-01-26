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

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceAuthenticator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.Group;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.security.password.Credential;
import com.bitium.confluence.config.SAMLConfluenceConfig;
import com.bitium.saml.SAMLContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.security.saml.websso.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.List;


public class SsoLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    //TODO: Make this default group configurable via the SAML Plugin Config
    private static final String DEFAULT_NEW_USER_GROUP = "confluence-users";

    private Log log = LogFactory.getLog(SsoLoginServlet.class);

	private SAMLConfluenceConfig saml2Config;

    private SAMLCredential credential;

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

			// Generate options for the current SSO request
	        WebSSOProfileOptions options = new WebSSOProfileOptions();
	        options.setBinding(org.opensaml.common.xml.SAMLConstants.SAML2_REDIRECT_BINDING_URI);
                options.setIncludeScoping(false);

			// Send request
	        WebSSOProfile webSSOprofile = new WebSSOProfileImpl(context.getSamlProcessor(), context.getMetadataManager());
	        webSSOprofile.sendAuthenticationRequest(messageContext, options);
		} catch (Exception e) {
		    log.error("saml plugin error + " + e.getMessage());
			response.sendRedirect("/confluence/login.action?samlerror=general");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			SAMLContext context = new SAMLContext(request, saml2Config);
			SAMLMessageContext messageContext = context.createSamlMessageContext(request, response);

			// Process response
	        context.getSamlProcessor().retrieveMessage(messageContext);

	        messageContext.setLocalEntityEndpoint(SAMLUtil.getEndpoint(messageContext.getLocalEntityRoleMetadata().getEndpoints(), messageContext.getInboundSAMLBinding(), request.getRequestURL().toString()));
	        messageContext.getPeerEntityMetadata().setEntityID(saml2Config.getIdpEntityId());

	        WebSSOProfileConsumer consumer = new WebSSOProfileConsumerImpl(context.getSamlProcessor(), context.getMetadataManager());
	        credential = consumer.processAuthenticationResponse(messageContext);

	        request.getSession().setAttribute("SAMLCredential", credential);

//	        String userName = ((XSAny)credential.getAttributes().get(0).getAttributeValues().get(0)).getTextContent();
                String userName = credential.getNameID().getValue();

	        authenticateUserAndLogin(request, response, userName);
		} catch (AuthenticationException e) {
			try {
			    log.error("saml plugin error + " + e.getMessage());
				response.sendRedirect("/confluence/login.action?samlerror=plugin_exception");
			} catch (IOException e1) {
				throw new ServletException();
			}
		} catch (Exception e) {
			try {
			    log.error("saml plugin error + " + e.getMessage());
				response.sendRedirect("/confluence/login.action?samlerror=plugin_exception");
			} catch (IOException e1) {
				throw new ServletException();
			}
		}
	}

	private void authenticateUserAndLogin(HttpServletRequest request,
			HttpServletResponse response, String username)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, IOException {
		Authenticator authenticator = SecurityConfigFactory.getInstance().getAuthenticator();

        if (authenticator instanceof ConfluenceAuthenticator) {
            UserAccessor userAccessor = (UserAccessor) ContainerManager.getComponent("userAccessor");
            ConfluenceUser confluenceUser = userAccessor.getUserByName(username);
            if (confluenceUser == null) {
                confluenceUser = tryCreateOrUpdateUser(username);
            }

            if (confluenceUser != null) {

                //Note: Need to use reflection to call the protected DefaultAuthenticator.authoriseUserAndEstablishSession
                Principal principal = confluenceUser;

                Method authUserMethod = DefaultAuthenticator.class.getDeclaredMethod("authoriseUserAndEstablishSession",
                        new Class[]{HttpServletRequest.class, HttpServletResponse.class, Principal.class});
                authUserMethod.setAccessible(true);
                Boolean result = (Boolean)authUserMethod.invoke(authenticator, new Object[]{request, response, principal});

                if (result) {
                    String redirectUrl = saml2Config.getRedirectUrl();
                    if (redirectUrl == null || redirectUrl.equals("")) {
                        redirectUrl = "/confluence/dashboard.action";
                    }
                    response.sendRedirect(redirectUrl);
                    return;
                }
            }
        }
		response.sendRedirect("/confluence/login.action?samlerror=user_not_found");
	}

    private ConfluenceUser tryCreateOrUpdateUser(String username) {
        if (saml2Config.getAutoCreateUserFlag()){
            UserAccessor userAccessor = (UserAccessor) ContainerManager.getComponent("userAccessor");

            String fullName = credential.getAttributeAsString("cn");
            String email = credential.getAttributeAsString("mail");

            log.warn("Creating user account for " + username );
            ConfluenceUser createdUser = userAccessor.createUser(new DefaultUser(username, fullName, email), Credential.NONE);

            // Find the first administrator user and use it to add the user to the confluence-users group if it exists
            ConfluenceUser administratorUser = getAdministratorUser();
            Group confluenceUsersGroup = userAccessor.getGroup(DEFAULT_NEW_USER_GROUP);
            if (administratorUser != null && confluenceUsersGroup != null) {
                AuthenticatedUserThreadLocal.set(administratorUser);
                userAccessor.addMembership(confluenceUsersGroup, createdUser);
            }
            return createdUser;
        } else {
            // not allowed to auto-create user
            log.error("User not found and auto-create disabled: " + username);
        }
        return null;
    }

    private ConfluenceUser getAdministratorUser() {
        UserAccessor userAccessor = (UserAccessor) ContainerManager.getComponent("userAccessor");
        List<String> administratorNames = userAccessor.getMemberNamesAsList(userAccessor.getGroup("confluence-administrators"));
        if (administratorNames != null && administratorNames.size() > 0) {
            return userAccessor.getUserByName(administratorNames.get(0));
        }
        return null;
    }

    public void setSaml2Config(SAMLConfluenceConfig saml2Config) {
		this.saml2Config = saml2Config;
	}
}
