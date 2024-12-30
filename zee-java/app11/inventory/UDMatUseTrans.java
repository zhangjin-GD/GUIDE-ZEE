package guide.app.inventory;

import guide.app.common.CommonUtil;
import guide.app.workorder.UDWO;
import guide.iface.sap.webservice.HearBean;
import guide.iface.sap.webservice.ItemWebService;

import java.rmi.RemoteException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import psdi.app.inventory.MatUseTrans;
import psdi.app.inventory.MatUseTransRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
/**
 *@function:ZEE-工单里使用出库子表
 *@author:zj
 *@date:2024-01-17 11:36:29
 *@modify:
 */
public class UDMatUseTrans extends MatUseTrans implements MatUseTransRemote{

	public UDMatUseTrans(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}
	
	@Override
	public void save() throws MXException, RemoteException {
		super.save();
		MboRemote owner = getOwner();
		if (owner!=null && owner instanceof UDWO) {
			String udcompany = owner.getString("udcompany");
			if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
				String itemnum = getString("itemnum");
				String storeloc = getString("storeloc");
				String binnum = getString("binnum");
				MboSetRemote udinvstocklineSet = MXServer.getMXServer().getMboSet("UDINVSTOCKLINE", MXServer.getMXServer().getSystemUserInfo());
				udinvstocklineSet.setWhere(" itemnum ='" + itemnum + "' and storeloc='"+storeloc+"' and binnum='"+binnum+"' and invstocknum in (select invstocknum from udinvstock where status not in ('APPR','CAN','CLOSE')) ");
				udinvstocklineSet.reset();
				if (!udinvstocklineSet.isEmpty() && udinvstocklineSet.count() > 0) {
					for (int j = 0; j < udinvstocklineSet.count(); j++) {
						MboRemote udinvstockline = udinvstocklineSet.getMbo(j);
						udinvstockline.setValue("remark", "DELETE", 11L);
					}
				}
				udinvstocklineSet.save();
				udinvstocklineSet.close();
				
				/**
				 * ZEE-领料时,udsapnum为空,则传SAP
				 * 2024-04-02 15:50:39
				 */
				String udsapnum = getString("udsapnum");
				String issuetype = getString("issuetype");
				if (issuetype != null && issuetype.equalsIgnoreCase("ISSUE") && udsapnum.equalsIgnoreCase("")) {
					if (storeloc.equalsIgnoreCase("ZEE-01")) { //正常库房
						try {
							issueDataToSap(); //出库信息
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else if (storeloc.equalsIgnoreCase("ZEE-02")) { //寄售库
						try {
							receiveDataJSToSap(); //入库信息
							issueDataToSap(); //出库信息
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				
				/**
				 * ZEE-退库,udsapnum为空,则传SAP
				 *2024-09-24 09:50:39
				 */
					if (issuetype != null && issuetype.equalsIgnoreCase("RETURN") && udsapnum.equalsIgnoreCase("")) {
						if (storeloc.equalsIgnoreCase("ZEE-01")) { //正常库房
							try {
								issueDataToSap(); //退库信息
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else if (storeloc.equalsIgnoreCase("ZEE-02")) { //寄售库
							try {
								receiveDataJSToSap(); //入库信息
								issueDataToSap(); //出库信息
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				
				
			}
		}
	}
	
	private void issueDataToSap() throws RemoteException, MXException, JSONException {
		String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
		String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
		System.out.println("\nSapStatus-----------status" + sapStatus + "-----------Debug" + sapDebug);
		if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
			JSONObject Header = new JSONObject();
			Header = getZEEMatUseHeader(this);
			Header.put("item", getZEEMatUseItem(this));
			if (CommonUtil.getString(Header, "item").toString().length() > 2) {
				if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
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
					setValue("udsapnum", num, 11L);
					setValue("udsapstatus", status, 11L);
//					setValue("udrecnum", getRecnum(), 11L);
					//ZEE - SAP返回状态Success 140-143
					if (getOwner()!=null && getOwner() instanceof UDWO) {
						String udcompany = getOwner().getString("udcompany");
						if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
							setValue("udsapstatus", "Success", 11L);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Object params[] = { "提示：SAP，" + e.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
			}
		}
	}
	
	private void receiveDataJSToSap() throws RemoteException, MXException, JSONException {
		String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
		String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
		System.out.println("\ndataJSToSap-----------status" + sapStatus + "-----------debug" + sapDebug);
		if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
			JSONObject Header = new JSONObject();
//			Header = CommonUtil.getMatHeader(this, "RCS" + getString("INVUSELINE.invusenum"), "101", MXServer.getMXServer().getDate(), getString("udvendor"));
			Header = getJSHeader(this, "RCS" + getString("refwo"), "101", MXServer.getMXServer().getDate(), "1000134629");
			Header.put("item", getZEEMatRcsItem());
			if (CommonUtil.getString(Header, "item").toString().length() > 2) {
				if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
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
					setValue("udsapnum", num, 11L);
					setValue("udsapstatus", status, 11L);
					//ZEE - SAP返回状态Success 181-184
					if (getOwner()!=null && getOwner() instanceof UDWO) {
						String udcompany = getOwner().getString("udcompany");
						if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
							setValue("udsapstatus", "Success", 11L);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Object params[] = { "提示：SAP，" + e.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
			}
		}
	}
	
	private JSONArray getZEEMatRcsItem() throws RemoteException, JSONException, MXException {
		JSONArray ItemSet = new JSONArray();
//		MboRemote invuseline = null;
//		MboSetRemote invuselineSet = getMboSet("INVUSELINE");
		MboRemote item = null;
		MboSetRemote itemSet = null;
//		if (!invuselineSet.isEmpty() && invuselineSet.count() > 0) {
//			for (int i = 0; (invuseline = invuselineSet.getMbo(i)) != null; i++) {
				JSONObject Item = new JSONObject();
				Item.put("ZSTOCKNO", "RCS" + getString("refwo"));// 物资单号
				Item.put("ZSTOCKITEMNO", getInt("matusetransid") % 100);// 物资单项目号

				Item.put("ZQUANTITY", String.format("%.2f", getDouble("quantity")));// 数量
//				Item.put("DMBTR3", String.format("%.2f", getDouble("linecost")));// 不含税金额
				Item.put("DMBTR3", "100");// 不含税金额
//				Item.put("WAERS", CommonUtil.getValue(this, "UDCOMPANY", "currency"));// 货币码0
				Item.put("WAERS", "EUR");// 货币码0

				// 采购出入库
//				Item.put("DMBTR1", String.format("%.2f", getDouble("linecost")));// 含税金额
				Item.put("DMBTR1", "120");// 含税金额
//				Item.put("DMBTR4", String.format("%.2f", getDouble("udtax1")));// 税额
				Item.put("DMBTR4", "111.11");// 税额
//				Item.put("MWSKZ", CommonUtil.getValue(invuseline, "UDTAX1CODE", "description"));// 税代码
				Item.put("MWSKZ", "1L");// 税代码

				Item.put("EBELN", getString("refwo"));// 采购凭证号
				Item.put("EBELP", getInt("matusetransid") % 100);// 采购凭证号采购凭证项目编号
				Item.put("ZAUXFIELD", "T001-" + getString("refwo"));// 付款条款(海外专用)+合同号

				itemSet = getMboSet("ITEM");
				if (!itemSet.isEmpty() && itemSet.count() > 0) {
					item = itemSet.getMbo(0);
					Item.put("MTART", item.getString("udmaterialtype"));// 物料类型代码
					Item.put("MAKTX", item.getString("description").replaceAll("[<>&]", ""));// 物料描述(短文本)
					if (item.getString("description") == null || item.getString("description").equalsIgnoreCase("")) {
						Item.put("MAKTX", getString("description").replaceAll("[<>&]", ""));// 物料描述(短文本)
					}
					Item.put("ZUNIT", getString("issueunit"));// 单位
					Item.put("ZMATERIALCODE", getString("itemnum"));// 物料编码
					Item.put("ZMATERIALL1", getString("itemnum").substring(0, 2));// 物料大类
					Item.put("ZMATERIALL2", getString("itemnum").substring(0, 4));// 物料中类
					Item.put("ZMATERIALL3", getString("itemnum").substring(0, 6));// 物料小类
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
//			}
//		}
		return ItemSet;
	}
	
	private static JSONObject getZEEMatUseHeader(MboRemote matuse) throws JSONException, RemoteException, MXException {
		JSONObject Header = new JSONObject();
		Header.put("ZSOURCE", CommonUtil.getValue("UDDEPT","deptnum='ZEE'","sapzsource"));// 原系统，固定值
		Header.put("BUKRS", CommonUtil.getValue("UDDEPT","deptnum='ZEE'","costcenter"));// 成本中心
		Header.put("ZSTOCKNO", "ZEEUSE" + matuse.getString("refwo"));// 物资单号-唯一键值
		Header.put("BUDAT", CommonUtil.getCurrentDateFormat("yyyyMMdd"));// 凭证日期;
		Header.put("ZDATE1", CommonUtil.getCurrentDateFormat("yyyyMMdd"));// 传输日期
		Header.put("ZTRAN", matuse.getString("udzeemovementtype"));// 移动类型
		Header.put("ZEAMHEADFIELD1", "");
		Header.put("ZEAMHEADFIELD2", "");
		Header.put("ZEAMHEADFIELD3", "");
		Header.put("ZEAMHEADFIELD4", "");
		Header.put("ZEAMHEADFIELD5", "");
		return Header;
	}
	
	public static JSONArray getZEEMatUseItem(MboRemote matuse) throws RemoteException, JSONException, MXException {
		JSONArray ItemSet = new JSONArray();
		MboRemote item = null;
		MboSetRemote itemSet = null;
		String KOSTL = null;
		JSONObject Item = new JSONObject();
//		Item.put("ZSTOCKNO", "ZEEUSE" + matuse.getInt("matusetransid"));// 物资单号
		Item.put("ZSTOCKNO", "ZEEUSE" + matuse.getString("refwo"));// 物资单号
		Item.put("ZSTOCKITEMNO", matuse.getInt("matusetransid") % 100 );// 物资单项目号
		Item.put("ZQUANTITY", matuse.getDouble("quantity"));// 数量
		
		//寄售金额UDPREDICTTAXPRICE
		if(matuse.getString("storeloc") !=null && matuse.getString("storeloc").equalsIgnoreCase("ZEE-02")){
//			Item.put("DMBTR3", String.format("%.2f", matuse.getMboSet("UDZEEINVBALANCES").getMbo(0).getDouble("udpredicttaxprice")));// 成本
			Item.put("DMBTR3", "100");// 成本
		}else{
			Item.put("DMBTR3", String.format("%.2f", matuse.getDouble("linecost")));// 成本
		}
		Item.put("WAERS", CommonUtil.getValue("UDDEPT","deptnum='ZEE'","currency"));// 货币码0

		itemSet = matuse.getMboSet("ITEM");
		if (!itemSet.isEmpty() && itemSet.count() > 0) {
			item = itemSet.getMbo(0);
			Item.put("MTART", item.getString("udmaterialtype"));// 物料类型代码
			Item.put("MAKTX", item.getString("description").replaceAll("[<>&]", ""));// 物料描述(短文本)
			if(item.getString("description") == null || item.getString("description").equalsIgnoreCase("")){
				Item.put("MAKTX", matuse.getString("description").replaceAll("[<>&]", ""));// 物料描述(短文本)
			}
			Item.put("ZUNIT", item.getString("orderunit"));// 单位
			Item.put("ZMATERIALCODE", matuse.getString("itemnum"));// 物料编码
			Item.put("ZMATERIALL1", matuse.getString("itemnum").substring(0, 2));// 物料大类
			Item.put("ZMATERIALL2", matuse.getString("itemnum").substring(0, 4));// 物料中类
			Item.put("ZMATERIALL3", matuse.getString("itemnum").substring(0, 6));// 物料小类
			Item.put("ZMATERIALDESCL1", CommonUtil.getValue(item, "CLASS1", "description"));// 物料大类
			Item.put("ZMATERIALDESCL2", CommonUtil.getValue(item, "CLASS2", "description"));// 物料中类
			Item.put("ZMATERIALDESCL3", CommonUtil.getValue(item, "CLASS3", "description"));// 物料小类
		}
		if (matuse.getString("assetnum") != null && !matuse.getString("assetnum").equalsIgnoreCase("")) {
			MboSetRemote assetSet = matuse.getMboSet("ASSET");
			if (!assetSet.isEmpty() && assetSet.count() > 0) {
				MboRemote asset = assetSet.getMbo(0);
				Item.put("ZEQUIPCODE", matuse.getString("assetnum"));// 设备编号
				Item.put("ZEQUIPNAME", asset.getString("description").replaceAll("[<>&]", ""));// 设备描述
				Item.put("ZEQUIPCLASS", asset.getString("udassettypecode"));// 设备分类
				Item.put("ZEQUIPCLASSNAME", asset.getString("udassettypecode.name"));// 设备分类描述
				KOSTL = asset.getString("udcostcenter");
				if (KOSTL == null || KOSTL.equalsIgnoreCase("")) {
					KOSTL = CommonUtil.getValue("UDDEPT","deptnum='ZEE'","costcenter");
				}
			}
		} else {
			KOSTL = CommonUtil.getValue("UDDEPT","deptnum='ZEE'","costcenter");
		}
		Item.put("KOSTL", KOSTL);// 成本中心0
		Item.put("AUFNR", "");// 内部订单号
		Item.put("ZREPAIRTYPE", matuse.getString("WORKORDER.WORKTYPE.wtypedesc"));// 维修类型
		Item.put("ZEQUIPCLASSNAME", matuse.getString("refwo"));// 维修工单编号
		Item.put("ZEAMITEMFIELD1", "");
		Item.put("ZEAMITEMFIELD2", "");
		Item.put("ZEAMITEMFIELD3", "");
		Item.put("ZEAMITEMFIELD4", "");
		Item.put("ZEAMITEMFIELD5", "");
		ItemSet.put(Item);
		return ItemSet;
	}
	
//	private String getRecnum() throws MXException,RemoteException {
//		String udrecnum = "";
//		int qty = 0;
//		int num = 0;
//		MboSetRemote matusetransSet = MXServer.getMXServer().getMboSet("MATUSETRANS", MXServer.getMXServer().getSystemUserInfo());
//		matusetransSet.setWhere(" refwo='"+getString("refwo")+"' ");
//		matusetransSet.reset();
//		qty = matusetransSet.count();
//		System.out.println("\n----625---qty---"+qty);
//		matusetransSet.close();
//		num = qty + 1;
//		System.out.println("\n----625---num---"+num);
//		udrecnum = getString("refwo") + "0" +num;
//		System.out.println("\n----625---udrecnum---"+udrecnum);
//		return udrecnum;
//	}
	
	public static JSONObject getJSHeader(MboRemote mbo, String num, String zTran, Date transDate, String vendor) throws JSONException, RemoteException, MXException {
		JSONObject Header = new JSONObject();
		Header.put("ZSOURCE", "610");// 原系统，固定值
		Header.put("BUKRS", "BE04");// 公司代码
		Header.put("ZSTOCKNO", num);// 物资单号
		Header.put("BUDAT", CommonUtil.getDateFormat(transDate, "yyyyMMdd"));// 凭证日期;
		Header.put("ZDATE1", CommonUtil.getDateFormat(transDate, "yyyyMMdd"));// 传输日期
		Header.put("ZTRAN", zTran);// 移动类型
		Header.put("LIFNR", "1000134629");// 供应商或债权人的帐号0
		Header.put("ZEAMHEADFIELD1", "");
		Header.put("ZEAMHEADFIELD2", "");
		Header.put("ZEAMHEADFIELD3", "");
		Header.put("ZEAMHEADFIELD4", "");
		Header.put("ZEAMHEADFIELD5", "");
		return Header;
	}
	
    private JSONArray getMatRcsItem() throws RemoteException, JSONException, MXException {
        JSONArray ItemSet = new JSONArray();
        MboRemote invuseline = null;
        MboSetRemote invuselineSet = getMboSet("INVUSELINE");
        MboRemote item = null;
        MboSetRemote itemSet = null;
        if (!invuselineSet.isEmpty() && invuselineSet.count() > 0) {
                for (int i = 0; (invuseline = invuselineSet.getMbo(i)) != null; i++) {
                        JSONObject Item = new JSONObject();
                        Item.put("ZSTOCKNO", "RCS" + getString("invusenum"));// 物资单号
                        Item.put("ZSTOCKITEMNO", invuseline.getInt("invuselinenum"));// 物资单项目号
                        if (getString("udapptype").equalsIgnoreCase("MATRETCS")) {// 退货
                                Item.put("ZSTOCKNO", "RCS" + CommonUtil.getValue(invuseline, "ORIGINALUSE", "invusenum"));// 物资单号
                                Item.put("ZSTOCKITEMNO", CommonUtil.getValue(invuseline, "ORIGINALUSE", "invuselinenum"));// 物资单项目号
                        }
                        Item.put("ZQUANTITY", String.format("%.2f", invuseline.getDouble("quantity")));// 数量
                        Item.put("DMBTR3", String.format("%.2f", invuseline.getDouble("udlinecost")));// 成本
                        Item.put("WAERS", CommonUtil.getValue(this, "UDCOMPANY", "currency"));// 货币码0

                        // 采购出入库
                        Item.put("DMBTR1", String.format("%.2f", invuseline.getDouble("udtotalcost")));// 含税金额
                        Item.put("DMBTR4", String.format("%.2f", invuseline.getDouble("udtax1")));// 税额
                        Item.put("MWSKZ", CommonUtil.getValue(invuseline, "UDTAX1CODE", "description"));// 税代码
                        Item.put("EBELN", invuseline.getString("invusenum"));// 采购凭证号
                        Item.put("EBELP", invuseline.getString("invuselinenum"));// 采购凭证号采购凭证项目编号
                        Item.put("ZAUXFIELD", "T001-" + getString("udconnum"));// 付款条款(海外专用)+合同号

                        itemSet = invuseline.getMboSet("ITEM");
                        if (!itemSet.isEmpty() && itemSet.count() > 0) {
                                item = itemSet.getMbo(0);
                                Item.put("MTART", item.getString("udmaterialtype"));// 物料类型代码
                                Item.put("MAKTX", item.getString("description").replaceAll("[<>&]", ""));// 物料描述(短文本)
                                if (item.getString("description") == null || item.getString("description").equalsIgnoreCase("")) {
                                        Item.put("MAKTX", invuseline.getString("description").replaceAll("[<>&]", ""));// 物料描述(短文本)
                                }
                                Item.put("ZUNIT", invuseline.getString("receivedunit"));// 单位
                                Item.put("ZMATERIALCODE", invuseline.getString("itemnum"));// 物料编码
                                Item.put("ZMATERIALL1", invuseline.getString("itemnum").substring(0, 2));// 物料大类
                                Item.put("ZMATERIALL2", invuseline.getString("itemnum").substring(0, 4));// 物料中类
                                Item.put("ZMATERIALL3", invuseline.getString("itemnum").substring(0, 6));// 物料小类
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
