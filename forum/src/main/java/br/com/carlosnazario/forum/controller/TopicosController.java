package br.com.carlosnazario.forum.controller;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.carlosnazario.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.carlosnazario.forum.controller.dto.TopicoDto;
import br.com.carlosnazario.forum.controller.form.AtualizacaoTopicoForm;
import br.com.carlosnazario.forum.controller.form.TopicoForm;
import br.com.carlosnazario.forum.modelo.Topico;
import br.com.carlosnazario.forum.repository.CursoRepository;
import br.com.carlosnazario.forum.repository.TopicoRepository;

@RestController
@RequestMapping("topicos")
public class TopicosController {

	@Autowired	// injecao de dependencia 
	private TopicoRepository topicoRepository;
	
	@Autowired
	private CursoRepository cursoRepository;
	
	
//	@RequestMapping(value = "/topicos", method = RequestMethod.GET)   // como o RequestMaping anotado na classe nao eh necessario a anotacao do path da URI no metodo
//	@ResponseBody				// com o RestController nao eh necessario anotar cada metodo que se deseja devolver os dados da API com a anotacao @RequestBody
	//@RequestParam informa que os parametros da requisicao virao via url e nao no corpo da requisicao
	
	// Declaracao do metodo listar usando parametros soltos no metodo
//	@GetMapping
//	public Page<TopicoDto> listar(@RequestParam(required = false) 
//			String nomeCurso, @RequestParam int pagina, @RequestParam 
//			int qtd, @RequestParam String ordenacao ) {

	// Declaracao do metodo listar recebendo um Pageable como parametro
	// Obs - necessita da anotacao @EnableSpringDataWebSupport na classe 
	// ForumApplication (que possui o metodo main da aplicacao)
	// A anotacao @PageableDefault permite a configuracao padrao da paginacao
	// caso o client nao passar os parametros
	// A Anotacao @Cacheable permite que o metodo seja armazenado em cahe
	// Obs - necessita da anotacao @EnableCaching na classe ForumApplication 
	// (que possui o metodo main da aplicacao)
	
	@GetMapping
	@Cacheable(value = "listaDeTopicos")
	public Page<TopicoDto> listar(@RequestParam(required = false) String nomeCurso,
			@PageableDefault(sort = "id", direction = Direction.ASC, page = 0, size = 10) 
			Pageable paginacao) {

		// Desnecessario se utilizado o Pageable como argumento do metodo
//		Pageable paginacao = PageRequest.of(pagina, qtd, Direction.ASC, ordenacao);
		
		if(nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDto.converter(topicos);
		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(nomeCurso, paginacao);
			return TopicoDto.converter(topicos);
		}
	}	 

	// O @ResqustBody informa que o parametro topico deve ser pego no corpo da requisicao e nao na url (ccmo foi no metodo lista)
	// O @Valid informa que os parametros anotados na classe serao validados pelo Bean Validation
	// O @CacheEvict limpa o cache. No caso, quando um novo cadastro eh realizado se deve atualizar a 
	// a pagina em cache do usuario que consulta a lista, para que esta nao exiba informacoes desatualizadas
	
	@PostMapping
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);
		
		// com a implementacao abaixo eh devolvido ao client o response com codigo 201 (created) 
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}
	
	@GetMapping("/{id}")
	// @PathVariable informa que o id vira no path da URL e nao como parametro da requisicao
	public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id) {
		Optional<Topico> topico = topicoRepository.findById(id);
		if(topico.isPresent()) {
			return ResponseEntity.ok(new DetalhesDoTopicoDto(topico.get()));
		}
		return ResponseEntity.notFound().build();
	}
	
	@PutMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	// @Transaction informa a JPA para que seja realizado o commit no banco de dados
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form) {
//		Topico topico = form.atualizar(id, topicoRepository);
//		return ResponseEntity.ok(new TopicoDto(topico));
		Optional<Topico> optional = topicoRepository.findById(id);
		if(optional.isPresent()) {
			Topico topico = form.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
		}
		return ResponseEntity.notFound().build();
		
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<?> remover(@PathVariable Long id) {
//		topicoRepository.deleteById(id);
//		return ResponseEntity.ok().build();
		Optional<Topico> optional = topicoRepository.findById(id);
		if(optional.isPresent()) {
			topicoRepository.deleteById(id);
				return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
}

/* REST - Representation State Transfer
 * Modelo de arquitetura para sistames distribuidos (sistemas em que um ou mais sistemas
 * conversam entre si. Baseado no protocolo HTTP da web usado em APIs para transferencia 
 * de dados.
 * Uma aplicacao REST geerencia os recursos da API (como Aluno, Topico, Resposta, ou Curso). 
 * Um identificados de recursos (URI) mapeia a rota HTTP para o recurso: Aluno(/alunos)
 * Os verbos (ou metodos) HTTP sao usados para manipular os recursos como:
 * GET/alunos, POST/alunos, PUT/alunos/{id} ou DELETE/alunos/{id}
 * A representacao do recurso (Media Types) sao os formatos em que os recursos sao trafegados
 * como:
 *	JSON: "aluno":{
 *				"nome":"Fulano",
 *				"email":"fulano@email.com"
 *		  }		
 *	XML:  <aluno>
 *				<nome>Fulano</nome>
 *				<email>fulano@email.com</email>
 *		  </aluno>
 */
/*	O h2 eh um banco em memoria, sendo assim ele eh resetado toda a vez que o servidor
 * eh derrubado. Para consulta-lo eh so digitar o path cadastrado na applicarion,properties
 * No caso : spring.h2.console.path=/h2-console >>> http://localhost:8080/h2-console
 */
/* Nao constuma ser boa pratica devolver a classe de dominio diretamente no controller
 * visto que podem haver dados que nao ser quer e nem devem ser trafegados (uma classe 
 * usuario pode ter uma atributo senha, por exemplo, o qual nao seria uma boa pratica
 * ser trafegado na resposta da requisicao ). Desta forma se encapsula os atributos 
 * que se deseja ser trafegados via uma classe DTO (Data Transfer Object)
*/ 
/* A comunicacao na transferencia de recursos eh stateless, ou seja nao salva o estado
 * dos recursos. Embora possa ser guardada uma sessao do usuario por meio de um cookie
 * (uma especie de identificador do usuario que consome um recurso de uma API 
*/
/* A anotacao @Autowired permite injetar uma dependencia das Entidades
 * do pacato repository. Como estes possuem metodos que manipulam o
 * banco de dados, herdandos da JpaRepository, estes podem ser 
 * chamados nos endpoints dos contollers 
*/
/* O Jpa exige que as Entidade possuam o construtor padrao da classe,
 * sem parametros
*/
/* Ja foi mencionado que nao eh uma boa pratica passar a entidade nos controllers
 * Na verdade, ha uma convencao que no recebimento de dados (usando o metodo GET) se 
 * utiliza o padrao DTO. No caso de envio de dados pos parte do client (usando o metodo
 * POST), se utiliza o padrao FORM
 */
/* O verbo HTTP PUT geralmente eh usado para atualizar todos os dados ja cadastrados no banco
 * O verbo PATCH seria usado para atualizar um dado especifico
 */
/* O método getOne(), por padrão, se não achar o elemento vai dar erro. 
 * Até se poderia fazer um Try/Catch, se cair no catch devolver o "404", 
 * mas tem outro jeito. Existe outro método nos Repositorys chamado findById(). 
 * Ele também tem a mesma lógica. Você passa um id e ele vai fazer uma 
 * consulta filtrando pelo id no banco de dados, só que se ele não encontrar, 
 * ele não joga exception, como o metodo getById. 
 * 
 * O retorno do findById() não é a entidade topico. Ele devolve um Optional, 
 * que é uma classe que veio na API do Java 8. Entao o retorno de ser modificado
 * para ser um Optional<topico> (isto é, para ser um Optional de tópico).
 */
 /* A interface Pageable permite a paginacao dos dados, ao contrario do List
  * que devolve somente uma lista com os dados. A classe PageRequest tambem
  * permite a ordenacao dos dados
  */
/*	Exemplo de url para receber a paginacao, se o metodo recebe-lo como 
 * 	parametro do metodo que faz a listagem dos dados
 * 	http://localhost:8080/topicos?page=0&size=3&sort=titulo,desc
 */
/*	A utilizacao do cache deve ser analisada com cuidado visto que o cache
 * 	elimina a necessidade de consulta ao banco de dados, mas limpar um cache
 * 	tambem exige processamento. Dessa forma, se um recurso eh solicitado com
 * 	muito frequencia, cachea-lo demandaria mais processacmento do que ir ao
 * 	banco de dados e realmente pegar os registros
 */
