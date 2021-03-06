package com.inventory.sales.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.acct.model.LedgerModel;
import com.webspark.common.util.SConstants;
import com.webspark.model.S_LoginModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Jinshad P.T.
 * 
 * @Date Feb 14, 2014
 */

@Entity
@Table(name = SConstants.tb_names.LAUNDRY_SALES)
public class LaundrySalesModel implements Serializable {

	private static final long serialVersionUID = -7772485934441974718L;

	public LaundrySalesModel() {
		super();
		this.active = true;
	}

	public LaundrySalesModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}

	public LaundrySalesModel(LedgerModel customer, double payment_amount) {
		super();
		this.customer = customer;
		this.payment_amount = payment_amount;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "sales_number")
	private long sales_number;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "voucher_no")
	private long voucher_no;

	@Column(name = "date")
	private Date date;

	@Column(name = "created_time")
	private Timestamp created_time;

	@Column(name = "expected_delivery_time")
	private Timestamp expected_delivery_time;

	@Column(name = "actual_delivery_time")
	private Timestamp actual_delivery_time;

	@Column(name = "responsible_person")
	private long responsible_person;

	@OneToOne
	@JoinColumn(name = "customer_ledger_id")
	private LedgerModel customer;

	@Column(name = "shipping_charge")
	private double shipping_charge;

	@Column(name = "excise_duty")
	private double excise_duty;

	@Column(name = "advance_amount")
	private double advance_amount;

	@Column(name = "payment_amount")
	private double payment_amount;

	@Column(name = "paid_by_payment", columnDefinition = "double default 0", nullable = false)
	private double paid_by_payment;

	@Column(name = "payment_done", columnDefinition = "char default 'N'", nullable = false)
	private char payment_done;

	@Column(name = "amount")
	private double amount;

	@Column(name = "status")
	private long status;

	@Column(name = "type", length = 1)
	private int type;

	@Column(name = "sales_person")
	private long sales_person;

	@Column(name = "credit_period")
	private int credit_period;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active = true;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;

	@Column(name = "delivared")
	private boolean delivared;

	@Column(name = "room_no")
	private String room_no;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "laundry_sales_details_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "details_id") })
	private List<LaundrySalesDetailsModel> details_list = new ArrayList<LaundrySalesDetailsModel>();

	@Column(name = "comments")
	private String comments;

	public LaundrySalesModel(long sales_number, double payment_amount,
			double amount) {
		super();
		this.sales_number = sales_number;
		this.payment_amount = payment_amount;
		this.amount = amount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getVoucher_no() {
		return voucher_no;
	}

	public void setVoucher_no(long voucher_no) {
		this.voucher_no = voucher_no;
	}

	public long getSales_number() {
		return sales_number;
	}

	public void setSales_number(long sales_number) {
		this.sales_number = sales_number;
	}

	public LedgerModel getCustomer() {
		return customer;
	}

	public void setCustomer(LedgerModel customer) {
		this.customer = customer;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public S_LoginModel getLogin() {
		return login;
	}

	public void setLogin(S_LoginModel login) {
		this.login = login;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public S_OfficeModel getOffice() {
		return office;
	}

	public void setOffice(S_OfficeModel office) {
		this.office = office;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getShipping_charge() {
		return shipping_charge;
	}

	public void setShipping_charge(double shipping_charge) {
		this.shipping_charge = shipping_charge;
	}

	public double getExcise_duty() {
		return excise_duty;
	}

	public void setExcise_duty(double excise_duty) {
		this.excise_duty = excise_duty;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public double getPayment_amount() {
		return payment_amount;
	}

	public void setPayment_amount(double payment_amount) {
		this.payment_amount = payment_amount;
	}

	public long getSales_person() {
		return sales_person;
	}

	public void setSales_person(long sales_person) {
		this.sales_person = sales_person;
	}

	public int getCredit_period() {
		return credit_period;
	}

	public void setCredit_period(int credit_period) {
		this.credit_period = credit_period;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getResponsible_person() {
		return responsible_person;
	}

	public void setResponsible_person(long responsible_person) {
		this.responsible_person = responsible_person;
	}

	public Timestamp getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Timestamp created_time) {
		this.created_time = created_time;
	}

	public Timestamp getExpected_delivery_time() {
		return expected_delivery_time;
	}

	public void setExpected_delivery_time(Timestamp expected_delivery_time) {
		this.expected_delivery_time = expected_delivery_time;
	}

	public Timestamp getActual_delivery_time() {
		return actual_delivery_time;
	}

	public void setActual_delivery_time(Timestamp actual_delivery_time) {
		this.actual_delivery_time = actual_delivery_time;
	}

	public double getAdvance_amount() {
		return advance_amount;
	}

	public void setAdvance_amount(double advance_amount) {
		this.advance_amount = advance_amount;
	}

	public List<LaundrySalesDetailsModel> getDetails_list() {
		return details_list;
	}

	public void setDetails_list(List<LaundrySalesDetailsModel> details_list) {
		this.details_list = details_list;
	}

	public boolean isDelivared() {
		return delivared;
	}

	public void setDelivared(boolean delivared) {
		this.delivared = delivared;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getRoom_no() {
		return room_no;
	}

	public void setRoom_no(String room_no) {
		this.room_no = room_no;
	}

	public double getPaid_by_payment() {
		return paid_by_payment;
	}

	public void setPaid_by_payment(double paid_by_payment) {
		this.paid_by_payment = paid_by_payment;
	}

	public char getPayment_done() {
		return payment_done;
	}

	public void setPayment_done(char payment_done) {
		this.payment_done = payment_done;
	}
}
