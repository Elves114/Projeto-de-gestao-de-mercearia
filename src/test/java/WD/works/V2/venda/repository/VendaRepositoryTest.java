package WD.works.V2.venda.repository;

import WD.works.V2.dashboard.dto.VendaGraficoResponse;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.produtos.repository.ProdutoRepository;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.usuario.repository.UsuarioRepository;
import WD.works.V2.util.TestDataFactory;
import WD.works.V2.venda.entity.Venda;
import WD.works.V2.venda.status.StatusVenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL"
})
@ActiveProfiles("test")
@DisplayName("VendaRepository — queries de integração")
class VendaRepositoryTest {

    @Autowired private VendaRepository vendaRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    private Empresa empresa;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Criar entidades reais na base de dados H2
        empresa = TestDataFactory.empresa(10L);
        empresa.setId(null); // Deixar o H2 gerar o ID
        empresa = empresaRepository.save(empresa);

        usuario = TestDataFactory.usuario(1L, empresa.getId(), Perfil.FUNCIONARIO);
        usuario.setId(null);
        usuario = usuarioRepository.save(usuario);

        // Criar duas vendas (uma ontem, uma hoje)
        criarVenda(usuario, LocalDateTime.now().minusDays(1), "100.00");
        criarVenda(usuario, LocalDateTime.now(), "200.00");
    }

    @Test
    @DisplayName("Pesquisa vendas com filtros nulos devolve todas")
    void pesquisarComFiltrosNulos() {
        Page<Venda> page = vendaRepository.pesquisar(
                empresa.getId(), null, null, null, null, PageRequest.of(0, 10)
        );
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Filtra por ID de venda específico")
    void pesquisarComVendaId() {
        Venda v = vendaRepository.findAll().get(0);
        Page<Venda> page = vendaRepository.pesquisar(
                empresa.getId(), v.getId(), null, null, null, PageRequest.of(0, 10)
        );
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Filtra por período de datas")
    void pesquisarComPeriodo() {
        Page<Venda> page = vendaRepository.pesquisar(
                empresa.getId(), null,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                null, PageRequest.of(0, 10)
        );
        assertThat(page.getTotalElements()).isEqualTo(1); // Apenas a de hoje
    }

    @Test
    @DisplayName("Lista ordenada por data descendente")
    void listarOrdenadoPorDataDesc() {
        Page<Venda> page = vendaRepository.findByEmpresaIdOrderByDataVendaDesc(
                empresa.getId(), PageRequest.of(0, 10)
        );
        assertThat(page.getContent().get(0).getDataVenda())
                .isAfter(page.getContent().get(1).getDataVenda());
    }

    @Test
    @DisplayName("Agrupa vendas por período para o gráfico")
    void vendasPorPeriodo() {
        List<VendaGraficoResponse> resultado = vendaRepository.vendasPorPeriodo(
                empresa.getId(),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1)
        );
        assertThat(resultado).hasSize(2); // Um dia com 100, outro com 200
    }

    @Test
    @DisplayName("Agrupa vendas vazias no gráfico")
    void vendasPorPeriodoVazio() {
        List<VendaGraficoResponse> resultado = vendaRepository.vendasPorPeriodo(
                empresa.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3)
        );
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Filtra por ID de utilizador")
    void pesquisarComUsuarioId() {
        Page<Venda> page = vendaRepository.pesquisar(
                empresa.getId(), null, null, null, usuario.getId(), PageRequest.of(0, 10)
        );
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Encontra venda por ID e Empresa")
    void findByIdAndEmpresaId() {
        Venda v = vendaRepository.findAll().get(0);
        Optional<Venda> encontrada = vendaRepository.findByIdAndEmpresaId(v.getId(), empresa.getId());
        assertThat(encontrada).isPresent();
    }

    @Test
    @DisplayName("Não encontra venda de outra empresa")
    void findByIdAndEmpresaIdEmpresaErrada() {
        Venda v = vendaRepository.findAll().get(0);
        Optional<Venda> encontrada = vendaRepository.findByIdAndEmpresaId(v.getId(), 999L);
        assertThat(encontrada).isEmpty();
    }

    @Test
    @DisplayName("Verifica existência por ID e Empresa")
    void existsByIdAndEmpresaId() {
        Venda v = vendaRepository.findAll().get(0);
        assertThat(vendaRepository.existsByIdAndEmpresaId(v.getId(), empresa.getId())).isTrue();
        assertThat(vendaRepository.existsByIdAndEmpresaId(v.getId(), 999L)).isFalse();
    }

    // Auxiliar
    private void criarVenda(Usuario usuario, LocalDateTime data, String total) {
        Venda v = new Venda();
        v.setEmpresa(empresa);
        v.setUsuario(usuario);
        v.setDataVenda(data);
        v.setTotal(new BigDecimal(total));
        v.setLucroTotal(new BigDecimal("10.00"));
        v.setStatus(StatusVenda.CONCLUIDA);
        vendaRepository.save(v);
    }
}