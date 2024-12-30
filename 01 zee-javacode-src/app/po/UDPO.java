package guide.app.po;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import guide.app.common.CommonUtil;
import guide.iface.sap.webservice.HearBean;
import guide.iface.sap.webservice.ItemWebService;
import guide.iface.sap.webservice.SecondHearBean;
import guide.iface.sap.webservice.SecondWebService;
import psdi.app.doclink.Docinfo;
import psdi.app.po.PO;
import psdi.app.po.PORemote;
import psdi.app.po.virtual.ReceiptInputSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

import java.io.File;

import psdi.security.UserInfo;

public class UDPO extends PO implements PORemote {

	private static final int KEYLEN = 2;

	private static final int POKEYLEN = 3;

	public UDPO(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}
	
	@Override
	public void init() throws MXException {
		super.init();
		try {
			String udcompany = this.getString("udcompany");
			if (udcompany != null && udcompany.equalsIgnoreCase("11A7TCT")) {
				if (!this.toBeAdded()) {
					String personid = this.getUserInfo().getPersonId();
					String createby = this.getString("purchaseagent");
					String status = this.getString("status");
					
					String[] attrs = { "UDPURPLAT", "UDCATEGORY", "UDMATSTATUS", "UDREMARK", "UDCURRENCY", "UDUKURS","DAYS", "ACCEPMETHOD", "PAYMETHOD", "BIGPROJECT", "UDAUTHORIZER","UDDELIVERYDATE", "UDCFO", "UDCEO" };
					if ("APPR".equalsIgnoreCase(status) || "CAN".equalsIgnoreCase(status)) {
						this.setFieldFlag(attrs, 7L, true);
					} else {
						boolean isInWF = CommonUtil.isInWF(this);
						boolean assign = CommonUtil.isAssign(this, createby);
						// 启用流程
						if (isInWF) {
							// 是流程中人员
							if (assign) {
								// 登录人=创建人
								if (personid.equalsIgnoreCase(createby)) {
									this.setFlag(7L, false);
								} else {
									this.setFlag(7L, true);
								}
							} else {
								this.setFlag(7L, true);
							}
						}
					}
					boolean udiscon = this.getBoolean("udiscon");
					if(udiscon==true) {
						this.setFieldFlag("udconnum", 128L, true);
					}else{
						this.setFieldFlag("udconnum", 128L, false);
					}
				}
			} else if (udcompany.equalsIgnoreCase("ZEE")) {
				MboSetRemote POSet = this.getMboSet("POLINE");
				if (!POSet.isEmpty() && POSet.count() > 0) {
					Double max = POSet.getMbo(0).getDouble("udconfirmdur");
					for (int i = 0; i < POSet.count(); i++) {
						MboRemote PO = POSet.getMbo(i);
						Double udconfirmdur = PO.getDouble("udconfirmdur");
						if (udconfirmdur > max) {
							max = udconfirmdur;
						}
					}
					this.setValue("udallconfirmdur", max, 11L);
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

		String appName = this.getThisMboSet().getApp();
		String personid = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();

		if (appName != null && !appName.isEmpty()) {
			double ukurs = 1;
			String udappType = appName.replaceAll("UD", ""); // 替换UD
			this.setValue("udapptype", udappType, 11L);
			this.setValue("udcreateby", personid, 2L);// 创建人
			this.setValue("udcreatetime", currentDate, 11L);// 创建时间
			this.setValue("purchaseagent", personid, 2L);// 采购员
			MboSetRemote companySet = this.getMboSet("UDCOMPANY");
			if (!companySet.isEmpty() && companySet.count() > 0) {
				String currency = companySet.getMbo(0).getString("currency");
				this.setValue("udcurrency", currency, 11L);
				MboSetRemote currexchSet = this.getMboSet("UDCURREXCH");
				if (!currexchSet.isEmpty() && currexchSet.count() > 0) {
					ukurs = currexchSet.getMbo(0).getDouble("ukurs");
				}
			}
			this.setValue("udukurs", ukurs, 11L);
			getMboValue("ponum").autoKey();
			this.setValue("udrevponum", this.getString("ponum"), 11L);
			this.setValue("udrevnum", this.getInt("revisionnum"), 11L);
		}
		
		/**
		 * ZEE
		 * 2023-07-21 14:43:28
		 */
		String udcompany = getString("udcompany");
		if(udcompany!=null && udcompany.equalsIgnoreCase("ZEE")){
			if(appName != null && appName.equalsIgnoreCase("UDPOOT")){
				this.setValue("vendor","ZEEVENDOR",11L);
			}
		}
	}

	@Override
	public void save() throws MXException, RemoteException {
		super.save();
		String appName = this.getThisMboSet().getApp();
		if (appName != null && !appName.isEmpty()) {
			if ("UDRECOT".equalsIgnoreCase(appName) || "UDRECPOF".equalsIgnoreCase(appName)
					|| "UDRECPOM".equalsIgnoreCase(appName) || "UDRECPOS".equalsIgnoreCase(appName) || "UDRECZEE".equalsIgnoreCase(appName)) {// 接收入库
				// 校验
				check();

				String transRelationName = "PARENTMATRECTRANS";
				if ("UDRECPOS".equalsIgnoreCase(appName))
					transRelationName = "NOCOSTSERVRECTRANS"; //服务接收

				MboSetRemote recTransSet = getMboSet(transRelationName);//
				if (!recTransSet.isEmpty() && recTransSet.count() > 0 && recTransSet.toBeSaved()) {
					String issueType = getTypeValue(recTransSet, "issuetype");
					if (issueType == null
							|| (!issueType.equalsIgnoreCase("RECEIPT") && !issueType.equalsIgnoreCase("RETURN")))
						throw new MXApplicationException("guide", "1040");

					String zType = getTypeValue(recTransSet, "udztype");
					System.out.println("\n---319---zttype----"+zType);
					if (zType == null || (!zType.equalsIgnoreCase("Y") && !zType.equalsIgnoreCase("N")))
						throw new MXApplicationException("guide", "1040");

					String recNum = createRecTransRecNum("MATRECTRANS", transRelationName);
					System.out.println("\n---319---recNum----"+recNum);
					if ("UDRECPOS".equalsIgnoreCase(appName))
						recNum = createRecTransRecNum("SERVRECTRANS", transRelationName);

					if (zType != null && zType.equalsIgnoreCase("Y")) {
						MboRemote recTrans = null;
						for (int j = 0; (recTrans = recTransSet.getMbo(j)) != null; j++) {
							if ((recTrans.getString("udsapnum") == null
									|| recTrans.getString("udsapnum").equalsIgnoreCase("")) && recTrans.toBeAdded()) {
								recNum = CommonUtil.getValue(recTrans, "ORIGINALRECEIPT", "udrecnum");
							}
						}
					}
					
					if (recNum != null && !recNum.equalsIgnoreCase("")) {
						try {
							/**
							 * ZEE入库接口2024-04-15 08:57:04
							 */
							if ("UDRECPOM".equalsIgnoreCase(appName) || "UDRECZEE".equalsIgnoreCase(appName)) {
								dataMatToSap(recTransSet, recNum, issueType, zType);
							} else if ("UDRECPOS".equalsIgnoreCase(appName)) {
								// dataSerToSap(recTransSet, recNum, issueType, zType);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}
			}
			if ("UDPOMAT".equalsIgnoreCase(appName) || "UDPOSER".equalsIgnoreCase(appName)
					|| "UDPOFIX".equalsIgnoreCase(appName)) {
				if (!getMboValue("udpurplat").getInitialValue().asString().equalsIgnoreCase("CON")
						&& getString("udpurplat").equalsIgnoreCase("CON")) {
					MboSetRemote polineSet = this.getMboSet("POLINE");
					if (!polineSet.isEmpty() && polineSet.count() > 0) {
						boolean isflag = false;
						for (int i = 0; polineSet.getMbo(i) != null; i++) {
							MboRemote poline = polineSet.getMbo(i);
							if (poline.isNull("udcontractlineid")) {
								isflag = true;
								break;
							}
						}
						if (isflag) {
							throw new MXApplicationException("guide", "1082");
						}
					}
				}
			}
			// 寄售移库
			if ("UDPOMAT".equalsIgnoreCase(appName)
					&& !getMboValue("status").getInitialValue().asString().equalsIgnoreCase("APPR")
					&& getString("status").equalsIgnoreCase("APPR")) {
				MboSetRemote polineSet = this.getMboSet("POLINE");

				for (int i = 0; polineSet.getMbo(i) != null; i++) {
					MboRemote poline = polineSet.getMbo(i);

					if (!poline.isNull("udinvbalancesid")) {

						MboSetRemote invBalSet = poline.getMboSet("udinvbalances");
						double curbal = 0;
						if (!invBalSet.isEmpty() && invBalSet.count() > 0) {
							MboRemote invBal = invBalSet.getMbo(0);
							curbal = invBal.getDouble("curbal");
							double orderqty = poline.getDouble("orderqty");
							int polinenum = poline.getInt("polinenum");
							double diff = curbal - orderqty;
							if (diff >= 0) {
								invBal.setValue("curbal", diff, 11L);
							} else {
								Object params[] = { polinenum };
								throw new MXApplicationException("guide", "1094", params);
							}
						}
					}
				}
			}
		}
		if (this.toBeAdded()) {
			setAutoKeyNum();
		}
		// PCT 直接采购 库房不可以为 PCT-01
		String udcompany = getString("UDCOMPANY");
		if (udcompany.equals("GR02PCT")) {
			String udpurplat = getString("UDPURPLAT");
			if (udpurplat.equals("DPO")) {
				MboSetRemote mboSet = getMboSet("POLINE");
				if (!mboSet.isEmpty() && mboSet.count() > 0) {
					for (int i = 0; i < mboSet.count(); i++) {
						MboRemote mbo = mboSet.getMbo(i);
						String storeloc = mbo.getString("STORELOC");
						if (storeloc.equals("PCT-01")) {
							Object[] objects = {
									"The purchase type is direct purchase, PCT-01 warehouse cannot be selected!" };
							throw new MXApplicationException("instantmessaging", "tsdimexception", objects);
						}
					}
				}
			}
		}
		// 即售即发
		if (udcompany.equals("GR02PCT")) {
			String uddept = getString("UDDEPT");
			if (!uddept.equals("GR02120002") && !uddept.equals("GR02120010")) {
				MboSetRemote mboSet = getMboSet("POLINE");
				if (!mboSet.isEmpty() && mboSet.count() > 0) {
					for (int i = 0; i < mboSet.count(); i++) {
						MboRemote mbo = mboSet.getMbo(i);
						mbo.setValue("ISSUE", 1, 2L);
					}
				}
			}
		}
		
		/**
		 * ZEE-状态为APPR时发送PDF给供应商
		 * 2023-07-20 10:15:40
		 */
		String status = getString("status");
//		if (udcompany.equalsIgnoreCase("ZEE") && getMboValue("status").isModified()) {
//			if (!status.equalsIgnoreCase("") && status.equalsIgnoreCase("APPR")) {
//				MboSetRemote polineSet = getMboSet("POLINE");
//				if (!polineSet.isEmpty() && polineSet.count() > 0) {
//					for (int i = 0; i < polineSet.count(); i++) {
//						MboRemote poline = polineSet.getMbo(i);
//						poline.setValue("udstatus", "APPROVED", 11L);
//					}
//				}
//				
////				try {
////					polineSendVendor();
////				} catch (JSONException e) {
////					e.printStackTrace();
////				}
//			}
//		}
		
		/**
		 * ZEE-展示接收明细行剩余数量：根据poline赋予子表UDRECEIPT默认值
		 * @author djy
		 *2024-06-03 14:47:43
		 *260-299行
		 */	
		if (!udcompany.isEmpty() && udcompany.equalsIgnoreCase("ZEE") && status != null && status.equalsIgnoreCase("APPR")) {
			String ponum0 = getString("ponum");
			MboSetRemote udreceiptSet0 = MXServer.getMXServer().getMboSet("UDRECEIPT", MXServer.getMXServer().getSystemUserInfo());
			udreceiptSet0.setWhere(" ponum = '" + ponum0 +"' ");
			udreceiptSet0.reset();
			if (udreceiptSet0.isEmpty() || udreceiptSet0.count()== 0) {
				MboSetRemote polineSet = this.getMboSet("POLINE");
				if(!polineSet.isEmpty() && polineSet.count() > 0 ){
					for (int i = 0; i < polineSet.count(); i++) {
						MboRemote poline = polineSet.getMbo(i);
						String ponum = poline.getString("ponum");
						String polinenum = poline.getString("polinenum");
						String itemnum = poline.getString("itemnum");
						String description = poline.getString("description");
						String linetype = poline.getString("linetype");
						Double receiptqty = 0.0; 
						Double remainqty = poline.getDouble("orderqty");
						MboSetRemote udreceiptSet = MXServer.getMXServer().getMboSet("UDRECEIPT", MXServer.getMXServer().getSystemUserInfo());
						udreceiptSet.setWhere(" 1=2 ");
						udreceiptSet.reset();
						if(udreceiptSet.isEmpty() || udreceiptSet.count()==0){
							MboRemote udrec = udreceiptSet.add();
							udrec.setValue("ponum", ponum, 11L);
							udrec.setValue("polinenum", polinenum, 11L);
							udrec.setValue("itemnum", itemnum, 11L);
							udrec.setValue("description", description, 11L);
							udrec.setValue("receiptqty", receiptqty, 11L);
							udrec.setValue("remainqty", remainqty, 11L);
							udrec.setValue("linetype", linetype, 11L);
						}
						udreceiptSet.save();
						udreceiptSet.close();
					}
				}
			}
			udreceiptSet0.close();
		}
		
		/**
		 * ZEE-PO APPR时，给PO的UDORDERDEPT赋值
		 * 2024-06-20 15:35:47
		 * 226-258
		 */
		String status1 = getString("status");
		if (udcompany.equalsIgnoreCase("ZEE") ) {
		      List<String> itemdept = new ArrayList<String>();
			if (!status1.equalsIgnoreCase("CAN")) {
				MboSetRemote polineSet1 = getMboSet("POLINE");
				if (!polineSet1.isEmpty() && polineSet1.count() > 0) {
					for (int i = 0; i < polineSet1.count(); i++) {
						MboRemote poline = polineSet1.getMbo(i);
						String itemnum = poline.getString("itemnum");
						MboSetRemote uditemSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
						uditemSet.setWhere(" itemnum = '" + itemnum + "' and udcompany = 'ZEE' ");
						uditemSet.reset();
						if(!uditemSet.isEmpty() && uditemSet.count() > 0){
							MboRemote uditem = uditemSet.getMbo(0);
							MboSetRemote uddeptSet = MXServer.getMXServer().getMboSet("UDDEPT", MXServer.getMXServer().getSystemUserInfo());
							uddeptSet.setWhere(" deptnum = '" + uditem.getString("dept") + "' ");
							uddeptSet.reset();
							if(!uddeptSet.isEmpty() && uddeptSet.count() > 0){
								MboRemote uddept = uddeptSet.getMbo(0);
								itemdept.add(uddept.getString("description").substring(0,1));
							}
						}
						uditemSet.close();
					}
					HashSet h = new HashSet(itemdept);
                    itemdept.clear();
                    itemdept.addAll(h);
                    String message = itemdept.stream().collect(Collectors.joining());
                    setValue("udorderdept", message.replaceAll(" ", ""), 11L);
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
		checkBudat();
	}

	private void checkBudat() throws RemoteException, MXException {
		// 凭证时间验证
		Date udbudat1 = getDate("udbudat");
		String udcompany = getString("udcompany");
		try {
			if (udcompany.equalsIgnoreCase("GR02PCT")) {
				if (udbudat1 != null && !udbudat1.equals("")) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String udbudat = sdf.format(udbudat1);
					checkSapDate(udbudat);
				}
			} else {
				return;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 结束

	}

	private void checkSapDate(String udbudat) throws RemoteException, MXException, ParseException {
		MboRemote recTrans = null;
		MboSetRemote recTransSet = getMboSet("PARENTMATRECTRANS");//
		if (!recTransSet.isEmpty() && recTransSet.count() > 0 && recTransSet.toBeSaved()) {
			for (int j = 0; (recTrans = recTransSet.getMbo(j)) != null; j++) {
				if ((recTrans.getString("udsapnum") == null || recTrans.getString("udsapnum").equalsIgnoreCase(""))
						&& recTrans.toBeAdded()) {
					MboSetRemote matuSet = MXServer.getMXServer().getMboSet("MATUSETRANS",
							MXServer.getMXServer().getSystemUserInfo());
					matuSet.setWhere("itemnum='" + recTrans.getString("itemnum")
							+ "' and Issuetype='ISSUE' and to_date(transdate) > to_date('" + udbudat
							+ "','yyyy-mm-dd')'");
					matuSet.reset();
					if (!matuSet.isEmpty() && matuSet.count() > 0) {
						Object[] obj = { "Material code：" + recTrans.getString("itemnum")
								+ "The latest delivery time is longer than the voucher time, please select a new one！" };
						throw new MXApplicationException("udmessage", "error1", obj);
					}
					MboSetRemote matrSet = MXServer.getMXServer().getMboSet("MATRECTRANS",
							MXServer.getMXServer().getSystemUserInfo());
					matrSet.setWhere("itemnum='" + recTrans.getString("itemnum")
							+ "' and to_date(transdate) > to_date('" + udbudat + "','yyyy-mm-dd')");
					matrSet.reset();
					if (!matrSet.isEmpty() && matrSet.count() > 0) {
						Object[] obj = { "Material code：" + recTrans.getString("itemnum")
								+ "The latest storage time is longer than the voucher time, please select a new one！" };
						throw new MXApplicationException("udmessage", "error1", obj);
					}
					matuSet.close();
					matrSet.close();
				}
			}
		}
		recTransSet.close();
	}

	private void checkItem() throws RemoteException, MXException {
		String udcompany = this.getString("udcompany");
		MboSetRemote matRecSet = this.getMboSet("PARENTMATRECTRANS");
		if (!matRecSet.isEmpty() && matRecSet.count() > 0) {
			for (int i = 0; matRecSet.getMbo(i) != null; i++) {
				MboRemote matRec = matRecSet.getMbo(i);
				String itemnum = matRec.getString("itemnum");
				if (!matRec.toBeDeleted()) {
					MboSetRemote itemcpSet = matRec.getMboSet("$UDITEMCP" + i, "UDITEMCP",
							"udcompany='" + udcompany + "' and itemnum='" + itemnum + "' and isdisable=1");
					itemcpSet.reset();
					if (!itemcpSet.isEmpty() && itemcpSet.count() > 0) {
						Object params[] = { itemnum };
						throw new MXApplicationException("guide", "1115", params);
					}
				}
			}
		}
		matRecSet.close();
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

	private void setAutoKeyNum() throws RemoteException, MXException {
		String apptype = this.getString("udapptype");
		String udcompany = this.getString("udcompany");
		String persongroup = "";
		MboSetRemote personGroupSet = this.getMboSet("$PERSONGROUP", "PERSONGROUP",
				"uddept = '" + udcompany + "' and parent='CSPL'");
		if (personGroupSet != null && !personGroupSet.isEmpty()) {
			persongroup = personGroupSet.getMbo(0).getString("persongroup");
		}
		if ("pomat".equalsIgnoreCase(apptype)) {
			String prkeyNum = CommonUtil.autoKeyNum("PO", "UDPOKEYNUM", persongroup + "CG", "yyyyMMdd", POKEYLEN);
			this.setValue("udpokeynum", prkeyNum, 11L);
		}
	}

	private String getTypeValue(MboSetRemote parentRecSet, String attr) throws RemoteException, MXException {
		MboRemote parentRec = null;
		String typeValue = "ERROR";
		boolean flag = false;
		for (int i = 0; (parentRec = parentRecSet.getMbo(i)) != null; i++) {
			if (parentRec.toBeAdded()) {
				if (!flag) {
					typeValue = parentRec.getString(attr);
					flag = true;
				}
				if (parentRec.getString(attr) != null && !parentRec.getString(attr).equalsIgnoreCase(typeValue))
					typeValue = "DIFF";
			}
		}
		return typeValue;
	}

	private void dataSerToSap(MboSetRemote nocostServrectransSet, String recNum, String issueType, String zType)
			throws RemoteException, MXException, JSONException {
		String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
		String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
		System.out.println("\nPOSER-----------status" + sapStatus + "-----------debug" + sapDebug);
		if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
			JSONObject Header = new JSONObject();
			Header = CommonUtil.getSerRecHeader(this, recNum, issueType, zType);
			Header.put("item", CommonUtil.getSerRecItem(this, nocostServrectransSet, recNum));
			if (CommonUtil.getString(Header, "item").toString().length() > 2) {
				if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
					Object params[] = { "提示，XML:" + Header.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
				String num = "";
				String status = "";
				try {
					SecondHearBean result = SecondWebService.itemRequestWebService1(Header.toString());
					num = result.getZRETURN_CODE();
					status = result.getZHEADMSG();
					CommonUtil.ifaceLog(Header.toString(), getUserInfo().getPersonId(), getName(),
							Header.getString("UNIQUEID"), num, status);
					if (num == null || !status.toLowerCase().startsWith("success")) {
						Object params[] = { "提示：" + status + "!" };
						throw new MXApplicationException("instantmessaging", "tsdimexception", params);
					}
					MboRemote serRec = null;
					for (int j = 0; (serRec = nocostServrectransSet.getMbo(j)) != null; j++) {
						if ((serRec.getString("udsapnum") == null || serRec.getString("udsapnum").equalsIgnoreCase(""))
								&& serRec.toBeAdded()) {
							serRec.setValue("udsapnum", num, 11L);
							serRec.setValue("udsapstatus", status, 11L);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Object params[] = { "提示：" + e.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
			}
		}
	}

	private void dataMatToSap(MboSetRemote parentmatrecSet, String recNum, String issueType, String zType)
			throws RemoteException, MXException, JSONException {
		String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
		String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
		System.out.println("\nPOMAT-----------status" + sapStatus + "-----------debug" + sapDebug);
		if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
			JSONObject Header = new JSONObject();
			Header = CommonUtil.getMatRecHeader(this, recNum, issueType, zType);
			Header.put("item", CommonUtil.getMatRecItem(this, parentmatrecSet, recNum));
			System.out.println("\n----319---length---"+CommonUtil.getString(Header, "item").toString().length());
			if (CommonUtil.getString(Header, "item").toString().length() > 2) {
				if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
					Object params[] = { "提示，SAP，XML:" + Header.toString() + "!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
				String num = "";
				String status = "";
				try {
					System.out.println("\n--319----headerstring---"+Header.toString());
					HearBean result = ItemWebService.itemRequestWebService(Header.toString());
					num = result.getBELNR();
					status = result.getZHEADMSG();
					CommonUtil.ifaceLog(Header.toString(), getUserInfo().getPersonId(), getName(),
							Header.getString("ZSTOCKNO"), num, status);
					if (num == null || !status.equalsIgnoreCase("成功")) {
						Object params[] = { "提示：SAP，" + status + "!" };
						throw new MXApplicationException("instantmessaging", "tsdimexception", params);
					}
					MboRemote matrec = null;
					for (int j = 0; (matrec = parentmatrecSet.getMbo(j)) != null; j++) {
						if ((matrec.getString("udsapnum") == null || matrec.getString("udsapnum").equalsIgnoreCase(""))
								&& matrec.toBeAdded()) {
							matrec.setValue("udsapnum", num, 11L);
							matrec.setValue("udsapstatus", status, 11L);
							//ZEE - SAP返回状态Success 617-620
							if(getString("udcompany")!=null && getString("udcompany").equalsIgnoreCase("ZEE")){
								matrec.setValue("udsapstatus", "Success", 11L);
							}
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

	/**
	 * 创建入库单号
	 * 
	 * @param tableName
	 * @param relationName
	 * @return
	 * @throws RemoteException
	 * @throws MXException
	 */
	private String createRecTransRecNum(String tableName, String relationName) throws RemoteException, MXException {
		String ponum = this.getString("ponum");
		String keyNum = CommonUtil.autoKeyNum(tableName, "UDRECNUM", ponum, "", KEYLEN);
		MboSetRemote recTransSet = this.getMboSet(relationName);
		if (!recTransSet.isEmpty() && recTransSet.count() > 0) {
			int zItemno = 0;
			for (int i = 0; recTransSet.getMbo(i) != null; i++) {
				MboRemote recTrans = recTransSet.getMbo(i);
				if (recTrans.isNull("udrecnum")) {
					zItemno++;
					recTrans.setValue("udrecnum", keyNum, 11L);//第几次入库
					recTrans.setValue("udzitemno", zItemno, 11L);//第几次入库时的第几行
					recTrans.setValue("udbudat", this.getDate("udbudat"), 11L);
				}
			}
		}
		return keyNum;
	}

	@Override
	public MboRemote createPOLineFromPR(MboRemote fromPR, MboRemote fromPRLine, MboSetRemote poLines)
			throws MXException, RemoteException {
		MboRemote toPOLine = super.createPOLineFromPR(fromPR, fromPRLine, poLines);
		System.out.println("\n---918----AAA");
//		if (!fromPR.getString("udcompany").equalsIgnoreCase("ZEE")) {
//			toPOLine.setValueNull("linecost", 2L);// 清空
//		}
		System.out.println("\n---918----BBB");
		String tax1code = "";
		String udcompany = fromPR.getString("udcompany");
		String materialType = CommonUtil.getValue(toPOLine, "ITEM", "udmaterialType");
		toPOLine.setValue("udmaterialtype", materialType, 11L);
		toPOLine.setValue("udprojectnum", fromPRLine.getString("udprojectnum"), 11L);
		toPOLine.setValue("udbudgetnum", fromPRLine.getString("udbudgetnum"), 11L);
		MboRemote po = poLines.getOwner();
		if (po != null && po instanceof UDPO) {
			MboSetRemote companiesSet = po.getMboSet("po_vendor");
			if (!companiesSet.isEmpty() && companiesSet.count() > 0) {
				MboRemote companies = companiesSet.getMbo(0);
				tax1code = companies.getString("tax1code");
			}
		}
		if ("GR02PCT".equalsIgnoreCase(udcompany)) {
			tax1code = fromPRLine.getString("tax1code");
		}
		
		/**
		 * ZEE-取PR的默认税率
		 * 2023-09-19 14:45:02
		 */
		if ("ZEE".equalsIgnoreCase(udcompany)) {
			tax1code = fromPRLine.getString("tax1code");
		}

		toPOLine.setValue("tax1code", tax1code, 11L);
		toPOLine.setValue("udtotalprice", fromPRLine.getDouble("udtotalprice"), 11L);
		toPOLine.setValue("udtotalcost", fromPRLine.getDouble("udtotalcost"), 2L);
		
		/**
		 * 	ZEE-如果物资，自动代入PRLINE的costcenter
		 *  ZEE-自动代入PRLINE的gl
		 * DJY 2024-03-18 10:39:13
		 */
		toPOLine.setValue("udcostcenterzee", fromPRLine.getString("udcostcenterzee"),11L);
		toPOLine.setValue("udglzee", fromPRLine.getString("udglzee"),11L);
		
		
		String udpurplat = po.getString("udpurplat");
		if ("CON".equalsIgnoreCase(udpurplat)) {
			MboSetRemote matConLineSet = toPOLine.getMboSet("UDMATCONLINE");
			if (!matConLineSet.isEmpty() && matConLineSet.count() > 0) {
				MboRemote matConLine = matConLineSet.getMbo(0);
				int contractlineid = matConLine.getInt("udcontractlineid");
				tax1code = matConLine.getString("tax1code");
				double totalunitcost = matConLine.getDouble("totalunitcost");// 含税单价
				toPOLine.setValue("udcontractlineid", contractlineid, 11L);
				toPOLine.setValue("tax1code", tax1code, 11L);
				toPOLine.setValue("udtotalprice", totalunitcost, 2L);
			}
		}
		
		/**
		 * ZEE-将折后价带入POLINE
		 * 2023-07-19 11:04:06
		 */
		if (udcompany.equalsIgnoreCase("ZEE")) {
			MboSetRemote matConLineSet = MXServer.getMXServer().getMboSet("UDCONTRACTLINE",MXServer.getMXServer().getSystemUserInfo());
			matConLineSet.setWhere("linetype='ITEM' and itemnum='"+toPOLine.getString("itemnum")+"' and gconnum in (select gconnum from udcontract where status='APPR' and to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd') and udcompany='"+getString("udcompany")+"' and vendor='"+getString("vendor")+"')");
			matConLineSet.reset();
			if (!matConLineSet.isEmpty() && matConLineSet.count() > 0) {
				MboRemote matConLine = matConLineSet.getMbo(0);
				int contractlineid = matConLine.getInt("udcontractlineid");
				tax1code = matConLine.getString("tax1code");
				double taxrate = 0.0D;
				MboSetRemote taxSet = MXServer.getMXServer().getMboSet("TAX",MXServer.getMXServer().getSystemUserInfo());
				taxSet.setWhere(" taxcode='"+tax1code+"' ");
				taxSet.reset();
				if (!taxSet.isEmpty() && taxSet.count() >0) {
					MboRemote tax = taxSet.getMbo(0);
					taxrate = tax.getDouble("taxrate");
				}
				taxSet.close();
				double percentTaxRate = taxrate / 100;
				double uddiscountprice = matConLine.getDouble("uddiscountprice");// 含税单价
				toPOLine.setValue("udcontractlineid", contractlineid, 11L);
				toPOLine.setValue("tax1code", tax1code, 11L);
				toPOLine.setValue("unitcost", uddiscountprice, 11L);
				System.out.println("\n---918----CCC"+toPOLine.getDouble("unitcost"));
				System.out.println("\n---918----DDD"+toPOLine.getDouble("orderqty"));
				toPOLine.setValue("linecost", toPOLine.getDouble("unitcost") * toPOLine.getDouble("orderqty"), 11L);
				System.out.println("\n---918----EEE");
				toPOLine.setValue("udtotalprice", uddiscountprice * (1 + percentTaxRate), 11L);
				toPOLine.setValue("udtotalcost", toPOLine.getDouble("udtotalprice") * toPOLine.getDouble("orderqty"), 11L);
				toPOLine.setValue("tax1", toPOLine.getDouble("udtotalcost") - toPOLine.getDouble("linecost"), 11L);
			}
			matConLineSet.close();
			
			//PR创建PO时,将UDPROJECTNUM和UDCAPEX/UDCOSTTYPE带入到POLINE
			toPOLine.setValue("udprojectnum", fromPRLine.getString("udprojectnum"), 11L);
			toPOLine.setValue("udcapex", fromPRLine.getString("udcapex"), 11L);
			toPOLine.setValue("udcosttype", fromPRLine.getString("udcosttype"), 11L);
			toPOLine.setValue("udcostcenterasset", fromPRLine.getString("udcostcenterasset"), 11L);
			MboRemote pozee = toPOLine.getOwner();
			if (pozee!=null) {
				pozee.setValue("udprojectnum", fromPRLine.getString("udprojectnum"), 11L);
				pozee.setValue("udcapex", fromPRLine.getString("udcapex"), 11L);
			}
		}
		
		return toPOLine;
	}

	@Override
	public void setRelatedMboEditibility(String relationName, MboSetRemote mboSet) throws MXException, RemoteException {
		super.setRelatedMboEditibility(relationName, mboSet);
		if (relationName.equals("UDFIXRECEIPTINPUT")) {
			((ReceiptInputSet) mboSet).setRelationshipStringFromPO("UDFIXRECEIPTINPUT");
		}
	}

	public void copyPoLineSetForInvbalances(MboSetRemote invbalancesSet) throws RemoteException, MXException {
		UDPOLineSet newPOLineSet = (UDPOLineSet) this.getMboSet("POLINE");
		newPOLineSet.copyPoLineSet(invbalancesSet);
	}

	// 修订订单
	public MboRemote revPO(String ponum, String podesc, int revnum) throws RemoteException, MXException {
		String srSql = "udrevponum='%s' and udrevnum='%s' and status not in (select value from synonymdomain where domainid='POSTATUS' and maxvalue in ('CAN','CLOSE'))";
		String srSqlFormat = String.format(srSql, new Object[] { ponum, Integer.valueOf(revnum) });
		MboSetRemote mboSet = this.getMboSet("$po", "po", srSqlFormat);
		if (!(mboSet.isEmpty())) {
			throw new MXApplicationException("guide", "1128");
		}
		Date requireddate = this.getDate("requireddate");
		String status = this.getString("status");
		if (!"REVISD".equalsIgnoreCase(status)) {
			this.changeStatus("REVISD", requireddate, "订单修订");
		}
		MboRemote newRevision = duplicate();
		if (newRevision != null) {
			newRevision.setValue("udrevponum", ponum, 11L);
			newRevision.setValue("revcomments", podesc, 11L);
			newRevision.setValue("udrevnum", revnum, 11L);
			newRevision.setValue("status", "WAPPR", 11L);
			newRevision.setValue("historyflag", false, 11L);
			newRevision.setValue("requireddate", requireddate, 11L);
		}
		return newRevision;
	}
	
	//http://10.18.11.156/reports/POZEE-includingtax/POZEE-includingtax86821716792589727.pdf    前缀路径在22服务上的代码里,可以搜索let defUrl'https://cspeam.coscoshipping.com/reports/'
	public int polineSendVendor() throws MXException, RemoteException, JSONException {
		UserInfo userInfo = getUserInfo();
		String flag = "Tip，there are no purchase order details lines to send！";
		MboSetRemote polineSet = getMboSet("POLINE");
		if (!polineSet.isEmpty() && polineSet.count() > 0) {
			String ponum = getString("ponum");// 订单号
			String purchasename = getString("PURCHASE.displayname");// 采购员
			double totaltax1 = getDouble("totaltax1");// 税额
			double totalcost = getDouble("totalcost");// 含税金额
			String vendor = getString("vendor");// 含税金额
			double pretaxtotal = totalcost - totaltax1;// 不含税金额
			MboSetRemote companiesSet = MXServer.getMXServer().getMboSet("COMPANIES",MXServer.getMXServer().getSystemUserInfo());
			companiesSet.setWhere("company='" + vendor + "'");
			companiesSet.reset();
			String toAddress = "";
			if (!companiesSet.isEmpty()) {
				toAddress = companiesSet.getMbo(0).getString("udemail");
			}
			companiesSet.close();
			String title = "PO：" + ponum + "，Purchaser：" + purchasename + "，Amount including tax：" + totalcost + "，Amount excluding tax：" + pretaxtotal
					+ "，Tax amount：" + totaltax1;
			if (toAddress != null && !toAddress.equalsIgnoreCase("")) {
				// 报表参数
				JSONObject paramRpt = new JSONObject();
				paramRpt.put("reportName", "udpomatl_djzee.rptdesign");
				paramRpt.put("description", "POZEE-includingtax");
				paramRpt.put("appName", "UDPOZEE");
				paramRpt.put("keyNum", ponum);
				JSONObject paramData = new JSONObject();
				paramData.put("recnum", ponum);
				// 报表执行
				File attachment = CommonUtil.getReport(userInfo, paramRpt, paramData);
				// 消息参数
				String personId = getUserInfo().getPersonId();
				String currentDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");
				JSONObject jsonData = new JSONObject();
				jsonData.put("id", ponum);
				jsonData.put("to_user", toAddress);
				jsonData.put("subject", title);
				jsonData.put("content", title);
				jsonData.put("create_time", currentDate);
				jsonData.put("create_by", personId);
				jsonData.put("change_time", currentDate);
				jsonData.put("change_by", personId);
				jsonData.put("file_path", attachment.getAbsolutePath());
				// 消息执行
				String returnResult = CommonUtil.sendGDEam(MXServer.getMXServer().getProperty("guide.gdnotify.url"),
						jsonData);// http://10.18.11.22:6001/v1/api/notify     http://10.18.11.22:6001/v1/api/sendmail
				String returnCode = CommonUtil.getString(new JSONObject(returnResult), "code");
				if (returnCode != null && returnCode.equalsIgnoreCase("200")) {
					for (int i = 0; i < polineSet.count(); i++) {
						MboRemote polien = polineSet.getMbo(i);
						polien.setValue("udstatus", "SENT", 11L);
					}
					polineSet.close();
					Object[] obj = { ponum };
					(getThisMboSet()).addWarning(new MXApplicationException("guide", "1250",obj));
					//flag = "订单" + ponum + "邮件已发送成功！";
				} else {
					flag = "PO" + ponum + "Email sending failed：" + CommonUtil.getString(new JSONObject(returnResult), "result");
					(getThisMboSet()).addWarning(new MXApplicationException("Tip", flag));
				}
			} else {
				flag = "Tip，no recipient information, no email sent！";
				(getThisMboSet()).addWarning(new MXApplicationException("Tip", flag));
			}
		}else{
			(getThisMboSet()).addWarning(new MXApplicationException("Tip", flag));
		}
		return 1;
	}
	
    public static String noDuplicate (String str) {
        // write code here
    	StringBuffer sb=new StringBuffer();
    	for(int  i=0;i<str.length();i++) {
    		//依次拿出每一个字符
    		char c=str.charAt(i);
    		//该字符的第一个索引位置和最后一个索引位置相同，表示只出现一次
    		if(str.indexOf(c)==str.lastIndexOf(c)) {
    			sb.append(c);
    		}else {//该字符的第一个索引位置和最后一个索引位置不同
    			if(str.indexOf(c)==i) {
    				sb.append(c);
    			}
    		}
    	}
    	String result=new String(sb);
    	return result;
    }
}
