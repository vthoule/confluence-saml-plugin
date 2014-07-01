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
package com.bitium.confluence.saml;

import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.SAMLConstants;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessor;
import org.springframework.security.saml.processor.SAMLProcessorImpl;

import com.bitium.confluence.config.SAMLConfig;

public class SAMLContext {
	private static final Logger logger = LoggerFactory.getLogger(SAMLContext.class);
	private static final SAMLProcessor samlProcessor;
	
	private MetadataManager metadataManager;
	private KeyManager idpKeyManager;
	
	private SAMLContextProviderImpl messageContextProvider;
	
	static {
		try {
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			logger.error("Error during DefaultBootstrap.bootstrap()", e);
		}
		
		SAMLBinding redirectBinding = new HTTPRedirectDeflateBinding(Configuration.getParserPool());
		SAMLBinding postBinding = new HTTPPostBinding(Configuration.getParserPool(), null);
        samlProcessor = new SAMLProcessorImpl(Arrays.asList(redirectBinding, postBinding));
	}
	
	public SAMLContext(HttpServletRequest request, SAMLConfig configuration) throws ConfigurationException, CertificateException, UnsupportedEncodingException, MetadataProviderException, ServletException, ResourceException {
		configuration.setDefaultBaseUrl(getDefaultBaseURL(request));
		
		idpKeyManager = new IdpKeyManager(configuration.getIdpEntityId(), configuration.getX509Certificate());
		SpMetadataGenerator spMetadataGenerator = new SpMetadataGenerator();
		MetadataProvider spMetadataProvider = spMetadataGenerator.generate(configuration);
		IdpMetadataGenerator idpMetadataGenerator = new IdpMetadataGenerator();
		MetadataProvider idpMetadataProvider = idpMetadataGenerator.generate(configuration);
		
		metadataManager = new MetadataManager(Arrays.asList(spMetadataProvider, idpMetadataProvider));
		metadataManager.setKeyManager(idpKeyManager);
		metadataManager.setHostedSPName(configuration.getSpEntityId());
		metadataManager.refreshMetadata();
		
		messageContextProvider = new SAMLContextProviderImpl();
		messageContextProvider.setMetadata(metadataManager);
		messageContextProvider.setKeyManager(idpKeyManager);
		messageContextProvider.afterPropertiesSet();
	}
	
	public SAMLMessageContext createSamlMessageContext(HttpServletRequest request, HttpServletResponse response) throws ServletException, MetadataProviderException {
		SAMLMessageContext context = messageContextProvider.getLocalAndPeerEntity(request, response);
		
		SPSSODescriptor spDescriptor = (SPSSODescriptor) context.getLocalEntityRoleMetadata();
		
		String responseURL = request.getRequestURL().toString();
		spDescriptor.getDefaultAssertionConsumerService().setResponseLocation(responseURL);
		for (AssertionConsumerService service : spDescriptor.getAssertionConsumerServices()) {
			service.setResponseLocation(responseURL);
		}
		
		spDescriptor.setAuthnRequestsSigned(false);
		context.setCommunicationProfileId(SAMLConstants.SAML2_WEBSSO_PROFILE_URI);
		
		return context;
	}

	public SAMLProcessor getSamlProcessor() {
		return samlProcessor;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}
	
	public KeyManager getIdpKeyManager() {
		return idpKeyManager;
	}

	private String getDefaultBaseURL(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort());
        sb.append(request.getContextPath());
        return sb.toString();
    }
	
	
}
