package nz.govt.natlib.ndha.manualdeposit.customizemetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nz.govt.natlib.ndha.common.mets.FileGroupCollection;
import nz.govt.natlib.ndha.manualdeposit.IBaseForm;

public interface ICustomizeMetaDataEditorView extends IBaseForm {
	
	public void setPresenter(CustomizeMetaDataPresenter thePresenter);

	public abstract void setVisible(boolean isVisible);
	
	public void checkButtons();
	
	public void showGlassPane(boolean show);
	
	public void showError(String header, String message);

	public void showError(String header, String message, Exception ex);

	public void showMessage(String header, String message);
	
	public void setWaitCursor(boolean isWaiting);

	public boolean confirm(String message);

	public boolean confirm(String message, boolean useYesNo);
	
	public void setCanClose(boolean canClose);
	
	//public void setTableData(ArrayList<FileGroupCollection> entities);
	
	public Map<String, Object> getTableData();
	
	public void setTableDataBlank(ArrayList<FileGroupCollection> entities, List<String> metaDataList);
	
	public void setTableData(ArrayList<FileGroupCollection> entities, List<String> metaDataList, CustomizeMetaDataTableModel metaDataValues);
	
}
