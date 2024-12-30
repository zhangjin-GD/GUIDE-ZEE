package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldProjectBidNum extends MAXTableDomain {

	public FldProjectBidNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDPROBID", "PROBIDNUM=:" + thisAttr);
		String[] FromStr = { "PROBIDNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria(
				"status ='APPR' and not exists (select 1 from udprocon where udprocon.probidnum=udprobid.probidnum)");
		return super.getList();
	}
}
