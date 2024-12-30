package guide.app.workplan;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.common.CommonUtil;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWorkPlanPlanDate extends MboValueAdapter {

	public FldWorkPlanPlanDate(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		Date plandate = mbo.getDate("PLANDATE");
		String plandateStr = CommonUtil.getDateFormat(plandate, "yyyy-MM-dd");
		mbo.setValue("description", plandateStr + "工作计划", 11L);
	}
}
