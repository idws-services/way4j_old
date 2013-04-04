package way4j.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import way4j.application.model.Pessoa;
import way4j.tools.generics.controller.GenericController;

@Controller
@RequestMapping("/pessoa.do")
public class PessoaController extends GenericController<Pessoa>{

	@RequestMapping("/method")
	public void method(){
		System.out.println("##############");
	}
	
}