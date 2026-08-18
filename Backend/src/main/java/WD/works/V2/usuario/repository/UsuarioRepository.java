package WD.works.V2.usuario.repository;

import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByEmpresaId(Long empresaId);

    Optional<Usuario> findByIdAndEmpresaId(
            Long usuarioId,
            Long empresaId
    );
    Page<Usuario> findByEmpresaIdOrderByNomeAsc(
            Long empresaId,
            Pageable pageable
    );
    boolean existsByIdAndEmpresaId(
            Long usuarioId,
            Long empresaId
    );

    boolean existsByEmpresaIdAndPerfil(
            Long empresaId,
            Perfil perfil
    );
    Optional<Usuario> findByEmailAndEmpresaId(
            String email,
            Long empresaId
    );


    @Query("""
        SELECT u
        FROM Usuario u
        WHERE u.empresa.id = :empresaId
          AND (
              LOWER(u.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
              OR LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%'))
          )
        ORDER BY u.nome ASC
        """)
    Page<Usuario> pesquisarPorEmpresa(
            @Param("empresaId") Long empresaId,
            @Param("termo") String termo,
            Pageable pageable
    );



}