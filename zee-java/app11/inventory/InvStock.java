package guide.app.inventory;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import guide.iface.sap.webservice.HearBean;
import guide.iface.sap.webservice.ItemWebService;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.security.ConnectionKey;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InvStock extends UDMbo implements MboRemote {

	public InvStock(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			if (!this.toBeAdded()) {
				String status = this.getString("status");
				if ("APPR".equalsIgnoreCase(status)) {
					this.setFlag(READONLY, true);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
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
		/**
		 * ZEE-库存盘点状态为APPR时更新库存余量
		 * 2023-07-17 15:25:26
		 */
		String udcompany = getString("udcompany");
		String status = getString("status");
		if (status!=null && udcompany!=null && status.equalsIgnoreCase("APPR") && udcompany.equalsIgnoreCase("ZEE")) {
			MboSetRemote invStockLineDifZEESet = getMboSet("UDINVSTOCKLINEDIFZEE");
			if (!invStockLineDifZEESet.isEmpty() && invStockLineDifZEESet.count() > 0) {
				dataToInv(invStockLineDifZEESet);
			}
		}
	}

	public void invStockLineInsert(String invstocknum, boolean itemzero, String siteid) {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			ConnectionKey connectionKeySession = new ConnectionKey(this.getUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			String sql = "insert into udinvstockline "
					+ " (udinvstocklineid,linenum,invstocknum,siteid,orgid,itemnum,unit,storeloc,"
					+ " binnum,lotnum,stdcost,avgcost,lastcost,curbal,invcost,invdate,materialtype)"
					+ " select udinvstocklineidseq.nextval,rownum,udinvstock.invstocknum,udinvstock.siteid,udinvstock.orgid,inventory.itemnum,"
					+ " inventory.issueunit as unit,inventory.location as storeloc,"
					+ " invbalances.binnum,invbalances.lotnum,invcost.stdcost,invcost.avgcost,invcost.lastcost,invbalances.curbal,"
					+ " invbalances.curbal * invcost.avgcost as invcost,sysdate,item.udmaterialtype"
					+ " from udinvstock"
					+ " left join udinvstockloc on udinvstock.invstocknum = udinvstockloc.invstocknum"
					+ " left join udinvstockbin on udinvstock.invstocknum = udinvstockbin.invstocknum"
					+ " left join inventory on inventory.location = udinvstockloc.storeloc"
					+ " left join item on item.itemnum=inventory.itemnum"
					+ " left join classstructure on classstructure.classstructureid=item.classstructureid and classstructure.uditemtype=udinvstock.itemtype"
					+ " left join invbalances on invbalances.itemnum = inventory.itemnum and invbalances.location = inventory.location and invbalances.siteid = inventory.siteid and nvl(udinvstockbin.udbinnum,invbalances.binnum) = nvl(invbalances.udnewbinnum,invbalances.binnum)"
					+ " left join invcost on invcost.itemnum = inventory.itemnum and invcost.location = inventory.location and invcost.siteid = inventory.siteid"
					+ " where 1=1"
					+ " and invbalances.curbal >= nvl(udinvstock.udstartquantity,invbalances.curbal) and invbalances.curbal <= nvl(udinvstock.udendquantity,invbalances.curbal)"
					+ " and invcost.avgcost >= nvl(udinvstock.udstartamount,invcost.avgcost) and invcost.avgcost <= nvl(udinvstock.udendamount,invcost.avgcost)"
					+ " and ((udinvstock.itemtype is not null and classstructure.uditemtype =udinvstock.itemtype) or (udinvstock.itemtype is null and 1=1))"
					+ " and udinvstock.invstocknum = ? and udinvstock.siteid = ?";
			if (itemzero) {
				sql += " and 1=1";
			} else {
				sql += " and invbalances.curbal > 0 ";
			}
			ptmt = conn.prepareStatement(sql);
			// 传参
			ptmt.setString(1, invstocknum);
			ptmt.setString(2, siteid);
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
	
	/**
	 * ZEE-2024-01-30 16:02:06
	 * 插入库存盘点明细行
	 */
	public void invStockLineInsertZEE(String invstocknum, boolean itemzero, String siteid) {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			ConnectionKey connectionKeySession = new ConnectionKey(this.getUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			String sql = "insert into udinvstockline "
					+ " (udinvstocklineid,linenum,invstocknum,siteid,orgid,itemnum,unit,storeloc,"
					+ " binnum,lotnum,stdcost,avgcost,lastcost,curbal,invcost,invdate,materialtype)"
					+ " select udinvstocklineidseq.nextval,rownum,udinvstock.invstocknum,udinvstock.siteid,udinvstock.orgid,inventory.itemnum,"
					+ " inventory.issueunit as unit,inventory.location as storeloc,"
					+ " invbalances.binnum,invbalances.lotnum,invcost.stdcost,invcost.avgcost,invcost.lastcost,invbalances.curbal,"
					+ " invbalances.curbal * invcost.avgcost as invcost,sysdate,item.udmaterialtype"
					+ " from udinvstock"
					+ " left join udinvstockloc on udinvstock.invstocknum = udinvstockloc.invstocknum"
					+ " left join udinvstockbin on udinvstock.invstocknum = udinvstockbin.invstocknum"
					+ " left join inventory on inventory.location = udinvstockloc.storeloc"
					+ " left join item on item.itemnum=inventory.itemnum"
					+ " left join classstructure on classstructure.classstructureid=item.classstructureid and classstructure.uditemtype=udinvstock.itemtype"
					+ " left join invbalances on invbalances.itemnum = inventory.itemnum and invbalances.location = inventory.location and invbalances.siteid = inventory.siteid and coalesce(udinvstockbin.udbinnum,invbalances.binnum,'0') = coalesce(invbalances.binnum,invbalances.udnewbinnum,'0')"
					+ " left join invcost on invcost.itemnum = inventory.itemnum and invcost.location = inventory.location and invcost.siteid = inventory.siteid"
					+ " where 1=1"
					+ " and invbalances.curbal >= nvl(udinvstock.udstartquantity,invbalances.curbal) and invbalances.curbal <= nvl(udinvstock.udendquantity,invbalances.curbal)"
					+ " and invcost.avgcost >= nvl(udinvstock.udstartamount,invcost.avgcost) and invcost.avgcost <= nvl(udinvstock.udendamount,invcost.avgcost)"
					+ " and ((udinvstock.itemtype is not null and classstructure.uditemtype =udinvstock.itemtype) or (udinvstock.itemtype is null and 1=1))"
					+ " and udinvstock.invstocknum = ? and udinvstock.siteid = ?";
			if (itemzero) {
				sql += " and 1=1";
			} else {
				sql += " and invbalances.curbal > 0 ";
			}
			ptmt = conn.prepareStatement(sql);
			// 传参
			ptmt.setString(1, invstocknum);
			ptmt.setString(2, siteid);
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

	public void invStockLineDelete(String invstocknum, String siteid) {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			ConnectionKey connectionKeySession = new ConnectionKey(this.getUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			String sql = "delete from udinvstockline where udinvstockline.invstocknum = ? and udinvstockline.siteid = ?";
			ptmt = conn.prepareStatement(sql);
			// 传参
			ptmt.setString(1, invstocknum);
			ptmt.setString(2, siteid);
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

	public void invStockQtyUpdate(String invstocknum, String siteid) {
		Connection conn = null;
		PreparedStatement ptmt = null;
		try {
			ConnectionKey connectionKeySession = new ConnectionKey(this.getUserInfo());
			conn = MXServer.getMXServer().getDBManager().getConnection(connectionKeySession);
			String sql = "update udinvstockline set udinvstockline.quantity = udinvstockline.curbal , udinvstockline.differqty =0 where udinvstockline.quantity is null and udinvstockline.invstocknum = ? and udinvstockline.siteid = ?";
			ptmt = conn.prepareStatement(sql);
			// 传参
			ptmt.setString(1, invstocknum);
			ptmt.setString(2, siteid);
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

	public void adjustInv() throws RemoteException, MXException, JSONException {
		MboSetRemote invStockLine209Set = getMboSet("UDINVSTOCKLINE209");
		if (!invStockLine209Set.isEmpty() && invStockLine209Set.count() > 0) {
			dataToSap(invStockLine209Set, "209");
		}
		MboSetRemote invStockLine210Set = getMboSet("UDINVSTOCKLINE210");
		if (!invStockLine210Set.isEmpty() && invStockLine210Set.count() > 0) {
			dataToSap(invStockLine210Set, "210");
		}
		MboSetRemote invStockLineDifSet = getMboSet("UDINVSTOCKLINEDIF");
		if (!invStockLineDifSet.isEmpty() && invStockLineDifSet.count() > 0) {
			dataToInv(invStockLineDifSet);
		}
	}

	private void dataToInv(MboSetRemote invStockLineSet) throws RemoteException, MXException {
		MboRemote invStockLine = null;
		MboSetRemote invbalancesSet = null;
		MboRemote invbalances = null;
		double curbal = 0.0;
		double oldcurbal = 0.0;
		String udcompany = getString("udcompany");
		for (int i = 0; (invStockLine = invStockLineSet.getMbo(i)) != null; i++) {
			invbalancesSet = invStockLine.getMboSet("INVBALANCES"); //编码、库房、货位、批次
			if (!invbalancesSet.isEmpty() && invbalancesSet.count() > 0) {
				invbalances = invbalancesSet.getMbo(0);
				oldcurbal = invbalances.getDouble("curbal");
				curbal = invbalances.getDouble("curbal") + invStockLine.getDouble("differqty");
				
				/**
				 * ZEE-将实盘数量更新到当前余量
				 */
				if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
					curbal = invStockLine.getDouble("quantity");
				}
				invbalances.setValue("curbal", curbal, 11L);
				
				/**
				 * ZEE-更新到库存调整表INVTRANS
				 */
				String itemnum = invbalances.getString("itemnum");
				String location = invbalances.getString("location");
				String binnum = invbalances.getString("binnum");
				String lotnum = invbalances.getString("lotnum");
				double quantity = invStockLine.getDouble("differqty");
				double physcnt = invbalances.getDouble("physcnt");
				double cost = invbalances.getDouble("UDINVCOST.avgcost");
				String enterby = getUserInfo().getPersonId();
				String sql1 = "insert into invtrans (itemnum, storeloc, transdate, transtype, quantity, curbal, physcnt, oldcost, newcost, enterby, memo, binnum, lotnum, gldebitacct, glcreditacct, financialperiod, linecost, linecost2, invtransid, matrectransid, orgid, siteid, itemsetid, consignment) "
						+ "values"
						+ "('"+itemnum+"','"+location+"',sysdate,'CURBALADJ','"+quantity+"','"+oldcurbal+"','"+physcnt+"','"+cost+"','"+cost+"','"+enterby+"','ZEE','"+binnum+"','"+lotnum+"','COSCO','COSCO','COSCO','0','0',invtransSEQ.NEXTVAL,'','COSCO','CSPL','ITEMSET','0')";
				try {
					exeSQL(sql1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void dataToSap(MboSetRemote invStockLineSet, String zTran)
			throws RemoteException, MXException, JSONException {
		String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
		String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
		System.out.println("\nINVSTK-----------status" + sapStatus + "-----------debug" + sapDebug);
		if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
			Date stockDate = getDate("stockdate");
			String stkNum = "STK" + getString("invstocknum") + zTran;
			JSONObject Header = new JSONObject();
			Header = getHeader(stkNum, zTran, stockDate);
			Header.put("item", getItem(invStockLineSet, stkNum));
			if (CommonUtil.getString(Header, "item").toString().length() > 2) {
				if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
					System.out.println("\n提示，SAP，XML:" + Header.toString() + "!");
					Object params[] = { "提示，SAP，XML:" + Header.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
				String num = "";
				String status = "";
				try {
					HearBean result = ItemWebService.itemRequestWebService(Header.toString());
					num = result.getBELNR();
					status = result.getZHEADMSG();
					CommonUtil.ifaceLog(Header.toString(), getUserInfo().getPersonId(), getName(),
							Header.getString("ZSTOCKNO"), num, status);
					if (num == null) {
						Object params[] = { "提示：SAP，" + status + "!" };
						throw new MXApplicationException("instantmessaging", "tsdimexception", params);
					}
					setValue("udsapnum" + zTran, num, 11L);
					setValue("udsapstatus" + zTran, status, 11L);
				} catch (Exception e) {
					e.printStackTrace();
					Object params[] = { "提示：SAP，" + e.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
			}
		}
	}

	private JSONObject getHeader(String num, String zTran, Date transDate)
			throws JSONException, RemoteException, MXException {
		JSONObject Header = new JSONObject();
		Header.put("ZSOURCE", CommonUtil.getValue(this, "UDCOMPANY", "sapzsource"));// 原系统，固定值
		Header.put("BUKRS", CommonUtil.getValue(this, "UDCOMPANY", "costcenter"));// 公司代码
		Header.put("ZSTOCKNO", num);// 物资单号
		Header.put("BUDAT", CommonUtil.getDateFormat(transDate, "yyyyMMdd"));// 凭证日期;
		Header.put("ZDATE1", CommonUtil.getDateFormat(transDate, "yyyyMMdd"));// 传输日期
		Header.put("ZTRAN", zTran);// 移动类型
		Header.put("ZEAMHEADFIELD1", "");
		Header.put("ZEAMHEADFIELD2", "");
		Header.put("ZEAMHEADFIELD3", "");
		Header.put("ZEAMHEADFIELD4", "");
		Header.put("ZEAMHEADFIELD5", "");
		return Header;
	}

	private JSONArray getItem(MboSetRemote invStockLineSet, String stkNum)
			throws RemoteException, JSONException, MXException {
		JSONArray ItemSet = new JSONArray();
		if (!invStockLineSet.isEmpty() && invStockLineSet.count() > 0) {
			MboRemote invStockLine = null;
			MboRemote item = null;
			MboSetRemote itemSet = null;
			int flag = 1;
			// String KOSTL = null;
			for (int i = 0; (invStockLine = invStockLineSet.getMbo(i)) != null; i++) {
				if (invStockLine.getDouble("differqty") < 0)
					flag = -1;
				JSONObject Item = new JSONObject();
				Item.put("ZSTOCKNO", stkNum);// 物资单号
				Item.put("ZSTOCKITEMNO", i + 1);// 物资单项目号
				Item.put("ZQUANTITY", String.format("%.2f", flag * invStockLine.getDouble("differqty")));// 数量
				Item.put("DMBTR3", String.format("%.2f",
						flag * invStockLine.getDouble("differqty") * invStockLine.getDouble("avgcost")));// 成本
				Item.put("WAERS", CommonUtil.getValue(this, "UDCOMPANY", "currency"));// 货币码0

				Item.put("KOSTL", "");// 成本中心（出库字段）
				Item.put("AUFNR", "");// 内部订单号（出库字段）

				itemSet = invStockLine.getMboSet("ITEM");
				if (!itemSet.isEmpty() && itemSet.count() > 0) {
					item = itemSet.getMbo(0);
					Item.put("MTART", item.getString("udmaterialtype"));// 物料类型代码
					Item.put("MAKTX", item.getString("description"));// 物料描述(短文本)
					Item.put("ZUNIT", item.getString("issueunit"));// 单位
					Item.put("ZMATERIALCODE", invStockLine.getString("itemnum"));// 物料编码
					Item.put("ZMATERIALL1", invStockLine.getString("itemnum").substring(0, 2));// 物料大类
					Item.put("ZMATERIALL2", invStockLine.getString("itemnum").substring(0, 4));// 物料中类
					Item.put("ZMATERIALL3", invStockLine.getString("itemnum").substring(0, 6));// 物料小类
					Item.put("ZMATERIALDESCL1", CommonUtil.getValue(item, "CLASS1", "description"));// 物料大类
					Item.put("ZMATERIALDESCL2", CommonUtil.getValue(item, "CLASS2", "description"));// 物料中类
					Item.put("ZMATERIALDESCL3", CommonUtil.getValue(item, "CLASS3", "description"));// 物料小类
				}
				Item.put("ZEAMITEMFIELD1", "");
				Item.put("ZEAMITEMFIELD2", "");
				Item.put("ZEAMITEMFIELD3", "");
				Item.put("ZEAMITEMFIELD4", "");
				Item.put("ZEAMITEMFIELD5", "");
				ItemSet.put(Item);
			}
		}
		return ItemSet;
	}
	
	public void exeSQL(String sql) throws MXException, RemoteException,
			SQLException {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = MXServer
					.getMXServer()
					.getDBManager()
					.getConnection(
							MXServer.getMXServer().getSystemUserInfo()
									.getConnectionKey());

			if (null != connection) {
				stmt = connection.createStatement();
				try {
					stmt.execute(sql);
					connection.commit();
				} catch (SQLException e) {
					connection.rollback();
					System.out.println(sql + e);
				}
			}

		} catch (RemoteException e) {
		} catch (Exception e) {
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e1) {
				}
			}
			try {
				MXServer.getMXServer()
						.getDBManager()
						.freeConnection(
								MXServer.getMXServer().getSystemUserInfo()
										.getConnectionKey());
				if (connection != null) {
					connection.close();
				}
			} catch (RemoteException e1) {
			} catch (Exception e1) {
			}
		}
	}
	
	/**
	 * ZEE - 库存盘点传SAP
	 * @throws DJY
	 * @throws 2024-9-25  10：30
	 * @throws 506-626
	 */
	public void DATATOSAPZEE() throws RemoteException, MXException, JSONException {
		MboSetRemote invStockLineZEE209Set = getMboSet("UDINVSTOCKLINEZEE209");
		if (!invStockLineZEE209Set.isEmpty() && invStockLineZEE209Set.count() > 0) {
			invdataToSapZEE(invStockLineZEE209Set, "209");//盘盈
		}
		MboSetRemote invStockLineZEE210Set = getMboSet("UDINVSTOCKLINEZEE210");
		if (!invStockLineZEE210Set.isEmpty() && invStockLineZEE210Set.count() > 0) {
			invdataToSapZEE(invStockLineZEE210Set, "210");//盘亏
		}
	}

	private void invdataToSapZEE(MboSetRemote invStockLineSet, String zTran)
			throws RemoteException, MXException, JSONException {
		String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
		String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
		System.out.println("\nINVSTK-----------status" + sapStatus + "-----------debug" + sapDebug);
		if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
			Date stockDate = getDate("stockdate");
			String stkNum = "STK" + getString("invstocknum") + zTran;
			JSONObject Header = new JSONObject();
			Header = getHeaderZEE(stkNum, zTran, stockDate);
			Header.put("item", getItemZEE(invStockLineSet, stkNum));
			if (CommonUtil.getString(Header, "item").toString().length() > 2) {
				if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
					System.out.println("\n提示，SAP，XML:" + Header.toString() + "!");
					Object params[] = { "提示，SAP，XML:" + Header.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
				String num = "";
				String status = "";
				try {
					HearBean result = ItemWebService.itemRequestWebService(Header.toString());
					num = result.getBELNR();
					status = result.getZHEADMSG();
					CommonUtil.ifaceLog(Header.toString(), getUserInfo().getPersonId(), getName(),
							Header.getString("ZSTOCKNO"), num, status);
					if (num == null) {
						Object params[] = { "提示：SAP，" + status + "!" };
						throw new MXApplicationException("instantmessaging", "tsdimexception", params);
					}
					setValue(("udsapnum" + zTran), num, 11L);
					setValue(("udsapstatus" + zTran), status, 11L);
					//ZEE - SAP返回状态Success 554-557
					if(getString("udcompany")!=null && getString("udcompany").equalsIgnoreCase("ZEE")){
						setValue("udsapstatus", "Success", 11L);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Object params[] = { "提示：SAP，" + e.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
			}
		}
	}
	
	private JSONObject getHeaderZEE(String num, String zTran, Date transDate)
			throws JSONException, RemoteException, MXException {
		JSONObject Header = new JSONObject();
		Header.put("ZSOURCE", CommonUtil.getValue("UDDEPT","deptnum='ZEE'","sapzsource"));// 原系统，固定值
		Header.put("BUKRS", CommonUtil.getValue("UDDEPT","deptnum='ZEE'","costcenter"));// 成本中心
		Header.put("ZSTOCKNO", num);// 物资单号-唯一键值
		Header.put("BUDAT", CommonUtil.getDateFormat(transDate, "yyyyMMdd"));// 凭证日期;
		Header.put("ZDATE1", CommonUtil.getDateFormat(transDate, "yyyyMMdd"));// 传输日期
		Header.put("ZTRAN", zTran);// 移动类型
		Header.put("ZEAMHEADFIELD1", "");
		Header.put("ZEAMHEADFIELD2", "");
		Header.put("ZEAMHEADFIELD3", "");
		Header.put("ZEAMHEADFIELD4", "");
		Header.put("ZEAMHEADFIELD5", "");
		return Header;
	}

	private JSONArray getItemZEE(MboSetRemote invStockLineSet, String stkNum)
			throws RemoteException, JSONException, MXException {
		JSONArray ItemSet = new JSONArray();
		if (!invStockLineSet.isEmpty() && invStockLineSet.count() > 0) {
			MboRemote invStockLine = null;
			MboRemote item = null;
			MboSetRemote itemSet = null;
			int flag = 1;
			for (int i = 0; (invStockLine = invStockLineSet.getMbo(i)) != null; i++) {
				if (invStockLine.getDouble("differqty") < 0){
					flag = -1;
				}
				JSONObject Item = new JSONObject();
				Item.put("ZSTOCKNO", stkNum);// 物资单号-唯一键值
				Item.put("ZSTOCKITEMNO", i + 1);// 物资单项目号，序号
				Item.put("ZQUANTITY", String.format("%.2f", flag * invStockLine.getDouble("differqty")));// 盘点差异数量
				Item.put("DMBTR3", String.format("%.2f",
						flag * invStockLine.getDouble("differqty") * invStockLine.getDouble("avgcost")));// 盘点差异成本
				Item.put("WAERS",  CommonUtil.getValue("UDDEPT","deptnum='ZEE'","currency"));// 货币码0

				Item.put("KOSTL", "");// 成本中心（出库字段）
				Item.put("AUFNR", "");// 内部订单号（出库字段）

				itemSet = invStockLine.getMboSet("ITEM");
				if (!itemSet.isEmpty() && itemSet.count() > 0) {
					item = itemSet.getMbo(0);
					Item.put("MTART", item.getString("udmaterialtype"));// 物料类型代码
					Item.put("MAKTX", item.getString("description"));// 物料描述(短文本)
					Item.put("ZUNIT", item.getString("issueunit"));// 单位
					Item.put("ZMATERIALCODE", invStockLine.getString("itemnum"));// 物料编码
					Item.put("ZMATERIALL1", invStockLine.getString("itemnum").substring(0, 2));// 物料大类
					Item.put("ZMATERIALL2", invStockLine.getString("itemnum").substring(0, 4));// 物料中类
					Item.put("ZMATERIALL3", invStockLine.getString("itemnum").substring(0, 6));// 物料小类
					Item.put("ZMATERIALDESCL1", CommonUtil.getValue(item, "CLASS1", "description"));// 物料大类
					Item.put("ZMATERIALDESCL2", CommonUtil.getValue(item, "CLASS2", "description"));// 物料中类
					Item.put("ZMATERIALDESCL3", CommonUtil.getValue(item, "CLASS3", "description"));// 物料小类
				}
				Item.put("ZEAMITEMFIELD1", "");
				Item.put("ZEAMITEMFIELD2", "");
				Item.put("ZEAMITEMFIELD3", "");
				Item.put("ZEAMITEMFIELD4", "");
				Item.put("ZEAMITEMFIELD5", "");
				ItemSet.put(Item);
			}
		}
		return ItemSet;
	}

}
