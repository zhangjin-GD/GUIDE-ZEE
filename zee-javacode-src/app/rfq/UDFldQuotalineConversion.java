package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldQuotalineConversion extends MAXTableDomain{
	/**
	 * ZEE - quotationline.udconversion：根据输入的udconversion，基于udissueqty、udroundfactor，计算最小订购数量
	 * 2024-12-27  11:17  
	 */
	public UDFldQuotalineConversion(MboValue mbv) {
		super(mbv);
	}
	
	public void action() throws MXException, RemoteException {
		super.action();
		String zeevenconverStatus = MXServer.getMXServer().getProperty("guide.zeevenconver.enabled");
		if (zeevenconverStatus != null && zeevenconverStatus.equalsIgnoreCase("ACTIVE")) {
		MboRemote mbo = getMboValue().getMbo(); //QUOTATIONLINE
		MboSetRemote rfq = mbo.getMboSet("RFQ");
		if (mbo != null && rfq.getMbo(0).getString("udcompany").equalsIgnoreCase("ZEE") && mbo.getString("udcalculate").equalsIgnoreCase("Y")){
			String itemnum = mbo.getString("itemnum");
			Double udroundfactor = mbo.getDouble("udroundfactor");
			Double udissueqty = mbo.getDouble("udissueqty");
			MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
			uditemcpSet.setWhere(" itemnum = '" + itemnum +"' ");
			uditemcpSet.reset();
		Double maxlimit = uditemcpSet.getMbo(0).getDouble("maxlimit");
		if(!String.valueOf(maxlimit).equalsIgnoreCase("") && maxlimit!=0){
			MboSetRemote udinventorySet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
			udinventorySet.setWhere(" itemnum = '" + itemnum +"' ");
			udinventorySet.reset();
				Double curbaltotal = udinventorySet.sum("curbal");
			    if (String.valueOf(mbo.getDouble("udconversion")).equals("") || mbo.getDouble("udconversion") == 0) {
			    	mbo.setValue("udissueqty", "", 11L);
		            return;
		        }
				if((!String.valueOf(mbo.getDouble("udconversion")).equals("") && mbo.getDouble("udconversion")!=0) && (!String.valueOf(udroundfactor).equals("") && udroundfactor != 0)){
					Double manualUdconversion = mbo.getDouble("udconversion"); // 手动修改后的udconversion
					//在途
					Double udotwqty = (mbo.getMboSet("UDZEEPRTRANSIT").sum("orderqty") + mbo.getMboSet("UDZEEPOTRANSIT").sum("orderqty") )*manualUdconversion;//?
//					Double udotwqty = 0.0;
					Double manualminorderqty = (Math.ceil((maxlimit-(curbaltotal+udotwqty))/manualUdconversion/ udroundfactor))*udroundfactor;
					if((curbaltotal+udotwqty)<=maxlimit){
						 mbo.setValue("orderqty", manualminorderqty, 11L);
					} 
				}
				}
			}
		}
	}
}
