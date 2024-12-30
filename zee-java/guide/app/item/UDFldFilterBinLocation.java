package guide.app.item;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 * ZEE - 物资台账公司子表筛选默认货位
 * @author DJY
 *2024-05-23 15:05:47
 */
public class UDFldFilterBinLocation extends MAXTableDomain{

	public UDFldFilterBinLocation(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDBIN", "BINNUM=:" + thisAttr);
		String[] FromStr = { "BINNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote list = super.getList();
		MboRemote mbo = getMboValue().getMbo();
		String udcompany = mbo.getString("udcompany");
		String storeloc = mbo.getString("storeloc");
		if(!udcompany.equalsIgnoreCase("") && udcompany.equalsIgnoreCase("ZEE")){
			if(!storeloc.isEmpty() && !storeloc.equalsIgnoreCase(""))	{
			    list.setWhere("location = '" + storeloc +"' ");
				list.reset();
			}
			else{
				list.setWhere(" location like 'ZEE%' ");
			}
		}
		return list;
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String udcompany = mbo.getString("udcompany");
		if(!udcompany.equalsIgnoreCase("") && udcompany.equalsIgnoreCase("ZEE")){
			String udbin = mbo.getString("udbin");
			String itemnum = mbo.getString("itemnum");
			MboSetRemote inventorySet = MXServer.getMXServer().getMboSet("INVENTORY", MXServer.getMXServer().getSystemUserInfo());
			inventorySet.setWhere(" itemnum = '" + itemnum + "' ");
			inventorySet.reset();
			if (!inventorySet.isEmpty() && inventorySet.count() > 0) {
				for (int i = 0; i < inventorySet.count(); i++) {
					MboRemote inventory = inventorySet.getMbo(i);
					inventory.setValue("binnum", udbin, 11L);
				}
			}
			inventorySet.save();
			inventorySet.close();
		}
	}
	
} 