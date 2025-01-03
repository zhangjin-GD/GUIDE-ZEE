package guide.app.item;

import java.rmi.RemoteException;

import psdi.app.item.Item;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldItemcpvenVendor  extends MAXTableDomain{

	public UDFldItemcpvenVendor(MboValue mbv) {
		super(mbv);
		// TODO Auto-generated constructor stub
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("COMPANIES", "COMPANY=:" + thisAttr);
		String[] FromStr = { "COMPANY" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		MboRemote mbo = getMboValue().getMbo();
		listSet.setWhere("exists (select 1 from udcomptaxcode where company=companies.company and disabled=0 and udcompany='ZEE')");
		listSet.reset();
		return listSet;
	}
	
	/**
	 * ZEE - 控制UDITEMCPVEN，不允许同一个物资有重复的供应商
	 * 47-73
	 * 2024-12-25  11:17  
	 */
	public void action() throws MXException, RemoteException{
		super.action();
		String zeevenconverStatus = MXServer.getMXServer().getProperty("guide.zeevenconver.enabled");
		if (zeevenconverStatus != null && zeevenconverStatus.equalsIgnoreCase("ACTIVE")) {
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if ((owner != null) && (owner instanceof Item)) {
			String itemnum = owner.getString("itemnum");
			String vendor = mbo.getString("vendor");
			MboSetRemote uditemcpvenSet = MXServer.getMXServer().getMboSet("UDITEMCPVEN", MXServer.getMXServer().getSystemUserInfo());
			uditemcpvenSet.setWhere(" itemnum = '"+itemnum+"' and vendor = '"+vendor+"' ");
			uditemcpvenSet.reset();
			if(!uditemcpvenSet.isEmpty() && uditemcpvenSet.count() > 0){
				MboRemote uditemcpven = uditemcpvenSet.getMbo(0);
				if(vendor.equalsIgnoreCase(uditemcpven.getString("vendor"))){
					Object params[] = { "Do not allow the same item to have duplicate vendors! " };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params);
				}
			}
		}
	}
}
}
