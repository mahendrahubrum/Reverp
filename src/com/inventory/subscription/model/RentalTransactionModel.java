package com.inventory.subscription.model;

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

/***
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * Jan 24, 2015
 */

@SuppressWarnings("serial")
@Entity
@Table(name = SConstants.tb_names.I_RENTAL_TRANSACTION)
public class RentalTransactionModel implements Serializable {


	public RentalTransactionModel() {
	
	}
	
	public RentalTransactionModel(long id, String comments) {
		super();
		this.id = id;
		this.comments = comments;
	}

	public RentalTransactionModel(LedgerModel customer, double payment_amount) {
		super();
		this.customer = customer;
		this.payment_amount = payment_amount;
	}

	public RentalTransactionModel(long sales_number, double payment_amount, double amount) {
		super();
		this.sales_number = sales_number;
		this.payment_amount = payment_amount;
		this.amount = amount;
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@Column(name = "sales_number")
	private long sales_number;

	@Column(name = "transaction_id")
	private long transaction_id;

	@Column(name = "date")
	private Date date;

	@Column(name = "created_time")
	private Timestamp created_time;

	@Column(name = "responsible_person")
	private long responsible_person;

	@OneToOne
	@JoinColumn(name = "customer_ledger_id")
	private LedgerModel customer;

	@Column(name = "shipping_charge")
	private double shipping_charge;

	@Column(name = "excise_duty")
	private double excise_duty;

	@Column(name = "payment_amount")
	private double payment_amount;

	@Column(name = "amount")
	private double amount;

	@Column(name = "active", columnDefinition = "boolean default true", nullable = false)
	private boolean active;

	@Column(name = "rent_type")
	private long rent_type;

	@Column(name = "sales_person")
	private long sales_person;

	@Column(name = "sales_type", columnDefinition = "bigint default 0", nullable = false)
	private long sales_type;

	@Column(name = "credit_period")
	private int credit_period;

	@OneToOne
	@JoinColumn(name = "office_id")
	private S_OfficeModel office;

	@OneToOne
	@JoinColumn(name = "login_id")
	private S_LoginModel login;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "rental_inv_link", joinColumns = { @JoinColumn(name = "master_id") }, inverseJoinColumns = { @JoinColumn(name = "item_details_id") })
	private List<RentalTransactionDetailsModel> inventory_details_list = new ArrayList<RentalTransactionDetailsModel>();

	@Column(name = "comments")
	private String comments;

	@Column(name = "currency_id", columnDefinition = "bigint default 0", nullable = false)
	private long currencyId;
	
	@Column(name = "status")
	private long status;

	@Column(name = "foreign_currency_amount", columnDefinition = "double default 0", nullable = false)
	private double foreignCurrencyAmount;
	
	@Column(name = "sales_local_type", columnDefinition = "int default 0", nullable = false)
	private int sales_local_type;
	
	@Column(name = "payment_done", columnDefinition = "char default 'N'", nullable = false)
	private char payment_done;
	
	@Column(name = "paid_by_payment", columnDefinition = "double default 0", nullable = false)
	private double paid_by_payment;
	
	public char getPayment_done() {
		return payment_done;
	}

	public void setPayment_done(char payment_done) {
		this.payment_done = payment_done;
	}

	public double getPaid_by_payment() {
		return paid_by_payment;
	}

	public void setPaid_by_payment(double paid_by_payment) {
		this.paid_by_payment = paid_by_payment;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}
	
	public int getSales_local_type() {
		return sales_local_type;
	}

	public void setSales_local_type(int sales_local_type) {
		this.sales_local_type = sales_local_type;
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

	public List<RentalTransactionDetailsModel> getInventory_details_list() {
		return inventory_details_list;
	}

	public void setInventory_details_list(
			List<RentalTransactionDetailsModel> inventory_details_list) {
		this.inventory_details_list = inventory_details_list;
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

	
	public long getRent_type() {
		return rent_type;
	}

	public void setRent_type(long rent_type) {
		this.rent_type = rent_type;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getSales_type() {
		return sales_type;
	}

	public void setSales_type(long sales_type) {
		this.sales_type = sales_type;
	}

	public double getForeignCurrencyAmount() {
		return foreignCurrencyAmount;
	}

	public void setForeignCurrencyAmount(double foreignCurrencyAmount) {
		this.foreignCurrencyAmount = foreignCurrencyAmount;
	}

	public long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(long currencyId) {
		this.currencyId = currencyId;
	}

}
