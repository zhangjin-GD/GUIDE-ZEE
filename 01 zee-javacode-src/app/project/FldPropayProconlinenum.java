package guide.app.project;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldPropayProconlinenum extends MAXTableDomain {

	public FldPropayProconlinenum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDPROCONLINE", "PROCONLINENUM=:" + thisAttr);
		String[] FromStr = { "PROCONLINENUM", "PROCONNUM" };
		String[] ToStr = { thisAttr, "PROCONNUM" };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		Mbo mbo = this.getMboValue().getMbo();
		String proconnum = mbo.getString("proconnum");
		if (proconnum.isEmpty()) {
			Object[] obj = { "温馨提示：请先选择项目合同编号后再操作！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}
		String sql = "not exists (select 1 from udpropay where udpropay.status not in ('取消') "
				+ "and udpropay.proconnum=udproconline.proconnum "
				+ "and udpropay.proconlinenum=udproconline.proconlinenum) and proconnum=:proconnum";
		setListCriteria(sql);// 条件待定
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote proConLineSet = mbo.getMboSet("UDPROCONLINE");
		if (!proConLineSet.isEmpty()) {
			MboRemote proConLine = proConLineSet.getMbo(0);
			String paytype = proConLine.getString("paytype");
			String payterm = proConLine.getString("payterm");
			double linecost = proConLine.getDouble("linecost");
			Date paiddate = proConLine.getDate("paiddate");

			mbo.setValue("paytype", paytype, 11L);
			mbo.setValue("payterm", payterm, 11L);
			mbo.setValue("linecost", linecost, 2L);
			mbo.setValue("paiddate", paiddate, 11L);
		}

		if (this.getMboValue().isNull()) {
			mbo.setValueNull("paytype", 11L);
			mbo.setValueNull("payterm", 11L);
			mbo.setValue("linecost", 0.0D, 2L);
			mbo.setValueNull("paiddate", 11L);
		}
	}
}
