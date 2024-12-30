package guide.app.inventory.bean;

public class InventoryBean {

	private Integer id;

	private String company;

	private String dept;

	private String vendor;

	private String purchaser;

	private String itemnum;

	private Double orderqty;
	
	private Double conversion;
	
	private String orderunit;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	public String getItemnum() {
		return itemnum;
	}

	public void setItemnum(String itemnum) {
		this.itemnum = itemnum;
	}
	
	public String getOrderunit() {
		return orderunit;
	}

	public void setOrderunit(String orderunit) {
		this.orderunit = orderunit;
	}

	public Double getOrderqty() {
		return orderqty;
	}

	public void setOrderqty(Double orderqty) {
		this.orderqty = orderqty;
	}
	
	public void setConversion(Double conversion) {
		this.conversion = conversion;
	}
	
	public double getConversion() {
		return conversion;
	}
}
