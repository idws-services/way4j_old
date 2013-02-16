package way4j.tools.view.jsf.primefaces;

import java.util.List;

import javax.faces.component.UIComponent;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;

import way4j.tools.view.jsf.components.Component;
import way4j.tools.view.jsf.components.JSFExpressions;

public class PDataTable extends Component{
	
	private List<UIColumn> columns; 
	private String valueVar;
	private boolean paginate; 
	private boolean draggableColumns;
	private String paginationPosition;
	private boolean lazyDataTable;
	private String emptyMessage;
	private String rowsPerPageTemplate;
	private String selectionModel;
	private String selectionVar;
	
	@Override
	public UIComponent createComponent(){
		
		DataTable dataTable = new DataTable();
		dataTable.setColumns(getColumns());
		dataTable.setValue(JSFExpressions.createValueExpression(getValue()));
		dataTable.setVar(getValueVar());
		dataTable.setEmptyMessage(getEmptyMessage());
		dataTable.setPaginator(isPaginate());
		dataTable.setDraggableColumns(isDraggableColumns());
		dataTable.setPaginatorPosition(getPaginationPosition());
		dataTable.setSelectionMode(getSelectionModel());
		dataTable.setSelection(JSFExpressions.createValueExpression(getSelectionVar()));
		dataTable.setRowsPerPageTemplate(getRowsPerPageTemplate());
		
		return dataTable;
	}

	public String getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(String selectionModel) {
		this.selectionModel = selectionModel;
	}

	public String getSelectionVar() {
		return selectionVar;
	}



	public void setSelectionVar(String selectionVar) {
		this.selectionVar = selectionVar;
	}

	public List<UIColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<UIColumn> columns) {
		this.columns = columns;
	}

	public String getValueVar() {
		return valueVar;
	}

	public void setValueVar(String valueVar) {
		this.valueVar= valueVar;
	}

	public boolean isPaginate() {
		return paginate;
	}

	public void setPaginate(boolean paginate) {
		this.paginate = paginate;
	}

	public boolean isDraggableColumns() {
		return draggableColumns;
	}

	public void setDraggableColumns(boolean draggableColumns) {
		this.draggableColumns = draggableColumns;
	}

	public String getPaginationPosition() {
		return paginationPosition;
	}

	public void setPaginationPosition(String paginationPosition) {
		this.paginationPosition = paginationPosition;
	}

	public boolean isLazyDataTable() {
		return lazyDataTable;
	}

	public void setLazyDataTable(boolean lazyDataTable) {
		this.lazyDataTable = lazyDataTable;
	}

	public String getEmptyMessage() {
		return emptyMessage;
	}

	public void setEmptyMessage(String emptyMessage) {
		this.emptyMessage = emptyMessage;
	}

	public String getRowsPerPageTemplate() {
		return rowsPerPageTemplate;
	}

	public void setRowsPerPageTemplate(String rowsPerPageTemplate) {
		this.rowsPerPageTemplate = rowsPerPageTemplate;
	}
	
	public enum PaginatorPositions{
		
		TOP("top");
		
		PaginatorPositions(String v){
			this.value = v;
		}
		
		String getValue(){
			return value;
		}
		String value;
	}
	
	public enum SelectionMode{
		
		SINGLE("single"), MULTIPLE("multiple");
		
		SelectionMode(String v){
			this.value = v;
		}
		
		String getValue(){
			return value;
		}
		String value;
	}	
	
}
