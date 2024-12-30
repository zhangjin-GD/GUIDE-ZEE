package guide.webclient.beans.rfq;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.ControlInstance;

/**
 *@function:
 *@author:zj
 *@date:上午10:46:57
 *@modify:
 */
public class UDSelZEERfqLineVendorDataBean extends DataBean {
	
	public MboSetRemote getMboSetRemote() throws MXException, RemoteException {
		MboSetRemote msr = super.getMboSetRemote();
		DataBean db = app.getDataBean("1642507112233");
		MboRemote linembo = db.getMbo(db.getCurrentRow());
		String sql = "1=1";
		if (linembo != null) {
			String itemnum = linembo.getString("itemnum");
			sql = " itemnum = '"+itemnum+"' ";
		}
		msr.setWhere(sql);
		msr.reset();
		return msr;
	}
	
	public int execute() throws MXException, RemoteException {
		Vector vec = getSelection();
		MboRemote mbo = this.app.getAppBean().getMbo();
		MboRemote mr = null;
		DataBean db = app.getDataBean("1642507112233");
		MboRemote linembo = db.getMbo(db.getCurrentRow());
		if (linembo != null) {
			MboSetRemote rfqlinevendorSet = MXServer.getMXServer().getMboSet("UDRFQLINEVENDOR", MXServer.getMXServer().getSystemUserInfo());
			for (int i = 0;i< vec.size();i++) {
				mr = (MboRemote) vec.elementAt(i);//mr是勾选的数据
				if (mr != null) {
					MboRemote rfqlinevendor = rfqlinevendorSet.add();
					rfqlinevendor.setValue("rfqnum", linembo.getString("rfqnum"),11L);
					rfqlinevendor.setValue("rfqlinenum", linembo.getString("rfqlinenum"),11L);
					rfqlinevendor.setValue("itemnum", linembo.getString("itemnum"),11L);
					rfqlinevendor.setValue("vendor", mr.getString("vendor"),11L);
				}
			}
			rfqlinevendorSet.save();
			rfqlinevendorSet.close();
		}
		this.app.getAppBean().save();
		return 1;
	}
	
}
