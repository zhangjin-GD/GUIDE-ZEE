package guide.app.inventory;

import guide.app.workorder.UDWO;

import java.rmi.RemoteException;

import psdi.app.inventory.FldMatUseTransAssetnum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldMatUseTransAssetnum extends FldMatUseTransAssetnum{

	public UDFldMatUseTransAssetnum(MboValue mbv) throws MXException,
			RemoteException {
		super(mbv);
		// TODO Auto-generated constructor stub
	}

	public void action() throws MXException, RemoteException {
		super.action();
		/**
		 *  ZEE- 选择设备后自动带出costcenter
		 *  DJY
		 *  2025/2/18
		 */
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = "";
		if (owner!=null && owner instanceof UDWO) {
			udcompany = owner.getString("udcompany");
		}
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			String assetnum = mbo.getString("assetnum");
			if(!assetnum.equalsIgnoreCase("") && assetnum != null){
				MboSetRemote udassetSet = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
				udassetSet.setWhere(" assetnum =  '"+assetnum+"' and udcompany = 'ZEE' ");
				udassetSet.reset();
				if(!udassetSet.isEmpty() && udassetSet.count() > 0){
					MboRemote udasset = udassetSet.getMbo(0);
					String udcostcenter = udasset.getString("udcostcenter");
					if(!udcostcenter.equalsIgnoreCase("")){
						mbo.setValue("udcostcenterasset", udcostcenter, 11L);
					}else if(udcostcenter.equalsIgnoreCase("")){
						mbo.setValue("udcostcenterasset", "", 11L);
					}
				}
				String enterby = mbo.getString("enterby");
				MboSetRemote personSet = MXServer.getMXServer().getMboSet("PERSON", MXServer.getMXServer().getSystemUserInfo());
				personSet.setWhere(" personid ='"+enterby+"' ");
				personSet.reset();
				if(!personSet.isEmpty() && personSet.count() > 0){
					MboRemote person = personSet.getMbo(0);
					String uddept = person.getString("uddept");
					MboSetRemote uddeptSet = MXServer.getMXServer().getMboSet("UDDEPT", MXServer.getMXServer().getSystemUserInfo());
					uddeptSet.setWhere(" deptnum ='"+uddept+"' ");
					uddeptSet.reset();
					if(!uddeptSet.isEmpty() && uddeptSet.count() > 0){
						MboRemote dept = uddeptSet.getMbo(0);
						String costcenter = dept.getString("costcenter");
						if(!costcenter.equalsIgnoreCase("")){
							mbo.setValue("udcostcenterzee", costcenter, 11L);
						}
					}
				}
			}else if(assetnum.equalsIgnoreCase("") || assetnum == null){
				mbo.setValue("udcostcenterasset", "", 11L);
			}
		}
	}
}
