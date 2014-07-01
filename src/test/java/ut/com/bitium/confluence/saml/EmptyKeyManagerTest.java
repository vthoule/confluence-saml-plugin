package ut.com.bitium.confluence.saml;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import com.bitium.confluence.saml.EmptyKeyManager;

public class EmptyKeyManagerTest {

	@Test
	public void testEmptyKeyManager() {
		EmptyKeyManager manager = new EmptyKeyManager();
		assertNull(manager.resolve(null));
		assertNull(manager.resolveSingle(null));
		assertNull(manager.getCredential(null));
		assertNull(manager.getDefaultCredential());
		assertNull(manager.getDefaultCredentialName());
		assertNull(manager.getCertificate(null));
		assertTrue(manager.getAvailableCredentials().isEmpty());
	}
}
