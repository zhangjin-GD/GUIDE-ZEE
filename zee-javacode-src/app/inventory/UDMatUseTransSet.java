package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;
import psdi.app.inventory.MatUseTransSet;
import psdi.app.inventory.MatUseTransSetRemote;


/**
 *@function:
 *@author:zj
 *@date:2024-01-17 11:35:30
 *@modify:
 */
public class UDMatUseTransSet extends MatUseTransSet implements MatUseTransSetRemote{
	
	public UDMatUseTransSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}
	
	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDMatUseTrans(ms);
	}
	
}
