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
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.AbstractCredentialResolver;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.springframework.security.saml.key.KeyManager;

import com.bitium.confluence.util.X509Utils;

public class IdpKeyManager extends AbstractCredentialResolver implements KeyManager {
	private String entityId;
	private Set<String> availableCredentials;
	private X509Certificate certificate;	
	private BasicX509Credential credential;
	
	public IdpKeyManager(String entityId, String certificateStr) throws MetadataProviderException, CertificateException, UnsupportedEncodingException {
		super();
		this.entityId = entityId;
		availableCredentials = new HashSet<String>();
		availableCredentials.add(entityId);
		
		certificate = X509Utils.generateX509Certificate(certificateStr);
		
		credential = new BasicX509Credential();
		credential.setEntityId(entityId);
		credential.setEntityCertificate(certificate);
	}
	
	@Override
	public Credential getCredential(String key) {
		return StringUtils.equals(key, entityId) ? credential : null;
	}

	@Override
	public Credential getDefaultCredential() {
		return getCredential(entityId);
	}

	@Override
	public String getDefaultCredentialName() {
		return entityId;
	}

	@Override
	public Set<String> getAvailableCredentials() {
		return availableCredentials;
	}

	@Override
	public Iterable<Credential> resolve(CriteriaSet criteriaSet) throws SecurityException {
		return Arrays.asList(getCredential(criteriaSet.get(EntityIDCriteria.class).getEntityID()));
	}
	
	@Override
	public X509Certificate getCertificate(String key) {
		return StringUtils.equals(key, entityId) ? certificate : null;
	}
}
