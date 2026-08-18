package WD.works.V2.estoque.entity;

import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.produtos.entity.Produto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "estoques",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_estoque_produto_empresa",
                        columnNames = {"produto_id", "empresa_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A quantidade é obrigatória")
    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    @Column(nullable = false)
    private Integer quantidade = 0;

    @NotNull(message = "A quantidade mínima é obrigatória")
    @PositiveOrZero(message = "A quantidade mínima não pode ser negativa")
    @Column(name = "quantidade_minima", nullable = false)
    private Integer quantidadeMinima = 0;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "produto_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_estoque_produto")
    )
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "empresa_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_estoque_empresa")
    )
    private Empresa empresa;
}