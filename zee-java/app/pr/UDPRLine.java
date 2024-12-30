package guide.app.pr;

import java.rmi.RemoteException;

import guide.app.common.CommonUtil;
import psdi.app.pr.PRLine;
import psdi.app.pr.PRLineRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDPRLine extends PRLine implements PRLineRemote {

	private static final int KEYLEN = 4;

	public UDPRLine(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();

		try {
			MboRemote owner = this.getOwner();
			if (owner != null && owner instanceof UDPR) {
				String apptype = owner.getString("udapptype");
				String prSerType = owner.getString("udprsertype");
				if ("PRSER".equalsIgnoreCase(apptype)) {
					String[] attrs = { "assetnum" };
					if ("A".equalsIgnoreCase(prSerType)) {
						this.setFieldFlag(attrs, 128L, true);
					} else {
						this.setFieldFlag(attrs, 128L, false);
					}
				}
			}
			
			/**
			 * ZEE - PR预算list，可见当前单据的所用预算总额（不含税）
			 * 2024-9-29  15:17
			 * DJY
			 */
			if(owner!=null && owner instanceof UDPR && owner.getString("udcompany").equalsIgnoreCase("ZEE")){
				MboSetRemote udbudgetzeeSet = getMboSet("UDBUDGETZEE");
				if(!udbudgetzeeSet.isEmpty() && udbudgetzeeSet.count()>0){
						MboRemote udbudgetzee = udbudgetzeeSet.getMbo(0);
						MboSetRemote pringcostSet = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
						String prnum = getString("prnum");
						String udbudgetnum =getString("udbudgetnum");
						pringcostSet.setWhere("prnum = '"+prnum+"' and udbudgetnum='"+udbudgetnum+"' ");
						pringcostSet.reset();
						if(!pringcostSet.isEmpty() && pringcostSet.count() > 0){
							udbudgetzee.setValue("udthisprbudget", pringcostSet.sum("linecost"), 11L);
						}
						pringcostSet.close();						
				}
				
				//ZEE - 完善系统中采购的order unit, conversion逻辑:PRLINE改变推荐供应商的值，也会代入该供应商在UDCONVERSION表的订购单位、转换系数 80-107
				String frommeasureunit = "";
				String itemnum = getString("itemnum");
				String udprevendor = getString("udprevendor");
				MboSetRemote itemSet = MXServer.getMXServer().getMboSet("ITEM", MXServer.getMXServer().getSystemUserInfo());
				itemSet.setWhere(" itemnum = '" + itemnum +"' ");
				itemSet.reset();
				MboSetRemote udconversionSet = MXServer.getMXServer().getMboSet("UDCONVERSION", MXServer.getMXServer().getSystemUserInfo());
				udconversionSet.setWhere(" itemnum = '" + itemnum +"' and vendor = '" + udprevendor +"' ");
				udconversionSet.reset();
				if (getString("udprevendor") != null&& !getString("udprevendor").equalsIgnoreCase("")) {
					if (!udconversionSet.isEmpty()&& udconversionSet.count() > 0) {
						frommeasureunit = udconversionSet.getMbo(0).getString("frommeasureunit");
						if (frommeasureunit != null&& !frommeasureunit.equalsIgnoreCase("")) {
							setFieldFlag("conversion", 128L, false);// 设置非必填
							setFieldFlag("conversion", 7L, true);// 设置只读
						}
					} else if (udconversionSet.isEmpty()|| udconversionSet.count() == 0) {
						setFieldFlag("conversion", 7L, false);// 取消只读
						setFieldFlag("conversion", 128L, true);// 设置必填
					}
				} else {
					setFieldFlag("conversion", 7L, false);// 取消只读
					setFieldFlag("conversion", 128L, true);// 设置必填
				}
				udconversionSet.close();
				itemSet.close();
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
		MboRemote owner = this.getOwner();
		if (owner != null && owner instanceof UDPR) {
			String personid = this.getUserInfo().getPersonId();
			String udcompany = owner.getString("udcompany");
			String udapptype = owner.getString("udapptype");
			String udprojectnum = owner.getString("udprojectnum");
			String udbudgetnum = owner.getString("udbudgetnum");

			if ("PRMAT".equalsIgnoreCase(udapptype)) {
				this.setValue("linetype", "ITEM", 2L);
			}
			if ("PRFIX".equalsIgnoreCase(udapptype)) {
				this.setValue("linetype", "ITEM", 2L);
				this.setValue("gldebitacct", "COSCO", 2L);
				this.setValue("issue", 1, 2L);
			}
			if ("PRSER".equalsIgnoreCase(udapptype)) {
				this.setValue("linetype", "SERVICE", 2L);
				this.setValue("gldebitacct", "COSCO", 2L);
				this.setValue("issue", 1, 2L);
			}

			this.setValue("udprojectnum", udprojectnum, 11L);
			this.setValue("udbudgetnum", udbudgetnum, 11L);

			String tax1code = CommonUtil.getValue("UDDEPT", "type='COMPANY' and deptnum='" + udcompany + "'","TAX1CODE");
			this.setValue("tax1code", tax1code, 2L);
			this.setValue("conversion", 1.0, 2L);

			MboSetRemote maxUserSet = this.getMboSet("$MAXUSER", "MAXUSER");
			maxUserSet.setWhere("personid ='" + personid + "'");
			maxUserSet.reset();
			if (maxUserSet != null && !maxUserSet.isEmpty()) {
				MboRemote maxUser = maxUserSet.getMbo(0);
				if ("PRMAT".equalsIgnoreCase(udapptype)) {
					this.setValue("storeloc", maxUser.getString("defstoreroom"), 2L);
				}
			}
			
			/**
			 * ZEE
			 * 2023-07-21 14:39:38
			 */
			if(!udcompany.equalsIgnoreCase("") && udcompany.equalsIgnoreCase("ZEE")){
				//DJY  2023-7-26 PRLINE的CAPEX也会联动
				String udcapex = owner.getString("udcapex");
				this.setValue("udcapex", udcapex, 11L);
			}
		}
	}

	public void appValidate() throws MXException, RemoteException {
		/*
		 * if(toBeAdded() && isModified("orderqty")){ MboSetRemote invbalancesSet =
		 * getMboSet("UDINVBALCURBAL"); if (invbalancesSet != null &&
		 * !invbalancesSet.isEmpty()) { double invCurbal = invbalancesSet.sum("curbal");
		 * double orderqty = getDouble("orderqty"); double orderqtyOld =
		 * this.getMboValue("orderqty").getPreviousValue().asDouble();
		 * 
		 * if (invCurbal >= orderqty) { int userInput =
		 * MXApplicationYesNoCancelException.getUserInput("check",
		 * MXServer.getMXServer(), getUserInfo()); switch (userInput) { case
		 * MXApplicationYesNoCancelException.NULL: Object[] obj = {
		 * "温馨提示：申请数量小于库存余量,请先进行领料！ \n 选择 Yes 继续，选择 No 取消！" }; throw new
		 * MXApplicationYesNoCancelException("check", "udmessage", "error0", obj); case
		 * MXApplicationYesNoCancelException.YES: setFieldFlag("remark", 128L, true);
		 * break; case MXApplicationYesNoCancelException.NO:
		 * this.getMboValue("orderqty").setValue(orderqtyOld, 11L); break; case
		 * MXApplicationYesNoCancelException.CANCEL:
		 * this.getMboValue("orderqty").setValue(orderqtyOld, 11L); break; } }else{
		 * setFieldFlag("remark", 128L, false); } } }
		 */
		super.appValidate();
	}

	@Override
	public void save() throws MXException, RemoteException {
		super.save();
		if (this.toBeAdded() || this.isModified("udfaclassnum")) {
//			setAutoKeyNum();
		}
		if (this.toBeDeleted()) {
			MboSetRemote prImpLineSet = this.getMboSet("UDPRIMPLINE");
			if (!prImpLineSet.isEmpty() && prImpLineSet.count() > 0) {
				MboRemote prImpLine = prImpLineSet.getMbo(0);
				prImpLine.setValueNull("prlineid", 11L);
			}
		}
		
		/**
		 * ZEE-根据UDITEMCPVEN表自动添加供应商列表
		 * 2024-02-06 13:28:19
		 */
		MboRemote owner = getOwner();
		if (owner != null && owner instanceof UDPR) {
			String udcompany = owner.getString("udcompany");
			if (udcompany.equalsIgnoreCase("ZEE")){
				//即收即发必填工单/位置/设备，否则无法保存
				if(getString("issue").equalsIgnoreCase("Y") && getString("wonum").equalsIgnoreCase("")
						&& getString("location").equalsIgnoreCase("") && getString("assetnum").equalsIgnoreCase("")){
					Object params[] = { "One of work order / location / asset must be selected, or you can not save!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params);
				}
			}
		}
		
	}
	
	//ZEE - 完善系统中采购的order unit, conversion逻辑:PRLINE改变推荐供应商的值，也会代入该供应商在UDCONVERSION表的订购单位、转换系数 308-345
	  public void initFieldFlagsOnMbo(String attrName) throws MXException{
		  super.initFieldFlagsOnMbo(attrName);
		  try{
		  if (attrName.equalsIgnoreCase("orderunit"))
		  {	  
			  String frommeasureunit = "";
				String itemnum = getString("itemnum");
				String udprevendor = getString("udprevendor");
				MboSetRemote itemSet = MXServer.getMXServer().getMboSet("ITEM", MXServer.getMXServer().getSystemUserInfo());
				itemSet.setWhere(" itemnum = '" + itemnum +"' ");
				itemSet.reset();
				MboSetRemote udconversionSet = MXServer.getMXServer().getMboSet("UDCONVERSION", MXServer.getMXServer().getSystemUserInfo());
				udconversionSet.setWhere(" itemnum = '" + itemnum +"' and vendor = '" + udprevendor +"' ");
				udconversionSet.reset();
				if (getString("udprevendor") != null&& !getString("udprevendor").equalsIgnoreCase("")) {
					if (!udconversionSet.isEmpty()&& udconversionSet.count() > 0) {
						frommeasureunit = udconversionSet.getMbo(0).getString("frommeasureunit");
						if (frommeasureunit != null&& !frommeasureunit.equalsIgnoreCase("")) {
							setFieldFlag("orderunit", 128L, false);// 设置非必填
							setFieldFlag("orderunit", 7L, true);// 设置只读
						}
					} else if (udconversionSet.isEmpty() || udconversionSet.count() == 0) {
						setFieldFlag("orderunit", 7L, false);// 取消只读
						setFieldFlag("orderunit", 128L, true);// 设置必填
					}

				} else {
					setFieldFlag("orderunit", 7L, false);// 取消只读
					setFieldFlag("orderunit", 128L, true);// 设置必填
				}
				udconversionSet.close();
				itemSet.close();
		  	}	
		  } catch (RemoteException e) {
            e.printStackTrace();
		  }
	  }
	
}
