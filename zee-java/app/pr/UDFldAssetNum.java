package guide.app.pr;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurAssetnum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldAssetNum extends FldPurAssetnum{

	public UDFldAssetNum(MboValue mbv) throws MXException {
		super(mbv);
	}
	public void action() throws MXException, RemoteException{
		/**
		 * ZEE-如果物资，则根据设备所属部门代入costcenter
		 * DJY 2024-04-01 13:39:13
		 */
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = owner.getString("udcompany");
		String assetnum = "";
		String costcenter = "";
		String deptnum = "";
		MboSetRemote assetSet = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
		assetSet.setWhere(" udcompany='ZEE' and assetnum='"+mbo.getString("assetnum")+"' ");
		assetSet.reset();
		if("ZEE".equalsIgnoreCase(udcompany)){
			if (!assetSet.isEmpty() && assetSet.count() > 0) {
				MboRemote asset = assetSet.getMbo(0);
				assetnum = asset.getString("assetnum");
				costcenter = asset.getString("udcostcenter");
			}
			assetSet.close();
			mbo.setValue("udcostcenterasset", costcenter, 11L);
		}
	}

}
