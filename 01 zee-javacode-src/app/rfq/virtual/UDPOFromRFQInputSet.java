package guide.app.rfq.virtual;

import java.rmi.RemoteException;

import psdi.app.rfq.virtual.POFromRFQInputSet;
import psdi.app.rfq.virtual.POFromRFQInputSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetInfo;
import psdi.mbo.MboValueInfo;
import psdi.security.ConnectionKey;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.MXException;

import java.sql.Connection;

import guide.app.rfq.UDRFQ;
import guide.app.rfq.UDRFQVendor;
import psdi.mbo.AutoKey;

public class UDPOFromRFQInputSet extends POFromRFQInputSet implements POFromRFQInputSetRemote {

	public UDPOFromRFQInputSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDPOFromRFQInput(ms);
	}

	@Override
	public MboRemote setup() throws MXException, RemoteException {
		MboRemote mbo = null;
		MboRemote owner = this.getOwner();
		if (owner != null && owner instanceof UDRFQVendor) {
			MboSetInfo ti = MXServer.getMXServer().getMaximoDD().getMboSetInfo("PO");
			MboValueInfo di = ti.getMboValueInfo("ponum");
			ConnectionKey conKey = null;
			UserInfo ui = owner.getUserInfo();
			conKey = new ConnectionKey(ui);
			Connection con = this.getMboServer().getDBConnection(conKey);
			AutoKey ak = new AutoKey(con, di, ui, this.getOwner(), ti);
			mbo = this.addAtEnd();
			mbo.setValue("ponum", ak.nextValue(), 11L);
			MboRemote rfq = owner.getOwner();
			if (rfq != null && rfq instanceof UDRFQ) {
				mbo.setValue("description", rfq.getString("description"), 11L);
			}
		}
		return mbo;
	}
}
