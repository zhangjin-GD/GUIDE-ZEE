package guide.app.common;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;

public class Comlogs {

	public static void log2DB(String classname, String description, String message, String stacetrace) {
		try {
			MboSetRemote logSet = MXServer.getMXServer().getMboSet("UDCOMLOG",MXServer.getMXServer().getSystemUserInfo());		
			logSet.setWhere("1=2");
			MboRemote log = logSet.add();
			log.setValue("logtime", MXServer.getMXServer().getDate());
			log.setValue("classname", classname);
			log.setValue("description", description);
			log.setValue("message", message);
			log.setValue("stacetrace", stacetrace);
			logSet.save();
			logSet.close();
		} catch (Exception e) {
			System.out.println("日志写入失败！\n");
			e.printStackTrace();
		}
	}
}
