package guide.app.common;


import guide.app.common.ComExecute;
import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.sql.SQLException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class DBComCronTask extends SimpleCronTask {

  
	public DBComCronTask() throws RemoteException, MXException {

	}

	public void cronAction() {
		try {
			MboRemote crontaskInstance = getCrontaskInstance();
			System.out.println("\n-----------------instanceName:"+crontaskInstance.getString("instanceName"));
			MboSetRemote parameterSet = crontaskInstance.getMboSet("PARAMETER");
			if (!parameterSet.isEmpty() && parameterSet.count() > 0) {
				MboRemote parameter = null;
				String sqlNum = null;
				String sql = null;
				for (int i = 0; (parameter = parameterSet.getMbo(i)) != null; i++) {
					sqlNum = parameter.getString("value");
					sql = CommonUtil.getAttrs(sqlNum);
					if(sql != null && !sql.equalsIgnoreCase("")){
						System.out.println("\n-----------------CronTask:"+sql);
						ComExecute.executeSql(sql);
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}