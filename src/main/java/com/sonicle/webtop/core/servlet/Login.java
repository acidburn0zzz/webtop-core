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

import com.sonicle.commons.web.servlet.ServletUtils;
import com.sonicle.webtop.core.LocaleKey;
import com.sonicle.webtop.core.Manager;
import com.sonicle.webtop.core.Manifest;
import com.sonicle.webtop.core.WebTopApp;
import com.sonicle.webtop.core.bol.ODomain;
import freemarker.template.Template;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author malbinola
 */
public class Login extends HttpServlet {
	
	public static final String FAILURE_INVALID = "invalid";
	public static final String FAILURE_MAINTENANCE = "maintenance";
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebTopApp wta = ServletHelper.getWebTopApp(request);
		Manager manager = wta.getManager();
		
		try {
			Locale locale = ServletHelper.homogenizeLocale(request);
			
			//SettingsManager sm = wta.getSettingsManager();
			//ServiceManifest manifest = wta.getServiceManifest(ServicesManager.MAIN_SERVICE_ID);
			//boolean maintenance = LangUtils.value(sm.getServiceSetting(ServicesManager.MAIN_SERVICE_ID, Settings.MAINTENANCE), false);
			boolean maintenance = true;
			
			// Defines messages...
			String maintenanceMessage = (maintenance) ? wta.lookupResource(locale, LocaleKey.LOGIN_MAINTENANCE, true) : "";
			
			// Defines failure message
			boolean failure = false;
			String failureMessage = "";
			String failureAttribute = ServletUtils.getStringAttribute(request, "loginFailure");
			WebTopApp.logger.debug("failureAttribute is null? {}", failureAttribute==null);
			if(failureAttribute != null) {
				switch (failureAttribute) {
					case Login.FAILURE_INVALID:
						failure = true;
						failureMessage = wta.lookupResource(locale, LocaleKey.LOGIN_ERROR_FAILURE, true);
						break;
					case Login.FAILURE_MAINTENANCE:
						failure = true;
						failureMessage = wta.lookupResource(locale, LocaleKey.LOGIN_ERROR_MAINTENANCE, true);
						break;
				}
			}
			
			Map tplMap = new HashMap();
			ServletHelper.fillPageVars(tplMap, locale, wta);
			ServletHelper.fillSystemInfoVars(tplMap, locale, wta);
			//tplMap.put("title", wta.lookupAndFormatResource(locale, LocaleKey.LOGIN_TITLE, true, "5"));
			tplMap.put("failure", failure);
			tplMap.put("failureMessage", failureMessage);
			tplMap.put("maintenance", maintenance);
			tplMap.put("maintenanceMessage", maintenanceMessage);
			tplMap.put("usernamePlaceholder", wta.lookupResource(locale, LocaleKey.LOGIN_USERNAME_PLACEHOLDER, true));
			tplMap.put("passwordPlaceholder", wta.lookupResource(locale, LocaleKey.LOGIN_PASSWORD_PLACEHOLDER, true));
			tplMap.put("domainLabel", wta.lookupResource(locale, LocaleKey.LOGIN_DOMAIN_LABEL, true));
			tplMap.put("submitLabel", wta.lookupResource(locale, LocaleKey.LOGIN_SUBMIT_LABEL, true));
			List<ODomain> domains = manager.getDomains();
			tplMap.put("showDomain", (domains.size()>1));
			tplMap.put("domains", domains);
			
			Template tpl = wta.loadTemplate("com/sonicle/webtop/core/login.html");
			tpl.process(tplMap, response.getWriter());
			
		} catch(Exception ex) {
			WebTopApp.logger.error("Error in login servlet!", ex);
		} finally {
			ServletHelper.setCacheControl(response);
			ServletHelper.setPageContentType(response);
			WebTopApp.clearLoggerDC();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}
}
