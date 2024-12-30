package guide.iface.sap.webservice;

import java.util.List;

public class SapBean {

	private HearBean hearBean;
	
	private List<ItemBean> itemBeans;

	public HearBean getHearBean() {
		return hearBean;
	}

	public void setHearBean(HearBean hearBean) {
		this.hearBean = hearBean;
	}

	public List<ItemBean> getItemBeans() {
		return itemBeans;
	}

	public void setItemBeans(List<ItemBean> itemBeans) {
		this.itemBeans = itemBeans;
	}
}
