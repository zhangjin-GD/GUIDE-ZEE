package guide.app.po.virtual;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import psdi.app.inventory.MatRecTrans;
import psdi.app.location.LocationRemote;
import psdi.app.mr.MRRemote;
import psdi.app.po.POLineRemote;
import psdi.app.po.POLineSetRemote;
import psdi.app.po.PORemote;
import psdi.app.po.ShipmentLineRemote;
import psdi.app.po.virtual.ReceiptInput;
import psdi.app.po.virtual.ReceiptInputRemote;
import psdi.app.po.virtual.ReceiptInputSet;
import psdi.app.po.virtual.ReceiptInputSetRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.MXMath;

public class UDReceiptInputSet extends ReceiptInputSet implements ReceiptInputSetRemote {
	
	public UDReceiptInputSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	/**
	 * ZEE - 根据拒收数量给应到数量赋值
	 * 2024-10-9 11:29
	 * 33-65
	 */
	@Override
	public MboRemote setup() throws MXException, RemoteException {
		MboRemote mbo = super.setup();
		String relationshipString = this.getRelationshipStringFromPO();
		MboRemote owningPO = this.getOwner();
		MboSetRemote poLineSetRemote = this.getPOLineSet(owningPO);
		poLineSetRemote.setOrderBy("polinenum");
		if (relationshipString.equals("MATRECEIPTINPUT") || relationshipString.equals("SERVRECEIPTINPUT")) {
			int k = 0;
			while (true) {
				mbo = this.getMbo(k);
				if (mbo == null) {
					break;
				}
				double qtyRequested = mbo.getDouble("QTYREQUESTED");
				double qtyUnaccepted = 0;
				MboSetRemote polineUnaccSet = mbo.getMboSet("POLINEUNACCEPTED");
				if (!polineUnaccSet.isEmpty() && polineUnaccSet.count() > 0) {
					for (int i = 0; i < polineUnaccSet.count(); i++) {
						MboRemote polineUnaccline = polineUnaccSet.getMbo(i);
						if (polineUnaccline.toBeDeleted()) {
							polineUnaccline.setValue("orderqty", "0", 11L);
						}
					}
					qtyUnaccepted = polineUnaccSet.sum("orderqty");
				}
				double qty = qtyRequested - qtyUnaccepted;
				mbo.setValue("qtyrequested", qty, 2L);
				k++;
			}
		}
		return mbo;
	}

	@Override
	public void execute() throws MXException, RemoteException {
		super.execute();
		MboRemote owningPO = this.getOwner();
		String relationshipString = this.getRelationshipStringFromPO();
		if (relationshipString.equals("UDFIXRECEIPTINPUT")) {
			generateFixReceipts(owningPO.getMboSet("UDFIXRECTRANS"));
		}
	}

	public void generateFixReceipts(MboSetRemote targetMbos) throws MXException, RemoteException {
		StringBuffer resultset = new StringBuffer();
		Vector<MboRemote> vector = this.getSelection();
		for (int i = 0; i < vector.size(); i++) {
			MboRemote mr = (MboRemote) vector.elementAt(i);
			double qtyrequested = mr.getDouble("qtyrequested");
			double orderqty = mr.getDouble("orderqty");
			int polinenum = mr.getInt("polinenum");
			if (qtyrequested > orderqty) {
				resultset.append(polinenum).append(",");
			}
		}
		if (resultset != null && resultset.length() > 0) {
			Object params[] = { resultset.toString() };
			throw new MXApplicationException("guide", "1067", params);
		}

		for (int i = 0; i < vector.size(); i++) {
			MboRemote mr = (MboRemote) vector.elementAt(i);
//			double qtyrequested = mr.getDouble("qtyrequested");
//			for (int j = 0; j < qtyrequested; j++) {
			createFixReceipt(mr, targetMbos);
//			}
		}
	}

	public MboRemote createFixReceipt(MboRemote mr, MboSetRemote targetSet) throws RemoteException, MXException {
		String personId = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		int polineid = mr.getInt("poline.polineid");
		String itemnum = mr.getString("itemnum");
		String itemDesc = mr.getString("description");
		String tax1code = mr.getString("poline.tax1code");
		double unitcost = mr.getDouble("poline.unitcost");
		double udtotalcost = mr.getDouble("poline.udtotalcost");
		double orderqty = mr.getDouble("orderqty");
		double qtyrequested = mr.getDouble("qtyrequested");

		String requestedby = mr.getString("poline.requestedby");
		String udbudgetnum = mr.getString("poline.udbudgetnum");
		String udprojectnum = mr.getString("poline.udprojectnum");
		String remark = mr.getString("remark");
		
		double totalunitcost = 0;
		if (orderqty != 0) {
			totalunitcost = udtotalcost / orderqty;
		}
		double tax1 = totalunitcost - unitcost;

		MboRemote newReceipt = targetSet.addAtEnd();
		newReceipt.setValue("itemnum", itemnum, 11L);
		newReceipt.setValue("description", itemDesc, 11L);
		newReceipt.setValue("createby", personId, 11L);
		newReceipt.setValue("createtime", currentDate, 11L);
		newReceipt.setValue("issuetype", "RECEIPT", 11L);
		newReceipt.setValue("quantity", qtyrequested, 11L);
		newReceipt.setValue("tax1code", tax1code, 11L);
		newReceipt.setValue("unitcost", unitcost, 11L);
		newReceipt.setValue("totalunitcost", totalunitcost, 11L);
		newReceipt.setValue("tax1", tax1, 11L);
		newReceipt.setValue("polineid", polineid, 11L);

		if (!mr.isNull("poline.prline.prlineid")) {
			int prlineid = mr.getInt("poline.prline.prlineid");
			newReceipt.setValue("prlineid", prlineid, 11L);
		}
		if (!mr.isNull("poline.rfqline.rfqlineid")) {
			int rfqlineid = mr.getInt("poline.rfqline.rfqlineid");
			newReceipt.setValue("rfqlineid", rfqlineid, 11L);
		}
		newReceipt.setValue("transtime", currentDate, 11L);
		newReceipt.setValue("requestedby", requestedby, 11L);
		newReceipt.setValue("udbudgetnum", udbudgetnum, 11L);
		newReceipt.setValue("udprojectnum", udprojectnum, 11L);
		newReceipt.setValue("remark", remark, 11L);
		return newReceipt;
	}

	/**
	 * 复制标准 createReceiptsPrep 方法
	 * 
	 * @param poLineSet
	 * @param po
	 * @throws RemoteException
	 * @throws MXException
	 */
	private void createFixReceiptsPrep(MboSetRemote poLineSet, MboRemote po) throws RemoteException, MXException {
		int i = 0;
		while (true) {
			MboRemote poLineRemote = poLineSet.getMbo(i);
			if (poLineRemote == null) {
				return;
			}
			if (!((POLineRemote) poLineRemote).isServiceType() && !poLineRemote.getBoolean("receiptscomplete")) {
				double orderQty = poLineRemote.getDouble("orderqty");

				ReceiptInputRemote receiptInputRemote = (ReceiptInputRemote) this.addAtEnd();
				receiptInputRemote.setValue("LINETYPE", poLineRemote.getString("LINETYPE"), 2L);
				// 赋值
				this.setFixReceiptVariablesPrep(po, receiptInputRemote, poLineRemote);

				if (receiptInputRemote.getDouble("qtyrequested") < 0.0D && orderQty > 0.0D) {
					receiptInputRemote.getThisMboSet().remove();
				}

				receiptInputRemote.setValue("asn", false, 11L);

			}

			++i;
		}
	}

	/**
	 * 复制标准 setReceiptVariablesPrep 方法
	 * 
	 * @param po
	 * @param dummyReceipt
	 * @param poLineRemote
	 * @throws MXException
	 * @throws RemoteException
	 */
	private void setFixReceiptVariablesPrep(MboRemote po, ReceiptInputRemote dummyReceipt, MboRemote poLineRemote)
			throws MXException, RemoteException {
		dummyReceipt.setPOMbo((PORemote) po);
		dummyReceipt.setPOLineMbo((POLineRemote) poLineRemote);

		dummyReceipt.setPOLineSet((POLineSetRemote) poLineRemote.getThisMboSet());
		dummyReceipt.setValue("ponum", poLineRemote.getString("ponum"), 2L);
		dummyReceipt.setValue("polinenum", poLineRemote.getString("polinenum"), 2L);
		dummyReceipt.setValue("linetype", poLineRemote.getString("linetype"), 2L);
		dummyReceipt.setValue("orderqty", poLineRemote.getString("orderqty"), 11L);
		dummyReceipt.setValue("currencylinecost", poLineRemote.getDouble("linecost"), 2L);
		dummyReceipt.setValue("receivedqty", poLineRemote.getString("receivedqty"));
		dummyReceipt.setValue("tostoreloc", poLineRemote.getString("storeloc"), 11L);
		dummyReceipt.setValue("inspectionrequired", poLineRemote.getString("inspectionrequired"), 11L);
		dummyReceipt.setValue("remark", poLineRemote.getString("remark"), 3L);
		MboSetRemote receiptSetRemote;

		if (!poLineRemote.isNull("orderqty")) {

			receiptSetRemote = po.getMboSet("UDFIXRECTRANS");

			double qtySum = 0.0D;
			int x = 0;

			while (true) {
				MboRemote receiptRemote = receiptSetRemote.getMbo(x);
				if (receiptRemote == null) {
					dummyReceipt.setValue("qtyrequested", poLineRemote.getDouble("orderqty") - qtySum, 2L);
					if (dummyReceipt.getDouble("qtyrequested") == 0.0D) {
						dummyReceipt.getThisMboSet().remove();
					}
					break;
				}

				if (receiptRemote.getInt("polineid") == poLineRemote.getInt("polineid")
						&& !receiptRemote.toBeDeleted()) {
					qtySum += receiptRemote.getDouble("quantity");
				}
				++x;
			}
		}

		dummyReceipt.setFieldFlag("ORDERQTY", 7L, true);
		dummyReceipt.setValue("currencyamtrcved", poLineRemote.getDouble("receivedtotalcost"), 11L);

		dummyReceipt.setValue("itemnum", poLineRemote.getString("itemnum"));
		dummyReceipt.setValue("itemsetid", poLineRemote.getString("itemsetid"));
		dummyReceipt.setValue("conditioncode", poLineRemote.getString("conditioncode"));
		dummyReceipt.setValue("description", poLineRemote.getString("description"));
	}
	
}
