package guide.iface.sap.webservice;
import java.util.List;

public class SapBeanOfCurrency {

	private HearBeanOfCurrency hearBean;
	
	private List<ItemBeanOfCurrency> itemBeans;

	public HearBeanOfCurrency getHearBean() {
		return hearBean;
	}

	public void setHearBean(HearBeanOfCurrency hearBean) {
		this.hearBean = hearBean;
	}

	public List<ItemBeanOfCurrency> getItemBeans() {
		return itemBeans;
	}

	public void setItemBeans(List<ItemBeanOfCurrency> itemBeans) {
		this.itemBeans = itemBeans;
	}
}
