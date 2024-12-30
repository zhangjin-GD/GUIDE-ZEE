package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

/**
 *@function:ZEE-设备台账选择位置
 *@date:2023-08-01 11:00:47
 *@modify:
 */
public class UDFldAssetudLocation extends MboValueAdapter {

	public UDFldAssetudLocation() {
		super();
	}

	public UDFldAssetudLocation(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String udlocation = mbo.getString("udlocation");
		mbo.setValue("location", udlocation, 11L);
	}
}
