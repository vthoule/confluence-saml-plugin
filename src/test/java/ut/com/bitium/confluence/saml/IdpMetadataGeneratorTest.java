package ut.com.bitium.confluence.saml;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.ConfigurationException;

import com.bitium.confluence.config.SAMLConfig;
import com.bitium.confluence.saml.IdpMetadataGenerator;

public class IdpMetadataGeneratorTest {
	@Test
	public void testIdpMetadataGenerator() throws MetadataProviderException, ResourceException, ConfigurationException {
		DefaultBootstrap.bootstrap();
		SAMLConfig config = mock(SAMLConfig.class);
		when(config.getIdpEntityId()).thenReturn("testId");
		when(config.getLoginUrl()).thenReturn("testLoginUrl");
		when(config.getLogoutUrl()).thenReturn("testLogoutUrl");

		IdpMetadataGenerator generator = new IdpMetadataGenerator();
		MetadataProvider metadata = generator.generate(config);
		EntityDescriptor entityDescriptor = metadata.getEntityDescriptor("testId");
		assertEquals("testId", entityDescriptor.getEntityID());

		IDPSSODescriptor idpSsoDescriptor = entityDescriptor.getIDPSSODescriptor(SAMLConstants.SAML20P_NS);
		assertFalse(idpSsoDescriptor.getWantAuthnRequestsSigned());

		assertEquals(1, idpSsoDescriptor.getSingleSignOnServices().size());
		SingleSignOnService signOnService = idpSsoDescriptor.getSingleSignOnServices().get(0);
		assertEquals(SAMLConstants.SAML2_REDIRECT_BINDING_URI, signOnService.getBinding());
		assertEquals("testLoginUrl", signOnService.getLocation());

		assertEquals(1, idpSsoDescriptor.getSingleLogoutServices().size());
		SingleLogoutService logoutService = idpSsoDescriptor.getSingleLogoutServices().get(0);
		assertEquals(SAMLConstants.SAML2_REDIRECT_BINDING_URI, logoutService.getBinding());
		assertEquals("testLogoutUrl", logoutService.getLocation());
	}
}
