package guide.webclient.beans.po;

import java.rmi.RemoteException;

import psdi.app.po.PORemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.beans.po.PRLineBean;

public class UDPRLineBean extends PRLineBean {

	@Override
	public MboSetRemote getMboSetRemote() throws MXException, RemoteException {

		MboSetRemote mboSet = super.getMboSetRemote();
		MboRemote mbo = this.app.getAppBean().getMbo();
		String udcompany = mbo.getString("udcompany");
		String vendor = mbo.getString("vendor");
		String udapptype = mbo.getString("udapptype");
		String udpurplat = mbo.getString("udpurplat");
		String personid = mbo.getUserInfo().getPersonId();
		String apptype = udapptype.replaceAll("PO", "PR");

		String sql = "(udpurchaser='" + personid + "' or exists(select 1 from persongroupteam"
				+ " where persongroup in (select persongroup from persongroup where description='采购部')"
				+ " and resppartygroup='" + personid + "'))"
				+ " and exists (select 1 from pr where pr.prnum=prline.prnum and pr.udapptype='" + apptype + "')";

		if ("CON".equalsIgnoreCase(udpurplat)) {
			sql += " and exists (select 1 from udcontractline conline where conline.linetype='ITEM' and conline.itemnum=prline.itemnum and"
					+ " exists (select 1 from udcontract con where con.udcompany='" + udcompany + "' and con.vendor='"
					+ vendor + "' and con.status ='APPR' and con.gconnum=conline.gconnum"
					+ " and to_char(sysdate,'yyyy-mm-dd')>= to_char(con.startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(con.enddate,'yyyy-mm-dd')))";
		}
		mboSet.setAppWhere(sql);
		mboSet.reset();
		return mboSet;
	}

	public int execute() throws MXException, RemoteException {
		PORemote mbo = (PORemote) this.parent.getMbo();
		mbo.copyPRToCurrentPO(getMboSet());
		String prnum = getString("prnum");
		MboSetRemote prSet = MXServer.getMXServer().getMboSet("PR", MXServer.getMXServer().getSystemUserInfo());
		prSet.setWhere("prnum='" + prnum + "'");
		prSet.reset();
		if (!prSet.isEmpty() && prSet.count() > 0) {
			mbo.setValue("udcgly", prSet.getMbo(0).getString("udcgly"));
			mbo.setValue("udcglb", prSet.getMbo(0).getString("udcglb"));
		}
		this.parent.save();
		return 2;
	}
}
