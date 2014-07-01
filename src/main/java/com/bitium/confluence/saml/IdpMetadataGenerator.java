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

import org.opensaml.Configuration;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataMemoryProvider;

import com.bitium.confluence.config.SAMLConfig;

public class IdpMetadataGenerator {
	private XMLObjectBuilderFactory builderFactory;

	public IdpMetadataGenerator() {
		this.builderFactory = Configuration.getBuilderFactory();
	}

	@SuppressWarnings("unchecked")
	public MetadataProvider generate(SAMLConfig configuration) throws MetadataProviderException, ResourceException  {
		SAMLObjectBuilder<EntityDescriptor> builder = (SAMLObjectBuilder<EntityDescriptor>) builderFactory.getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        EntityDescriptor descriptor = builder.buildObject();
        descriptor.setID(configuration.getIdpEntityId());
        descriptor.setEntityID(configuration.getIdpEntityId());
        descriptor.getRoleDescriptors().add(buildIDPSSODescriptor(configuration));

        MetadataMemoryProvider memoryProvider = new MetadataMemoryProvider(descriptor);
        memoryProvider.initialize();

        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
    	extendedMetadata.setSigningKey(configuration.getIdpEntityId());

    	ExtendedMetadataDelegate idpMetadataProvider = new ExtendedMetadataDelegate(memoryProvider, extendedMetadata);
    	idpMetadataProvider.setMetadataRequireSignature(false);
    	idpMetadataProvider.initialize();

    	return idpMetadataProvider;
	}

	@SuppressWarnings("unchecked")
	private IDPSSODescriptor buildIDPSSODescriptor(SAMLConfig configuration) {
        SAMLObjectBuilder<IDPSSODescriptor> builder = (SAMLObjectBuilder<IDPSSODescriptor>) builderFactory.getBuilder(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        IDPSSODescriptor idpDescriptor = builder.buildObject();
        idpDescriptor.setWantAuthnRequestsSigned(false);
        idpDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

        idpDescriptor.getSingleSignOnServices().add(getSingleSignOnService(configuration, SAMLConstants.SAML2_REDIRECT_BINDING_URI));
        idpDescriptor.getSingleLogoutServices().add(getSingleLogoutService(configuration, SAMLConstants.SAML2_REDIRECT_BINDING_URI));

        return idpDescriptor;

    }

	@SuppressWarnings("unchecked")
	private SingleSignOnService getSingleSignOnService(SAMLConfig configuration, String binding) {
		SAMLObjectBuilder<SingleSignOnService> builder = (SAMLObjectBuilder<SingleSignOnService>) builderFactory.getBuilder(SingleSignOnService.DEFAULT_ELEMENT_NAME);
		SingleSignOnService service = builder.buildObject();
		service.setBinding(binding);
		service.setLocation(configuration.getLoginUrl());

		return service;
	}

	@SuppressWarnings("unchecked")
	private SingleLogoutService getSingleLogoutService(SAMLConfig configuration, String binding) {
		SAMLObjectBuilder<SingleLogoutService> builder = (SAMLObjectBuilder<SingleLogoutService>) builderFactory.getBuilder(SingleLogoutService.DEFAULT_ELEMENT_NAME);
		SingleLogoutService service = builder.buildObject();
		service.setBinding(binding);
		service.setLocation(configuration.getLogoutUrl());

		return service;
	}
}
