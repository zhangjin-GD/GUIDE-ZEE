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
			Double udconversion = mbo.getDouble("udconversion");
			Double udroundfactor = mbo.getDouble("udroundfactor");
					if((!String.valueOf(udconversion).equals("") && udconversion != 0) && (!String.valueOf(udroundfactor).equals("") && udroundfactor != 0)){
						Double manualUdissueqty = mbo.getDouble("udissueqty"); // 手动修改后的udissueqty
							Double manualorderqty = (Math.ceil(manualUdissueqty/udconversion / udroundfactor))*udroundfactor;
							if(manualUdissueqty >= 0){
							mbo.setValue("orderqty", manualorderqty, 11L);
							}else if(manualUdissueqty < 0){
								Object params[] = { " Issue quantity cannot be less than 0 !  "};
								throw new MXApplicationException("instantmessaging", "tsdimexception",params);
								}
					}else if((!String.valueOf(udconversion).equals("") && udconversion != 0) && (String.valueOf(udroundfactor).equals("") || udroundfactor == 0)){
								if(mbo.getDouble("udissueqty") >= 0){
										mbo.setValue("orderqty", mbo.getDouble("udissueqty") /udconversion, 11L);
								}else if(mbo.getDouble("udissueqty") < 0){
									Object params[] = { " Issue quantity cannot be less than 0 !  "};
									throw new MXApplicationException("instantmessaging", "tsdimexception",params);
									}
					}
				}
			}
		}
	
}
