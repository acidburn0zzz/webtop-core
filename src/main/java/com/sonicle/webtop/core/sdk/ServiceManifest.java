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
package com.sonicle.webtop.core.sdk;

import com.sonicle.commons.LangUtils;
import java.text.MessageFormat;
import java.util.Locale;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author malbinola
 */
public class ServiceManifest {
	
	protected String id;
	protected String xid;
	protected String javaPackage;
	protected String jsPackage;
	protected ServiceVersion version;
	protected ServiceVersion oldVersion;
	protected String buildDate;
	protected String serviceClassName;
	protected String userOptionsServiceClassName;
	protected String publicServiceClassName;
	protected String deamonServiceClassName;
	protected String serviceJsClassName;
	protected String clientOptionsModelJsClassName;
	protected String userOptionsViewJsClassName;
	protected String userOptionsModelJsClassName;
	protected Boolean hidden;
	protected String company;
	protected String companyEmail;
	protected String companyWebSite;
	protected String supportEmail;
	protected String dataSourceName;
	protected String[] initCheckTables;
	
	public ServiceManifest() {
		version = new ServiceVersion();
		oldVersion = new ServiceVersion();
		buildDate = StringUtils.EMPTY;
		company = "Unknown Company";
		companyEmail = "sonicle@sonicle.com";
		companyWebSite = "http://www.sonicle.com";
		supportEmail = "sonicle@sonicle.com";
	}
	
	public ServiceManifest(HierarchicalConfiguration svcEl) throws Exception {
		
		String pkg = svcEl.getString("package");
		if(StringUtils.isEmpty(pkg)) throw new Exception("Invalid value for property [package]");
		javaPackage = StringUtils.lowerCase(pkg);
		id = javaPackage;
		
		String jspkg = svcEl.getString("jsPackage");
		if(StringUtils.isEmpty(jspkg)) throw new Exception("Invalid value for property [jsPackage]");
		jsPackage = jspkg; // Lowercase allowed!
		
		String sname = svcEl.getString("shortName");
		if(StringUtils.isEmpty(sname)) throw new Exception("Invalid value for property [shortName]");
		xid = sname;
		
		ServiceVersion ver = new ServiceVersion(svcEl.getString("version"));
		if(ver.isUndefined()) throw new Exception("Invalid value for property [version]");
		version = ver;
		
		buildDate = StringUtils.defaultIfBlank(svcEl.getString("buildDate"), null);
		
		if(svcEl.containsKey("serviceClassName")) {
			String cn = StringUtils.defaultIfEmpty(svcEl.getString("serviceClassName"), "Service");
			serviceClassName = LangUtils.buildClassName(javaPackage, cn);
			serviceJsClassName = StringUtils.defaultIfEmpty(svcEl.getString("serviceJsClassName"), cn);
			clientOptionsModelJsClassName = StringUtils.defaultIfEmpty(svcEl.getString("clientOptionsModelJsClassName"), "model.ClientOptions");
		}
		
		if(svcEl.containsKey("publicServiceClassName")) {
			publicServiceClassName = LangUtils.buildClassName(javaPackage, StringUtils.defaultIfEmpty(svcEl.getString("publicServiceClassName"), "PublicService"));
		}
		
		if(svcEl.containsKey("deamonServiceClassName")) {
			deamonServiceClassName = LangUtils.buildClassName(javaPackage, StringUtils.defaultIfEmpty(svcEl.getString("deamonServiceClassName"), "DeamonService"));
		}
		
		if(!svcEl.configurationsAt("userOptions").isEmpty()) {
			userOptionsServiceClassName = LangUtils.buildClassName(javaPackage, StringUtils.defaultIfEmpty(svcEl.getString("userOptions.serviceClassName"), "UserOptionsService"));
			userOptionsViewJsClassName = StringUtils.defaultIfEmpty(svcEl.getString("userOptions.viewJsClassName"), "view.UserOptions");
			userOptionsModelJsClassName = StringUtils.defaultIfEmpty(svcEl.getString("userOptions.modelJsClassName"), "model.UserOptions");
		}
		
		hidden = svcEl.getBoolean("hidden", false);
		company = StringUtils.defaultIfBlank(svcEl.getString("company"), null);
		companyEmail = StringUtils.defaultIfBlank(svcEl.getString("companyEmail"), null);
		companyWebSite = StringUtils.defaultIfBlank(svcEl.getString("companyWebSite"), null);
		supportEmail = StringUtils.defaultIfBlank(svcEl.getString("supportEmail"), null);
	}
	
	/*
	public ServiceManifest(
		String javaPackage, String jsPackage, String shortName, ServiceVersion version, String buildDate,
		String serviceClassName, String serviceJsClassName, 
		String publicServiceClassName, String deamonServiceClassName, 
		String userOptionsServiceClassName, String userOptionsViewJsClassName, String userOptionsModelJsClassName, 
		Boolean hidden, 
		String company, String companyEmail, String companyWebSite, String supportEmail
		) throws Exception {
		super();
		
		if(StringUtils.isEmpty(javaPackage)) throw new Exception("Invalid value for property [package]");
		this.javaPackage = javaPackage.toLowerCase();
		this.id = this.javaPackage;
		if(StringUtils.isEmpty(jsPackage)) throw new Exception("Invalid value for property [jsPackage]");
		this.jsPackage = jsPackage; // Lowercase allowed!
		if(StringUtils.isEmpty(shortName)) throw new Exception("Invalid value for property [shortName]");
		this.xid = shortName;
		
		if(version.isUndefined()) throw new Exception("Invalid value for property [version]");
		this.version = version;
		if(!StringUtils.isEmpty(buildDate)) this.buildDate = buildDate;
		
		//TODO: Enable check or not?
		//boolean noclass = StringUtils.isEmpty(className) && StringUtils.isEmpty(publicClassName) & StringUtils.isEmpty(deamonClassName);
		//if(noclass) throw new Exception("You need to fill at least a service class");
		if(!StringUtils.isEmpty(serviceClassName)) {
			this.serviceClassName = LangUtils.buildClassName(this.javaPackage, serviceClassName);
			this.serviceJsClassName = LangUtils.buildClassName(this.jsPackage, StringUtils.defaultIfEmpty(serviceJsClassName, serviceClassName));
		}
		if(!StringUtils.isEmpty(publicServiceClassName)) {
			this.publicServiceClassName = LangUtils.buildClassName(this.javaPackage, publicServiceClassName);
		}
		if(!StringUtils.isEmpty(deamonServiceClassName)) {
			this.deamonServiceClassName = LangUtils.buildClassName(this.javaPackage, deamonServiceClassName);
		}
		if(!StringUtils.isEmpty(userOptionsServiceClassName)) {
			if(StringUtils.isEmpty(userOptionsViewJsClassName)) throw new Exception("Property [userOptionsViewJsClassName] needs to be defined");
			this.userOptionsServiceClassName = LangUtils.buildClassName(this.javaPackage, userOptionsServiceClassName);
			this.userOptionsViewJsClassName = LangUtils.buildClassName(this.jsPackage, userOptionsViewJsClassName);
			this.userOptionsModelJsClassName = LangUtils.buildClassName(this.jsPackage, userOptionsModelJsClassName);
		}
		
		this.hidden = hidden;
		if(!StringUtils.isEmpty(company)) this.company = company;
		if(!StringUtils.isEmpty(companyEmail)) this.companyEmail = companyEmail;
		if(!StringUtils.isEmpty(companyWebSite)) this.companyWebSite = companyWebSite;
		if(!StringUtils.isEmpty(supportEmail)) this.supportEmail = supportEmail;
	}
	*/
	
	/**
	 * Gets specified service ID.
	 * @return The value.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Gets specified service XID (short ID).
	 * @return The value.
	 */
	public String getXId() {
		return xid;
	}
	
	/**
	 * Gets the server-side package name.
	 * (eg. com.sonicle.webtop.core)
	 * @return The value.
	 */
	public String getPackageName() {
		return javaPackage;
	}
	
	/**
	 * Converts the package name into its path representation.
	 * (eg. com.sonicle.webtop.mail -> com/sonicle/webtop/mail)
	 * @return The value.
	 */
	public String getJarPath() {
		return StringUtils.lowerCase(StringUtils.replace(getPackageName(), ".", "/"));
	}
	
	/**
	 * Gets the client-side package name.
	 * (eg. Sonicle.webtop.mail)
	 * @return The value.
	 */
	public String getJsPackageName() {
		return jsPackage;
	}
	
	/**
	 * Converts the js package name into its path representation.
	 * (eg. Sonicle.webtop.mail -> sonicle/webtop/mail)
	 * @return The value.
	 */
	public String getJsPath() {
		return StringUtils.lowerCase(StringUtils.replace(getJsPackageName(), ".", "/"));
	}
	
	public ServiceVersion getVersion() {
		return version;
	}
	
	public ServiceVersion getOldVersion() {
		return oldVersion;
	}
	
	public void setOldVersion(ServiceVersion value) {
		oldVersion = value;
	}
	
	public String getBuildDate() {
		return buildDate;
	}
	
	/**
	 * Gets the class name of server-side service implementation.
	 * (eg. com.sonicle.webtop.core.CoreService)
	 * @return The value.
	 */
	public String getServiceClassName() {
		return serviceClassName;
	}
	
	public String getUserOptionsServiceClassName() {
		return userOptionsServiceClassName;
	}
	
	/**
	 * Gets the class name of server-side public service implementation.
	 * (eg. com.sonicle.webtop.core.CorePublicService)
	 * @return The value.
	 */
	public String getPublicServiceClassName() {
		return publicServiceClassName;
	}
	
	/**
	 * Gets the class name of server-side deamon service implementation.
	 * (eg. com.sonicle.webtop.core.CoreDeamonService)
	 * @return The value.
	 */
	public String getDeamonServiceClassName() {
		return deamonServiceClassName;
	}
	
	/**
	 * Gets the class name of client-side service implementation.
	 * (eg. Sonicle.webtop.mail.MailService)
	 * @param full True to include js package.
	 * @return The value.
	 */
	public String getServiceJsClassName(boolean full) {
		return (full) ? LangUtils.buildClassName(jsPackage, serviceJsClassName) : serviceJsClassName;
	}
	
	public String getClientOptionsModelJsClassName(boolean full) {
		return (full) ? LangUtils.buildClassName(jsPackage, clientOptionsModelJsClassName) : clientOptionsModelJsClassName;
	}
	
	public String getUserOptionsViewJsClassName(boolean full) {
		return (full) ? LangUtils.buildClassName(jsPackage, userOptionsViewJsClassName) : userOptionsViewJsClassName;
	}
	
	public String getUserOptionsModelJsClassName(boolean full) {
		return (full) ? LangUtils.buildClassName(jsPackage, userOptionsModelJsClassName) : userOptionsModelJsClassName;
	}
	
	public String getLocaleJsClassName(Locale locale, boolean full) {
		String cn = MessageFormat.format("Locale_{0}", locale.toString());
		return (full) ? LangUtils.buildClassName(jsPackage, cn) : cn;
	}
	
	public String getJsLocaleClassName(Locale locale) {
		return MessageFormat.format("{0}.Locale_{1}", getJsPackageName(), locale.toString());
	}
	
	/**
	 * 
	 * @return 
	 */
	public String getJsBaseUrl() {
		return MessageFormat.format("resources/{0}", getId());
	}
	
	public String getCompany() {
		return company;
	}
	
	public String getCompanyEmail() {
		return companyEmail;
	}
	
	public String getCompanyWebSite() {
		return companyWebSite;
	}
	
	public String getSupportEmail() {
		return supportEmail;
	}
	
	public String getDataSourceName() {
		return dataSourceName;
	}
	
	public String[] getInitCheckTables() {
		return initCheckTables;
	}
}
