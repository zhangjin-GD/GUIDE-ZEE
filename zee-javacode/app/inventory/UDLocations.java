package guide.app.inventory;

import java.rmi.RemoteException;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDLocations extends UDMbo implements MboRemote {

	public UDLocations(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			String status = getString("status");
			String[] str = { "description", "location" };
			MboSetRemote udlocation = getMboSet("UDLOCATION");
			MboSetRemote udlocations = getMboSet("UDLOCATIONS");
			if (status.equalsIgnoreCase("APPR")) {
				setFieldFlag(str, 7L, true);
				udlocation.setFlag(7L, true);
				udlocations.setFlag(7L, true);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		String status = getString("status");
		String location = getString("location");
		String createby = getString("createby");
		String createtime = getString("createtime");
		MboSetRemote udlocation = getMboSet("UDLOCATION");
		MboSetRemote udlocations = getMboSet("UDLOCATIONS");
		if (status.equalsIgnoreCase("APPR")) {
			setFieldFlag(getString("description"), 7L, true);
			setFieldFlag(getString("location"), 7L, true);
			udlocation.setFlag(7L, true);
			udlocations.setFlag(7L, true);
			MboSetRemote insertlocationsSet = getMboSet("UDLOCATIONS");// 新增
			MboSetRemote updatelocationsSet = getMboSet("UDLOCATION");// 修改
			if (!insertlocationsSet.isEmpty() && insertlocationsSet.count() > 0) {
				for (int i = 0; i < insertlocationsSet.count(); i++) {
					MboRemote insertlocations = insertlocationsSet.getMbo(i);
					MboSetRemote udbinSet = MXServer.getMXServer().getMboSet("UDBIN",
							MXServer.getMXServer().getSystemUserInfo());
					udbinSet.setWhere("location='" + location + "'");
					udbinSet.reset();
					UDBin udbin = (UDBin) udbinSet.add();
					udbin.setValue("location", location, 11L);
					udbin.setValue("binnum", insertlocations.getString("udbinnum"), 11L);
					udbin.setValue("binname", insertlocations.getString("binname"), 11L);
					udbin.setValue("createby", createby, 11L);
					udbin.setValue("createdate", createtime, 11L);
					udbinSet.save();
					udbinSet.close();
				}
				insertlocationsSet.close();
			}
			if (!updatelocationsSet.isEmpty() && updatelocationsSet.count() > 0) {
				for (int i = 0; i < updatelocationsSet.count(); i++) {
					MboRemote updatelocations = updatelocationsSet.getMbo(i);
					String binnum = updatelocations.getString("binnum");
					MboSetRemote udbinSet = MXServer.getMXServer().getMboSet("UDBIN",
							MXServer.getMXServer().getSystemUserInfo());
					udbinSet.setWhere("location='" + location + "' and binnum='" + binnum + "'");
					udbinSet.reset();
					if (!udbinSet.isEmpty() && udbinSet.count() > 0) {

						MboRemote udbin = udbinSet.getMbo(0);
						udbin.setValue("unactive", 1, 11L);
					}
					udbinSet.save();
					udbinSet.close();
					updatelocationsSet.close();
				}
			}
			udlocation.setFlag(7L, true);
			udlocations.setFlag(7L, true);
		}
		super.save();
	}
}
