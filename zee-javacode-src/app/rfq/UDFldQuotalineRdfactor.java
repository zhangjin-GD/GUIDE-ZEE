package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldQuotalineRdfactor extends MAXTableDomain{
	/**
	 * ZEE - quotationline.udroundfactor：根据输入的udroundfactor，基于udissueqty、udconversion，计算最小订购数量
	 * 2024-12-27  11:17  
	 */
	public UDFldQuotalineRdfactor(MboValue mbv) {
		super(mbv);
	}

	public void action() throws MXException, RemoteException {
		super.action();
		String zeevenconverStatus = MXServer.getMXServer().getProperty("guide.zeevenconver.enabled");
		if (zeevenconverStatus != null && zeevenconverStatus.equalsIgnoreCase("ACTIVE")) {
		MboRemote mbo = getMboValue().getMbo(); //RFQLINE
		MboSetRemote rfq = mbo.getMboSet("RFQ");
		if (mbo != null && rfq.getMbo(0).getString("udcompany").equalsIgnoreCase("ZEE") && mbo.getString("udcalculate").equalsIgnoreCase("Y")){
			String itemnum = mbo.getString("itemnum");
			Double udconversion = mbo.getDouble("udconversion");
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
				//在途
				Double udotwqty = (mbo.getMboSet("UDZEEPRTRANSIT").sum("orderqty") + mbo.getMboSet("UDZEEPOTRANSIT").sum("orderqty") )*udconversion;//?
//				Double udotwqty = 0.0;
				if (String.valueOf(mbo.getDouble("udroundfactor")).equals("") || mbo.getDouble("udroundfactor") == 0) {
		            return; 
		        }
			    if((String.valueOf(udconversion).equals("") || udconversion == 0)){
					mbo.setValue("udissueqty", "", 11L);
					return; 
				}
				if((!String.valueOf(mbo.getDouble("udroundfactor")).equals("") && mbo.getDouble("udroundfactor")!=0) && (!String.valueOf(udconversion).equals("") && udconversion != 0)){
					Double manualUdroundfactor = mbo.getDouble("udroundfactor"); // 手动修改后的udroundfactor
					Double manualminorderqty = (Math.ceil((maxlimit-(curbaltotal+udotwqty))/udconversion/ manualUdroundfactor))*manualUdroundfactor;
					if((curbaltotal+udotwqty)<=maxlimit){
						 mbo.setValue("orderqty", manualminorderqty, 11L);
						} 
					}
				}
			}
		}
	}
}
