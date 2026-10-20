package WD.works.V2.usuario.auth.repository;

import WD.works.V2.usuario.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            UPDATE RefreshToken rt
               SET rt.revogado = true,
                   rt.revogadoEm = :agora
             WHERE rt.usuario.id = :usuarioId
               AND rt.revogado = false
            """)
    int revogarTodosDoUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("agora") LocalDateTime agora
    );

    @Modifying
    @Query("""
            DELETE FROM RefreshToken rt
             WHERE rt.expiracao < :limite
                OR (rt.revogado = true AND rt.revogadoEm < :limite)
            """)
    int limparAntigos(@Param("limite") LocalDateTime limite);

    long countByUsuarioIdAndRevogadoFalse(Long usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.tokenHash = :hash")
    Optional<RefreshToken> findByTokenHashComLock(@Param("hash") String hash);

}
