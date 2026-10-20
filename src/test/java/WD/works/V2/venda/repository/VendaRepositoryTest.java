package WD.works.V2.venda.repository;

import WD.works.V2.categorias.entity.CategoriaProduto;
import WD.works.V2.dashboard.dto.VendaGraficoResponse;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.produtos.status.StatusProduto;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.venda.entity.Venda;
import WD.works.V2.venda.itensVenda.entity.ItemVenda;
import WD.works.V2.venda.status.StatusVenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VendaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VendaRepository vendaRepository;

    private Empresa empresa;
    private Usuario usuario;
    private CategoriaProduto categoria;
    private Produto produto;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setNome("Empresa Teste");
        empresa.setNuit("123456789");
        empresa.setEmail("empresa@teste.com");
        empresa.setContacto("841234567");
        empresa.setEndereco("Endereço Teste");
        empresa.setStatus(WD.works.V2.empresa.status.Status.ATIVO);
        empresa.setDataCriacao(LocalDateTime.now());
        entityManager.persist(empresa);

        usuario = new Usuario();
        usuario.setNome("Usuário Teste");
        usuario.setEmail("user@teste.com");
        usuario.setSenha("$2a$10$hashed");
        usuario.setPerfil(Perfil.ADMIN);
        usuario.setStatus(WD.works.V2.usuario.status.Status.ATIVO);
        usuario.setEmpresa(empresa);
        usuario.setTentativasLogin(0);
        entityManager.persist(usuario);

        categoria = new CategoriaProduto();
        categoria.setNome("Categoria Teste");
        categoria.setEmpresa(empresa);
        entityManager.persist(categoria);

        produto = new Produto();
        produto.setNome("Produto Teste");
        produto.setPrecoCompra(new BigDecimal("10.00"));
        produto.setPrecoVenda(new BigDecimal("15.00"));
        produto.setEmpresa(empresa);
        produto.setCategoria(categoria);
        produto.setStatus(StatusProduto.ATIVO);
        entityManager.persist(produto);

        entityManager.flush();
    }

    @Test
    @DisplayName("pesquisar() com todos os filtros nulos deve devolver todas as vendas da empresa")
    void pesquisarComFiltrosNulos() {
        // Criar vendas
        Venda venda1 = criarVenda(LocalDateTime.now().minusDays(1));
        Venda venda2 = criarVenda(LocalDateTime.now().minusHours(1));
        entityManager.flush();

        // Executar com todos os filtros nulos
        Page<Venda> resultado = vendaRepository.pesquisar(
                empresa.getId(),
                null,
                null,
                null,
                null,
                PageRequest.of(0, 20)
        );

        // Verificar
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("pesquisar() com vendaId deve filtrar por ID específico")
    void pesquisarComVendaId() {
        Venda venda1 = criarVenda(LocalDateTime.now().minusDays(1));
        Venda venda2 = criarVenda(LocalDateTime.now().minusHours(1));
        entityManager.flush();

        Page<Venda> resultado = vendaRepository.pesquisar(
                empresa.getId(),
                venda1.getId(),
                null,
                null,
                null,
                PageRequest.of(0, 20)
        );

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getId()).isEqualTo(venda1.getId());
    }

    @Test
    @DisplayName("pesquisar() com período deve filtrar por data")
    void pesquisarComPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(3);
        LocalDateTime fim = LocalDateTime.now().minusDays(1);

        Venda vendaAntiga = criarVenda(LocalDateTime.now().minusDays(10));
        Venda vendaNoPeriodo = criarVenda(LocalDateTime.now().minusDays(2));
        Venda vendaFutura = criarVenda(LocalDateTime.now().minusHours(1));
        entityManager.flush();

        Page<Venda> resultado = vendaRepository.pesquisar(
                empresa.getId(),
                null,
                inicio,
                fim,
                null,
                PageRequest.of(0, 20)
        );

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getId()).isEqualTo(vendaNoPeriodo.getId());
    }

    @Test
    @DisplayName("pesquisar() com usuarioId deve filtrar por usuário")
    void pesquisarComUsuarioId() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setNome("Outro Usuário");
        outroUsuario.setEmail("outro@teste.com");
        outroUsuario.setSenha("$2a$10$hashed");
        outroUsuario.setPerfil(Perfil.FUNCIONARIO);
        outroUsuario.setStatus(WD.works.V2.usuario.status.Status.ATIVO);
        outroUsuario.setEmpresa(empresa);
        outroUsuario.setTentativasLogin(0);
        entityManager.persist(outroUsuario);

        Venda vendaUsuario1 = criarVenda(LocalDateTime.now().minusDays(1));
        Venda vendaOutroUsuario = new Venda();
        vendaOutroUsuario.setDataVenda(LocalDateTime.now().minusHours(1));
        vendaOutroUsuario.setTotal(new BigDecimal("25.00"));
        vendaOutroUsuario.setLucroTotal(new BigDecimal("5.00"));
        vendaOutroUsuario.setEmpresa(empresa);
        vendaOutroUsuario.setUsuario(outroUsuario);
        vendaOutroUsuario.setStatus(StatusVenda.CONCLUIDA);
        entityManager.persist(vendaOutroUsuario);
        entityManager.flush();

        Page<Venda> resultado = vendaRepository.pesquisar(
                empresa.getId(),
                null,
                null,
                null,
                usuario.getId(),
                PageRequest.of(0, 20)
        );

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getId()).isEqualTo(vendaUsuario1.getId());
    }

    @Test
    @DisplayName("vendasPorPeriodo() deve agrupar vendas por dia")
    void vendasPorPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(3);
        LocalDateTime fim = LocalDateTime.now();

        // 2 vendas no mesmo dia
        criarVenda(LocalDateTime.now().minusDays(1).withHour(10));
        criarVenda(LocalDateTime.now().minusDays(1).withHour(14));
        // 1 venda em outro dia
        criarVenda(LocalDateTime.now().minusDays(2).withHour(12));
        entityManager.flush();

        List<VendaGraficoResponse> resultado = vendaRepository.vendasPorPeriodo(
                empresa.getId(),
                inicio,
                fim
        );

        assertThat(resultado).hasSize(2);

        // Verificar que os totais estão agregados por dia
        BigDecimal totalDia1 = resultado.stream()
                .filter(r -> r.getData().equals(LocalDate.now().minusDays(1)))
                .findFirst()
                .map(VendaGraficoResponse::getValor)
                .orElse(BigDecimal.ZERO);

        assertThat(totalDia1).isEqualByComparingTo(new BigDecimal("30.00"));
    }

    @Test
    @DisplayName("vendasPorPeriodo() com período vazio deve devolver lista vazia")
    void vendasPorPeriodoVazio() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now().minusDays(5);

        criarVenda(LocalDateTime.now().minusDays(1));
        entityManager.flush();

        List<VendaGraficoResponse> resultado = vendaRepository.vendasPorPeriodo(
                empresa.getId(),
                inicio,
                fim
        );

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("findByEmpresaIdOrderByDataVendaDesc deve ordenar por data descendente")
    void listarOrdenadoPorDataDesc() {
        Venda vendaAntiga = criarVenda(LocalDateTime.now().minusDays(5));
        Venda vendaRecente = criarVenda(LocalDateTime.now().minusHours(1));
        entityManager.flush();

        Page<Venda> resultado = vendaRepository.findByEmpresaIdOrderByDataVendaDesc(
                empresa.getId(),
                PageRequest.of(0, 20)
        );

        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent().get(0).getId()).isEqualTo(vendaRecente.getId());
        assertThat(resultado.getContent().get(1).getId()).isEqualTo(vendaAntiga.getId());
    }

    @Test
    @DisplayName("findByIdAndEmpresaId deve devolver venda se pertencer à empresa")
    void findByIdAndEmpresaId() {
        Venda venda = criarVenda(LocalDateTime.now());
        entityManager.flush();

        assertThat(vendaRepository.findByIdAndEmpresaId(venda.getId(), empresa.getId()))
                .isPresent();
    }

    @Test
    @DisplayName("findByIdAndEmpresaId deve devolver vazio se empresa não corresponde")
    void findByIdAndEmpresaIdEmpresaErrada() {
        Venda venda = criarVenda(LocalDateTime.now());
        entityManager.flush();

        assertThat(vendaRepository.findByIdAndEmpresaId(venda.getId(), 999L))
                .isEmpty();
    }

    @Test
    @DisplayName("existsByIdAndEmpresaId deve devolver true se existir")
    void existsByIdAndEmpresaId() {
        Venda venda = criarVenda(LocalDateTime.now());
        entityManager.flush();

        assertThat(vendaRepository.existsByIdAndEmpresaId(venda.getId(), empresa.getId()))
                .isTrue();
    }

    @Test
    @DisplayName("existsByIdAndEmpresaId deve devolver false se não existir")
    void existsByIdAndEmpresaIdNaoExiste() {
        assertThat(vendaRepository.existsByIdAndEmpresaId(999L, empresa.getId()))
                .isFalse();
    }

    /* ============================================================
       Métodos auxiliares
       ============================================================ */

    private Venda criarVenda(LocalDateTime dataVenda) {
        Venda venda = new Venda();
        venda.setDataVenda(dataVenda);
        venda.setTotal(new BigDecimal("15.00"));
        venda.setLucroTotal(new BigDecimal("5.00"));
        venda.setEmpresa(empresa);
        venda.setUsuario(usuario);
        venda.setStatus(StatusVenda.CONCLUIDA);

        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("15.00"));
        item.setSubtotal(new BigDecimal("15.00"));
        item.setLucro(new BigDecimal("5.00"));
        item.setVenda(venda);
        venda.getItens().add(item);

        entityManager.persist(venda);
        return venda;
    }
}
