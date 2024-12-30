package guide.app.inventory;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.security.ConnectionKey;
import psdi.server.MXServer;
import psdi.util.MXException;

public class InvMthly extends UDMbo implements MboRemote {

	public InvMthly(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		Date currentDateTime = MXServer.getMXServer().getDate();
		String currentDate = CommonUtil.getDateFormat(currentDateTime, "yyyy-MM-dd");
		Date mthlyDate = CommonUtil.getCalDate(currentDateTime, 1);
		setValue("description", currentDate+"物资台账", 11L);
		//ZEE - 库存月结主表描述为英文 36-39
		if(getString("udcompany")!=null && getString("udcompany").equalsIgnoreCase("ZEE")){
			setValue("description", currentDate+" Material Ledger", 11L);
		}
		setValue("mthlydate", mthlyDate, 11L);
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if(getString("status") != null && getString("status").equalsIgnoreCase("WAPPR")){
			MboSetRemote lineSet = getMboSet("UDINVMTHLYLINE");
			if (!lineSet.isEmpty() && lineSet.count() > 0) {
				setValue("curcost", lineSet.sum("invcost"), 11L);
			}
		}
	}
	
	/**
	 * 定时任务调用
	 * 
	 * @param invmthlynum
	 * @param storeloc`
	 * @param siteid
	 * @throws MXException
	 * @throws RemoteException
	 */
	public void invMonthly(String invmthlyNum) throws MXException, RemoteException {
		invMonthlyLineInsert(invmthlyNum);
		invMonthlyCostInsert(invmthlyNum);
		invMonthlyGLInsert(invmthlyNum);
	}

	public void invMonthlyGLInsert(String invmthlyNum) throws MXException {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			String sql1031 = CommonUtil.getAttrs("1031");
			System.out.println("\n-------------------"+sql1031);
			ConnectionKey connectionKeySession = new ConnectionKey(this.getUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			ptmt = conn.prepareStatement(sql1031);
			// 传参
			ptmt.setString(1, invmthlyNum);
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
	
	public void invMonthlyCostInsert(String invmthlyNum) throws MXException {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			String company = getString("udcompany");
			String preNum = CommonUtil.getValue(this, "PREINVMTHLY", "invmthlynum");
			String preDate = "20"+CommonUtil.getValue(this, "PREINVMTHLY", "mthlydate");
			String mthlyDate = CommonUtil.getDateFormat(getDate("mthlydate"), "yyyy-MM-dd");
			if(preDate.equalsIgnoreCase("20"))
				preDate = mthlyDate;
			String sql1009 = CommonUtil.getAttrs("1009");
			String sql1032 = CommonUtil.getAttrs("1032");
			String sql1016 = CommonUtil.getAttrs("1016");
			String sql1017 = CommonUtil.getAttrs("1017");
			String sql1028 = CommonUtil.getAttrs("1028");
			String sql1056 = CommonUtil.getAttrs("1056");
			String sql1057 = CommonUtil.getAttrs("1057");
			String sql1058 = CommonUtil.getAttrs("1058");
			String sql1059 = CommonUtil.getAttrs("1059");
			String sql1060 = CommonUtil.getAttrs("1060");
			
			sql1056 = sql1056.replaceAll(":company", company);
			sql1056 = sql1056.replaceAll(":startdate", preDate);
			sql1056 = sql1056.replaceAll(":enddate", mthlyDate);
			sql1057 = sql1057.replaceAll(":company", company);
			sql1057 = sql1057.replaceAll(":startdate", preDate);
			sql1057 = sql1057.replaceAll(":enddate", mthlyDate);
			sql1058 = sql1058.replaceAll(":company", company);
			sql1058 = sql1058.replaceAll(":startdate", preDate);
			sql1058 = sql1058.replaceAll(":enddate", mthlyDate);
			sql1059 = sql1059.replaceAll(":company", company);
			sql1059 = sql1059.replaceAll(":startdate", preDate);
			sql1059 = sql1059.replaceAll(":enddate", mthlyDate);
			sql1060 = sql1060.replaceAll(":company", company);
			sql1060 = sql1060.replaceAll(":startdate", preDate);
			sql1060 = sql1060.replaceAll(":enddate", mthlyDate);
			sql1016 = sql1016.replaceAll(":company", company);
			sql1017 = sql1017.replaceAll(":startdate", preDate);
			sql1017 = sql1017.replaceAll(":enddate", mthlyDate);
			
			sql1032 = sql1032.replaceAll(":1056", sql1056);
			sql1032 = sql1032.replaceAll(":1057", sql1057);
			sql1032 = sql1032.replaceAll(":1058", sql1058);
			sql1032 = sql1032.replaceAll(":1059", sql1059);
			sql1032 = sql1032.replaceAll(":1060", sql1060);
			sql1032 = sql1032.replaceAll(":1016", sql1016);
			sql1032 = sql1032.replaceAll(":1017", sql1017);
			sql1032 = sql1032.replaceAll(":1028", sql1028);
			
			sql1009 = sql1009.replaceAll(":1032", sql1032);
			System.out.println("\n-------------------"+sql1009);
			
			ConnectionKey connectionKeySession = new ConnectionKey(this.getUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			ptmt = conn.prepareStatement(sql1009);
			// 传参
			ptmt.setString(1, invmthlyNum);
			ptmt.setString(2, preNum);
			ptmt.setString(3, invmthlyNum);
			ptmt.setString(4, preNum);
			ptmt.setString(5, invmthlyNum);
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
	
	public void invMonthlyLineInsert(String invmthlyNum) throws MXException {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			String company = getString("udcompany");
			String preDate = "20"+CommonUtil.getValue(this, "PREINVMTHLY", "mthlydate");
			String mthlyDate = CommonUtil.getDateFormat(getDate("mthlydate"), "yyyy-MM-dd");
			if(preDate.equalsIgnoreCase("20"))
				preDate = mthlyDate;
			String sql1004 = CommonUtil.getAttrs("1004");
			String sql1029 = CommonUtil.getAttrs("1029");
			String sql1016 = CommonUtil.getAttrs("1016");
			String sql1017 = CommonUtil.getAttrs("1017");
			String sql1025 = CommonUtil.getAttrs("1025");
			String sql1026 = CommonUtil.getAttrs("1026");
			String sql1027 = CommonUtil.getAttrs("1027");
			String sql1051 = CommonUtil.getAttrs("1051");
			String sql1052 = CommonUtil.getAttrs("1052");
			String sql1053 = CommonUtil.getAttrs("1053");
			String sql1054 = CommonUtil.getAttrs("1054");
			String sql1055 = CommonUtil.getAttrs("1055");
			
			sql1051 = sql1051.replaceAll(":company", company);
			sql1051 = sql1051.replaceAll(":startdate", preDate);
			sql1051 = sql1051.replaceAll(":enddate", mthlyDate);
			sql1052 = sql1052.replaceAll(":company", company);
			sql1052 = sql1052.replaceAll(":startdate", preDate);
			sql1052 = sql1052.replaceAll(":enddate", mthlyDate);
			sql1054 = sql1054.replaceAll(":company", company);
			sql1054 = sql1054.replaceAll(":startdate", preDate);
			sql1054 = sql1054.replaceAll(":enddate", mthlyDate);
			sql1055 = sql1055.replaceAll(":company", company);
			sql1055 = sql1055.replaceAll(":startdate", preDate);
			sql1055 = sql1055.replaceAll(":enddate", mthlyDate);
			
			sql1016 = sql1016.replaceAll(":company", company);
			sql1017 = sql1017.replaceAll(":startdate", preDate);
			sql1017 = sql1017.replaceAll(":enddate", mthlyDate);
			
			sql1053 = sql1053.replaceAll(":1051", sql1051);
			sql1053 = sql1053.replaceAll(":1052", sql1052);
			sql1053 = sql1053.replaceAll(":1054", sql1054);
			sql1053 = sql1053.replaceAll(":1055", sql1055);
			sql1053 = sql1053.replaceAll(":1016", sql1016);
			sql1053 = sql1053.replaceAll(":1017", sql1017);
			sql1053 = sql1053.replaceAll(":1025", sql1025);
			sql1053 = sql1053.replaceAll(":1026", sql1026);
			sql1053 = sql1053.replaceAll(":1027", sql1027);
			
			sql1029 = sql1029.replaceAll(":1053", sql1053);
			sql1029 = sql1029.replaceAll(":1016", sql1016);
			sql1029 = sql1029.replaceAll(":1017", sql1017);
			
			sql1004 = sql1004.replaceAll(":1029", sql1029);
			System.out.println("\n-------------------"+sql1004);
			
			ConnectionKey connectionKeySession = new ConnectionKey(this.getUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			ptmt = conn.prepareStatement(sql1004);
			// 传参
			ptmt.setString(1, invmthlyNum);
			ptmt.setString(2, company);
			// 执行
			ptmt.executeUpdate();
			// 提交
			conn.commit();

		} catch (RemoteException e) {
			e.printStackTrace();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		} 
		finally {
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

	public void invMonthlyLineDelete(String sql) {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			ConnectionKey connectionKeySession = new ConnectionKey(this.getUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			System.out.println("\n----------------"+sql);
			ptmt = conn.prepareStatement(sql);
			// 传参
//			ptmt.setString(1, invmthlyNum);
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
