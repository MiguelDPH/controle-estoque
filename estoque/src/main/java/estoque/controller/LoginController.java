package estoque.controller;

import estoque.models.LogDeAcoes;
import estoque.models.Usuario;
import estoque.repository.LogDeAcoesRepository;
import estoque.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import estoque.security.CustomUserDetails;
import estoque.security.CustomUserDetailsService;
import estoque.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogDeAcoesRepository logDeAcoesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    public static class LoginResponse {
        public String token;
        public Long userId;
        public String message;

        public LoginResponse(String token, Long userId, String message) {
            this.token = token;
            this.userId = userId;
            this.message = message;
        }
    }

    @PostMapping("/logar")
    public ResponseEntity<?> loginUsuario(@RequestBody Usuario usuario) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getSenha())
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());

            final String token = jwtUtil.generateToken(userDetails);

            Long userId = ((CustomUserDetails) userDetails).getUserId();

            log.info("Login bem-sucedido para o email: {}", usuario.getEmail());
            logDeAcoesRepository.save(new LogDeAcoes(usuarioRepository.findByEmail(usuario.getEmail()), "LOGIN_SUCESSO", "Usuário logou no sistema."));

            return ResponseEntity.ok(new LoginResponse(token, userId, "Login efetuado com sucesso!"));

        } catch (UsernameNotFoundException e) {
            log.error("Erro no login: Credenciais inválidas (Usuário não encontrado) para o email: {}", usuario.getEmail());
            logDeAcoesRepository.save(new LogDeAcoes(null, "LOGIN_FALHA", "Tentativa de login com email: " + usuario.getEmail() + " (Usuário não encontrado)."));
            return new ResponseEntity<>("Email ou senha inválidos.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Erro no login: Senha incorreta para o email: {}", usuario.getEmail());
            logDeAcoesRepository.save(new LogDeAcoes(null, "LOGIN_FALHA", "Tentativa de login com email: " + usuario.getEmail() + " (Senha incorreta)."));
            return new ResponseEntity<>("Email ou senha inválidos.", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(value = "/cadastroUsuario")
    public ResponseEntity<String> cadastroUsuario(@Valid @RequestBody Usuario usuario, BindingResult result) {
        if(result.hasErrors()) {
            String detalhesErro = result.getAllErrors().getFirst().getDefaultMessage();

            logDeAcoesRepository.save(new LogDeAcoes(
                    null,
                    "VALIDACAO_FALHA",
                    "Tentativa de cadastro falhou. Erro: " + detalhesErro + " | Dados: " + usuario.getEmail()
            ));

            log.error("Erros de validação ao cadastrar usuário: {}", result.getAllErrors());
            return new ResponseEntity<>("Erro na validação: " + detalhesErro, HttpStatus.BAD_REQUEST);
        }

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        usuarioRepository.save(usuario);

        logDeAcoesRepository.save(new LogDeAcoes(
                usuario,
                "CADASTRO_USUARIO",
                "Usuário " + usuario.getNome() + " cadastrado com sucesso."
        ));

        log.info("Usuário salvo com sucesso: {}", usuario.getNome());
        return new ResponseEntity<>("Usuário cadastrado com sucesso!", HttpStatus.CREATED);
    }
}