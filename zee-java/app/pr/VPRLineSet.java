package guide.app.pr;

import java.rmi.RemoteException;
import java.util.Vector;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.custapp.NonPersistentCustomMboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSetRemote;
import psdi.util.MXException;

public class VPRLineSet extends NonPersistentCustomMboSet implements NonPersistentCustomMboSetRemote {

	public VPRLineSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {

		return new VPRLine(ms);
	}

//	@Override
//	public MboRemote setup() throws MXException, RemoteException {
//		MboRemote vpr = this.getOwner();
//		MboRemote mbo = null;
//		if (vpr != null && vpr instanceof VPR) {
//			MboRemote owner = vpr.getOwner();
//			if (owner != null && owner instanceof UDPR) {
//				MboSetRemote prlineSet = owner.getMboSet("PRLINE");
//				if (prlineSet != null && !prlineSet.isEmpty()) {
//					for (int i = 0; prlineSet.getMbo(i) != null; i++) {
//						MboRemote prline = prlineSet.getMbo(i);
//						int prlineid = prline.getInt("prlineid");
//						int prlinenum = prline.getInt("prlinenum");
//						String prnum = prline.getString("prnum");
//						String itemnum = prline.getString("itemnum");
//						String description = prline.getString("description");
//						String modelnum = prline.getString("item.udmodelnum");
//						double orderqty = prline.getDouble("orderqty");
//						String orderunit = prline.getString("orderunit");
//						String requestedby = prline.getString("requestedby");
//						String udpurchaser = prline.getString("udpurchaser");
//						mbo = this.addAtEnd();
//						mbo.setValue("prlineid", prlineid, 11L);
//						mbo.setValue("prnum", prnum, 11L);
//						mbo.setValue("prlinenum", prlinenum, 11L);
//						mbo.setValue("itemnum", itemnum, 11L);
//						mbo.setValue("description", description, 11L);
//						mbo.setValue("modelnum", modelnum, 11L);
//						mbo.setValue("orderqty", orderqty, 11L);
//						mbo.setValue("orderunit", orderunit, 11L);
//						mbo.setValue("requestedby", requestedby, 11L);
//						mbo.setValue("udpurchaser", udpurchaser, 11L);
//					}
//				}
//			} else if (owner != null && owner instanceof UDPRLine) {
//				MboSetRemote prlineSet = owner.getMboSet("UDPRNOTRFQ");
//				if (prlineSet != null && !prlineSet.isEmpty()) {
//					for (int i = 0; prlineSet.getMbo(i) != null; i++) {
//						MboRemote prline = prlineSet.getMbo(i);
//						int prlineid = prline.getInt("prlineid");
//						int prlinenum = prline.getInt("prlinenum");
//						String prnum = prline.getString("prnum");
//						String itemnum = prline.getString("itemnum");
//						String description = prline.getString("description");
//						String modelnum = prline.getString("item.udmodelnum");
//						double orderqty = prline.getDouble("orderqty");
//						String orderunit = prline.getString("orderunit");
//						String requestedby = prline.getString("requestedby");
//						String udpurchaser = prline.getString("udpurchaser");
//						mbo = this.addAtEnd();
//						mbo.setValue("prlineid", prlineid, 11L);
//						mbo.setValue("prnum", prnum, 11L);
//						mbo.setValue("prlinenum", prlinenum, 11L);
//						mbo.setValue("itemnum", itemnum, 11L);
//						mbo.setValue("description", description, 11L);
//						mbo.setValue("modelnum", modelnum, 11L);
//						mbo.setValue("orderqty", orderqty, 11L);
//						mbo.setValue("orderunit", orderunit, 11L);
//						mbo.setValue("requestedby", requestedby, 11L);
//						mbo.setValue("udpurchaser", udpurchaser, 11L);
//					}
//				}
//			}
//		}
//		return mbo;
//	}

//	@Override
//	public void execute() throws MXException, RemoteException {
//		super.execute();
//
//		Vector<MboRemote> vector = this.getSelection();
//		MboRemote vpr = this.getOwner();
//		if (vpr != null && vpr instanceof VPR) {
//			String udpurchaser = vpr.getString("udpurchaser");
//			MboRemote pr = vpr.getOwner();
//			if (pr != null && pr instanceof UDPR) {
//				for (int i = 0; i < vector.size(); i++) {
//					MboRemote mr = (MboRemote) vector.elementAt(i);
//					int vprlineid = mr.getInt("prlineid");
//					MboSetRemote prlineSet = pr.getMboSet("PRLINE");
//					if (prlineSet != null && !prlineSet.isEmpty()) {
//						for (int j = 0; prlineSet.getMbo(j) != null; j++) {
//							MboRemote prline = prlineSet.getMbo(j);
//							int prlineid = prline.getInt("prlineid");
//							if (vprlineid == prlineid) {
//								prline.setValue("udpurchaser", udpurchaser, 11L);
//							}
//						}
//					}
//				}
//			}
//		}
//	}
}
