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
Ext.define('Sonicle.webtop.core.app.AppPrivate', {
	extend: 'Sonicle.webtop.core.app.AppBase',
	requires: [
		'Sonicle.String',
		'Sonicle.Date',
		'Sonicle.PageMgr',
		'Sonicle.URLMgr',
		'Sonicle.PrintMgr',
		'Sonicle.ActivityMonitor',
		'Sonicle.DesktopNotificationMgr',
		'Sonicle.WebSocketManager',
		'Sonicle.WebSocket',
		'Sonicle.upload.Uploader',
		'Sonicle.data.proxy.Ajax',
		'Sonicle.data.identifier.NegativeString',
		'Sonicle.form.field.VTypes',
		'Sonicle.plugin.EnterKeyPlugin',
		'Sonicle.plugin.FieldTooltip',
		
		'Sonicle.webtop.core.ux.data.BaseModel',
		'Sonicle.webtop.core.ux.data.EmptyModel',
		'Sonicle.webtop.core.ux.data.SimpleModel',
		'Sonicle.webtop.core.ux.data.ArrayStore',
		'Sonicle.webtop.core.ux.panel.Panel',
		'Sonicle.webtop.core.ux.panel.Fields',
		'Sonicle.webtop.core.ux.panel.Form',
		'Sonicle.webtop.core.ux.panel.Tab',
		
		'Sonicle.webtop.core.app.WT',
		'Sonicle.webtop.core.app.FileTypes',
		'Sonicle.webtop.core.app.Factory',
		'Sonicle.webtop.core.app.Util',
		'Sonicle.webtop.core.app.Log',
		'Sonicle.webtop.core.app.ThemeMgr',
		
		'Sonicle.webtop.core.app.WTPrivate',
		'Sonicle.webtop.core.app.ServiceDescriptor',
		'Sonicle.webtop.core.app.ComManager',
		'Sonicle.webtop.core.sdk.Service'
	
	].concat(WTS.appRequires || []),
	views: [
		Ext.String.format('WTA.view.main.{0}', WTS.layoutClassName)
	],
	refs: {
		viewport: 'viewport'
	},
	
	currentService: null,
	
	kaTask: null,
	seTask: null,
	
	constructor: function() {
		var me = this;
		WT.app = me;
		me.callParent(arguments);
	},
	
	init: function() {
		WTA.Log.debug('application:init');
		Ext.tip.QuickTipManager.init();
		Ext.setGlyphFontFamily('FontAwesome');
		Ext.themeName = WTS.servicesVars[0].theme;
		Ext.getDoc().on('contextmenu', function(e) {
			e.preventDefault(); // Disable browser context if no context menu is defined
		});
		
		// Inits state provider
		if(Ext.util.LocalStorage.supported) {
			Ext.state.Manager.setProvider(new Ext.state.LocalStorageProvider());
		} else {
			Ext.state.Manager.setProvider(new Ext.state.CookieProvider({
				expires: new Date(Ext.Date.now() + (1000*60*60*24*90)) // 90 days
			}));
		}
		WTA.FileTypes.init(WTS.fileTypes);
	},
	
	launch: function() {
		var me = this, desc;
		
		// Loads service descriptors from startup object
		Ext.each(WTS.services, function(obj) {
			desc = Ext.create('WTA.ServiceDescriptor', {
				index: obj.index,
				maintenance: obj.maintenance,
				id: obj.id,
				xid: obj.xid,
				ns: obj.ns,
				path: obj.path,
				version: obj.version,
				build: obj.build,
				serviceClassName: obj.serviceClassName,
				serviceVarsClassName: obj.serviceVarsClassName,
				localeClassName: obj.localeClassName,
				userOptions: obj.userOptions,
				name: obj.name,
				description: obj.description,
				company: obj.company
			});
			
			me.locales.add(obj.id, Ext.create(obj.localeClassName));
			me.services.add(desc);
		}, me);
		
		//TODO: portare il metodo onRequiresLoaded direttamente qui!
		me.onRequiresLoaded.call(me);
	},
	
	onRequiresLoaded: function() {
		var me = this,
				def = null, vp, vpc;
		
		// Instantiates core service
		var cdesc = me.services.getAt(0);
		cdesc.getInstance();
		cdesc.initService();
		
		// Creates main viewport
		vp = me.viewport = me.getView(me.views[0]).create({
			servicesCount: me.services.count()-1 //TODO: calcolare il numero di servizi visibili
		});
		vpc = me.viewport.getController();
		
		// Inits loaded services and activate the default one
		Ext.each(me.getDescriptors(), function(desc) {
			if(!desc.getMaintenance()) {
				if(desc.initService()) {
					var svc = desc.getInstance();
					vp.addServiceButton(desc);
					if(svc.hasNewActions()) vp.addServiceNewActions(svc.getNewActions());
					// Saves first succesfully activated service for later displaying default
					if(def === null) def = desc.getId();
				}
			} else {
				//TODO: show grayed button
			}
		});
		
		// Sets default service
		if(WTS.defaultService) {
			var desc = me.getDescriptor(WTS.defaultService);
			if(desc.isInited()) def = WTS.defaultService;
		}
		if(def !== null) me.activateService(def);
		
		// If necessary, show whatsnew
		if(WT.getVar('isWhatsnewNeeded')) {
			vpc.showWhatsnew(false);
		}
		
		// Inits messages (webSocket/ServerEvents)
		WTA.ComManager.setConnectionWarnMsg(WT.res('warn.connectionlost'));
		WTA.ComManager.on('receive', function(s,messages) {
			Ext.each(messages, function(msg) {
				if (msg && msg.service) {
					var svc = me.getService(msg.service);
					if(svc) svc.handleMessage(msg);
				}
			});
		});
		WTA.ComManager.connect();
		
		Sonicle.ActivityMonitor.on('change', function(s, idle) {
			console.log('ActivityMonitor: ' + (idle ? 'user is idle' : 'user is working'));
		});
		Sonicle.ActivityMonitor.start();
		
		me.hideLoadingLayer();
	},
	
	/**
	 * Activates (shows) specified service.
	 * @param {String} id The service ID.
	 */
	activateService: function(id) {
		var me = this,
				vpc = me.getViewport().getController(),
				inst = me.getService(id);
				
		if (!inst) return;
		vpc.addServiceCmps(inst);
		me.currentService = id;
		if (vpc.activateService(inst)) {
			inst.activationCount++;
			inst.fireEvent('activate');
		}
	},
	
	hideLoadingLayer: function() {
		Ext.fly('wt-loading').animate({
			to: {opacity: 0},
			duration: 200,
			remove: true
		});
		Ext.fly('wt-loading-mask').animate({
			to: {opacity: 0.4},
			easing: 'bounceOut',
			duration: 1000,
			remove: true
		});
		
		/*
		var el = Ext.get('wt-loading'),
				box = el.getBox();
		el.animate({
			to: {opacity: 0},
			duration: 2500,
			remove: true
		});
		Ext.get('wt-loading-mask').animate({
			to: {
				x: box.x,
				y: box.y,
				width: box.width,
				height: box.height,
				opacity: 0
			},
			easing: 'bounceOut',
			duration: 5000,
			remove: true
		});
		*/
	
		/*
		     var loadingMask = Ext.get('loading-mask');
     var loading = Ext.get('loading');

     //  Hide loading message
     loading.fadeOut({ duration: 0.2, remove: true });

     //  Hide loading mask
     loadingMask.setOpacity(0.9);
     loadingMask.shift({
          xy: loading.getXY(),
          width: loading.getWidth(),
          height: loading.getHeight(),
          remove: true,
          duration: 1,
          opacity: 0.1,
          easing: 'bounceOut'
     });
		*/
	},
	
	/**
	 * Returns the Service API interface.
	 * @param {String} id The service ID.
	 * @returns {Object} The service API object or null if service is not valid.
	 */
	getServiceApi: function(id) {
		var svc = this.getService(id);
		return svc ? svc.getApiInstance() : null;
	}
});

Ext.override(Ext.data.proxy.Server, {
	constructor: function(cfg) {
		this.callOverridden([cfg]);
		this.addListener('exception', function(s,resp,op) {
			if(resp.status === 401) WT.reload();
		});
	}
});

Ext.override(Ext.menu.Item, {
	onClick: function(e) {
		e.menuData = WT.getContextMenuData();
		return this.callParent([e]);
	}
});
