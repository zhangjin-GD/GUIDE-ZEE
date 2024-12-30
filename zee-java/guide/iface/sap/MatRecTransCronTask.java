package guide.iface.sap;


import guide.app.common.CommonUtil;
import guide.iface.sap.webservice.HearBean;
import guide.iface.sap.webservice.ItemWebService;

import java.rmi.RemoteException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class MatRecTransCronTask extends SimpleCronTask {

  
	public MatRecTransCronTask() throws RemoteException, MXException {

	}

	public void cronAction() {
		try {
			String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
			String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
			System.out.println("\nCronPOMAT-----------status" + sapStatus + "-----------debug" + sapDebug);
			if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
				String matUseSql = getParamAsString("sqlWhere");
				if(matUseSql == null || matUseSql.equalsIgnoreCase(""))
					matUseSql = "1=2";
				MboSetRemote matrectransSet = MXServer.getMXServer().getMboSet("MATRECTRANS",MXServer.getMXServer().getSystemUserInfo());
				matrectransSet.setWhere(matUseSql);
				if(!matrectransSet.isEmpty() && matrectransSet.count() > 0){
					MboRemote matrectrans = null;
					JSONObject Header = new JSONObject();
					JSONArray ItemSet = new JSONArray();
					MboSetRemote poSet = null;
					String recNum = "NULL";
					String curRecNum = "1000";
					int ct = matrectransSet.count()-1;
					for(int i=0;(matrectrans=matrectransSet.getMbo(i))!=null;i++){
						curRecNum = matrectrans.getString("udrecnum");
						poSet = matrectrans.getMboSet("PO");
						if(!poSet.isEmpty() && poSet.count() > 0){
							if(!curRecNum.equalsIgnoreCase(recNum)){
								if(i != 0){
									Header.put("item", ItemSet);
									dataToSap(Header, recNum);
								}
								recNum = curRecNum;
								if(matrectrans.getString("udztype").equalsIgnoreCase("Y")){//退货
									Header = CommonUtil.getMatRecHeader(poSet.getMbo(0), CommonUtil.getValue(matrectrans, "ORIGINALRECEIPT", "udrecnum"), matrectrans.getString("issuetype"), matrectrans.getString("udztype"));// 物资单号
								}else{
									Header = CommonUtil.getMatRecHeader(poSet.getMbo(0), recNum, matrectrans.getString("issuetype"), matrectrans.getString("udztype"));
								}
								ItemSet = new JSONArray();
							}
							ItemSet.put(getMatRecItem(matrectrans, poSet.getMbo(0)));
						}
						if(i == ct){
							Header.put("item", ItemSet);
							dataToSap(Header, recNum);
						}
					}
			    }
				matrectransSet.close();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} 

	}
	
	public void dataToSap(JSONObject Header, String recNum) throws RemoteException, MXException, JSONException {
		if (CommonUtil.getString(Header, "item").toString().length() > 2) {
			String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
			if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
				System.out.println("\n---------XML:" + Header.toString());
				CommonUtil.setSapStatus("MATRECTRANS", "udrecnum='"+recNum+"'", "0", "DEBUG");
				return;
			}
			String num = "";
			String status = "";
			try {
				HearBean result = ItemWebService.itemRequestWebService(Header.toString());
				num = result.getBELNR();
				status = result.getZHEADMSG();
				CommonUtil.ifaceLog(Header.toString(), getRunasUserInfo().getPersonId(), "MATRECTRANS", Header.getString("ZSTOCKNO"), num, status);
				CommonUtil.setSapStatus("MATRECTRANS", "udrecnum='"+recNum+"'", num, status);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private JSONObject getMatRecItem(MboRemote matrectrans, MboRemote po) throws RemoteException, JSONException, MXException {
		int flag = 1;
		if (matrectrans.getDouble("quantity") < 0)
			flag = -1;
		double totalcost = 0.00;
		MboRemote item = null;
		MboSetRemote itemSet = null;
//		String assetnum = null;
		JSONObject Item = new JSONObject();
		Item.put("ZSTOCKNO", "REC" + matrectrans.getString("udrecnum"));// 物资单号
		Item.put("ZSTOCKITEMNO", matrectrans.getString("udzitemno"));// 物资单项目号
		if(matrectrans.getString("udztype").equalsIgnoreCase("Y")){//退货
			Item.put("ZSTOCKNO", "REC" + CommonUtil.getValue(matrectrans, "ORIGINALRECEIPT", "udrecnum"));// 物资单号
			Item.put("ZSTOCKITEMNO", CommonUtil.getValue(matrectrans, "ORIGINALRECEIPT", "udzitemno"));// 物资单项目号
		}
//		item.put("WRBTR1", matrectrans.getString("itemnum"));//预留字段0
		Item.put("ZQUANTITY", String.format("%.2f", flag * matrectrans.getDouble("quantity")));// 数量
		Item.put("DMBTR3", String.format("%.2f", flag * matrectrans.getDouble("linecost")));// 成本
		Item.put("WAERS", CommonUtil.getValue(po, "UDCOMPANY", "currency"));// 货币码0
		
//		采购出入库
		totalcost = matrectrans.getDouble("linecost") + matrectrans.getDouble("tax1");
		Item.put("DMBTR1", String.format("%.2f", flag * totalcost));// 含税金额
		Item.put("DMBTR4", String.format("%.2f", flag * matrectrans.getDouble("tax1")));// 税额
		Item.put("MWSKZ", CommonUtil.getValue(matrectrans, "TAX1CODE", "description"));// 税代码
		Item.put("EBELN", matrectrans.getString("ponum"));// 采购凭证号
		Item.put("EBELP", matrectrans.getString("polinenum"));// 采购凭证号采购凭证项目编号
		Item.put("ZAUXFIELD", "T001-" + po.getString("udconnum"));// 付款条款(海外专用)+合同号
		
//		领料出入库
//		Item.put("KOSTL", sapItem.getString("kostl"));// 成本中心（出库字段）
//		Item.put("AUFNR", sapItem.getString("aufnr"));// 内部订单号（出库字段）
//		Item.put("ZREPAIRTYPE", sapItem.getString("WORKORDER.WORKTYPE.wtypedesc"));// 维修类型
//		Item.put("ZEQUIPCLASSNAME", sapItem.getString("wonum"));// 维修工单编号
		
		itemSet = matrectrans.getMboSet("ITEM");
		if (!itemSet.isEmpty() && itemSet.count() > 0) {
			item = itemSet.getMbo(0);
			Item.put("MTART", item.getString("udmaterialtype"));// 物料类型代码
			Item.put("MAKTX", item.getString("description"));// 物料描述(短文本)
			if(item.getString("description") == null || item.getString("description").equalsIgnoreCase("")){
				Item.put("MAKTX", matrectrans.getString("description"));// 物料描述(短文本)
			}
			Item.put("ZUNIT", matrectrans.getString("receivedunit"));// 单位
			Item.put("ZMATERIALCODE", matrectrans.getString("itemnum"));// 物料编码
			Item.put("ZMATERIALL1", matrectrans.getString("itemnum").substring(0, 2));// 物料大类
			Item.put("ZMATERIALL2", matrectrans.getString("itemnum").substring(0, 4));// 物料中类
			Item.put("ZMATERIALL3", matrectrans.getString("itemnum").substring(0, 6));// 物料小类
			Item.put("ZMATERIALDESCL1", CommonUtil.getValue(item, "CLASS1", "description"));// 物料大类
			Item.put("ZMATERIALDESCL2", CommonUtil.getValue(item, "CLASS2", "description"));// 物料中类
			Item.put("ZMATERIALDESCL3", CommonUtil.getValue(item, "CLASS3", "description"));// 物料小类
		}
		/*
		assetnum = sapItem.getString("assetnum");
		if (assetnum != null && !assetnum.equalsIgnoreCase("")) {
			MboSetRemote assetSet = sapItem.getMboSet("ASSET");
			if (!assetSet.isEmpty() && assetSet.count() > 0) {
				MboRemote asset = assetSet.getMbo(0);
				Item.put("ZEQUIPCODE", sapItem.getString("assetnum"));// 设备编号
				Item.put("ZEQUIPNAME", asset.getString("description"));// 设备描述
				Item.put("ZEQUIPCLASS", asset.getString("udassettypecode"));// 设备分类
				Item.put("ZEQUIPCLASSNAME", asset.getString("udassettypecode.description"));// 设备分类描述
				KOSTL = asset.getString("udcostcenter");
				if(KOSTL != null && KOSTL.equalsIgnoreCase("VIRTUAL")){
					KOSTL = CommonUtil.getValue(this, "UDDEPT", "COSTCENTER");
				}
			}
		} else {
			KOSTL = CommonUtil.getValue(this, "UDDEPT", "COSTCENTER");
		}
		*/
		Item.put("ZEAMITEMFIELD1", "");
		Item.put("ZEAMITEMFIELD2", "");
		Item.put("ZEAMITEMFIELD3", "");
		Item.put("ZEAMITEMFIELD4", "");
		Item.put("ZEAMITEMFIELD5", "");
		return Item;
	}
	
}