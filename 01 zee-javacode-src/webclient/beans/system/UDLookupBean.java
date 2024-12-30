package guide.webclient.beans.system;

import java.rmi.RemoteException;

import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.util.MXSystemException;
import psdi.webclient.system.beans.LookupBean;

public class UDLookupBean extends LookupBean{
	public UDLookupBean() {
		usingSmartFill = false;
	}

	public MboSetRemote getMboSetRemote() throws MXException, RemoteException {
		MboSetRemote mboSetRemote = null;
		if (creatingEvent != null
				&& (creatingEvent.getValue() instanceof MboSetRemote)) {
			usingSmartFill = true;
			mboSetRemote = (MboSetRemote) creatingEvent.getValue();
			setTableFlag(32L, true);
			setTableFlag(256L, true);
			fetchData = true;
		} else {
			if (mboName != null || parentRelationship != null)
				return super.getMboSetRemote();
			if (mboSetRemote == null && parent != null)
				try {
					mboSetRemote = parent.getRemoteForLookup();
					mboSetRemote.setUserWhere("udworktype = '"+ parent.getString("worktype") +"' and udofs='"+ parent.getString("udofs") +"'");
				} catch (MXException ex) {
					throw ex;
				} catch (RemoteException ex) {
					throw new MXSystemException("system", "remoteexception", ex);
				} catch (Throwable t) {
					t.printStackTrace();
				}
		}
		return mboSetRemote;
	}

	boolean usingSmartFill;
}
