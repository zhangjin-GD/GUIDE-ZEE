package guide.app.workplan;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class WorkTask extends Mbo implements MboRemote {

	public WorkTask(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			String plannum = parent.getString("plannum");
			//设置默认时间 早上8点半 下午17点
			Date createtime = parent.getDate("createtime");
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(createtime);
			calendar1.set(Calendar.HOUR_OF_DAY, 8);
			calendar1.set(Calendar.MINUTE, 30);
			calendar1.set(Calendar.SECOND, 0);
			Date planStartDate = calendar1.getTime();
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(createtime);
			calendar2.set(Calendar.HOUR_OF_DAY, 17);
			calendar2.set(Calendar.MINUTE, 0);
			calendar2.set(Calendar.SECOND, 0);
			Date planEndDate = calendar2.getTime();
			
			this.setValue("plannum", plannum, 11L);
			this.setValue("planstartdate", planStartDate, 11L);
			this.setValue("planenddate", planEndDate, 11L);
		}
	}
}
