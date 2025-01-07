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
		MboSetRemote rfq = mbo.getMboSet("RFQ");
		if (mbo != null && rfq.getMbo(0).getString("udcompany").equalsIgnoreCase("ZEE") && mbo.getString("udcalculate").equalsIgnoreCase("Y")){
			String itemnum = mbo.getString("itemnum");
			Double udconversion = mbo.getDouble("udconversion");
			Double udroundfactor = mbo.getDouble("udroundfactor");
			MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
			uditemcpSet.setWhere(" itemnum = '" + itemnum +"' ");
			uditemcpSet.reset();
		Double maxlimit = uditemcpSet.getMbo(0).getDouble("maxlimit");
		if(!String.valueOf(maxlimit).equalsIgnoreCase("") && maxlimit!=0){
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
				if((curbaltotal+udotwqty+manualUdissueqty)<=maxlimit){
					curbaltotal=curbaltotal+manualUdissueqty;
					Double manualorderqty = (Math.ceil((maxlimit-(curbaltotal+udotwqty))/udconversion / udroundfactor))*udroundfactor;
					mbo.setValue("orderqty", manualorderqty, 11L);
				}else if((curbaltotal+udotwqty+manualUdissueqty)>maxlimit){
					Object params[] = { " Notice: please input <= "+(maxlimit-(curbaltotal+udotwqty))+" , confirm it will not more than the maxlimit "+maxlimit+" ! (If the input limit is minus, which means the maxlimit should be adjusted upper!)"};
					throw new MXApplicationException("instantmessaging", "tsdimexception",params);
					}
				
		}else if((!String.valueOf(udconversion).equals("") && udconversion != 0) && (String.valueOf(udroundfactor).equals("") || udroundfactor == 0)){
			Double maxlimit1 = mbo.getDouble("UDITEMCP.maxlimit");//最大库存
			if(!String.valueOf(maxlimit1).equalsIgnoreCase("") && maxlimit1!=0){
			Double udotwqty1 = (mbo.getMboSet("UDZEEPRTRANSIT").sum("orderqty") + mbo.getMboSet("UDZEEPOTRANSIT").sum("orderqty"))*udconversion;
			MboSetRemote udinventorySet1 = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
			udinventorySet1.setWhere(" itemnum = '" + itemnum +"' ");
			udinventorySet1.reset();
			Double curbaltotal1 = udinventorySet1.sum("curbal");
				if(mbo.getDouble("udissueqty") > 0 && !String.valueOf(maxlimit1).equalsIgnoreCase("") && maxlimit!=0){
					if((curbaltotal1+udotwqty1+mbo.getDouble("udissueqty"))<=maxlimit1){
						curbaltotal1=curbaltotal1+mbo.getDouble("udissueqty");
					mbo.setValue("orderqty", (maxlimit1-(curbaltotal1+udotwqty1))/udconversion, 11L);
					}else if((curbaltotal1+udotwqty1+mbo.getDouble("udissueqty"))>maxlimit1){
						Object params[] = { " Notice: please input <= "+(maxlimit1-(curbaltotal1+udotwqty1))+" , confirm it will not more than the maxlimit "+maxlimit1+" ! (If the input limit is minus, which means the maxlimit should be adjusted upper!) "};
						throw new MXApplicationException("instantmessaging", "tsdimexception",params);
					}
				}
			}
		}
				}
		if(mbo.getDouble("udissueqty") >= 0 && String.valueOf(mbo.getDouble("UDITEMCP.maxlimit")).equalsIgnoreCase("") || mbo.getDouble("UDITEMCP.maxlimit")==0){
			mbo.setValue("orderqty", mbo.getDouble("udissueqty")/mbo.getDouble("conversion"), 11L);
		}
			}
		}
	}
	
}
