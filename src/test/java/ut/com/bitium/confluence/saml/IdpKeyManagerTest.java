package ut.com.bitium.confluence.saml;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.security.cert.CertificateException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.criteria.EntityIDCriteria;

import com.bitium.confluence.saml.IdpKeyManager;

public class IdpKeyManagerTest {
	@Test
	public void testIdpKeyManagerWithCorrectEntityId() throws IOException, ResourceException, CertificateException, MetadataProviderException, SecurityException {
		ClasspathResource resource = new ClasspathResource("/test-cert.pem");
		String certificateStr = IOUtils.toString(resource.getInputStream());

		IdpKeyManager idpKeyManager = new IdpKeyManager("testId", certificateStr);
		assertEquals(idpKeyManager.getCredential("testId"), idpKeyManager.getDefaultCredential());
		assertEquals("testId", idpKeyManager.getDefaultCredential().getEntityId());
		assertEquals(idpKeyManager.getCertificate("testId").getPublicKey(), idpKeyManager.getDefaultCredential().getPublicKey());
		assertEquals("testId", idpKeyManager.getDefaultCredentialName());
		assertTrue(idpKeyManager.getAvailableCredentials().contains("testId"));
		assertEquals(1, idpKeyManager.getAvailableCredentials().size());

		CriteriaSet criteriaSet = new CriteriaSet();
		criteriaSet.add(new EntityIDCriteria("testId"));
		assertEquals("testId", idpKeyManager.resolve(criteriaSet).iterator().next().getEntityId());
		assertEquals("testId", idpKeyManager.resolveSingle(criteriaSet).getEntityId());
	}

	public void testIdpKeyManagerWithWrongEntityId() throws IOException, ResourceException, CertificateException, MetadataProviderException, SecurityException {
		ClasspathResource resource = new ClasspathResource("/test-cert.pem");
		String certificateStr = IOUtils.toString(resource.getInputStream());

		IdpKeyManager idpKeyManager = new IdpKeyManager("testId", certificateStr);
		assertNull(idpKeyManager.getCredential("wrongTestId"));
		assertNull(idpKeyManager.getCertificate("wrongTestId"));

		CriteriaSet criteriaSet = new CriteriaSet();
		criteriaSet.add(new EntityIDCriteria("wrongTestId"));
		assertFalse(idpKeyManager.resolve(criteriaSet).iterator().hasNext());
	}
}
