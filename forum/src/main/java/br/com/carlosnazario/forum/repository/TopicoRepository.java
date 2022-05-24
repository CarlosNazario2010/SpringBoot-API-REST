package br.com.carlosnazario.forum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.carlosnazario.forum.modelo.Topico;

public interface TopicoRepository extends JpaRepository<Topico, Long>{

	// Como o metodo findByCursoNome obviamente nao faz parte da Interface Jpa Repository, devemos declarar a 
	// assinatura do metodo no Repository
	// Filtro por atributo de relacionamento >> curso eh o nome da entidade e nome o nome do atributo
	Page<Topico> findByCursoNome(String nomeCurso, Pageable paginacao);

}


/* Interface que herda os metodos do JpaRepository que possui os 
 * metodos CRUD padrao
 */
/* O SpringData possui o metodo findBy + o nome do atributo e desta 
 * forma cria a query que filtra o resultado
 */

