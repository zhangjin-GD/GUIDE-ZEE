package guide.webclient.beans.asset;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import guide.app.asset.EqRunLog;
import guide.app.common.CommonUtil;
import psdi.mbo.MboRemote;
import psdi.security.ConnectionKey;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;
import psdi.webclient.system.controller.WebClientEvent;

public class EqRunLogAppBean extends AppBean {

	// 导入
	public void impToEqRunLog() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		this.clientSession.loadDialog("impToEqRunLog");
	}

	// 更新工作时间
	public void upWorkHour() throws RemoteException, MXException {
		EqRunLog mbo = (EqRunLog) this.getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		mbo.getWorkHour();
		if (this.hasSaveAccess()) {
			WebClientEvent event = this.clientSession.getCurrentEvent();
			this.save();
			this.clientSession.showMessageBox(event, "guide", "1044", (Object[]) null);
		}
	}

	// 更新作业箱量
	public void upBoxUnit() throws RemoteException, MXException {
		EqRunLog mbo = (EqRunLog) this.getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		mbo.getBoxUnit();
		if (this.hasSaveAccess()) {
			WebClientEvent event = this.clientSession.getCurrentEvent();
			this.save();
			this.clientSession.showMessageBox(event, "guide", "1044", (Object[]) null);
		}
	}

	// 更新用油量
	public void upOill() throws RemoteException, MXException {
		EqRunLog mbo = (EqRunLog) this.getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		mbo.getOill();
		if (this.hasSaveAccess()) {
			WebClientEvent event = this.clientSession.getCurrentEvent();
			this.save();
			this.clientSession.showMessageBox(event, "guide", "1044", (Object[]) null);
		}
	}

	// 更新运行状态
	public void upRunLine() throws RemoteException, MXException {
		EqRunLog mbo = (EqRunLog) this.getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		mbo.getWorkHour();
		mbo.getBoxUnit();
		mbo.getOill();
		if (this.hasSaveAccess()) {
			WebClientEvent event = this.clientSession.getCurrentEvent();
			this.save();
			this.clientSession.showMessageBox(event, "guide", "1044", (Object[]) null);
		}
	}

	// 更新安全件台账
	public void upMatsafe() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		boolean ismatsafe = mbo.getBoolean("ismatsafe");
		if (ismatsafe) {
			throw new MXApplicationException("guide", "1060");
		}
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			MXServer mxServer = MXServer.getMXServer();
			String sqlWhere = mbo.getString("eqrunnum");
			ConnectionKey connectionKeySession = new ConnectionKey(mxServer.getSystemUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			String sql = CommonUtil.getAttrs("1037");
			if (sql != null && !sql.isEmpty()) {
				sql = sql.replaceAll(":sqlWhere", sqlWhere);
			} else {
				return;
			}
			ptmt = conn.prepareStatement(sql);
			// 执行
			ptmt.executeUpdate();
			// 提交
			conn.commit();
			mbo.setValue("ismatsafe", true, 11L);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ptmt != null) {
					ptmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (this.hasSaveAccess()) {
			WebClientEvent event = this.clientSession.getCurrentEvent();
			this.save();
			this.clientSession.showMessageBox(event, "guide", "1044", (Object[]) null);
		}
	}
}
