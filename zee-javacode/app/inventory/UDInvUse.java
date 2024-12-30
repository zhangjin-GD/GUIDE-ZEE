package guide.app.inventory;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import guide.app.common.CommonUtil;
import guide.app.workorder.UDWO;
import guide.iface.sap.webservice.HearBean;
import guide.iface.sap.webservice.ItemWebService;
import psdi.app.inventory.InvUse;
import psdi.app.inventory.InvUseRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValueInfo;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDInvUse extends InvUse implements InvUseRemote {

	private static boolean isHashSetLoaded = false;
	private static HashSet<String> skipFieldCopy = new HashSet<String>();

	public UDInvUse(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
	}

	@Override
	public void initFieldFlagsOnMbo(String attrName) throws MXException {
		super.initFieldFlagsOnMbo(attrName);
		try {
			if (!this.toBeAdded()) {
				String personid = this.getUserInfo().getPersonId();
				String maxUserid = CommonUtil.getValue("GROUPUSER",
						"groupname = 'MAXADMIN' and userid='" + personid + "'", "USERID");
				if (maxUserid == null) {
					String status = getString("status");
					String createby = getString("udcreateby");
					boolean isInWF = CommonUtil.isInWF(this);
					boolean assign = CommonUtil.isAssign(this, personid);
					// 启用流程
					if (isInWF) {
						if (assign) {
							if (personid.equalsIgnoreCase(createby)) {
								attributeReadonly(false);
							} else {
								attributeReadonly(true);
							}
						} else {
							attributeReadonly(true);
						}
					} else {
						if (("ENTERED".equalsIgnoreCase(status) || "BACK".equalsIgnoreCase(status))
								&& personid.equalsIgnoreCase(createby)) {
							attributeReadonly(false);
						} else {
							attributeReadonly(true);
						}
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void attributeReadonly(boolean state) throws RemoteException, MXException {
		String[] attrMbo = { "description", "udwonum", "udusestatus", "udcreateby" };
		this.setFieldFlag(attrMbo, 7L, state);
		MboSetRemote invuseLineSet = this.getMboSet("INVUSELINE");
		invuseLineSet.setFlag(7L, state);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();

		String personid = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		this.setValue("udcreateby", personid, 2L);// 创建人
		this.setValue("udcreatetime", currentDate, 11L);// 创建时间

		String appName = this.getThisMboSet().getApp();
		if (appName != null && !appName.isEmpty()) {
			String udappType = appName.replaceAll("UD", ""); // 替换UD
			this.setValue("udapptype", udappType, 11L);
			if (appName != null) {
				if ("UDMATUSEOT".equalsIgnoreCase(appName)) {// 领料单
					this.setValue("usetype", "ISSUE", 2L);
				} else if ("UDMATRETOT".equalsIgnoreCase(appName)) {// 退料单
					this.setValue("usetype", "ISSUE", 2L);
				} else if ("UDMATUSEWO".equalsIgnoreCase(appName) || "UDMATUSECS".equalsIgnoreCase(appName)) {// 工单领料
					this.setValue("usetype", "ISSUE", 2L);
				} else if ("UDMATRETWO".equalsIgnoreCase(appName) || "UDMATRETCS".equalsIgnoreCase(appName)) {// 工单退料
					this.setValue("usetype", "ISSUE", 2L);
				} else if ("UDTRANSFER".equalsIgnoreCase(appName)) {// 库存转移
					this.setValue("usetype", "TRANSFER", 2L);
				} else if("UDTRANSFERZEE".equalsIgnoreCase(appName) && getString("udcompany").equalsIgnoreCase("ZEE")){
					//ZEE - 库存转移应用程序113-116
					this.setValue("usetype", "TRANSFER", 2L);
				}
			}
		}

		this.setValue("udoldretstatus", "WAPPR", 11L);

		MboSetRemote maxUserSet = this.getMboSet("$MAXUSER", "MAXUSER");
		maxUserSet.setWhere("personid ='" + personid + "'");
		maxUserSet.reset();
		if (maxUserSet != null && !maxUserSet.isEmpty()) {
			MboRemote maxUser = maxUserSet.getMbo(0);
			this.setValue("fromstoreloc", maxUser.getString("defstoreroom"), 2L);
		}

		if ("UDMATUSECS".equalsIgnoreCase(appName) || "UDMATRETCS".equalsIgnoreCase(appName)) {
			String udcompany = getString("udcompany");
			MboSetRemote locationsSet = getMboSet("$LOCATIONS", "LOCATIONS",
					"udcompany ='" + udcompany + "' and udisconsignment=1");
			if (locationsSet != null && !locationsSet.isEmpty()) {
				MboRemote locations = locationsSet.getMbo(0);
				this.setValue("fromstoreloc", locations.getString("location"), 2L);
			}
		}

	}

	@Override
	public void save() throws MXException, RemoteException {
		super.save();

		String appType = getString("udapptype");
		String status = getString("status");
		/**
		 * ZEE-2024-05-28 16:28:04
		 * ZEE不校验国内的逻辑
		 */
		if (!getString("udcompany").equalsIgnoreCase("ZEE") && appType != null && status != null && ("ENTERED".equalsIgnoreCase(status) || (!getMboValue("status").getInitialValue().asString().equalsIgnoreCase("COMPLETE")&& "COMPLETE".equalsIgnoreCase(status)))&& ("MATUSEWO".equalsIgnoreCase(appType) || "MATUSEOT".equalsIgnoreCase(appType)|| "MATUSECS".equalsIgnoreCase(appType))) {// 领料
			checkQuantity();
			// setParentDesc();
		}
		
		/**
		 * ZEE
		 * 2023-07-17 10:35:47
		 */
		String udcompany = getString("udcompany");
		if (status!=null && udcompany!=null && status.equalsIgnoreCase("COMPLETE") && udcompany.equalsIgnoreCase("ZEE")) {
			MboSetRemote invuselineSet = getMboSet("INVUSELINE");
			if (!invuselineSet.isEmpty() && invuselineSet.count() > 0) {
				for (int i = 0; i < invuselineSet.count(); i++) {
					MboRemote invuseline = invuselineSet.getMbo(i);
					String itemnum = invuseline.getString("itemnum");
					String fromstoreloc = invuseline.getString("fromstoreloc");
					String frombin = invuseline.getString("frombin");
					MboSetRemote udinvstocklineSet = MXServer.getMXServer().getMboSet("UDINVSTOCKLINE", MXServer.getMXServer().getSystemUserInfo());
					udinvstocklineSet.setWhere(" itemnum ='" + itemnum + "' and storeloc='"+fromstoreloc+"' and binnum='"+frombin+"' and invstocknum in (select invstocknum from udinvstock where status not in ('APPR','CAN','CLOSE')) ");
					udinvstocklineSet.reset();
					if (!udinvstocklineSet.isEmpty() && udinvstocklineSet.count() > 0) {
						for (int j = 0; j < udinvstocklineSet.count(); j++) {
							MboRemote udinvstockline = udinvstocklineSet.getMbo(j);
							udinvstockline.setValue("remark", "DELETE", 11L);
						}
					}
					udinvstocklineSet.save();
					udinvstocklineSet.close();
				}
			}
		}

	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
		if (this.isModified("UDOLDRETSTATUS")) {
			Date currentDate = MXServer.getMXServer().getDate();
			MboSetRemote matReturnSet = this.getMboSet("UDMATRETURN");
			if (!matReturnSet.isEmpty() && matReturnSet.count() > 0) {
				for (int i = 0; matReturnSet.getMbo(i) != null; i++) {
					MboRemote matReturn = matReturnSet.getMbo(i);
					matReturn.setValue("changedate", currentDate, 11L);
				}
			}
		}
	}

	@Override
	public void changeStatus(String status, Date date, String memo, long accessModifier)
			throws MXException, RemoteException {
		super.changeStatus(status, date, memo, accessModifier);

		if (status != null && status.equalsIgnoreCase("COMPLETE")) {
			// 校验
			check();

			String issueType = getString("udapptype");
			//ZEE-库存转移不传SAP
			if (issueType != null && !issueType.equalsIgnoreCase("TRANSFER") && !issueType.equalsIgnoreCase("TRANSFERZEE")) {
				try {
					boolean isSap = CommonUtil.getLocSap(getString("fromstoreloc"));
					if (isSap) {
						if (!issueType.equalsIgnoreCase("MATUSECS") && !issueType.equalsIgnoreCase("MATRETCS")) {
							dataToSap();
						} else if (issueType.equalsIgnoreCase("MATUSECS") || issueType.equalsIgnoreCase("MATRETCS")) {
							dataCSToSap();
							dataToSap();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void check() throws RemoteException, MXException {
		// 校验库存月结
		checkInvMthly();
		// 校验库存盘点
		// checkInvStock();
		// 校验物料是否禁用
		checkItem();
		// 校验时间
		try {
			checkBudat();
		} catch (RemoteException | MXException | ParseException e) {
			e.printStackTrace();
		}
	}

	private void checkBudat() throws RemoteException, MXException, ParseException {
		// 凭证时间验证
		String udcompany = getString("udcompany");
		Date udbudat1 = getDate("udbudat");
		String appName = this.getThisMboSet().getApp();
		if (appName != null && !appName.isEmpty()) {
			if (appName.equalsIgnoreCase("UDMATUSEWO") || appName.equalsIgnoreCase("UDMATUSEOT")) {
				MboRemote mbo = this.getThisMboSet().getMbo();
				if (udcompany.equalsIgnoreCase("GR02PCT")) {
					if (udbudat1 != null && !udbudat1.equals("")) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String udbudat = sdf.format(udbudat1);

						checkSapDate(mbo, udbudat);

					}
				} else {
					return;
				}
			}
		}
		// 结束
	}

	private void checkSapDate(MboRemote mbo, String udbudat) throws RemoteException, MXException, ParseException {
		MboSetRemote invuselineSet = mbo.getMboSet("INVUSELINE");
		if (!invuselineSet.isEmpty() && invuselineSet.count() > 0) {
			for (int i = 0; i < invuselineSet.count(); i++) {
				MboRemote invuseline = invuselineSet.getMbo(i);
				if (!invuseline.toBeDeleted()) {
					MboSetRemote matuSet = MXServer.getMXServer().getMboSet("MATUSETRANS",
							MXServer.getMXServer().getSystemUserInfo());
					matuSet.setWhere("itemnum='" + invuseline.getString("itemnum")
							+ "' and Issuetype='ISSUE' and to_date(transdate) > to_date('" + udbudat
							+ "','yyyy-mm-dd')'");
					matuSet.reset();
					if (!matuSet.isEmpty() && matuSet.count() > 0) {
						Object[] obj = { "Material code：" + invuseline.getString("itemnum")
								+ "The latest delivery time is longer than the voucher time, please select a new one！" };
						throw new MXApplicationException("udmessage", "error1", obj);
					}
					MboSetRemote matrSet = MXServer.getMXServer().getMboSet("MATRECTRANS",
							MXServer.getMXServer().getSystemUserInfo());
					matrSet.setWhere("itemnum='" + invuseline.getString("itemnum")
							+ "' and to_date(transdate) > to_date('" + udbudat + "','yyyy-mm-dd')'");
					matrSet.reset();
					if (!matrSet.isEmpty() && matrSet.count() > 0) {
						Object[] obj = { "Material code：" + invuseline.getString("itemnum")
								+ "The latest storage time is longer than the voucher time, please select a new one！" };
						throw new MXApplicationException("udmessage", "error1", obj);
					}
					matuSet.close();
					matrSet.close();
				}
			}
			invuselineSet.close();
		} else {
			return;
		}
	}

	private void checkItem() throws RemoteException, MXException {
		String udcompany = this.getString("udcompany");
		MboSetRemote invUseLineSet = this.getMboSet("INVUSELINE");
		if (!invUseLineSet.isEmpty() && invUseLineSet.count() > 0) {
			for (int i = 0; invUseLineSet.getMbo(i) != null; i++) {
				MboRemote invUseLine = invUseLineSet.getMbo(i);
				if (!invUseLine.toBeDeleted()) {
					String itemnum = invUseLine.getString("itemnum");
					MboSetRemote itemcpSet = invUseLine.getMboSet("$UDITEMCP" + i, "UDITEMCP",
							"udcompany='" + udcompany + "' and itemnum='" + itemnum + "' and isdisable=1");
					itemcpSet.reset();
					if (!itemcpSet.isEmpty() && itemcpSet.count() > 0) {
						Object params[] = { itemnum };
						throw new MXApplicationException("guide", "1115", params);
					}
				}
			}
		}
		invUseLineSet.close();
	}

	private void checkInvMthly() throws RemoteException, MXException {
		MboSetRemote invmthlySet = this.getMboSet("UDINVMTHLYCHECK");
		if (!invmthlySet.isEmpty() && invmthlySet.count() > 0) {
			MboRemote invmthly = invmthlySet.getMbo(0);
			String invmthlynum = invmthly.getString("invmthlynum");
			Object params[] = { invmthlynum };
			throw new MXApplicationException("guide", "1105", params);
		}
	}

	private void checkInvStock() throws RemoteException, MXException {
		MboSetRemote InvStockSet = this.getMboSet("UDINVSTOCKCHECK");
		if (!InvStockSet.isEmpty() && InvStockSet.count() > 0) {
			MboRemote invstock = InvStockSet.getMbo(0);
			String invstocknum = invstock.getString("invstocknum");
			Object params[] = { invstocknum };
			throw new MXApplicationException("guide", "1106", params);
		}
	}

	private void dataToSap() throws RemoteException, MXException, JSONException {
		String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
		String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
		System.out.println("\nINVUSE-----------status" + sapStatus + "-----------debug" + sapDebug);
		if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
			JSONObject Header = new JSONObject();
			Header = CommonUtil.getMatUseHeader(this);
			Header.put("item", CommonUtil.getMatUseItem(this));
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
				} catch (Exception e) {
					e.printStackTrace();
					Object params[] = { "提示：SAP，" + e.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
			}
		}
	}

	private void dataCSToSap() throws RemoteException, MXException, JSONException {
		String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
		String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
		System.out.println("\ndataCSToSap-----------status" + sapStatus + "-----------debug" + sapDebug);
		if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
			JSONObject Header = new JSONObject();
			if (getString("udapptype").equalsIgnoreCase("MATUSECS")) {// 入库
				Header = CommonUtil.getMatHeader(this, "RCS" + getString("invusenum"), "101",
						MXServer.getMXServer().getDate(), getString("udvendor"));
			} else if (getString("udapptype").equalsIgnoreCase("MATRETCS")) {// 退货
				String originaluse = getString("invusenum");
				MboSetRemote invuselineSet = getMboSet("INVUSELINE");
				if (!invuselineSet.isEmpty() && invuselineSet.count() > 0) {
					originaluse = CommonUtil.getValue(invuselineSet.getMbo(0), "ORIGINALUSE", "invusenum");
				}
				// 物资单号
				Header = CommonUtil.getMatHeader(this, "RCS" + originaluse, "101X", MXServer.getMXServer().getDate(),
						getString("udvendor"));
			}
			Header.put("item", getMatRcsItem());
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
					setValue("udsapnumcs", num, 11L);
					setValue("udsapstatuscs", status, 11L);
				} catch (Exception e) {
					e.printStackTrace();
					Object params[] = { "提示：SAP，" + e.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
			}
		}
	}

	private JSONArray getMatRcsItem() throws RemoteException, JSONException, MXException {
		JSONArray ItemSet = new JSONArray();
		MboRemote invuseline = null;
		MboSetRemote invuselineSet = getMboSet("INVUSELINE");
		MboRemote item = null;
		MboSetRemote itemSet = null;
		// String assetnum = null;
		// String KOSTL = null;
		if (!invuselineSet.isEmpty() && invuselineSet.count() > 0) {
			for (int i = 0; (invuseline = invuselineSet.getMbo(i)) != null; i++) {
				// assetnum = invuseline.getString("assetnum");
				JSONObject Item = new JSONObject();
				Item.put("ZSTOCKNO", "RCS" + getString("invusenum"));// 物资单号
				Item.put("ZSTOCKITEMNO", invuseline.getInt("invuselinenum"));// 物资单项目号
				if (getString("udapptype").equalsIgnoreCase("MATRETCS")) {// 退货
					Item.put("ZSTOCKNO", "RCS" + CommonUtil.getValue(invuseline, "ORIGINALUSE", "invusenum"));// 物资单号
					Item.put("ZSTOCKITEMNO", CommonUtil.getValue(invuseline, "ORIGINALUSE", "invuselinenum"));// 物资单项目号
				}
				// item.put("WRBTR1", invuseline.getString("itemnum"));//预留字段0
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

				// 领料出入库
				// Item.put("KOSTL", sapItem.getString("kostl"));// 成本中心（出库字段）
				// Item.put("AUFNR", sapItem.getString("aufnr"));// 内部订单号（出库字段）
				// Item.put("ZREPAIRTYPE", sapItem.getString("WORKORDER.WORKTYPE.wtypedesc"));//
				// 维修类型
				// Item.put("ZEQUIPCLASSNAME", sapItem.getString("wonum"));// 维修工单编号

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
				/*
				 * assetnum = sapItem.getString("assetnum"); if (assetnum != null &&
				 * !assetnum.equalsIgnoreCase("")) { MboSetRemote assetSet =
				 * sapItem.getMboSet("ASSET"); if (!assetSet.isEmpty() && assetSet.count() > 0)
				 * { MboRemote asset = assetSet.getMbo(0); Item.put("ZEQUIPCODE",
				 * sapItem.getString("assetnum"));// 设备编号 Item.put("ZEQUIPNAME",
				 * asset.getString("description"));// 设备描述 Item.put("ZEQUIPCLASS",
				 * asset.getString("udassettypecode"));// 设备分类 Item.put("ZEQUIPCLASSNAME",
				 * asset.getString("udassettypecode.description"));// 设备分类描述 KOSTL =
				 * asset.getString("udcostcenter"); if(KOSTL != null &&
				 * KOSTL.equalsIgnoreCase("VIRTUAL")){ KOSTL = CommonUtil.getValue(this,
				 * "UDDEPT", "COSTCENTER"); } } } else { KOSTL = CommonUtil.getValue(this,
				 * "UDDEPT", "COSTCENTER"); }
				 */
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

	/**
	 * 校验 （当前余量-(申请中数量+同单同批次数量)>本单申请数量)
	 * 
	 * @throws MXException
	 * @throws RemoteException
	 */
	public void checkQuantity() throws RemoteException, MXException {
		MboSetRemote invUseLineSet = this.getMboSet("INVUSELINE");
		if (invUseLineSet != null && !invUseLineSet.isEmpty()) {
			String linenum = "";
			for (int i = 0; invUseLineSet.getMbo(i) != null; i++) {
				double curbal = 0, qty = 0, borrowQty = 0, returnQty = 0;

				MboRemote invUseLine = invUseLineSet.getMbo(i);
				if (!invUseLine.toBeDeleted()) {
					String itemnum = invUseLine.getString("itemnum");
					int invuselineid = invUseLine.getInt("invuselineid");
					String invusenum = invUseLine.getString("invusenum");
					double quantity = invUseLine.getDouble("quantity");
					String frombin = invUseLine.getString("frombin");
					String fromlot = invUseLine.getString("fromlot");
					// 查询库存
					MboSetRemote invBalSet = invUseLine.getMboSet("UDINVBALCURBAL");
					if (invBalSet != null && !invBalSet.isEmpty()) {
						MboRemote invBal = invBalSet.getMbo(0);
						curbal = invBal.getDouble("curbal");
						System.out.println("\n---528---curbal---"+curbal);
						// 借用数量
						MboSetRemote borrowSet = invBal.getMboSet("UDSHAREUSELINE_BORROW");
						if (!borrowSet.isEmpty() && borrowSet.count() > 0) {
							borrowQty = borrowSet.sum("orderqty");
						}
						System.out.println("\n---528---borrowQty---"+borrowQty);
						// 归还数量
						MboSetRemote returnSet = invBal.getMboSet("UDSHAREUSELINE_RETURN");
						if (!returnSet.isEmpty() && returnSet.count() > 0) {
							returnQty = returnSet.sum("orderqty");
						}
						System.out.println("\n---528---returnQty---"+returnQty);
						
						double shareQty = borrowQty - returnQty;
						curbal = curbal - shareQty;
						System.out.println("\n---528---curbal-F---"+curbal);
					}
					// 其他领用在途（不含本单）
					MboSetRemote invUseLineQtySet = invUseLine.getMboSet("UDINVUSELINEQTY");
					if (invUseLineQtySet != null && !invUseLineQtySet.isEmpty()) {
						qty = invUseLineQtySet.sum("quantity");
						System.out.println("\n---528---qty---"+qty);
					}
					// 同单 同物料 批次
					double lot = 0.0;
					MboRemote owner = invUseLine.getOwner();
					MboSetRemote invUseLineLotSet = owner.getMboSet("INVUSELINE");
					if (invUseLineLotSet != null && !invUseLineLotSet.isEmpty()) {
						for (int j = 0; invUseLineLotSet.getMbo(j) != null; j++) {
							MboRemote invUseLineLot = invUseLineLotSet.getMbo(j);
							String itemnumNew = invUseLineLot.getString("itemnum");
							int invuselineidNew = invUseLineLot.getInt("invuselineid");
							String invusenumNew = invUseLineLot.getString("invusenum");
							String frombinNew = invUseLineLot.getString("frombin");
							String fromlotNew = invUseLineLot.getString("fromlot");

							if (!invUseLineLot.toBeDeleted() && invuselineid != invuselineidNew
									&& itemnum.equalsIgnoreCase(itemnumNew) && invusenum.equalsIgnoreCase(invusenumNew)
									&& frombin.equalsIgnoreCase(frombinNew) && fromlot.equalsIgnoreCase(fromlotNew)) {
								lot += invUseLineLot.getDouble("quantity");
							}
						}
					}
					String lotStr = String.format("%.2f", lot);
					lot = Double.parseDouble(lotStr);
					System.out.println("\n---528---lot---"+lot);
					double results = curbal - (qty + lot);
					String resultsStr = String.format("%.2f", results);
					results = Double.parseDouble(resultsStr);
					System.out.println("\n---528---results---"+results);
					System.out.println("\n---528---quantity---"+quantity);
					if (results < quantity) {
						int invuselinenum = invUseLine.getInt("invuselinenum");
						linenum += invuselinenum + ",";
					}
				}
			}
			if (linenum != null && !linenum.equalsIgnoreCase("")) {
				// 去掉逗号
				String params = linenum.substring(0, linenum.length() - 1);
				Object[] obj = { params };
				throw new MXApplicationException("guide", "1094", obj);
			}
		}
	}

	@Override
	public void copyInvBalancesSetForItems(MboSetRemote invBalancesSet) throws RemoteException, MXException {
		// 新增批次校验

		super.copyInvBalancesSetForItems(invBalancesSet);

		Vector<MboRemote> selectedMbos = invBalancesSet.getSelection();
		if (selectedMbos.size() > 0) {
			for (int i = 0; i < selectedMbos.size(); i++) {
				MboRemote mr = (MboRemote) selectedMbos.elementAt(i);
				String itemnum1 = mr.getString("itemnum");
				String lotnum1 = mr.getString("lotnum");
				String location1 = mr.getString("location");
				MboSetRemote invUseLineSet = this.getMboSet("INVUSELINE");
				if (!invUseLineSet.isEmpty() && invUseLineSet.count() > 0) {
					for (int j = 0; invUseLineSet.getMbo(j) != null; j++) {
						MboRemote invUseLine = invUseLineSet.getMbo(j);
						String itemnum2 = invUseLine.getString("itemnum");
						String lotnum2 = invUseLine.getString("fromlot");
						String location2 = invUseLine.getString("fromstoreloc");
						if (itemnum1.equalsIgnoreCase(itemnum2) && lotnum1.equalsIgnoreCase(lotnum2)
								&& location1.equalsIgnoreCase(location2)) {
							// 批次清空,重新设值触发2L
							invUseLine.setValueNull("fromlot", 2L);
							invUseLine.setValue("fromlot", lotnum1, 2L);
						}
					}
				}
			}
		}
	}

	// 先进先出校验
	public void checkLot() throws RemoteException, MXException {

		MboSetRemote invUseLineSet = this.getMboSet("INVUSELINE");
		if (invUseLineSet != null && !invUseLineSet.isEmpty()) {

			StringBuffer result = new StringBuffer();
			LinkedHashSet<String> valueSet = new LinkedHashSet<String>();
			String invusenum = this.getString("invusenum");
			String fromstoreloc = this.getString("fromstoreloc");

			for (int i = 0; invUseLineSet.getMbo(i) != null; i++) {
				MboRemote invUseLine = invUseLineSet.getMbo(i);
				String itemnum = invUseLine.getString("itemnum");
				valueSet.add(itemnum);
			}
			for (String value : valueSet) {
				MboSetRemote invLineSet = this.getMboSet("$INVUSELINE", "INVUSELINE",
						"invusenum='" + invusenum + "' and itemnum='" + value + "'");
				int invLineCount = invLineSet.count();
				for (int i = 0; invLineSet.getMbo(i) != null; i++) {
					boolean isPass = true;
					MboRemote invLine = invLineSet.getMbo(i);
					String itemnum1 = invLine.getString("itemnum");
					String location1 = invLine.getString("fromstoreloc");
					String binnum1 = invLine.getString("frombin");
					String lotnum1 = invLine.getString("fromlot");
					MboSetRemote invbalSet = this.getMboSet("$INVBALANCES", "INVBALANCES",
							"curbal>0 and location='" + fromstoreloc + "' and itemnum='" + value + "'");
					invbalSet.setOrderBy("physcntdate");

					int invBalCount = invbalSet.count();
					int count = 1;
					if (invBalCount > invLineCount) {
						count = invLineCount;
					} else {
						count = invBalCount;
					}
					for (int j = 0; j < count; j++) {
						MboRemote invbal = invbalSet.getMbo(j);
						String itemnum2 = invbal.getString("itemnum");
						String location2 = invbal.getString("location");
						String binnum2 = invbal.getString("binnum");
						String lotnum2 = invbal.getString("lotnum");
						if (itemnum1.equalsIgnoreCase(itemnum2) && location1.equalsIgnoreCase(location2)
								&& binnum1.equalsIgnoreCase(binnum2) && lotnum1.equalsIgnoreCase(lotnum2)) {
							isPass = false;
						}
					}
					if (isPass) {
						int invuselinenum = invLine.getInt("invuselinenum");
						result.append(invuselinenum).append(",");
					}
				}
			}
			if (result.length() > 0) {
				this.setValue("udcheckresults", "行" + result.toString() + "还存在更早批次，请确认！", 11L);
			} else {
				this.setValueNull("udcheckresults", 11L);
			}
		}
	}

	public void invUseWoInsert() throws RemoteException, MXException {
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof UDWO) {
			UDWO wo = (UDWO) parent;
			String personid = this.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			String personDept = "";
			String wonum = wo.getString("wonum");
			String status = wo.getString("status");
			String uddept = wo.getString("uddept");
			String currentMaxStatus = wo.getTranslator().toInternalString("WOSTATUS", status);// 内部状态值
			MboSetRemote personSet = this.getMboSet("$PERSON", "PERSON");
			personSet.setWhere("personid ='" + personid + "'");
			personSet.reset();
			if (personSet != null && !personSet.isEmpty()) {
				personDept = personSet.getMbo(0).getString("uddept");
			}
			String udcostcenter = parent.getString("asset.udcostcenter");// 成本中心
			// asset表的成本中心为空
			if (udcostcenter == null || udcostcenter.isEmpty()) {
				throw new MXApplicationException("guide", "1018");
			}
			// 工单部门与登陆人部门不同
			if (!uddept.equalsIgnoreCase(personDept)) {
				throw new MXApplicationException("guide", "1054");
			}
			// 工单内部状态为 WAPPR CAN CLOSE
			if ("WAPPR".equalsIgnoreCase(currentMaxStatus) || "CAN".equalsIgnoreCase(currentMaxStatus)
					|| "CLOSE".equalsIgnoreCase(currentMaxStatus)) {
				throw new MXApplicationException("guide", "1055");
			}

			MboSetRemote invUseWoSet = parent.getMboSet("UDINVUSEWO");
			MboRemote invUseWo = invUseWoSet.add();
			invUseWo.setValue("udapptype", "MATUSEWO", 11L);
			invUseWo.setValue("udcreateby", personid, 2L);// 创建人
			invUseWo.setValue("udcreatetime", currentDate, 11L);// 创建时间
			invUseWo.setValue("usetype", "ISSUE", 2L);
			invUseWo.setValue("udmovementtype", "205", 2L);
			MboSetRemote maxUserSet = this.getMboSet("$MAXUSER", "MAXUSER");
			maxUserSet.setWhere("personid ='" + personid + "'");
			maxUserSet.reset();
			if (maxUserSet != null && !maxUserSet.isEmpty()) {
				MboRemote maxUser = maxUserSet.getMbo(0);
				invUseWo.setValue("fromstoreloc", maxUser.getString("defstoreroom"), 2L);
			}
			invUseWo.setValue("udwonum", wonum, 2L);
		}
	}

	/**
	 * 子表就一行时则自动将物资描述带入到主表描述中
	 * 
	 * @throws MXException
	 * @throws RemoteException
	 */
	private void setParentDesc() throws RemoteException, MXException {
		MboSetRemote invUseLineSet = this.getMboSet("INVUSELINE");
		if (!invUseLineSet.isEmpty() && invUseLineSet.count() == 1 && getString("status").equalsIgnoreCase("ENTERED")) {
			String desc = invUseLineSet.getMbo(0).getString("asset.description")
					+ invUseLineSet.getMbo(0).getString("description");
			this.setValue("description", desc + " 领用");
		}
	}

	public MboRemote duplicate() throws MXException, RemoteException {
		if (!(isHashSetLoaded)) {
			loadSkipFieldCopyHashSet();
		}
		MboRemote newMboRemote = copy();
		MboSetRemote lineSetRemote = this.getMboSet("INVUSELINE");
		lineSetRemote.resetQbe();
		lineSetRemote.reset();
		if (!lineSetRemote.isEmpty()) {
			lineSetRemote.copy(newMboRemote.getMboSet("INVUSELINE"));
		}
		return newMboRemote;
	}

	protected boolean skipCopyField(MboValueInfo mvi) {
		return skipFieldCopy.contains(mvi.getName());
	}

	private void loadSkipFieldCopyHashSet() {
		isHashSetLoaded = true;
		skipFieldCopy.add("INVUSENUM");
		skipFieldCopy.add("STATUS");
		skipFieldCopy.add("STATUSDATE");
		skipFieldCopy.add("RECEIPTS");
		skipFieldCopy.add("EXCHANGEDATE");
		skipFieldCopy.add("EXCHANGERATE");
		skipFieldCopy.add("UDCOMPANY");
		skipFieldCopy.add("UDDEPT");
		skipFieldCopy.add("UDOFS");
		skipFieldCopy.add("UDCREATEBY");
		skipFieldCopy.add("UDCREATETIME");
		skipFieldCopy.add("UDSAPNUM");
		skipFieldCopy.add("UDSAPSTATUS");
	}
}
