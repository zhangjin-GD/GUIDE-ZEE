package guide.webclient.beans.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class InvUseLineAssetTableBean extends DataBean{

	@Override
	protected MboSetRemote getMboSetRemote() throws MXException, RemoteException {
		MboSetRemote mboSet = super.getMboSetRemote();
		MboRemote mbo = this.app.getAppBean().getMbo();
		String sql = "1=2";
		String movementType = mbo.getString("udmovementtype");
		String company = mbo.getString("udcompany");
		if (movementType != null && movementType.equalsIgnoreCase("204")) {
			sql = "udcompany='"+company+"' and udcostcenter is not null and udassettypecode='FAC' and udassettypecode1='BLDG'";
		} else if (movementType != null && (movementType.equalsIgnoreCase("205") || movementType.equalsIgnoreCase("207"))) {
			sql = "udcompany='"+company+"' and udcostcenter is not null and udassettypecode!='FAC'";
		} else if (movementType != null && movementType.equalsIgnoreCase("206")) {
			sql = "udcompany='"+company+"' and udcostcenter is not null and udassettypecode='FAC' and udassettypecode1!='BLDG' and udassettypecode1!='CNTN'";
		} else if (movementType != null && movementType.equalsIgnoreCase("221")) {
			sql = "udcompany='"+company+"' and udcostcenter is not null and udassettypecode='FAC' and udassettypecode1='CNTN'";
		}
		mboSet.setAppWhere(sql);
		mboSet.reset();
		return mboSet;
	}
}
