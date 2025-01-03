package guide.app.item;

import java.rmi.RemoteException;

import psdi.app.item.Item;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldUdItemcpVen extends Mbo implements MboRemote {

	public UDFldUdItemcpVen(MboSet ms) throws RemoteException {
		super(ms);
		// TODO Auto-generated constructor stub
	}

	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote owner = getOwner();
		if ((owner != null) && (owner instanceof Item)) {
			String itemnum = owner.getString("itemnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String personid = owner.getUserInfo().getPersonId();
			MboSetRemote personSet = owner.getMboSet("$PERSON", "PERSON");
			personSet.setWhere("personid ='" + personid + "'");
			personSet.reset();
			if (personSet != null && !personSet.isEmpty() && personSet.count() > 0){
				for (int i = 0; i < personSet.count(); i++){
				MboRemote person = personSet.getMbo(i);
				String udcompany = person.getString("udcompany");
				this.setValue("udcompany", udcompany, 11L);

				}
				this.setValue("itemnum",itemnum,11L);
				this.setValue("linenum", linenum, 11L);
			}
		}

	}
	//UDITEMCPVEN表ZEE选择ACTIVE的供应商后给UDITEMCP表的供应商赋值
	public void save() throws MXException, RemoteException{
		super.save();
		String udcompany = getString("udcompany");
		if(udcompany.equalsIgnoreCase("ZEE")){
		String itemnum = getString("itemnum");
		boolean udactive = getBoolean("udactive");
		MboSetRemote uditemcpSet = getMboSet( "UDITEMCP");	
		uditemcpSet.setWhere("itemnum ='" + itemnum + "' and udcompany = '"+udcompany+"'");
		uditemcpSet.reset();
		if (uditemcpSet != null && !uditemcpSet.isEmpty()){
			for(int i = 0; i < uditemcpSet.count(); i++){
			MboRemote uditemcp = uditemcpSet.getMbo(0);
			if(udactive){
				String vendor = getString("vendor");
				uditemcp.setValue("vendor",vendor,11L);
			}
			}
		}
		uditemcpSet.close();
		
		/**
		 * ZEE - 联动UDITEMCPVEN与UDCONVERSION表
		 * 72-141
		 * 2024-12-25  11:17  
		 */
		String zeevenconverStatus = MXServer.getMXServer().getProperty("guide.zeevenconver.enabled");
		if (zeevenconverStatus != null && zeevenconverStatus.equalsIgnoreCase("ACTIVE")) {
			MboSetRemote uditemcpvenSet = MXServer.getMXServer().getMboSet("UDITEMCPVEN", MXServer.getMXServer().getSystemUserInfo());
			uditemcpvenSet.setWhere(" udcompany = 'ZEE' and itemnum = '"+ itemnum + "' ");
			uditemcpvenSet.reset();
			if (uditemcpvenSet.isEmpty()) {
				String vendor = getString("vendor");
				if (!vendor.equalsIgnoreCase("")) {
					MboSetRemote udconversionSet = getUdconversionSet(itemnum,vendor);
					if (udconversionSet.isEmpty()) {
						MboRemote udconversion = udconversionSet.add();
						updateConversion(udconversion);
						udconversionSet.save();
					} else if (!udconversionSet.isEmpty()
							&& udconversionSet.count() > 0) {
						MboRemote udconversion = udconversionSet.getMbo(0);
						updateConversion(udconversion);
						udconversionSet.save();
					}
					udconversionSet.close();
				}
			}
			if (!uditemcpvenSet.isEmpty() && uditemcpvenSet.count() > 0) {
				if (toBeAdded()) {
					String vendor = getString("vendor");
					MboSetRemote udconversionSet = getUdconversionSet(itemnum,vendor);
					if (udconversionSet.isEmpty()) {
						MboRemote udconversion = udconversionSet.add();
						updateConversion(udconversion);
						udconversionSet.save();
					} else if (!udconversionSet.isEmpty()
							&& udconversionSet.count() > 0) {
						MboRemote udconversion = udconversionSet.getMbo(0);
						updateConversion(udconversion);
						udconversionSet.save();
					}
					udconversionSet.close();
				}
				if (isModified("itemnum") || isModified("vendor")
						|| isModified("frommeasureunit")
						|| isModified("tomeasureunit")
						|| isModified("conversion")
						|| isModified("roundfactor")) {
					String vendor = getString("vendor");
					MboSetRemote udconversionSet = getUdconversionSet(itemnum,vendor);
					if (!udconversionSet.isEmpty() && udconversionSet.count() > 0) {
						MboRemote udconversion = udconversionSet.getMbo(0);
						updateConversion(udconversion);
						udconversionSet.save();
					}
					udconversionSet.close();
				}

				if (toBeDeleted()) {
					String vendor = getString("vendor");
					MboSetRemote udconversionSet = getUdconversionSet(itemnum,vendor);
					if (!udconversionSet.isEmpty()&& udconversionSet.count() > 0) {
						udconversionSet.deleteAll();
						udconversionSet.save();
					}
					udconversionSet.close();
				}
			}
			uditemcpvenSet.close();
		}
	}
}
	
	//更新
	private void updateConversion(MboRemote conversionMbo) throws MXException, RemoteException {
	    conversionMbo.setValue("itemnum", getString("itemnum"), 2L);
	    conversionMbo.setValue("vendor",  getString("vendor"), 11L);
	    conversionMbo.setValue("udcompany", getString("udcompany"), 11L);
	    conversionMbo.setValue("frommeasureunit", getString("frommeasureunit"), 11L);
	    conversionMbo.setValue("tomeasureunit", getString("tomeasureunit"), 2L);
	    Double conversion =  getDouble("conversion");
	    if (conversion != null && conversion != 0) {
	        conversionMbo.setValue("conversion", conversion, 11L);
	    } else {
	        conversionMbo.setValue("conversion", 1.0, 11L);
	    }
	    Double roundfactor =  getDouble("roundfactor");
	    if (roundfactor != null && roundfactor != 0) {
	        conversionMbo.setValue("roundfactor", roundfactor, 11L);
	    } else {
	        conversionMbo.setValue("roundfactor", 1.0, 11L);
	    }
	    conversionMbo.setValue("udvendoritemnum",  getString("udvendoritemnum"), 11L);
	    conversionMbo.setValue("udvendoritemdesc",  getString("udvendoritemdesc"), 11L);
	}
	
	private void updateConversion(MboRemote conversionMbo,MboRemote uditemcpvenMbo) throws MXException, RemoteException {
	    conversionMbo.setValue("itemnum", uditemcpvenMbo.getString("itemnum"), 2L);
	    conversionMbo.setValue("vendor",  uditemcpvenMbo.getString("vendor"), 11L);
	    conversionMbo.setValue("udcompany",  uditemcpvenMbo.getString("udcompany"), 11L);
	    conversionMbo.setValue("frommeasureunit",  uditemcpvenMbo.getString("frommeasureunit"), 11L);
	    conversionMbo.setValue("tomeasureunit",  uditemcpvenMbo.getString("tomeasureunit"), 2L);
	    Double conversion =  uditemcpvenMbo.getDouble("conversion");
	    if (conversion != null && conversion != 0) {
	        conversionMbo.setValue("conversion", conversion, 11L);
	    } else {
	        conversionMbo.setValue("conversion", 1.0, 11L);
	    }
	    Double roundfactor =  uditemcpvenMbo.getDouble("roundfactor");
	    if (roundfactor != null && roundfactor != 0) {
	        conversionMbo.setValue("roundfactor", roundfactor, 11L);
	    } else {
	        conversionMbo.setValue("roundfactor", 1.0, 11L);
	    }
	    conversionMbo.setValue("udvendoritemnum",  uditemcpvenMbo.getString("udvendoritemnum"), 11L);
	    conversionMbo.setValue("udvendoritemdesc",  uditemcpvenMbo.getString("udvendoritemdesc"), 11L);
	}
	
	private MboSetRemote getUdconversionSet(String itemnum, String uditemcpvendor) throws RemoteException, MXException {
	    MboSetRemote udconversionSet = MXServer.getMXServer().getMboSet("UDCONVERSION", MXServer.getMXServer().getSystemUserInfo());
	    udconversionSet.setWhere("udcompany = 'ZEE' and itemnum = '" + itemnum + "' and vendor = '" + uditemcpvendor + "'");
	    udconversionSet.reset();
	    return udconversionSet;
	}
}
