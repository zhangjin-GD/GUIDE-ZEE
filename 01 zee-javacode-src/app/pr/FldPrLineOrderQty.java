package guide.app.pr;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurOrderQty;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldPrLineOrderQty extends FldPurOrderQty {

	public FldPrLineOrderQty(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		Mbo mbo = this.getMboValue().getMbo();
		double unitcost = mbo.getDouble("unitcost");// 不含税单价
		double orderqty = mbo.getDouble("orderqty");
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}

		double percentTaxRate = taxrate / 100;// 税率

		double totalprice = unitcost * (1 + percentTaxRate);// 含税单价

		double totalcost = totalprice * orderqty; // 含税总价

		double linecost = unitcost * orderqty;// 不含税总价

		double tax1 = totalcost - linecost;// 税额

		mbo.setValue("udtotalprice", totalprice, 11L);
		mbo.setValue("udtotalcost", totalcost, 11L);
		mbo.setValue("tax1", tax1, 11L);
		
//		/**
//		 * @function:PR时的库存余量、在途数量的限制
//		 * @date: DJY 2024-03-26 10:04:24
//		 * @modify:与2024-12-3的TOM-round系数冲突,因此注释
//		 */
//		MboRemote owner = mbo.getOwner();
//		if (owner != null) {
//		String appName = owner.getThisMboSet().getApp();
//		if ("UDPRZEE".equalsIgnoreCase(appName)){
//		String itemnum = mbo.getString("itemnum");
//		double curbaltotal = 0;
//		double udotwqty = mbo.getDouble("udotwqty");
//		double maxlimit = 0;
//		double limitqty = 0;
//		MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
//		uditemcpSet.setWhere("itemnum = '" + itemnum +"' ");
//		uditemcpSet.reset();
//		if(!uditemcpSet.isEmpty() && uditemcpSet.count() > 0){
//			MboRemote uditemcp = uditemcpSet.getMbo(0);
//			maxlimit =  uditemcp.getDouble("maxlimit");
//		}
//		MboSetRemote inventorySet = MXServer.getMXServer().getMboSet("INVENTORY", MXServer.getMXServer().getSystemUserInfo());
//		inventorySet.setWhere("itemnum = '" + itemnum +"' ");
//		inventorySet.reset();
//		if(!inventorySet.isEmpty() && inventorySet.count() > 0){
//			MboRemote inventory = inventorySet.getMbo(0);
//			curbaltotal =  inventory.getDouble("curbaltotal");
//		}
//		if(maxlimit != 0){
//		limitqty = maxlimit - curbaltotal - udotwqty - orderqty;
//		if (limitqty < 0 && (maxlimit - curbaltotal - udotwqty-0) >= 0) {
//			limitqty = maxlimit - curbaltotal - udotwqty;
//			Object params[] = { "Notice: The PR quantity should be less than or equals to "+limitqty+"! "};
//			throw new MXApplicationException("instantmessaging", "tsdimexception", params);
//		}
//		if (limitqty < 0 && (maxlimit - curbaltotal - udotwqty-0) < 0) {
//			if((maxlimit - curbaltotal) > 0 && udotwqty == 0){
//			limitqty = maxlimit - curbaltotal;
//			}
//			if((maxlimit - udotwqty) > 0 && curbaltotal == 0){
//			limitqty = maxlimit - udotwqty;
//			}
//			if((maxlimit - curbaltotal) > 0 && udotwqty != 0){
//			limitqty = 0;
//			}
//			if((maxlimit - udotwqty) > 0 && curbaltotal != 0){
//			limitqty = 0;
//			}
//			if((maxlimit - udotwqty) < 0 ){
//			limitqty = 0;
//			}
//			if((maxlimit - curbaltotal) < 0 ){
//			limitqty = 0;
//			}
//			if(limitqty == 0){
//				mbo.setValue("orderqty", limitqty,11L);
//			}
//			Object params[] = { "Notice: The PR quantity should be less than or equals to "+limitqty+"! "};
//			throw new MXApplicationException("instantmessaging", "tsdimexception", params);
//		}
//		}		
//	}
//	}
		
		//ZEE - 完善系统中采购的转换系数、最小订购数量114-146
//		String frommeasureunit = "";
//		Double conversion = 0.0;	
//		String itemnum = mbo.getString("itemnum");
//		String udprevendor = mbo.getString("udprevendor");
//		MboSetRemote itemSet = MXServer.getMXServer().getMboSet("ITEM", MXServer.getMXServer().getSystemUserInfo());
//		itemSet.setWhere(" itemnum = '" + itemnum +"' ");
//		itemSet.reset();
//		MboSetRemote udconversionSet = MXServer.getMXServer().getMboSet("UDCONVERSION", MXServer.getMXServer().getSystemUserInfo());
//		udconversionSet.setWhere(" itemnum = '" + itemnum +"' and vendor = '" + udprevendor +"' ");
//		udconversionSet.reset();
//		if(!udconversionSet.isEmpty() && udconversionSet.count() > 0){
//			frommeasureunit = udconversionSet.getMbo(0).getString("frommeasureunit");
//			conversion = udconversionSet.getMbo(0).getDouble("conversion");
//			Double roundfactor = udconversionSet.getMbo(0).getDouble("roundfactor");
//			if( frommeasureunit!=null && !frommeasureunit.equalsIgnoreCase("")){
//				if(!roundfactor.equals("") && roundfactor != 0){
//					Double maxlimit = mbo.getDouble("UDITEMCP.maxlimit");
//					Double curbaltotal = mbo.getDouble("INVENTORY.curbaltotal");
//					Double udotwqty = mbo.getDouble("udotwqty")*conversion;//在途
//					Double orderqty1 = (maxlimit-(curbaltotal+udotwqty))/conversion;
//					Double resultup =  (Math.ceil(orderqty1 / roundfactor))*roundfactor;//最小订购数量
//					if(mbo.getDouble("orderqty")<resultup){
//						Object params[] = { "Order quantity should be more than the minimum order quantity "+resultup+" ! " };
//						throw new MXApplicationException("instantmessaging", "tsdimexception",params);
//					}else if(mbo.getDouble("orderqty")>= resultup){
//						mbo.setValue("udissueqty", mbo.getDouble("orderqty")*conversion,11L);//最小发放数量
//					}
//				}
//			}
//		}else if(udconversionSet.isEmpty() || udconversionSet.count() == 0){
//			mbo.setValue("udissueqty", mbo.getDouble("orderqty")*mbo.getDouble("conversion"),11L);
//		}
	}
}
