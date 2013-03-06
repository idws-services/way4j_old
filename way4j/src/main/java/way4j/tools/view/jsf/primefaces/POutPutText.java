package way4j.tools.view.jsf.primefaces;

import way4j.tools.view.jsf.components.Component;
import way4j.tools.view.jsf.components.JSFExpressions;

import com.sun.faces.taglib.html_basic.OutputTextTag;

public class POutPutText extends Component{
	
	@Override
	public OutputTextTag createComponent(){
		
		OutputTextTag outPutText = new OutputTextTag();
		//outPutText.setValue(JSFExpressions.createValueExpression(getValue()));
		
		return outPutText;
		
	}
	
}
