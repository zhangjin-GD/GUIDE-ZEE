package guide.app.pr;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurWonum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldWONum extends FldPurWonum {

	public UDFldWONum(MboValue mbv) throws MXException {
		super(mbv);
		// TODO Auto-generated constructor stub
	}

	public void action() throws MXException, RemoteException {
		/**
		 * ZEE-如果工单并且工单有设备，则根据设备代入设备costcenter DJY 2024-04-08 10:39:13
		 */
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = owner.getString("udcompany");
		String wonum = "";
		String assetnum = "";
		String costcenter = "";
		wonum = mbo.getString("refwo");
		MboSetRemote woSet = MXServer.getMXServer().getMboSet("WORKORDER",
				MXServer.getMXServer().getSystemUserInfo());
		woSet.setWhere(" udcompany='ZEE' and wonum='" + wonum + "' ");
		woSet.reset();
		if ("ZEE".equalsIgnoreCase(udcompany)) {
			if (!woSet.isEmpty() && woSet.count() > 0) {
				MboRemote wo = woSet.getMbo(0);
				assetnum = wo.getString("assetnum");
				MboSetRemote assetSet = MXServer.getMXServer().getMboSet(
						"ASSET", MXServer.getMXServer().getSystemUserInfo());
				assetSet.setWhere(" udcompany='ZEE' and assetnum='" + assetnum
						+ "' ");
				assetSet.reset();
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					MboRemote asset = assetSet.getMbo(0);
					costcenter = asset.getString("udcostcenter");
				}
				assetSet.close();
			}
			mbo.setValue("udcostcenterasset", costcenter, 11L);
		}

	}
}
