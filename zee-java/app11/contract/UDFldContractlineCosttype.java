package guide.app.contract;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldContractlineCosttype extends MAXTableDomain{
	public UDFldContractlineCosttype(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDCOSTTYPE", "UDKOSTENSOORT=:" + thisAttr);
		String[] FromStr = { "UDKOSTENSOORT" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		String linetype = mbo.getString("linetype");
		if (linetype.equalsIgnoreCase("SERVICE")) {
			setListCriteria(" udkostensoort like '4%' ");
		}
		else
		{
			setListCriteria(" udkostensoort not like '4%' ");
		}
		return super.getList();
	}
}
