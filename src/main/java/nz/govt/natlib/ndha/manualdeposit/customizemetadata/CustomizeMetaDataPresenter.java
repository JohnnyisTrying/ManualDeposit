package nz.govt.natlib.ndha.manualdeposit.customizemetadata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTree;

import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.common.mets.FileSystemObject;
import nz.govt.natlib.ndha.manualdeposit.ManualDepositPresenter;
import nz.govt.natlib.ndha.manualdeposit.metadata.EDataType;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataFields;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataListValues;
import nz.govt.natlib.ndha.manualdeposit.metadata.MetaDataTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class CustomizeMetaDataPresenter {
	
	private final ICustomizeMetaDataEditorView customizeMetaDataEditorView;
	//private JTable theCustomizeMetaDataTable;
	private JTree theEntityTree;
	private CustomizeMetaDataTableModel customizeMetaDataTableModel;
	private final ManualDepositPresenter manualDepositPresenter;
	private final MetaDataTableModel metaDataTableModel;
	private List<IMetaDataTypeExtended> metaDataList = new ArrayList<IMetaDataTypeExtended>();
	private final String dateFormat = "dd/MM/yyyy";
	private final static Log LOG = LogFactory.getLog(CustomizeMetaDataPresenter.class);
	
	public static CustomizeMetaDataPresenter create(final ICustomizeMetaDataEditorView frame,
			final ManualDepositPresenter parent, CustomizeMetaDataTableModel tableModel, MetaDataTableModel parentTableModel,
			JTree tree) {
		return new CustomizeMetaDataPresenter(frame, parent, tableModel, parentTableModel, tree);
	}

	public CustomizeMetaDataPresenter(final ICustomizeMetaDataEditorView frame,
			final ManualDepositPresenter parent, CustomizeMetaDataTableModel tableModel, MetaDataTableModel parentTableModel,
			JTree tree) {
		customizeMetaDataEditorView = frame;
		manualDepositPresenter = parent;
		customizeMetaDataEditorView.setPresenter(this);
		customizeMetaDataTableModel = tableModel;
		metaDataTableModel = parentTableModel;
		theEntityTree = tree;
	}
	
	public void closeForm() {
		if (customizeMetaDataEditorView.confirm("Do you wish to save the changes?")) {
			saveMetaData();
		}else {
			customizeMetaDataEditorView.setVisible(false);
		}
	}
	
	public void addTableData(ArrayList<FileGroupCollection> entities, MetaDataFields metaData) {
		List<String> metaDataColumnsList = new ArrayList<String>();
		metaDataColumnsList.add("Entity Name");
		
		for (IMetaDataTypeExtended data : metaData) {
			if (data.getIsCustomizable()){
				metaDataColumnsList.add(data.getDataFieldDescription());
			}
		}
		if(customizeMetaDataTableModel.getCustomMetaData() == null){
			customizeMetaDataEditorView.setTableDataBlank(entities, metaDataColumnsList);
		}
		else{
			customizeMetaDataEditorView.setTableData(entities, metaDataColumnsList, customizeMetaDataTableModel);
		}
		
	}
	
	public void showMetaData() {
		customizeMetaDataEditorView.showView();
	}
	
    public void setDescriptiveMetaData(List<IMetaDataTypeExtended> metaDataList) {
    	this.metaDataList = metaDataList;
    }

    public void addDescriptiveMetaDataField(IMetaDataTypeExtended metaData) {
    	metaDataList.add(metaData);
    }

    public List<IMetaDataTypeExtended> getDescriptiveMetaData() {
    	return metaDataList;
    }
	
	@SuppressWarnings("unchecked")
	public void saveMetaData() {
		boolean saveFailure = false;
		ArrayList<FileGroupCollection> entities = manualDepositPresenter.getEntities();
		Map<String, Object> tableData = customizeMetaDataEditorView.getTableData();
		Map<String, EDataType> EDataTypes = metaDataTableModel.getMetaData().getMetaDataEDataTypes("By Desc");
		Map<FileGroupCollection, Boolean> entityCustomized = new HashMap<FileGroupCollection, Boolean>();
		
		// Outer Map
		// Map<Entity Name, Map<Column, value>>
		Map<String, Map<String, String>> metaDataVal = new HashMap<String, Map<String, String>>();
		// Inner Map
		Map<String, String> entityMetaDataVals;
				
		for(FileGroupCollection entity: entities){
			entityMetaDataVals = new HashMap<String, String>();
			String keyEntityName = entity.getEntityName();
			HashMap<String, Object> dataValue = (HashMap<String, Object>) tableData.get(keyEntityName);
			Set<String> tableColumns = dataValue.keySet();
			entityCustomized.put(entity, false);
			for(String column: tableColumns){	
				String value = (String) dataValue.get(column);
				
				// Validation
				if(EDataTypes.get(column).toString().equals("Date")){
					if(!validateDate(value, keyEntityName, column)){
						saveFailure = true;
						break;
					}
				}
				else if(EDataTypes.get(column).toString().equals("Multi-select")){
					if(!validateMultiSelect(value, keyEntityName, column)){
						saveFailure = true;
						break;
					}
				}
				
				// If a custom value has been entered, and validation passed.
				if(!value.isEmpty() && !saveFailure){	
					entityCustomized.put(entity, true);
				}
				
				entityMetaDataVals.put(column, value);
			}
			if(saveFailure)
				break;
			
			metaDataVal.put(keyEntityName, entityMetaDataVals);
		}
		
		// If validation is successful - save custom meta data.
		if(!saveFailure){
			customizeMetaDataTableModel.setMetaData(metaDataVal);
			// Set customized flags for each Entity, used to alter color of entity name in Entity Tree.
			for(FileGroupCollection entity: entityCustomized.keySet()){				
				entity.setCustomized(entityCustomized.get(entity));
			}
		}		
		customizeMetaDataEditorView.setVisible(saveFailure);
		theEntityTree.repaint();
	}
	
	public void openFile(String value) {
		for(FileGroupCollection fgc : manualDepositPresenter.getEntities()){
			if(fgc.getEntityName().equals(value)){
				for(FileSystemObject fso : fgc.getAllFiles()){
					// Only use the first file as all copies use same original file.
					if (fso.getIsFile()) {
							try {
								Runtime.getRuntime().exec(
										"rundll32 SHELL32.DLL,ShellExec_RunDLL "
												+ fso.getFile().getAbsolutePath());
							} catch (Exception ex) {
								String message = "Could not open file "	+ fso.getDescription();
								customizeMetaDataEditorView.showError("Could not open file", message, ex);
								reportException(ex);
							}
						break;
					}
				}
			}
			
		}
	}
	
	
	private boolean validateDate(String userValue, String entityName, String column){
		if((userValue == null) || (userValue.equals(""))){
			return true;
		}
		SimpleDateFormat fieldDateFormat = new SimpleDateFormat(dateFormat);
		fieldDateFormat.setLenient(false);
		
		try{
			fieldDateFormat.parse(userValue);
		} catch(ParseException e){
			LOG.error("Invalid format", e);
			customizeMetaDataEditorView.showError("Invalid Date Format", entityName+" contains invalid date format in "+column+" column");
			return false;
		}
		
		return true;
	}
	
	private boolean validateMultiSelect(String userValue, String entityName, String column){
		boolean valueMatch = false;
		
		if((userValue == null) || (userValue.equals(""))){
			return true;
		}
		
		IMetaDataTypeExtended entity = metaDataTableModel.getMetaData().getMetaDataTypeByDesc(column);
		List<MetaDataListValues> fieldValues = entity.getListItems();
		for(MetaDataListValues value: fieldValues){
			if(userValue.equals(value.getDisplay())){
				valueMatch = true;
				break;
			}
		}
		
		if(!valueMatch){
//			try {
//				throw new ParseException("Invalid Multi Select Option: "+entityName+" contains an invalid selection in "+column+" column", 0);
//			} catch (ParseException e) {
//				LOG.error("Invalid Multi Select Option", e);
				customizeMetaDataEditorView.showError("Invalid Multi Select Option", entityName+" contains an invalid selection in "+column+" column");
//				return false;
//			}
		}
		return valueMatch;
	}
	
	private static void reportException(Exception ex) {
		LOG.error(ex.getMessage(), ex);
		ex.printStackTrace();
	}
	

}
