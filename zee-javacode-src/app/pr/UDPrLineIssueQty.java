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
		if (mbo != null){
			String frommeasureunit = "";
			Double conversion = 1.0;	
			String itemnum = mbo.getString("itemnum");
			String udprevendor = mbo.getString("udprevendor");
			MboSetRemote itemSet = MXServer.getMXServer().getMboSet("ITEM", MXServer.getMXServer().getSystemUserInfo());
			itemSet.setWhere(" itemnum = '" + itemnum +"' ");
			itemSet.reset();
			MboSetRemote udconversionSet = MXServer.getMXServer().getMboSet("UDCONVERSION", MXServer.getMXServer().getSystemUserInfo());
			udconversionSet.setWhere(" itemnum = '" + itemnum +"' and vendor = '" + udprevendor +"' ");
			udconversionSet.reset();
			if(!udconversionSet.isEmpty() && udconversionSet.count() > 0){
				frommeasureunit = udconversionSet.getMbo(0).getString("frommeasureunit");
				conversion = udconversionSet.getMbo(0).getDouble("conversion");
				Double roundfactor = udconversionSet.getMbo(0).getDouble("roundfactor");
				if( frommeasureunit!=null && !frommeasureunit.equalsIgnoreCase("")){
					if(!roundfactor.equals("") && roundfactor != 0){
						Double maxlimit = mbo.getDouble("UDITEMCP.maxlimit");//最大库存
						MboSetRemote udinventorySet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
						udinventorySet.setWhere(" itemnum = '" + itemnum +"' ");
						udinventorySet.reset();
						Double curbaltotal = udinventorySet.sum("curbal");
						Double udotwqty = (mbo.getMboSet("UDMATPRLINEOTW").sum("orderqty") + mbo.getMboSet("UDMATPOLINEOTW").sum("orderqty"))*conversion;
						Double initminorderqty = (Math.ceil((maxlimit-(curbaltotal+udotwqty))/conversion / roundfactor))*roundfactor;//初始化计算最小订购数量
						Double manualUdissueqty = mbo.getDouble("udissueqty"); // 手动修改后的udissueqty
						Double manualorderqty = manualUdissueqty / conversion;// 手动修改后的orderqty
							if(initminorderqty > 0){
								if(manualorderqty < initminorderqty){
									Object params[] = { " Issue quantity should be more than the minimum issue quantity "+initminorderqty*conversion + " ! "};
									throw new MXApplicationException("instantmessaging", "tsdimexception",params);
								}else if(manualorderqty>=0 &&  manualorderqty >=initminorderqty){
									// 如果在合理范围内，根据roundfactor调整最终的orderqty
									Double finalmanorderqty = Math.ceil(manualorderqty / roundfactor) * roundfactor;
									 mbo.setValue("orderqty", finalmanorderqty, 11L);
								}
						}
			}else if(roundfactor.equals("") || roundfactor == 0){
					if(mbo.getDouble("udissueqty") > 0){
						mbo.setValue("orderqty", mbo.getDouble("udissueqty")/conversion, 11L);
				}
			}
		}
	}else if(udconversionSet.isEmpty() || udconversionSet.count() == 0){
				if(mbo.getDouble("udissueqty") >= 0){
				mbo.setValue("orderqty", mbo.getDouble("udissueqty")/mbo.getDouble("conversion"), 2L);
				}else if(mbo.getDouble("udissueqty") < 0){
					Object params[] = { " Issue quantity cannot be less than 0 !  "};
					throw new MXApplicationException("instantmessaging", "tsdimexception",params);
				}
			}
		}
	}

}
