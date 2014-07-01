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

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Set;

import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.Credential;
import org.springframework.security.saml.key.KeyManager;

public class EmptyKeyManager implements KeyManager {

	@Override
	public Iterable<Credential> resolve(CriteriaSet arg0)
			throws SecurityException {
		return null;
	}

	@Override
	public Credential resolveSingle(CriteriaSet arg0) throws SecurityException {
		return null;
	}

	@Override
	public Credential getCredential(String keyName) {
		return null;
	}

	@Override
	public Credential getDefaultCredential() {
		return null;
	}

	@Override
	public String getDefaultCredentialName() {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<String> getAvailableCredentials() {
		return Collections.EMPTY_SET;
	}

	@Override
	public X509Certificate getCertificate(String alias) {
		return null;
	}
}
