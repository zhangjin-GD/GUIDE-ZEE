package guide.app.inventory;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import guide.app.common.CommonUtil;
import guide.app.inventory.bean.InventoryBean;
import guide.app.pr.PRVendor;
import guide.app.pr.UDPR;
import guide.app.pr.UDPRLine;
import guide.app.pr.UDPRSet;
import psdi.mbo.MboSetRemote;
import psdi.security.ConnectionKey;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class UDAutoReorderItemVendorCronTask extends SimpleCronTask {

	private static final int MAXLINE = 150;

	// 重订购
	@Override
	public void cronAction() {
		try {
			System.out.println("--开始--重订购--item--vendor--");
			List<InventoryBean> list = getInventoryList();
			createPR(list);
			System.out.println("--结束--重订购--item--vendor--");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
	}

	private List<InventoryBean> getInventoryList() throws RemoteException, MXException {
		String sqlWhere = getParamAsString("sqlWhere");
		List<InventoryBean> list = new ArrayList<InventoryBean>();
		Connection connection = null;
		Statement stmt = null;
		try {
//			String sql = "select i.udcompany,i.dept as uddept,i.vendor,i.purchaser,i.itemnum,round((i.maxlimit-(i.curbal+i.prqty+i.poqty-i.iuqty))/i.udconversion) qty, i.conversion , i.orderunit from"
//					+ " (select uditemcp.udcompany,uditemcp.dept,uditemcp.vendor,uditemcp.purchaser,uditemcp.itemnum,uditemcp.maxlimit,uditemcp.minlimit,udconversion.conversion,item.orderunit,"
//					+ " nvl(invb.curbal,0) curbal, nvl(prl.prqty,0) prqty, nvl(pol.poqty,0) poqty, nvl(iul.iuqty,0) iuqty,"
//					+ " nvl(udconversion.conversion,1) udconversion" + " from uditemcp"
//					+ " left join item on uditemcp.itemnum=item.itemnum"
//					+ " left join udconversion on udconversion.frommeasureunit=item.orderunit and udconversion.tomeasureunit=item.issueunit and uditemcp.itemnum = udconversion.itemnum and uditemcp.vendor =udconversion.vendor "
//
//					+ " left join" + " (select itemnum,sum(invbalances.curbal) curbal from locations"
//					+ " left join invbalances on invbalances.location=locations.location" + " where " + sqlWhere
//					+ " and invbalances.curbal>0 "
//					+ " group by invbalances.itemnum ) invb on uditemcp.itemnum=invb.itemnum"
//
//					+ " left join"
//					+ " (select prline.itemnum,sum(prline.orderqty * nvl(udconversion.conversion,1)) prqty from pr"
//					+ " left join prline on pr.prnum=prline.prnum" + " left join item on item.itemnum=prline.itemnum"
//					+ " left join uditemcp on uditemcp.itemnum=item.itemnum"
//					+ " left join udconversion on udconversion.frommeasureunit=item.orderunit and udconversion.tomeasureunit=item.issueunit  and uditemcp.vendor =udconversion.vendor  "
//					+ " where " + "uditemcp."+sqlWhere
//					+ " and pr.status not in ('CAN') and prline.polineid is null and prline.itemnum is not null and prline.udpurstatus is null"
//					+ " group by prline.itemnum) prl on uditemcp.itemnum=prl.itemnum"
//
//					+ " left join "
//					+ " (select poline.itemnum,sum(poline.orderqty * nvl(udconversion.conversion,1)) poqty from po"
//					+ " left join poline on poline.ponum=po.ponum" + " left join item on item.itemnum=poline.itemnum"
//					+ " left join uditemcp on uditemcp.itemnum=item.itemnum"
//					+ " left join udconversion on udconversion.frommeasureunit=item.orderunit and udconversion.tomeasureunit=item.issueunit  and uditemcp.vendor =udconversion.vendor "
//					+ " where " + "uditemcp."+sqlWhere
//					+ " and po.status not in ('CAN') and poline.receiptscomplete=0 and poline.itemnum is not null and poline.udpurstatus is null"
//					+ " group by poline.itemnum) pol on uditemcp.itemnum=pol.itemnum"
//
//                    + " left join " + " (select matusetrans.itemnum,sum(matusetrans.quantity) iuqty from matusetrans"
//                    + " where matusetrans.storeloc like 'ZEE%' " 
//                    + " group by matusetrans.itemnum) iul on uditemcp.itemnum=iul.itemnum "
//
//					+ " where " + "uditemcp."+sqlWhere
//					+ " and uditemcp.maxlimit>0 and uditemcp.minlimit>0 and uditemcp.dept is not null and uditemcp.vendor is not null and item.status='ACTIVE' and item.udisfix=0) i"
//					+ " where (i.curbal+i.prqty+i.poqty-i.iuqty)<i.minlimit order by i.udcompany,i.vendor,i.itemnum";
			
//			String sql = " select i.udcompany,i.dept as uddept,i.vendor,i.purchaser,i.itemnum,ceil((i.maxlimit-(i.curbal+i.prqty+i.poqty-i.iuqty))/i.udconversion) qty,i.conversion,i.orderunit  from "
//					+"  (select uditemcp.udcompany,uditemcp.dept,udconversion.vendor,uditemcp.purchaser,uditemcp.itemnum,uditemcp.maxlimit,uditemcp.minlimit,udconversion.conversion,udconversion.frommeasureunit as orderunit, "
//					+"  nvl(invb.curbal,0) curbal, nvl(prl.prqty,0) prqty, nvl(pol.poqty,0) poqty, nvl(iul.iuqty,0) iuqty, "
//					+"  nvl(udconversion.conversion,1) udconversion  from uditemcp "
//					+"  left join item on uditemcp.itemnum=item.itemnum "
//					+"  left join uditemcpven on uditemcpven.itemnum = uditemcp.itemnum "
//					+"  left join udconversion on  udconversion.tomeasureunit=item.issueunit and uditemcp.itemnum = udconversion.itemnum and udconversion.vendor in (select vendor from uditemcpven where uditemcpven.itemnum = uditemcp.itemnum and uditemcpven.udactive='1') "
//					+"  left join  (select itemnum,sum(invbalances.curbal) curbal from locations "
//					+"  left join invbalances on invbalances.location=locations.location " + " where "+sqlWhere
//					+"  and invbalances.curbal>0  "
//					+"  group by invbalances.itemnum ) invb on uditemcp.itemnum=invb.itemnum"
//					+"  left join "
//					+"  (select prline.itemnum,sum(prline.orderqty * nvl(udconversion.conversion,1)) prqty from pr "
//					+"  left join prline on pr.prnum=prline.prnum  left join item on item.itemnum=prline.itemnum "
//					+"  left join uditemcp on uditemcp.itemnum=item.itemnum "
//					+"  left join uditemcpven on uditemcpven.itemnum = uditemcp.itemnum " 
//					+"  left join udconversion on  udconversion.tomeasureunit=item.issueunit and uditemcp.itemnum = udconversion.itemnum and udconversion.vendor in (select vendor from uditemcpven where uditemcpven.itemnum = uditemcp.itemnum and uditemcpven.udactive='1')" 
//					+ " where " + "uditemcp."+sqlWhere
//					+"  and pr.status not in ('CAN') and prline.polineid is null and prline.itemnum is not null and prline.udpurstatus is null "
//					+"  group by prline.itemnum) prl on uditemcp.itemnum=prl.itemnum "
//					+"  left join  "
//					+"  (select poline.itemnum,sum(poline.orderqty * nvl(udconversion.conversion,1)) poqty from po "
//					+"  left join poline on poline.ponum=po.ponum  left join item on item.itemnum=poline.itemnum "
//					+"  left join uditemcp on uditemcp.itemnum=item.itemnum "
//					+"  left join uditemcpven on uditemcpven.itemnum = uditemcp.itemnum "
//					+"  left join udconversion on  udconversion.tomeasureunit=item.issueunit and uditemcp.itemnum = udconversion.itemnum and udconversion.vendor in (select vendor from uditemcpven where uditemcpven.itemnum = uditemcp.itemnum and uditemcpven.udactive='1') "
//					+ " where " + "uditemcp."+sqlWhere
//					+"  and po.status not in ('CAN') and poline.receiptscomplete=0 and poline.itemnum is not null and poline.udpurstatus is null "
//					+"  group by poline.itemnum) pol on uditemcp.itemnum=pol.itemnum "
//					+"  left join    (select matusetrans.itemnum,sum(matusetrans.quantity) iuqty from matusetrans "
//					+"  where matusetrans.storeloc like 'ZEE%'  "
//					+"  group by matusetrans.itemnum)iul on uditemcp.itemnum=iul.itemnum "
//					+ " where " + "uditemcp."+sqlWhere
//					+"  and uditemcp.maxlimit>0 and uditemcp.minlimit>0 and uditemcp.dept is not null and uditemcp.purchaser is not null and item.status='ACTIVE' and item.udisfix=0 "
//					+"  and uditemcpven.udactive = '1'and udconversion.vendor is not null"
//					+"  ) i"
//					+"  where (i.curbal+i.prqty+i.poqty-i.iuqty)<i.minlimit and (i.maxlimit-(i.curbal+i.prqty+i.poqty-i.iuqty)) > 0 order by i.udcompany,i.vendor,i.itemnum";
					
			String sql = " select i.udcompany,i.dept as uddept,i.vendor,i.purchaser,i.itemnum,(ceil((i.maxlimit-(i.curbal+i.prqty+i.poqty))/i.udconversion / i.roundfactor))*i.roundfactor qty,i.conversion,i.orderunit  from "
					+"  (select uditemcp.udcompany,uditemcp.dept,udconversion.vendor,uditemcp.purchaser,uditemcp.itemnum,uditemcp.maxlimit,uditemcp.minlimit,udconversion.conversion,udconversion.frommeasureunit as orderunit, "
					+"  nvl(invb.curbal,0) curbal, nvl(prl.prqty,0) prqty, nvl(pol.poqty,0) poqty, nvl(iul.iuqty,0) iuqty, "
					+"  nvl(udconversion.conversion,1) udconversion,  nvl(udconversion.roundfactor,1) roundfactor from uditemcp "
					+"  left join item on uditemcp.itemnum=item.itemnum "
					+"  left join uditemcpven on uditemcpven.itemnum = uditemcp.itemnum "
					+"  left join udconversion on  udconversion.tomeasureunit=item.issueunit and uditemcp.itemnum = udconversion.itemnum and udconversion.vendor in (select vendor from uditemcpven where uditemcpven.itemnum = uditemcp.itemnum and uditemcpven.udactive='1') "
					+"  left join  (select itemnum,sum(invbalances.curbal) curbal from locations "
					+"  left join invbalances on invbalances.location=locations.location " + " where "+sqlWhere
					+"  and invbalances.curbal>0   "
					+"  group by invbalances.itemnum ) invb on uditemcp.itemnum=invb.itemnum"
					+"  left join "
					+"  (select prline.itemnum,sum(prline.orderqty * nvl(udconversion.conversion,1)) prqty from pr "
					+"  left join prline on pr.prnum=prline.prnum  left join item on item.itemnum=prline.itemnum "
					+"  left join uditemcp on uditemcp.itemnum=item.itemnum "
					+"  left join udconversion on  uditemcp.itemnum = udconversion.itemnum and udconversion.vendor in (select vendor from uditemcpven where uditemcpven.itemnum = uditemcp.itemnum and uditemcpven.udactive='1') " 
					+ " where " + "uditemcp."+sqlWhere
					+"  and pr.status not in ('CAN') and prline.polineid is null and prline.itemnum is not null and prline.udpurstatus is null "
					+"  group by prline.itemnum) prl on uditemcp.itemnum=prl.itemnum "
					+"  left join  "
					+"  (select poline.itemnum,sum(poline.orderqty * nvl(udconversion.conversion,1)) poqty from po "
					+"  left join poline on poline.ponum=po.ponum  left join item on item.itemnum=poline.itemnum "
					+"  left join uditemcp on uditemcp.itemnum=item.itemnum "
					+"  left join udconversion on  uditemcp.itemnum = udconversion.itemnum and udconversion.vendor in (select vendor from uditemcpven where uditemcpven.itemnum = uditemcp.itemnum and uditemcpven.udactive='1') "
					+ " where " + "uditemcp."+sqlWhere
					+"  and po.status not in ('CAN') and poline.receiptscomplete=0 and poline.itemnum is not null and poline.udpurstatus is null and po.udcompany='ZEE' "
					+"  group by poline.itemnum) pol on uditemcp.itemnum=pol.itemnum "
					+"  left join    (select matusetrans.itemnum,sum(matusetrans.quantity) iuqty from matusetrans "
					+"  where matusetrans.storeloc like 'ZEE%'  "
					+"  group by matusetrans.itemnum)iul on uditemcp.itemnum=iul.itemnum "
					+ " where " + "uditemcp."+sqlWhere
					+"  and uditemcp.maxlimit>0 and uditemcp.minlimit>0 and uditemcp.dept is not null and uditemcp.purchaser is not null and item.status='ACTIVE' and item.udisfix=0 "
					+"  and uditemcpven.udactive = '1'and udconversion.vendor is not null"
					+"  ) i"
					+"  where (i.curbal+i.prqty+i.poqty)<i.minlimit and (i.maxlimit-(i.curbal+i.prqty+i.poqty)) > 0 order by i.udcompany,i.vendor,i.itemnum";
			
			ConnectionKey connectionKey = new ConnectionKey(getRunasUserInfo());
			connection = MXServer.getMXServer().getDBManager().getConnection(connectionKey);
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				InventoryBean inventory = new InventoryBean();
				String company = rset.getString("udcompany");
				if (company == null) {
					company = "";
				}
				String dept = rset.getString("uddept");
				if (dept == null) {
					dept = "";
				}
				String vendor = rset.getString("vendor");
				if (vendor == null) {
					vendor = "";
				}
				String purchaser = rset.getString("purchaser");
				if (purchaser == null) {
					purchaser = "";
				}
				String itemnum = rset.getString("itemnum");
				if (itemnum == null) {
					itemnum = "";
				}
				String orderunit = rset.getString("orderunit");
				if (orderunit == null) {
					orderunit = "";
				}
				double qty = rset.getDouble("qty");
			    double conversion = rset.getDouble("conversion");
				if (qty > 0) {
					inventory.setCompany(company);
					inventory.setDept(dept);
					inventory.setVendor(vendor);
					inventory.setPurchaser(purchaser);
					inventory.setItemnum(itemnum);
					inventory.setOrderqty(qty);
					inventory.setConversion(conversion);
					inventory.setOrderunit(orderunit);
					list.add(inventory);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	private void createPR(List<InventoryBean> list) throws RemoteException, MXException {
		Date sysdate = MXServer.getMXServer().getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sysdateStr = sdf.format(sysdate);

		Map<String, Map<String, Map<String, List<InventoryBean>>>> mapSet = list.stream().collect(Collectors.groupingBy(
				InventoryBean::getCompany,
				Collectors.groupingBy(InventoryBean::getDept, Collectors.groupingBy(InventoryBean::getVendor))));
		for (Entry<String, Map<String, Map<String, List<InventoryBean>>>> mapCom : mapSet.entrySet()) {
			String company = mapCom.getKey();
			Map<String, Map<String, List<InventoryBean>>> mapDeptSet = mapCom.getValue();

			String purchaser = CommonUtil.getValue("LOCATIONS", "udcompany='" + company + "'", "INVOWNER");
			for (Entry<String, Map<String, List<InventoryBean>>> mapDept : mapDeptSet.entrySet()) {
				String dept = mapDept.getKey();
				Map<String, List<InventoryBean>> mapVendorSet = mapDept.getValue();

				for (Entry<String, List<InventoryBean>> mapVendor : mapVendorSet.entrySet()) {
					String vendor = mapVendor.getKey();
					List<InventoryBean> inventoryList = mapVendor.getValue();
					int line = 1;
					int number = 1;
					UDPRSet prSet = null;
					UDPR pr = null;
					for (InventoryBean inventory : inventoryList) {
						String itemnum = inventory.getItemnum();
						if (!inventory.getPurchaser().isEmpty()) {
							purchaser = inventory.getPurchaser();
						}
						double orderqty = inventory.getOrderqty();
						double conversion = inventory.getConversion();
						String orderunit = inventory.getOrderunit();
						if (line == 1) {
							String langCode = CommonUtil.getValue("PERSON", "personid='" + purchaser + "'", "LANGUAGE");
							if (langCode == null) {
								langCode = "ZH";
							}
							UserInfo userinfo = getRunasUserInfo();
							userinfo.setLangCode(langCode);
							prSet = (UDPRSet) MXServer.getMXServer().getMboSet("PR", userinfo);
							prSet.setWhere("1=2");
							prSet.reset();

							String deptDesc = CommonUtil.getValue("UDDEPT", "deptnum='" + dept + "'", "DESCRIPTION");
							String vendorDesc = CommonUtil.getValue("COMPANIES", "company='" + vendor + "'", "NAME");

							pr = (UDPR) prSet.add();
							pr.setValue("udapptype", "PRZEE", 11L);
							pr.setValue("udmatstatus", "SPORADIC", 11L);
							pr.setValue("description", "Reorder:" + sysdateStr + ",Dept:" + dept + "," + deptDesc
									+ ",Supplier:" + vendor + "," + vendorDesc + "," + number, 11L);
							pr.setValue("exchangerate", 1, 11L);
							pr.setValue("exchangedate", sysdate, 11L);
							pr.setValue("udcreateby", purchaser, 2L);// 创建人
							pr.setValue("requestedby", purchaser, 2L);// 创建人
							pr.setValue("udcreatetime", sysdate, 11L);// 创建时间
							if (!company.isEmpty()) {
								pr.setValue("udcompany", company, 11L);
							}
							if (!dept.isEmpty()) {
								pr.setValue("uddept", dept, 11L);
							}
							pr.setValue("requireddate", CommonUtil.getCalDate(sysdate, 14), 2L);// 要求日期，默认14天后
							pr.setValue("udreorder", "1", 11L);// 是否重订购
							
							MboSetRemote prVendorSet = pr.getMboSet("UDPRVENDOR");
							PRVendor prVendor = (PRVendor) prVendorSet.add();
							prVendor.setValue("vendor", vendor, 11L);
							prVendor.setValue("isawarded", true, 2L);
							company = pr.getString("udcompany");
							number++;
						}
						if (pr != null) {
							
							MboSetRemote prlineSet = pr.getMboSet("PRLINE");
							UDPRLine prline = (UDPRLine) prlineSet.add();
							prline.setValue("itemnum", itemnum, 2L);
							String tax1code = CommonUtil.getValue("UDDEPT",
									"type='COMPANY' and deptnum='" + company + "'", "TAX1CODE");
							prline.setValue("tax1code", tax1code, 2L);
							prline.setValue("orderqty", orderqty, 11L);
							prline.setValue("udissueqty", orderqty*conversion, 11L);
							prline.setValue("conversion", conversion,11L);
							prline.setValue("orderunit", orderunit,11L);
						}
						if (prSet != null) {
							prSet.save();
							prSet.close();
						}
						if (line >= MAXLINE) {
							line = 0;
						}
						line++;
					}
				}
			}
		}
	}
}
	
	
