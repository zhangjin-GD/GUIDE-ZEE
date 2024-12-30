package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldServItem extends MAXTableDomain{

	public UDFldServItem(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("SERVICEITEMS", "itemnum =:" + thisAttr);
		String[] FromStr = { "itemnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	public MboSetRemote getList() throws MXException, RemoteException {
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDPR && owner.getString("udcompany").equalsIgnoreCase("ZEE")) {
			setListCriteria(" 1 = 1");
		}
		return super.getList();
	}
	
	public void action() throws MXException, RemoteException{
		/**
		 * 	ZEE-如果标准服务，在ud字段选择后自动代入标准字段
		 * 2024-04-15 10:39:13
		 */
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String udcompany = owner.getString("udcompany");
		String linetype = mbo.getString("linetype");
		String udservitem = mbo.getString("udservitem");
		if("ZEE".equalsIgnoreCase(udcompany) && linetype.equalsIgnoreCase("STDSERVICE") && !udservitem.equalsIgnoreCase("")){
			mbo.setValue("itemnum", udservitem, 2L);
		}
	}
}
