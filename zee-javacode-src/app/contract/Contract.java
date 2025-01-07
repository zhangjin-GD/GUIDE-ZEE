package guide.app.contract;

import java.rmi.RemoteException;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class Contract extends UDMbo implements MboRemote {

	public Contract(MboSet ms) throws RemoteException {
		super(ms);
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
					if ("APPR".equalsIgnoreCase(status) || "CAN".equalsIgnoreCase(status)) {
						attributeReadonly(true);
					} else {
						attributeReadonly(false);
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void attributeReadonly(boolean state) throws RemoteException, MXException {
		String[] attrMbo = { "description", "contype", "purchaseagent", "vendor", "startdate", "enddate" };
		this.setFieldFlag(attrMbo, 7L, state);
		MboSetRemote invuseLineSet = this.getMboSet("UDCONTRACTLINE");
		invuseLineSet.setFlag(7L, state);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String personId = this.getUserInfo().getPersonId();
		this.setValue("purchaseagent", personId, 2L);
		this.setValue("totalcost", 0, 11L);
		this.setValue("pretaxtotal", 0, 11L);
	}

	public void save() throws MXException, RemoteException {
		super.save();
		/**
		 * ZEE - poline - 合同应用程序，APPR时，该物资的单个/多个有效合同供应商。
		 * active勾选：如果单个，则直接更新表时设置active=1，如果多个则找合同最低价的一条更新表时设置active=1。
		 * DJY
		 * 63 - 203
		 * 2025/1/2 14:10
		 */
		String zeevenconverStatus = MXServer.getMXServer().getProperty("guide.zeevenconver.enabled");
		if (zeevenconverStatus != null && zeevenconverStatus.equalsIgnoreCase("ACTIVE")) {
		String udcompany = getString("udcompany");
		if(udcompany.equalsIgnoreCase("ZEE")){
			String status = getString("status");
			if(isModified("status") && status.equalsIgnoreCase("APPR")){
				String gconnum = getString("gconnum");
				MboSetRemote udcontractlineSet = MXServer.getMXServer().getMboSet("UDCONTRACTLINE", MXServer.getMXServer().getSystemUserInfo());
				udcontractlineSet.setWhere(" gconnum = '"+gconnum+"' ");
				udcontractlineSet.reset();
				if(!udcontractlineSet.isEmpty() && udcontractlineSet.count() > 0){
					for(int i = 0; i <udcontractlineSet.count(); i++){
						MboRemote udcontractline = udcontractlineSet.getMbo(i);
						if(udcontractline.getString("linetype").equalsIgnoreCase("ITEM")){
							String itemnum = udcontractline.getString("itemnum");
							MboSetRemote itemSet = MXServer.getMXServer().getMboSet("ITEM", MXServer.getMXServer().getSystemUserInfo());
							itemSet.setWhere(" itemnum = '"+itemnum+"' ");
							itemSet.reset();
							String issueunit = itemSet.getMbo(0).getString("issueunit");
							MboSetRemote udcontractSet1 = MXServer.getMXServer().getMboSet("UDCONTRACT", MXServer.getMXServer().getSystemUserInfo());
							udcontractSet1.setWhere(" gconnum in (select gconnum from udcontractline where itemnum = '"+itemnum+"') and udcompany = 'ZEE' and (status = 'APPR' or gconnum = '"+gconnum+"') and  to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd') ");
							udcontractSet1.reset();//包含此单据APPR和其他单据APPR
							if(!udcontractSet1.isEmpty() && udcontractSet1.count() > 0){
										for(int j = 0; j<udcontractSet1.count(); j++){
											MboRemote udcontract = udcontractSet1.getMbo(j);
											String htvendor = udcontract.getString("vendor");
											MboSetRemote uditemcpvenSet3 = getUditemcpvenSet(itemnum,htvendor);
												int maxLinenum = 0;
											if (uditemcpvenSet3.isEmpty()) {
												 MboSetRemote uditemcpvenSet4 = MXServer.getMXServer().getMboSet("UDITEMCPVEN", MXServer.getMXServer().getSystemUserInfo());
												 uditemcpvenSet4.setWhere(" itemnum = '"+itemnum+"' ");
												 uditemcpvenSet4.reset();
									            if (!uditemcpvenSet4.isEmpty()) {
									                maxLinenum = uditemcpvenSet4.getMbo(0).getInt("linenum");
									                for(int k = 0; k<uditemcpvenSet4.count(); k++) {
									                    int currentLinenum = uditemcpvenSet4.getMbo(k).getInt("linenum");
									                    if (currentLinenum > maxLinenum) {
									                        maxLinenum = currentLinenum;
									                    }
									                }
									            }else{
									            	maxLinenum = 0;
									            }
												MboSetRemote udcontractlineSet1 = MXServer.getMXServer().getMboSet("UDCONTRACTLINE", MXServer.getMXServer().getSystemUserInfo());
												udcontractlineSet1.setWhere(" gconnum = '"+udcontract.getString("gconnum")+"' and itemnum = '"+itemnum+"' ");
												udcontractlineSet1.reset(); 
												String orderunit = udcontractlineSet1.getMbo(0).getString("orderunit");
												String udconversion = udcontractlineSet1.getMbo(0).getString("udconversion");
												String udroundfactor = udcontractlineSet1.getMbo(0).getString("udroundfactor");
												if(!orderunit.equalsIgnoreCase("") && !udconversion.equalsIgnoreCase("") &&  !udroundfactor.equalsIgnoreCase("") ){
												 MboSetRemote uditemcpvenSet = MXServer.getMXServer().getMboSet("UDITEMCPVEN", MXServer.getMXServer().getSystemUserInfo());
												 uditemcpvenSet.setWhere(" 1=2 ");
												 if(!itemSet.getMbo(0).getString("issueunit").equalsIgnoreCase(itemSet.getMbo(0).getString("orderunit"))){
												 MboRemote newUditemcpven = uditemcpvenSet.add(11L);
												 newUditemcpven.setValue("itemnum", itemnum,2L);
												 newUditemcpven.setValue("frommeasureunit", orderunit,11L);
												 newUditemcpven.setValue("tomeasureunit", issueunit,11L);
												 newUditemcpven.setValue("conversion", udconversion,11L);
												 newUditemcpven.setValue("roundfactor", udroundfactor,11L);
												 newUditemcpven.setValue("vendor", htvendor,11L);
												 newUditemcpven.setValue("udcompany", "ZEE",11L);
												 newUditemcpven.setValue("linenum", maxLinenum+1,11L);
												 uditemcpvenSet.save();
												 }
												uditemcpvenSet.close();
														}
													} 
										}	
										 String htminvedor = findMinValue(itemnum,gconnum);
										 MboSetRemote uditemcpvenSet5 = MXServer.getMXServer().getMboSet("UDITEMCPVEN", MXServer.getMXServer().getSystemUserInfo());
										 uditemcpvenSet5.setWhere(" itemnum = '"+itemnum+"' ");
										 uditemcpvenSet5.reset();
										 if(!uditemcpvenSet5.isEmpty() && uditemcpvenSet5.count() > 0){
											 for(int m=0; m<uditemcpvenSet5.count(); m++){
											 uditemcpvenSet5.getMbo(m).setValue("udactive", "0",2L);
											 }
											 uditemcpvenSet5.save();
											 uditemcpvenSet5.close();
										 }
										 MboSetRemote uditemcpvenSet6 = MXServer.getMXServer().getMboSet("UDITEMCPVEN", MXServer.getMXServer().getSystemUserInfo());
										 uditemcpvenSet6.setWhere(" itemnum = '"+itemnum+"' and vendor = '"+htminvedor+"' ");
										 uditemcpvenSet6.reset();
										 if(!uditemcpvenSet6.isEmpty() && uditemcpvenSet6.count() > 0){
											 uditemcpvenSet6.getMbo(0).setValue("udactive", "1",2L);
											 uditemcpvenSet6.save();
											 uditemcpvenSet6.close();
										 }
//									}
								}
							}
						}
					
					}
				}
			}
		}
	}
	
	private MboSetRemote getUditemcpvenSet(String itemnum, String uditemcpvendor) throws RemoteException, MXException {
	    MboSetRemote uditemcpvenSet = MXServer.getMXServer().getMboSet("UDITEMCPVEN", MXServer.getMXServer().getSystemUserInfo());
	    uditemcpvenSet.setWhere("udcompany = 'ZEE' and itemnum = '" + itemnum + "' and vendor = '" + uditemcpvendor + "'");
	    uditemcpvenSet.reset();
	    return uditemcpvenSet;
	}
	
	//找合同中最低价供应商
	private String findMinValue(String itemnum,String gcon) throws RemoteException, MXException{
		String vendor = "";
		String gconnum = "";
		Double min = 0.0;
		MboSetRemote udconSet = MXServer.getMXServer().getMboSet("UDCONTRACTLINE", MXServer.getMXServer().getSystemUserInfo());
		udconSet.setWhere(" itemnum = '" + itemnum + "' and linetype = 'ITEM' and gconnum in (select gconnum from udcontract where  udcompany = 'ZEE' and (status = 'APPR' or gconnum = '"+gcon+"') and to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd')) ");
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
}
