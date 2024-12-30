package guide.webclient.beans.assetcat;

import java.rmi.RemoteException;
import java.util.Hashtable;

import psdi.app.assetcatalog.AssetCatalogServiceRemote;
import psdi.app.assetcatalog.ClassStructureRemote;
import psdi.app.assetcatalog.ClassStructureSetRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.util.MXSession;
import psdi.webclient.beans.common.TreeControlBean;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.ComponentInstance;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.Utility;
import psdi.webclient.system.controller.WebClientEvent;

public class CustAssociateSpecBean extends TreeControlBean {
	private DataBean originalBean = null;
	private ClassStructureSetRemote classStructSet = null;
	private boolean isfrominitialize = false;

	public void initialize() throws MXException, RemoteException {
		this.isfrominitialize = true;
		super.initialize();
		ControlInstance originalControl = this.creatingEvent.getSourceControlInstance();
		this.originalBean = this.clientSession.getDataBean(originalControl.getProperty("datasrc"));
		this.classStructSet = (ClassStructureSetRemote) this.getMboSet();

		this.classStructSet.setOriginatingObject(this.originalBean.getMbo());
		if (this.classStructSet.getApp() == null) {
			String appName = this.app.getId();
			if (appName != null) {
				if ("uditemreq".equalsIgnoreCase(appName)) {
					MboRemote lineMbo = this.originalBean.getMbo();
					String itemtype = lineMbo.getString("itemtype");
					this.classStructSet.setAppWhere("uditemtype ='" + itemtype + "'");
				}
				this.classStructSet.setIsLookup(true);
				this.classStructSet.setApp(appName.toUpperCase());
			}
		}
	}

	public int selectrecord() throws MXException {
		WebClientEvent event = this.sessionContext.getCurrentEvent();

		try {
			super.selectrecord();
			this.updateOriginatingRecord();
		} catch (MXException var3) {
			Utility.sendEvent(
					new WebClientEvent("dialogclose", this.app.getCurrentPageId(), (Object) null, this.sessionContext));
			Utility.showMessageBox(event, var3);
		} catch (RemoteException var4) {
			Utility.sendEvent(
					new WebClientEvent("dialogclose", this.app.getCurrentPageId(), (Object) null, this.sessionContext));
			Utility.showMessageBox(event, var4);
		}

		return 1;
	}

	protected void updateOriginatingRecord() throws MXException, RemoteException {
		String uniqueIdSelected = this.sessionContext.getCurrentEvent().getValueString();
		String originalAttr = null;
		MboSetRemote originalSet = null;
		Object eventVal = this.creatingEvent.getValue();
		Hashtable popupInfo = null;
		boolean expBuilder = false;
		if (eventVal != null && eventVal instanceof Hashtable) {
			popupInfo = (Hashtable) eventVal;
			if (popupInfo.containsKey("c_datasrc")) {
				originalSet = this.clientSession.getDataBean(popupInfo.get("c_datasrc").toString()).getMboSet();
			}

			if (popupInfo.containsKey("c_attribute")) {
				originalAttr = popupInfo.get("c_attribute").toString();
			}

			expBuilder = true;
		} else {
			ComponentInstance compInst = this.creatingEvent.getSourceComponentInstance();
			originalAttr = compInst.getProperty("dataattribute");
			originalSet = this.originalBean.getMboSet();
		}

		MboRemote selectedClassMbo = this.getMbo();
		if (selectedClassMbo == null) {
			MXSession mxs = this.getMXSession();
			AssetCatalogServiceRemote assetCatService = (AssetCatalogServiceRemote) mxs.lookup("ASSETCATALOG");
			selectedClassMbo = assetCatService.getClassStructure(mxs.getUserInfo(), uniqueIdSelected);
		}

		if (selectedClassMbo.getThisMboSet().count() == 1 && this.classStructSet.hasAFakeTreeNode()) {
			String objectName = originalSet.getName().toUpperCase();
			MboRemote useWith = ((ClassStructureRemote) selectedClassMbo).getUseWith(objectName);
			if (useWith == null) {
				Utility.sendEvent(new WebClientEvent("dialogclose", this.app.getCurrentPageId(), (Object) null,
						this.sessionContext));
				return;
			}
		}

		MboRemote originalRecord = originalSet.getMbo();
		if (originalRecord == null) {
			originalRecord = originalSet.getMbo(0);
		}

		if (originalRecord != null && selectedClassMbo != null) {
			String classAttr = null;
			String hierarchyAttr = null;
			if (originalAttr.equalsIgnoreCase("CLASSSTRUCTURE.HIERARCHYPATH")) {
				classAttr = "classstructureid";
				hierarchyAttr = "CLASSSTRUCTURE.HIERARCHYPATH";
			} else if (!expBuilder) {
				int index = originalAttr.indexOf(".");
				classAttr = originalAttr.substring(0, index);
				if (classAttr.equalsIgnoreCase("CLASSSTRUCTURE")) {
					classAttr = "classstructureid";
				}

				hierarchyAttr = originalAttr;
			} else {
				hierarchyAttr = originalAttr;
			}

			this.originalBean.setValue(classAttr, selectedClassMbo.getString("classstructureid"));
			String hierarchypath = originalRecord.getString(hierarchyAttr);
			if (expBuilder) {
				this.originalBean.setValue(hierarchyAttr, hierarchypath + selectedClassMbo.getString("hierarchypath"),
						2L);
			} else {
				this.originalBean.setValue(hierarchyAttr, hierarchypath, 2L);
			}

			Utility.sendEvent(
					new WebClientEvent("dialogclose", this.app.getCurrentPageId(), (Object) null, this.sessionContext));
		}

	}

	protected synchronized boolean moveTo(int row) throws MXException, RemoteException {
		if (row == 0 && this.isfrominitialize) {
			this.isfrominitialize = false;
			return true;
		} else {
			return super.moveTo(row);
		}
	}
}
