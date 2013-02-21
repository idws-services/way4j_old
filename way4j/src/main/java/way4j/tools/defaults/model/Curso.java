package way4j.tools.defaults.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import way4j.tools.utils.GenericUtils;

@Table
@Entity
public class Curso implements Serializable{
	
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="curso_seq")
	@SequenceGenerator(initialValue=1, name="curso_seq", sequenceName="curso_seq" )
	@Id
	private long idCurso;
	
	@Column
	private String nome;
	
	public long getIdCurso() {
		return idCurso;
	}
	public void setIdCurso(long idCurso) {
		this.idCurso = idCurso;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Override
	public String toString(){
		return GenericUtils.gson.toJson(this);
	}
	
}
