package com.bitium.confluence.logout;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.velocity.VelocityContext;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.bitium.confluence.config.SAMLConfluenceConfig;
import com.bitium.saml.SAMLStatusCodesProvider.StatusCodesExplained;
import com.bitium.saml.servlet.SsoLoginServlet;
import com.opensymphony.webwork.ServletActionContext;
/**
 * @FQCN com.bitium.confluence.logout.SAMLLoggedOut
 * @author vthoule (Alkaes Consulting)
 * @since 1.0.5
 * @version 1.0.5
 * @description Provides a Logged Out Page
 */
public class SAMLLoggedOut extends ConfluenceActionSupport {
	private static final long serialVersionUID = 1L;

	private SAMLConfluenceConfig saml2Config;

	public SAMLLoggedOut(final SAMLConfluenceConfig saml2Config) {
		this.saml2Config = saml2Config;
	}

	public String doDefault() throws Exception {
		return super.doDefault();
		// return "success";
	}

	public String getContent() {
		Map<String, Object> contextParameters = new HashMap<String, Object>();
		Map<String, Object> createVelocityParams = createVelocityParams();
		createVelocityParams.put("statusCodesExplained", getStatusCodesExplained());
		String content = VelocityUtils.getRenderedContent((CharSequence)saml2Config.getLoggedOutPageTemplate(), createVelocityParams);
		return content;
	}
	
	private StatusCodesExplained getStatusCodesExplained() {
		HttpServletRequest httpServletRequest = ServletActionContext.getRequest();
		HttpSession session = httpServletRequest.getSession();
		StatusCodesExplained statusCodesExplained = (StatusCodesExplained)session.getAttribute(SsoLoginServlet.SAML_STATUSCODES_EXPLAINED_KEY); 

		if (statusCodesExplained != null) {
			Object[] args = new Object[]{
					statusCodesExplained.getKeyOfstatusCode1stLevel(),
					statusCodesExplained.getMsgCode1stlevel(),
					statusCodesExplained.getKeyOfstatusCode2ndLevel(),
					statusCodesExplained.getMsgCode2ndlevel()};
			String errorMessage = getText("saml2.statusCode.erroMessage", args);
			statusCodesExplained.setMsgError(errorMessage);
		}
		return statusCodesExplained;
	}

//	public String doExecute() throws Exception {
//		return "success";
//	}

	protected Map<String, Object> createVelocityParams() {
		final Map<String, Object> params = MacroUtils.defaultVelocityContext();
		final Map<String, Object> result = new HashMap<String, Object>();
		if (!params.containsKey("i18n")) {
			result.put("i18n", getI18n());
		}
		result.putAll(params);
		return result;
	}



//	public String execute() throws Exception {
//		
//		return "success";
//	}

	
}
