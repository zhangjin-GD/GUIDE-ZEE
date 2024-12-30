package guide.app.item;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldIConveritem  extends MAXTableDomain{
	public UDFldIConveritem(MboValue mbv){
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ITEM", "ITEMNUM=:" + thisAttr);
		String[] FromStr = { "ITEMNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
		
	}
	public MboSetRemote getList() throws MXException, RemoteException{
		
		MboSetRemote listSet = super.getList();
		MboRemote mbo = getMboValue().getMbo();
		listSet.setWhere(" itemnum in ((select itemnum from uditemcp where udcompany='ZEE'))");
		return listSet;

	}

}
