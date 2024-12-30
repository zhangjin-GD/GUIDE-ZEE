package guide.webclient.beans.invuse;

import java.rmi.RemoteException;
import java.sql.SQLException;

import guide.app.common.ComExecute;
import guide.app.common.CommonUtil;
import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.invusage.InvUseLineDataBean;

public class UDInvUseLineDataBean extends InvUseLineDataBean {

	public int impInvuseLine() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		// 调用dialog
		this.clientSession.loadDialog("impInvuseLine");
		return 1;
	}

	// 出库流水号
	public int ckdlsh() throws RemoteException, MXException {

		String sql = CommonUtil.getAttrs("1038");
		if (sql != null && !sql.equalsIgnoreCase("")) {
			try {
				ComExecute.executeSql(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 1;
	}
}
