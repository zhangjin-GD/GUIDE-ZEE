package guide.webclient.beans.rfq;

import java.rmi.RemoteException;

import psdi.app.rfq.RFQRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.beans.rfq.PRLineBean;

/**
 *@function:ZEE-PRTORFQ-CONTRACT
 *@author:zj
 *@date:2024-09-12 14:06:56
 *@modify:
 */
public class UDZEEPRLineBean extends PRLineBean {

	@Override
	public MboSetRemote getMboSetRemote() throws MXException, RemoteException {

		MboSetRemote mboSet = super.getMboSetRemote();
		MboRemote mbo = this.app.getAppBean().getMbo();
		String udcompany = mbo.getString("udcompany");
		String personid = mbo.getUserInfo().getPersonId();
		String sql = "1=1";
		/**
		 * ZEE-2024-09-10 10:00:10
		 * DJY-过滤有合同的PRLINE
		 */
		if(!udcompany.equalsIgnoreCase("") && udcompany.equalsIgnoreCase("ZEE")){
			sql =  " (udpurchaser='" + personid + "' or exists(select 1 from persongroupteam where persongroup='ZEE020205' and resppartygroup='" + personid + "')) "
					+ " and prnum in (select prnum from pr where pr.prnum=prline.prnum and pr.udapptype='PRZEE') "
					+"  and itemnum not in (select itemnum from udcontractline where udcontractline.itemnum = prline.itemnum and gconnum in (select gconnum  from udcontract where udcompany = 'ZEE' and status = 'APPR'  and to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd'))) ";
		}
		mboSet.setAppWhere(sql);
		mboSet.reset();
		return mboSet;
	}

	public int execute() throws MXException, RemoteException {
		RFQRemote mbo = (RFQRemote) this.parent.getMbo();
		mbo.copyPRToCurrentRFQ(getMboSet());
		String prnum = getString("prnum");
		MboSetRemote prSet = MXServer.getMXServer().getMboSet("PR", MXServer.getMXServer().getSystemUserInfo());
		prSet.setWhere("prnum='" + prnum + "'");
		prSet.reset();
		if (!prSet.isEmpty() && prSet.count() > 0) {
			MboRemote pr = prSet.getMbo(0);
			mbo.setValue("udcgly", pr.getString("udcgly"), 11L);
			mbo.setValue("udcglb", pr.getString("udcglb"), 11L);
			if (mbo.isNull("description")) {
				mbo.setValue("description", pr.getString("description"), 11L);
			}
		}
		this.parent.save();
		return 2;
	}
}
