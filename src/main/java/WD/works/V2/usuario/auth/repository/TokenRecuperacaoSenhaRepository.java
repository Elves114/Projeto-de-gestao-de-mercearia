package WD.works.V2.usuario.auth.repository;

import WD.works.V2.usuario.auth.entity.TokenRecuperacaoSenha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRecuperacaoSenhaRepository
        extends JpaRepository<TokenRecuperacaoSenha, Long> {

    Optional<TokenRecuperacaoSenha> findByTokenHash(String tokenHash);
}