package way4j.tools.view.jsf.primefaces;

import javax.faces.component.UIColumn;

import org.primefaces.component.column.Column;

import way4j.tools.view.jsf.components.Component;
import way4j.tools.view.jsf.components.JSFExpressions;

public class PColumn extends Component{
	
	String sortBy;
	String filterBy; 
	int width;
	boolean resizable;
	
	@Override
	public UIColumn createComponent(){
		
		Column column = new Column();
		//column.setSortBy(JSFExpressions.createValueExpression(getSortBy()));
		//column.setFilterBy(JSFExpressions.createValueExpression(getFilterBy()));
		column.setWidth(getWidth());
		column.setResizable(isResizable());
		
		return (UIColumn)column;
		
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getFilterBy() {
		return filterBy;
	}

	public void setFilterBy(String filterBy) {
		this.filterBy = filterBy;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}
	
}
