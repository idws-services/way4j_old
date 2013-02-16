package way4j.tools.systemBuild.clazz;

import java.io.IOException;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaSourceCodeObject extends SimpleJavaFileObject{

	 	private String qualifiedName ;
	    private String sourceCode ;

	    protected JavaSourceCodeObject(String name, String code) {
	        super(URI.create("string:///" +name.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE);
	        this.qualifiedName = name ;
	        this.sourceCode = code ;
	    }
	 
	    @Override
	    public CharSequence getCharContent(boolean ignoreEncodingErrors)
	            throws IOException {
	        return sourceCode ;
	    }
	 
	    public String getQualifiedName() {
	        return qualifiedName;
	    }
	 
	    public void setQualifiedName(String qualifiedName) {
	        this.qualifiedName = qualifiedName;
	    }
	 
	    public String getSourceCode() {
	        return sourceCode;
	    }
	 
	    public void setSourceCode(String sourceCode) {
	        this.sourceCode = sourceCode;
	    }

}
