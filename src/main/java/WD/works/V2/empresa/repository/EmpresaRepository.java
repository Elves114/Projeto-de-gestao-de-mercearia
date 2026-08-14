package WD.works.V2.empresa.repository;

import WD.works.V2.empresa.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    boolean existsByNuit(String nuit);

    boolean existsByEmail(String email);

    Optional<Empresa> findByNuit(String nuit);

    Optional<Empresa> findByEmail(String email);
}