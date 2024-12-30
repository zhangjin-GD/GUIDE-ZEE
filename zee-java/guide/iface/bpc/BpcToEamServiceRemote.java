package guide.iface.bpc;

import java.rmi.RemoteException;

import psdi.server.AppServiceRemote;
import psdi.util.MXException;

/**
 * @function:BCT>-EAM 预算管理
 * @date:2021-01-25 15:39:09
 * @modify:
 */
public interface BpcToEamServiceRemote extends AppServiceRemote {

	public String UdBpcService(String json) throws RemoteException, MXException;

}