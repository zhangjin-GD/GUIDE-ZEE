package guide.app.fixed;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.security.ConnectionKey;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FixStock extends UDMbo implements MboRemote {

	public FixStock(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
	}

	public void fixStockLineInsert(String fixstocknum, String udcompany, String orgid, String siteid) {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {

			ConnectionKey connectionKeySession = new ConnectionKey(this.getUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			String sql = "insert into udfixstockline (udfixstocklineid,linenum,fixstocknum,fixassetnum,siteid,orgid)"
					+ "	select udfixstocklineidseq.nextval,rownum as linenum, ? as fixstocknum, fixassetnum ,? as orgid, ? as siteid"
					+ " from udfixed where udfixed.udcompany = ? and not exists (select 1 from udfixstockline where udfixstockline.fixassetnum=udfixed.fixassetnum)";
			ptmt = conn.prepareStatement(sql);
			// 传参
			ptmt.setString(1, fixstocknum);
			ptmt.setString(2, orgid);
			ptmt.setString(3, siteid);
			ptmt.setString(4, udcompany);
			// 执行
			ptmt.executeUpdate();
			// 提交
			conn.commit();

		} catch (RemoteException e) {
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

	public void fixStockLineDelete(String fixstocknum, String siteid, String orgid) {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			ConnectionKey connectionKeySession = new ConnectionKey(this.getUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			String sql = "delete from udfixstockline where udfixstockline.fixstocknum = ? and udfixstockline.siteid = ? and udfixstockline.orgid = ?";
			ptmt = conn.prepareStatement(sql);
			// 传参
			ptmt.setString(1, fixstocknum);
			ptmt.setString(2, siteid);
			ptmt.setString(3, orgid);
			// 执行
			ptmt.executeUpdate();
			// 提交
			conn.commit();

		} catch (RemoteException e) {
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
