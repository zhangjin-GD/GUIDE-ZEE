package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.FldMatRecTransToBin;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFilterBinlocation extends MAXTableDomain{

	public UDFilterBinlocation(MboValue mbv) throws MXException {
		super(mbv);
		// TODO Auto-generated constructor stub
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDBIN", "BINNUM=:" + thisAttr);
		String[] FromStr = { "BINNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	/**
	 * djy
	 * ZEE-选择udstoreroom下的货位udbinlocation
	 * 2024-05-09 13:35:47
	 */

	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = owner.getString("udcompany");	
		String udstoreroom = mbo.getString("udstoreroom");	
		if(udcompany.equalsIgnoreCase("ZEE") && !udstoreroom.equalsIgnoreCase("")){
		 setListCriteria(" location = '"+ udstoreroom + "' ");
		}
		 return super.getList();
	}
	/**
	 * djy
	 * ZEE-通过客户化字段udbinlocation选择的库房赋值到tobin
	 * 2024-05-09 13:35:47
	 */
	public void action() throws MXException, RemoteException{
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = owner.getString("udcompany");
		String udbinlocation = mbo.getString("udbinlocation");
		if(udcompany.equalsIgnoreCase("ZEE") && !udbinlocation.equalsIgnoreCase("")){
			mbo.setValue("tobin", udbinlocation, 11L);
		}
	}
}
