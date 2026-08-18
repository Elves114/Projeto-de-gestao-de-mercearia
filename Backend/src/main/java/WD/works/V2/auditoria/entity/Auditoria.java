package WD.works.V2.auditoria.entity;

import WD.works.V2.auditoria.tipo.TipoAuditoria;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "auditorias",
        indexes = {

                @Index(
                        name = "idx_auditoria_empresa",
                        columnList = "empresa_id"
                ),

                @Index(
                        name = "idx_auditoria_usuario",
                        columnList = "usuario_id"
                ),

                @Index(
                        name = "idx_auditoria_data",
                        columnList = "data"
                ),

                @Index(
                        name = "idx_auditoria_empresa_data",
                        columnList = "empresa_id, data"
                ),

                @Index(
                        name = "idx_auditoria_tipo",
                        columnList = "tipo"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private TipoAuditoria tipo;

    /*
     * Nome da entidade/tabela afetada.
     *
     * Exemplos:
     * "Venda"
     * "Produto"
     * "Estoque"
     * "Usuario"
     */
    @Column(
            nullable = false,
            length = 100
    )
    private String tabela;

    /*
     * ID do registro afetado.
     *
     * Exemplo:
     * Venda #25 → "25"
     */
    @Column(
            nullable = false,
            length = 100
    )
    private String registo;

    @Column(
            nullable = false,
            length = 1000
    )
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime data;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "empresa_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_auditoria_empresa"
            )
    )
    private Empresa empresa;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_auditoria_usuario"
            )
    )
    private Usuario usuario;

    @PrePersist
    protected void prePersist() {

        if (data == null) {
            data = LocalDateTime.now();
        }
    }
}