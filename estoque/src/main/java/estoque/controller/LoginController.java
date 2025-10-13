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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogDeAcoesRepository logDeAcoesRepository;

    @PostMapping("/logar")
    public ResponseEntity<String> loginUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioLogado = this.usuarioRepository.login(usuario.getEmail(),usuario.getSenha());

        if (usuarioLogado !=null) {
            log.info("Login bem-sucedido para o email: {}", usuario.getEmail());
            logDeAcoesRepository.save(new LogDeAcoes(usuarioLogado, "LOGIN_SUCESSO", "Usuário logou no sistema."));
            return new ResponseEntity<>("Login efetuado com sucesso!", HttpStatus.OK);
        }

        log.error("Erro no login: Credenciais inválidas para o email: {}", usuario.getEmail());
        logDeAcoesRepository.save(new LogDeAcoes(null, "LOGIN_FALHA", "Tentativa de login com email: " + usuario.getEmail()));
        return new ResponseEntity<>("Email ou senha inválidos.", HttpStatus.UNAUTHORIZED);
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

        usuarioRepository.save(usuario);
        log.info("Usuário salvo com sucesso: {}", usuario.getNome());
        logDeAcoesRepository.save(new LogDeAcoes(usuario, "CADASTRO_USUARIO", "Usuário " + usuario.getNome() + " cadastrado."));
        return new ResponseEntity<>("Usuário cadastrado com sucesso!", HttpStatus.CREATED);
    }
}