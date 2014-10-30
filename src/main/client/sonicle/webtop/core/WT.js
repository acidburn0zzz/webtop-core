Ext.define('Sonicle.webtop.core.WT', {
	singleton: true,
	alternateClassName: 'WT',
	
	statics: {
		ID: 'com.sonicle.webtop.core',
		XID: 'wt',
		NS: 'Sonicle.webtop.core'
	},
	
	strings: null,
	loadedCss: null,
	
	/**
	 * Returns the application.
	 * This is shorthand reference to Sonicle.webtop.core.getApplication().
	 * @returns {Sonicle.webtop.core.Application} The resulting object.
	 */
	getApp: function() {
		return Sonicle.webtop.core.getApplication();
	},
	
	preNs: function(ns, cn) {
		if(arguments.length === 1) {
			//return 'Sonicle.webtop.core.'+cn;
			return WT.NS + '.' + cn;
		} else {
			return ns + '.' + cn;
		}
	},
	
	/**
	 * Returns a string resource.
	 * @param {String} svc The service id.
	 * @param {String} key The resource key.
	 * @returns {String} The value.
	 */
	res: function(svc, key) {
		if(arguments.length === 1) {
			key = svc;
			svc = WT.NS;
		}
		if(svc === WT.NS) {
			return WT.strings[key];
		} else {
			var inst = WT.getApp().getService();
			if(inst === null) return null;
			return inst.res(key);
		}
	},
	
	proxy: function(svc, act, rootp) {
		return {
			type: 'ajax',
			url: 'service-request',
			extraParams: {
				service: svc,
				action: act
			},
			reader: {
				type: 'json',
				rootProperty: rootp,
				messageProperty: 'message'
			}
		};
	},
	
	apiProxy: function(svc, act, rootp) {
		return {
			type: 'ajax',
			api: {
				create: 'service-request?crud=create',
				read: 'service-request?crud=read',
				update: 'service-request?crud=update',
				destroy: 'service-request?crud=delete'
			},
			extraParams: {
				service: svc,
				action: act
			},
			reader: {
				type: 'json',
				rootProperty: rootp,
				messageProperty: 'message'
			}
		};
	},
	
	componentLoader: function(svc, act, opts) {
		if(!opts) opts = {};
		return {
			url: 'service-request',
			params: Ext.applyIf({
				service: svc,
				action: act
			}, opts.params || {}),
			contentType: 'html',
			loadMask: true
		};
	},
	
	ajaxReq: function(svc, act, opts) {
		var me = this;
		if(!opts) opts = {};
		var fn = opts.callback, scope = opts.scope;
		var options = {
			url: 'service-request',
			method: 'POST',
			params: Ext.applyIf({
				service: svc,
				action: act
			}, opts.params || {}),
			headers: {"Content-Type": "application/x-www-form-urlencoded; charset=utf-8"},
			success: function(resp, opts) {
				var obj = Ext.decode(resp.responseText);
				Ext.callback(fn, scope || me, [obj.success, obj, opts]);
			},
			failure: function(resp, opts) {
				Ext.callback(fn, scope || me, [false, null, opts]);
			},
			scope: me
		};
		if(opts.timeout) Ext.apply(options, {timeout: opts.timeout});
		Ext.Ajax.request(options);
	},
	
	/**
	 * Loads a CSS file by adding in the page a new link element.
	 * @param {String} url The URL from which to load the CSS.
	 */
	loadCss: function(url) {
		var me = this;
		if(!me.loadedCss) me.loadedCss = {};
		if(!me.loadedCss[url]) {
			var doc = window.document;
			var link = doc.createElement('link');
			link.rel = 'stylesheet';
			link.type = 'text/css';
			link.href = url;
			doc.getElementsByTagName('head')[0].appendChild(link);
			me.loadedCss[url] = url;
		}
	},
	
	/**
	 * Asynchronously loads the specified script URL and calls the supplied 
	 * callbacks. A success flag is passed to callback function in order to
	 * determine operation result.
	 * @param {String} url The URL from which to load the script.
	 * @param {Function} cb The callback to call.
	 * @param {Object} scope The scope (this) for the supplied callbacks.
	 */
	loadScriptAsync: function(url,cb,scope) {
		var me = this;
		Ext.Loader.loadScript({
			url: 'resources/'+url,
			onLoad: function() {
				Ext.callback(cb, scope || me, [true]);
			},
			onError: function() {
				Ext.callback(cb, scope || me, [false]);
			},
			scope: me
		});
	},
	
	/**
	 * Decodes (parses) a properties text to an object.
	 * @param {String} text The properties string.
	 * @returns {Object} The resulting object.
	 */
	decodeProps: function(text) {
		var i1, i2 = -1, line, ieq;
		var hm = {}, key, val;
		var done = false;
		while(!done) {
			i1 = i2+1;
			i2 = text.indexOf('\n', i1);
			line = null;
			if(i2 < 0) {
				if(i1 < text.length) line = text.substring(i1, text.length);
				done = true;
			} else {
				line = text.substring(i1, i2);
			}
			if(line) {
				ieq = line.indexOf('=');
				if(ieq < 0) continue;
				key = line.substring(0, ieq);
				val = line.substring(ieq+1);
				hm[key] = val;
			}
		}
		return hm;
	},
	
	info: function(msg, tit) {
		Ext.Msg.show({
			title: tit || WT.res('info'),
			message: msg,
			buttons: Ext.MessageBox.OK,
			icon: Ext.MessageBox.INFO
		});
	},
	
	warn: function(msg, tit) {
		Ext.Msg.show({
			title: tit || WT.res('warning'),
			message: msg,
			buttons: Ext.MessageBox.OK,
			icon: Ext.MessageBox.WARNING
		});
	},
	
	error: function(msg, tit) {
		Ext.Msg.show({
			title: tit || WT.res('error'),
			message: msg,
			buttons: Ext.MessageBox.OK,
			icon: Ext.MessageBox.ERROR
		});
	},
	
	wsMsg: function(service, action, config) {
		return Ext.JSON.encode(Ext.apply(config||{},{ service: service, action: action }));
	}
});
