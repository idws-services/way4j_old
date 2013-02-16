package way4j.tools.systemBuild.clazz;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

public class Compiler {
	
	public static void compile(String canonicalName, String sourceCode){
		/*Creating dynamic java source code file object*/
		SimpleJavaFileObject fileObject = new JavaSourceCodeObject(canonicalName, sourceCode) ;
		JavaFileObject javaFileObjects[] = new JavaFileObject[]{fileObject} ;
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);
		
		/* Prepare a list of compilation units (java source code file objects) to input to compilation task*/
		Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(javaFileObjects);
		
		/*Prepare any compilation options to be used during compilation*/
		//In this example, we are asking the compiler to place the output files under bin folder.
		String[] compileOptions = new String[]{"-d", "bin"} ;
		Iterable<String> compilationOptionss = Arrays.asList(compileOptions);
		
		/*Create a diagnostic controller, which holds the compilation problems*/
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		
		/*Create a compilation task from compiler by passing in the required input objects prepared above*/
		CompilationTask compilerTask = compiler.getTask(null, stdFileManager, diagnostics, compilationOptionss, null, compilationUnits) ;
		
		//Perform the compilation by calling the call method on compilerTask object.
		boolean status = compilerTask.call();
		
		if (!status){//If compilation error occurs
			/*Iterate through each compilation problem and print it*/
			for (Diagnostic diagnostic : diagnostics.getDiagnostics()){
				System.out.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
			}
		}
		try {
			stdFileManager.close() ;//Close the file manager
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
