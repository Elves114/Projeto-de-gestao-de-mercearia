package WD.works.V2.movimentoStock.entity;

import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.movimentoStock.acoes.Acao;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "movimentos_stock",
        indexes = {
                @Index(
                        name = "idx_movimento_empresa",
                        columnList = "empresa_id"
                ),
                @Index(
                        name = "idx_movimento_produto",
                        columnList = "produto_id"
                ),
                @Index(
                        name = "idx_movimento_data",
                        columnList = "data"
                ),
                @Index(
                        name = "idx_movimento_empresa_data",
                        columnList = "empresa_id, data"
                ),
                @Index(
                        name = "idx_movimento_empresa_produto_data",
                        columnList = "empresa_id, produto_id, data"
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimentoStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private Acao acao;

    /*
     * Quantidade efetivamente movimentada.
     *
     * ENTRADA  → 10
     * SAIDA    → 5
     * DEVOLUCAO → 3
     *
     * No AJUSTE representa a diferença entre
     * a quantidade anterior e a posterior.
     */
    @Column(
            nullable = false
    )
    private Integer quantidade;

    /*
     * Quantidade existente antes do movimento.
     */
    @Column(
            name = "quantidade_anterior",
            nullable = false
    )
    private Integer quantidadeAnterior;

    /*
     * Quantidade existente depois do movimento.
     */
    @Column(
            name = "quantidade_posterior",
            nullable = false
    )
    private Integer quantidadePosterior;

    @Column(
            length = 500
    )
    private String descricao;

    @CreationTimestamp
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime data;

    /*
     * Estoque afetado pelo movimento.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "estoque_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_movimento_estoque"
            )
    )
    private Estoque estoque;

    /*
     * Produto afetado.
     *
     * Mantemos esta relação mesmo podendo chegar
     * ao produto através de Estoque porque facilita
     * consultas e relatórios históricos.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "produto_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_movimento_produto"
            )
    )
    private Produto produto;

    /*
     * Empresa à qual o movimento pertence.
     *
     * Essencial para isolamento multiempresa.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "empresa_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_movimento_empresa"
            )
    )
    private Empresa empresa;

    /*
     * Usuário responsável pela operação.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_movimento_usuario"
            )
    )
    private Usuario usuario;
}