package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.InvLifoFifoCost;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
/**
 *@function:ZEE-FIFO
 *@date:2023-08-15 14:44:05
 *@modify:
 */
public class UDInvLifoFifoCost extends InvLifoFifoCost {

	public UDInvLifoFifoCost(MboSet ms) throws RemoteException {
		super(ms);
	}
	
	@Override
	public void save() throws MXException, RemoteException {
		super.save();
		String refobject = getString("refobject");
		int refobjectid = getInt("refobjectid");
		if (refobject!=null && !refobject.equalsIgnoreCase("") && getString("refobjectid")!=null && !getString("refobjectid").equalsIgnoreCase("")) {
			if (refobject.equals("MATRECTRANS")) {
				MboSetRemote matrecSet = MXServer.getMXServer().getMboSet("MATRECTRANS", MXServer.getMXServer().getSystemUserInfo());
				matrecSet.setWhere(" matrectransid='"+refobjectid+"' ");
				matrecSet.reset();
				if (!matrecSet.isEmpty() && matrecSet.count() > 0) {
					MboRemote matrec = matrecSet.getMbo(0);
					double unitcost = matrec.getDouble("unitcost");
					setValue("unitcost", unitcost, 11L);
				}
			}
		}
	}
}
