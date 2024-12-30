package guide.app.inventory;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import guide.app.common.CommonUtil;
import guide.app.inventory.bean.InventoryBean;
import guide.app.pr.UDPR;
import guide.app.pr.UDPRLine;
import guide.app.pr.UDPRSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class UDAutoReorderCronTask extends SimpleCronTask {

	// 重订购
	@Override
	public void cronAction() {
		try {
			System.out.println("----开始--重订购---");
			Map<String, List<InventoryBean>> list = getInventoryList();
			createPR(list);
			System.out.println("----结束--重订购---");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
	}

	private Map<String, List<InventoryBean>> getInventoryList() throws RemoteException, MXException {
		String sqlWhere = getParamAsString("sqlWhere");
		Map<String, List<InventoryBean>> map = new TreeMap<String, List<InventoryBean>>();

		MboSetRemote locSet = MXServer.getMXServer().getMboSet("locations", getRunasUserInfo());
		locSet.setWhere(sqlWhere);
		locSet.setOrderBy("location");
		locSet.reset();
		if (!locSet.isEmpty() && locSet.count() > 0) {
			for (int j = 0; locSet.getMbo(j) != null; j++) {
				String location = locSet.getMbo(j).getString("location");
				int i = 0;
				int k;
				int maxQty = 200;// 最大行数
				do {
					MboSetRemote mboSet = MXServer.getMXServer().getMboSet("inventory", getRunasUserInfo());
					SqlFormat format = new SqlFormat(
							"exists ( select 1 from ( select row_number() over(order by inventoryid) rnx,inventoryid rnxid from inventory "
									+ " where reorder=1 and orderqty>0 and minlevel>0 and location ='" + location + "' "
									+ " and exists(select 1 from item where itemnum=inventory.itemnum and status='ACTIVE')) xxx "
									+ " where rnxid=inventory.inventoryid and xxx.rnx between :1 and :2)");
					int a;
					if (i == 0) {
						a = i * maxQty;
					} else {
						a = i * maxQty + 1;
					}
					int b = (i + 1) * maxQty;
					format.setInt(1, a);
					format.setInt(2, b);
					mboSet.setWhere(format.format());
					mboSet.setOrderBy("inventoryid");
					mboSet.reset();
					if (!mboSet.isEmpty() && mboSet.count() > 0) {
						List<InventoryBean> arrayList = new ArrayList<InventoryBean>();
						for (k = 0; mboSet.getMbo(k) != null; k++) {
							MboRemote mbo = mboSet.getMbo(k);
							int inventoryid = mbo.getInt("inventoryid");
							String itemnum = mbo.getString("itemnum");// 物料
							double minlevel = mbo.getDouble("minlevel");// 重订购点
							double curbaltotal = mbo.getDouble("curbaltotal");// 库存数量（当前余量）
							double orderqty = mbo.getDouble("orderqty");// 采购数量
							MboSetRemote prlineSet = mbo.getMboSet("udprline");
							double sumPrQty = prlineSet.sum("orderqty");// PR数量
							MboSetRemote polineSet = mbo.getMboSet("udpoline");
							double sumPoQty = polineSet.sum("orderqty");// PO数量
							double itemQty = curbaltotal + sumPrQty + sumPoQty;
							// 重订购点 > 库存数量+PR数量+PO数量
							if (minlevel > itemQty) {
								InventoryBean inventory = new InventoryBean();
								inventory.setId(inventoryid);
								inventory.setItemnum(itemnum);
								inventory.setOrderqty(orderqty);
								arrayList.add(inventory);
							}
						}
						mboSet.close();
						if (arrayList.size() > 0) {
							map.put(location + "$" + i, arrayList);
						}
						if (k < maxQty) {
							break;
						}
						i++;
					} else {
						break;
					}
				} while (true);

			}
		}
		locSet.close();
		return map;
	}

	private void createPR(Map<String, List<InventoryBean>> map) throws RemoteException, MXException {
		Date sysdate = MXServer.getMXServer().getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sysdateStr = sdf.format(sysdate);
		for (Entry<String, List<InventoryBean>> entry : map.entrySet()) {
			String key = entry.getKey();
			String keySub = key.substring(0, key.indexOf("$"));
			MboSetRemote mboSet = MXServer.getMXServer().getMboSet("locations", getRunasUserInfo());
			SqlFormat format = new SqlFormat("location ='" + keySub + "'");
			mboSet.setWhere(format.format());
			mboSet.reset();
			if (!mboSet.isEmpty() && mboSet.count() > 0) {
				MboRemote mbo = mboSet.getMbo(0);
				String location = mbo.getString("location");
				String invowner = mbo.getString("invowner");

				UDPRSet prSet = (UDPRSet) mbo.getMboSet("$PR", "PR", "1=2");
				UDPR pr = (UDPR) prSet.add();
				pr.setValue("udapptype", "PRMAT", 11L);
				pr.setValue("udmatstatus", "SPORADIC", 11L);
				pr.setValue("description", key + " 重订购/REORDER " + sysdateStr, 11L);
				pr.setValue("exchangerate", 1, 11L);
				pr.setValue("exchangedate", sysdate, 11L);
				pr.setValue("udcreateby", invowner, 2L);// 创建人
				pr.setValue("requestedby", invowner, 2L);// 创建人
				pr.setValue("udcreatetime", sysdate, 11L);// 创建时间
				pr.setValue("requireddate", CommonUtil.getCalDate(sysdate, 14), 2L);// 要求日期，默认14天后
				String udcompany = pr.getString("udcompany");
				MboSetRemote prlineSet = pr.getMboSet("PRLINE");
				for (InventoryBean inventory : entry.getValue()) {
					String itemnum = inventory.getItemnum();
					Double orderqty = inventory.getOrderqty();

					UDPRLine prline = (UDPRLine) prlineSet.add();
					prline.setValue("itemnum", itemnum, 2L);
					String tax1code = CommonUtil.getValue("UDDEPT", "type='COMPANY' and deptnum='"+udcompany+"'", "TAX1CODE");
					prline.setValue("tax1code", tax1code, 2L);
					prline.setValue("orderqty", orderqty, 2L);
					prline.setValue("storeloc", location, 2L);
				}

				prSet.save();
				prSet.close();
			}
			mboSet.close();
		}
	}
}
