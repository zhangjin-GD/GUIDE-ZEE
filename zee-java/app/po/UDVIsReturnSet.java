package guide.app.po;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.NonPersistentMboSet;
import psdi.util.MXException;

public class UDVIsReturnSet extends NonPersistentMboSet implements UDVIsReturnSetRemote {

	public UDVIsReturnSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {

		return new UDVIsReturn(ms);
	}

	public MboRemote setup() throws MXException, RemoteException {
		MboRemote owner = this.getOwner();
		MboRemote mbo = null;
		if (owner != null && owner instanceof UDPO) {
			Set<String> recnumset = new HashSet<String>();
			Set<String> removeSet = new HashSet<String>();
			MboSetRemote matRecTransSet = owner.getMboSet("RECEIPTTYPEMATREC");
			matRecTransSet.setOrderBy("udrecnum");
			matRecTransSet.reset();
			if (matRecTransSet != null && !matRecTransSet.isEmpty()) {
				for (int i = 0; matRecTransSet.getMbo(i) != null; i++) {
					MboRemote matRecTrans = matRecTransSet.getMbo(i);
					String recnum = matRecTrans.getString("udrecnum");
					if (!matRecTrans.isNull("udzitemno")) {
						recnumset.add(recnum);
					}
					MboSetRemote returnReceiptSet = matRecTrans.getMboSet("RETURNRECEIPTS");
					if (returnReceiptSet != null && !returnReceiptSet.isEmpty()) {
						removeSet.add(recnum);
					}

				}
			}
			for (String remove : removeSet) {
				recnumset.remove(remove);// 去掉有退回的入库单号
			}
			for (String recnum : recnumset) {
				mbo = this.addAtEnd();
				mbo.setValue("recnum", recnum, 11L);
			}

		}
		return mbo;
	}

	public void execute() throws MXException, RemoteException {
		MboRemote owningPO = this.getOwner();
		if (owningPO != null && owningPO instanceof UDPO) {
			this.generateReturnReceipts(owningPO.getMboSet("PARENTMATRECTRANS"));
		}
	}

	private void generateReturnReceipts(MboSetRemote targetMboSet) throws RemoteException, MXException {
		Enumeration<MboRemote> e = this.getSelection().elements();
		while (e.hasMoreElements()) {
			UDVIsReturnRemote receiptInput = (UDVIsReturnRemote) e.nextElement();
			receiptInput.createReturnReceipt(targetMboSet);
		}
	}
}
