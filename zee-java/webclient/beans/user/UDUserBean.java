package guide.webclient.beans.user;


import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;

import org.json.JSONException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.beans.user.UserBean;

public class UDUserBean extends UserBean{
	
	public int showPassWord() throws MXException, RemoteException {
		int ct = 0;
		MboSetRemote maxuserSet = MXServer.getMXServer().getMboSet("MAXUSER", MXServer.getMXServer().getSystemUserInfo());
		maxuserSet.setWhere("udpassword is null");
		if (!maxuserSet.isEmpty() && maxuserSet.count() > 0) {
			ct = maxuserSet.count();
			MboRemote maxuser = null;
			String passWord = null;
			for (int i = 0; (maxuser = maxuserSet.getMbo(i)) != null; i++) {
				try {
					passWord = CommonUtil.getPassWord(maxuser.getBytes("password"));
					if(passWord != null){
						maxuser.setValue("udpassword", passWord, 11L);
					}
					if(i == 888){
						ct = i;
						break;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			maxuserSet.save();
		}
		maxuserSet.close();
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", "已更新"+ct+"条，请注意调整管理员信息！", 1);
		return 1;
	}
	
}