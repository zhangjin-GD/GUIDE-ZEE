package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 *@function:
 *@author:zj
 *@date:下午4:34:28
 *@modify:
 */
public class UDFldInvoiceCostType extends MAXTableDomain {

	public UDFldInvoiceCostType(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ALNDOMAIN", "VALUE =:" + thisAttr);
		String[] FromStr = { "VALUE" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}  
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String ponum = mbo.getString("ponum");
		String costcenter = "";
		if (ponum!=null && !ponum.equalsIgnoreCase("")) {
			String purchaser = mbo.getString("INVC_PO.purchaseagent");
			costcenter = getCostcenter(purchaser);
		} else {
			String enterby = mbo.getString("enterby");
			costcenter = getCostcenter(enterby);
		}
		mbo.setValue("udcostcenter", costcenter, 11L);
	}
	
	private String getCostcenter(String personid) throws MXException,RemoteException {
		String costcenter = "";
		MboSetRemote personSet = MXServer.getMXServer().getMboSet("PERSON", MXServer.getMXServer().getSystemUserInfo());
		personSet.setWhere(" personid='"+personid+"' ");
		personSet.reset();
		if (!personSet.isEmpty() && personSet.count() > 0) {
			MboRemote person = personSet.getMbo(0);
			costcenter = person.getString("UDDEPT.costcenter");
		}
		personSet.close();
		return costcenter;
	}
}
