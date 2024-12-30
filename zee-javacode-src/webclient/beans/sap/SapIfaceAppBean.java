package guide.webclient.beans.sap;

import guide.iface.sap.SapHeader;

import java.rmi.RemoteException;

import org.json.JSONException;

import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;

public class SapIfaceAppBean extends AppBean{

	public void sendSap() throws RemoteException, MXException {
		MboRemote mbo = this.getMbo();
		if (mbo.toBeSaved()) {
			Object[] obj = { "温馨提示：请先保存后，再点击库存盘点按钮！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}
		String status = mbo.getString("sapstatus");
		if (status != null && !status.equalsIgnoreCase("")) {
			throw new MXApplicationException("guide", "1039");
		}
		try {
			((SapHeader) mbo).dataToSap();
			this.app.getAppBean().save();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void viewSap() throws RemoteException, MXException {
		MboRemote mbo = this.getMbo();
		if (mbo.toBeSaved()) {
			Object[] obj = { "温馨提示：请先保存后，再点击库存盘点按钮！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}
		try {
			String message = ((SapHeader) mbo).getData();
			this.app.getAppBean().save();
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", message, 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
