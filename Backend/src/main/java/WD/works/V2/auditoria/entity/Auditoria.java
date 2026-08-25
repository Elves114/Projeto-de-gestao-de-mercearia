package WD.works.V2.auditoria.entity;

import WD.works.V2.auditoria.gravidade.GravidadeAuditoria;
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
                ),

                @Index(
                        name = "idx_auditoria_gravidade",
                        columnList = "gravidade"
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

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private GravidadeAuditoria gravidade;

    @Column(
            nullable = false,
            length = 100
    )
    private String tabela;

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

    /*
     * Endereço IP de onde a operação foi realizada.
     */
    @Column(length = 45)
    private String ip;

    /*
     * Método HTTP utilizado.
     *
     * Exemplos:
     * GET
     * POST
     * PUT
     * PATCH
     * DELETE
     */
    @Column(length = 10)
    private String metodo;

    /*
     * Endpoint utilizado.
     *
     * Exemplo:
     * /api/produtos/15
     */
    @Column(length = 500)
    private String endpoint;

    /*
     * Estado do registro antes da operação.
     *
     * Será armazenado posteriormente como JSON.
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String dadosAntigos;

    /*
     * Estado do registro depois da operação.
     *
     * Será armazenado posteriormente como JSON.
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String dadosNovos;

    /*
     * Dados enviados na requisição.
     *
     * Será armazenado posteriormente como JSON.
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String payload;

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

        if (gravidade == null) {
            gravidade = GravidadeAuditoria.INFO;
        }
    }
}