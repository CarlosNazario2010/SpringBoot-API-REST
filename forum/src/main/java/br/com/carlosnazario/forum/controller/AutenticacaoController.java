package br.com.carlosnazario.forum.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.carlosnazario.forum.confg.security.TokenService;
import br.com.carlosnazario.forum.controller.dto.TokenDto;
import br.com.carlosnazario.forum.controller.form.LoginForm;

@RestController
@RequestMapping("/auth")
@Profile(value = {"prod", "test"})
public class AutenticacaoController {

	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping
	public ResponseEntity<TokenDto> autenticar(@RequestBody @Valid LoginForm form) {
		UsernamePasswordAuthenticationToken dadosLogin	= form.converter();
		
		try {
			Authentication authentication = authManager.authenticate(dadosLogin);
			String token = tokenService.gerarToken(authentication);
			return ResponseEntity.ok(new TokenDto(token, "Bearer"));
		} catch (AuthenticationException e) {
			return ResponseEntity.badRequest().build();
		}
	}
}

/* Como o padrao REST determina que no trafego dos dados nao se deve guardar estado
 * (STATELESS), nao eh uma boa pratica fazer a autenticacao na API via session, com
 * a geracao de cookies. Ao inves disso pode ser gerado um token (com um prazo de 
 * validade) que sera gerado na primeira reguisicao do client e sera solicitado no
 * cabecalho das proximas requisicoes. Caso o prazo do token expire, um novo token
 * deve ser gerado em uma nova autenticacao
 * A geracao do token eh realizado com a dependencia JWT (JsonWebToken)  
 */
