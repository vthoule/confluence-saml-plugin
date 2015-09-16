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

import org.apache.commons.lang.StringUtils;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.bitium.saml.SAMLConfig;

public class SAMLConfluenceConfig implements SAMLConfig {
	
	private PluginSettings pluginSettings;

	private String defaultBaseURL;
	
	public static final String ENTITY_ID_SETTING = "saml2.entityId";
	public static final String LOGIN_URL_SETTING = "saml2.loginUrl";
	public static final String LOGOUT_URL_SETTING = "saml2.logoutUrl";
	public static final String X509_CERTIFICATE_SETTING = "saml2.x509Certificate";
	public static final String IDP_REQUIRED_SETTING = "saml2.idpRequired";
	public static final String REDIRECT_URL_SETTING = "saml2.redirectUrl";
	
	public void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory) {
		this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
	}
	
	public void setLoginUrl(String loginUrl) {
		pluginSettings.put(LOGIN_URL_SETTING, loginUrl);
	}

	public void setLogoutUrl(String logoutUrl) {
		pluginSettings.put(LOGOUT_URL_SETTING, logoutUrl);		
	}

	public void setEntityId(String entityId) {
		pluginSettings.put(ENTITY_ID_SETTING, entityId);		
	}

	public void setX509Certificate(String x509Certificate) {
		pluginSettings.put(X509_CERTIFICATE_SETTING, x509Certificate);		
	}

	public void setIdpRequired(String idpRequired) {
		pluginSettings.put(IDP_REQUIRED_SETTING, idpRequired);		
	}
	
	public void setRedirectUrl(String redirectUrl) {
		pluginSettings.put(REDIRECT_URL_SETTING, redirectUrl);		
	}
	
	public String getIdpRequired() {
		return StringUtils.defaultString((String)pluginSettings.get(IDP_REQUIRED_SETTING));
	}
	
	public boolean getIdpRequiredFlag() {
		if (StringUtils.defaultString((String)pluginSettings.get(IDP_REQUIRED_SETTING)).equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getLoginUrl() {
		return StringUtils.defaultString((String)pluginSettings.get(LOGIN_URL_SETTING));
	}

	public String getLogoutUrl() {
		return StringUtils.defaultString((String)pluginSettings.get(LOGOUT_URL_SETTING));
	}

	public String getIdpEntityId() {
		return StringUtils.defaultString((String)pluginSettings.get(ENTITY_ID_SETTING));
	}

	public String getX509Certificate() {
		return StringUtils.defaultString((String)pluginSettings.get(X509_CERTIFICATE_SETTING));
	}
	
	public String getRedirectUrl() {
		return StringUtils.defaultString((String)pluginSettings.get(REDIRECT_URL_SETTING));
	}

	public void setDefaultBaseUrl(String defaultBaseURL) {
		this.defaultBaseURL = defaultBaseURL;		
	}
	
	public String getAlias() {
		return "confluenceSAML";
	}

	public String getBaseUrl() {
		return StringUtils.defaultString(defaultBaseURL);
	}

	public String getSpEntityId() {
		return defaultBaseURL + "/" + getAlias();
	}

}
