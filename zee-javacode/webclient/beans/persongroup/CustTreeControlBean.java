package guide.webclient.beans.persongroup;

import java.rmi.RemoteException;

import psdi.util.MXException;
import psdi.webclient.beans.common.TreeControlBean;
import psdi.webclient.system.beans.DataBean;

public class CustTreeControlBean extends TreeControlBean {

	private String table = "PERSONGROUP"; // 表名
	private String keynum = "PERSONGROUP"; // 编号
	private String keyid = "PERSONGROUPID";// ID

	public CustTreeControlBean() {
	}

	@Override
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
	}

	@Override
	public int selectrecord() throws MXException, RemoteException {

		return 1;
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		return 1;
	}

	@Override
	public int selectnode() throws MXException, RemoteException {
		super.selectnode();
		long selectedUniqueId = Long.parseLong(getuniqueidvalue().replace(",", ""));

		String sql = keynum + " in (select " + keynum + " from " + table + " start with " + keynum + " = (select "
				+ keynum + " from " + table + " where " + keyid + "=" + selectedUniqueId + ") connect by  prior "
				+ keynum + " = parent )";
		DataBean resultsBean = app.getResultsBean();
		resultsBean.setUserWhere(sql);
		resultsBean.reset();
		return 1;
	}
}
