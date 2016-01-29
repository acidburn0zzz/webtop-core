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
Ext.define('Sonicle.webtop.core.view.UserOptions', {
	alternateClassName: 'WT.view.UserOptions',
	extend: 'WT.sdk.UserOptionsView',
	requires: [
		'WT.model.Simple',
		'WT.store.OTPDelivery',
		'WT.store.Timezone',
		'WT.store.StartDay',
		'WT.store.DateFmtShort',
		'WT.store.DateFmtLong',
		'WT.store.TimeFmtShort',
		'WT.store.TimeFmtLong',
		'Sonicle.form.field.Icon',
		'Sonicle.plugin.FieldTooltip',
		'Sonicle.webtop.core.ux.PermStatusField'
	],
	
	viewModel: {
		formulas: {
			upiFieldEditable: function(get) {
				return WT.getOption('wtUpiProviderWritable') && get('record.canManageUpi');
			},
			isOTPActive: WTF.isEmptyFormula('record', 'otpDelivery', true),
			syncAlertEnabled: WTF.checkboxBind('record', 'syncAlertEnabled')
		}
	},
	
	initComponent: function() {
		var me = this, vm;
		me.callParent(arguments);
		
		vm = me.getViewModel();
		vm.setFormulas(Ext.apply(vm.getFormulas() || {}, {
			areMine: function() {
				return WT.getOption('profileId') === me.profileId;
			}
		}));
		
		me.add({
			xtype: 'wtopttabsection',
			title: WT.res('opts.main.tit'),
			items: [{
				xtype: 'textfield',
				bind: '{record.id}',
				disabled: true,
				fieldLabel: WT.res('opts.main.fld-profile.lbl'),
				width: 380
			}, {
				xtype: 'textfield',
				bind: '{record.displayName}',
				fieldLabel: WT.res('opts.main.fld-displayName.lbl'),
				width: 380,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'sospacer'
			}, {
				xtype: 'formseparator'
			}, 
			WTF.lookupCombo('id', 'desc', {
				bind: '{record.theme}',
				store: {
					autoLoad: true,
					model: 'WT.model.Simple',
					proxy: WTF.proxy(me.ID, 'LookupThemes', 'themes')
				},
				fieldLabel: WT.res('opts.main.fld-theme.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				},
				needReload: true
			}), 
			WTF.lookupCombo('id', 'desc', {
				bind: '{record.layout}',
				store: {
					autoLoad: true,
					model: 'WT.model.Simple',
					proxy: WTF.proxy(me.ID, 'LookupLayouts', 'layouts')
				},
				fieldLabel: WT.res('opts.main.fld-layout.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				},
				needReload: true
			}), 
			WTF.lookupCombo('id', 'desc', {
				bind: '{record.laf}',
				store: {
					autoLoad: true,
					model: 'WT.model.Simple',
					proxy: WTF.proxy(me.ID, 'LookupLAFs', 'lafs')
				},
				fieldLabel: WT.res('opts.main.fld-laf.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				},
				needReload: true
			})]
		}, {
			xtype: 'wtopttabsection',
			title: WT.res('opts.i18n.tit'),
			items: [WTF.localCombo('id', 'desc', {
				bind: '{record.language}',
				store: {
					autoLoad: true,
					model: 'WT.model.Simple',
					proxy: WTF.proxy(me.ID, 'LookupLanguages', 'languages')
				},
				fieldLabel: WT.res('opts.i18n.fld-language.lbl'),
				width: 340,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				},
				needLogin: true
			}), 
			WTF.localCombo('id', 'desc', {
				bind: '{record.timezone}',
				store: Ext.create('WT.store.Timezone', {
					autoLoad: true
				}),
				fieldLabel: WT.res('opts.i18n.fld-timezone.lbl'),
				width: 450,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				},
				needLogin: true
			}), {
				xtype: 'sospacer'
			}, {
				xtype: 'formseparator'
			}, 
			WTF.lookupCombo('id', 'desc', {
				bind: '{record.startDay}',
				store: Ext.create('WT.store.StartDay', {
					autoLoad: true
				}),
				fieldLabel: WT.res('opts.i18n.fld-startDay.lbl'),
				width: 280,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				},
				needReload: true
			}),
			WTF.lookupCombo('id', 'desc', {
				bind: '{record.shortDateFormat}',
				store: Ext.create('WT.store.DateFmtShort', {
					autoLoad: true
				}),
				fieldLabel: WT.res('opts.i18n.fld-shortDateFormat.lbl'),
				width: 280,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				},
				needReload: true
			}),
			WTF.lookupCombo('id', 'desc', {
				bind: '{record.longDateFormat}',
				store: Ext.create('WT.store.DateFmtLong', {
					autoLoad: true
				}),
				fieldLabel: WT.res('opts.i18n.fld-longDateFormat.lbl'),
				width: 280,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				},
				needReload: true
			}),
			WTF.lookupCombo('id', 'desc', {
				bind: '{record.shortTimeFormat}',
				store: Ext.create('WT.store.TimeFmtShort', {
					autoLoad: true
				}),
				fieldLabel: WT.res('opts.i18n.fld-shortTimeFormat.lbl'),
				width: 280,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				},
				needReload: true
			}),
			WTF.lookupCombo('id', 'desc', {
				bind: '{record.longTimeFormat}',
				store: Ext.create('WT.store.TimeFmtLong', {
					autoLoad: true
				}),
				fieldLabel: WT.res('opts.i18n.fld-longTimeFormat.lbl'),
				width: 280,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				},
				needReload: true
			})]
		}, {
			xtype: 'wtopttabsection',
			title: WT.res('opts.upi.tit'),
			items: [{
				xtype: 'wtpermstatusfield',
				bind: '{record.canManageUpi}',
				fieldLabel: WT.res('opts.upi.canwrite'),
				userText: me.profileId
			}, {
				xtype: 'sospacer'
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiTitle}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-title.lbl'),
				width: 250,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiFirstName}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-firstName.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiLastName}',
					disabled: '{!upiFieldEditable}'
				},
				width: 300,
				fieldLabel: WT.res('opts.upi.fld-lastName.lbl'),
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiNickname}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-nickname.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, 
			WTF.remoteCombo('id', 'desc', {
				bind: {
					value: '{record.upiGender}',
					disabled: '{!upiFieldEditable}'
				},
				autoLoadOnValue: true,
				store: Ext.create('Sonicle.webtop.core.store.Gender'),
				triggers: {
					clear: WTF.clearTrigger()
				},
				fieldLabel: WT.res('opts.upi.fld-gender.lbl'),
				width: 250,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}), {
				xtype: 'textfield',
				bind: {
					value: '{record.upiEmail}',
					disabled: '{!upiFieldEditable}'
				},
				width: 400,
				fieldLabel: WT.res('opts.upi.fld-email.lbl'),
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiMobile}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-mobile.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiTelephone}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-telephone.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiFax}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-fax.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiPager}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-pager.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiAddress}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-address.lbl'),
				width: 400,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiCity}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-city.lbl'),
				width: 400,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiPostalCode}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-postalCode.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiState}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-state.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiCountry}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-country.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiCompany}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-company.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiFunction}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-function.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiCustom1}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-custom1.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiCustom2}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-custom2.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}, {
				xtype: 'textfield',
				bind: {
					value: '{record.upiCustom3}',
					disabled: '{!upiFieldEditable}'
				},
				fieldLabel: WT.res('opts.upi.fld-custom3.lbl'),
				width: 300,
				listeners: {
					blur: {
						fn: me.onBlurAutoSave,
						scope: me
					}
				}
			}]
		}, {
			xtype: 'wtopttabsection',
			title: WT.res('opts.otp.tit'),
			disabled: WT.getOption('wtOtpEnabled'),
			items: [{
				xtype: 'container',
				layout: 'form',
				items: [{
					xtype : 'fieldcontainer',
					layout: 'hbox',
					fieldLabel: WT.res('opts.otp.fld-delivery.lbl'),
					items: [
					WTF.lookupCombo('id', 'desc', {
						bind: '{record.otpDelivery}',
						store: Ext.create('WT.store.OTPDelivery', {
							autoLoad: true
						}),
						readOnly: true,
						emptyText: WT.res('word.none.female'),
						width: 250
					}), {
						xtype: 'sospacer',
						vertical: false
					}, {
						xtype: 'button',
						bind: {
							hidden: '{record.otpEnabled}'
						},
						text: WT.res('act-activate.lbl'),
						menu: [{
							itemId: 'email',
							text: WT.res('store.otpdelivery.email'),
							handler: function() {
								me.activateOTP('email');
							},
							scope: me
						}, {
							itemId: 'googleauth',
							text: WT.res('store.otpdelivery.googleauth'),
							handler: function() {
								me.activateOTP('googleauth');
							},
							scope: me
						}]
					}, {
						xtype: 'button',
						bind: {
							hidden: '{!record.otpEnabled}'
						},
						text: WT.res('act-deactivate.lbl'),
						handler: function() {
							me.deactivateOTP();
						},
						scope: me
					}]
				}]
			}, {
				xtype: 'container',
				reference: 'delivery',
				layout: 'card',
				items: [{
					xtype: 'container',
					itemId: 'none'
				}, {
					xtype: 'container',
					itemId: 'googleauth',
					items: [{
						xtype: 'component',
						padding: '0 5 0 5',
						html: WT.res('opts.otp.googleauth.html')
					}]
				}, {
					xtype: 'container',
					itemId: 'email',
					items: [{
						xtype: 'component',
						padding: '0 5 0 5',
						html: WT.res('opts.otp.email.html')
					}, {
						xtype: 'container',
						layout: 'form',
						items: [{
							xtype: 'displayfield',
							bind: '{record.otpEmailAddress}',
							fieldLabel: WT.res('otp.setup.email.fld-emailaddress.lbl')
						}]
					}]
				}]
			}, {
				xtype: 'container',
				reference: 'thisdevice',
				layout: 'card',
				items: [{
					xtype: 'container',
					itemId: 'trusted',
					layout: 'form',
					items: [{
						xtype: 'fieldset',
						layout: 'form',
						items: [{
							xtype: 'component',
							html: WT.res('opts.otp.thisdevice.trusted.html')
						}, {
							xtype: 'sospacer'
						}, {
							xtype: 'button',
							bind: {
								disabled: '{!areMine}'
							},
							text: WT.res('opts.otp.btn-untrustthis.lbl'),
							handler: function() {
								me.untrustThisOTP();
							}
						}]
					}]
				}, {
					xtype: 'container',
					itemId: 'nottrusted',
					layout: 'form',
					items: [{
						xtype: 'fieldset',
						title: WT.res('opts.otp.thisdevice.nottrusted.tit'),
						items: [{
							xtype: 'component',
							html: WT.res('opts.otp.thisdevice.nottrusted.html')
						}]
					}]
				}]
			}, {
				xtype: 'container',
				layout: 'form',
				items: [{
					xtype: 'fieldset',
					title: WT.res('opts.otp.otherdevices.tit'),
					items: [{
						xtype: 'component',
						html: WT.res('opts.otp.otherdevices.html')
					}, {
						xtype: 'sospacer'
					}, {
						xtype: 'button',
						bind: {
							disabled: '{!areMine}'
						},
						text: WT.res('opts.otp.btn-untrustother.lbl'),
						handler: function() {
							me.untrustOtherOTP();
						}
					}]
				}]
			}]
		}, {
			xtype: 'wtopttabsection',
			title: WT.res('opts.sync.tit'),
			layout: 'fit',
			items: [{
				xtype: 'wtpanel',
				layout: 'border',
				items: [{
					region: 'north',
					xtype: 'wtfieldspanel',
					bodyPadding: 0,
					height: 60,
					items: [{
						xtype: 'wtpermstatusfield',
						bind: '{record.canSyncDevices}',
						fieldLabel: WT.res('opts.sync.cansyncdevices'),
						userText: me.profileId
					}, {
						xtype: 'fieldcontainer',
						layout: 'hbox',
						defaults: {
							margin: '0 10 0 0'
						},
						items: [{
							xtype: 'checkbox',
							reference: 'fldsyncalertenabled', // Publishes field into viewmodel...
							bind: '{syncAlertEnabled}',
							margin: '0 20 0 0',
							hideEmptyLabel: true,
							boxLabel: WT.res('opts.sync.fld-syncAlertEnabled.lbl'),
							listeners: {
								blur: {
									fn: me.onBlurAutoSave,
									scope: me
								}
							}
						}, {
							xtype: 'numberfield',
							bind: {
								value: '{record.syncAlertTolerance}',
								disabled: '{!fldsyncalertenabled.checked}'
							},
							minValue: 1,
							maxValue: 30,
							fieldLabel: WT.res('opts.sync.fld-syncAlertTolerance.lbl'),
							labelWidth: 80,
							width: 140,
							listeners: {
								blur: {
									fn: me.onBlurAutoSave,
									scope: me
								}
							}
						}]
					}]
				}, {
					region: 'center',
					xtype: 'gridpanel',
					reference: 'gpsync',
					store: {
						autoSync: true,
						model: 'WT.ux.data.EmptyModel',
						proxy: WTF.apiProxy(me.ID, 'ManageSyncDevices'),
						groupField: 'user'
					},
					columns: [{
						dataIndex: 'device',
						header: WT.res('opts.sync.gp-sync.device.lbl'),
						groupable: false,
						flex: 1
					}, {
						dataIndex: 'user',
						header: WT.res('opts.sync.gp-sync.user.lbl'),
						groupable: true,
						flex: 1
					}, {
						dataIndex: 'lastSync',
						xtype: 'datecolumn',
						format: WT.getShortDateFmt() + ' ' + WT.getShortTimeFmt(),
						header: WT.res('opts.sync.gp-sync.lastSync.lbl'),
						groupable: false,
						flex: 1
					}],
					features: [{
						id: 'grouping',
						ftype: 'grouping',
						groupHeaderTpl: '{columnName}: {name} ({children.length})',
						hideGroupedHeader: true
					}],
					tbar: [
						me.addAction('showSyncDeviceInfo', {
							text: WT.res('opts.sync.details.tit'),
							tooltip: null,
							iconCls: 'wt-icon-info-xs',
							handler: function() {
								var sm = me.lref('gpsync').getSelectionModel();
								me.showSyncDeviceInfo(sm.getSelection()[0]);
							},
							disabled: true
						}),
						me.addAction('deleteSyncDevice', {
							text: WT.res('act-delete.lbl'),
							tooltip: null,
							iconCls: 'wt-icon-delete-xs',
							handler: function() {
								var sm = me.lref('gpsync').getSelectionModel();
								me.deleteSyncDevice(sm.getSelection());
							},
							disabled: true
						}),
						'-',
						me.addAction('refreshSyncDevices', {
							text: WT.res('act-refresh.lbl'),
							tooltip: null,
							iconCls: 'wt-icon-refresh-xs',
							handler: function() {
								me.refreshSyncDevices();
							}
						})
					],
					listeners: {
						selectionchange: function(s,recs) {
							me.getAction('showSyncDeviceInfo').setDisabled(!recs.length);
							me.getAction('deleteSyncDevice').setDisabled(!recs.length);
						}
					}
				}]
			}],
			listeners: {
				activate: {
					fn: function() {
						me.refreshSyncDevices();
					},
					single: true
				}
			}
		});
		vm.bind('{record.otpDelivery}', me.onOTPDeliveryChanged, me);
		vm.bind('{record.otpDeviceIsTrusted}', me.onOTPDeviceIsTrusted, me);
	},
	
	onOTPDeliveryChanged: function(val) {
		var tab = this.lref('delivery');
		tab.getLayout().setActiveItem(WTU.deflt(val, 'none'));
	},
	
	onOTPDeviceIsTrusted: function(val) {
		var me = this,
				tab = me.lref('thisdevice'), tit;
		tab.getLayout().setActiveItem(WTU.iif(val, 'trusted', 'nottrusted'));
		if(val === true) {
			tit = Ext.String.format(WT.res('opts.otp.thisdevice.trusted.tit'), me.getModel().get('otpDeviceTrustedOn'));
			tab.getComponent('trusted').getComponent(0).setTitle(tit);
		}
	},
	
	activateOTP: function(delivery) {
		var me = this,
				view = (delivery === 'email') ? 'view.OTPSetupEmail' : 'view.OTPSetupGoogleAuth',
				vw = WT.createView(me.ID, view, {
					viewCfg: {
						profileId: me.profileId
					}
				});
		
		vw.getView().on('wizardcompleted', function(s) {
			me.loadModel();
		});
		vw.show();
	},
	
	deactivateOTP: function() {
		var me = this;
		WT.confirm(WT.res('confirm.areyousure'), function(bid) {
			if(bid === 'yes') {
				WT.ajaxReq(WT.ID, 'ManageOTP', {
					params: {
						operation: 'deactivate',
						profileId: me.profileId
					},
					callback: function(success) {
						if(success) me.loadModel();
					}
				});
			}
		});
	},
	
	untrustThisOTP: function() {
		var me = this;
		WT.confirm(WT.res('confirm.areyousure'), function(bid) {
			if(bid === 'yes') {
				WT.ajaxReq(WT.ID, 'ManageOTP', {
					params: {operation: 'untrustthis'},
					callback: function(success) {
						if(success) me.loadModel();
					}
				});
			}
		});
	},
	
	untrustOtherOTP: function() {
		WT.confirm(WT.res('confirm.areyousure'), function(bid) {
			if(bid === 'yes') {
				WT.ajaxReq(WT.ID, 'ManageOTP', {
					params: {operation: 'untrustothers'}
				});
			}
		});
	},
	
	refreshSyncDevices: function() {
		var me = this,
				sto = me.lref('gpsync').getStore();
		sto.load();
	},
	
	showSyncDeviceInfo: function(rec) {
		var me = this;
		me.wait();
		WT.ajaxReq(WT.ID, 'ManageSyncDevices', {
			params: {
				crud: 'info',
				id: rec.getId()
			},
			callback: function(success, obj) {
				me.unwait();
				if(success) {
					WT.msg(obj.data, {
						title: WT.res('opts.sync.details.tit')
					});
				}
			}
		});
	},
	
	deleteSyncDevice: function(recs) {
		var me = this,
				grid = me.lref('gpsync'),
				sto = grid.getStore();
		
		WT.confirm(WT.res('confirm.delete'), function(bid) {
			if(bid === 'yes') {
				sto.remove(recs[0]);
			}
		}, me);
	}
});
