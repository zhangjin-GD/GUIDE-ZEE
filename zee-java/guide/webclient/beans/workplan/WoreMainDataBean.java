package guide.webclient.beans.workplan;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class WoreMainDataBean extends DataBean{

	/**
	 * 创建工单
	 * @throws MXException 
	 * @throws RemoteException 
	 */
	public void createWork() throws RemoteException, MXException {
		MboRemote mbo = this.getMbo();
		Vector<MboRemote> vector = this.getSelection();
		String flad = null;
		String wonum = null;
		if (vector.size() > 0) {
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				if (flad == null) {
					flad = mr.getString("assetnum");
				}else{
					if (!flad.equals(mr.getString("assetnum"))) {
						Object[] obj = { "必须是相同设备才能创建同一个工单" };
					    throw new MXApplicationException("udmessage", "error1", obj);
					}
				}
			}
			MboSetRemote workSet = mbo.getMboSet("WORKORDER");
			MboRemote work = workSet.add();
			
			work.setValue("worktype", "CM", 11L);
			work.setValue("description", mbo.getString("asset.description")+"遗留问题",11L);
			work.setValue("status", "WAPPR", 11L);
			work.setValue("targstartdate", mbo.getDate("planstartdate"), 11L);
			work.setValue("targcompdate", mbo.getDate("planenddate"), 11L);
			work.setValue("lead", mbo.getString("workleader"), 11L);
			work.setValue("udassettypename", mbo.getString("ASSET.udassettypename"), 2L);
			work.setValue("assetnum", mbo.getString("assetnum"), 2L);
			mbo.setValue("solvewonum", work.getString("wonum"), 11L);
			mbo.setValue("status", "COMP", 11L);
			
			wonum = work.getString("wonum");
			String solvewonum = work.getString("wonum");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				mr.setValue("solvewonum", solvewonum,11L);
				mr.setValue("status", "COMP", 11L);
			}
		}else{
			Object[] obj = { "没有可创建的工单" };
		    throw new MXApplicationException("udmessage", "error1", obj);
		}
		this.app.getAppBean().save();
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", "工单创建成功，单号:"+ wonum +"", 1);
	}
}
