package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldBinNum extends MAXTableDomain {

	public UDFldBinNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDBIN", "BINNUM=:" + thisAttr);
		String[] FromStr = { "BINNUM","BINNAME"};
		String[] ToStr = { thisAttr,"BINNAME"};
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String location = owner.getString("location");
		setListCriteria("location='"+location+"' and unactive=0");
		return super.getList();
	}


}
