package WD.works.V2.alertaStock.entity;

import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.estoque.entity.Estoque;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "alertas_stock",
        indexes = {
                @Index(
                        name = "idx_alerta_stock_empresa",
                        columnList = "empresa_id"
                ),
                @Index(
                        name = "idx_alerta_stock_estoque",
                        columnList = "estoque_id"
                ),
                @Index(
                        name = "idx_alerta_stock_ativo",
                        columnList = "ativo"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertaStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Indica se o alerta ainda está ativo.
     *
     * true  → stock continua baixo
     * false → stock voltou ao normal
     */
    @Column(
            nullable = false
    )
    private boolean ativo = true;

    /*
     * Momento em que o alerta foi criado.
     */
    @Column(
            name = "criado_em",
            nullable = false,
            updatable = false
    )
    private LocalDateTime criadoEm;

    /*
     * Momento em que o alerta foi resolvido.
     *
     * Será null enquanto o alerta estiver ativo.
     */
    @Column(
            name = "resolvido_em"
    )
    private LocalDateTime resolvidoEm;

    /*
     * Estoque que originou o alerta.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "estoque_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_alerta_stock_estoque"
            )
    )
    private Estoque estoque;

    /*
     * Empresa proprietária do alerta.
     *
     * Mantemos esta relação diretamente para garantir
     * o isolamento multiempresa e facilitar consultas.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "empresa_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_alerta_stock_empresa"
            )
    )
    private Empresa empresa;

    @PrePersist
    protected void aoCriar() {

        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}