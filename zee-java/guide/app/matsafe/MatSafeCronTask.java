package guide.app.matsafe;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import guide.app.common.CommonUtil;
import psdi.security.ConnectionKey;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class MatSafeCronTask extends SimpleCronTask {

	public MatSafeCronTask() throws RemoteException, MXException {

	}

	@Override
	public void cronAction() {
		System.out.println("---安全件--开始---");
		updateMatSafeToCalact();
		System.out.println("---安全件--结束---");
	}

	private void updateMatSafeToCalact() {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			MXServer mxServer = MXServer.getMXServer();
			String sqlWhere = getParamAsString("sqlWhere");
			ConnectionKey connectionKeySession = new ConnectionKey(mxServer.getSystemUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			String sql1022 = CommonUtil.getAttrs("1022");
			String sql = null;
			if (sql1022 != null && !sql1022.isEmpty()) {
				sql = sql1022.replaceAll(":sqlWhere", sqlWhere);
			} else {
				return;
			}
			ptmt = conn.prepareStatement(sql);
			// 执行
			ptmt.executeUpdate();
			// 提交
			conn.commit();
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
	}
}
