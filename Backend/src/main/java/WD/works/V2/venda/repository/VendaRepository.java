package WD.works.V2.venda.repository;

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
    SELECT FUNCTION('DATE', v.dataVenda), COALESCE(SUM(v.total), 0)
    FROM Venda v
    WHERE v.empresa.id = :empresaId
      AND v.dataVenda >= :inicio
      AND v.dataVenda < :fim
    GROUP BY FUNCTION('DATE', v.dataVenda)
    ORDER BY FUNCTION('DATE', v.dataVenda) ASC
    """)
    List<Object[]> vendasPorPeriodo(
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

}