package guide.app.inventory;

import guide.app.common.CommonUtil;
import psdi.security.ConnectionKey;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UDAutoExpectCronTask extends SimpleCronTask {

    public UDAutoExpectCronTask() throws RemoteException, MXException {

    }
    @Override
    public void cronAction() {
        System.out.println("---预期--开始---");
        updateAutoExpect();
        System.out.println("---预期--结束---");
    }
    private void updateAutoExpect() {
        Connection conn = null;
        PreparedStatement ptmt = null;
        try {
            MXServer mxServer = MXServer.getMXServer();
            String sqlWhere = getParamAsString("sqlWhere");
            ConnectionKey connectionKeySession = new ConnectionKey(mxServer.getSystemUserInfo());
            conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
            String sql1080 = CommonUtil.getAttrs("1080");
            String sql = null;
            if (sql1080 != null && !sql1080.isEmpty()) {
                sql = sql1080.replaceAll(":sqlWhere", sqlWhere);
//                System.out.println("sql = " + sql);
            } else {
                return;
            }
            ptmt = conn.prepareStatement(sql);
            ptmt.executeUpdate();
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
