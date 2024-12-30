package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldAssetPOLineId extends MAXTableDomain {

	public FldAssetPOLineId(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("poline", "polineid=:" + thisAttr);
		String[] FromStr = { "polineid" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("exists(select 1 from po where po.ponum=poline.ponum and po.udcompany=:udcompany)"
				+ " and not exists(select 1 from asset where asset.udpolineid=poline.polineid and asset.status='ACTIVE')"
				+ " and poline.receiptscomplete=1");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = getMboValue().getMbo();
		MboSetRemote polineSet = mbo.getMboSet("UDPOLINE");
		if (!polineSet.isEmpty() && polineSet.count() > 0) {
			MboRemote poline = polineSet.getMbo(0);
			String vendor = poline.getString("po.vendor");
			double unitcost = poline.getDouble("unitcost");
			String itemnum = poline.getString("itemnum");
			mbo.setValue("vendor", vendor, 11L);
			mbo.setValue("purchaseprice", unitcost, 11L);
			mbo.setValue("uditemnum", itemnum, 11L);
		}
	}
}
