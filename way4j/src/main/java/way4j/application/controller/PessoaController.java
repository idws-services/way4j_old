package way4j.application.controller;

import javax.faces.bean.RequestScoped;

import org.springframework.stereotype.Controller;

import way4j.application.model.Pessoa;
import way4j.tools.generics.controller.GenericController;

@Controller(value="pessoaController")
@RequestScoped
public class PessoaController extends GenericController<Pessoa>{
	
}