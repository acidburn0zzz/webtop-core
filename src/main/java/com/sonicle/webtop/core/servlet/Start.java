/*
 * WebTop Services is a Web Application framework developed by Sonicle S.r.l.
 * Copyright (C) 2014 Sonicle S.r.l.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY SONICLE, SONICLE DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact Sonicle S.r.l. at email address sonicle@sonicle.com
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * Sonicle logo and Sonicle copyright notice. If the display of the logo is not
 * reasonably feasible for technical reasons, the Appropriate Legal Notices must
 * display the words "Copyright (C) 2014 Sonicle S.r.l.".
 */
package com.sonicle.webtop.core.servlet;

import com.sonicle.commons.LangUtils;
import com.sonicle.commons.web.ServletUtils;
import com.sonicle.webtop.core.app.CoreManifest;
import com.sonicle.webtop.core.CoreServiceSettings;
import com.sonicle.webtop.core.CoreSettings;
import com.sonicle.webtop.core.CoreUserSettings;
import com.sonicle.webtop.core.app.AbstractServlet;
import com.sonicle.webtop.core.app.SettingsManager;
import com.sonicle.webtop.core.app.WebTopApp;
import com.sonicle.webtop.core.app.WebTopSession;
import com.sonicle.webtop.core.bol.js.JsWTSPrivate;
import com.sonicle.webtop.core.app.RunContext;
import freemarker.template.Template;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author malbinola
 */
public class Start extends AbstractServlet {
	public static final String URL = "start"; // This must reflect web.xml!
	
	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebTopApp wta = getWebTopApp(request);
		SettingsManager setm = wta.getSettingsManager();
		WebTopSession wts = RunContext.getWebTopSession();
		
		//String sextdebug=System.getProperty("com.sonicle.webtop.extdebug");
		//boolean extdebug=sextdebug!=null && sextdebug.equals("true");
		boolean extdebug = WebTopApp.getPropExtDebug();
		
		try {
			WebTopApp.logger.trace("Servlet: start [{}]", ServletHelper.getSessionID(request));
			
			// Checks maintenance mode
			boolean maintenance = LangUtils.value(setm.getServiceSetting(CoreManifest.ID, CoreSettings.MAINTENANCE), false);
			if(maintenance && false) throw new MaintenanceException();
			
			wts.initPrivate(request);
			
			// Checks if otp page needs to be displayed
			boolean isOtpVerified = wts.hasProperty(CoreManifest.ID, Otp.WTSPROP_OTP_VERIFIED);
			if(!isOtpVerified) throw new OtpException();
			
			wts.initPrivateEnvironment(request);
			CoreUserSettings cus = new CoreUserSettings(wts.getUserProfile().getId()); // Keep at this line!
			
			Locale locale = wts.getLocale();
			String layout = cus.getLayout();
			
			Map vars = new HashMap();
			
			// Page + loader variables
			AbstractServlet.fillPageVars(vars, locale, wta, null);
			AbstractServlet.fillIncludeVars(vars, locale, cus.getTheme(), cus.getLookAndFeel(), cus.getRightToLeft(), extdebug);
			vars.put("layout", cus.getLayout());
			vars.put("loadingMessage", wta.lookupResource(locale, "tpl.start.loading"));
			
			// Startup variables
			JsWTSPrivate jswts = new JsWTSPrivate();
			wts.fillStartup(jswts, layout);
			vars.put("WTS", LangUtils.unescapeUnicodeBackslashes(jswts.toJson()));
			
			// Load and build template
			Template tpl = wta.loadTemplate("com/sonicle/webtop/core/start.html");
			tpl.process(vars, response.getWriter());
			
		} catch(MaintenanceException ex) {
			SecurityUtils.getSubject().logout();
			request.setAttribute(Login.ATTRIBUTE_LOGINFAILURE, Login.LOGINFAILURE_MAINTENANCE);
			ServletUtils.forwardRequest(request, response, "login");
			
		} catch(OtpException ex) {
			ServletUtils.forwardRequest(request, response, "otp");
			
		} catch(Exception ex) {
			WebTopApp.logger.error("Error in start servlet!", ex);
			
		} finally {
			response.setHeader("Cache-Control", "private, no-cache");
			ServletHelper.setPageContentType(response);
			WebTopApp.clearLoggerDC();
		}
	}
	
	private static class MaintenanceException extends Exception {
		public MaintenanceException() {
			super();
		}
	}
	
	private static class OtpException extends Exception {
		public OtpException() {
			super();
		}
	}
}
