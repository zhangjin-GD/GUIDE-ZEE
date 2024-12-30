package guide.webclient.beans.report;

import java.rmi.RemoteException;

import com.ibm.tivoli.maximo.report.birt.admin.ReportAdminServiceRemote;

import psdi.app.report.ReportRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.WebClientEvent;

public class ReportTableBean extends DataBean{

	public int preViewXmlList() throws MXException, RemoteException {
		WebClientEvent event = this.clientSession.getCurrentEvent();

		int msgRet = event.getMessageReturn();
		if (msgRet == 2) {
			return 1;
		} else {
			ReportAdminServiceRemote reportAdminServiceRemote = (ReportAdminServiceRemote) this.getMXSession()
					.lookup("BIRTREPORT");
			int engineState = reportAdminServiceRemote.getReportEngineState();
			if (engineState == 2) {
				this.clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "Reports Overloaded",
						this.getMessage("reports", "previewoverload"), 2);
				return 1;
			} else if (engineState == 1) {
				this.clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "Zero Concurrent Run",
						this.clientSession.getMaxMessage("reports", "zeroConcurrentRun").getMessage(), 2);
				return 1;
			} else if (engineState == 3) {
				this.clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "Reporting Disabled",
						this.clientSession.getMaxMessage("reports", "adminDisabled").getMessage(), 2);
				return 1;
			} else {
				//调用的方法
				try {
					ReportRemote reportRemote = (ReportRemote) this.getMbo();
					if (reportRemote.getBoolean("norequestpage")) {
						this.clientSession.showMessageBox(this.clientSession.getCurrentEvent(),
								this.getMessage("reports", "repappnoreqpgtitle"),
								this.getMessage("reports", "repappnoreqpg"), 0);
						return 1;
					}

					this.app.put("runtype", reportRemote.getString("runtype"));
					this.app.put("appname", reportRemote.getString("appname"));
					this.app.put("reportname", reportRemote.getString("reportname"));
					String pageName = "reportd" + reportRemote.getInt("reportnum");
					ControlInstance lookup = this.clientSession.findDialog(pageName);
					if (lookup == null) {
						String[] params1 = new String[] { pageName };
						this.clientSession.showMessageBox("reports", "repappnoxml", params1);
						return 1;
					}

					this.clientSession.loadDialog(pageName);
				} catch (MXException var9) {
					var9.printStackTrace();
				}

				return 1;
			}
		}
	}
	
	private String getMessage(String msgGrp, String msgKey) throws RemoteException, MXException {
		return MXServer.getMXServer().getMaxMessageCache().getMessage(msgGrp, msgKey, this.clientSession.getUserInfo())
				.getMessage();
	}
}
