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
import guide.app.pr.UDPR;
import guide.app.pr.UDPRLine;
import guide.app.pr.UDPRSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.security.ConnectionKey;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class UDAutoReorderItemCronTask extends SimpleCronTask {

	private static final int MAXLINE = 150;
	
	// 重订购
	@Override
	public void cronAction() {
		try {
			System.out.println("----开始--重订购--item--");
			List<InventoryBean> list = getInventoryList();
			createPR(list);
			System.out.println("----结束--重订购--item--");
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
			String sql = "select i.udcompany,i.itemnum,(i.maxlimit-(i.curbal+i.prqty+i.poqty-i.iuqty)) qty from"
					+ " (select uditemcp.udcompany,uditemcp.itemnum,uditemcp.maxlimit,uditemcp.minlimit,"
					+ " nvl(invb.curbal,0) curbal,nvl(prl.prqty,0) prqty,nvl(pol.poqty,0) poqty,nvl(iul.iuqty,0) iuqty"
					+ " from uditemcp"
					+ " left join item on uditemcp.itemnum=item.itemnum"
					+ " left join"
					+ " (select itemnum,sum(invbalances.curbal) curbal from locations"
					+ " left join invbalances on invbalances.location=locations.location"
					+ " where "+sqlWhere+" and invbalances.curbal>0 "
					+ " group by invbalances.itemnum ) invb on uditemcp.itemnum=invb.itemnum"
					+ " left join"
					+ " (select prline.itemnum,sum(prline.orderqty) prqty from pr"
					+ " left join prline on pr.prnum=prline.prnum"
					+ " where "+sqlWhere+" and pr.status not in ('CAN') and prline.polineid is null and prline.itemnum is not null"
					+ " group by prline.itemnum) prl on uditemcp.itemnum=prl.itemnum"
					+ " left join "
					+ " (select poline.itemnum,sum(poline.orderqty) poqty from po"
					+ " left join poline on poline.ponum=po.ponum"
					+ " where "+sqlWhere+" and po.status not in ('CAN') and poline.receiptscomplete=0 and poline.itemnum is not null"
					+ " group by poline.itemnum) pol on uditemcp.itemnum=pol.itemnum"
					+ " left join "
					+ " (select invuseline.itemnum,sum(invuseline.quantity) iuqty from invuse"
					+ " left join invuseline on invuseline.invusenum=invuse.invusenum"
					+ " where "+sqlWhere+" and invuse.status  not in ('CANCELLED','COMPLETE') and invuseline.usetype='ISSUE' and invuseline.itemnum is not null"
					+ " group by invuseline.itemnum) iul on uditemcp.itemnum=iul.itemnum"
					+ " where "+sqlWhere+" and uditemcp.maxlimit>0 and uditemcp.minlimit>0 and item.status='ACTIVE') i"
					+ " where (i.curbal+i.prqty+i.poqty-i.iuqty)<i.minlimit order by i.udcompany,i.itemnum";
			ConnectionKey connectionKey = new ConnectionKey(getRunasUserInfo());
			connection = MXServer.getMXServer().getDBManager().getConnection(connectionKey);
			stmt = connection.createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				InventoryBean inventory = new InventoryBean();
				String company = rset.getString("udcompany");
				String itemnum = rset.getString("itemnum");
				double qty = rset.getDouble("qty");
				if (qty > 0) {
					inventory.setCompany(company);
					inventory.setItemnum(itemnum);
					inventory.setOrderqty(qty);

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
		
		Map<String, List<InventoryBean>> groupObj = list.stream().collect(Collectors.groupingBy(InventoryBean::getCompany));
		for (Entry<String, List<InventoryBean>> enters : groupObj.entrySet()) {
			MboSetRemote locationsSet = MXServer.getMXServer().getMboSet("locations", getRunasUserInfo());
			SqlFormat format = new SqlFormat("udcompany ='" + enters.getKey() + "'");
			locationsSet.setWhere(format.format());
			locationsSet.setOrderBy("location");
			locationsSet.reset();
			if (!locationsSet.isEmpty() && locationsSet.count() > 0) {
				int line = 1;
				int number = 1;
				MboRemote locations = locationsSet.getMbo(0);
				String location = locations.getString("location");
				String invowner = locations.getString("invowner");
				String language = locations.getString("invowner.language");
				List<InventoryBean> listSet = enters.getValue();
				UDPRSet prSet = null;
				UDPR pr = null;
				String company = "";
				for (InventoryBean inventory:listSet) {
					if (line == 1) {
						UserInfo userinfo = getRunasUserInfo();
						userinfo.setLangCode(language);
						prSet = (UDPRSet) MXServer.getMXServer().getMboSet("PR", userinfo);
						prSet.setWhere("1=2");
						prSet.reset();
//						prSet = (UDPRSet) locations.getMboSet("$PR" + number, "PR", "1=2");
						pr = (UDPR) prSet.add();
						pr.setValue("udapptype", "PRMAT", 11L);
						pr.setValue("udmatstatus", "SPORADIC", 11L);
						pr.setValue("description", number + " 重订购/REORDER " + sysdateStr, 11L);
						pr.setValue("exchangerate", 1, 11L);
						pr.setValue("exchangedate", sysdate, 11L);
						pr.setValue("udcreateby", invowner, 2L);// 创建人
						pr.setValue("requestedby", invowner, 2L);// 创建人
						pr.setValue("udcreatetime", sysdate, 11L);// 创建时间
						pr.setValue("requireddate", CommonUtil.getCalDate(sysdate, 14), 2L);// 要求日期，默认14天后
						company = pr.getString("udcompany");
						number++;
					}
					if (pr != null) {
						MboSetRemote prlineSet = pr.getMboSet("PRLINE");
						String itemnum = inventory.getItemnum();
						Double orderqty = inventory.getOrderqty();
						UDPRLine prline = (UDPRLine) prlineSet.add();
						prline.setValue("itemnum", itemnum, 2L);
						String tax1code = CommonUtil.getValue("UDDEPT", "type='COMPANY' and deptnum='" + company + "'",
								"TAX1CODE");
						prline.setValue("tax1code", tax1code, 2L);
						prline.setValue("orderqty", orderqty, 2L);
						prline.setValue("storeloc", location, 2L);
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
			locationsSet.close();
		}
	}
}
