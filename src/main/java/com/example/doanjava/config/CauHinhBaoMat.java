package com.example.doanjava.config;

import com.example.doanjava.service.TaiKhoanDangNhapService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
public class CauHinhBaoMat {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            TaiKhoanDangNhapService service,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(service);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationSuccessHandler xuLyDangNhapThanhCong() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication auth) -> {
            String luaChon = request.getParameter("userRole");
            boolean benhNhan = auth.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ROLE_BENH_NHAN"));
            boolean bacSi = auth.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ROLE_BAC_SI"));
            boolean admin = auth.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

            boolean hopLe = ("PATIENT".equals(luaChon) && benhNhan)
                    || ("DOCTOR".equals(luaChon) && (bacSi || admin));
            if (!hopLe) {
                SecurityContextHolder.clearContext();
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath() + "/login?roleError");
                return;
            }
            if (benhNhan) {
                response.sendRedirect(request.getContextPath() + "/patient/dashboard");
            } else if (admin) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider,
            AuthenticationSuccessHandler xuLyDangNhapThanhCong) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/xac-thuc-dang-nhap", "/register", "/error", "/css/**", "/js/**", "/images/**", "/bac-si/**").permitAll()
                        // Đặt trước anyRequest(): chặn cả GET/POST, chi tiết, ca khám và xuất CSV.
                        .requestMatchers("/lichhen", "/lichhen/**", "/thongbao", "/catruc").hasAnyRole("BAC_SI", "ADMIN")
                        .requestMatchers("/patient/**").hasRole("BENH_NHAN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/doctor/profile").hasAnyRole("BAC_SI", "ADMIN")
                        .requestMatchers("/doctor/**").hasRole("BAC_SI")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/xac-thuc-dang-nhap")
                        .successHandler(xuLyDangNhapThanhCong)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(ex -> ex.accessDeniedHandler((request, response, denied) -> response.sendError(403)))
                .headers(headers -> headers.cacheControl(cache -> {}))
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation.migrateSession()));
        return http.build();
    }
}
