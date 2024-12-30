package guide.app.asset;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.security.ConnectionKey;
import psdi.server.MXServer;
import psdi.util.MXException;

public class EqRunLog extends UDMbo implements MboRemote {

	public EqRunLog(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		Date sysDate = MXServer.getMXServer().getDate();
		// 获取前一个月 第一天
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(sysDate);
		calendar1.add(Calendar.MONTH, -1);
		calendar1.set(Calendar.DAY_OF_MONTH, 1);
		this.setValue("startdate", calendar1.getTime(), 11L);
		// 获取前一个月 最后一天
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(sysDate);
		calendar2.set(Calendar.DAY_OF_MONTH, 0);
		this.setValue("enddate", calendar2.getTime(), 11L);

		this.setValue("ratio", 1, 11L);
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if (this.isModified("startdate") || this.isModified("enddate")) {
			Date enddate = this.getDate("enddate");
			MboSetRemote assetRunSet = this.getMboSet("ASSETRUN");
			if (!assetRunSet.isEmpty() && assetRunSet.count() > 0) {
				for (int i = 0; assetRunSet.getMbo(i) != null; i++) {
					MboRemote assetRun = assetRunSet.getMbo(i);
					String assetnum1 = assetRun.getString("assetnum");

					// TOS 作业量
					MboSetRemote workSet = this.getMboSet("$UDTOSWORKLOAD", "UDTOSWORKLOAD",
							"udcompany=:udcompany and assetnum = '" + assetnum1
									+ "' and rundate >= :startdate and rundate <= :enddate");
					double teuload = workSet.sum("teuload");
					double workload = workSet.sum("workload");
					double boxmove = workSet.sum("move");

					// TOS 运行小时
					MboSetRemote runSet = this.getMboSet("$UDTOSRUNTIME", "UDTOSRUNTIME",
							"udcompany=:udcompany and assetnum = '" + assetnum1
									+ "' and rundate >= :startdate and rundate <= :enddate");
					double runtime = runSet.sum("runtime");

					// 领料单
					MboSetRemote matuseSet = this.getMboSet("$MATUSETRANS", "MATUSETRANS",
							"itemnum in (select itemnum from item where udmaterialtype='1005') and assetnum = '"
									+ assetnum1 + "' and transdate >= :startdate and transdate <= :enddate"
									+ " and exists (select 1 from locations where locations.location=matusetrans.storeloc and locations.udcompany=:udcompany)");
					double quantity = Math.abs(matuseSet.sum("quantity"));

					// 上一次对应设备的值
					MboSetRemote eqrunlogLineSet = this.getMboSet("$UDEQRUNLOGLINE", "UDEQRUNLOGLINE", "assetnum='"
							+ assetnum1
							+ "' and exists(select 1 from udeqrunlog where udeqrunlog.eqrunnum=udeqrunlogline.eqrunnum and udeqrunlog.udcompany=:udcompany)");
					eqrunlogLineSet.setOrderBy("startdate desc");
					eqrunlogLineSet.reset();
					double electrickwhcur = 0;
					double watercur = 0;
					if (!eqrunlogLineSet.isEmpty() && eqrunlogLineSet.count() > 0) {
						MboRemote eqrunlogLine = eqrunlogLineSet.getMbo(0);
						electrickwhcur = eqrunlogLine.getDouble("electrickwhcur");	// 本月抄表（电）
						watercur = eqrunlogLine.getDouble("watercur");	// 本月抄表（水）
					}
					boolean flag = false;
					MboSetRemote runLineSet = this.getMboSet("UDEQRUNLOGLINE");
					if (!runLineSet.isEmpty() && runLineSet.count() > 0) {
						for (int j = 0; runLineSet.getMbo(j) != null; j++) {
							MboRemote runLine = runLineSet.getMbo(j);
							String assetnum2 = runLine.getString("assetnum");
							if (assetnum1.equalsIgnoreCase(assetnum2)) {
								flag = true;
								runLine.setValue("workhour", runtime, 11L);
								runLine.setValue("boxunit", workload, 11L);
								runLine.setValue("boxteu", teuload, 11L);
								runLine.setValue("boxmove", boxmove, 11L);
								runLine.setValue("oill", quantity, 11L);
								runLine.setValue("electrickwhpre", electrickwhcur, 2L);// 本月写入下一个单子的上月
								runLine.setValue("waterpre", watercur, 2L);// 本月写入下一个单子的上月
								runLine.setValue("createdate", enddate, 11L);
								break;
							}
						}
						if (!flag) {
							MboRemote runLine = runLineSet.add();
							runLine.setValue("assetnum", assetnum1, 11L);
							runLine.setValue("workhour", runtime, 11L);
							runLine.setValue("boxunit", workload, 11L);
							runLine.setValue("boxteu", teuload, 11L);
							runLine.setValue("boxmove", boxmove, 11L);
							runLine.setValue("oill", quantity, 11L);
							runLine.setValue("electrickwhpre", electrickwhcur, 2L);// 本月写入下一个单子的上月
							runLine.setValue("waterpre", watercur, 2L);// 本月写入下一个单子的上月
							runLine.setValue("createdate", enddate, 11L);
						}
					} else {
						MboRemote runLine = runLineSet.add();
						runLine.setValue("assetnum", assetnum1, 11L);
						runLine.setValue("workhour", runtime, 11L);
						runLine.setValue("boxunit", workload, 11L);
						runLine.setValue("boxteu", teuload, 11L);
						runLine.setValue("boxmove", boxmove, 11L);
						runLine.setValue("oill", quantity, 11L);
						runLine.setValue("electrickwhpre", electrickwhcur, 2L);// 本月写入下一个单子的上月
						runLine.setValue("waterpre", watercur, 2L);// 本月写入下一个单子的上月
						runLine.setValue("createdate", enddate, 11L);
					}
				}
			}
		}
	}

	// 获取工作时间
	public void getWorkHour() throws RemoteException, MXException {
		Map<String, Double> runMap = new HashMap<String, Double>();
		Connection connection = null;
		Statement stmt = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String udcompany = this.getString("udcompany");
			Date startdate = this.getDate("startdate");
			Date enddate = this.getDate("enddate");
			String startdateStr = format.format(startdate);
			String enddateStr = format.format(enddate);
			String sql = "select udtosruntime.assetnum,sum(runtime) runtime from udtosruntime "
					+ "left join asset on asset.assetnum =udtosruntime.assetnum where asset.assetnum is not null "
					+ "and to_char(udtosruntime.rundate,'yyyy-mm-dd') >= '" + startdateStr + "' "
					+ "and to_char(udtosruntime.rundate,'yyyy-mm-dd') <= '" + enddateStr + "' "
					+ "and udtosruntime.udcompany='" + udcompany + "' group by udtosruntime.assetnum";
			MXServer mxServer = MXServer.getMXServer();
			ConnectionKey connectionKeySession = new ConnectionKey(mxServer.getSystemUserInfo());
			connection = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				String assetnum = rset.getString("assetnum");
				double runtime = rset.getDouble("runtime");
				if (Math.abs(runtime) > 0) {
					runMap.put(assetnum, runtime);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		Date enddate = this.getDate("enddate");
		for (Entry<String, Double> entry : runMap.entrySet()) {
			String key = entry.getKey();
			Double value = entry.getValue();
			MboSetRemote runLineSet = this.getMboSet("UDEQRUNLOGLINE");
			if (!runLineSet.isEmpty() && runLineSet.count() > 0) {
				for (int i = 0; runLineSet.getMbo(i) != null; i++) {
					MboRemote runLine = runLineSet.getMbo(i);
					String assetnum = runLine.getString("assetnum");
					if (assetnum.equalsIgnoreCase(key)) {
						runLine.setValue("workhour", value, 11L);
						runLine.setValue("createdate", enddate, 11L);
						break;
					}
				}
			}
		}
	}

	// 获取作业箱量
	public void getBoxUnit() throws RemoteException, MXException {
		Map<String, Double> runMap = new HashMap<String, Double>();
		Connection connection = null;
		Statement stmt = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String udcompany = this.getString("udcompany");
			Date startdate = this.getDate("startdate");
			Date enddate = this.getDate("enddate");
			String startdateStr = format.format(startdate);
			String enddateStr = format.format(enddate);
			String sql = "select udtosworkload.assetnum,sum(workload) workload from udtosworkload "
					+ "left join asset on asset.assetnum =udtosworkload.assetnum where asset.assetnum is not null "
					+ "and to_char(udtosworkload.rundate,'yyyy-mm-dd') >= '" + startdateStr + "' "
					+ "and to_char(udtosworkload.rundate,'yyyy-mm-dd') <= '" + enddateStr + "' "
					+ "and udtosworkload.udcompany='" + udcompany + "' group by udtosworkload.assetnum";
			MXServer mxServer = MXServer.getMXServer();
			ConnectionKey connectionKeySession = new ConnectionKey(mxServer.getSystemUserInfo());
			connection = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				String assetnum = rset.getString("assetnum");
				double workload = rset.getDouble("workload");
				if (Math.abs(workload) > 0) {
					runMap.put(assetnum, workload);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		double ratio = this.getDouble("ratio");
		Date enddate = this.getDate("enddate");
		for (Entry<String, Double> entry : runMap.entrySet()) {
			String key = entry.getKey();
			Double value = entry.getValue();
			double boxteu = value * ratio;
			MboSetRemote runLineSet = this.getMboSet("UDEQRUNLOGLINE");
			if (!runLineSet.isEmpty() && runLineSet.count() > 0) {
				for (int i = 0; runLineSet.getMbo(i) != null; i++) {
					MboRemote runLine = runLineSet.getMbo(i);
					String assetnum = runLine.getString("assetnum");
					if (assetnum.equalsIgnoreCase(key)) {
						runLine.setValue("boxunit", value, 11L);
						runLine.setValue("boxteu", boxteu, 11L);
						runLine.setValue("createdate", enddate, 11L);
						break;
					}
				}
			}
		}
	}

	// 获取用油量
	public void getOill() throws RemoteException, MXException {

		Map<String, Double> runMap = new HashMap<String, Double>();
		Connection connection = null;
		Statement stmt = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String udcompany = this.getString("udcompany");
			Date startdate = this.getDate("startdate");
			Date enddate = this.getDate("enddate");
			String startdateStr = format.format(startdate);
			String enddateStr = format.format(enddate);
			String sql = "select assetnum,sum(quantity) quantity from matusetrans where "
					+ "itemnum in(select itemnum from item where udmaterialtype='1005') "
					+ "and to_char(transdate,'yyyy-mm-dd') >= '" + startdateStr + "' "
					+ "and to_char(transdate,'yyyy-mm-dd') <= '" + enddateStr + "' "
					+ "and exists(select 1 from locations where locations.location=matusetrans.storeloc "
					+ "and locations.udcompany='" + udcompany + "') and assetnum is not null group by assetnum";
			MXServer mxServer = MXServer.getMXServer();
			ConnectionKey connectionKeySession = new ConnectionKey(mxServer.getSystemUserInfo());
			connection = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				String assetnum = rset.getString("assetnum");
				double quantity = rset.getDouble("quantity");
				if (Math.abs(quantity) > 0) {
					runMap.put(assetnum, quantity);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		Date enddate = this.getDate("enddate");
		for (Entry<String, Double> entry : runMap.entrySet()) {
			String key = entry.getKey();
			Double value = Math.abs(entry.getValue());
			MboSetRemote runLineSet = this.getMboSet("UDEQRUNLOGLINE");
			if (!runLineSet.isEmpty() && runLineSet.count() > 0) {
				for (int i = 0; runLineSet.getMbo(i) != null; i++) {
					MboRemote runLine = runLineSet.getMbo(i);
					String assetnum = runLine.getString("assetnum");
					if (assetnum.equalsIgnoreCase(key)) {
						runLine.setValue("oill", value, 11L);
						runLine.setValue("createdate", enddate, 11L);
						break;
					}
				}
			}
		}
	}

}
