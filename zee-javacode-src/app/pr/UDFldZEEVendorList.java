package guide.app.pr;

import guide.app.pr.UDPR;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldZEEVendorList  extends MAXTableDomain{

	public UDFldZEEVendorList(MboValue mbv) {
		super(mbv);
		// TODO Auto-generated constructor stub
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDZEEVENDORLIST", "VENDOR=:" + thisAttr);
		String[] FromStr = { "VENDOR" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		MboRemote mbo = getMboValue().getMbo();
		String itemnum = mbo.getString("itemnum");
		MboRemote owner = mbo.getOwner();
		String appName = owner.getThisMboSet().getApp();
		String thisAttr = getMboValue().getAttributeName();
		if (appName.equalsIgnoreCase("UDPRZEE")) {
			listSet.setWhere(" itemnum = '"+itemnum+"' ");
		}
		return listSet;
	}
	
	public void action() throws MXException, RemoteException{
		super.action();
		/**
		 * ZEE - 有效合同供应商代入价格
		 */
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String apptype = owner.getString("udapptype");
		if (owner != null && owner instanceof UDPR) {
			String udprevendor = mbo.getString("udprevendor");
			String itemnum = mbo.getString("itemnum");
			if (!udprevendor.equalsIgnoreCase("") && "PRZEE".equalsIgnoreCase(apptype)) {
				mbo.setValue("udprevendor", udprevendor, 11L);
				MboSetRemote udcontractlineSet = MXServer.getMXServer().getMboSet("UDCONTRACTLINE",MXServer.getMXServer().getSystemUserInfo());
				udcontractlineSet.setWhere(" linetype = 'ITEM' and itemnum = '"
								+ itemnum
								+ "' and gconnum in (select gconnum from udcontract where vendor = '"
								+ udprevendor
								+ "' and udcompany = 'ZEE' and status='APPR' and to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd') "
								+ ") order by uddiscountprice asc");
				udcontractlineSet.reset();
				if (!udcontractlineSet.isEmpty()&& udcontractlineSet.count() > 0) {
					MboRemote udcontractline = udcontractlineSet.getMbo(0);
					Double uddiscountprice = udcontractline.getDouble("uddiscountprice");
					mbo.setValue("unitcost", uddiscountprice, 2L);
				}
			}
			
				String frommeasureunit = "";
				Double conversion = 1.0;	
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
							Double maxlimit = mbo.getDouble("UDITEMCP.maxlimit");
							if(!String.valueOf(maxlimit).equalsIgnoreCase("") && maxlimit!=0){
							MboSetRemote udinventorySet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
							udinventorySet.setWhere(" itemnum = '" + itemnum +"' ");
							udinventorySet.reset();
							Double curbaltotal = udinventorySet.sum("curbal");
							Double udotwqty = (mbo.getMboSet("UDMATPRLINEOTW").sum("orderqty") + mbo.getMboSet("UDMATPOLINEOTW").sum("orderqty"))*conversion;
							Double orderqty = (maxlimit-(curbaltotal+udotwqty))/conversion;
							Double resultup =  (Math.ceil(orderqty / roundfactor))*roundfactor;//最小订购数量
							if(resultup>0){
							mbo.setValue("orderqty", resultup,11L);	
							//1209
							mbo.setValue("udissueqty", resultup*conversion,11L);//最小发放数量
						}
							}else if(String.valueOf(maxlimit).equalsIgnoreCase("") || maxlimit==0){
								mbo.setValue("orderqty", "1",11L);
								mbo.setValue("udissueqty", 1*conversion,11L);
							}	
						}
						mbo.setValue("orderunit", frommeasureunit,2L);
						mbo.setValue("conversion", conversion,2L);
						mbo.setFieldFlag("conversion", 128L, false);//设置非必填
						mbo.setFieldFlag("conversion", 7L, true);//设置只读
						mbo.setFieldFlag("orderunit", 128L, false);//设置非必填
						mbo.setFieldFlag("orderunit", 7L, true);//设置只读
					}else if(frommeasureunit.equalsIgnoreCase("")){
						if(!itemSet.isEmpty() && itemSet.count() > 0){
						mbo.setValue("orderunit", itemSet.getMbo(0).getString("orderunit"),2L);
						mbo.setValue("conversion", "1",2L);
						mbo.setFieldFlag("conversion", 7L, false);//取消只读
						mbo.setFieldFlag("conversion", 128L, true);//设置必填
						mbo.setFieldFlag("orderunit", 7L, false);//取消只读
						mbo.setFieldFlag("orderunit", 128L, true);//设置必填
						}
					}
				}else if(udconversionSet.isEmpty() && udconversionSet.count() == 0){
						if(!itemSet.isEmpty() && itemSet.count() > 0){
							mbo.setValue("orderunit", itemSet.getMbo(0).getString("orderunit"),2L);
							mbo.setValue("conversion", "1",2L);
							mbo.setFieldFlag("conversion", 7L, false);//取消只读
							mbo.setFieldFlag("conversion", 128L, true);//设置必填
							mbo.setFieldFlag("orderunit", 7L, false);//取消只读
							mbo.setFieldFlag("orderunit", 128L, true);//设置必填
							}
					}
					udconversionSet.close();
					itemSet.close();
			}
	}
}
