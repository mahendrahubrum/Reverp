package com.inventory.payment.ui;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vaadin.data.collectioncontainer.CollectionContainer;
import org.vaadin.dialogs.ConfirmDialog;

import com.inventory.config.acct.dao.CustomerDao;
import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.CustomerModel;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.payment.dao.LaundryPaymentDao;
import com.inventory.payment.model.PaymentModel;
import com.inventory.sales.dao.LaundrySalesDao;
import com.inventory.sales.model.LaundrySalesModel;
import com.inventory.transaction.biz.FinTransaction;
import com.inventory.transaction.model.TransactionModel;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.webspark.Components.DocumentAttach;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SConfirmWithCommonds;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHelpPopupView;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SListSelect;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SRadioButton;
import com.webspark.Components.STextArea;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SWindow;
import com.webspark.Components.SparkLogic;
import com.webspark.Components.WindowNotifications;
import com.webspark.common.util.CommonUtil;
import com.webspark.common.util.SConstants;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.mailclient.ui.ComposeMailUI;
import com.webspark.mailclient.ui.ShowEmailsUI;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.model.S_OfficeModel;

/**
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Aug 26, 2013
 */
public class LaundryCustomerPaymentsUI extends SparkLogic {

	private static final long serialVersionUID = 8774283694107704131L;

	private SComboField paymentIdComboField;
	private SComboField customerComboField;
	private SComboField toAccountComboField;
	private SDateField dateField;
	private SDateField chequeDateField;
	private STextField chequenumberField;
	private STextField customerAmountField;
	private STextField discountField;
	private STextField paymentAmountField;
	private STextArea descriptionField;
	private SComboField currencyComboField;

	SWindow sendWindow, showWindow;
	private SButton sendMailButton, showMails;

	private SButton saveButton;
	private SButton updateButton;
	private SButton deleteButton;
	private SButton cancelButton;

	SListSelect salesSelect;

	private SCollectionContainer container;

	private LaundryPaymentDao paymentDao;
	private LaundrySalesDao salesDao;

	private SettingsValuePojo settings;

	private WrappedSession session;

	private long paymentId = 0;

	private DocumentAttach docAttach;

	SButton createNewButton;
	SRadioButton cashOrCheck;

	LedgerDao ledgerDao;

	private SDateField filterFromField;
	private SDateField filterToField;
	private STextField saleTotalAmountField;
	private STextField returnTotalAmountField;
	private STextField billTotalAmountField;
	
	WindowNotifications windowNotif;
	SConfirmWithCommonds confirmBox;
	SHorizontalLayout popupLay;
	SHelpPopupView helpPopup;

	@Override
	public SPanel getGUI() {
		
		windowNotif=new WindowNotifications();
		helpPopup=new SHelpPopupView("");
		popupLay=new SHorizontalLayout();
		popupLay.addComponent(helpPopup);
		confirmBox=new SConfirmWithCommonds("Confirm..?", getOfficeID());
		
		
		setSize(820, 540);
		SPanel panel = new SPanel();
		panel.setSizeFull();

		ledgerDao = new LedgerDao();

		cashOrCheck = new SRadioButton(getPropertyName("payment_type"), 200,
				SConstants.cashOrCheckList, "intKey", "value");
		cashOrCheck.setHorizontal(true);
		cashOrCheck.setValue(1);

		createNewButton = new SButton();
		createNewButton.setStyleName("createNewBtnStyle");
		createNewButton.setDescription("Add New Receipt");

		sendMailButton = new SButton(getPropertyName("send_mail"));
		sendMailButton.setIcon(new ThemeResource("icons/sendmail.png"));
		sendMailButton.setStyleName("deletebtnStyle");

		showMails = new SButton(getPropertyName("show_mails"));
		showMails.setIcon(new ThemeResource("icons/sendmail.png"));
		showMails.setStyleName("deletebtnStyle");

		session = getHttpSession();

		salesDao = new LaundrySalesDao();

		if (session.getAttribute("settings") != null)
			settings = (SettingsValuePojo) session.getAttribute("settings");

		paymentDao = new LaundryPaymentDao();

		salesSelect = new SListSelect(getPropertyName("sales_number"), 200,
				null, "id", "comments");
		salesSelect.setImmediate(true);
		salesSelect.setMultiSelect(true);
		salesSelect.setNullSelectionAllowed(true);

		docAttach = new DocumentAttach(SConstants.documentAttach.CHEQUE);
		docAttach.setVisible(false);

		SFormLayout mainFormLayout = new SFormLayout();
		mainFormLayout.setMargin(true);

		SFormLayout customerFormLayout = new SFormLayout();

		SFormLayout amountFormLayout = new SFormLayout();
		amountFormLayout.setMargin(true);

		SGridLayout gridLayout = new SGridLayout();
		gridLayout.setColumns(2);
		gridLayout.setRows(1);
		gridLayout.setSpacing(true);
		gridLayout.setStyleName("master_border");

		SGridLayout buttonGridLayout = new SGridLayout();
		buttonGridLayout.setColumns(8);
		buttonGridLayout.setRows(1);
		buttonGridLayout.setSpacing(true);
		buttonGridLayout.setSizeFull();

		SHorizontalLayout buttonFormLayout = new SHorizontalLayout();
		buttonFormLayout.setSpacing(true);

		paymentIdComboField = new SComboField(null, 200);
		paymentIdComboField
				.setInputPrompt("---------------Create New---------------");
		loadPaymentNo(0);

		customerComboField = new SComboField(getPropertyName("customer"), 200);
		customerComboField
				.setInputPrompt("---------------Select---------------");
		loadCustomerCombo();

		toAccountComboField = new SComboField(getPropertyName("to_account"),
				200);
		toAccountComboField
				.setInputPrompt("---------------Select---------------");
		loadAccountCombo();

		dateField = new SDateField(getPropertyName("date"), 100);
		dateField.setValue(getWorkingDate());
		chequeDateField = new SDateField(getPropertyName("cheque_date"), 100);
		chequeDateField.setValue(getWorkingDate());
		chequeDateField.setVisible(false);
		chequenumberField = new STextField(getPropertyName("cheuqe_no"), 200);
		chequenumberField.setVisible(false);
		customerAmountField = new STextField(
				getPropertyName("customer_amount"), 200);
		customerAmountField.setStyleName("textfield_align_right");
		customerAmountField.setValue("0.00");
		discountField = new STextField(getPropertyName("discount"), 200);
		discountField.setStyleName("textfield_align_right");
		discountField.setValue("0.00");
		paymentAmountField = new STextField(getPropertyName("payment_amount"),
				200);
		paymentAmountField.setStyleName("textfield_align_right");
		paymentAmountField.setValue("0.00");
		paymentAmountField.setReadOnly(true);
		descriptionField = new STextArea(getPropertyName("description"), 200,
				30);
		currencyComboField = new SComboField(getPropertyName("currency"), 200);
		loadCurrecny();

		saleTotalAmountField = new STextField(
				getPropertyName("total_bill_amount"), 200);
		saleTotalAmountField.setStyleName("textfield_align_right");
		saleTotalAmountField.setValue("0.00");
		saleTotalAmountField.setReadOnly(true);

		returnTotalAmountField = new STextField("Total Paid Amount", 200);
		returnTotalAmountField.setStyleName("textfield_align_right");
		returnTotalAmountField.setValue("0.00");
		returnTotalAmountField.setReadOnly(true);

		billTotalAmountField = new STextField("Balance Amount", 200);
		billTotalAmountField.setStyleName("textfield_align_right");
		billTotalAmountField.setValue("0.00");
		billTotalAmountField.setReadOnly(true);

		filterFromField = new SDateField(getPropertyName("from_date"), 100,
				getDateFormat(), getWorkingDate());
		filterFromField.setImmediate(true);
		filterToField = new SDateField(getPropertyName("to_date"), 100,
				getDateFormat(), getWorkingDate());
		filterToField.setImmediate(true);

		SHorizontalLayout dateLay = new SHorizontalLayout();
		dateLay.addComponent(filterFromField);
		dateLay.addComponent(filterToField);

		SHorizontalLayout salLisrLay = new SHorizontalLayout(
				getPropertyName("payment_no"));
		salLisrLay.addComponent(paymentIdComboField);
		salLisrLay.addComponent(createNewButton);

		customerFormLayout.addComponent(salLisrLay);
		customerFormLayout.addComponent(customerComboField);
		customerFormLayout.addComponent(cashOrCheck);
		customerFormLayout.addComponent(toAccountComboField);
		customerFormLayout.addComponent(dateLay);
		customerFormLayout.addComponent(salesSelect);
		customerFormLayout.addComponent(saleTotalAmountField);
		customerFormLayout.addComponent(returnTotalAmountField);
		customerFormLayout.addComponent(billTotalAmountField);

		salesSelect.setHeight("80");

		amountFormLayout.addComponent(dateField);
		amountFormLayout.addComponent(chequeDateField);
		amountFormLayout.addComponent(chequenumberField);
		amountFormLayout.addComponent(customerAmountField);
		amountFormLayout.addComponent(discountField);
		amountFormLayout.addComponent(paymentAmountField);
		amountFormLayout.addComponent(descriptionField);
		amountFormLayout.addComponent(currencyComboField);
		amountFormLayout.addComponent(docAttach);

		gridLayout.addComponent(customerFormLayout);
		gridLayout.addComponent(amountFormLayout);

		saveButton = new SButton(getPropertyName("save"));
		saveButton.setStyleName("savebtnStyle");
		saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
		updateButton = new SButton(getPropertyName("update"));
		updateButton.setVisible(false);
		updateButton.setIcon(new ThemeResource("icons/updateSideIcon.png"));
		updateButton.setStyleName("updatebtnStyle");
		deleteButton = new SButton(getPropertyName("delete"));
		deleteButton.setVisible(false);
		deleteButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		deleteButton.setStyleName("deletebtnStyle");
		cancelButton = new SButton(getPropertyName("cancel"));
		cancelButton.setVisible(false);
		cancelButton.setIcon(new ThemeResource("icons/deleteSideIcon.png"));
		cancelButton.setStyleName("deletebtnStyle");

		buttonFormLayout.addComponent(saveButton);
		buttonFormLayout.addComponent(updateButton);

		if (settings.isKEEP_DELETED_DATA())
			buttonFormLayout.addComponent(cancelButton);
		else
			buttonFormLayout.addComponent(deleteButton);

		buttonFormLayout.addComponent(sendMailButton);
		buttonFormLayout.addComponent(showMails);
		buttonGridLayout.addComponent(buttonFormLayout, 4, 0);

		mainFormLayout.addComponent(gridLayout);
		mainFormLayout.addComponent(buttonGridLayout);

		sendMailButton.setVisible(false);
		showMails.setVisible(false);
		
		
		ClickListener confirmListener=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if(event.getButton().getId().equals("1")) {
					try {
						saveReportedIssue(getOptionId(),confirmBox.getComments(),(Long)paymentIdComboField.getValue(),confirmBox.getUserID());
						Notification.show("Success",
								"Session Saved Successfully..!",
								Type.WARNING_MESSAGE);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				confirmBox.close();
			}
		};
		
		confirmBox.setClickListener(confirmListener);
		
		ClickListener clickListnr=new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if(event.getButton().getId().equals(windowNotif.SAVE_SESSION)) {
					if(paymentIdComboField.getValue()!=null && !paymentIdComboField.getValue().toString().equals("0")) {
						saveSessionActivity(getOptionId(), (Long)paymentIdComboField.getValue(),
								"Customer Payment : No. "+paymentIdComboField.getItemCaption(paymentIdComboField.getValue()));
						Notification.show("Success",
								"Session Saved Successfully..!",
								Type.WARNING_MESSAGE);
					}
					else
						Notification.show("Select an Invoice..!",
								"Select an Invoice for save in session",
								Type.HUMANIZED_MESSAGE);
				}
				else if(event.getButton().getId().equals(windowNotif.REPORT_ISSUE)) {
					if(paymentIdComboField.getValue()!=null && !paymentIdComboField.getValue().toString().equals("0")) {
						confirmBox.open();
					}
					else
						Notification.show("Select an Invoice..!", "Select an Invoice for Save in session",
								Type.HUMANIZED_MESSAGE);
				}
				else {
					try {
						helpPopup=new SHelpPopupView(getOptionId());
						popupLay.removeAllComponents();
						popupLay.addComponent(helpPopup);
						helpPopup.setPopupVisible(true);
						helpPopup.setHideOnMouseOut(false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		
		windowNotif.setClickListener(clickListnr);

		cashOrCheck.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent arg0) {
				try {
					if ((Integer) cashOrCheck.getValue() == 1) {
						docAttach.setVisible(false);
						chequeDateField.setVisible(false);
						chequenumberField.setVisible(false);
					} else {
						docAttach.setVisible(true);
						chequeDateField.setVisible(true);
						chequenumberField.setVisible(true);
					}
					loadAccountCombo();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		filterFromField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				loadBillNumbers((Long) customerComboField.getValue(),
						CommonUtil.getSQLDateFromUtilDate(filterFromField
								.getValue()), CommonUtil
								.getSQLDateFromUtilDate(filterToField
										.getValue()));
			}
		});

		filterToField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				loadBillNumbers((Long) customerComboField.getValue(),
						CommonUtil.getSQLDateFromUtilDate(filterFromField
								.getValue()), CommonUtil
								.getSQLDateFromUtilDate(filterToField
										.getValue()));
			}
		});

		salesSelect.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent arg0) {
				try {
					Set set = (Set) salesSelect.getValue();
					if (set != null && set.size() > 0) {
						long purchaseId = 0;
						double amount = 0, paidAmy=0;
						LaundrySalesModel mdl;
						Iterator itr = set.iterator();
						while (itr.hasNext()) {
							purchaseId = (Long) itr.next();
							mdl = salesDao.getSale(purchaseId);
							amount += mdl.getAmount();
							paidAmy += mdl.getPayment_amount()+mdl.getPaid_by_payment();
						}
						 customerAmountField.setValue((amount-paidAmy) + "");
						 saleTotalAmountField.setNewValue(amount + "");
						 returnTotalAmountField.setNewValue(paidAmy + "");
						 billTotalAmountField.setNewValue((amount-paidAmy) + "");
					} else {
						 customerAmountField.setValue("0");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		toAccountComboField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							if (toAccountComboField.getValue() != null) {
								toAccountComboField.setDescription("<i class='ledger_bal_style'>Current Balance : "
										+ roundNumber(ledgerDao
												.getLedgerCurrentBalance((Long) toAccountComboField
														.getValue())) + "</i>");
							} else
								toAccountComboField.setDescription(null);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		createNewButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				paymentIdComboField.setValue((long) 0);
			}
		});

		customerComboField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				if (customerComboField.getValue() != null
						&& !customerComboField.getValue().equals("")
						&& !customerComboField.getValue().toString()
								.equals("0")) {
					try {

						customerComboField.setDescription("<i class='ledger_bal_style'>Current Balance : "
								+ roundNumber(ledgerDao
										.getLedgerCurrentBalance((Long) customerComboField
												.getValue())) + "</i>");

					} catch (Exception e) {
						e.printStackTrace();
					}

					sendMailButton.setVisible(true);
					showMails.setVisible(true);

				} else {
					customerComboField.setDescription(null);
					sendMailButton.setVisible(false);
					showMails.setVisible(false);
					salesSelect.setValue(null);
				}

				if (customerComboField.getValue() != null && !customerComboField.getValue().equals(""))
					loadBillNumbers((Long) customerComboField.getValue(),
							CommonUtil.getSQLDateFromUtilDate(filterFromField.getValue()), 
							CommonUtil.getSQLDateFromUtilDate(filterToField.getValue()));

			}
		});

		sendMailButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (customerComboField.getValue() != null) {
					try {
						CustomerModel led =new CustomerDao()
						.getCustomer((Long) customerComboField
								.getValue());
						if (led.getAddress() != null
								&& led.getAddress().getEmail() != null
								&& led.getAddress().getEmail().length() > 3)
							sendWindow = new ComposeMailUI(led.getAddress()
									.getEmail());
						else
							sendWindow = new ComposeMailUI();
						sendWindow.center();
						sendWindow.setModal(true);
						getUI().addWindow(sendWindow);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		showMails.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (customerComboField.getValue() != null) {
					try {
						CustomerModel led =new CustomerDao()
						.getCustomer((Long) customerComboField
								.getValue());
						if (led.getAddress() != null
								&& led.getAddress().getEmail() != null
								&& led.getAddress().getEmail().length() > 3) {
							showWindow = new ShowEmailsUI(led.getAddress()
									.getEmail());
							showWindow.center();
							showWindow.setModal(true);
							getUI().addWindow(showWindow);
						} else {
							SNotification.show(
									getPropertyName("email_not_found"),
									Type.WARNING_MESSAGE);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (isValid()) {
						long toAccId = toLong(toAccountComboField.getValue()
								.toString());
						long fromAccId = toLong(customerComboField.getValue()
								.toString());

						PaymentModel paymentModel = new PaymentModel();
						paymentModel.setPayment_id(getNextSequence(
								"Customer_Payment_Id", getLoginID()));
						paymentModel
								.setCurrency(new CurrencyModel(
										toLong(currencyComboField.getValue()
												.toString())));
						paymentModel.setDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setCheque_date(CommonUtil
								.getSQLDateFromUtilDate(chequeDateField
										.getValue()));
						paymentModel.setChequeNo(chequenumberField.getValue());
						paymentModel.setDescription(descriptionField.getValue());
						paymentModel.setDiscount(toDouble(discountField
								.getValue()));
						paymentModel.setFrom_account_id(fromAccId);
						paymentModel.setTo_account_id(toAccId);
						paymentModel
								.setOffice(new S_OfficeModel(getOfficeID()));
						paymentModel
								.setPayment_amount(toDouble(paymentAmountField
										.getValue().toString()));
						paymentModel
								.setSupplier_amount(toDouble(customerAmountField
										.getValue().toString()));
						paymentModel.setType(SConstants.CUSTOMER_PAYMENTS);
						paymentModel.setActive(true);
						paymentModel.setCash_or_check((Integer) cashOrCheck
								.getValue());
						paymentModel.setFromDate(CommonUtil
								.getSQLDateFromUtilDate(filterFromField
										.getValue()));
						paymentModel.setToDate(CommonUtil
								.getSQLDateFromUtilDate(filterToField
										.getValue()));

						String salesIDs = "";
						Set<Long> options_selected = new HashSet<Long>();
						if (options_selected != null) {

							options_selected = (Set<Long>) salesSelect
									.getValue();
							Iterator it1 = options_selected.iterator();
							while (it1.hasNext()) {
								salesIDs += it1.next() + ",";

							}
						}
						paymentModel.setSales_ids(salesIDs);

						FinTransaction transaction = new FinTransaction();
						transaction.addTransaction(SConstants.DR, fromAccId,
								toAccId,
								roundNumber(toDouble(paymentAmountField
										.getValue().toString())));

						paymentId = paymentDao.saveCustomerPayment(
								paymentModel,
								transaction
										.getTransaction(
												SConstants.CUSTOMER_PAYMENTS,
												CommonUtil
														.getSQLDateFromUtilDate(dateField
																.getValue())),
								options_selected);

						docAttach.saveDocument(paymentId, getOfficeID(),
								SConstants.CUSTOMER_PAYMENTS);

						loadPaymentNo(paymentId);

						saveActivity(
								getOptionId(),
								"New Customer Receipt. Payment No : "
										+ paymentModel.getPayment_id()
										+ ", Customer : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue())
										+ ", Payment Amount : "
										+ roundNumber(toDouble(paymentAmountField
												.getValue().toString())),paymentModel.getId());

						saveButton.setVisible(false);
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						cancelButton.setVisible(true);

						SNotification.show(getPropertyName("save_success"),
								Type.WARNING_MESSAGE);
					}
				} catch (Exception e) {
					SNotification.show(getPropertyName("error"),
							Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});

		updateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				try {
					if (isValid()) {

						long toAccId = toLong(toAccountComboField.getValue()
								.toString());
						long fromAccId = toLong(customerComboField.getValue()
								.toString());

						PaymentModel paymentModel = paymentDao
								.getPaymentModel(toLong(paymentIdComboField
										.getValue().toString()));

						paymentModel
								.setCurrency(new CurrencyModel(
										toLong(currencyComboField.getValue()
												.toString())));
						paymentModel.setDate(CommonUtil
								.getSQLDateFromUtilDate(dateField.getValue()));
						paymentModel.setCheque_date(CommonUtil
								.getSQLDateFromUtilDate(chequeDateField
										.getValue()));
						paymentModel.setChequeNo(chequenumberField.getValue());
						paymentModel.setDescription(descriptionField.getValue());
						paymentModel.setDiscount(toDouble(discountField
								.getValue()));
						paymentModel.setFrom_account_id(fromAccId);
						paymentModel.setTo_account_id(toAccId);
						paymentModel
								.setOffice(new S_OfficeModel(getOfficeID()));
						paymentModel
								.setPayment_amount(toDouble(paymentAmountField
										.getValue().toString()));
						paymentModel
								.setSupplier_amount(toDouble(customerAmountField
										.getValue().toString()));
						paymentModel.setType(SConstants.CUSTOMER_PAYMENTS);
						paymentModel.setActive(true);
						paymentModel.setCash_or_check((Integer) cashOrCheck
								.getValue());

						paymentModel.setFromDate(CommonUtil
								.getSQLDateFromUtilDate(filterFromField
										.getValue()));
						paymentModel.setToDate(CommonUtil
								.getSQLDateFromUtilDate(filterToField
										.getValue()));

						String salesIDs = "";
						Set<Long> options_selected = new HashSet<Long>();
						if (options_selected != null) {
							options_selected = (Set<Long>) salesSelect
									.getValue();
							Iterator it1 = options_selected.iterator();
							while (it1.hasNext()) {
								salesIDs += it1.next() + ",";
							}
						}
						paymentModel.setSales_ids(salesIDs);

						FinTransaction trans = new FinTransaction();
						trans.addTransaction(SConstants.DR, fromAccId, toAccId,
								roundNumber(toDouble(paymentAmountField
										.getValue().toString())));
						TransactionModel tran = paymentDao
								.getTransaction(paymentModel
										.getTransaction_id());
						tran.setTransaction_details_list(trans.getChildList());
						tran.setDate(paymentModel.getDate());
						tran.setLogin_id(getLoginID());

						paymentDao.updateCustomerPayment(paymentModel, tran,
								options_selected);
						docAttach.saveDocument(toLong(paymentIdComboField
								.getValue().toString()), getOfficeID(),
								SConstants.CUSTOMER_PAYMENTS);

						saveActivity(
								getOptionId(),
								"Updated Customer Receipt. Payment No : "
										+ paymentModel.getPayment_id()
										+ ", Customer : "
										+ customerComboField
												.getItemCaption(customerComboField
														.getValue())
										+ ", Payment Amount : "
										+ roundNumber(toDouble(paymentAmountField
												.getValue().toString())),paymentModel.getId());

						loadPaymentNo(paymentModel.getId());

						saveButton.setVisible(false);
						updateButton.setVisible(true);
						deleteButton.setVisible(true);
						cancelButton.setVisible(true);

						SNotification.show(getPropertyName("update_success"),
								Type.WARNING_MESSAGE);

					}
				} catch (Exception e) {
					SNotification.show(getPropertyName("error"),
							Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});

		deleteButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {

										paymentDao
												.deleteCustomerPayment(toLong(paymentIdComboField
														.getValue().toString()));
										docAttach
												.deleteDocument(
														toLong(paymentIdComboField
																.getValue()
																.toString()),
														getOfficeID(),
														SConstants.CUSTOMER_PAYMENTS);

										saveActivity(
												getOptionId(),
												"Customer Receipt Deleted. Payment No : "
														+ paymentIdComboField
																.getItemCaption(paymentIdComboField
																		.getValue())
														+ ", Customer : "
														+ customerComboField
																.getItemCaption(customerComboField
																		.getValue())
														+ ", Payment Amount : "
														+ roundNumber(toDouble(paymentAmountField
																.getValue()
																.toString())),(Long)paymentIdComboField
																.getValue());

										loadPaymentNo(0);
										SNotification
												.show(getPropertyName("deleted_successf"),
														Type.WARNING_MESSAGE);

										saveButton.setVisible(true);
										updateButton.setVisible(false);
										deleteButton.setVisible(false);
										cancelButton.setVisible(false);

									} catch (Exception e) {
										SNotification.show(
												getPropertyName("error"),
												Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
			}
		});

		cancelButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(), getPropertyName("are_you_sure"),
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									try {

										paymentDao
												.cancelCustomerPayment(toLong(paymentIdComboField
														.getValue().toString()));
										docAttach
												.deleteDocument(
														toLong(paymentIdComboField
																.getValue()
																.toString()),
														getOfficeID(),
														SConstants.CUSTOMER_PAYMENTS);

										saveActivity(
												getOptionId(),
												"Customer Receipt Deleted. Payment No : "
														+ paymentIdComboField
																.getItemCaption(paymentIdComboField
																		.getValue())
														+ ", Customer : "
														+ customerComboField
																.getItemCaption(customerComboField
																		.getValue())
														+ ", Payment Amount : "
														+ roundNumber(toDouble(paymentAmountField
																.getValue()
																.toString())),(Long)paymentIdComboField
																.getValue());

										loadPaymentNo(0);
										SNotification
												.show(getPropertyName("deleted_success"),
														Type.WARNING_MESSAGE);

										saveButton.setVisible(true);
										updateButton.setVisible(false);
										deleteButton.setVisible(false);
										cancelButton.setVisible(false);

									} catch (Exception e) {
										SNotification.show(
												getPropertyName("error"),
												Type.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}
						});
			}
		});

		paymentIdComboField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {

				try {
					docAttach.clear();
					if (paymentIdComboField.getValue() != null
							&& !paymentIdComboField.getValue().equals("")
							&& !paymentIdComboField.getValue().toString()
									.equals("0")) {
						PaymentModel model = paymentDao
								.getPaymentModel(toLong(paymentIdComboField
										.getValue().toString()));

						if (model != null) {
							cashOrCheck.setValue(model.getCash_or_check());
							customerComboField.setValue(model
									.getFrom_account_id());
							customerComboField.setDescription("<i class='ledger_bal_style'>Current Balance : "
									+ roundNumber(ledgerDao
											.getLedgerCurrentBalance(model
													.getFrom_account_id()))
									+ "</i>");
							toAccountComboField.setValue(model
									.getTo_account_id());
							toAccountComboField.setDescription("<i class='ledger_bal_style'>Current Balance : "
									+ roundNumber(ledgerDao
											.getLedgerCurrentBalance(model
													.getTo_account_id()))
									+ "</i>");
							descriptionField.setValue(model.getDescription());
							dateField.setValue(model.getDate());
							currencyComboField.setValue(model.getCurrency()
									.getId());
							chequeDateField.setValue(model.getCheque_date());
							chequenumberField.setValue(model.getChequeNo());
							filterFromField.setValue(model.getFromDate());
							filterToField.setValue(model.getToDate());

							if (model.getSales_ids() != null
									&& !model.getSales_ids().equals("")) {
								Set<Long> val = new HashSet<Long>();
								String[] a = model.getSales_ids().split(",");
								if (a.length > 0) {
									for (int i = 0; i < a.length; i++) {
										val.add(toLong(a[i]));
									}
								}
								salesSelect.setValue(val);
							}

							customerAmountField.setValue(String.valueOf(model
									.getSupplier_amount()));
							discountField.setValue(String.valueOf(model
									.getDiscount()));
							paymentAmountField.setNewValue(String.valueOf(model
									.getPayment_amount()));

							saveButton.setVisible(false);
							updateButton.setVisible(true);
							deleteButton.setVisible(true);
							cancelButton.setVisible(true);

							docAttach.loadDocument(
									(Long) paymentIdComboField.getValue(),
									getOfficeID(), SConstants.CUSTOMER_PAYMENTS);
						}
					} else {
						customerComboField.setValue(null);
						customerComboField.setDescription(null);
						toAccountComboField.setValue(null);
						toAccountComboField.setDescription(null);
						descriptionField.setValue("");
						dateField.setValue(getWorkingDate());
						chequeDateField.setValue(getWorkingDate());
						chequenumberField.setValue("");
						customerAmountField.setValue("0.00");
						discountField.setValue("0.00");
						paymentAmountField.setNewValue("0.00");
						currencyComboField.setValue(getCurrencyID());
						salesSelect.setValue(null);
						cashOrCheck.setValue((int) 1);
						filterFromField.setValue(getWorkingDate());
						filterToField.setValue(getWorkingDate());

						saveButton.setVisible(true);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
					}

					if (!isFinYearBackEntry()) {
						saveButton.setVisible(false);
						updateButton.setVisible(false);
						deleteButton.setVisible(false);
						cancelButton.setVisible(false);
						if (paymentIdComboField.getValue() == null
								|| paymentIdComboField.getValue().toString()
										.equals("0")) {
							Notification.show(
									getPropertyName("warning_transaction"),
									Type.WARNING_MESSAGE);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		customerAmountField.setImmediate(true);

		customerAmountField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							calculateNetPrice();
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show(getPropertyName("error"),
									Type.ERROR_MESSAGE);
						}

					}
				});

		discountField.setImmediate(true);

		discountField
				.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						try {
							calculateNetPrice();
						} catch (Exception e) {
							e.printStackTrace();
							Notification.show(getPropertyName("error"),
									Type.ERROR_MESSAGE);
						}

					}
				});

		if (!isFinYearBackEntry()) {
			saveButton.setVisible(false);
			updateButton.setVisible(false);
			deleteButton.setVisible(false);
			cancelButton.setVisible(false);
			Notification.show(getPropertyName("warning_transaction"),
					Type.WARNING_MESSAGE);
		}
		
		
		SVerticalLayout hLayout=new SVerticalLayout();
		hLayout.addComponent(popupLay);
		hLayout.addComponent(mainFormLayout);

		windowNotif.addComponent(hLayout, "left: 0px; right: 0px; z-index:-1;");
		panel.setContent(windowNotif);
		return panel;
	}

	public void calculateNetPrice() {
		double custAmt = 0, payAmt = 0, disc = 0;

		try {
			custAmt = Double.parseDouble(customerAmountField.getValue());
			disc = Double.parseDouble(discountField.getValue());
		} catch (Exception e) {
		}
		paymentAmountField.setNewValue(asString(custAmt - disc));
	}

	public void loadPaymentNo(long paymentId) {
		List<Object> list = new ArrayList<Object>();
		try {
			PaymentModel paymentModel = new PaymentModel();
			paymentModel.setId(0);
			paymentModel
					.setDescription("---------------Create New---------------");
			list.add(0, paymentModel);
			list.addAll(paymentDao.getPaymnetNo(getOfficeID(),
					SConstants.CUSTOMER_PAYMENTS));
			container = SCollectionContainer.setList(list, "id");
			paymentIdComboField.setContainerDataSource(container);
			paymentIdComboField.setItemCaptionPropertyId("description");
			if (paymentId > 0) {
				paymentIdComboField.setValue(paymentId);
			} else {
				paymentIdComboField.setValue(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void loadCurrecny() {
		List<Object> list = null;
		try {
			list = new CurrencyManagementDao().getlabels();
			container = SCollectionContainer.setList(list, "id");
			currencyComboField.setContainerDataSource(container);
			currencyComboField.setItemCaptionPropertyId("name");
			currencyComboField.setValue(getCurrencyID());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadAccountCombo() {
		List<Object> list = null;
		try {
			List groupList = new ArrayList();
			if ((Integer) cashOrCheck.getValue() == 1)
				groupList.add(settings.getCASH_GROUP());
			else
				groupList.add(settings.getCASH_GROUP());
			list = new LedgerDao()
					.getAllLedgersUnderGroupAndSubGroupsFromGroupList(
							getOfficeID(), getOrganizationID(), groupList);

			container = SCollectionContainer.setList(list, "id");
			toAccountComboField.setContainerDataSource(container);
			toAccountComboField.setItemCaptionPropertyId("name");
			toAccountComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadCustomerCombo() {
		List<Object> suppList = null;
		try {
			suppList = new LedgerDao().getAllCustomers(getOfficeID());
			container = SCollectionContainer.setList(suppList, "id");
			customerComboField.setContainerDataSource(container);
			customerComboField.setItemCaptionPropertyId("name");
			customerComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Boolean isValid() {
		clearErrorMessages();

		boolean valid = true;
		if (customerComboField.getValue() == null
				|| customerComboField.getValue().equals("")) {
			setRequiredError(customerComboField,
					getPropertyName("select_customer"), true);
			valid = false;
		}
		if (toAccountComboField.getValue() == null
				|| toAccountComboField.getValue().equals("")) {
			setRequiredError(toAccountComboField,
					getPropertyName("select_account"), true);
			valid = false;
		}

		if (!isValidAmount(customerAmountField)) {
			valid = false;
		}
		if (!isValidAmount(discountField)) {
			valid = false;
		}
		if (!isValidAmount(paymentAmountField)) {
			valid = false;
		}
		if(valid&&toDouble(paymentAmountField.getValue().toString())<=0){
			valid = false;
			setRequiredError(paymentAmountField, getPropertyName("invalid_data"), true);
		}

		if (settings.isPAYMENT_BILL_SELECTION_MANDATORY()) {
			if (((Set) salesSelect.getValue()).size() <= 0) {
				setRequiredError(salesSelect, "Select a bill no", true);
				valid = false;
			}

		}

		return valid;
	}

	private void clearErrorMessages() {
		customerComboField.setComponentError(null);
		toAccountComboField.setComponentError(null);
		customerAmountField.setComponentError(null);
		discountField.setComponentError(null);
		paymentAmountField.setComponentError(null);
		salesSelect.setComponentError(null);
	}

	private boolean isValidAmount(STextField field) {
		boolean valid = true;
		if (field.getValue() == null || field.getValue().equals("")) {
			setRequiredError(field, getPropertyName("enter_valid_amount"), true);
			valid = false;
		} else {
			try {
				Double.parseDouble(field.getValue().toString());
				valid = true;
			} catch (Exception e) {
				setRequiredError(field, getPropertyName("enter_valid_amount"),
						true);
				e.printStackTrace();
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void loadBillNumbers(Long ledgerId, Date fromDate, Date toDate) {
		ArrayList lst = new ArrayList();
		try {
			salesSelect.removeAllItems();
			saleTotalAmountField.setNewValue("0.00");
			billTotalAmountField.setNewValue("0.00");
			returnTotalAmountField.setNewValue("0.00");
			if (ledgerId != null && ledgerId != 0) {
				LaundrySalesModel mdl = null;
				double amount = 0;
				double returnAmount = 0;
				double totalAmount = 0;
				
				if (paymentIdComboField.getValue() != null && 
					!paymentIdComboField.getValue().equals("") && 
					!paymentIdComboField.getValue().toString().equals("0")) {
					
					lst.addAll(salesDao.getAllSalesIDsForCustomer(ledgerId, fromDate, toDate, false));
				} else {
					lst.addAll(salesDao.getAllSalesIDsForCustomer(ledgerId, fromDate, toDate, true));
				}
				System.out.println("List Size "+lst.size());
				CollectionContainer bic = CollectionContainer.fromBeans(lst,"id");
				salesSelect.setContainerDataSource(bic);
				salesSelect.setItemCaptionPropertyId("comments");
				HashSet set = new HashSet();
				Iterator iter = lst.iterator();
				while (iter.hasNext()) {
					mdl = (LaundrySalesModel) iter.next();
					set.add(mdl.getId());
					amount += toDouble(mdl.getComments().substring(
							mdl.getComments().indexOf('(') + 1,
							mdl.getComments().indexOf(')')));
				}
				salesSelect.setValue(set);

				returnAmount = paymentDao.getSalesReturnAmount(ledgerId,
						fromDate, toDate);
				totalAmount = amount - returnAmount;

				saleTotalAmountField.setNewValue(amount + "");
				returnTotalAmountField.setNewValue(returnAmount + "");
				billTotalAmountField.setNewValue(totalAmount + "");
				// customerAmountField.setNewValue(totalAmount+"");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public SComboField getPaymentIdComboField() {
		return paymentIdComboField;
	}

	public void setPaymentIdComboField(SComboField paymentIdComboField) {
		this.paymentIdComboField = paymentIdComboField;
	}
	@Override
	public SComboField getBillNoFiled() {
		return paymentIdComboField;
	}

}
