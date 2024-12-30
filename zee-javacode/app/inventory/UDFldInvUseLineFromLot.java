package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.FldInvUseLineFromLot;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldInvUseLineFromLot extends FldInvUseLineFromLot {

	public UDFldInvUseLineFromLot(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void validate() throws MXException, RemoteException {
		super.validate();

	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		MboSetRemote mbo = super.getList();
		mbo.setQbe("curbal", ">0");
		return mbo;
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDInvUse && !this.getMboValue().isNull()) {
			String appType = owner.getString("udapptype");
			if ("TRANSFER".equalsIgnoreCase(appType)) {
				if (!mbo.isNull("fromlot")) {
					String fromlot = mbo.getString("fromlot");
					mbo.setValue("tolot", fromlot, 11L);
				}
			}
			// 工单领料,非工单领料
			if ("MATUSEWO".equalsIgnoreCase(appType) || "MATUSEOT".equalsIgnoreCase(appType)
					|| "MATUSECS".equalsIgnoreCase(appType)) {

				String itemnum1 = mbo.getString("itemnum");// 物料
				String fromstoreloc = mbo.getString("fromstoreloc");// 库房
				String fromlot = mbo.getString("fromlot");// 批次
				MboSetRemote invBalLotSet = mbo.getMboSet("UDINVBALFROMLOT");
				if (!invBalLotSet.isEmpty() && invBalLotSet.count() > 0) {
					for (int i = 0; invBalLotSet.getMbo(i) != null; i++) {
						MboRemote invBalLot = invBalLotSet.getMbo(i);
						String itemnum2 = invBalLot.getString("itemnum");
						String location = invBalLot.getString("location");
						String lotnum = invBalLot.getString("lotnum");
						int lotsort = invBalLot.getInt("udlotsort");
						if (itemnum1.equalsIgnoreCase(itemnum2) && fromstoreloc.equalsIgnoreCase(location)
								&& fromlot.equalsIgnoreCase(lotnum)) {
							if (lotsort == 1) {
								mbo.setValueNull("udlotwarn", 11L);
							} else {
								mbo.setValue("udlotwarn", "A", 11L);
							}
						}
					}
				}
			}
		}
	}
}
