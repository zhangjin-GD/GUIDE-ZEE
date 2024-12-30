package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldMatrectransUDTobin extends MAXTableDomain {

	public FldMatrectransUDTobin(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
		MboRemote mbo = getMboValue().getMbo();
		MboSetRemote binSet = mbo.getMboSet("$UDBIN", "UDBIN", "location = :tostoreloc and unactive=0");
		if (!binSet.isEmpty() && binSet.count() > 0) {
			String thisAttr = getMboValue().getAttributeName();
			setRelationship("UDBIN", "binnum =:" + thisAttr);
			String[] FromStr = { "binnum" };
			String[] ToStr = { thisAttr };
			this.setLookupKeyMapInOrder(ToStr, FromStr);
			this.setListCriteria("location = :tostoreloc and unactive=0");
		} else {
			String thisAttr = getMboValue().getAttributeName();
			setRelationship("INVBALANCES", "binnum =:" + thisAttr);
			String[] FromStr = { "binnum" };
			String[] ToStr = { thisAttr };
			this.setLookupKeyMapInOrder(ToStr, FromStr);
			this.setListCriteria("itemnum = :itemnum and location = :tostoreloc and siteid = :siteid");
		}
	}

	@Override
	public void init() throws MXException, RemoteException {
		super.init();
		MboRemote mbo = getMboValue().getMbo();
		if (!mbo.isNull("tostoreloc")) {
			mbo.setFieldFlag("udtobin", 128L, true);
		} else {
			mbo.setFieldFlag("udtobin", 128L, false);
		}
	}

	@Override
	public void validate() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			if (udcompany.equalsIgnoreCase("GR02PCT")) {//
				super.validate();
			}
		}
	}

	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String issuetype = mbo.getString("ISSUETYPE");
		// 接收时
		if ("RECEIPT".equalsIgnoreCase(issuetype)) {
			mbo.setValue("tobin", mbo.getString("udtobin"), 11L);
		}
	}
}