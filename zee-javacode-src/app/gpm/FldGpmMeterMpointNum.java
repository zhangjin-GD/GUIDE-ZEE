package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldGpmMeterMpointNum extends MAXTableDomain {

	public FldGpmMeterMpointNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDMEASUREPOINT", "mpointnum =:" + thisAttr);
		String[] FromStr = { "mpointnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("assetnum=:assetnum");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote assetMeterSet = mbo.getMboSet("UDMEASUREPOINT");
		if (!assetMeterSet.isEmpty() && assetMeterSet.count() > 0) {
			MboRemote assetMeter = assetMeterSet.getMbo(0);
			String pmtype = assetMeter.getString("pmtype");
			mbo.setValue("pmtype", pmtype, 11L);
		}
	}
}
