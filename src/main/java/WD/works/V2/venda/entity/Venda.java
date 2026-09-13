package WD.works.V2.venda.entity;

import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.venda.itensVenda.entity.ItemVenda;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "vendas",
        indexes = {
                @Index(
                        name = "idx_venda_empresa",
                        columnList = "empresa_id"
                ),
                @Index(
                        name = "idx_venda_empresa_data",
                        columnList = "empresa_id, data_venda"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "data_venda",
            nullable = false,
            updatable = false
    )
    private LocalDateTime dataVenda;

    @Column(
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal total;

    @Column(
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal lucroTotal;

    @Version
    private Long version;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "empresa_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_venda_empresa"
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
                    name = "fk_venda_usuario"
            )
    )
    private Usuario usuario;

    @OneToMany(
            mappedBy = "venda",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemVenda> itens = new ArrayList<>();

    @PrePersist
    protected void prePersist() {

        if (dataVenda == null) {
            dataVenda = LocalDateTime.now();
        }
    }
}