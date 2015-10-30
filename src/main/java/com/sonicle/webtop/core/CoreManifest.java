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

import com.sonicle.webtop.core.bol.model.AuthResource;
import com.sonicle.webtop.core.sdk.ServiceManifest;
import com.sonicle.webtop.core.sdk.ServiceVersion;
import java.util.ArrayList;

/**
 *
 * @author malbinola
 */
public class CoreManifest extends ServiceManifest {
	
	public static final String ID = "com.sonicle.webtop.core";
	public static final String XID = "wt";
	public static final String JAVA_PACKAGE = "com.sonicle.webtop.core";
	public static final String JS_PACKAGE = "Sonicle.webtop.core";
	public static final String VERSION = "5.0.0";
	public static final String BUILD_DATE = "07/10/2014";
	public static final String SERVICE_CLASS_NAME = "com.sonicle.webtop.core.Service";
	public static final String SERVICE_JS_CLASS_NAME = "Service";
	public static final String CLIENTOPTIONS_MODEL_JS_CLASS_NAME = "model.ClientOptions";
	public static final String USEROPTIONS_SERVICE_CLASS_NAME = "com.sonicle.webtop.core.UserOptionsService";
	public static final String USEROPTIONS_VIEW_JS_CLASS_NAME = "view.UserOptions";
	public static final String USEROPTIONS_MODEL_JS_CLASS_NAME = "model.UserOptions";
	public static final String COMPANY = "Sonicle S.r.l.";
	public static final String COMPANY_EMAIL = "sonicle@sonicle.com";
	public static final String COMPANY_WEBSITE = "http://www.sonicle.com";
	public static final String SUPPORT_EMAIL = "sonicle@sonicle.com";
	public static final String DATA_SOURCE_NAME = "webtop";
	
	CoreManifest() {
		id = ID;
		xid = XID;
		javaPackage = JAVA_PACKAGE;
		jsPackage = JS_PACKAGE;
		version = new ServiceVersion(VERSION);
		buildDate = BUILD_DATE;
		serviceClassName = SERVICE_CLASS_NAME;
		// This is not a real js service, it's only used 
		// to store class for client-side ovveriding purposes.
		serviceJsClassName = SERVICE_JS_CLASS_NAME;
		clientOptionsModelJsClassName = CLIENTOPTIONS_MODEL_JS_CLASS_NAME;
		userOptionsServiceClassName = USEROPTIONS_SERVICE_CLASS_NAME;
		userOptionsViewJsClassName = USEROPTIONS_VIEW_JS_CLASS_NAME;
		userOptionsModelJsClassName = USEROPTIONS_MODEL_JS_CLASS_NAME;
		company = COMPANY;
		companyEmail = COMPANY_EMAIL;
		companyWebSite = COMPANY_WEBSITE;
		supportEmail = SUPPORT_EMAIL;
		
		resources = new ArrayList<>();
		
		/*
			Marks WebTop Admins
			- ACCESS: can manage webtop palform properties
		*/
		resources.add(new AuthResource("WTADMIN", new String[]{"ACCESS"}));
		
		/*
			Activities
			- MANAGE: access to management form
		*/
		resources.add(new AuthResource("ACTIVITIES", new String[]{"MANAGE"}));
		
		/*
			Causals
			- MANAGE: access to management form
		*/
		resources.add(new AuthResource("CAUSALS", new String[]{"MANAGE"}));
		
		/*
			User personal info (via Options)
			- WRITE: user can update/change its data (if provider supports it)
		*/
		resources.add(new AuthResource("UPI", new String[]{"WRITE"}));
		
		/*
			Device synchroniztion
			- ACCESS: ability to sync data with devices
		*/
		resources.add(new AuthResource("DEVICES_SYNC", new String[]{"ACCESS"}));
	}
}
