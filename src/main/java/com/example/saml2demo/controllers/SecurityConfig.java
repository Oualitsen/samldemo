package com.example.saml2demo.controllers;

import java.net.URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationRequestFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    final String metadataUrl = "https://dev-59088192.okta.com/app/exkfcyn3hpfpvVrio5d7/sso/saml/metadata";
    final String entityId = "http://localhost:8080";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, RelyingPartyRegistrationRepository repository)
            throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests().antMatchers("/**").authenticated();
        http.authorizeHttpRequests(req -> req.antMatchers("/saml/SSO").permitAll());
        http.saml2Login(saml -> {
            saml.relyingPartyRegistrationRepository(repository);
            if (false)
                saml.withObjectPostProcessor(new ObjectPostProcessor<Saml2WebSsoAuthenticationFilter>() {
                    @Override
                    public <O extends Saml2WebSsoAuthenticationFilter> O postProcess(O object) {
                        object.setFilterProcessesUrl("/saml/SSO");
                        return object;
                    }
                });
            ObjectPostProcessor<Saml2WebSsoAuthenticationRequestFilter> processor = new ObjectPostProcessor<>() {
                @Override
                public <O extends Saml2WebSsoAuthenticationRequestFilter> O postProcess(O filter) {
                    filter.setRedirectMatcher(new AntPathRequestMatcher("/saml/SSO"));
                    return filter;
                }
            };
            saml.loginProcessingUrl("/saml/SSO");
            saml.withObjectPostProcessor(processor);
        });
        return http.build();
    }

    @Bean
    RelyingPartyRegistrationRepository relyingPartyRegistration() throws Exception {
        var stream = new URL(metadataUrl).openStream();
        var r = RelyingPartyRegistrations.fromMetadata(stream)
                .registrationId("adfs")
                .assertionConsumerServiceLocation("/saml/SSO")
                .entityId(entityId)
                .assertingPartyDetails(party -> party.wantAuthnRequestsSigned(false))
                .build();

        return new InMemoryRelyingPartyRegistrationRepository(r);
    }
}
