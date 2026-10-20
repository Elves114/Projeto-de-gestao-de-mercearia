package WD.works.V2.venda.service;

import WD.works.V2.alertaStock.event.StockAlteradoEvent;
import WD.works.V2.alertaStock.service.AlertaStockService;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.configuracao.context.UsuarioContext;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.movimentoStock.repository.MovimentoStockRepository;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.produtos.repository.ProdutoRepository;
import WD.works.V2.produtos.status.StatusProduto;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.util.TestDataFactory;
import WD.works.V2.venda.dto.VendaRequest;
import WD.works.V2.venda.dto.VendaResponse;
import WD.works.V2.venda.entity.Venda;
import WD.works.V2.venda.itensVenda.dto.ItemVendaRequest;
import WD.works.V2.venda.repository.VendaRepository;
import WD.works.V2.venda.service.VendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VendaService — regras de venda")
class VendaServiceTest {

    @Mock private VendaRepository vendaRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private EstoqueRepository estoqueRepository;
    @Mock private MovimentoStockRepository movimentoRepository;
    @Mock private UsuarioContext usuarioContext;
    @Mock private EmpresaContext empresaContext;
    @Mock private AuditoriaService auditoriaService;
    @Mock private AlertaStockService alertaStockService;
    @Mock private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private VendaService service;

    private Empresa empresa;
    private Usuario usuario;
    private Produto produto;
    private Estoque estoque;

    @BeforeEach
    void setUp() {

        empresa = TestDataFactory.empresa(10L);
        usuario = TestDataFactory.usuario(1L, 10L, Perfil.FUNCIONARIO);
        produto = TestDataFactory.produto(1L, 10L, 100L, "Arroz");
        produto.setPrecoCompra(new BigDecimal("10.00"));
        produto.setPrecoVenda(new BigDecimal("15.00"));
        estoque = TestDataFactory.estoque(1L, produto, 10L, 100, 10);

        when(usuarioContext.getUsuarioAtual()).thenReturn(usuario);
        when(empresaContext.getEmpresaAtual()).thenReturn(empresa);
    }

    /* ==========================================================
       CRIAR VENDA — caminho feliz
       ========================================================== */

    @Test
    @DisplayName("Cria venda com sucesso, calcula total e lucro, atualiza stock")
    void deveCriarVendaComSucesso() {

        ItemVendaRequest item = new ItemVendaRequest(1L, 3);
        VendaRequest request = new VendaRequest(List.of(item));

        when(produtoRepository.findByIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(produto));
        when(estoqueRepository.findByProdutoIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(estoque));

        when(vendaRepository.save(any(Venda.class)))
                .thenAnswer(inv -> {
                    Venda v = inv.getArgument(0);
                    v.setId(500L);
                    return v;
                });

        VendaResponse response = service.criar(request);

        // 3 unidades × 15.00 = 45.00
        assertThat(response.getTotal()).isEqualByComparingTo("45.00");

        // lucro = (15 - 10) × 3 = 15.00
        assertThat(response.getLucroTotal()).isEqualByComparingTo("15.00");

        assertThat(response.getItens()).hasSize(1);
        assertThat(response.getId()).isEqualTo(500L);

        // Stock atualizado: 100 - 3 = 97
        assertThat(estoque.getQuantidade()).isEqualTo(97);

        verify(estoqueRepository).save(estoque);
        verify(movimentoRepository).save(any());
        verify(auditoriaService).registrar(any());

        // ★ Verifica que o evento foi publicado (I6)
        verify(applicationEventPublisher).publishEvent(any(StockAlteradoEvent.class));
    }

    /* ==========================================================
       VALIDAÇÕES
       ========================================================== */

    @Test
    @DisplayName("Rejeita venda sem itens")
    void deveRejeitarVendaSemItens() {

        VendaRequest request = new VendaRequest(List.of());

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("pelo menos um item");
    }

    @Test
    @DisplayName("Rejeita venda com produto duplicado")
    void deveRejeitarProdutoDuplicado() {

        ItemVendaRequest i1 = new ItemVendaRequest(1L, 2);
        ItemVendaRequest i2 = new ItemVendaRequest(1L, 3);
        VendaRequest request = new VendaRequest(List.of(i1, i2));

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("adicionado mais de uma vez");
    }

    @Test
    @DisplayName("Rejeita quando stock é insuficiente")
    void deveRejeitarStockInsuficiente() {

        estoque.setQuantidade(2);  // só há 2

        ItemVendaRequest item = new ItemVendaRequest(1L, 5);  // pede 5
        VendaRequest request = new VendaRequest(List.of(item));

        when(produtoRepository.findByIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(produto));
        when(estoqueRepository.findByProdutoIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(estoque));

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(vendaRepository, never()).save(any());
        verify(estoqueRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rejeita produto inativo")
    void deveRejeitarProdutoInativo() {

        produto.setStatus(StatusProduto.INATIVO);

        ItemVendaRequest item = new ItemVendaRequest(1L, 1);
        VendaRequest request = new VendaRequest(List.of(item));

        when(produtoRepository.findByIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(produto));
        when(estoqueRepository.findByProdutoIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(estoque));

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("inativo");
    }

    @Test
    @DisplayName("Rejeita produto que não existe")
    void deveRejeitarProdutoInexistente() {

        ItemVendaRequest item = new ItemVendaRequest(999L, 1);
        VendaRequest request = new VendaRequest(List.of(item));

        when(produtoRepository.findByIdAndEmpresaId(999L, 10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Produto não encontrado");
    }

    @Test
    @DisplayName("Rejeita quantidade <= 0")
    void deveRejeitarQuantidadeInvalida() {

        ItemVendaRequest item = new ItemVendaRequest(1L, 0);
        VendaRequest request = new VendaRequest(List.of(item));

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("maior que zero");
    }

    /* ==========================================================
       BUSCAR POR ID
       ========================================================== */

    @Test
    @DisplayName("Busca venda por ID pertencente à empresa")
    void deveBuscarVendaPorId() {

        when(empresaContext.getEmpresaIdAtual()).thenReturn(10L);

        Venda venda = new Venda();
        venda.setId(1L);
        venda.setEmpresa(empresa);
        venda.setUsuario(usuario);
        venda.setTotal(new BigDecimal("45.00"));
        venda.setLucroTotal(new BigDecimal("15.00"));
        venda.setDataVenda(java.time.LocalDateTime.now());

        when(vendaRepository.findByIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(venda));

        VendaResponse response = service.buscarPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmpresaId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Lança exceção quando venda não existe")
    void deveLancarExcecaoQuandoVendaNaoExiste() {

        when(empresaContext.getEmpresaIdAtual()).thenReturn(10L);
        when(vendaRepository.findByIdAndEmpresaId(99L, 10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Venda não encontrada");
    }
}