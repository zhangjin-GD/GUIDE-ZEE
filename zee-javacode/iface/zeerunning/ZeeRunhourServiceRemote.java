package guide.iface.zeerunning;

import java.rmi.RemoteException;

import psdi.server.AppServiceRemote;
import psdi.util.MXException;

/**
 * @function:ZEE设备运行时长
 * @date:2023-07-10 08:45:27
 * @modify:
 */
public interface ZeeRunhourServiceRemote extends AppServiceRemote {
	public String UdZeeRunhourService(String json) throws RemoteException, MXException;
	public String UdZeeFuelService(String json) throws RemoteException, MXException;
}