package estoque.controller;

import estoque.models.MovimentacaoEstoque;
import estoque.models.Produto;
import estoque.service.MovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

@RestController
@RequestMapping("/movimentacao")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @PostMapping("/entrada")
    public ResponseEntity<String> registrarEntrada(@Valid @RequestBody MovimentacaoEstoque movimentacao, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>("Erro de validação: " + result.getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            Produto produtoAtualizado = movimentacaoService.registrarEntrada(movimentacao);
            return new ResponseEntity<>("Entrada registrada. Novo estoque de " + produtoAtualizado.getNome() + ": " + produtoAtualizado.getQuantidadeEstoque(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/saida")
    public ResponseEntity<String> registrarSaida(@Valid @RequestBody MovimentacaoEstoque movimentacao, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>("Erro de validação: " + result.getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }

        try {
            Produto produtoAtualizado = movimentacaoService.registrarSaida(movimentacao);
            return new ResponseEntity<>("Saída registrada. Novo estoque de " + produtoAtualizado.getNome() + ": " + produtoAtualizado.getQuantidadeEstoque(), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Iterable<MovimentacaoEstoque>> listarMovimentacoes() {
        return new ResponseEntity<>(movimentacaoService.listarMovimentacoes(), HttpStatus.OK);
    }
}