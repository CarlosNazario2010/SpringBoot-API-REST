//import br.com.carlosnazario.forum.EnableSwagger2;

//package br.com.carlosnazario.forum.confg.swagger;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import br.com.carlosnazario.forum.modelo.Usuario;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//
//@Configuration
//public class SwaggerConfiguration {
//
//
//	/* Configuracoes para mapeamento e propriedades do Swagger e manipulacao do Token do usuario */
//
//	@Bean
//	public Docket forumApi() {
//		return new Docket(DocumentationType.SWAGGER_2)
//				.select()
//				.apis(RequestHandlerSelectors.basePackage("br.com.carlosnazario.forum"))
//				.paths(PathSelectors.ant("/**"))
//				.build()
//				.ignoredParameterTypes(Usuario.class)
//				.globalOperationParameters(Arrays,asList(
//            		new ParameterBulder()
//            		.name("Authorization")
//            		.description("Header para token JWT")
//            		.modelRef(new ModelRef("string"))
//            		.parameterType("header")
//            		.required(false)
//            		.build()));
//	}
//}

/*
 * Maven Dependence
 * 
 *  	<dependency>
			<groupId>io.springfox</groupId>
			<artifactId>springfox-swagger2</artifactId>
			<version>3.0.0</version>
		</dependency>
*/

/*
 * Anotacacao do application.properties
 * 
 * 	# swagger
	spring.mvc.pathmatch.matching-strategy=ant_path_matcher
 */

/*
 * Anotacao do Forum Aplication
 * 
 * @EnableSwagger2
*/

/*
 * url para a consulta da documentacao web
 * 
 * http://localhost:8080/swagger-ui.html
 * 
 * Obs - eh necessario habilitar o endponit na classe SecurityConfiguration
 * 		tambem eh necessario configurar 
 */

/*
 * A partir da versão 2.6 do Spring Boot houve uma mudança que 
 * impacta na utilização do Springfox Swagger, causando uma 
 * exception ao rodar o projeto. É necessário adicionar a 
 * seguinte propriedade no arquivo application.properties para 
 * que o projeto funcione sem problemas:
 * 
 * spring.mvc.pathmatch.matching-strategy=ant_path_matcher
 * 
 * Obs: Essa solução é incompatível com o módulo do Actuator, 
 * sendo que ao adicionar o actuator no projeto, o mesmo erro 
 * voltará a ocorrer. Para utilizar o Actuator em conjunto com o 
 * Springfox Swagger será necessário então realizar o downgrade 
 * do Spring Boot para alguma versão anterior à versão 2.6.
 * 
 * Para nao ser necessario realizar o downgrade, foi criada a 
 * classe toda comentada, apresentando somente os conceitos de 
 * documentacao de API pelo Swagger
 * 
*/