package estoque.repository;

import estoque.models.LogDeAcoes;
import org.springframework.data.repository.CrudRepository;

public interface LogDeAcoesRepository extends CrudRepository<LogDeAcoes, Long> {
}