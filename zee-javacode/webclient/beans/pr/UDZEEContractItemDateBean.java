package guide.webclient.beans.pr;

import guide.app.pr.UDPR;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class UDZEEContractItemDateBean extends DataBean{
	public int execute() throws MXException, RemoteException {
		/**
		 * ZEE-选择合同物资按钮
		 */
		DataBean prLine = app.getDataBean("prlines_prlines_table");

		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = prLine.getParent().getMbo();

		if (owner != null && owner instanceof UDPR) {
			String udcompany = owner.getString("udcompany");
			MboSetRemote prlineSet = owner.getMboSet("prline");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote prline = prlineSet.add();
				prline.setValue("itemnum", mr.getString("itemnum"), 2L);
				prline.setValue("tax1code", mr.getString("tax1code"), 2L);
				prline.setValue("unitcost", mr.getDouble("uddiscountprice"), 2L);
				MboSetRemote udcontractSet = MXServer.getMXServer().getMboSet("UDCONTRACT", MXServer.getMXServer().getSystemUserInfo());
				udcontractSet.setWhere("gconnum = '"+mr.getString("gconnum")+"' ");
				udcontractSet.reset();
				if(!udcontractSet.isEmpty() && udcontractSet.count() > 0){
				MboRemote udcontract = udcontractSet.getMbo(0);
				prline.setValue("udconvendor", udcontract.getString("vendor"), 2L);
				}
			}
		}
		
		prLine.reloadTable();
		return 1;
	}
}
