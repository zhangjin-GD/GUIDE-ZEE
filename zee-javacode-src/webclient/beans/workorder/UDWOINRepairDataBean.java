package guide.webclient.beans.workorder;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

/**
 *@function:ZEE-A+B=C
 *@author:DJY
 *@modify:
 */
public class UDWOINRepairDataBean extends DataBean{
	@Override
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote wo = this.app.getAppBean().getMbo();
		if (wo.getString("udworepairtimes").equalsIgnoreCase("Y")) {
			Object params[] = { "This work order has been synthesized and cannot be synthesized again! \n itemnum: "+wo.getString("udworepairitemnum")+"" };
			throw new MXApplicationException("instantmessaging", "tsdimexception",params);
		}
		setValue("wonum", wo.getString("wonum"), 11L);
		setValue("location", "ZEE-01", 11L);
//		setValue("prcost", "0", 11L);
		setValue("itemcost", "0", 11L);
		setValue("quantity", "0", 11L);
		setValue("debitaccount", getAccount(wo), 11L);
		setValue("creditaccount", getAccount(wo), 11L);
	}
	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote wo = this.app.getAppBean().getMbo();
		if(wo != null) {
			String ponum = "UDCOSCO9";
			String itemnum = getString("itemnum");
			String location = getString("location");
			double quantity = Double.parseDouble(getString("quantity"));
			String receivedunit = getIssueunit(itemnum);
			String orderunit = getOrderunit(itemnum);
			String issuetype = "RECEIPT"; //RECEIPT  接收
			String polinenum = "1";
			String enterby = MXServer.getMXServer().getSystemUserInfo().getPersonId();
			String tobin = "ABC";
			double linecost = Double.parseDouble(getString("itemcost").replace(",", "")) * Double.parseDouble(getString("quantity"));
			String description = wo.getString("wonum")+wo.getString("description")+"-ABC";
			String tax1code = "1L";
			String status = "COMP"; //COMP  完成
			String orgid = "COSCO";
			String siteid = "CSPL";
			String fromsiteid = "CSPL";
			String linetype = "ITEM"; //ITEM  项目
			String itemsetid = "ITEMSET";
			String langcode = getLangcode(enterby);
			String positeid = "CSPL";
			String inventoryCategory = "STK"; //STK  库存
			String inventoryStatus = "ACTIVE"; //ACTIVE  活动
			String inventoryCosttype = "AVERAGE"; //AVERAGE  平均 
			double itemcost = Double.parseDouble(getString("itemcost").replace(",", ""));//领料库存金额
			String refwo = wo.getString("wonum");

			boolean existInventory = getInventory(itemnum,location);
			System.out.println("\n----existInventory---"+existInventory);
			if (existInventory == false) {
				String sql1 = "insert into inventory(itemsetid, orderunit, binnum, issueunit, orgid, siteid, category, ccf, deliverytime, issue1yrago, issue2yrago, issue3yrago, issueytd, itemnum, location, maxlevel, minlevel, orderqty, inventoryid, internal, status, statusdate, reorder, costtype, hardresissue, consignment)"
						+"values"
						+"('"+itemsetid+"','"+orderunit+"','"+tobin+"','"+receivedunit+"','"+orgid+"','"+siteid+"','"+inventoryCategory+"','0','0','0','0','0','0','"+itemnum+"','"+location+"','0','0','1',inventorySEQ.NEXTVAL,'0','"+inventoryStatus+"',sysdate,'1','"+inventoryCosttype+"','0','0')";
				try {
					exeSQL(sql1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			boolean existInvbalances = getInvbalances(itemnum,location,tobin);
			System.out.println("\n----existInvbalances---"+existInvbalances);
			if (existInvbalances == false) {
				double curbal = Double.parseDouble(getString("quantity"));
				String sql2 = "insert into invbalances(itemnum, location, binnum, lotnum, curbal, physcnt, physcntdate, reconciled, orgid, siteid, itemsetid, invbalancesid, stagingbin, stagedcurbal)"
						+"values"
						+"('"+itemnum+"','"+location+"','"+tobin+"','','"+curbal+"','"+curbal+"',sysdate,'1','"+orgid+"','"+siteid+"','"+itemsetid+"',invbalancesSEQ.NEXTVAL,'0','0')";
				try {
					exeSQL(sql2);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				double addCurbal = Double.parseDouble(getString("quantity"));
				updateInvbalances(itemnum,location,tobin,addCurbal);
			}

			boolean existInvcost = getInvcost(itemnum,location);
			System.out.println("\n----existInvcost---"+existInvcost);
			if (existInvcost == false) {
				String sql3 = "insert into invcost(itemnum, location, condrate, itemsetid, siteid, stdcost, avgcost, lastcost, controlacc, orgid, invcostid)"
						+"values"
						+"('"+itemnum+"','"+location+"','100','"+itemsetid+"','"+siteid+"','"+itemcost+"','"+itemcost+"','"+itemcost+"','COSCO', '"+orgid+"',invcostSEQ.NEXTVAL)";
				try {
					exeSQL(sql3);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				updateInvcost(itemnum,location,itemcost,quantity);
			}
			
			
			String sql5 = "insert into matrectrans(itemnum,tostoreloc,transdate,actualdate,quantity,receivedunit,issuetype,unitcost,actualcost,ponum,rejectqty,conversion,enterby,outside,polinenum,issue,requestedby,totalcurbal,tobin,gldebitacct,linecost,financialperiod,currencycode,exchangerate,currencyunitcost,currencylinecost,description,loadedcost,tax1code,tax1,tax2,tax3,tax4,tax5,prorated,proratecost,status,curbal,exchangerate2,matrectransid,orgid,siteid,costinfo,enteredastask,fromsiteid,linetype,itemsetid,langcode,inspectedqty,positeid,hasld,porevisionnum,consignment,udztype,refwo)"
					+"values"
					+"('"+itemnum+"','"+location+"',sysdate,sysdate,'"+quantity+"','"+receivedunit+"','"+issuetype+"','"+itemcost+"','"+itemcost+"','"+ponum+"','0','1','"+enterby+"','0','"+polinenum+"','0','"+enterby+"','0','"+tobin+"','COSCO','"+linecost+"','COSCO','EUR','1','"+itemcost+"','"+linecost+"','"+description+"','"+linecost+"','"+tax1code+"','0','0','0','0','0','0','0','"+status+"','0','1',matrectransSEQ.NEXTVAL,'"+orgid+"','"+siteid+"','1','0','"+fromsiteid+"','"+linetype+"','"+itemsetid+"','"+langcode+"','0','"+positeid+"','0','0','0','0','" + refwo +"')";
			try {
				exeSQL(sql5);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			wo.setValue("udwoinrepairitemnum", itemnum, 11L);
			wo.setValue("udwoinrepairtimes", "Y", 11L);
			wo.getThisMboSet().save();
			clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Success !\n The itemnum is "+itemnum, 1);
		}
		return 1;
	}
	
	private String getLangcode(String personid) throws MXException,RemoteException {
		String langcode = "EN";
		MboSetRemote personSet = MXServer.getMXServer().getMboSet("PERSON", MXServer.getMXServer().getSystemUserInfo());
		personSet.setWhere(" personid='"+personid+"' ");
		personSet.reset();
		if (!personSet.isEmpty() && personSet.count() > 0) {
			langcode = personSet.getMbo(0).getString("language");
		}
		personSet.close();
		return langcode;
	}
	
	private String getAccount(MboRemote wo) throws MXException,RemoteException {
		String account = "";
		String assetnum = wo.getString("assetnum");
		MboSetRemote assetSet = MXServer.getMXServer().getMboSet("ASSET", MXServer.getMXServer().getSystemUserInfo());
		assetSet.setWhere(" assetnum='"+assetnum+"' ");
		assetSet.reset();
		if (!assetSet.isEmpty() && assetSet.count() > 0) {
			account = assetSet.getMbo(0).getString("udcostcenter");
		}
		assetSet.close();
		return account;
	}
	
	private void updateInvbalances(String itemnum,String location,String binnum,double addCurbal) throws MXException,RemoteException {
		double newCurbal = 0.0D;
		MboSetRemote invbalancesSet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
		invbalancesSet.setWhere(" itemnum='"+itemnum+"' and location='"+location+"' and binnum='"+binnum+"' ");
		invbalancesSet.reset();
		if (!invbalancesSet.isEmpty() && invbalancesSet.count() > 0) {
			double curbal = invbalancesSet.getMbo(0).getDouble("curbal");
			newCurbal = curbal + addCurbal;
			invbalancesSet.getMbo(0).setValue("curbal", newCurbal, 11L);
		}
		invbalancesSet.save();
		invbalancesSet.close();
	}
	
	private void updateInvcost(String itemnum,String location,double itemcost,double quantity) throws MXException,RemoteException {
		MboSetRemote invcostSet = MXServer.getMXServer().getMboSet("INVCOST", MXServer.getMXServer().getSystemUserInfo());
		invcostSet.setWhere(" itemnum='"+itemnum+"' and location='"+location+"' ");
		invcostSet.reset();
		if (!invcostSet.isEmpty() && invcostSet.count() > 0) {
			double avgcost = invcostSet.getMbo(0).getDouble("avgcost");
			MboSetRemote invbalancesSet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
			invbalancesSet.setWhere(" itemnum='"+itemnum+"' and location='"+location+"' ");
			invbalancesSet.reset();
			if (!invbalancesSet.isEmpty() && invbalancesSet.count() > 0) {
				double curbal = invbalancesSet.sum("curbal");
				double newavgcost = (avgcost*curbal  + itemcost) / (curbal);
				invcostSet.getMbo(0).setValue("stdcost", newavgcost, 11L);
				invcostSet.getMbo(0).setValue("avgcost", newavgcost, 11L);
				invcostSet.getMbo(0).setValue("lastcost", newavgcost, 11L);
			}
			invbalancesSet.close();
		}
		invcostSet.save();
		invcostSet.close();
	}
	
	private String getIssueunit(String itemnum) throws MXException,RemoteException {
		String issueunit = "";
		MboSetRemote itemSet = MXServer.getMXServer().getMboSet("ITEM", MXServer.getMXServer().getSystemUserInfo());
		itemSet.setWhere(" itemnum='"+itemnum+"' ");
		itemSet.reset();
		if (!itemSet.isEmpty() && itemSet.count() > 0) {
			issueunit = itemSet.getMbo(0).getString("issueunit");
		}
		itemSet.close();
		return issueunit;
	}
	
	private String getOrderunit(String itemnum) throws MXException,RemoteException {
		String orderunit = "";
		MboSetRemote itemSet = MXServer.getMXServer().getMboSet("ITEM", MXServer.getMXServer().getSystemUserInfo());
		itemSet.setWhere(" itemnum='"+itemnum+"' ");
		itemSet.reset();
		if (!itemSet.isEmpty() && itemSet.count() > 0) {
			orderunit = itemSet.getMbo(0).getString("orderunit");
		}
		itemSet.close();
		return orderunit;
	}
	
	private boolean getInventory(String itemnum,String location) throws MXException,RemoteException {
		boolean flag = false;
		MboSetRemote inventorySet = MXServer.getMXServer().getMboSet("INVENTORY", MXServer.getMXServer().getSystemUserInfo());
		inventorySet.setWhere(" itemnum='"+itemnum+"' and location='"+location+"' ");
		inventorySet.reset();
		if (!inventorySet.isEmpty() && inventorySet.count() > 0) {
			flag = true;
		}
		inventorySet.close();
		return flag;
	}
	
	private boolean getInvbalances(String itemnum,String location,String binnum) throws MXException,RemoteException {
		boolean flag = false;
		MboSetRemote inbalancesSet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
		inbalancesSet.setWhere(" itemnum='"+itemnum+"' and location='"+location+"' and binnum='"+binnum+"' ");
		inbalancesSet.reset();
		if (!inbalancesSet.isEmpty() && inbalancesSet.count() > 0) {
			flag = true;
		}
		inbalancesSet.close();
		return flag;
	}
	
	private boolean getInvcost(String itemnum,String location) throws MXException,RemoteException {
		boolean flag = false;
		MboSetRemote invcostSet = MXServer.getMXServer().getMboSet("INVCOST", MXServer.getMXServer().getSystemUserInfo());
		invcostSet.setWhere(" itemnum='"+itemnum+"' and location='"+location+"' ");
		invcostSet.reset();
		if (!invcostSet.isEmpty() && invcostSet.count() > 0) {
			flag = true;
		}
		invcostSet.close();
		return flag;
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
}
