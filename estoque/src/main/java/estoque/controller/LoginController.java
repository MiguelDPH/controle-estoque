package estoque.controller;

import estoque.models.Usuario;
import estoque.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/logar")
    public ResponseEntity <String> loginUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioLogado = this.usuarioRepository.login(usuario.getEmail(),usuario.getSenha());
        if (usuarioLogado !=null) {
            log.info("Login bem-sucedido para o email: {}", usuario.getEmail());
            return new ResponseEntity<>("Login efetuado com sucesso!", HttpStatus.OK);
        }
        log.error("Erro no login: Credenciais inválidas para o email: {}", usuario.getEmail());
        return new ResponseEntity<>("Email ou senha inválidos.", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(value = "/cadastroUsuario")
    public ResponseEntity<String> cadastroUsuario(@Valid @RequestBody Usuario usuario, BindingResult result) {

        if(result.hasErrors()) {
            log.error("Erros de validação ao cadastrar usuário: {}", result.getAllErrors()); // Use log.error
            return new ResponseEntity<>("Erro na validação: " + result.getAllErrors().getFirst().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        usuarioRepository.save(usuario);
        log.info("Usuário salvo com sucesso: {}", usuario.getNome());
        return new ResponseEntity<>("Usuário cadastrado com sucesso!", HttpStatus.CREATED);
    }
}