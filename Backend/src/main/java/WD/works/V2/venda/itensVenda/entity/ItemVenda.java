package WD.works.V2.venda.itensVenda.entity;

import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.venda.entity.Venda;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "itens_venda",
        indexes = {
                @Index(
                        name = "idx_item_venda_venda",
                        columnList = "venda_id"
                ),
                @Index(
                        name = "idx_item_venda_produto",
                        columnList = "produto_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "venda_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_item_venda_venda"
            )
    )
    private Venda venda;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "produto_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_item_venda_produto"
            )
    )
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    /*
     * Preço praticado no momento da venda.
     *
     * Não devemos depender do preço atual do Produto
     * para consultar uma venda antiga.
     */
    @Column(
            name = "preco_unitario",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal precoUnitario;

    @Column(
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal subtotal;

    /*
     * Lucro deste item no momento da venda.
     */
    @Column(
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal lucro;
}