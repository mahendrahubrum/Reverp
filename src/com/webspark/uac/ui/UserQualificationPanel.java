package com.webspark.uac.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.Align;
import com.webspark.Components.SButton;
import com.webspark.Components.SComboField;
import com.webspark.Components.SContainerPanel;
import com.webspark.Components.SHorizontalLayout;
import com.webspark.Components.STable;
import com.webspark.Components.STextField;
import com.webspark.Components.SVerticalLayout;
import com.webspark.common.util.CommonUtil;
import com.webspark.uac.dao.QualificationDao;
import com.webspark.uac.dao.UserManagementDao;
import com.webspark.uac.dao.UserQualificationDao;
import com.webspark.uac.model.QualificationModel;
import com.webspark.uac.model.S_OfficeModel;
import com.webspark.uac.model.UserModel;
import com.webspark.uac.model.UserQualificationModel;

@SuppressWarnings("serial")
public class UserQualificationPanel extends SContainerPanel {

	SVerticalLayout mainLayout;
	SComboField userCombo;
	
	STable table;
	
	static String TBC_SN = "SN";
	static String TBC_ID = "Id";
	static String TBC_QID = "Qid";
	static String TBC_QUALIFICATION = "Qualification";
	static String TBC_YEAR_ID = "Year Id";
	static String TBC_YEAR = "Year";
	static String TBC_DESCRIPTION = "Description";
	
	SButton saveButton;
	SButton createNewButton;
	
	SButton addItemButton;
	SButton updateItemButton;
	
	SComboField qualificationCombo;
	InlineDateField yearField;
	STextField descriptionField;
	
	private Object[] allHeaders;
	private Object[] requiredHeaders;
	UserQualificationDao dao;
	
	public UserQualificationPanel() {
		try {
			dao=new UserQualificationDao();
			allHeaders=new Object[]{TBC_SN, TBC_ID, TBC_QID, TBC_QUALIFICATION, TBC_YEAR_ID, TBC_YEAR, TBC_DESCRIPTION};
			requiredHeaders=new Object[]{TBC_SN, TBC_QUALIFICATION, TBC_YEAR, TBC_DESCRIPTION};
			mainLayout=new SVerticalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setMargin(true);
			setSize(750, 400);
			userCombo=new SComboField(null, 200, new UserManagementDao().getUsersWithFullNameAndCodeFromOffice(getOfficeID(), isSuperAdmin()), "id", "first_name", true, "Select");
			SHorizontalLayout createLayout=new SHorizontalLayout(getPropertyName("user"));
			createLayout.setSpacing(true);
			createNewButton = new SButton();
			createNewButton.setStyleName("createNewBtnStyle");
			createNewButton.setDescription("Create New");
			createLayout.addComponent(userCombo);
			createLayout.addComponent(createNewButton);
			
			table=new STable(null, 500, 150);
			table.addContainerProperty(TBC_SN, Integer.class, null, TBC_SN, null, Align.LEFT);
			table.addContainerProperty(TBC_ID, Long.class, null, TBC_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_QID, Long.class, null, TBC_QID, null, Align.LEFT);
			table.addContainerProperty(TBC_QUALIFICATION, String.class, null, TBC_QUALIFICATION, null, Align.LEFT);
			table.addContainerProperty(TBC_YEAR_ID, Date.class, null, TBC_YEAR_ID, null, Align.LEFT);
			table.addContainerProperty(TBC_YEAR, String.class, null, TBC_YEAR, null, Align.LEFT);
			table.addContainerProperty(TBC_DESCRIPTION, String.class, null, TBC_DESCRIPTION, null, Align.LEFT);
			table.setVisibleColumns(requiredHeaders);
			
			addItemButton = new SButton(null, "Add Item");
			addItemButton.setStyleName("addItemBtnStyle");
			updateItemButton = new SButton(null, "Update");
			updateItemButton.setStyleName("updateItemBtnStyle");
			updateItemButton.setVisible(false);
			
			qualificationCombo=new SComboField(getPropertyName("qualification"), 150, 
											new QualificationDao().getActiveQualificationModelList(getOfficeID()), 
											"id", "name", true, getPropertyName("select"));
			yearField=new InlineDateField(getPropertyName("year_passing"));
			yearField.setRequired(true);
			yearField.setResolution(Resolution.YEAR);
			yearField.setValue(getWorkingDate());
			descriptionField=new STextField(getPropertyName("description"), 150);
			
			SHorizontalLayout itemLayout=new SHorizontalLayout();
			itemLayout.setSpacing(true);
			itemLayout.setMargin(true);
			itemLayout.setStyleName("po_border");
			
			itemLayout.addComponent(qualificationCombo);
			itemLayout.addComponent(yearField);
			itemLayout.addComponent(descriptionField);
			itemLayout.addComponent(addItemButton);
			itemLayout.addComponent(updateItemButton);
			itemLayout.setComponentAlignment(addItemButton, Alignment.BOTTOM_CENTER);
			itemLayout.setComponentAlignment(updateItemButton, Alignment.BOTTOM_CENTER);
			
			saveButton = new SButton(getPropertyName("save"), 100, 25);
			saveButton.setIcon(new ThemeResource("icons/saveSideIcon.png"));
			saveButton.setStyleName("savebtnStyle");
			
			SHorizontalLayout buttonLayout=new SHorizontalLayout();
			buttonLayout.setSpacing(true);
			buttonLayout.addComponent(saveButton);
			
			mainLayout.addComponent(createLayout);
			mainLayout.addComponent(table);
			mainLayout.addComponent(itemLayout);
			mainLayout.addComponent(buttonLayout);
			mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
			setContent(mainLayout);
			
			createNewButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					userCombo.setValue(null);
				}
			});
			
			userCombo.addValueChangeListener(new ValueChangeListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void valueChange(ValueChangeEvent event) {
					try {
						table.removeAllItems();
						qualificationCombo.setValue(null);
						yearField.setValue(getWorkingDate());
						descriptionField.setValue("");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
						if(userCombo.getValue()!=null){
							List list=new ArrayList();
							list=dao.getUserQualificationList((Long)userCombo.getValue(), getOfficeID());
							if(list.size()>0){
								Iterator itr=list.iterator();
								table.setVisibleColumns(allHeaders);
								while (itr.hasNext()) {
									UserQualificationModel mdl = (UserQualificationModel) itr.next();
									table.addItem(new Object[]{
											table.getItemIds().size()+1,
											mdl.getId(),
											mdl.getQualification().getId(),
											mdl.getQualification().getName(),
											mdl.getYear(),
											CommonUtil.formatDateToYYYY(mdl.getYear()),
											mdl.getDescription()},table.getItemIds().size()+1);
								}
								table.setVisibleColumns(requiredHeaders);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			addItemButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(isAddingValid()){
							table.setVisibleColumns(allHeaders);
							Iterator itr=table.getItemIds().iterator();
							List<Long> idList=new ArrayList<Long>();
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								idList.add(toLong(item.getItemProperty(TBC_QID).getValue().toString()));
							}
							if(!idList.contains((Long)qualificationCombo.getValue())){
								table.addItem(new Object[]{
										table.getItemIds().size()+1,
										(long)0,
										(Long)qualificationCombo.getValue(),
										qualificationCombo.getItemCaption(qualificationCombo.getValue()),
										yearField.getValue(),
										CommonUtil.formatDateToYYYY(yearField.getValue()),
										descriptionField.getValue()},table.getItemIds().size()+1);
							}
							else{
								Notification.show(getPropertyName("qualification_added_earlier"), Type.WARNING_MESSAGE);
							}
							table.setVisibleColumns(requiredHeaders);
							qualificationCombo.setValue(null);
							yearField.setValue(getWorkingDate());
							descriptionField.setValue("");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			table.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					if(table.getValue()!=null){
						Item item =table.getItem(table.getValue());
						qualificationCombo.setValue((Long)item.getItemProperty(TBC_QID).getValue());
						yearField.setValue((Date)item.getItemProperty(TBC_YEAR_ID).getValue());
						descriptionField.setValue(item.getItemProperty(TBC_DESCRIPTION).getValue().toString());
						addItemButton.setVisible(false);
						updateItemButton.setVisible(true);
					}
					else{
						qualificationCombo.setValue(null);
						yearField.setValue(getWorkingDate());
						descriptionField.setValue("");
						addItemButton.setVisible(true);
						updateItemButton.setVisible(false);
					}
				}
			});
			
			updateItemButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						if(table.getValue()!=null){
							if(isAddingValid()){
								Iterator itr=table.getItemIds().iterator();
								List<Long> idList=new ArrayList<Long>();
								while (itr.hasNext()) {
									Item item = table.getItem(itr.next());
									idList.add(toLong(item.getItemProperty(TBC_QID).getValue().toString()));
								}
								Item item =table.getItem(table.getValue());
								idList.remove(toLong(item.getItemProperty(TBC_QID).getValue().toString()));
								if(!idList.contains((Long)qualificationCombo.getValue())){
									item.getItemProperty(TBC_QID).setValue((Long)qualificationCombo.getValue());
									item.getItemProperty(TBC_QUALIFICATION).setValue(qualificationCombo.getItemCaption(qualificationCombo.getValue()));
									item.getItemProperty(TBC_YEAR_ID).setValue(yearField.getValue());
									item.getItemProperty(TBC_YEAR).setValue(CommonUtil.formatDateToYYYY(yearField.getValue()));
									item.getItemProperty(TBC_DESCRIPTION).setValue(descriptionField.getValue());
									table.setValue(null);
								}
								else{
									Notification.show(getPropertyName("qualification_added_earlier"), Type.WARNING_MESSAGE);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			saveButton.addClickListener(new ClickListener() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						userCombo.setComponentError(null);
						if(userCombo.getValue()!=null){
							Iterator itr=table.getItemIds().iterator();
							List<UserQualificationModel> list=new ArrayList<UserQualificationModel>();
							while (itr.hasNext()) {
								Item item = table.getItem(itr.next());
								UserQualificationModel mdl=null;
								long id=toLong(item.getItemProperty(TBC_ID).getValue().toString());
								if(id!=0)
									mdl=dao.getUserQualificationModel(id);
								if(mdl==null)
									mdl=new UserQualificationModel();
								mdl.setQualification(new QualificationModel(toLong(item.getItemProperty(TBC_QID).getValue().toString())));
								mdl.setYear(CommonUtil.getSQLDateFromUtilDate((Date)item.getItemProperty(TBC_YEAR_ID).getValue()));
								mdl.setDescription(item.getItemProperty(TBC_DESCRIPTION).getValue().toString());
								mdl.setUser(new UserModel((Long)userCombo.getValue()));
								mdl.setOffice(new S_OfficeModel(getOfficeID()));
								list.add(mdl);
							}
							dao.save(list, (Long)userCombo.getValue(), getOfficeID());
							Notification.show(getPropertyName("save_success"), Type.WARNING_MESSAGE);
							long uid=(Long)userCombo.getValue();
							userCombo.setValue(null);
							userCombo.setValue(uid);
						}
						else
							setRequiredError(userCombo, getPropertyName("invalid_data"), true);
					} catch (Exception e) {
						e.printStackTrace();
						Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
					}
				}
			});
			
			addShortcutListener(new ShortcutListener("Save",
					ShortcutAction.KeyCode.ENTER, null) {
				@Override
				public void handleAction(Object sender, Object target) {
					if (saveButton.isVisible())
						saveButton.click();
				}
			});
			
			final Action actionDelete = new Action("Delete");

			table.addActionHandler(new Action.Handler() {
				@Override
				public Action[] getActions(final Object target, final Object sender) {
					return new Action[] { actionDelete };
				}
				@Override
				public void handleAction(final Action action, final Object sender, final Object target) {
					deleteItem();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isAddingValid(){
		boolean valid=true;
		
		if(qualificationCombo.getValue()==null || qualificationCombo.getValue().toString().equals("")){
			valid=false;
			setRequiredError(qualificationCombo, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(qualificationCombo, null, false);
		
		if(yearField.getValue()==null){
			valid=false;
			setRequiredError(yearField, getPropertyName("invalid_selection"), true);
		}
		else
			setRequiredError(yearField, null, false);
		
		return valid;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deleteItem() {
		try {
			if (table.getValue() != null) {
				table.removeItem(table.getValue());
				int SN = 0;
				Item newitem;
				Iterator it = table.getItemIds().iterator();
				while (it.hasNext()) {
					SN++;
					newitem = table.getItem((Integer) it.next());
					newitem.getItemProperty(TBC_SN).setValue(SN);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Notification.show(getPropertyName("error"), Type.ERROR_MESSAGE);
		}
	}
	
}
