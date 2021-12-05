package pl.kmiecik.ais.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void configure(AuthenticationManagerBuilder authenticationMgr) throws Exception {
        authenticationMgr.inMemoryAuthentication()
                .withUser("tester").password(passwordEncoder().encode("1234")).roles("CAPTAIN")
                .and()
                .withUser("user").password(passwordEncoder().encode("1")).roles("SAILOR");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/register", "/hello").permitAll()
                .antMatchers("/", "/**/fixtures").hasAnyRole("ADMIN","SAILOR", "CAPTAIN")
                .antMatchers("/admin").hasRole("ADMIN")
                .and()
                .formLogin().loginPage("/login").permitAll()
                .and()
                .formLogin().defaultSuccessUrl("/", true)
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        releaseH2Console(http);
    }


    private void releaseH2Console(HttpSecurity http) throws Exception {
        if (activeProfile.equals("dev")) {
            http.csrf().disable();
            http.headers().frameOptions().disable();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
