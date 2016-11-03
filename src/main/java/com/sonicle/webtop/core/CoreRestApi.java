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

import com.sonicle.commons.web.json.JsonResult;
import com.sonicle.commons.web.json.MapItem;
import com.sonicle.commons.web.json.RestJsonResult;
import com.sonicle.webtop.core.admin.CoreAdminManager;
import com.sonicle.webtop.core.app.RunContext;
import com.sonicle.webtop.core.app.WT;
import com.sonicle.webtop.core.bol.ODomain;
import com.sonicle.webtop.core.bol.js.JsSimple;
import com.sonicle.webtop.core.bol.model.SessionInfo;
import com.sonicle.webtop.core.sdk.BaseRestApi;
import com.sonicle.webtop.core.sdk.WTException;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 *
 * @author malbinola
 */
@Path("com.sonicle.webtop.core")
public class CoreRestApi extends BaseRestApi {
	private static final Logger logger = WT.getLogger(CoreRestApi.class);
	
	public CoreRestApi() {
		super();
	}
	
	private Response ok(Object data) {
		return Response.ok(JsonResult.GSON.toJson(data)).build();
	}
	
	private Response error() {
		return error(Status.INTERNAL_SERVER_ERROR.getStatusCode(), null);
	}
	
	private Response error(int status) {
		return error(status, null);
	}
	
	private Response error(int status, String message) {
		if(StringUtils.isBlank(message)) {
			return Response.status(status)
					.entity(new MapItem())
					.type(MediaType.APPLICATION_JSON)
					.build();
		} else {
			return Response.status(status)
					.entity(new MapItem().add("message", message))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
	}
	
	private CoreManager getManager() {
		return WT.getCoreManager();
	}
	
	@GET
	@Path("/themes")
	@Produces({MediaType.APPLICATION_JSON})
	public Response themesList() throws WTException {
		CoreManager core = getManager();
		List<JsSimple> items = core.listThemes();
		return ok(items);
	}
	
	@GET
	@Path("/layouts")
	@Produces({MediaType.APPLICATION_JSON})
	public Response layoutsList() throws WTException {
		CoreManager core = getManager();
		List<JsSimple> items = core.listLayouts();
		return ok(items);
	}
	
	@GET
	@Path("/lafs")
	@Produces({MediaType.APPLICATION_JSON})
	public Response lafsList() throws WTException {
		CoreManager core = getManager();
		List<JsSimple> items = core.listLAFs();
		return ok(items);
	}
	
	/*
	@GET
	@Path("/sessions")
	@Produces({MediaType.APPLICATION_JSON})
	public Response listSessions() throws WTException {
		CoreManager core = getManager();
		List<SessionInfo> items = core.listSessions();
		return ok(items);
	}
	*/
	
	/*
	@DELETE
	@Path("/sessions/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteSession(@PathParam("id")String id) throws WTException {
		CoreManager core = getManager();
		core.invalidateSession(id);
		return ok(new MapItem());
	}
	*/
	
	@GET
	@Path("/domains")
	@Produces({MediaType.APPLICATION_JSON})
	public Response listDomains() throws WTException {
		CoreManager core = getManager();
		List<ODomain> items = core.listDomains(true);
		return ok(items);
	}
	
	@GET
	@Path("/me/devicesSynchronization/enabled")
	@Produces({MediaType.APPLICATION_JSON})
	public Response isDeviceSynchronizationEnabled() throws WTException {
		boolean bool = RunContext.isPermitted(SERVICE_ID, "DEVICES_SYNC");
		return ok(new MapItem().add("response", bool));
	}
}
