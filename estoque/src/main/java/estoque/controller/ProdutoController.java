package estoque.controller;

import estoque.models.LogDeAcoes;
import estoque.models.Produto;
import estoque.models.Usuario;
import estoque.repository.LogDeAcoesRepository;
import estoque.repository.ProdutoRepository;
import estoque.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private LogDeAcoesRepository logDeAcoesRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario getUsuarioLogado() {
        return usuarioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Usuário de registro (ID 1) não encontrado."));
    }

    @GetMapping
    public ResponseEntity<Iterable<Produto>> listarProdutos() {
        return new ResponseEntity<>(produtoRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);

        if (produto.isPresent()) {
            return new ResponseEntity<>(produto.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<String> cadastrarProduto(@Valid @RequestBody Produto produto, BindingResult result) {

        produtoRepository.save(produto);
        logDeAcoesRepository.save(new LogDeAcoes(getUsuarioLogado(), "CADASTRO_PRODUTO", "Produto ID: " + produto.getId() + ", Nome: " + produto.getNome()));
        return new ResponseEntity<>("Produto cadastrado com sucesso!", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editarProduto(@PathVariable Long id, @Valid @RequestBody Produto dadosNovos, BindingResult result) {

        if (result.hasErrors()) {
            return new ResponseEntity<>("Erro na validação: " + result.getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        Optional<Produto> produtoExistente = produtoRepository.findById(id);

        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();

            produto.setNome(dadosNovos.getNome());
            produto.setCodigo(dadosNovos.getCodigo());
            produto.setDescricao(dadosNovos.getDescricao());
            produto.setValorUnidade(dadosNovos.getValorUnidade());
            produto.setEstoqueMinimo(dadosNovos.getEstoqueMinimo());
            produto.setCategoria(dadosNovos.getCategoria());

            produtoRepository.save(produto);

            logDeAcoesRepository.save(new LogDeAcoes(getUsuarioLogado(), "EDICAO_PRODUTO", "Produto ID: " + produto.getId() + " alterado."));
            return new ResponseEntity<>("Produto atualizado com sucesso!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Produto não encontrado.", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirProduto(@PathVariable Long id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
            logDeAcoesRepository.save(new LogDeAcoes(getUsuarioLogado(), "EXCLUSAO_PRODUTO", "Produto ID: " + id + " excluído."));
            return new ResponseEntity<>("Produto excluído com sucesso!", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("Produto não encontrado.", HttpStatus.NOT_FOUND);
        }
    }
}