package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class ProPayLine extends Mbo implements MboRemote {

	public ProPayLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof ProPay) {
			String propaynum = parent.getString("propaynum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("propaynum", propaynum, 11L);
			this.setValue("linenum", linenum, 11L);
			this.setValue("taxcost", 0, 2L);
			this.setValue("linecost", 0, 2L);
			this.setValue("linetaxcost", 0, 2L);
		}
	}

	@Override
	public void undelete() throws MXException, RemoteException {
		super.undelete();
		
		updateCost();
	}

	@Override
	public void delete(long accessModifier) throws MXException, RemoteException {
		super.delete(accessModifier);

		updateCost();
	}

	private void updateCost() throws RemoteException, MXException {
		MboRemote parent = this.getOwner();
		if (parent != null && parent instanceof ProPay) {
			parent.getMboSet("UDPROPAYLINE").resetQbe();
			double totallinetaxcost = this.getThisMboSet().sum("linetaxcost");
			double totallinecost = this.getThisMboSet().sum("linecost");
			double totaltaxcost = this.getThisMboSet().sum("taxcost");

			parent.setValue("totallinetaxcost", totallinetaxcost, 2L);
			parent.setValue("totallinecost", totallinecost, 2L);
			parent.setValue("totaltaxcost", totaltaxcost, 2L);
		}
	}

}
