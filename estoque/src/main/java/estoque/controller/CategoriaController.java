package estoque.controller;

import estoque.models.Categoria;
import estoque.models.LogDeAcoes;
import estoque.models.Usuario;
import estoque.repository.CategoriaRepository;
import estoque.repository.LogDeAcoesRepository;
import estoque.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LogDeAcoesRepository logDeAcoesRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario getUsuarioLogado() {
        return usuarioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Usuário de registro (ID 1) não encontrado."));
    }

    @GetMapping
    public ResponseEntity<Iterable<Categoria>> listarCategorias() {
        return new ResponseEntity<>(categoriaRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoriaPorId(@PathVariable Long id) {
        Optional<Categoria> categoria = categoriaRepository.findById(id);

        if (categoria.isPresent()) {
            return new ResponseEntity<>(categoria.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<String> cadastrarCategoria(@Valid @RequestBody Categoria categoria, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>("Erro na validação: " + result.getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        logDeAcoesRepository.save(new LogDeAcoes(getUsuarioLogado(), "CADASTRO_CATEGORIA", "Categoria ID: " + categoriaSalva.getId() + ", Nome: " + categoriaSalva.getNome()));

        return new ResponseEntity<>("Categoria cadastrada com sucesso!", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editarCategoria(@PathVariable Long id, @Valid @RequestBody Categoria dadosNovos, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>("Erro na validação: " + result.getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);

        if (categoriaExistente.isPresent()) {
            Categoria categoria = categoriaExistente.get();
            categoria.setNome(dadosNovos.getNome());

            Categoria categoriaAtualizada = categoriaRepository.save(categoria);

            logDeAcoesRepository.save(new LogDeAcoes(getUsuarioLogado(), "EDICAO_CATEGORIA", "Categoria ID: " + categoriaAtualizada.getId() + " alterada."));

            return new ResponseEntity<>("Categoria atualizada com sucesso!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Categoria não encontrada.", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirCategoria(@PathVariable Long id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);

            logDeAcoesRepository.save(new LogDeAcoes(getUsuarioLogado(), "EXCLUSAO_CATEGORIA", "Categoria ID: " + id + " excluída."));

            return new ResponseEntity<>("Categoria excluída com sucesso!", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Categoria não encontrada.", HttpStatus.NOT_FOUND);
        }
    }
}