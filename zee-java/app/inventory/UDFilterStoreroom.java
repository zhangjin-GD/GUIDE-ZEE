package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.po.virtual.ReceiptInputSetRemote;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFilterStoreroom extends MAXTableDomain{

	public UDFilterStoreroom(MboValue mbv) {
		super(mbv);
		// TODO Auto-generated constructor stub
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("LOCATIONS", "LOCATION=:" + thisAttr);
		String[] FromStr = { "LOCATION" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	/**
	 * djy
	 * ZEE-通过客户化字段udstoreroom选择自己码头的库房
	 * 2024-05-09 13:35:47
	 */

	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = owner.getString("udcompany");	
		if(udcompany.equalsIgnoreCase("ZEE")){
		 setListCriteria(" udcompany='ZEE' and type = 'STOREROOM'");
		 return super.getList();
		}
		else{
			setListCriteria(" 1=1 ");
			return super.getList();
		}
	}
	/**
	 * djy
	 * ZEE-通过客户化字段udstoreroom选择的库房赋值到tostoreloc
	 * 2024-05-09 13:35:47
	 */
	public void action() throws MXException, RemoteException{
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = owner.getString("udcompany");
		String udstoreroom = mbo.getString("udstoreroom");
		if(udcompany.equalsIgnoreCase("ZEE") && !udstoreroom.equalsIgnoreCase("")){
			mbo.setValue("tostoreloc", udstoreroom, 11L);
		}
	}
}
