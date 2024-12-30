package guide.webclient.beans.project;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class ProConReLineTableBean extends DataBean {

	@Override
	public int addrow() throws MXException {
		try {
			MboRemote mbo = this.app.getAppBean().getMbo();
			String proconnum = mbo.getString("proconnum");
			if (proconnum.isEmpty()) {
				Object[] obj = { "温馨提示：请先选择项目合同编号，后再新增行信息！" };
				throw new MXApplicationException("udmessage", "error1", obj);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		super.addrow();
		return 1;
	}

}
