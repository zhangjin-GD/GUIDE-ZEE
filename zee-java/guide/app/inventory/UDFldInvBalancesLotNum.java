package guide.app.inventory;

import guide.app.common.CommonUtil;
import guide.app.common.QRCodeUtils;

import java.rmi.RemoteException;

import org.json.JSONObject;

import psdi.app.inventory.FldInvBalancesLotNum;
import psdi.app.inventory.MatRecTrans;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldInvBalancesLotNum extends FldInvBalancesLotNum {

	public UDFldInvBalancesLotNum(MboValue mbv) throws MXException {
		super(mbv);
	}

	public void action() throws RemoteException, MXException {
		super.action();

		if (!this.getMboValue().isNull()) {
			MboRemote mbo = this.getMboValue().getMbo();
			MboRemote inventory = mbo.getOwner();
			if (inventory != null) {
				MboRemote matrectrans = inventory.getOwner();
				if (matrectrans != null && (matrectrans instanceof MatRecTrans)) {
					int matrectransid = matrectrans.getInt("matrectransid");
					double predicttaxprice = matrectrans.getDouble("udpredicttaxprice");
					double predictprice = matrectrans.getDouble("udpredictprice");
					String udvendor = matrectrans.getString("udvendor");
					String ponum = matrectrans.getString("ponum");
					mbo.setValue("udmatrectransid", matrectransid, 11L);
					mbo.setValue("udpredicttaxprice", predicttaxprice, 11L);
					mbo.setValue("udpredictprice", predictprice, 11L);
					mbo.setValue("udvendor", udvendor, 11L);
					mbo.setValue("udponum", ponum, 11L);
					String QRCodeStatus = MXServer.getMXServer().getProperty("guide.qrcode.status");
					if (QRCodeStatus != null && QRCodeStatus.equalsIgnoreCase("ACTIVE")) {
						createQRCode(matrectrans, mbo);
					}
				}
			}
		}
	}

	private void createQRCode(MboRemote matrectrans, MboRemote mbo) throws MXApplicationException {
		try {
			MboSetRemote polineSet = matrectrans.getMboSet("POLINE");
			if (!polineSet.isEmpty() && polineSet.count() > 0) {
				MboRemote poline = polineSet.getMbo(0);
				JSONObject jsonData = new JSONObject();
				jsonData.put("itemnum", mbo.getString("itemnum"));
				jsonData.put("location", mbo.getString("location"));
				jsonData.put("binnum", mbo.getString("binnum"));
				jsonData.put("lotnum", mbo.getString("lotnum"));
				jsonData.put("description", mbo.getString("ITEM.udlongdesc"));
				jsonData.put("transdate", matrectrans.getString("transdate"));
				jsonData.put("requestedBy", poline.getString("REQUESTEDBY.displayname"));
				jsonData.put("estTime", poline.getString("PRLINE.UDESTTIME.description"));
				System.out.println("\n-----------------" + jsonData);

				String docPath = MXServer.getMXServer().getProperty("guide.qrcode.path");
				if (docPath == null || docPath.equalsIgnoreCase("")) {
					docPath = "D:\\DOCLINKS\\QRCode\\INVBALANCES\\";
				}
				String docName = "" + matrectrans.getInt("matrectransid") + "";
				String docFile = docPath + docName.substring(0, 1) + "000";
				String status = QRCodeUtils.QRCode(jsonData.toString(), docFile, docName + ".jpg");
				if (status != null && status.equalsIgnoreCase("OK"))
					CommonUtil.createDoc(mbo, docFile, docName + ".jpg", "QRCodeINVBAL");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Object[] params = { "提示：" + e.toString() + "！" };
			throw new MXApplicationException("instantmessaging", "tsdimexception", params);
		}
	}

}
