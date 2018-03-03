package security.spring

import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}

/**
  * Created by ajos21 on 3/3/2018.
  */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = Array("security.spring"))
class CustomSecurityConfig extends  WebSecurityConfigurerAdapter{

  @Bean(name = Array(BeanIds.AUTHENTICATION_MANAGER))
  override def authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

}
