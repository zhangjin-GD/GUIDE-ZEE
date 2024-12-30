package guide.app.pr;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import guide.app.common.CommonUtil;
import guide.app.po.UDPO;
import psdi.app.pr.PR;
import psdi.app.pr.PRRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDPR extends PR implements PRRemote {

	private static final int PRKEYLEN = 3;

	public UDPR(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			String prSerType = this.getString("udprsertype");
			String apptype = this.getString("udapptype");
			if ("PRSER".equalsIgnoreCase(apptype)) {
				String[] attrs = { "udreason", "udtechspec", "accepmethod" };
				if ("A".equalsIgnoreCase(prSerType)) {
					this.setFieldFlag(attrs, 128L, true);
				} else {
					this.setFieldFlag(attrs, 128L, false);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
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
			this.setValue("udcreatetime", currentDate, 11L);// 创建时间
			this.setValue("requireddate", CommonUtil.getCalDate(currentDate, 14), 2L);// 要求日期，默认14天后
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
//			ZEE- PR预算编号，部门层面赋予默认值(采购中RFQ,PO,RECEIPT自动关联) 77-99
			if(udappType.equalsIgnoreCase("PRZEE")){
				String udbudgetnum = "";
				String uddept = "";
				String requestedby = this.getString("requestedby");
				
				MboSetRemote personSet = MXServer.getMXServer().getMboSet("PERSON", MXServer.getMXServer().getSystemUserInfo());
				personSet.setWhere("personid = '" + requestedby + "'  ");
				personSet.reset();
				if(!personSet.isEmpty() && personSet.count() > 0){
					uddept = personSet.getMbo(0).getString("uddept");
				}
				personSet.close();
				
				MboSetRemote udbudgetSet = MXServer.getMXServer().getMboSet("UDBUDGET", MXServer.getMXServer().getSystemUserInfo());
				udbudgetSet.setWhere("uddept = '" + uddept + "' and udcompany = 'ZEE' ");
				udbudgetSet.reset();
				if(!udbudgetSet.isEmpty() && udbudgetSet.count() > 0){
					udbudgetnum = udbudgetSet.getMbo(0).getString("budgetnum");
					this.setValue("udbudgetnum", udbudgetnum, 2L);
				}
				udbudgetSet.close();
			}
		}
	}

	@Override
	public void save() throws MXException, RemoteException {
		super.save();

		if (this.toBeAdded()) {
			setAutoKeyNum();
		}

		String appType = getString("udapptype");
		String status = getString("status");
		if (appType != null && status != null && ("WAPPR".equalsIgnoreCase(status) || "BACK".equalsIgnoreCase(status))
				&& ("PRFIX".equalsIgnoreCase(appType) || "PRMAT".equalsIgnoreCase(appType))) {
			checkOrderqty();
		}
	}

	private void setAutoKeyNum() throws RemoteException, MXException {
		String apptype = this.getString("udapptype");
		String udcompany = this.getString("udcompany");
		String persongroup = "";
		MboSetRemote personGroupSet = this.getMboSet("$PERSONGROUP", "PERSONGROUP", "uddept = '" + udcompany + "' ");
		if (personGroupSet != null && !personGroupSet.isEmpty()) {
			persongroup = personGroupSet.getMbo(0).getString("persongroup");
		}
		if ("prmat".equalsIgnoreCase(apptype)) {
			String prkeyNum = CommonUtil.autoKeyNum("PR", "UDPRKEYNUM", persongroup + "QG", "yyyyMMdd", PRKEYLEN);
			this.setValue("udprkeynum", prkeyNum, 11L);
		}
	}

	public void checkOrderqty() throws RemoteException, MXException {
		MboSetRemote prlineSet = this.getMboSet("PRLINE");
		if (!prlineSet.isEmpty() && prlineSet.count() > 0) {
			String linenum = "";
			for (int i = 0; prlineSet.getMbo(i) != null; i++) {
				MboRemote prline = prlineSet.getMbo(i);
				if (!prline.toBeDeleted()) {
					MboSetRemote invbalancesSet = prline.getMboSet("UDINVBALCURBAL");
					if (!invbalancesSet.isEmpty() && invbalancesSet.count() > 0) {
						double invCurbal = invbalancesSet.sum("curbal");
						double orderqty = prline.getDouble("orderqty");
						if (invCurbal >= orderqty) {
							int prlinenum = prline.getInt("prlinenum");
							linenum += prlinenum + ",";
						}
					}
				}
			}
			if (linenum != null && !linenum.equalsIgnoreCase("")) {
				// 去掉逗号
				String params = linenum.substring(0, linenum.length() - 1);
				Object[] obj = { params };
				this.getThisMboSet().addWarning(new MXApplicationException("guide", "1122", obj));
			}
		}
	}

	public String addPOsFromPR() throws MXException, RemoteException {
		if (this.getMboServer().getMaxVar().getBoolean("PRAPPROVAL", this.getOrgSiteForMaxvar("PRAPPROVAL"))
				&& !this.getInternalStatus().equalsIgnoreCase("APPR")) {
			String status = this.getTranslator().toExternalDefaultValue("PRSTATUS", "APPR", this);
			Object[] params = new Object[] { status };
			throw new MXApplicationException("pr", "cannotcreatepo", params);
		} else {
			return this.addPOFromPR(this.getString("description"));
		}
	}

	public String addPOFromPR(String description) throws RemoteException, MXException {

		UDPO contractPOHeader = (UDPO) this.createPOHeaderFromPR("", description, this);
		String ponum = contractPOHeader.getString("ponum");
		String personid = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		String udapptype = this.getString("udapptype");
		Date requireddate = this.getDate("requireddate");
		String udmatstatus = this.getString("udmatstatus");
		String currency = this.getString("udcurrency");
//		String vendor = this.getString("vendor");
		double ukurs = 1;
		if (!this.isNull("udukurs")) {
			ukurs = this.getDouble("udukurs");
		}
		String apptype = udapptype.replaceAll("PR", "PO");
		contractPOHeader.setValue("udapptype", apptype, 11L);
		contractPOHeader.setValue("udpurplat", "DPO", 11L);
		contractPOHeader.setValue("udcreateby", personid, 2L);
		contractPOHeader.setValue("udcreatetime", currentDate, 11L);
		contractPOHeader.setValue("purchaseagent", personid, 2L);
		contractPOHeader.setValue("requireddate", requireddate, 11L);
		contractPOHeader.setValue("udmatstatus", udmatstatus, 11L);
		contractPOHeader.setValue("udcurrency", currency, 11L);
		contractPOHeader.setValue("udukurs", ukurs, 11L);
		contractPOHeader.setValue("udrevponum", ponum, 11L);
		contractPOHeader.setValue("udrevnum", 0, 11L);
		MboSetRemote prLineSet = this.getMboSet("PRLINE");
		if (!prLineSet.isEmpty() && prLineSet.count() > 0) {
			for (int i = 0; prLineSet.getMbo(i) != null; i++) {
				MboRemote contractRemote = prLineSet.getMbo(i);
				MboSetRemote newPOLineSet = contractPOHeader.getMboSet("POLINE");
				contractPOHeader.createPOLineFromPR(this, contractRemote, newPOLineSet);
//				String linetype = prline.getString("linetype");
//				String itemnum = prline.getString("itemnum");
//				String tax1code = prline.getString("tax1code");
//				double orderqty = prline.getDouble("orderqty");
//				double udtotalprice = prline.getDouble("udtotalprice");
//				double udtotalcost = prline.getDouble("udtotalcost");
//				double unitcost = prline.getDouble("unitcost");
//				double linecost = prline.getDouble("linecost");
//				double tax1 = prline.getDouble("tax1");
//				String udbudgetnum = prline.getString("udbudgetnum");
//				String storeloc = prline.getString("storeloc");
//				String enterby = prline.getString("enterby");
//				String remark = prline.getString("remark");
//
//				UDPOLine poline = (UDPOLine) polineSet.addAtEnd();
//				poline.setValue("linetype", linetype, 2L);
//				poline.setValue("itemnum", itemnum, 2L);
//				poline.setValue("tax1code", tax1code, 2L);
//				poline.setValue("orderqty", orderqty, 2L);
//				poline.setValue("udtotalprice", udtotalprice, 11L);
//				poline.setValue("udtotalcost", udtotalcost, 2L);
//				poline.setValue("unitcost", unitcost, 11L);
//				poline.setValue("linecost", linecost, 11L);
//				poline.setValue("tax1", tax1, 11L);
//				poline.setValue("udbudgetnum", udbudgetnum, 11L);
//				poline.setValue("storeloc", storeloc, 2L);
//				poline.setValue("requestedby", enterby, 11L);
//				poline.setValue("remark", remark, 11L);
//
//				String ponum = poline.getString("ponum");
//				int porevisionnum = poline.getInt("revisionnum");
//				int polinenum = poline.getInt("polinenum");
//				int polineid = poline.getInt("polineid");
//
//				prline.setValue("ponum", ponum, 11L);
//				prline.setValue("porevisionnum", porevisionnum, 11L);
//				prline.setValue("polinenum", polinenum, 11L);
//				prline.setValue("polineid", polineid, 11L);
			}
		}

		boolean prAlreadyClosed = this.getInternalStatus().equalsIgnoreCase("COMP");
		if (!prAlreadyClosed
				&& this.getMboServer().getMaxVar().getBoolean("PRCHANGE", this.getOrgSiteForMaxvar("PRCHANGE"))
				&& this.isLineContNumFilled(prLineSet)) {
			String tempPONum = this.getTranslator().toExternalDefaultValue("PRSTATUS", "COMP", this);
			if (this.getInternalStatus().equalsIgnoreCase("WAPPR")) {
				MXServer.getBulletinBoard().post("pr.ALLOWWAPPRTOCLOSE", this.getUserInfo());
			}
			try {
				this.changeStatus(tempPONum, MXServer.getMXServer().getDate(), "");
			} finally {
				if (this.getInternalStatus().equalsIgnoreCase("WAPPR")) {
					MXServer.getBulletinBoard().remove("pr.ALLOWWAPPRTOCLOSE", this.getUserInfo());
				}
			}
		}

		return ponum;
	}

	public String addPOFromPR(String description, Vector<MboRemote> vector) throws RemoteException, MXException {

		UDPO contractPOHeader = (UDPO) this.createPOHeaderFromPR("", description, this);
		String ponum = contractPOHeader.getString("ponum");
		String personid = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		String udapptype = this.getString("udapptype");
		Date requireddate = this.getDate("requireddate");
		String udmatstatus = this.getString("udmatstatus");
		String currency = this.getString("udcurrency");
//		String vendor = this.getString("vendor");
		double ukurs = 1;
		if (!this.isNull("udukurs")) {
			ukurs = this.getDouble("udukurs");
		}
		String apptype = udapptype.replaceAll("PR", "PO");
		contractPOHeader.setValue("udapptype", apptype, 11L);
		contractPOHeader.setValue("udpurplat", "DPO", 11L);
		contractPOHeader.setValue("udcreateby", personid, 2L);
		contractPOHeader.setValue("udcreatetime", currentDate, 11L);
		contractPOHeader.setValue("purchaseagent", personid, 2L);
		contractPOHeader.setValue("requireddate", requireddate, 11L);
		contractPOHeader.setValue("udmatstatus", udmatstatus, 11L);
		contractPOHeader.setValue("udcurrency", currency, 11L);
		contractPOHeader.setValue("udukurs", ukurs, 11L);
		contractPOHeader.setValue("udrevponum", ponum, 11L);
		contractPOHeader.setValue("udrevnum", 0, 11L);
		MboSetRemote prLineSet = this.getMboSet("PRLINE");
		for (int i = 0; i < vector.size(); i++) {
			MboRemote contractRemote = (MboRemote) vector.elementAt(i);
			MboSetRemote newPOLineSet = contractPOHeader.getMboSet("POLINE");
			contractPOHeader.createPOLineFromPR(this, contractRemote, newPOLineSet);
		}
		boolean prAlreadyClosed = this.getInternalStatus().equalsIgnoreCase("COMP");
		if (!prAlreadyClosed
				&& this.getMboServer().getMaxVar().getBoolean("PRCHANGE", this.getOrgSiteForMaxvar("PRCHANGE"))
				&& this.isLineContNumFilled(prLineSet)) {
			String tempPONum = this.getTranslator().toExternalDefaultValue("PRSTATUS", "COMP", this);
			if (this.getInternalStatus().equalsIgnoreCase("WAPPR")) {
				MXServer.getBulletinBoard().post("pr.ALLOWWAPPRTOCLOSE", this.getUserInfo());
			}
			try {
				this.changeStatus(tempPONum, MXServer.getMXServer().getDate(), "");
			} finally {
				if (this.getInternalStatus().equalsIgnoreCase("WAPPR")) {
					MXServer.getBulletinBoard().remove("pr.ALLOWWAPPRTOCLOSE", this.getUserInfo());
				}
			}
		}

		return ponum;
	}
	
	/**
	 * 
	 *  ZEE- PR创建PO
	 *  DJY
	 *  2024/7/4
	 */
	public String addAllPOFromPR(String description) throws RemoteException, MXException {
		UDPO createPOHeader = (UDPO) this.createPOHeaderFromPR("", description, this);
		String ponum = createPOHeader.getString("ponum");
		String personid = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		String udapptype = this.getString("udapptype");
		Date requireddate = this.getDate("requireddate");
		String udmatstatus = this.getString("udmatstatus");
		String currency = this.getString("udcurrency");
		String udcompany = this.getString("udcompany");
		double ukurs = 1;
		if (!this.isNull("udukurs")) {
			ukurs = this.getDouble("udukurs");
		}
		String apptype = udapptype.replaceAll("PR", "PO");
		createPOHeader.setValue("udapptype", apptype, 11L);
		createPOHeader.setValue("udpurplat", "DPO", 11L);
		createPOHeader.setValue("udcreateby", personid, 2L);
		createPOHeader.setValue("udcreatetime", currentDate, 11L);
		createPOHeader.setValue("requireddate", requireddate, 11L);
		createPOHeader.setValue("udmatstatus", udmatstatus, 11L);
		createPOHeader.setValue("udcurrency", currency, 11L);
		createPOHeader.setValue("udukurs", ukurs, 11L);
		createPOHeader.setValue("udrevponum", ponum, 11L);
		createPOHeader.setValue("udrevnum", 0, 11L);
		createPOHeader.setValue("udcompany", udcompany, 11L);
		MboSetRemote prLineSet = this.getMboSet("PRLINE");
		prLineSet.setWhere("prnum = '"+this.getString("prnum")+"' and udprevendor = '"+this.getString("vendor")+"' and itemnum in (select itemnum from udcontractline where gconnum in (select gconnum from udcontract where vendor = '"
				+this.getString("vendor")+"' and status='APPR' and to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd')"
				+ "))");
		prLineSet.reset();
		if (!prLineSet.isEmpty() && prLineSet.count() > 0) {
			MboRemote oldprlineRemote = prLineSet.getMbo(0);
			String purchaseagent = oldprlineRemote.getString("udpurchaser");
			if(!purchaseagent.equalsIgnoreCase("")){
				System.out.println("PRLINE.UDPURCHASER");
				createPOHeader.setValue("purchaseagent", purchaseagent, 2L);  //采购员需要换成PRLINE.UDPURCHASER,如果PRLINE.UDPURCHASER非空，则取第一条的采购员
			}else{
				System.out.println("PR.UDPURCHASER");
				createPOHeader.setValue("purchaseagent", personid, 2L);//如果PRLINE.UDPURCHASER为空，则取PR当前创建人
			}
		}
		if (!prLineSet.isEmpty() && prLineSet.count() > 0) {
			for (int j = 0; prLineSet.getMbo(j) != null; j++) {
				MboRemote createprlineRemote = prLineSet.getMbo(j);
				MboSetRemote newPOLineSet = createPOHeader.getMboSet("POLINE");
				createPOHeader.createPOLineFromPR(this, createprlineRemote, newPOLineSet);
			}
		}
		boolean prAlreadyClosed = this.getInternalStatus().equalsIgnoreCase("COMP");
		if (!prAlreadyClosed
				&& this.getMboServer().getMaxVar().getBoolean("PRCHANGE", this.getOrgSiteForMaxvar("PRCHANGE"))
				&& this.isLineContNumFilled(prLineSet)) {
			String tempPONum = this.getTranslator().toExternalDefaultValue("PRSTATUS", "COMP", this);
			if (this.getInternalStatus().equalsIgnoreCase("WAPPR")) {
				MXServer.getBulletinBoard().post("pr.ALLOWWAPPRTOCLOSE", this.getUserInfo());
			}
			try {
				this.changeStatus(tempPONum, MXServer.getMXServer().getDate(), "");
			} finally {
				if (this.getInternalStatus().equalsIgnoreCase("WAPPR")) {
					MXServer.getBulletinBoard().remove("pr.ALLOWWAPPRTOCLOSE", this.getUserInfo());
				}
			}
		}
		return ponum;
	}
	
}
