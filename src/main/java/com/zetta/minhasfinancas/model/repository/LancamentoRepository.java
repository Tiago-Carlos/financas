package com.zetta.minhasfinancas.model.repository;

import com.zetta.minhasfinancas.model.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    /**
     * Ao extender JpaRepository, o spring vai injetar a implementação dessa interface
     * em tempo de execução, então não precisa implementar nada
     */
}
