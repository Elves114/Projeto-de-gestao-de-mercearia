package WD.works.V2.auditoria.repository;

import WD.works.V2.auditoria.entity.Auditoria;
import WD.works.V2.auditoria.tipo.TipoAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
}