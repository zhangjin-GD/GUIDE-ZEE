package guide.app.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.app.inventory.FldMatRecTransPoLineNum;
import psdi.app.inventory.MatRecTrans;
import psdi.app.po.POLineRemote;
import psdi.app.po.PORemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldMatRecTransPoLineNum extends FldMatRecTransPoLineNum {

	public UDFldMatRecTransPoLineNum(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		if (!this.getMboValue().isNull()) {

			Date currentDate = MXServer.getMXServer().getDate();
			MatRecTrans matrec = (MatRecTrans) this.getMboValue().getMbo();
			PORemote po = matrec.getPO();
			POLineRemote poline = matrec.getPOLine();
			String udprojectnum = poline.getString("udprojectnum");
			String udbudgetnum = poline.getString("udbudgetnum");
			double predicttaxprice = poline.getDouble("udpredicttaxprice");
			double predictprice = poline.getDouble("udpredictprice");
			String vendor = poline.getString("po.vendor");
			matrec.setValue("udprojectnum", udprojectnum, 11L);
			matrec.setValue("udbudgetnum", udbudgetnum, 11L);
			matrec.setValue("udpredicttaxprice", predicttaxprice, 11L);
			matrec.setValue("udpredictprice", predictprice, 11L);
			matrec.setValue("udvendor", vendor, 11L);
			if (matrec.isNull("udtobin")) {
				matrec.setValue("udtobin", matrec.getString("tobin"), 11L);
			}
			po.setValue("changedate", currentDate, 11L);
		}
	}
}
