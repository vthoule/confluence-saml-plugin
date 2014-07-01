package ut.com.bitium.confluence.util;

import java.io.IOException;
import java.security.cert.CertificateException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.util.resource.ResourceException;

import com.bitium.confluence.util.X509Utils;

public class X509UtilsTest {
	
	@Test
	public void testGenerateX509Certificate() throws CertificateException, ResourceException, IOException {
		ClasspathResource resource = new ClasspathResource("/test-cert.pem");
		String certificateStr = IOUtils.toString(resource.getInputStream()); 
		X509Utils.generateX509Certificate(certificateStr);
	}
}
