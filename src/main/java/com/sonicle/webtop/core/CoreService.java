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
import com.sonicle.webtop.core.bol.js.JsTheme;
import com.sonicle.webtop.core.sdk.Environment;
import com.sonicle.webtop.core.sdk.Service;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

/**
 *
 * @author malbinola
 */
public class CoreService extends Service {
	
	public static final Logger logger = Service.getLogger(CoreService.class);

	@Override
	public void initialize() {
		getFullEnv().getSession().test();
		logger.debug("Mi sono inizializzato: mi chiamo {}", getName(new Locale("it_IT")));
	}

	@Override
	public void cleanup() {
		
	}
	
	public void processSetTheme(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		String theme=request.getParameter("theme");
		logger.debug("change theme to {}",theme);
		getFullEnv().getSession().setTheme(theme);
		new JsonResult().printTo(out);
	}
	
	public void processGetThemes(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		ArrayList<JsTheme> items = new ArrayList<>();
		
		// TODO: handle themes dinamically
		items.add(new JsTheme("aria", "Aria"));
		items.add(new JsTheme("classic", "Classic"));
		items.add(new JsTheme("crisp", "Crisp"));
		items.add(new JsTheme("crisp-touch", "Crisp Touch"));
		items.add(new JsTheme("gray", "Gray"));
		items.add(new JsTheme("neptune", "Neptune"));
		items.add(new JsTheme("neptune-touch", "Neptune Touch"));
		
		new JsonResult("themes", items).printTo(out);
	}
}
