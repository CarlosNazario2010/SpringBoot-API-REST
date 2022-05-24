package br.com.carlosnazario.forum.confg.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.carlosnazario.forum.modelo.Usuario;
import br.com.carlosnazario.forum.repository.UsuarioRepository;

@Service
public class AutenticacaoService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(username);
		
		if(usuario.isPresent()) {
			return usuario.get();
		}
		throw new UsernameNotFoundException("Dados invalidos");
	}
}

/* 
 * Necessário criar uma classe, implementando a interface UserDetailsService do Spring Security.
 * A classe que implementa essa interface geralmente contém uma lógica para validar as 
 * credenciais de um cliente que está se autenticando.
*/