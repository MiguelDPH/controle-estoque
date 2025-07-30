package estoque.repository;

import estoque.models.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    @Query(value="select * from controle_estoque.usuario where email = :email and senha = :senha", nativeQuery = true)
    Usuario login(String email, String senha);
}