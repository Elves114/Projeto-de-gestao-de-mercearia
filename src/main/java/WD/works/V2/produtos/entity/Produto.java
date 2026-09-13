package WD.works.V2.produtos.entity;

import WD.works.V2.categorias.entity.CategoriaProduto;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.produtos.status.StatusProduto;
import WD.works.V2.usuario.entity.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "produtos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_produto_nome_empresa",
                        columnNames = {"nome", "empresa_id"}
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(
            min = 2,
            max = 150,
            message = "O nome do produto deve ter entre 2 e 150 caracteres"
    )
    @Column(nullable = false, length = 150)
    private String nome;

    @NotNull(message = "O preço de compra é obrigatório")
    @DecimalMin(
            value = "0.00",
            inclusive = false,
            message = "O preço de compra deve ser maior que zero"
    )
    @Column(
            name = "preco_compra",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal precoCompra;

    @NotNull(message = "O preço de venda é obrigatório")
    @DecimalMin(
            value = "0.00",
            inclusive = false,
            message = "O preço de venda deve ser maior que zero"
    )
    @Column(
            name = "preco_venda",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal precoVenda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "empresa_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_produto_empresa")
    )
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "categoria_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_produto_categoria")
    )
    private CategoriaProduto categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "criado_por_id",
            foreignKey = @ForeignKey(name = "fk_produto_criado_por")
    )
    private Usuario criadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ultima_alteracao_por_id",
            foreignKey = @ForeignKey(name = "fk_produto_alterado_por")
    )
    private Usuario ultimaAlteracaoPor;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private StatusProduto status;
}