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
						if(!String.valueOf(maxlimit).equalsIgnoreCase("") && maxlimit!=0){
						MboSetRemote udinventorySet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
						udinventorySet.setWhere(" itemnum = '" + itemnum +"' ");
						udinventorySet.reset();
						Double curbaltotal = udinventorySet.sum("curbal");
						Double udotwqty = (mbo.getMboSet("UDMATPRLINEOTW").sum("orderqty") + mbo.getMboSet("UDMATPOLINEOTW").sum("orderqty"))*conversion;
						Double initminorderqty = (Math.ceil((maxlimit-(curbaltotal+udotwqty))/conversion / roundfactor))*roundfactor;//初始化计算最小订购数量
						Double manualUdissueqty = mbo.getDouble("udissueqty"); // 手动修改后的udissueqty
						if((curbaltotal+udotwqty+manualUdissueqty)<=maxlimit){
							curbaltotal=curbaltotal+manualUdissueqty;
							Double manualorderqty = (Math.ceil((maxlimit-(curbaltotal+udotwqty))/conversion / roundfactor))*roundfactor;
							mbo.setValue("orderqty", manualorderqty, 11L);
						}else if((curbaltotal+udotwqty+manualUdissueqty)>maxlimit){
							Object params[] = { " Notice: please input <= "+(maxlimit-(curbaltotal+udotwqty))+" , confirm it will not more than the maxlimit "+maxlimit+" ! (If the input limit is minus, which means the maxlimit should be adjusted upper!)"};
							throw new MXApplicationException("instantmessaging", "tsdimexception",params);
						}
					}
			}else if(roundfactor.equals("") || roundfactor == 0){
				Double maxlimit = mbo.getDouble("UDITEMCP.maxlimit");//最大库存
				if(!String.valueOf(maxlimit).equalsIgnoreCase("") && maxlimit!=0){
				Double udotwqty = (mbo.getMboSet("UDMATPRLINEOTW").sum("orderqty") + mbo.getMboSet("UDMATPOLINEOTW").sum("orderqty"))*conversion;
				MboSetRemote udinventorySet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
				udinventorySet.setWhere(" itemnum = '" + itemnum +"' ");
				udinventorySet.reset();
				Double curbaltotal = udinventorySet.sum("curbal");
					if(mbo.getDouble("udissueqty") > 0 && !String.valueOf(maxlimit).equalsIgnoreCase("") && maxlimit!=0){
						if((curbaltotal+udotwqty+mbo.getDouble("udissueqty"))<=maxlimit){
							curbaltotal=curbaltotal+mbo.getDouble("udissueqty");
						mbo.setValue("orderqty", (maxlimit-(curbaltotal+udotwqty))/conversion, 11L);
						}else if((curbaltotal+udotwqty+mbo.getDouble("udissueqty"))>maxlimit){
							Object params[] = { " Notice: please input <= "+(maxlimit-(curbaltotal+udotwqty))+" , confirm it will not more than the maxlimit "+maxlimit+" ! (If the input limit is minus, which means the maxlimit should be adjusted upper!) "};
							throw new MXApplicationException("instantmessaging", "tsdimexception",params);
						}
					}
				}
			}
		}
	}else if(udconversionSet.isEmpty() || udconversionSet.count() == 0){
				if(mbo.getDouble("udissueqty") >= 0){
					Double maxlimit = mbo.getDouble("UDITEMCP.maxlimit");//最大库存
					if(!String.valueOf(maxlimit).equalsIgnoreCase("") && maxlimit!=0){
					Double udotwqty = (mbo.getMboSet("UDMATPRLINEOTW").sum("orderqty") + mbo.getMboSet("UDMATPOLINEOTW").sum("orderqty"))*mbo.getDouble("conversion");
					MboSetRemote udinventorySet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
					udinventorySet.setWhere(" itemnum = '" + itemnum +"' ");
					udinventorySet.reset();
					Double curbaltotal = udinventorySet.sum("curbal");
						if(mbo.getDouble("udissueqty") > 0 && !String.valueOf(maxlimit).equalsIgnoreCase("") && maxlimit!=0){
							if((curbaltotal+udotwqty+mbo.getDouble("udissueqty"))<=maxlimit){
								curbaltotal=curbaltotal+mbo.getDouble("udissueqty");
							mbo.setValue("orderqty", (maxlimit-(curbaltotal+udotwqty))/mbo.getDouble("conversion"), 11L);
							}else if((curbaltotal+udotwqty+mbo.getDouble("udissueqty"))>maxlimit){
								Object params[] = { " Notice: please input <= "+(maxlimit-(curbaltotal+udotwqty))+" , confirm it will not more than the maxlimit "+maxlimit+" ! (If the input limit is minus, which means the maxlimit should be adjusted upper!) "};
								throw new MXApplicationException("instantmessaging", "tsdimexception",params);
							}
						}
					}
				}else if(mbo.getDouble("udissueqty") < 0){
					Object params[] = { " Issue quantity cannot be less than 0 !  "};
					throw new MXApplicationException("instantmessaging", "tsdimexception",params);
				}
			}
			if(mbo.getDouble("udissueqty") >= 0 && String.valueOf(mbo.getDouble("UDITEMCP.maxlimit")).equalsIgnoreCase("") || mbo.getDouble("UDITEMCP.maxlimit")==0){
				mbo.setValue("orderqty", mbo.getDouble("udissueqty")/mbo.getDouble("conversion"), 11L);
			}
		}
	}

}
