package com.hotel.service.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.inventory.config.stock.model.ItemModel;
import com.inventory.config.tax.model.TaxModel;
import com.inventory.config.unit.model.UnitModel;
import com.webspark.common.util.SConstants;

/**
 * 
 * @author Anil K P
 * 
 * WebSpark.
 *
 * 25-Sep-2015
 */
@Entity
@Table(name = SConstants.tb_names.H_HOTEL_SALES_INVENTORY_DETAILS)
public class HotelSalesInventoryDetailsModel implements Serializable {

	private static final long serialVersionUID = -8848075054976232280L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;

	@OneToOne
	@JoinColumn(name = "item_id")
	private ItemModel item;

	@Column(name = "quantity")
	private double qunatity;

	@Column(name = "quantity_in_basic_unit")
	private double quantity_in_basic_unit;

	@OneToOne
	@JoinColumn(name = "unit_id")
	private UnitModel unit;

	@OneToOne
	@JoinColumn(name = "tax_id")
	private TaxModel tax;

	@Column(name = "tax_amount")
	private double tax_amount;

	@Column(name = "tax_percentage")
	private double tax_percentage;

	@Column(name = "balance")
	private double balance;

	@Column(name = "order_id")
	private long order_id;

	@Column(name = "unit_price")
	private double unit_price;

	@Column(name = "discount_amount")
	private double discount_amount;

	@Column(name = "cess_amount")
	private double cess_amount;

	@Column(name = "stock_ids", length = 300)
	private String stock_ids;

	@Column(name = "stk_id")
	private long stk_id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ItemModel getItem() {
		return item;
	}

	public void setItem(ItemModel item) {
		this.item = item;
	}

	public double getQunatity() {
		return qunatity;
	}

	public void setQunatity(double qunatity) {
		this.qunatity = qunatity;
	}

	public UnitModel getUnit() {
		return unit;
	}

	public void setUnit(UnitModel unit) {
		this.unit = unit;
	}

	public double getTax_amount() {
		return tax_amount;
	}

	public void setTax_amount(double tax_amount) {
		this.tax_amount = tax_amount;
	}

	public double getTax_percentage() {
		return tax_percentage;
	}

	public void setTax_percentage(double tax_percentage) {
		this.tax_percentage = tax_percentage;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(long order_id) {
		this.order_id = order_id;
	}

	public double getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}

	public double getDiscount_amount() {
		return discount_amount;
	}

	public void setDiscount_amount(double discount_amount) {
		this.discount_amount = discount_amount;
	}

	public TaxModel getTax() {
		return tax;
	}

	public void setTax(TaxModel tax) {
		this.tax = tax;
	}

	public double getCess_amount() {
		return cess_amount;
	}

	public void setCess_amount(double cess_amount) {
		this.cess_amount = cess_amount;
	}

	public double getQuantity_in_basic_unit() {
		return quantity_in_basic_unit;
	}

	public void setQuantity_in_basic_unit(double quantity_in_basic_unit) {
		this.quantity_in_basic_unit = quantity_in_basic_unit;
	}

	public String getStock_ids() {
		return stock_ids;
	}

	public void setStock_ids(String stock_ids) {
		this.stock_ids = stock_ids;
	}

	public long getStk_id() {
		return stk_id;
	}

	public void setStk_id(long stk_id) {
		this.stk_id = stk_id;
	}

}
