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

import com.sonicle.commons.LangUtils;
import com.sonicle.webtop.core.sdk.BaseUserSettings;
import com.sonicle.webtop.core.sdk.UserProfile;

/**
 *
 * @author malbinola
 */
public class CoreUserSettings extends BaseUserSettings {
	
    public CoreUserSettings(String domainId, String userId, String serviceId) {
        super(domainId, userId, serviceId);
    }
	
	/**
	 * [string]
	 * Look and feel
	 */
	public static final String LAF = "laf";
	/**
	 * [string]
	 * Theme name
	 */
	public static final String THEME = "theme";
	/**
	 * [boolean]
	 * Right-to-left mode
	 */
	public static final String RTL = "rtl";
	/**
	 * [string][*]
	 * Saves width of tool component
	 */
	public static final String VIEWPORT_TOOL_WIDTH = "viewport.tool.width";
	/**
	 * [boolean]
	 * Specifies if whatsnew window must be shown after a service upgrade
	 */
	public static final String WHATSNEW_ENABLED = "whatsnew.enabled";
	/**
	 * [string][*]
	 * Saves last seen service version for whatsnew handling
	 */
	public static final String WHATSNEW_VERSION = "whatsnew.version";
	
	//public static final String PROFILEDATA_EDITABLE = "profiledata.editable";
	//public static final String OTP_ENABLED = "otp.enabled";
	
	/**
	 * [string]
	 * Specifies delivery method. One of: NONE, EMAIL, GOOGLEAUTH.
	 */
	public static final String TFA_DELIVERY = "tfa.delivery";
	public static final String TFA_DELIVERY_EMAIL = "email";
	public static final String TFA_DELIVERY_GOOGLEAUTH = "googleauth";
	/**
	 * [string]
	 * Specifies generated secret string within googleauth delivery.
	 */
	public static final String TFA_SECRET = "tfa.secret";
	/**
	 * [string]
	 * Specifies choosen email address within email delivery.
	 */
	public static final String TFA_EMAILADDRESS = "tfa.emailaddress";
	public static final String TFA_SONICLEAUTH_INTERVAL = "tfa.sonicleauth.interval";
	public static final String TFA_TRUSTED_DEVICE = "tfa.trusteddevice";
	
	public String getTheme() {
		return getUserSetting(THEME, "crisp");
	}
	
	public boolean setTheme(String value) {
		return setUserSetting(THEME, value);
	}
	
	public String getLookAndFeel() {
		return getUserSetting(LAF, "default");
	}
	
	public boolean setLookAndFeel(String value) {
		return setUserSetting(LAF, value);
	}
	
	public boolean getRightToLeft() {
		return getUserSetting(RTL, false);
	}
	
	public Integer getViewportToolWidth() {
		return LangUtils.value(getUserSetting(CoreUserSettings.VIEWPORT_TOOL_WIDTH), Integer.class);
	}
	
	public boolean setViewportToolWidth(Integer value) {
		return setUserSetting(CoreUserSettings.VIEWPORT_TOOL_WIDTH, value);
	}
	
	public boolean getWhatsnewEnabled() {
		return getUserSetting(WHATSNEW_ENABLED, true);
	}
	
	public String getTFADelivery() {
		return getUserSetting(CoreUserSettings.TFA_DELIVERY);
	}
	
	public boolean setTFADelivery(String value) {
		return setUserSetting(CoreUserSettings.TFA_DELIVERY, value);
	}
	
	public String getTFASecret() {
		return getUserSetting(CoreUserSettings.TFA_SECRET);
	}
	
	public boolean setTFASecret(String value) {
		return setUserSetting(CoreUserSettings.TFA_SECRET, value);
	}
	
	public String getTFAEmailAddress() {
		return getUserSetting(CoreUserSettings.TFA_EMAILADDRESS);
	}
	
	public boolean setTFAEmailAddress(String value) {
		return setUserSetting(CoreUserSettings.TFA_EMAILADDRESS, value);
	}
	
	
	public static String getWhatsnewVersion(SettingsManager setm, UserProfile profile, String serviceId) {
		return setm.getUserSetting(profile, serviceId, CoreUserSettings.WHATSNEW_VERSION);
	}
	
	public static boolean setWhatsnewVersion(SettingsManager setm, UserProfile profile, String serviceId, String value) {
		return setm.setUserSetting(profile, serviceId, CoreUserSettings.WHATSNEW_VERSION, value);
	}
	
	
}
