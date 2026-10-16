package WD.works.V2.auditoria.repository;

import WD.works.V2.auditoria.entity.Auditoria;
import WD.works.V2.auditoria.gravidade.GravidadeAuditoria;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriaRepository
        extends JpaRepository<Auditoria, Long> {

    Page<Auditoria> findByEmpresaIdOrderByDataDesc(
            Long empresaId,
            Pageable pageable
    );

    Page<Auditoria> findByEmpresaIdAndUsuarioIdOrderByDataDesc(
            Long empresaId,
            Long usuarioId,
            Pageable pageable
    );

    Page<Auditoria> findByEmpresaIdAndTipoOrderByDataDesc(
            Long empresaId,
            TipoAuditoria tipo,
            Pageable pageable
    );
    Page<Auditoria> findByEmpresaIdAndGravidadeOrderByDataDesc(
            Long empresaId,
            GravidadeAuditoria gravidade,
            Pageable pageable
    );

    @Query("""
    SELECT a FROM Auditoria a
    WHERE a.empresa.id = :empresaId
      AND a.data >= :inicio
      AND a.data < :fim
    ORDER BY a.data DESC
    """)
    List<Auditoria> findByEmpresaIdAndDataBetween(
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}