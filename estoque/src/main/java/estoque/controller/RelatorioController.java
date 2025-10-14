package estoque.controller;

import estoque.models.LogDeAcoes;
import estoque.models.MovimentacaoEstoque;
import estoque.models.Produto;
import estoque.repository.LogDeAcoesRepository;
import estoque.repository.MovimentacaoEstoqueRepository;
import estoque.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Autowired
    private LogDeAcoesRepository logDeAcoesRepository;

    @GetMapping("/estoque")
    public ResponseEntity<Iterable<Produto>> relatorioEstoqueAtual() {
        return new ResponseEntity<>(produtoRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/logs")
    public ResponseEntity<Iterable<LogDeAcoes>> relatorioLogs() {
        return new ResponseEntity<>(logDeAcoesRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/movimentacao")
    public ResponseEntity<Iterable<MovimentacaoEstoque>> relatorioMovimentacoes(
            @RequestParam(required = false) String tipo, // ENTRADA ou SAIDA
            @RequestParam(required = false) Long produtoId) {

        Iterable<MovimentacaoEstoque> todasMovimentacoes = movimentacaoRepository.findAll();

        return ResponseEntity.ok(StreamSupport.stream(todasMovimentacoes.spliterator(), false)
                .filter(mov -> tipo == null || mov.getTipo().equalsIgnoreCase(tipo))
                .filter(mov -> produtoId == null || mov.getProduto().getId().equals(produtoId))
                .collect(Collectors.toList()));
    }
}