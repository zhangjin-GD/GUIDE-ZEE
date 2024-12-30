package guide.app.itemreq;

import java.rmi.RemoteException;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import guide.app.item.ItemCp;
import guide.app.item.ItemCpSet;
import psdi.app.item.Item;
import psdi.app.item.ItemSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class ItemReq extends UDMbo implements MboRemote {

	private final int KEYLEN = 4; // 编号流水号长度

	public ItemReq(MboSet ms) throws RemoteException {
		super(ms);
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

		if (!getMboValue("status").getInitialValue().asString().equalsIgnoreCase("APPR")
				&& getString("status").equalsIgnoreCase("APPR")) {
			// 创建Item
			insertItems();
		}
	}

	public void autoItemNum() throws RemoteException, MXException {

		if (this.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}

		MboSetRemote lineSet = this.getMboSet("UDITEMREQLINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			for (int i = 0; lineSet.getMbo(i) != null; i++) {
				MboRemote line = lineSet.getMbo(i);
				if (line.isNull("itemnum")) {
					String crid = line.getString("classstructureid");
					String keyNum = CommonUtil.autoKeyNum("UDITEMREQLINE", "ITEMNUMAUTO", crid, "", KEYLEN);
					if (!keyNum.isEmpty()) {
						boolean flag = true;
						while (flag) {
							boolean isItem = checkItemNum(keyNum);
							if (isItem) {
								keyNum = crid
										+ CommonUtil.getKeyDigit(keyNum, keyNum.length() - KEYLEN, keyNum.length());
							} else {
								flag = false;
							}
						}
						line.setValue("itemnumauto", keyNum, 11L);
						line.setValue("itemnum", keyNum, 11L);
						line.getThisMboSet().save();
					}
				}
			}
		}
	}

	private boolean checkItemNum(String itemnum) throws RemoteException, MXException {
		boolean flag = false;
		if (!itemnum.isEmpty()) {
			MXServer mxServer = MXServer.getMXServer();
			UserInfo adminInfo = mxServer.getSystemUserInfo();
			String langCode = this.getUserInfo().getLangCode();
			adminInfo.setLangCode(langCode);
			MboSetRemote tableSet = mxServer.getMboSet("ITEM", mxServer.getSystemUserInfo());
			if (!tableSet.isEmpty()) {
				tableSet.setWhere("itemnum='" + itemnum + "'");
				tableSet.reset();
				if (!tableSet.isEmpty()) {
					flag = true;
				}
			}
		}
		return flag;
	}

	private void insertItems() throws RemoteException, MXException {
		// 创建ITEM
		MboSetRemote lineSet = this.getMboSet("UDITEMREQLINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			for (int i = 0; lineSet.getMbo(i) != null; i++) {
				MboRemote line = lineSet.getMbo(i);
				String crid = line.getString("classstructureid");
				String itemnum = line.getString("itemnum");
				ItemSet itemSet = (ItemSet) MXServer.getMXServer().getMboSet("ITEM", this.getUserInfo());
				itemSet.setWhere("itemnum = '" + itemnum + "'");
				itemSet.reset();
				if (itemSet.isEmpty()) {
					String itemnumNew = CommonUtil.autoKeyNum("ITEM", "ITEMNUM", crid, "", KEYLEN);
					String originalnum = line.getString("originalnum");
					String description = line.getString("description");
					String orderunit = line.getString("orderunit");
					String issueunit = line.getString("issueunit");
					String classstructureid = line.getString("classstructureid");
					String purmethod = line.getString("purmethod");
					String abctype = line.getString("abctype");
					String modelnum = line.getString("modelnum");
					String specs = line.getString("specs");
					String manufacturer = line.getString("manufacturer");
					String techparam = line.getString("techparam");
					String partnum = line.getString("partnum");
					String udcompany = line.getString("udcompany");
					String materialtype = line.getString("materialtype");
					String remarks = line.getString("remarks");
					String purchaser = line.getString("purchaser");
					String keeper = line.getString("keeper");
					String storeloc = line.getString("storeloc");
					String uditemnum = line.getString("uditemnum");
					String udmatnum = line.getString("udmatnum");
					String uddept = line.getString("uditemreq.uddept");
					String assetnum = line.getString("assetnum");

					boolean isgrouppur = line.getBoolean("isgrouppur");
					boolean istechacce = line.getBoolean("istechacce");
					boolean issafety = line.getBoolean("issafety");
					boolean isfix = line.getBoolean("isfix");
					boolean isreturn = line.getBoolean("isreturn");
					boolean isgeneral = line.getBoolean("isgeneral");
					boolean isspare = line.getBoolean("isspare");
					boolean isimport = line.getBoolean("isimport");
					boolean isinspect = line.getBoolean("isinspect");

					Item item = (Item) itemSet.add();
					item.setValue("itemnum", itemnumNew, 2L);
					item.setValue("udoriginalnum", originalnum, 11L);
					item.setValue("description", description, 11L);
					item.setValue("orderunit", orderunit, 11L);
					item.setValue("issueunit", issueunit, 11L);
					item.setValue("classstructureid", classstructureid, 11L);
					item.setValue("udmodelnum", modelnum, 11L);
					item.setValue("udspecs", specs, 11L);
					item.setValue("udmanufacturer", manufacturer, 11L);
					item.setValue("udtechparam", techparam, 11L);
					item.setValue("udpartnum", partnum, 11L);
					item.setValue("udisgrouppur", isgrouppur, 11L);
					item.setValue("udistechacce", istechacce, 11L);
					item.setValue("udissafety", issafety, 11L);
					item.setValue("udisfix", isfix, 11L);
					item.setValue("udisimport", isimport, 11L);
					item.setValue("udisinspect", isinspect, 11L);
					item.setValue("udmaterialtype", materialtype, 11L);
					item.setValue("lottype", "LOT", 11L);
					
					/**
					 * ZEE-先进先出FIFO需使用无批次
					 * 2023-08-14 11:00:37
					 */
					if (line.getString("udcompany")!=null && line.getString("udcompany").equalsIgnoreCase("ZEE")) {
						item.setValue("lottype", "NOLOT", 11L);
					}
					
					item.setValue("status", "ACTIVE", 11L);
					ItemCpSet itemCpSet = (ItemCpSet) item.getMboSet("UDITEMCP");
					ItemCp itemCp = (ItemCp) itemCpSet.add();
					itemCp.setValue("itemnum", itemnumNew, 11L);
					itemCp.setValue("udcompany", udcompany, 11L);
					itemCp.setValue("purmethod", purmethod, 11L);
					itemCp.setValue("abctype", abctype, 11L);
					itemCp.setValue("isreturn", isreturn, 11L);
					itemCp.setValue("isgeneral", isgeneral, 11L);
					itemCp.setValue("isspare", isspare, 11L);
					itemCp.setValue("remarks", remarks, 11L);
					itemCp.setValue("purchaser", purchaser, 11L);
					itemCp.setValue("keeper", keeper, 11L);
					itemCp.setValue("storeloc", storeloc, 11L);
					itemCp.setValue("udmatnum", udmatnum, 11L);
					itemCp.setValue("uddept", uddept, 11L);
					itemCp.setValue("assetnum", assetnum, 11L);
					/**
					 * ZEE-给dept赋值
					 * 2023-08-15 09:42:33
					 */
					itemCp.setValue("dept", uddept, 11L);

					if (!line.isNull("supplycycle")) {
						double supplycycle = line.getDouble("supplycycle");
						itemCp.setValue("supplycycle", supplycycle, 11L);
					}
					if (!line.isNull("maxlimit")) {
						double maxlimit = line.getDouble("maxlimit");
						itemCp.setValue("maxlimit", maxlimit, 11L);
					}
					if (!line.isNull("minlimit")) {
						double minlimit = line.getDouble("minlimit");
						itemCp.setValue("minlimit", minlimit, 11L);
					}
					if (!line.isNull("lockstd")) {
						double lockstd = line.getDouble("lockstd");
						itemCp.setValue("lockstd", lockstd, 11L);
					}
					if (!line.isNull("teustd")) {
						double teustd = line.getDouble("teustd");
						itemCp.setValue("teustd", teustd, 11L);
					}
					if (!line.isNull("unitstd")) {
						double unitstd = line.getDouble("unitstd");
						itemCp.setValue("unitstd", unitstd, 11L);
					}
					if (!line.isNull("actionstd")) {
						double actionstd = line.getDouble("actionstd");
						itemCp.setValue("actionstd", actionstd, 11L);
					}
					if (!line.isNull("runstd")) {
						double runstd = line.getDouble("runstd");
						itemCp.setValue("runstd", runstd, 11L);
					}
					if (!line.isNull("calstd")) {
						double calstd = line.getDouble("calstd");
						itemCp.setValue("calstd", calstd, 11L);
					}

					itemCp.setValue("uditemnum", uditemnum, 11L);
					line.setValue("itemnum", itemnumNew, 11L);
					line.setValue("iteminfo", "物料已创建", 11L);
					itemSet.save();
					itemSet.close();
					
					/**
					 * ZEE-先进先出FIFO创建INVENTORY
					 * 2023-08-14 11:27:51
					 */
					if (line.getString("udcompany")!=null && line.getString("udcompany").equalsIgnoreCase("ZEE")) {
						MboSetRemote inventorySet = MXServer.getMXServer().getMboSet("INVENTORY", this.getUserInfo());
						MboRemote inventory = inventorySet.add(2L);
						inventory.setValue("itemsetid", "ITEMSET", 2L);
						inventory.setValue("manufacturer", manufacturer, 11L);
						inventory.setValue("modelnum", modelnum, 11L);
						inventory.setValue("orderunit", orderunit, 11L);
						inventory.setValue("binnum", "Z00002", 11L);
						inventory.setValue("controlacc", "COSCO", 2L);
						inventory.setValue("issueunit", issueunit, 11L);
						inventory.setValue("orgid", "COSCO", 2L);
						inventory.setValue("siteid", "CSPL", 2L);
						inventory.setValue("category", "STK", 2L);
						inventory.setValue("itemnum", itemnumNew, 2L);
						inventory.setValue("location", "ZEE-01", 2L);
						inventory.setValue("status", "ACTIVE", 2L);
						inventory.setValue("reorder", "1", 2L);
						inventory.setValue("costtype", "FIFO", 2L);
						inventorySet.save();
						inventorySet.close();
					}
				}
			}
		}
	}
}
