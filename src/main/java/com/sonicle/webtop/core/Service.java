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
package com.sonicle.webtop.core;

import com.sonicle.commons.db.DbUtils;
import com.sonicle.commons.web.json.JsonResult;
import com.sonicle.commons.web.ServletUtils;
import com.sonicle.commons.web.json.MapItem;
import com.sonicle.commons.web.json.Payload;
import com.sonicle.security.Principal;
import com.sonicle.webtop.core.bol.OUser;
import com.sonicle.webtop.core.bol.js.JsSimple;
import com.sonicle.webtop.core.bol.js.JsFeedback;
import com.sonicle.webtop.core.bol.js.JsUserOptionsService;
import com.sonicle.webtop.core.bol.js.JsTrustedDevice;
import com.sonicle.webtop.core.bol.js.JsValue;
import com.sonicle.webtop.core.bol.js.JsWhatsnewTab;
import com.sonicle.webtop.core.bol.js.TrustedDeviceCookie;
import com.sonicle.webtop.core.dal.UserDAO;
import com.sonicle.webtop.core.sdk.AppLocale;
import com.sonicle.webtop.core.sdk.CoreLocaleKey;
import com.sonicle.webtop.core.sdk.SuperEnvironment;
import com.sonicle.webtop.core.sdk.BaseService;
import com.sonicle.webtop.core.sdk.UserProfile;
import com.sonicle.webtop.core.sdk.ServiceMessage;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 *
 * @author malbinola
 */
public class Service extends BaseService {
	
	private static final Logger logger = WT.getLogger(Service.class);
	private SuperEnvironment env;
	private CoreUserSettings cus;
	
	@Override
	public void initialize() {
		env = getSuperEnv();
		UserProfile profile = env.getProfile();
		cus = new CoreUserSettings(profile.getDomainId(), profile.getUserId(), getId());
	}

	@Override
	public void cleanup() {
		
	}

	@Override
	public HashMap<String, Object> returnClientOptions() {
		UserProfile profile = env.getProfile();
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("domainId", profile.getDomainId());
		hm.put("userId", profile.getUserId());
		hm.put("principal", profile.getStringId());
		hm.put("theme", cus.getTheme());
		hm.put("layout", cus.getLayout());
		hm.put("laf", cus.getLookAndFeel());
		hm.put("locale", profile.getLocale());
		hm.put("timezone", profile.getTimeZone().getID());
		hm.put("dateFormat", cus.getDateFormat());
		hm.put("longDateFormat", cus.getLongDateFormat());
		hm.put("timeFormat", cus.getTimeFormat());
		hm.put("longTimeFormat", cus.getLongTimeFormat());
		return hm;
	}
	
	public void processGetLocales(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		ArrayList<JsSimple> items = new ArrayList<>();
		Locale locale = env.getSession().getLocale();
		
		try {
			for(AppLocale loc : WT.getInstalledLocales()) {
				//System.out.println(loc.getShortDatePattern());
				//System.out.println(loc.getShortTimePattern());
				//System.out.println(loc.getLocale().getDisplayName());
				//System.out.println(loc.getLocale().getDisplayName(locale));
				items.add(new JsSimple(loc.getId(), loc.getLocale().getDisplayName(locale)));
			}
			new JsonResult("locales", items).printTo(out);
			
		} catch (Exception ex) {
			logger.error("Error executing action GetLocales", ex);
			new JsonResult(false, "Unable to get locales").printTo(out);
		}
	}
	
	public void processGetTimezones(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		ArrayList<JsSimple> items = new ArrayList<>();
		
		try {	
			String normId = null;
			int off;
			for(TimeZone tz : WT.getTimezones()) {
				normId = StringUtils.replace(tz.getID(), "_", " ");
				off = tz.getRawOffset()/3600000;
				items.add(new JsSimple(tz.getID(), MessageFormat.format("{0} (GMT{1}{2})", normId, (off<0) ? "-" : "+", Math.abs(off))));
			}
			new JsonResult("timezones", items).printTo(out);
			
		} catch (Exception ex) {
			logger.error("Error executing action GetTimezones", ex);
			new JsonResult(false, "Unable to get timezones").printTo(out);
		}
	}
	
	public void processGetThemes(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		
		try {
			//TODO: handle themes dinamically
			ArrayList<JsSimple> data = new ArrayList<>();
			data.add(new JsSimple("aria", "Aria"));
			data.add(new JsSimple("classic", "Classic"));
			data.add(new JsSimple("crisp", "Crisp"));
			data.add(new JsSimple("crisp-touch", "Crisp Touch"));
			data.add(new JsSimple("gray", "Gray"));
			data.add(new JsSimple("neptune", "Neptune"));
			data.add(new JsSimple("neptune-touch", "Neptune Touch"));
			new JsonResult("themes", data).printTo(out);

		} catch (Exception ex) {
			logger.error("Error executing action GetThemes", ex);
			new JsonResult(false, "Unable to get themes").printTo(out);
		}
	}
	
	public void processGetLooksAndFeels(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		
		try {
			//TODO: handle lafs dinamically
			ArrayList<JsSimple> lafs = new ArrayList<>();
			lafs.add(new JsSimple("default", "WebTop"));
			new JsonResult("lafs", lafs).printTo(out);

		} catch (Exception ex) {
			logger.error("Error executing action GetLooksAndFeels", ex);
			new JsonResult(false, "Unable to get look&feels").printTo(out);
		}
	}
	
	public void processGetOptionsUsers(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		SuperEnvironment env = getSuperEnv();
		Connection con = null;
		
		try {
			ArrayList<JsSimple> data = new ArrayList<>();
			UserProfile up = env.getProfile();
			if(up.isSystemAdmin()) {
				con = WT.getCoreConnection();
				UserDAO udao = UserDAO.getInstance();
				List<OUser> users = udao.selectAll(con);
				String id = null, descr = null;
				for(OUser user : users) {
					id = Principal.buildName(user.getDomainId(), user.getUserId());
					descr = MessageFormat.format("{0} ({1})", user.getDisplayName(), id);
					data.add(new JsSimple(id, descr));
				}
				
			} else {
				//TODO: maybe define a permission to other users to control others options
				data.add(new JsSimple(up.getStringId(), up.getDisplayName()));
			}
			new JsonResult("users", data).printTo(out);
			
		} catch (Exception ex) {
			logger.error("Error executing action GetOptionsUsers", ex);
			new JsonResult(false, "Unable to get users").printTo(out);
		} finally {
			DbUtils.closeQuietly(con);
		}
	}
	
	public void processGetOptionsServices(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		
		try {
			String id = ServletUtils.getStringParameter(request, "id", true);
			
			ArrayList<JsUserOptionsService> data = new ArrayList<>();
			data.add(new JsUserOptionsService("com.sonicle.webtop.core", "wt", "WebTop Services", "Sonicle.webtop.core.view.CoreOptions"));
			if(!UserProfile.isSystemAdmin(id)) data.add(new JsUserOptionsService("com.sonicle.webtop.calendar", "wtcal", "Calendario", "Sonicle.webtop.calendar.CalendarOptions"));
			if(!UserProfile.isSystemAdmin(id)) data.add(new JsUserOptionsService("com.sonicle.webtop.mail", "wtmail", "Posta Elettronica", "Sonicle.webtop.mail.MailOptions"));
			new JsonResult(data).printTo(out);
			
		} catch (Exception ex) {
			logger.error("Error executing action GetOptionsServices", ex);
			new JsonResult(false, "Unable to get option services").printTo(out);
		}
	}
	
	public void processGetUserServices(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		Locale locale = env.getSession().getLocale();
		
		ArrayList<JsSimple> items = new ArrayList<>();
		List<String> ids = env.getSession().getServices();
		for(String id : ids) {
			items.add(new JsSimple(id, WT.lookupResource(id, locale, BaseService.RESOURCE_SERVICE_NAME)));
		}
		new JsonResult("services", items).printTo(out);
	}
	
	public void processFeedback(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		
		try {
			Payload<MapItem, JsFeedback> pl = ServletUtils.getPayload(request, JsFeedback.class);
			
			logger.debug("message: {}", pl.data.message);
			Thread.sleep(4000);
			new JsonResult().printTo(out);
			//new JsonResult(false, "Erroreeeeeeeeeeeeeeeeeeeeeee").printTo(out);
			
		} catch(Exception ex) {
			logger.error("Error executing action Feedback", ex);
			new JsonResult(false, "Unable to send feedback report.").printTo(out);
		}
	}
	
	public void processGetWhatsnewTabs(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		ArrayList<JsWhatsnewTab> tabs = null;
		JsWhatsnewTab tab = null;
		String html = null;
		UserProfile profile = env.getProfile();
		
		try {
			boolean full = ServletUtils.getBooleanParameter(request, "full", false);
			
			tabs = new ArrayList<>();
			List<String> ids = env.getSession().getServices();
			for(String id : ids) {
				if(full || env.getManager().needWhatsnew(id, profile)) {
					html = env.getManager().getWhatsnewHtml(id, env.getProfile(), full);
					if(!StringUtils.isEmpty(html)) {
						tab = new JsWhatsnewTab(id);
						tab.title = WT.lookupResource(id, profile.getLocale(), CoreLocaleKey.SERVICE_NAME);
						tabs.add(tab);
					}
				}
			}
			new JsonResult(tabs).printTo(out);
			
		} catch (Exception ex) {
			logger.error("Error executing action GetWhatsnewTabs", ex);
			new JsonResult(false, "Unable to get What's New info.").printTo(out);
		}
	}
	
	public void processGetWhatsnewHTML(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		
		try {
			String id = ServletUtils.getStringParameter(request, "id", true);
			boolean full = ServletUtils.getBooleanParameter(request, "full", false);
			
			String html = env.getManager().getWhatsnewHtml(id, env.getProfile(), full);
			out.println(html);
			
		} catch (Exception ex) {
			logger.error("Error executing action GetWhatsnewHTML", ex);
		}
	}
	
	public void processTurnOffWhatsnew(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		
		try {
			UserProfile profile = env.getProfile();
			List<String> ids = env.getSession().getServices();
			for(String id : ids) {
				env.getManager().resetWhatsnew(id, profile);
			}
			
		} catch (Exception ex) {
			logger.error("Error executing action TurnOffWhatsnew", ex);
		} finally {
			new JsonResult().printTo(out);
		}
	}
	
	public void processSetToolComponentWidth(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		try {
			String serviceId = ServletUtils.getStringParameter(request, "serviceId", true);
			Integer width = ServletUtils.getIntParameter(request, "width", true);
			
			UserProfile profile = getSuperEnv().getProfile();
			CoreUserSettings cusx = new CoreUserSettings(profile.getDomainId(), profile.getUserId(), serviceId);
			cusx.setViewportToolWidth(width);
			new JsonResult().printTo(out);
			
		} catch (Exception ex) {
			logger.error("Error executing action SetToolComponentWidth", ex);
			new JsonResult(false, "Unable to save with").printTo(out);
		}
	}
	
	public void processManageTFA(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		TFAManager tfam = env.getTFAManager();
		
		try {
			String crud = ServletUtils.getStringParameter(request, "crud", true);
			if(crud.equals("generate")) {
				String deliveryMode = ServletUtils.getStringParameter(request, "delivery", true);
				if(deliveryMode.equals(CoreUserSettings.TFA_DELIVERY_EMAIL)) {
					String email = ServletUtils.getStringParameter(request, "emailAddress", true);
					tfam.initTFAUsingEmail(env.getSession(), email);
					
				} else if(deliveryMode.equals(CoreUserSettings.TFA_DELIVERY_GOOGLEAUTH)) {
					tfam.initTFAUsingGoogleAuth(env.getSession());
				}
				new JsonResult(true).printTo(out);
				
			} else if(crud.equals("activate")) {
				String deliveryMode = ServletUtils.getStringParameter(request, "delivery", true);
				Integer code = ServletUtils.getIntParameter(request, "code", true);
				boolean enabled = tfam.activateTFA(env.getSession(), deliveryMode, code);
				new JsonResult(enabled).printTo(out);
				
			} else if(crud.equals("untrustthis")) {
				TrustedDeviceCookie tdc = tfam.readTrustedDeviceCookie(env.getProfile(), request);
				if(tdc == null) throw new Exception("This device is already untrusted");
				tfam.removeTrustedDevice(env.getProfile(), tdc.deviceId);
				tfam.clearTrustedDeviceCookie(env.getProfile(), response);
				new JsonResult().printTo(out);
				
			} else if(crud.equals("untrustothers")) {
				TrustedDeviceCookie thistdc = tfam.readTrustedDeviceCookie(env.getProfile(), request);
				ArrayList<JsTrustedDevice> tds = tfam.getTrustedDevices(env.getProfile());
				for(JsTrustedDevice td: tds) {
					if((thistdc != null) && (td.deviceId.equals(thistdc.deviceId))) continue;
					tfam.removeTrustedDevice(env.getProfile(), td.deviceId);
				}
				new JsonResult().printTo(out);
			}
			
		} catch (Exception ex) {
			logger.error("Error executing action ManageTFA", ex);
			new JsonResult(false, "Error managing TFA").printTo(out);
		}
	}
	
	public void processServerEvents(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		List<ServiceMessage> messages = new ArrayList();
		
		try {
			messages = env.getSession().getEnqueuedMessages();
			
		} catch (Exception ex) {
			logger.error("Error executing action ServerEvents", ex);
		} finally {
			new JsonResult(JsonResult.gson.toJson(messages)).printTo(out);
		}
	}
}
