package com.inventory.reports.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.inventory.config.acct.dao.LedgerDao;
import com.inventory.config.acct.model.LedgerModel;
import com.inventory.config.settings.bean.SettingsValuePojo;
import com.inventory.config.stock.dao.ItemDao;
import com.inventory.config.stock.dao.ItemGroupDao;
import com.inventory.config.stock.model.ItemModel;
import com.inventory.model.ItemGroupModel;
import com.inventory.purchase.dao.PurchaseReturnDao;
import com.inventory.purchase.model.PurchaseReturnInventoryDetailsModel;
import com.inventory.purchase.model.PurchaseReturnModel;
import com.inventory.purchase.ui.PurchaseReturnUI;
import com.inventory.reports.dao.SalesReportDao;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SCollectionContainer;
import com.webspark.Components.SComboField;
import com.webspark.Components.SDateField;
import com.webspark.Components.SFormLayout;
import com.webspark.Components.SGridLayout;
import com.webspark.Components.SHTMLLabel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.SLabel;
import com.webspark.Components.SNativeButton;
import com.webspark.Components.SNotification;
import com.webspark.Components.SPanel;
import com.webspark.Components.SPopupView;
import com.webspark.Components.SReportChoiceField;
import com.webspark.Components.STable;
import com.webspark.Components.SVerticalLayout;
import com.webspark.Components.SparkLogic;
import com.webspark.bean.ReportBean;
import com.webspark.common.util.CommonUtil;
import com.webspark.core.Report;
import com.webspark.dao.CurrencyManagementDao;
import com.webspark.model.CurrencyModel;
import com.webspark.uac.dao.OfficeDao;
import com.webspark.uac.dao.OrganizationDao;

/**
 * 
 * @author Anil K P
 * 
 *         WebSpark.
 * 
 *         Jul 22, 2014
 */
public class ItemWisePurchaseReturnReportUI extends SparkLogic {

	private static final long serialVersionUID = 662303327325964158L;
	private SComboField organizationComboField;
	private SComboField officeComboField;

	private SDateField fromDateField;
	private SDateField toDateField;
	private SComboField supplierComboField;
	private SComboField itemGroupCombo ;
	private SComboField itemsComboField;
	private SReportChoiceField reportChoiceField;

	private SPanel mainPanel;

	private SFormLayout mainFormLayout;
	private SHorizontalLayout dateHorizontalLayout;
	private SHorizontalLayout buttonHorizontalLayout;

	private SButton generateButton;
	private SButton generateConsolidatedButton;

	private SCollectionContainer container;
	private SCollectionContainer custContainer;

	private long customerId;

	private Report report;

	LedgerDao ledDao;

	private WrappedSession session;
	private SettingsValuePojo sett;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "ID";
	static String TBC_DATE = "Date";
	static String TBC_ITEM = "Item";
	static String TBC_BILL = "Bill";
	static String TBC_CUSTOMER = "Supplier";
	static String TBC_RETURN = "Return Qty";
	
	SHorizontalLayout popupContainer,mainHorizontal;
	Object[] allColumns;
	Object[] visibleColumns;
	STable table;
	SButton showButton;
	
	private STable subTable;
	SHorizontalLayout popHor;
	SNativeButton closeBtn;
	private Object[] allSubColumns;
	private Object[] visibleSubColumns;
	SPopupView popUp;
	private HashMap<Long, String> currencyHashMap;

	@SuppressWarnings("serial")
	@Override
	public SPanel getGUI() {
		allColumns = new Object[] { TBC_SN, TBC_ID,TBC_ITEM,TBC_RETURN };
		visibleColumns = new Object[]  { TBC_SN, TBC_ITEM,TBC_RETURN};
		
		allSubColumns = new Object[] { TBC_SN, TBC_ID,TBC_ITEM,TBC_BILL,TBC_DATE,TBC_CUSTOMER, TBC_RETURN };
		visibleSubColumns = new Object[]  { TBC_SN, TBC_ITEM,TBC_BILL,TBC_DATE,TBC_CUSTOMER,TBC_RETURN};
		popHor = new SHorizontalLayout();
		closeBtn = new SNativeButton("X");
		
		mainHorizontal=new SHorizontalLayout();
		popupContainer = new SHorizontalLayout();
		showButton=new SButton(getPropertyName("show"));
		
		table = new STable(null, 650, 250);
		table.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		table.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		table.addContainerProperty(TBC_RETURN, Double.class, null,getPropertyName("return_qty"), null, Align.LEFT);

		table.setColumnExpandRatio(TBC_ITEM, 2);
		table.setColumnExpandRatio(TBC_RETURN, 1);
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setVisibleColumns(visibleColumns);
		
		subTable = new STable(null, 750, 250);
		subTable.addContainerProperty(TBC_SN, Integer.class, null, "#", null,Align.CENTER);
		subTable.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null,Align.CENTER);
		subTable.addContainerProperty(TBC_ITEM, String.class, null,getPropertyName("item"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_BILL, String.class, null,getPropertyName("bill"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_DATE, String.class, null,getPropertyName("date"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_CUSTOMER, String.class, null,getPropertyName("supplier"), null, Align.LEFT);
		subTable.addContainerProperty(TBC_RETURN, Double.class, null,getPropertyName("return_qty"), null, Align.LEFT);

		subTable.setColumnExpandRatio(TBC_SN, (float) 0.3);
		subTable.setColumnExpandRatio(TBC_DATE, (float) 0.5);
		subTable.setColumnExpandRatio(TBC_ITEM, 2);
		subTable.setColumnExpandRatio(TBC_CUSTOMER, 1);
		subTable.setColumnExpandRatio(TBC_RETURN, 1);
		subTable.setSelectable(true);
		subTable.setMultiSelect(false);
		subTable.setVisibleColumns(visibleSubColumns);
		
		customerId = 0;
		report = new Report(getLoginID());
		ledDao = new LedgerDao();
		setSize(1050, 380);
		mainPanel = new SPanel();
		mainPanel.setSizeFull();

		mainFormLayout = new SFormLayout();
		mainFormLayout.setSpacing(true);

		dateHorizontalLayout = new SHorizontalLayout();
		dateHorizontalLayout.setSpacing(true);

		buttonHorizontalLayout = new SHorizontalLayout();
		buttonHorizontalLayout.setSpacing(true);

		// officeComboField = new SOfficeComboField("Office", 200);
		fromDateField = new SDateField(getPropertyName("from_date"));
		fromDateField.setValue(getMonthStartDate());
		toDateField = new SDateField(getPropertyName("to_date"));
		toDateField.setValue(getWorkingDate());
		dateHorizontalLayout.addComponent(fromDateField);
		dateHorizontalLayout.addComponent(toDateField);
		// mainFormLayout.addComponent(officeComboField);

		session = getHttpSession();
		if (session.getAttribute("settings") != null)
			sett = (SettingsValuePojo) session.getAttribute("settings");

		try {

			organizationComboField = new SComboField(
					getPropertyName("organization"), 200,
					new OrganizationDao().getAllOrganizations(), "id", "name");
			officeComboField = new SComboField(getPropertyName("office"), 200);

			mainFormLayout.addComponent(organizationComboField);
			mainFormLayout.addComponent(officeComboField);

			mainFormLayout.addComponent(dateHorizontalLayout);

			supplierComboField = new SComboField(getPropertyName("supplier"),
					200, null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(supplierComboField);
			
			List itemGroupList = new ItemGroupDao().getAllActiveItemGroupsNames(getOrganizationID());
			itemGroupList.add(0, new ItemGroupModel(0, getPropertyName("all")));
			
			itemGroupCombo = new SComboField(getPropertyName("item_group"), 200, itemGroupList, "id", "name", true, getPropertyName("all"));
			itemGroupCombo.setValue((long)0);
			mainFormLayout.addComponent(itemGroupCombo);

			itemsComboField = new SComboField(getPropertyName("item"), 200,
					null, "id", "name", false, getPropertyName("all"));
			mainFormLayout.addComponent(itemsComboField);

			reportChoiceField = new SReportChoiceField(
					getPropertyName("export_to"));
			mainFormLayout.addComponent(reportChoiceField);

			generateButton = new SButton(getPropertyName("generate"));
			generateButton.setClickShortcut(KeyCode.ENTER);
			generateConsolidatedButton = new SButton(getPropertyName("consolidated_report"));

			buttonHorizontalLayout.addComponent(generateButton);
			buttonHorizontalLayout.addComponent(showButton);
			buttonHorizontalLayout.addComponent(generateConsolidatedButton);
			buttonHorizontalLayout.setComponentAlignment(generateButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(showButton,Alignment.MIDDLE_CENTER);
			buttonHorizontalLayout.setComponentAlignment(generateConsolidatedButton,Alignment.MIDDLE_CENTER);
			mainFormLayout.addComponent(buttonHorizontalLayout);
			mainHorizontal.addComponent(popHor);
			mainHorizontal.addComponent(mainFormLayout);
			mainHorizontal.addComponent(table);
			mainHorizontal.addComponent(popupContainer);
			mainHorizontal.setMargin(true);
			mainPanel.setContent(mainHorizontal);

			closeBtn.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					popUp.setPopupVisible(false);
				}
			});
			
			organizationComboField
					.addValueChangeListener(new ValueChangeListener() {
						public void valueChange(ValueChangeEvent event) {

							try {

								SCollectionContainer bic = SCollectionContainer.setList(
										new OfficeDao()
												.getAllOfficeNamesUnderOrg((Long) organizationComboField
														.getValue()), "id");
								officeComboField.setContainerDataSource(bic);
								officeComboField
										.setItemCaptionPropertyId("name");

								Iterator it = officeComboField.getItemIds()
										.iterator();
								if (it.hasNext())
									officeComboField.setValue(it.next());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			officeComboField.addValueChangeListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					try {

						reloadItemCombo();

						List<Object> customerList = ledDao
								.getAllSuppliers((Long) officeComboField
										.getValue());
						LedgerModel ledgerModel = new LedgerModel();
						ledgerModel.setId(0);
						ledgerModel
								.setName(getPropertyName("all"));
						if (customerList == null) {
							customerList = new ArrayList<Object>();
						}
						customerList.add(0, ledgerModel);

						SCollectionContainer bic2 = SCollectionContainer
								.setList(customerList, "id");
						supplierComboField.setContainerDataSource(bic2);
						supplierComboField.setItemCaptionPropertyId("name");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			if (isSystemAdmin() || isSuperAdmin()) {
				organizationComboField.setEnabled(true);
				officeComboField.setEnabled(true);
			} else {
				organizationComboField.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeComboField.setEnabled(true);
				} else {
					officeComboField.setEnabled(false);
				}
			}

			organizationComboField.setValue(getOrganizationID());
			officeComboField.setValue(getOfficeID());
			
			itemGroupCombo.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					reloadItemCombo();
				}
			});

			final CloseListener closeListener = new CloseListener() {
				@Override
				public void windowClose(CloseEvent e) {
					showButton.click();
				}
			};

			final Action actionDelete = new Action(getPropertyName("edit"));
			
			subTable.addActionHandler(new Handler() {
				
				@SuppressWarnings("static-access")
				@Override
				public void handleAction(Action action, Object sender, Object target) {
					try{
						Item item = null;
						if (subTable.getValue() != null) {
							item = subTable.getItem(subTable.getValue());
							PurchaseReturnUI option=new PurchaseReturnUI();
							option.setCaption(getPropertyName("purchase_return"));
					//		option.getDebitNoteNoComboField().setValue((Long) item.getItemProperty(TBC_ID).getValue());
							popUp.setPopupVisible(false);
							option.center();
							getUI().getCurrent().addWindow(option);
							option.addCloseListener(closeListener);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				@Override
				public Action[] getActions(Object target, Object sender) {
					return new Action[] { actionDelete };
				}
			});
			
			subTable.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (subTable.getValue() != null) {
							Item itm = subTable.getItem(subTable.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							PurchaseReturnModel sale=new PurchaseReturnDao().getPurchaseReturnModel(id);
							SFormLayout form = new SFormLayout();
							form.addComponent(new SHTMLLabel(null,"<h2><u>"+getPropertyName("purchase_return")+"</u></h2>"));
							form.addComponent(new SLabel(getPropertyName("no"),sale.getReturn_no()+""));
							form.addComponent(new SLabel(getPropertyName("supplier"),sale.getSupplier().getName()));
							form.addComponent(new SLabel(getPropertyName("date"),CommonUtil.getUtilDateFromSQLDate(sale.getDate())));
							double amount = sale.getAmount() / sale.getConversionRate();
							if(sale.getNetCurrencyId().getId() == getCurrencyID()){
								form.addComponent(new SLabel(getPropertyName("net_amount"),sale.getAmount() + " "+getCurrencyDescription(getCurrencyID())));
							} else {								
								form.addComponent(new SLabel(getPropertyName("net_amount"),
										roundNumber(amount) + " "+getCurrencyDescription(getCurrencyID())
										+" ("+sale.getAmount()+" "+getCurrencyDescription(sale.getNetCurrencyId().getId())+")"));
							}
							
							form.addComponent(new SLabel(getPropertyName("amount_paid"),sale.getAmount() + ""));
							SGridLayout grid = new SGridLayout(getPropertyName("item_details"));
							grid.setColumns(12);
							grid.setRows(sale
									.getInventory_details_list().size() + 3);

							grid.addComponent(new SLabel(null, "#"), 0, 0);
							grid.addComponent(new SLabel(null, getPropertyName("item")), 1,0);
							grid.addComponent(new SLabel(null, getPropertyName("return_qty")), 2, 0);
							grid.addComponent(new SLabel(null, getPropertyName("unit")),	3, 0);
							grid.addComponent(new SLabel(null, getPropertyName("rate")),	4, 0);
							grid.addComponent(new SLabel(null, getPropertyName("amount")),5, 0);
							grid.setSpacing(true);
							
							int i = 1;
							PurchaseReturnInventoryDetailsModel invObj;
							Iterator itr = sale.getInventory_details_list().iterator();
							while(itr.hasNext()){
								invObj=(PurchaseReturnInventoryDetailsModel)itr.next();
								Item itim=table.getItem(table.getValue());
								long iem = (Long) itim.getItemProperty(TBC_ID).getValue();
								if(invObj.getItem().getId()!=iem)
									continue;
								grid.addComponent(new SLabel(null, i + ""),	0, i);
								grid.addComponent(new SLabel(null, invObj.getItem().getName()), 1, i);
								grid.addComponent(new SLabel(null, invObj.getQunatity() + ""), 2,	i);
								grid.addComponent(new SLabel(null, invObj.getUnit().getSymbol() + ""),3, i);
								grid.addComponent(new SLabel(null, invObj.getUnit_price()+""),4, i);
								grid.addComponent(new SLabel(null,(invObj.getUnit_price() * invObj.getQunatity() 
																- invObj.getDiscount()
																+ invObj.getTaxAmount())+ " "+getCurrencyDescription(getCurrencyID())), 5, i);
								
								i++;
							}
							form.addComponent(grid);
							form.addComponent(new SLabel(getPropertyName("comment"), sale.getComments()));
							form.setStyleName("grid_max_limit");
							popupContainer.removeAllComponents();
							SPopupView pop = new SPopupView("", form);
							popupContainer.addComponent(pop);
							pop.setPopupVisible(true);
							pop.setHideOnMouseOut(false);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			
			
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						if (table.getValue() != null) {
							Item itm = table.getItem(table.getValue());
							long id = (Long) itm.getItemProperty(TBC_ID).getValue();
							

							long custId = 0;
							if (supplierComboField.getValue() != null && !supplierComboField.getValue().equals("")) {
								custId = toLong(supplierComboField.getValue().toString());
							}
							List reportList = new SalesReportDao().getItemWisePurchaseReturnDetails(
															id,
															custId,
															CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
															CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
															(Long) officeComboField.getValue(),(Long) itemGroupCombo.getValue());
							subTable.removeAllItems();
							subTable.setVisibleColumns(allSubColumns);
							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ReportBean)itr.next();
									subTable.addItem(new Object[]{
											subTable.getItemIds().size()+1,
											bean.getId(),
											bean.getItem_name(),
											bean.getNum(),
											bean.getDt().toString(),
											bean.getClient_name(),
											bean.getQuantity()},subTable.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							subTable.setVisibleColumns(visibleSubColumns);
							popUp = new SPopupView( "",
									new SVerticalLayout(true,new SHorizontalLayout(new SHTMLLabel(null,
															"<h2><u style='margin-left: 40px;'>Purchase Return Details",725), closeBtn), subTable));

							popHor.addComponent(popUp);
							popUp.setPopupVisible(true);
							popUp.setHideOnMouseOut(false);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			

			showButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						table.removeAllItems();
						table.setVisibleColumns(allColumns);
						if (officeComboField.getValue() != null) {

							String items = "";

							List reportList;

							long itemID = 0;
							long custId = 0;

							if (itemsComboField.getValue() != null && !itemsComboField.getValue().equals("") && !itemsComboField.getValue().toString().equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}
							if (supplierComboField.getValue() != null && !supplierComboField.getValue().equals("")) {
								custId = toLong(supplierComboField.getValue().toString());
							}
							reportList = new SalesReportDao().showItemWisePurchaseReturnDetails(
															itemID,
															custId,
															CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
															CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
															(Long) officeComboField.getValue(),(Long) itemGroupCombo.getValue());
							
							if(reportList.size()>0){
								ReportBean bean=null;
								Iterator itr=reportList.iterator();
								while(itr.hasNext()){
									bean=(ReportBean)itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											bean.getId(),
											bean.getName(),
											roundNumber(bean.getAmount())},table.getItemIds().size()+1);
								}
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(visibleColumns);
							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"), true);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			generateConsolidatedButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if (officeComboField.getValue() != null) {

							

							long itemID = 0;
							long custId = 0;

							if (itemsComboField.getValue() != null && !itemsComboField.getValue().equals("") && !itemsComboField.getValue().toString().equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}
							if (supplierComboField.getValue() != null && !supplierComboField.getValue().equals("")) {
								custId = toLong(supplierComboField.getValue().toString());
							}
							List reportList = new SalesReportDao().showItemWisePurchaseReturnDetails(
															itemID,
															custId,
															CommonUtil.getSQLDateFromUtilDate(fromDateField.getValue()), 
															CommonUtil.getSQLDateFromUtilDate(toDateField.getValue()),
															(Long) officeComboField.getValue(),(Long) itemGroupCombo.getValue());
							
							if(reportList.size()>0){
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("Consolidated_3_1_Report");
								report.setReportFileName("ConsolidatedReport");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_purchase_return_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("CUSTOMER_LABEL", getPropertyName("item"));
								map.put("AMOUNT_LABEL", getPropertyName("quantity"));
								map.put("TOTAL_LABEL", getPropertyName("total"));
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("supplier")+" : "
											+ supplierComboField
													.getItemCaption(supplierComboField
															.getValue()) + "\t";
								}
								if (toLong(itemGroupCombo.getValue().toString()) != 0) {
									subHeader += getPropertyName("group")
											+ " : "
											+ itemGroupCombo
													.getItemCaption(itemGroupCombo
															.getValue());
								}
								if (itemID != 0) {
									subHeader += getPropertyName("item")+" : "
											+ itemsComboField
													.getItemCaption(itemsComboField
															.getValue());
								}

								subHeader += "\n "+getPropertyName("from")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(fromDateField
														.getValue())
										+ "\t "+getPropertyName("to")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(toDateField
														.getValue());

								report.setReportSubTitle(subHeader);

								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, map);

								reportList.clear();
							}
							else{
								SNotification.show(getPropertyName("no_data_available"),Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(visibleColumns);
							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"), true);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			
			if (isSystemAdmin() || isSuperAdmin()) {
				organizationComboField.setEnabled(true);
				officeComboField.setEnabled(true);
			} else {
				organizationComboField.setEnabled(false);
				if (isOrganizationAdmin()) {
					officeComboField.setEnabled(true);
				} else {
					officeComboField.setEnabled(false);
				}
			}
			
			
			generateButton.addClickListener(new ClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {

					try {

						if (officeComboField.getValue() != null) {

							String items = "";

							List reportList;

							long itemID = 0;
							long custId = 0;

							if (itemsComboField.getValue() != null
									&& !itemsComboField.getValue().equals("")
									&& !itemsComboField.getValue().toString()
											.equals("0")) {
								itemID = (Long) itemsComboField.getValue();
							}
							if (supplierComboField.getValue() != null
									&& !supplierComboField.getValue()
											.equals("")) {
								custId = toLong(supplierComboField.getValue()
										.toString());
							}
							reportList = new SalesReportDao().getItemWisePurchaseReturnDetails(
									itemID,
									custId,
									CommonUtil
											.getSQLDateFromUtilDate(fromDateField
													.getValue()), CommonUtil
											.getSQLDateFromUtilDate(toDateField
													.getValue()),
									(Long) officeComboField.getValue(),(Long) itemGroupCombo.getValue());

							if (reportList.size() > 0) {

								Collections.sort(reportList,
										new Comparator<ReportBean>() {
											@Override
											public int compare(
													final ReportBean object1,
													final ReportBean object2) {

												int result = object2
														.getDt()
														.compareTo(
																object1.getDt());
												if (result == 0) {
													result = object1
															.getItem_name()
															.toLowerCase()
															.compareTo(
																	object2.getItem_name()
																			.toLowerCase());
												}
												return result;
											}

										});
								HashMap<String, Object> map = new HashMap<String, Object>();
								report.setJrxmlFileName("ItemWisePurchaseReturnReport");
								report.setReportFileName("ItemWise Purchase Return Report");
								
								map.put("REPORT_TITLE_LABEL", getPropertyName("item_wise_purchase_return_report"));
								map.put("SL_NO_LABEL", getPropertyName("sl_no"));
								map.put("DATE_LABEL", getPropertyName("date"));
								map.put("ITEM_LABEL", getPropertyName("item"));
								map.put("QUANTITY_LABEL", getPropertyName("quantity"));
								map.put("BILL_NO_LABEL", getPropertyName("bill_no"));
								map.put("SUPPLIER_LABEL", getPropertyName("supplier"));
								
								String subHeader = "";
								if (customerId != 0) {
									subHeader += getPropertyName("supplier")+" : "
											+ supplierComboField
													.getItemCaption(supplierComboField
															.getValue()) + "\t";
								}
								
								if (toLong(itemGroupCombo.getValue().toString()) != 0) {
									subHeader += getPropertyName("group")
											+ " : "
											+ itemGroupCombo
													.getItemCaption(itemGroupCombo
															.getValue());
								}
								
								if (itemID != 0) {
									subHeader += getPropertyName("item")+" : "
											+ itemsComboField
													.getItemCaption(itemsComboField
															.getValue());
								}

								subHeader += "\n "+getPropertyName("from")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(fromDateField
														.getValue())
										+ "\t "+getPropertyName("to")+" : "
										+ CommonUtil
												.formatDateToDDMMYYYY(toDateField
														.getValue());

								report.setReportSubTitle(subHeader);

								report.setIncludeHeader(true);
								report.setIncludeFooter(false);
								report.setReportType(toInt(reportChoiceField
										.getValue().toString()));
								report.setOfficeName(officeComboField
										.getItemCaption(officeComboField
												.getValue()));
								report.createReport(reportList, map);

								reportList.clear();

							} else {
								SNotification.show(
										getPropertyName("no_data_available"),
										Type.WARNING_MESSAGE);
							}

							setRequiredError(officeComboField, null, false);
						} else {
							setRequiredError(officeComboField,
									getPropertyName("invalid_selection"), true);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainPanel;
	}
	private String getCurrencyDescription(long currencyId) {
		if(currencyHashMap == null){
			currencyHashMap = new HashMap<Long, String>();
			try {
				List list = new CurrencyManagementDao().getCurrencySymbol();
				Iterator<CurrencyModel> itr = list.iterator();
				while(itr.hasNext()){
					CurrencyModel model = itr.next();
					currencyHashMap.put(model.getId(), model.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return currencyHashMap.get(currencyId);
	}

	protected void loadCustomerCombo(long officeId) {
		List<Object> custList = null;
		try {
			if (officeId != 0) {
				custList = ledDao.getAllCustomers(officeId);
			} else {
				custList = ledDao.getAllCustomers();
			}
			LedgerModel ledgerModel = new LedgerModel();
			ledgerModel.setId(0);
			ledgerModel.setName(getPropertyName("all"));
			if (custList == null) {
				custList = new ArrayList<Object>();
			}
			custList.add(0, ledgerModel);
			custContainer = SCollectionContainer.setList(custList, "id");
			supplierComboField.setContainerDataSource(custContainer);
			supplierComboField.setItemCaptionPropertyId("name");
			supplierComboField.setValue(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void reloadItemCombo() {
		try {

			List itemList = new ItemDao().getAllActiveItemsWithAppendingItemCode((Long) officeComboField.getValue(),
					false, (Long)itemGroupCombo.getValue());
			ItemModel salesModel = new ItemModel(0, getPropertyName("all"));
			if (itemList == null) {
				itemList = new ArrayList<Object>();
			}
			itemList.add(0, salesModel);

			SCollectionContainer bic1 = SCollectionContainer.setList(itemList,
					"id");
			itemsComboField.setContainerDataSource(bic1);
			itemsComboField.setItemCaptionPropertyId("name");
			itemsComboField.setValue((long)0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public Boolean isValid() {
		return null;
	}

	@Override
	public Boolean getHelp() {
		return null;
	}

}
