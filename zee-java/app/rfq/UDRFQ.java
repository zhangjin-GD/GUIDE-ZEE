package guide.app.rfq;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.app.rfq.RFQ;
import psdi.app.rfq.RFQRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDRFQ extends RFQ implements RFQRemote {

	public UDRFQ(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		// PCT 外协时 描述字段不必填
		super.init();
		try {
			String personId = getUserInfo().getPersonId();
			MboSetRemote mboSet = getMboSet("$PERSON", "PERSON", "PERSONID='" + personId + "'");
			String udcompany = mboSet.getMbo(0).getString("udcompany");
			if (!udcompany.equals("GR02PCT")) {
				setFieldFlag("DESCRIPTION", 128L, true);
			} else {
				setFieldFlag("DESCRIPTION", 128L, false);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();

		String appName = this.getThisMboSet().getApp();
		String personid = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();

		if (appName != null && !appName.isEmpty()) {
			double ukurs = 1;
			String udappType = appName.replaceAll("UD", ""); // 替换UD
			this.setValue("udapptype", udappType, 11L);
			this.setValue("udcreateby", personid, 2L);// 创建人
			this.setValue("purchaseagent", personid, 2L);// 采购员
			this.setValue("udcreatetime", currentDate, 11L);// 创建时间
			MboSetRemote companySet = this.getMboSet("UDCOMPANY");
			if (!companySet.isEmpty() && companySet.count() > 0) {
				String currency = companySet.getMbo(0).getString("currency");
				this.setValue("udcurrency", currency, 11L);
				MboSetRemote currexchSet = this.getMboSet("UDCURREXCH");
				if (!currexchSet.isEmpty() && currexchSet.count() > 0) {
					ukurs = currexchSet.getMbo(0).getDouble("ukurs");
				}
			}
			this.setValue("udukurs", ukurs, 11L);
		}
	}

	@Override
	public void createRFQLineFromPR(MboRemote fromPR, MboRemote fromPRLine) throws MXException, RemoteException {
		super.createRFQLineFromPR(fromPR, fromPRLine);

		MboSetRemote rfqLineSet = getMboSet("RFQLINE");
		for (int i = 0; rfqLineSet.getMbo(i) != null; i++) {
			MboRemote rfqLine = rfqLineSet.getMbo(i);
			if (rfqLine != null && rfqLine.getInt("rfqlineid") == fromPRLine.getInt("rfqlineid")
					&& rfqLine.getInt("rfqlinenum") == fromPRLine.getInt("rfqlinenum")
					&& rfqLine.getString("rfqnum") != null
					&& rfqLine.getString("rfqnum").equalsIgnoreCase(fromPRLine.getString("rfqnum"))
					&& rfqLine.getString("siteid") != null
					&& rfqLine.getString("siteid").equalsIgnoreCase(fromPRLine.getString("siteid"))) {

				rfqLine.setValue("udprojectnum", fromPRLine.getString("udprojectnum"), 11L);
				rfqLine.setValue("udbudgetnum", fromPRLine.getString("udbudgetnum"), 11L);
				
				/**
				 * ZEE-rfq选择prline时给rfqline赋值
				 */
				rfqLine.setValue("udcapex", fromPRLine.getString("udcapex"), 11L);
				rfqLine.setValue("udcosttype", fromPRLine.getString("udcosttype"), 2L);
				rfqLine.setValue("linetype", fromPRLine.getString("linetype"), 2L);
                rfqLine.setValue("udprevendor", fromPRLine.getString("udprevendor"), 2L);
			}
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
	}

	public void costRanking() throws RemoteException, MXException {

		int rfqlinenumnew = -1;
		int ranking = -1;
		MboSetRemote quoLineSet = getMboSet("UDQUOTATIONLINE");
		quoLineSet.setOrderBy("rfqlinenum,udtotalcost");
		quoLineSet.reset();
		if (!quoLineSet.isEmpty() && quoLineSet.count() > 0) {
			for (int i = 0; quoLineSet.getMbo(i) != null; i++) {
				MboRemote quoLine = quoLineSet.getMbo(i);
				int rfqlinenum = quoLine.getInt("rfqlinenum");
				if (rfqlinenumnew != rfqlinenum) {
					ranking = 1;
				}
				quoLine.setValue("udranking", "" + ranking + "", 11L);
				if (ranking == 1) {
					quoLine.setValue("isawarded", true, 2L);
				}
				rfqlinenumnew = rfqlinenum;
				ranking++;
			}
		}
	}
}
