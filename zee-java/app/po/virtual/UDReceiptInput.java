package guide.app.po.virtual;

import java.rmi.RemoteException;

import psdi.app.inventory.InventoryRemote;
import psdi.app.inventory.MatRecTransRemote;
import psdi.app.mr.MRRemote;
import psdi.app.po.POLineRemote;
import psdi.app.po.POLineSetRemote;
import psdi.app.po.PORemote;
import psdi.app.po.ShipmentLineRemote;
import psdi.app.po.ShipmentRemote;
import psdi.app.po.virtual.ReceiptInput;
import psdi.app.po.virtual.ReceiptInputRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.MXMath;

public class UDReceiptInput extends ReceiptInput implements ReceiptInputRemote {

	public UDReceiptInput(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}
	
}
