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

package com.bitium.confluence.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.Group;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.bitium.saml.X509Utils;

public class ConfigureAction extends ConfluenceActionSupport {
	private static final long serialVersionUID = 1L;

	private String loginUrl;
	private String logoutUrl;
	private String entityId;
	private String uidAttribute;
	private String autoCreateUser;
	private String defaultAutoCreateUserGroup;
	private String x509Certificate;
	private String idpRequired;
	private String redirectUrl;
	private String maxAuthenticationAge;
	private String urlOnLoginError;
	private String loggedOutPageTemplate;

	private ArrayList<String> existingGroups;


	private SAMLConfluenceConfig saml2Config;

	public void setSaml2Config(SAMLConfluenceConfig saml2Config) {
		this.saml2Config = saml2Config;
	}

	public ConfigureAction() {
	}

	public String getIdpRequired() {
		return idpRequired;
	}

	public void setIdpRequired(String idpRequired) {
		this.idpRequired = idpRequired;
	}

	public String getX509Certificate() {
		return x509Certificate;
	}

	public void setX509Certificate(String x509Certificate) {
		this.x509Certificate = x509Certificate;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getUidAttribute() {
		return uidAttribute;
	}

	public void setUidAttribute(String uidAttribute) {
		this.uidAttribute = uidAttribute;
	}
	
	public void setMaxAuthenticationAge(String maxAuthenticationAge) {
		this.maxAuthenticationAge=maxAuthenticationAge;
	}
	
	public String getMaxAuthenticationAge() {
		return this.maxAuthenticationAge;
	}

	public String getAutoCreateUser() {
		return autoCreateUser;
	}

	public void setAutoCreateUser(String autoCreateUser) {
		this.autoCreateUser = autoCreateUser;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getUrlOnLoginError() {
		return urlOnLoginError;
	}

	public void setUrlOnLoginError(String urlOnLoginError) {
		this.urlOnLoginError = urlOnLoginError;
	}

	public String getLoggedOutPageTemplate() {
		return loggedOutPageTemplate;
	}

	public void setLoggedOutPageTemplate(String loggedOutPageTemplate) {
		this.loggedOutPageTemplate = loggedOutPageTemplate;
	}	

	public String getDefaultAutoCreateUserGroup() {
		return defaultAutoCreateUserGroup;
	}

	public void setDefaultAutoCreateUserGroup(String defaultAutoCreateUserGroup) {
		this.defaultAutoCreateUserGroup = defaultAutoCreateUserGroup;
	}

	public ArrayList<String> getExistingGroups() {
		UserAccessor userAccessor = (UserAccessor) ContainerManager.getComponent("userAccessor");
		List<Group> groupObjects = userAccessor.getGroupsAsList();
		existingGroups = new ArrayList<String>();
		for (Group groupObject : groupObjects) {
			existingGroups.add(groupObject.getName());
		}
		setExistingGroups(existingGroups);
		return existingGroups;
	}

	public void setExistingGroups(ArrayList<String> existingGroups) {
		this.existingGroups = existingGroups;
	}

	protected List getPermissionTypes() {
		List requiredPermissions = super.getPermissionTypes();
		requiredPermissions.add("ADMINISTRATECONFLUENCE");
		return requiredPermissions;
	}

	@Override
	public void validate() {
		if (StringUtils.isBlank(getLoginUrl())) {
			addActionError(getText("saml2Plugin.admin.loginUrlEmpty"));
		} else {
			try {
				new URL(getLoginUrl());
			} catch (MalformedURLException e) {
				addActionError(getText("saml2Plugin.admin.loginUrlInvalid"));
			}
		}
		if (StringUtils.isBlank(getLogoutUrl())) {
			// addActionError(getText("saml2Plugin.admin.logoutUrlEmpty"));
		} else {
			try {
				new URL(getLogoutUrl());
			} catch (MalformedURLException e) {
				addActionError(getText("saml2Plugin.admin.logoutUrlInvalid"));
			}
		}
		if (StringUtils.isBlank(getEntityId())) {
			addActionError(getText("saml2Plugin.admin.entityIdEmpty"));
		}
		if (StringUtils.isBlank(getUidAttribute())) {
			addActionError(getText("saml2Plugin.admin.uidAttributeEmpty"));
		}
		if (StringUtils.isBlank(getX509Certificate())) {
			addActionError(getText("saml2Plugin.admin.x509CertificateEmpty"));
		} else {
			try {
				X509Utils.generateX509Certificate(getX509Certificate());
			} catch (Exception e) {
				addActionError(getText("saml2Plugin.admin.x509CertificateInvalid"));
			}
		}
		if (StringUtils.isBlank(getIdpRequired())) {
			setIdpRequired("false");
		} else {
			setIdpRequired("true");
		}
		if (StringUtils.isBlank(getAutoCreateUser())) {
			setAutoCreateUser("false");
		} else {
			setAutoCreateUser("true");
		}
		
		if(StringUtils.isBlank(getMaxAuthenticationAge()) || (!StringUtils.isNumeric(getMaxAuthenticationAge()))){
			addActionError(getText("saml2Plugin.admin.maxAuthenticationAgeInvalid"));
		}

		super.validate();
	}

	public String doDefault() throws Exception {
		setLoginUrl(saml2Config.getLoginUrl());
		setLogoutUrl(saml2Config.getLogoutUrl());
		setEntityId(saml2Config.getIdpEntityId());
			setUidAttribute(saml2Config.getUidAttribute());
		setX509Certificate(saml2Config.getX509Certificate());
		setRedirectUrl(saml2Config.getRedirectUrl());
			setLoggedOutPageTemplate(saml2Config.getLoggedOutPageTemplate());
		long maxAuthenticationAge = saml2Config.getMaxAuthenticationAge();
		
		//Default Value
		if(maxAuthenticationAge==Long.MIN_VALUE){
			setMaxAuthenticationAge("7200");
		}
		//Stored Value
		else{
			setMaxAuthenticationAge(String.valueOf(maxAuthenticationAge));
		}
				
		String idpRequired = saml2Config.getIdpRequired();

		if (idpRequired != null) {
			setIdpRequired(idpRequired);
		} else {
			setIdpRequired("false");
		}

		String autoCreateUser = saml2Config.getAutoCreateUser();
		if (autoCreateUser != null) {
			setAutoCreateUser(autoCreateUser);
		} else {
			setAutoCreateUser("false");
		}

		String defaultAutocreateUserGroup = saml2Config.getAutoCreateUserDefaultGroup();
		if (defaultAutocreateUserGroup.isEmpty()) {
			// NOTE: Set the default to "confluence-users".
			// This is used when configuring the plugin for the first time and no default was set
			defaultAutocreateUserGroup = SAMLConfluenceConfig.DEFAULT_AUTOCREATE_USER_GROUP;
		}
		setDefaultAutoCreateUserGroup(defaultAutocreateUserGroup);
		return super.doDefault();
	}

	public String execute() throws Exception {
		saml2Config.setLoginUrl(getLoginUrl());
		saml2Config.setLogoutUrl(getLogoutUrl());
		saml2Config.setEntityId(getEntityId());
		saml2Config.setUidAttribute(getUidAttribute());
		saml2Config.setX509Certificate(getX509Certificate());
		saml2Config.setIdpRequired(getIdpRequired());
		saml2Config.setRedirectUrl(getRedirectUrl());
		saml2Config.setAutoCreateUser(getAutoCreateUser());
		saml2Config.setAutoCreateUserDefaultGroup(getDefaultAutoCreateUserGroup());
		saml2Config.setMaxAuthenticationAge(Long.parseLong(getMaxAuthenticationAge()));
		saml2Config.setUrlOnLoginError(getUrlOnLoginError());
		saml2Config.setLoggedOutPageTemplate(getLoggedOutPageTemplate());

		addActionMessage(getText("saml2plugin.admin.message.saved"));
		return "success";
	}

}