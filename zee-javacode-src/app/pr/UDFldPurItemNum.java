package guide.app.pr;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurItemNum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldPurItemNum extends FldPurItemNum {

	public UDFldPurItemNum(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	public void validate() throws RemoteException, MXException {
		super.validate();

		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String udapptype = owner.getString("udapptype");
			MboSetRemote itemSet = mbo.getMboSet("ITEM");
			if (!itemSet.isEmpty() && itemSet.count() > 0) {
				MboRemote item = itemSet.getMbo(0);
				boolean isfix = item.getBoolean("udisfix");
				if ("PRMAT".equalsIgnoreCase(udapptype) && isfix) {
					throw new MXApplicationException("guide", "1002");
				} else if ("PRFIX".equalsIgnoreCase(udapptype) && !isfix) {
					throw new MXApplicationException("guide", "1003");
				}
			}
		}
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDPR) {
			String apptype = owner.getString("udapptype");
			if ("PRFIX".equalsIgnoreCase(apptype) || "PRMAT".equalsIgnoreCase(apptype)
					|| "PRSER".equalsIgnoreCase(apptype) || "PRZEE".equalsIgnoreCase(apptype)) {
				String udcompany = owner.getString("udcompany");
				String uddept = owner.getString("uddept");
				String tax1code = CommonUtil.getValue("UDDEPT", "type='COMPANY' and deptnum='"+udcompany+"'", "TAX1CODE");
				mbo.setValue("tax1code", tax1code, 11L);
				// 平均价
				MboSetRemote invCostSet = mbo.getMboSet("UDINVCOST");
				if (!invCostSet.isEmpty() && invCostSet.count() > 0) {
					MboRemote invCost = invCostSet.getMbo(0);
					double avgcost = invCost.getDouble("avgcost");
					mbo.setValue("unitcost", avgcost, 2L);
				}
				// 默认库房和备注
				MboSetRemote itemCpSet = mbo.getMboSet("UDITEMCP");
				if (!itemCpSet.isEmpty() && itemCpSet.count() > 0) {
					MboRemote itemCp = itemCpSet.getMbo(0);
					String remark = itemCp.getString("remarks");
					String storeloc = itemCp.getString("storeloc");
					if (storeloc != null && !storeloc.isEmpty()) {
						mbo.setValue("storeloc", storeloc, 2L);
					}
					mbo.setValue("remark", remark, 11L);
				}
				// 合同ID
				MboSetRemote matConLineSet = mbo.getMboSet("UDMATCONLINE");
				if (!matConLineSet.isEmpty() && matConLineSet.count() > 0) {
					MboRemote matConLine = matConLineSet.getMbo(0);
					double totalunitcost = matConLine.getDouble("totalunitcost");
					if (totalunitcost > 0) {
						mbo.setValue("udtotalprice", totalunitcost, 2L);
					}
				}
				if (this.getMboValue().isNull()) {
					mbo.setValue("unitcost", 0, 2L);
					mbo.setValueNull("remark");
				}
				// PCT
				if ("GR02PCT".equalsIgnoreCase(udcompany)) {
					// GR02120002 技术部 GR02120010 IT 正常入库，其它部门即收即发
					if (!"GR02120002".equalsIgnoreCase(uddept) && !"GR02120010".equalsIgnoreCase(uddept)) {
						mbo.setValue("issue", 1, 2L);
					}
				}
				
				/**
				 * ZEE-选择itemnum时，代入uditemcp里的costtype
				 */
				if ("ZEE".equalsIgnoreCase(udcompany)) {
					MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
					uditemcpSet.setWhere(" udcompany='ZEE' and itemnum='"+mbo.getString("itemnum")+"' ");
					uditemcpSet.reset();
					if (!uditemcpSet.isEmpty() && uditemcpSet.count() > 0) {
						MboRemote uditemcp = uditemcpSet.getMbo(0);
						String udcosttype = uditemcp.getString("udcosttype");
						mbo.setValue("udcosttype", udcosttype, 2L);
					}
					uditemcpSet.close();
					
					/**
					 * 	ZEE-如果物资，则根据物资所属部门代入costcenter
					 * 2024-03-18 10:39:13
					 */
					String linetype = mbo.getString("linetype");
					String costcenter = "";
					String deptnum = "";
					if (!linetype.equalsIgnoreCase("") && linetype.equalsIgnoreCase("ITEM")) {
						if (!uditemcpSet.isEmpty() && uditemcpSet.count() > 0) {
							MboRemote uditemcp = uditemcpSet.getMbo(0);
							deptnum = uditemcp.getString("dept");
						}
						MboSetRemote uddeptSet = MXServer.getMXServer().getMboSet("UDDEPT",MXServer.getMXServer().getSystemUserInfo());
						uddeptSet.setWhere("deptnum = '" + deptnum + "' ");
						uddeptSet.reset();
						if (!uddeptSet.isEmpty() && uddeptSet.count() > 0) {
							MboRemote dept = uddeptSet.getMbo(0);
							costcenter = dept.getString("costcenter");
							mbo.setValue("udcostcenterzee", costcenter, 11L);
						}
						uddeptSet.close();
					}
					
					/**
					 * ZEE-如果UDITEMCP.STORELOC有值则取，没有则默认ZEE-01;
					 * DJY
					 * 2024-05-23 15:35:47
					 * 75-106行
					 */
					if ("PRZEE".equalsIgnoreCase(apptype)) {
						String itemnum = mbo.getString("itemnum");
						String udcosttype = mbo.getString("udcosttype").replaceAll("\\s", ""); // 去除所有空格
						if (udcosttype == null || udcosttype.equalsIgnoreCase("")) {
							return;
						}
						 long udcosttypeValue = Long.parseLong(udcosttype);
						 if (udcosttypeValue < 4000) {
								MboSetRemote uditemcpSet1 = MXServer.getMXServer().getMboSet("UDITEMCP",MXServer.getMXServer().getSystemUserInfo());
								uditemcpSet1.setWhere(" itemnum = '" + itemnum+ "' and udcompany = 'ZEE' ");
								uditemcpSet1.reset();
								if (!uditemcpSet1.isEmpty() && uditemcpSet1.count() > 0) {
									MboRemote uditemcp = uditemcpSet1.getMbo(0);
									String storeloc = uditemcp.getString("storeloc");
									storeloc = uditemcp.getString("storeloc");
									mbo.setValue("storeloc", storeloc, 2L);
								} else { // 防止标准服务也会给库房赋值报错
									if (!linetype.equalsIgnoreCase("") && linetype.equalsIgnoreCase("ITEM")) {
										mbo.setValue("storeloc", "ZEE-01", 2L);
									}
								}
								uditemcpSet1.close();
						 }

						/**
						 * ZEE-PRLINE默认赋值采购员ZEETEST DJY 2024-05-24 10:35:47
						 * 96-106行
						 */
						if (!itemnum.equalsIgnoreCase("")) {
							mbo.setValue("udpurchaser", "ZEETEST", 2L);
						}
						
						/**
						 * ZEE - 给推荐供应商udprevendor赋值
						 * DJY
						 * 2024-09-13 15:13:57
						 */
						if(!itemnum.equalsIgnoreCase("") ){
							MboSetRemote uditemcpSet2 = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
							uditemcpSet2.setWhere(" itemnum = '" + itemnum +"' and vendor is not null");
							uditemcpSet2.reset();
							if(!uditemcpSet2.isEmpty() && uditemcpSet2.count() > 0){
								MboRemote uditemcp = uditemcpSet2.getMbo(0);
								String udprevendor = uditemcp.getString("vendor");
								mbo.setValue("udprevendor", udprevendor,2L);
							}
							uditemcpSet2.close();
							
							String htvedor = findMinValue(itemnum);
                            if((uditemcpSet2.isEmpty() || uditemcpSet2.count() == 0) && vendorExists(htvedor)){
                            	mbo.setValue("udprevendor", htvedor,2L);
                            }
                            if((uditemcpSet2.isEmpty() || uditemcpSet2.count() == 0) && !vendorExists(htvedor)){
                                mbo.setValue("udprevendor", "ZEETEST",11L);
                            }
							
							//ZEE - PRLINE选择itemnum后，udprevendor若自动有值，则根据itemnum&udprevendor到udconversion表匹配对应的from measure unit、转换系数，订购单位、转换系数只读
							//udprevendor若自动无值，则自动代入item的order unit，并且转换系数可变，订购单位、转换系数必填
							String frommeasureunit = "";
							Double conversion = 1.0;
//							Double roundfactor = 1.0;
							MboSetRemote itemSet = MXServer.getMXServer().getMboSet("ITEM", MXServer.getMXServer().getSystemUserInfo());
							itemSet.setWhere(" itemnum = '" + itemnum +"' ");
							itemSet.reset();
							//
//							Double maxlimit = mbo.getDouble("UDITEMCP.maxlimit");
//							Double minlimit = mbo.getDouble("UDITEMCP.minlimit");
//							Double curbaltotal = mbo.getDouble("INVENTORY.curbaltotal");
//							Double udotwqty = mbo.getDouble("udotwqty");//在途
//							Double orderqty = maxlimit-(curbaltotal+udotwqty);
							//
							if(mbo.getString("udprevendor")!=null && !mbo.getString("udprevendor").equalsIgnoreCase("")){
								MboSetRemote udconversionSet = MXServer.getMXServer().getMboSet("UDCONVERSION", MXServer.getMXServer().getSystemUserInfo());
								udconversionSet.setWhere(" itemnum = '" + itemnum +"' and vendor = '" + mbo.getString("udprevendor") +"' ");
								udconversionSet.reset();
								if(!udconversionSet.isEmpty() && udconversionSet.count() > 0){
									frommeasureunit = udconversionSet.getMbo(0).getString("frommeasureunit");
									conversion = udconversionSet.getMbo(0).getDouble("conversion");
									Double roundfactor = udconversionSet.getMbo(0).getDouble("roundfactor");
									if( frommeasureunit!=null && !frommeasureunit.equalsIgnoreCase("")){
										if(!roundfactor.equals("") && roundfactor != 0){
											Double maxlimit = mbo.getDouble("UDITEMCP.maxlimit");
											if(!String.valueOf(maxlimit).equalsIgnoreCase("") && maxlimit!=0){
//											Double curbaltotal = mbo.getDouble("INVENTORY.curbaltotal");
//											Double curbaltotal = mbo.getDouble("udcurbaltotal");//当前余量
//											Double curbaltotal = mbo.getMboSet("UDINVENTORY").sum("curbaltotal");
											MboSetRemote udinventorySet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
											udinventorySet.setWhere(" itemnum = '" + itemnum +"' and location in ( 'ZEE-01') ");
											udinventorySet.reset();
											Double curbaltotal = udinventorySet.sum("curbal");
											System.out.println("1213--2-curbaltotal-"+curbaltotal);
//											Double udotwqty = mbo.getDouble("udotwqty")*conversion;//在途
											System.out.println("1213--1-sumorfqty1-"+ mbo.getMboSet("UDMATPRLINEOTW").sum("orderqty")*conversion);
											System.out.println("1213--1-sumorfqty2-"+ mbo.getMboSet("UDMATPOLINEOTW").sum("orderqty")*conversion);
											Double udotwqty = (mbo.getMboSet("UDMATPRLINEOTW").sum("orderqty") + mbo.getMboSet("UDMATPOLINEOTW").sum("orderqty"))*conversion;
											Double orderqty = (maxlimit-(curbaltotal+udotwqty))/conversion;
											Double resultup =  (Math.ceil(orderqty / roundfactor))*roundfactor;//最小订购数量
											if(resultup>0){
												mbo.setValue("orderqty", resultup,11L);	
												//1209
												mbo.setValue("udissueqty", resultup*conversion,11L);//最小发放数量
											}
											}else if(String.valueOf(maxlimit).equalsIgnoreCase("") || maxlimit==0){
												mbo.setValue("orderqty", "1",11L);
												mbo.setValue("udissueqty", 1*conversion,11L);
											}
										}
										mbo.setValue("orderunit", frommeasureunit,2L);
										mbo.setValue("conversion", conversion,2L);
										mbo.setFieldFlag("conversion", 128L, false);//设置非必填
										mbo.setFieldFlag("conversion", 7L, true);//设置只读
										mbo.setFieldFlag("orderunit", 128L, false);//设置非必填
										mbo.setFieldFlag("orderunit", 7L, true);//设置只读
									}else if(frommeasureunit.equalsIgnoreCase("")){
										mbo.setValue("orderunit", mbo.getMboSet("ITEM").getString("orderunit"),2L);
										mbo.setValue("conversion", "1",2L);
										mbo.setFieldFlag("conversion", 7L, false);//取消只读
										mbo.setFieldFlag("conversion", 128L, true);//设置必填
										mbo.setFieldFlag("orderunit", 7L, false);//取消只读
										mbo.setFieldFlag("orderunit", 128L, true);//设置必填
									}
								}else if(udconversionSet.isEmpty() && udconversionSet.count() == 0){
									if(!itemSet.isEmpty() && itemSet.count() > 0){
										mbo.setValue("orderunit", itemSet.getMbo(0).getString("orderunit"),2L);
										mbo.setValue("conversion", "1",2L);
										mbo.setFieldFlag("conversion", 7L, false);//取消只读
										mbo.setFieldFlag("conversion", 128L, true);//设置必填
										mbo.setFieldFlag("orderunit", 7L, false);//取消只读
										mbo.setFieldFlag("orderunit", 128L, true);//设置必填
										}
								}
								udconversionSet.close();
							}else{
								if(!itemSet.isEmpty() && itemSet.count() > 0){
									mbo.setValue("orderunit", itemSet.getMbo(0).getString("orderunit"),2L);
									mbo.setValue("conversion", "1",2L);
									mbo.setFieldFlag("conversion", 7L, false);//取消只读
									mbo.setFieldFlag("conversion", 128L, true);//设置必填
									mbo.setFieldFlag("orderunit", 7L, false);//取消只读
									mbo.setFieldFlag("orderunit", 128L, true);//设置必填
								}
							}	
							itemSet.close();
							
					}
						
					}
				}
			}
			// 标准服务
			String itemnum = mbo.getString("itemnum");
			String materialType = CommonUtil.getValue(mbo, "ITEM", "udmaterialType");
			if (itemnum == null || itemnum.equalsIgnoreCase("")
					|| (materialType != null && materialType.equalsIgnoreCase("5201"))) {
				mbo.setValue("issue", 1, 11L);
			}
		}
	}
	
	//找合同中最低价供应商
	public String findMinValue(String itemnum) throws RemoteException, MXException{
		String vendor = "";
		String gconnum = "";
		Double min = 0.0;
		MboSetRemote udconSet = MXServer.getMXServer().getMboSet("UDCONTRACTLINE", MXServer.getMXServer().getSystemUserInfo());
		udconSet.setWhere(" itemnum = '" + itemnum + "' and linetype = 'ITEM' and gconnum in (select gconnum from udcontract where status='APPR' and udcompany='ZEE' and to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd')) ");
		udconSet.reset();
		if(!udconSet.isEmpty() && udconSet.count() > 0){
			MboRemote udcon = udconSet.getMbo(0);
			min = udcon.getDouble("uddiscountprice");
			gconnum = udcon.getString("gconnum");
			for(int i = 0; i< udconSet.count(); i++){
				MboRemote udc = udconSet.getMbo(i);
				if(udc.getDouble("uddiscountprice") - min < -0.0000001){
					min = udc.getDouble("uddiscountprice");
					gconnum = udc.getString("gconnum");
				}
			}
			
		}
		MboSetRemote udcontractSet = MXServer.getMXServer().getMboSet("UDCONTRACT", MXServer.getMXServer().getSystemUserInfo());
		udcontractSet.setWhere(" gconnum = '" + gconnum + "' ");
		udcontractSet.reset();
		if(!udcontractSet.isEmpty() && udcontractSet.count() > 0){
			MboRemote udcontract = udcontractSet.getMbo(0);
			vendor = udcontract.getString("vendor");
		}		
		return vendor;	
	}
	
    //判断供应商是否存在系统
    public Boolean vendorExists(String vendor) throws RemoteException, MXException{
            MboSetRemote vendorSet = MXServer.getMXServer().getMboSet("COMPANIES", MXServer.getMXServer().getSystemUserInfo());
            vendorSet.setWhere(" company = '"+vendor+"' ");
            vendorSet.reset();
            if(!vendorSet.isEmpty() && vendorSet.count() > 0){
                    return true;
            }else{
                    return false;
            }
    }
	
}
