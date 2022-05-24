package br.com.carlosnazario.forum.controller.dto;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;

import br.com.carlosnazario.forum.modelo.Topico;

public class TopicoDto {

	private Long id;
	private String titulo;
	private String mensagem;
	private LocalDateTime dataCriacao;
	
	
	public TopicoDto(Topico topico) {
		this.id = topico.getId();
		this.titulo = topico.getTitulo();
		this.mensagem = topico.getMensagem();
		this.dataCriacao = topico.getDataCriacao();
	}
	
	public Long getId() {
		return id;
	}
	public String getTitulo() {
		return titulo;
	}
	public String getMensagem() {
		return mensagem;
	}
	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public static Page<TopicoDto> converter(Page<Topico> topicos) {
		
		return topicos.map(TopicoDto::new);
		
		// implementacao quando se utilizava retorno List ao inves de Page
//		return topicos.stream().map(TopicoDto::new).collect(Collectors.toList());
	}
}

/* Em geral as classes dto vao ao banco de dados e devolvem os dados ao cliente
 * via as classes de controller
 * As classes form sao as classes que pegam os dados da requisicao do usuario
 * e passam ao controller
 */
/* Classes DTO (data transfer object) servem para encapsular os dados que seraro
 * trafegados no controller para acesso ao banco de dados e na devolucao para 
 * o usuario. Como nem sempre desejamos passar todos os dados da 
 * classe (que no fundo eh um Entidade do JPA) podemos por na classe DTO apenas 
 * os atributos que desejamos que sejam trafegados e criamos um metodo que converte
 * os valors dos atributos da classe de dominio em atributos da classe DTO 
 * correspondente a essa classe de dominio */
