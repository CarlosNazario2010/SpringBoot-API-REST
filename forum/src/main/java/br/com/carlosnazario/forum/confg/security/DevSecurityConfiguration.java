	package br.com.carlosnazario.forum.confg.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/*
 * Para mudar o profile da aplicacao eh so colocar o seguinte comando
 * nos argumentos da variaveis VM no Run Configuration:
 * 		Dspring.profiles.active=dev    		OU
 * 		Dspring.profiles.active=prod
 */

@EnableWebSecurity
@Configuration
@Profile("dev")		// configuracao de seguranca em ambiente de desenvolvimento
public class DevSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/**").permitAll()
			.and().csrf().disable();
	}
}
