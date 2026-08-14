package WD.works.V2.categorias.entity;

import WD.works.V2.categorias.status.StatusCategoria;
import WD.works.V2.empresa.entity.Empresa;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "categorias_produto",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_categoria_nome_empresa",
                        columnNames = {"nome", "empresa_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da categoria é obrigatório")
    @Size(
            min = 2,
            max = 100,
            message = "O nome da categoria deve ter entre 2 e 100 caracteres"
    )
    @Column(nullable = false, length = 100)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "empresa_id",
            nullable = false
    )
    private Empresa empresa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCategoria status = StatusCategoria.ATIVA;
}