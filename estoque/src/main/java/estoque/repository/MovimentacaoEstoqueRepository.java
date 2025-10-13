package estoque.repository;

import estoque.models.MovimentacaoEstoque;
import org.springframework.data.repository.CrudRepository;

public interface MovimentacaoEstoqueRepository extends CrudRepository<MovimentacaoEstoque, Long> {
}