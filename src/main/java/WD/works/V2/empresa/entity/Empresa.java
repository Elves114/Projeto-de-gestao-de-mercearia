package WD.works.V2.empresa.entity;

import WD.works.V2.empresa.status.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "empresas",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_empresa_nuit",
                        columnNames = "nuit"
                ),
                @UniqueConstraint(
                        name = "uk_empresa_email",
                        columnNames = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da empresa é obrigatório")
    @Size(
            min = 2,
            max = 150,
            message = "O nome da empresa deve ter entre 2 e 150 caracteres"
    )
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "O NUIT é obrigatório")
    @Pattern(
            regexp = "\\d{9}",
            message = "O NUIT deve conter exatamente 9 dígitos"
    )
    @Column(nullable = false, unique = true, length = 9)
    private String nuit;

    @NotBlank(message = "O email da empresa é obrigatório")
    @Email(message = "Informe um email válido")
    @Size(
            max = 150,
            message = "O email não pode ultrapassar 150 caracteres"
    )
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "O contacto da empresa é obrigatório")
    @Pattern(
            regexp = "\\d{9}",
            message = "O contacto deve conter exatamente 9 dígitos"
    )
    @Column(nullable = false, length = 9)
    private String contacto;

    @Size(
            max = 255,
            message = "O endereço não pode ultrapassar 255 caracteres"
    )
    @Column(length = 255)
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ATIVO;

    @CreationTimestamp
    @Column(
            name = "data_criacao",
            nullable = false,
            updatable = false
    )
    private LocalDateTime dataCriacao;
}