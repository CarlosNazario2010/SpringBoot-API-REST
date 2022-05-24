package br.com.carlosnazario.forum.confg.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.carlosnazario.forum.modelo.Usuario;
import br.com.carlosnazario.forum.repository.UsuarioRepository;

public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {

	/*
	 * Não é possível fazer injeção de dependências com a anotação @Autowired na classe,
	 * porque ela não é um bean gerenciado pelo Spring. O filtro eh instanciado manualmente 
	 * na classe SecurityConfiguration. Dessa forma foi criado um construtor na classe 
	 * AutenticacaoViaTokenFilter que recebe um tokenService. Dessa forma sempre que 
	 * esta classe eh chamada, um objeto TokenService sera criado
	 */
	private TokenService tokenService;
	
	// O mesmo dito para o tokenService vale para o usuarioRepository
	private UsuarioRepository usuarioRepository;
	
	public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
		this.tokenService = tokenService;
		this.usuarioRepository = usuarioRepository;
	}
	
	//classe que recupera o token do cliente, valida e, se valido, autentica o usuario
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String token = recuperarToken(request);
		boolean valido = tokenService.isTokenValido(token);
		if (valido) {
			autenticaCliente(token);
		}
		filterChain.doFilter(request, response);
	}

	private void autenticaCliente(String token) {
		Long idUsuario = tokenService.getIdUsuario(token);
		Usuario usuario = usuarioRepository.findById(idUsuario).get();
		
		UsernamePasswordAuthenticationToken authentication = 
				new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private String recuperarToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		
		if(token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}
		return token.substring(7, token.length());
	}
}
