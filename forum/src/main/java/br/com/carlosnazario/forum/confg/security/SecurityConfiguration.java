package br.com.carlosnazario.forum.confg.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.carlosnazario.forum.repository.UsuarioRepository;

@EnableWebSecurity
@Configuration
@Profile(value = {"prod", "test"})	// configuracoes de seguranca em ambiente de producao e de testes
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private AutenticacaoService autenticacaoService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	// configuracoes de autenticacao (ex login, senha)	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(autenticacaoService)
			.passwordEncoder(new BCryptPasswordEncoder());	
	}
	
	// configuracoes de autorizacao (ex quem pode acessar cada endpoint, url)
	/*
	 * Permitido sem altenticacao os endpoints com metodo GET e o POST do Login,
	 * desativa a camada de protecao csrf, informa que a politica de acesso a API 
	 * eh Stateles e informa que o FiltroViaToken criado, rodara antes do filtro
	 * de autenticacao padrao do Spring
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/topicos").permitAll()
			.antMatchers(HttpMethod.GET, "/topicos/*").permitAll()			
			.antMatchers(HttpMethod.POST, "/auth").permitAll()
			.antMatchers(HttpMethod.GET, "/actuator/**").permitAll()
			.antMatchers(HttpMethod.DELETE, "/topicos/*").hasRole("MODERADOR")
			.anyRequest().authenticated()
			.and().csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenService, usuarioRepository), UsernamePasswordAuthenticationFilter.class);
	}
	
	/*
	 * Configuracoes de recursos estaticos (ex, css, js, imagens). 
	 * Usado para configurar os acessos aos endpoints das propriedades 
	 * estaticas do Swagger, visto que o endpoint do swagger dispara 
	 * varias chamadas para recursos estaticos
	 * Como este nao sera utilizado, o codigo estara comentado
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
//		web.ignoring().antMatchers("/**.html", "/v2/api-docs", "/webjars/**","/configuration/**", "/swagger-resources/**");
	}
	
	/*
	 * Cria um gerenciador de autenticacoes.
	 * O @Bean devolve um bean que pode ser manipulado pelo Spring.
	 * Sem ele nao eh possivel a injecao de dependencia no AutenticationController
	 */
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	
//	// gerando o hash da senha com o BCrypt '123456'	
//	public static void main(String[] args) {
//		System.out.println(new BCryptPasswordEncoder().encode("123456"));
//		
//		// hash da senha 123456		
//		$2a$10$kSK2BmrSmZBTu82N1re7L.Ge0lN0Y9tj1pTCbejq3U.AG3Ik66suC
//	}
}
