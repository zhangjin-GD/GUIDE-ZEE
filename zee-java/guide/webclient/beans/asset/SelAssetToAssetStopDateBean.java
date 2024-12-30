package guide.webclient.beans.asset;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import guide.app.asset.AssetStopLine;
import guide.app.common.CommonUtil;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelAssetToAssetStopDateBean extends DataBean {
	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		String type = mbo.getString("udapptype");
		DataBean dateBean = app.getDataBean("udassetstopline_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = dateBean.getParent().getMbo();
		if (owner != null) {
			Date currentDate = MXServer.getMXServer().getDate();
			try {
				String appType = owner.getString("udapptype");
				if(appType != null && appType.equalsIgnoreCase("ASSETSTOP")){
					Date currentTime = CommonUtil.getFormatDate(currentDate, "yyyy-MM-dd");
					Date createTime = CommonUtil.getFormatDate(CommonUtil.getCalDate(owner.getDate("createtime"), 1), "yyyy-MM-dd");
					if(currentTime.equals(createTime) || currentTime.after(createTime)){
						return 1;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(currentDate);
			cal1.add(Calendar.DAY_OF_MONTH, 1);
			cal1.set(Calendar.HOUR_OF_DAY, 8);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);
			Date time8 = cal1.getTime();

			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(currentDate);
			cal2.add(Calendar.DAY_OF_MONTH, 1);
			cal2.set(Calendar.HOUR_OF_DAY, 17);
			cal2.set(Calendar.MINUTE, 0);
			cal2.set(Calendar.SECOND, 0);
			Date time17 = cal2.getTime();

			MboSetRemote lineSet = owner.getMboSet("UDASSETSTOPLINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				AssetStopLine line = (AssetStopLine) lineSet.add();
				line.setValue("assetnum", mr.getString("assetnum"), 11L);
				line.setValue("planstarttime", time8, 11L);
				line.setValue("planendtime", time17, 11L);
				if(type != null && type.equalsIgnoreCase("assetpm")){
					line.setValueNull("planstarttime", 11L);
					line.setValueNull("planendtime", 11L);
				}
			}
		}
		dateBean.reloadTable();
		return 1;
	}
}
