package guide.app.contract;

import java.rmi.RemoteException;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class Contract extends UDMbo implements MboRemote {

	public Contract(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void initFieldFlagsOnMbo(String attrName) throws MXException {
		super.initFieldFlagsOnMbo(attrName);
		try {
			if (!this.toBeAdded()) {
				String personid = this.getUserInfo().getPersonId();
				String maxUserid = CommonUtil.getValue("GROUPUSER",
						"groupname = 'MAXADMIN' and userid='" + personid + "'", "USERID");
				if (maxUserid == null) {
					String status = getString("status");
					if ("APPR".equalsIgnoreCase(status) || "CAN".equalsIgnoreCase(status)) {
						attributeReadonly(true);
					} else {
						attributeReadonly(false);
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void attributeReadonly(boolean state) throws RemoteException, MXException {
		String[] attrMbo = { "description", "contype", "purchaseagent", "vendor", "startdate", "enddate" };
		this.setFieldFlag(attrMbo, 7L, state);
		MboSetRemote invuseLineSet = this.getMboSet("UDCONTRACTLINE");
		invuseLineSet.setFlag(7L, state);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String personId = this.getUserInfo().getPersonId();
		this.setValue("purchaseagent", personId, 2L);
		this.setValue("totalcost", 0, 11L);
		this.setValue("pretaxtotal", 0, 11L);
	}

	public void setStatusWappr() {

	}
}
