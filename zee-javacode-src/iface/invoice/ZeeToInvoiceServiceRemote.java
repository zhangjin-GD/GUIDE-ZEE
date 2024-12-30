package guide.iface.invoice;

import java.rmi.RemoteException;

import psdi.server.AppServiceRemote;
import psdi.util.MXException;

/**
 * @function:ZEE>-INVOCE 发票中间表
 * @date:2023-06-29 09:10:27
 * @modify:
 */
public interface ZeeToInvoiceServiceRemote extends AppServiceRemote {

	public String UdZeeToInvoiceService(String json) throws RemoteException, MXException;

}