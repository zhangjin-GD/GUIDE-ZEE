package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldQuotalineIssueQty extends MAXTableDomain{
	/**
	 * ZEE - quotationline.udissueqty：根据输入的发放数量，基于udconversion、udroundfactor，计算最小订购数量
	 * 2024-12-26  11:17  
	 */

	public UDFldQuotalineIssueQty(MboValue mbv) {
		super(mbv);
	}
	
	public void action() throws MXException, RemoteException {
		super.action();
		String zeevenconverStatus = MXServer.getMXServer().getProperty("guide.zeevenconver.enabled");
		if (zeevenconverStatus != null && zeevenconverStatus.equalsIgnoreCase("ACTIVE")) {
		MboRemote mbo = getMboValue().getMbo(); 
		if (mbo != null){
			String itemnum = mbo.getString("itemnum");
			Double udconversion = mbo.getDouble("udconversion");
			Double udroundfactor = mbo.getDouble("udroundfactor");
			MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
			uditemcpSet.setWhere(" itemnum = '" + itemnum +"' ");
			uditemcpSet.reset();
				Double maxlimit = uditemcpSet.getMbo(0).getDouble("maxlimit");
			MboSetRemote udinventorySet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
			udinventorySet.setWhere(" itemnum = '" + itemnum +"' ");
			udinventorySet.reset();
				Double curbaltotal = udinventorySet.sum("curbal");
				//在途
				MboSetRemote prlineSet = mbo.getMboSet("prline");
				Double udotwqty = (mbo.getMboSet("UDZEEPRTRANSIT").sum("orderqty") + mbo.getMboSet("UDZEEPOTRANSIT").sum("orderqty"))*udconversion;
//				Double udotwqty = 0.0;
		    if (String.valueOf(mbo.getDouble("udissueqty")).equals("") || mbo.getDouble("udissueqty") == 0) {
		            return; 
		        }
			if((!String.valueOf(udconversion).equals("") && udconversion != 0) && (!String.valueOf(udroundfactor).equals("") && udroundfactor != 0)){
				Double initminorderqty = (Math.ceil((maxlimit-(curbaltotal+udotwqty))/udconversion / udroundfactor))*udroundfactor;
				Double manualUdissueqty = mbo.getDouble("udissueqty"); // 手动修改后的udissueqty
				Double manualorderqty = manualUdissueqty / udconversion;// 手动修改后的orderqty			
				if(initminorderqty > 0){
					if(manualorderqty < initminorderqty){
						Object params[] = { " Issue quantity should be more than the minimum issue quantity "+initminorderqty*udconversion + " ! "};
						throw new MXApplicationException("instantmessaging", "tsdimexception",params);
					}else if(manualorderqty>=0 &&  manualorderqty >=initminorderqty){
						// 如果在合理范围内，根据roundfactor调整最终的orderqty
						Double finalmanorderqty = Math.ceil(manualorderqty / udroundfactor) * udroundfactor;
						 mbo.setValue("orderqty", finalmanorderqty, 11L);
						}
					}
			}else if((!String.valueOf(udconversion).equals("") && udconversion != 0) && (String.valueOf(udroundfactor).equals("") || udroundfactor == 0)){
				if(!String.valueOf(mbo.getDouble("udissueqty")).equals("") && mbo.getDouble("udissueqty") > 0){
					mbo.setValue("orderqty", mbo.getDouble("udissueqty")/udconversion, 11L);
					}
				}
			}
		}
	}
	
}
