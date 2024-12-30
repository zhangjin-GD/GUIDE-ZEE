package guide.webclient.beans.gjobplan;

import guide.app.common.ComExecute;
import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.sql.SQLException;

import psdi.mbo.MboRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;

public class UDGjobPlanAppBean extends AppBean {

	public int createGpm() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		String message = "提示，PM创建失败！";
		String sql = CommonUtil.getAttrs("1043");
		String personId = mbo.getUserInfo().getPersonId();
		String gjpnum = mbo.getString("gjpnum");
		String abbrCompany = getString("gjpnum").split("-")[0];
		String serialNum = getString("gjpnum").split("-")[2];
		if(sql != null && !sql.equalsIgnoreCase("")){
			try {
				sql = sql.replaceAll(":personid", personId);
				sql = sql.replaceAll(":abbrcompany", abbrCompany);
				sql = sql.replaceAll(":serialnum", serialNum);
				sql = sql.replaceAll(":gjpnum", gjpnum);
				System.out.println("\n-----------------createGpm:"+sql);
				ComExecute.executeSql(sql);
				message = "提示，PM创建成功！";
			} catch (SQLException e) {
				message += e.toString();
				e.printStackTrace();
			}
		}
		mbo.setValue("changeby", personId);
		mbo.setValue("changetime", MXServer.getMXServer().getDate());
		this.app.getAppBean().save();
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", message, 1);
		return 1;
	}
	
}
