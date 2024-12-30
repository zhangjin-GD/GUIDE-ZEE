package guide.iface.sap;

import java.rmi.RemoteException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import guide.app.common.CommonUtil;
import guide.iface.sap.webservice.HearBean;
import guide.iface.sap.webservice.ItemWebService;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class SapHeader extends Mbo implements MboRemote {

	public SapHeader(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			String sapstatus = this.getString("sapstatus");
			if ("成功".equalsIgnoreCase(sapstatus) || "Success".equalsIgnoreCase(sapstatus)) {
				this.setFlag(READONLY, true);
			} else {
				this.setFlag(READONLY, false);
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
		String personId = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		this.setValue("createby", personId, 11L);// 创建人
		this.setValue("createtime", currentDate, 11L);// 创建时间
		this.setValue("budat", currentDate, 11L);// 凭证日期
		this.setValue("zdate1", currentDate, 11L);// 传输日期
		MboSetRemote personSet = this.getMboSet("$PERSON", "PERSON", "personid ='" + personId + "'");
		if (personSet != null && !personSet.isEmpty()) {
			MboRemote person = personSet.getMbo(0);
			this.setValue("udcompany", person.getString("udcompany"), 2L);
		}
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
		setValue("changeby", getUserInfo().getPersonId(), 11L);
		setValue("changetime", MXServer.getMXServer().getDate(), 11L);
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
	}

	public String getData() throws RemoteException, MXException, JSONException {
		JSONObject Header = new JSONObject();
		Header = getHeader();
		Header.put("item", getItem());
		return Header.toString();

	}

	public void dataToSap() throws RemoteException, MXException, JSONException {
		String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
		String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
		System.out.println("\nHEADER-----------status" + sapStatus + "-----------debug" + sapDebug);
		if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
			JSONObject header = new JSONObject();
			header = getHeader();
			header.put("item", getItem());
			if (CommonUtil.getString(header, "item").toString().length() > 2) {
				if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
					Object params[] = { "提示，XML:" + header.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
				String num = "";
				String status = "";
				try {
					HearBean result = ItemWebService.itemRequestWebService(header.toString());
					num = result.getBELNR();
					status = result.getZHEADMSG();
					CommonUtil.ifaceLog(header.toString(), getUserInfo().getPersonId(), getName(),
							header.getString("ZSTOCKNO"), num, status);
					if (num == null) {
						Object params[] = { "提示：" + status + "!" };
						throw new MXApplicationException("instantmessaging", "tsdimexception", params);
					}
					setValue("sapnum", num, 11L);
					setValue("sapstatus", status, 11L);
					//ZEE - SAP返回状态Success 107-110
					if(getString("udcompany")!=null && getString("udcompany").equalsIgnoreCase("ZEE")){
						setValue("sapstatus", "Success", 11L);
					}
					setValue("saptime", MXServer.getMXServer().getDate(), 11L);
				} catch (Exception e) {
					e.printStackTrace();
					Object params[] = { "提示：" + e.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
			}
		}
	}

	private JSONObject getHeader() throws JSONException, RemoteException, MXException {
		JSONObject Header = new JSONObject();
		Header.put("ZSOURCE", getString("zsource"));// 原系统，固定值
		Header.put("BUKRS", getString("bukrs"));// 公司代码
		Header.put("ZSTOCKNO", getString("zstockno"));// 物资单号
		Header.put("BUDAT", CommonUtil.getDateFormat(getDate("budat"), "yyyyMMdd"));// 凭证日期;
		Header.put("ZDATE1", CommonUtil.getDateFormat(getDate("zdate1"), "yyyyMMdd"));// 传输日期
		Header.put("ZTRAN", getString("ztran"));// 移动类型
		if (getString("ztran").indexOf("10") == 0) {
			Header.put("LIFNR", getString("lifnr"));// 供应商或债权人的帐号0
		}
		Header.put("ZEAMHEADFIELD1", "");
		Header.put("ZEAMHEADFIELD2", "");
		Header.put("ZEAMHEADFIELD3", "");
		Header.put("ZEAMHEADFIELD4", "");
		Header.put("ZEAMHEADFIELD5", "");
		return Header;
	}

	private JSONArray getItem() throws RemoteException, JSONException, MXException {
		JSONArray ItemSet = new JSONArray();
		MboSetRemote sapItemSet = getMboSet("UDSAPITEM");
		if (!sapItemSet.isEmpty() && sapItemSet.count() > 0) {
			double totalcost = 0.00;
			MboRemote sapItem = null;
			MboRemote item = null;
			MboSetRemote itemSet = null;
			String assetnum = null;
			String KOSTL = null;
			for (int i = 0; (sapItem = sapItemSet.getMbo(i)) != null; i++) {
				JSONObject Item = new JSONObject();
				Item.put("ZSTOCKNO", sapItem.getString("zstockno"));// 物资单号
				Item.put("ZSTOCKITEMNO", i + 1);// 物资单项目号
				// item.put("WRBTR1", matrectrans.getString("itemnum"));//预留字段0
				Item.put("ZQUANTITY", sapItem.getDouble("zquantity"));// 数量
				Item.put("DMBTR3", sapItem.getDouble("dmbtr3"));// 成本
				Item.put("WAERS", sapItem.getString("waers"));// 货币码0

				// 移动类型为采购出入库
				if (getString("ztran").indexOf("10") == 0) {
					totalcost = sapItem.getDouble("dmbtr3") + sapItem.getDouble("dmbtr4");
					Item.put("DMBTR1", String.format("%.2f", totalcost));// 含税金额
					Item.put("DMBTR4", sapItem.getDouble("dmbtr4"));// 税额
					Item.put("MWSKZ", sapItem.getString("mwskz"));// 税代码
					Item.put("EBELN", sapItem.getString("ebeln"));// 采购凭证号
					Item.put("EBELP", sapItem.getString("ebelp"));// 采购凭证号采购凭证项目编号
					Item.put("ZAUXFIELD", sapItem.getString("zauxfield"));// 付款条款(海外专用)+合同号
					// 物资类型为固定资产
					if ("2001".equals(sapItem.getString("mtart"))) {
						Item.put("ANLN1", sapItem.getString("anln1"));// 固定资产编号（采购入库 101移动类型）
					}
				} else { // 移动类型为领料出入库
					Item.put("KOSTL", sapItem.getString("kostl"));// 成本中心（出库字段）
					Item.put("AUFNR", sapItem.getString("aufnr"));// 内部订单号（出库字段）
					Item.put("ZREPAIRTYPE", sapItem.getString("wtypedesc"));// 维修类型
					Item.put("ZEQUIPCLASSNAME", sapItem.getString("wonum"));// 维修工单编号
				}

				itemSet = sapItem.getMboSet("ITEM");
				if (!itemSet.isEmpty() && itemSet.count() > 0) {
					item = itemSet.getMbo(0);
					Item.put("MTART", item.getString("udmaterialtype"));// 物料类型代码
					Item.put("MAKTX", item.getString("description"));// 物料描述(短文本)
					Item.put("ZUNIT", item.getString("orderunit"));// 单位
					Item.put("ZMATERIALCODE", sapItem.getString("itemnum"));// 物料编码
					Item.put("ZMATERIALL1", sapItem.getString("itemnum").substring(0, 2));// 物料大类
					Item.put("ZMATERIALL2", sapItem.getString("itemnum").substring(0, 4));// 物料中类
					Item.put("ZMATERIALL3", sapItem.getString("itemnum").substring(0, 6));// 物料小类
					Item.put("ZMATERIALDESCL1", CommonUtil.getValue(item, "CLASS1", "description"));// 物料大类
					Item.put("ZMATERIALDESCL2", CommonUtil.getValue(item, "CLASS2", "description"));// 物料中类
					Item.put("ZMATERIALDESCL3", CommonUtil.getValue(item, "CLASS3", "description"));// 物料小类
				} else {
					Item.put("MTART", sapItem.getString("mtart"));// 物料类型代码
					Item.put("MAKTX", sapItem.getString("maktx"));// 物料描述(短文本)
					Item.put("ZUNIT", sapItem.getString("zunit"));// 单位
				}
				assetnum = sapItem.getString("assetnum");
				if (assetnum != null && !assetnum.equalsIgnoreCase("")) {
					MboSetRemote assetSet = sapItem.getMboSet("ASSET");
					if (!assetSet.isEmpty() && assetSet.count() > 0) {
						MboRemote asset = assetSet.getMbo(0);
						Item.put("ZEQUIPCODE", sapItem.getString("assetnum"));// 设备编号
						Item.put("ZEQUIPNAME", asset.getString("description"));// 设备描述
						Item.put("ZEQUIPCLASS", asset.getString("udassettypecode"));// 设备分类
						Item.put("ZEQUIPCLASSNAME", asset.getString("udassettypecode.name"));// 设备分类描述
						KOSTL = asset.getString("udcostcenter");
						if (KOSTL != null && KOSTL.equalsIgnoreCase("VIRTUAL")) {
							KOSTL = CommonUtil.getValue(this, "UDDEPT", "COSTCENTER");
						}
					}
				} else {
					KOSTL = sapItem.getString("kostl");
				}
				Item.put("KOSTL", KOSTL);// 成本中心（出库字段）

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
