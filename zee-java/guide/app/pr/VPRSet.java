package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSetRemote;
import psdi.util.MXException;

public class VPRSet extends NonPersistentCustomMboSet implements NonPersistentCustomMboSetRemote {

	public VPRSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {

		return new VPR(ms);
	}

	@Override
	public MboRemote setup() throws MXException, RemoteException {
		MboRemote owner = this.getOwner();
		MboRemote mbo = null;
		if (owner != null && owner instanceof UDPR) {
			String prnum = owner.getString("prnum");
			mbo = this.addAtEnd();
			mbo.setValue("prnum", prnum, 11L);
			mbo.setValueNull("udpurchaser");
		} else if (owner != null && owner instanceof UDPRLine) {
			int prlineid = owner.getInt("prlineid");
			mbo = this.addAtEnd();
			mbo.setValue("prlineid", prlineid, 11L);
			mbo.setValueNull("udpurchaser");
		}
		return mbo;
	}

//	@Override
//	public void execute() throws MXException, RemoteException {
//		super.execute();
//		MboRemote owner = this.getOwner();// PR OR PRLINE
//		String purchaser = this.getString("udpurchaser");
//		if (purchaser != null && !purchaser.isEmpty()) {
//			if (owner != null && owner instanceof UDPR) {
//				MboSetRemote newPRLineSet = this.getMbo().getMboSet("PRLINE");
//				Vector<MboRemote> vector = newPRLineSet.getSelection();
//				for (int i = 0; i < vector.size(); i++) {
//					MboRemote mr = (MboRemote) vector.elementAt(i);
//					int vprlineid = mr.getInt("prlineid");
//					MboSetRemote prlineSet = owner.getMboSet("PRLINE");
//					if (prlineSet != null && !prlineSet.isEmpty()) {
//						for (int j = 0; prlineSet.getMbo(j) != null; j++) {
//							MboRemote prline = prlineSet.getMbo(j);
//							int prlineid = prline.getInt("prlineid");
//							if (vprlineid == prlineid) {
//								prline.setValue("udpurchaser", purchaser, 11L);
//							}
//						}
//					}
//				}
//			} else if (owner != null && owner instanceof UDPRLine) {
//				MboSetRemote newPRLineSet = this.getMbo().getMboSet("PRNOTRFQ");
//				Vector<MboRemote> vector = newPRLineSet.getSelection();
//				for (int i = 0; i < vector.size(); i++) {
//					MboRemote mr = (MboRemote) vector.elementAt(i);
//					int vprlineid = mr.getInt("prlineid");
//					MboSetRemote prlineSet = owner.getMboSet("UDPRNOTRFQ");
//					if (prlineSet != null && !prlineSet.isEmpty()) {
//						for (int j = 0; prlineSet.getMbo(j) != null; j++) {
//							MboRemote prline = prlineSet.getMbo(j);
//							int prlineid = prline.getInt("prlineid");
//							if (vprlineid == prlineid) {
//								prline.setValue("udpurchaser", purchaser, 11L);
//							}
//						}
//					}
//				}
//			}
//		}
//	}
}
