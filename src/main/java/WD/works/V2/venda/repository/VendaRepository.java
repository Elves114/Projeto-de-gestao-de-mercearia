package WD.works.V2.venda.repository;

import WD.works.V2.dashboard.dto.VendaGraficoResponse;
import WD.works.V2.venda.entity.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VendaRepository
        extends JpaRepository<Venda, Long> {

    Optional<Venda> findByIdAndEmpresaId(
            Long id,
            Long empresaId
    );

    Page<Venda> findByEmpresaIdOrderByDataVendaDesc(
            Long empresaId,
            Pageable pageable
    );

    boolean existsByIdAndEmpresaId(
            Long id,
            Long empresaId
    );

    /* * ============================================================ *
     PESQUISA / LISTAGEM DE VENDAS
     * ============================================================ *
     * Todos os filtros são opcionais. * * Se o filtro for null, ele não é aplicado.*
     * A venda continua sendo filtrada pela empresa
     *do usuário autenticado. */

    @Query("""
        SELECT v FROM Venda v 
        WHERE v.empresa.id = :empresaId 
          AND (:vendaId IS NULL OR v.id = :vendaId) 
          AND (CAST(:inicio AS LocalDateTime) IS NULL OR v.dataVenda >= :inicio) 
          AND (CAST(:fim    AS LocalDateTime) IS NULL OR v.dataVenda <  :fim) 
          AND (:usuarioId IS NULL OR v.usuario.id = :usuarioId) 
        ORDER BY v.dataVenda DESC
        """)
    Page<Venda> pesquisar(
            @Param("empresaId") Long empresaId,
            @Param("vendaId") Long vendaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );

    /* * ============================================================ *
                                 DASHBOARD
     * ============================================================ */
    @Query("SELECT COUNT(v) FROM Venda v WHERE v.empresa.id = :empresaId AND v.dataVenda >= :inicio AND v.dataVenda < :fim ")
    long countVendasPorPeriodo(
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query(" SELECT COALESCE(SUM(v.total), 0) FROM Venda v WHERE v.empresa.id = :empresaId AND v.dataVenda >= :inicio AND v.dataVenda < :fim ")
    BigDecimal somarVendasPorPeriodo(
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query(" SELECT COALESCE(SUM(v.lucroTotal), 0) FROM Venda v WHERE v.empresa.id = :empresaId AND v.dataVenda >= :inicio AND v.dataVenda < :fim ")
    BigDecimal somarLucroPorPeriodo(
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("""
        SELECT new WD.works.V2.dashboard.dto.VendaGraficoResponse(
            CAST(v.dataVenda AS LocalDate),
            COALESCE(SUM(v.total), 0.0)
        )
        FROM Venda v
        WHERE v.empresa.id = :empresaId
          AND v.dataVenda >= :inicio
          AND v.dataVenda < :fim
        GROUP BY CAST(v.dataVenda AS LocalDate)
        ORDER BY CAST(v.dataVenda AS LocalDate) ASC
        """)
    List<VendaGraficoResponse> vendasPorPeriodo(
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

}