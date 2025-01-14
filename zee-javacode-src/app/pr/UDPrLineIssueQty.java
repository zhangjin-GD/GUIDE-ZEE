package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDPrLineIssueQty extends MAXTableDomain{

	public UDPrLineIssueQty(MboValue mbv) {
		super(mbv);
		// TODO Auto-generated constructor stub
	}
	/**
	 * DJY
	 * ZEE - 完善系统中采购的转换系数、最小订购数量
	 * 2024-12-9 14:20
	 * */
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo(); //PRLINE
		MboRemote owner = mbo.getOwner();
		if (mbo != null && owner.getString("udcompany").equalsIgnoreCase("ZEE")){
			Double conversion = mbo.getDouble("conversion");	
			String itemnum = mbo.getString("itemnum");
			String udprevendor = mbo.getString("udprevendor");
			MboSetRemote udconversionSet = MXServer.getMXServer().getMboSet("UDCONVERSION", MXServer.getMXServer().getSystemUserInfo());
			udconversionSet.setWhere(" itemnum = '" + itemnum +"' and vendor = '" + udprevendor +"' ");
			udconversionSet.reset();
			if(!udconversionSet.isEmpty() && udconversionSet.count() > 0){
				Double roundfactor = udconversionSet.getMbo(0).getDouble("roundfactor");
					if((!String.valueOf(conversion).equals("") && conversion != 0) && (!String.valueOf(roundfactor).equals("") && roundfactor != 0)){
						Double manualUdissueqty = mbo.getDouble("udissueqty"); // 手动修改后的udissueqty
							Double manualorderqty = (Math.ceil(manualUdissueqty/conversion / roundfactor))*roundfactor;
							if(manualUdissueqty >= 0){
								mbo.setValue("orderqty", manualorderqty, 11L);
								}else if(manualUdissueqty < 0){
									Object params[] = { " Issue quantity cannot be less than 0 !  "};
									throw new MXApplicationException("instantmessaging", "tsdimexception",params);
								}
				}else if((!String.valueOf(conversion).equals("") && conversion != 0) && (String.valueOf(roundfactor).equals("") || roundfactor == 0)){
						if(mbo.getDouble("udissueqty") >= 0){
								mbo.setValue("orderqty", mbo.getDouble("udissueqty") /conversion, 11L);
						}else if(mbo.getDouble("udissueqty") < 0){
							Object params[] = { " Issue quantity cannot be less than 0 !  "};
							throw new MXApplicationException("instantmessaging", "tsdimexception",params);
						}
				}
	}else if(udconversionSet.isEmpty() || udconversionSet.count() == 0){
				if(mbo.getDouble("udissueqty") >= 0){
								mbo.setValue("orderqty", mbo.getDouble("udissueqty")/ mbo.getDouble("conversion"), 11L);
				}else if(mbo.getDouble("udissueqty") < 0){
					Object params[] = { " Issue quantity cannot be less than 0 !  "};
					throw new MXApplicationException("instantmessaging", "tsdimexception",params);
				}
			}
		}
	}

}
