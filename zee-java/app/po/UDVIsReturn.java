package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.NonPersistentMbo;
import psdi.util.MXException;

public class UDVIsReturn extends NonPersistentMbo implements UDVIsReturnRemote {

	public UDVIsReturn(MboSet ms) throws RemoteException {
		super(ms);
	}

	public MboRemote createReturnReceipt(MboSetRemote targetSet) throws MXException, RemoteException {
		MboRemote newReceipt = null;
		MboSetRemote matRecTransSet = this.getMboSet("MATRECTRANS");
		if (matRecTransSet != null && !matRecTransSet.isEmpty()) {
			for (int i = 0; matRecTransSet.getMbo(i) != null; i++) {
				MboRemote matRecTrans = matRecTransSet.getMbo(i);

				newReceipt = targetSet.addAtEnd(2L);
				newReceipt.setValue("issuetype", "!RETURN!", 11L);
				newReceipt.setValue("receiptref", matRecTrans.getInt("matrectransid"), 11L);
				newReceipt.setValue("ponum", matRecTrans.getString("ponum"), 2L);
				newReceipt.setValue("polinenum", matRecTrans.getInt("polinenum"), 2L);
				newReceipt.setValue("exchangerate", matRecTrans.getDouble("exchangerate"), 2L);

				newReceipt.setValue("receiptquantity", matRecTrans.getDouble("quantity") * -1.0D, 2L);

				newReceipt.setValue("asn", matRecTrans.getString("asn"), 2L);
				newReceipt.setValue("invoicenum", matRecTrans.getString("invoicenum"), 2L);

				if (!matRecTrans.isNull("remark")) {
					newReceipt.setValue("remark", matRecTrans.getString("remark"), 2L);
				}

				newReceipt.setValue("tostoreloc", matRecTrans.getString("tostoreloc"), 11L);
				newReceipt.setValue("tobin", matRecTrans.getString("tobin"), 11L);
				newReceipt.setValue("tolot", matRecTrans.getString("tolot"), 11L);
				newReceipt.setValue("conditioncode", matRecTrans.getString("conditioncode"), 11L);

				newReceipt.setValue("gldebitacct", matRecTrans.getString("gldebitacct"), 11L);
				newReceipt.setValue("status", "!COMP!", 2L);
				newReceipt.setValue("enterby", this.getUserInfo().getPersonId(), 11L);
				newReceipt.setValue("udztype", true, 2L);

			}
		}
		return newReceipt;
	}
}
