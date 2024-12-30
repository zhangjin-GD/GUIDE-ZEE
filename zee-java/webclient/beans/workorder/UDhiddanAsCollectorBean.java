package guide.webclient.beans.workorder;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

import java.rmi.RemoteException;
import java.util.Vector;

public class UDhiddanAsCollectorBean extends DataBean {

	protected void initialize() throws MXException, RemoteException {
		super.initialize();
	}

	// 点击对话框确定按钮
	public int execute() throws MXException, RemoteException {
		// 获取选中的数据
		Vector vec = getSelection();
		// 如果没有选中进行提示
		if (vec.size() <= 0)
			throw new MXApplicationException("提示", "请至少选择一项安全隐患。");
		// 获取要添加的列表的数据源表示，本例为采样员列表的数据源标识，可以在应用程序设计其中查看表格属性中进行查看
		DataBean parentBean = app.getDataBean("1663305278335");
		// 获取当前应用程序主表的记录
		MboRemote appmbo = app.getAppBean().getMbo();
		// 对已选中的集合进行循环
		for (int i = 0; vec.size() > i; i++) {
			// 获得一条选中的数据的Mbo
			MboRemote mr = (MboRemote) vec.elementAt(i);
			if (mr != null) {
				// 新建数据行
				parentBean.addrow();
				// 设置各个字段值：字段名，字段值
				parentBean.setValue("udhiddannum", mr.getString("udhiddannum"));
				parentBean.setValue("risk", mr.getString("risk"), 11L);
				parentBean.setValue("description", mr.getString("description"));
				parentBean.setValue("wonum", appmbo.getString("wonum"));

			}
		}
		return 1;
	}

//	// 获取选择范围
//	public MboSetRemote getMboSetRemote() throws MXException, RemoteException {
//		// 获得增加人员的集合
//		MboSetRemote msr = super.getMboSetRemote();
//		MboRemote mbo = this.app.getAppBean().getMbo();
//		// 选择人员的 where子句， 此子句可以 在添加到select * from person where 后面执行查询
//		String sql = " udassettypecode = '" + mbo.getString("udassettypecode") + "'";
//		// 设置选择条件
//		msr.setWhere(sql);
//		// 重置列表
//		msr.reset();
//		return msr;
//	}
}
