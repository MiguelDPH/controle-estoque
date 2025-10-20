package estoque.service;

import estoque.models.MovimentacaoEstoque;
import estoque.models.Produto;
import estoque.models.Usuario;
import estoque.repository.MovimentacaoEstoqueRepository;
import estoque.repository.ProdutoRepository;
import estoque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import estoque.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Service
public class MovimentacaoService {
    private static final Logger log = LoggerFactory.getLogger(MovimentacaoService.class);

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario getUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            Long userId = ((CustomUserDetails) principal).getUserId();

            return usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados."));
        }

        throw new RuntimeException("Usuário não autenticado no contexto de segurança.");
    }

    @Transactional
    public Produto registrarEntrada(MovimentacaoEstoque movimentacao) throws Exception {

        Optional<Produto> produtoOpt = produtoRepository.findById(movimentacao.getProduto().getId());

        if (produtoOpt.isEmpty()) {
            throw new Exception("Produto não encontrado.");
        }

        Produto produto = produtoOpt.get();
        Integer quantidadeEntrada = movimentacao.getQuantidade();

        Integer novaQuantidade = produto.getQuantidadeEstoque() + quantidadeEntrada;
        produto.setQuantidadeEstoque(novaQuantidade);
        produtoRepository.save(produto);

        movimentacao.setValorUnidadeHistorico(produto.getValorUnidade());

        movimentacao.setTipo("ENTRADA");
        movimentacao.setUsuario(getUsuarioLogado());
        movimentacaoRepository.save(movimentacao);

        return produto;
    }

    @Transactional
    public Produto registrarSaida(MovimentacaoEstoque movimentacao) throws Exception {

        Optional<Produto> produtoOpt = produtoRepository.findById(movimentacao.getProduto().getId());

        if (produtoOpt.isEmpty()) {
            throw new Exception("Produto não encontrado.");
        }

        Produto produto = produtoOpt.get();
        Integer quantidadeAtual = produto.getQuantidadeEstoque();
        Integer quantidadeSaida = movimentacao.getQuantidade();

        if (quantidadeAtual < quantidadeSaida) {
            throw new IllegalArgumentException("Estoque insuficiente. Quantidade atual: " + quantidadeAtual);
        }

        Integer novaQuantidade = quantidadeAtual - quantidadeSaida;
        produto.setQuantidadeEstoque(novaQuantidade);
        produtoRepository.save(produto);

        movimentacao.setValorUnidadeHistorico(produto.getValorUnidade());

        movimentacao.setTipo("SAIDA");
        movimentacao.setUsuario(getUsuarioLogado());
        movimentacaoRepository.save(movimentacao);

        if (novaQuantidade <= produto.getEstoqueMinimo()) {
            log.warn("ALERTA DE ESTOQUE MÍNIMO: O produto {} (ID: {}) atingiu o nível de estoque mínimo ({}). Estoque atual: {}",
                    produto.getNome(), produto.getId(), produto.getEstoqueMinimo(), novaQuantidade);
        }

        return produto;
    }

    public Iterable<MovimentacaoEstoque> listarMovimentacoes(Long produtoId) {
        if (produtoId != null) {
            return movimentacaoRepository.findByProdutoId(produtoId);
        }
        return movimentacaoRepository.findAll();
    }
}