package com.zetta.minhasfinancas.model.repository;

import com.zetta.minhasfinancas.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Spring query methods
     *  podia ser findByNome(String ome);
     *  ou findByEmailAndNome(String email, String nome);
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * select * from usuario where exists e vai procurar usuario
     * com esse email.
     */
    boolean existsByEmail(String email);
}
