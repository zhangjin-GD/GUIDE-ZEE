package guide.app.project;

import java.rmi.RemoteException;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class ProPay extends UDMbo implements MboRemote {

	public ProPay(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();

		try {
			// READONLY 只读 REQUIRED 必填
			String[] proconName = { "PROCONNUM", "PROCONLINENUM" };

			// 存在行信息时只读，反之不只读
			MboSetRemote proPayLineSet = this.getMboSet("UDPROPAYLINE");
			if (!proPayLineSet.isEmpty()) {
				this.setFieldFlag(proconName, READONLY, true);
			} else {
				this.setFieldFlag(proconName, READONLY, false);
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		this.setValue("linecost", 0.0, 11L);
		this.setValue("totallinecost", 0.0, 11L);
		this.setValue("totaltaxcost", 0.0, 11L);
		this.setValue("totallinetaxcost", 0.0, 2L);
		this.setValue("currencycode", CommonUtil.getValue(this, "UDCOMPANY", "currency"), 2L);

	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
	}
}
