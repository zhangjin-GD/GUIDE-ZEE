package guide.app.item;

import guide.app.common.CommonUtil;
import psdi.security.ConnectionKey;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UDAutoAttributeCronTask extends SimpleCronTask {

	public UDAutoAttributeCronTask() throws RemoteException, MXException {

	}

	@Override
	public void cronAction() {
		System.out.println("---属性--开始---");
		updateAutoExpect();
		System.out.println("---属性--结束---");
	}

	private void updateAutoExpect() {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			MXServer mxServer = MXServer.getMXServer();
			String sqlWhere = getParamAsString("sqlWhere");
			ConnectionKey connectionKeySession = new ConnectionKey(mxServer.getSystemUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			String sql1081 = CommonUtil.getAttrs("1081");
			String sql = null;
			if (sql1081 != null && !sql1081.isEmpty()) {
				sql = sql1081.replaceAll(":sqlWhere", sqlWhere);
				System.out.println("sql = " + sql);
			} else {
				return;
			}
			ptmt = conn.prepareStatement(sql);
			// ִ��
			ptmt.executeUpdate();
			// �ύ
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
