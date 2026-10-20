package WD.works.V2.movimentoStock.service;

import WD.works.V2.alertaStock.event.StockAlteradoEvent;
import WD.works.V2.alertaStock.service.AlertaStockService;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.configuracao.context.UsuarioContext;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.movimentoStock.acoes.Acao;
import WD.works.V2.movimentoStock.dto.MovimentoStockRequest;
import WD.works.V2.movimentoStock.entity.MovimentoStock;
import WD.works.V2.movimentoStock.repository.MovimentoStockRepository;
import WD.works.V2.movimentoStock.service.MovimentoStockService;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.produtos.repository.ProdutoRepository;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.util.TestDataFactory;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MovimentoStockService — entradas, saídas e ajustes")
class MovimentoStockServiceTest {

    @Mock private MovimentoStockRepository movimentoRepository;
    @Mock private EstoqueRepository estoqueRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private UsuarioContext usuarioContext;
    @Mock private AlertaStockService alertaStockService;
    @Mock private AuditoriaService auditoriaService;
    @Mock private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private MovimentoStockService service;

    private Empresa empresa;
    private Usuario usuario;
    private Produto produto;
    private Estoque estoque;

    @BeforeEach
    void setUp() {

        empresa = TestDataFactory.empresa(10L);
        usuario = TestDataFactory.usuario(1L, 10L, Perfil.ADMIN);
        produto = TestDataFactory.produto(1L, 10L, 100L, "Arroz");
        estoque = TestDataFactory.estoque(1L, produto, 10L, 100, 10);

        when(usuarioContext.getUsuarioAtual()).thenReturn(usuario);
        when(produtoRepository.findByIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(produto));
        when(estoqueRepository.findByProdutoIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(estoque));
        when(movimentoRepository.save(any(MovimentoStock.class)))
                .thenAnswer(inv -> {
                    MovimentoStock m = inv.getArgument(0);
                    m.setId(1L);
                    return m;
                });
    }

    /* ==========================================================
       ENTRADA
       ========================================================== */

    @Test
    @DisplayName("ENTRADA aumenta o stock")
    void entradaDeveAumentarStock() {

        MovimentoStockRequest request = new MovimentoStockRequest();
        request.setAcao(Acao.ENTRADA);
        request.setQuantidade(50);
        request.setProdutoId(1L);

        service.criar(request);

        assertThat(estoque.getQuantidade()).isEqualTo(150);
        verify(estoqueRepository).save(estoque);
        verify(applicationEventPublisher).publishEvent(any(StockAlteradoEvent.class));
    }

    /* ==========================================================
       SAÍDA
       ========================================================== */

    @Test
    @DisplayName("SAIDA reduz o stock")
    void saidaDeveReduzirStock() {

        MovimentoStockRequest request = new MovimentoStockRequest();
        request.setAcao(Acao.SAIDA);
        request.setQuantidade(30);
        request.setProdutoId(1L);

        service.criar(request);

        assertThat(estoque.getQuantidade()).isEqualTo(70);
        verify(applicationEventPublisher).publishEvent(any(StockAlteradoEvent.class));
    }

    @Test
    @DisplayName("SAIDA rejeitada quando não há stock suficiente")
    void saidaRejeitadaSemStock() {

        MovimentoStockRequest request = new MovimentoStockRequest();
        request.setAcao(Acao.SAIDA);
        request.setQuantidade(500);  // há só 100
        request.setProdutoId(1L);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(estoqueRepository, never()).save(any());
    }

    /* ==========================================================
       AJUSTE
       ========================================================== */

    @Test
    @DisplayName("AJUSTE define diretamente a nova quantidade")
    void ajusteDeveDefinirQuantidade() {

        MovimentoStockRequest request = new MovimentoStockRequest();
        request.setAcao(Acao.AJUSTE);
        request.setQuantidadeAjuste(250);
        request.setProdutoId(1L);
        request.setDescricao("Ajuste após contagem física");   // ★ obrigatório (I4)

        service.criar(request);

        assertThat(estoque.getQuantidade()).isEqualTo(250);
        verify(applicationEventPublisher).publishEvent(any(StockAlteradoEvent.class));
    }

    @Test
    @DisplayName("AJUSTE rejeitado sem descrição (I4)")
    void ajusteRejeitadoSemDescricao() {

        MovimentoStockRequest request = new MovimentoStockRequest();
        request.setAcao(Acao.AJUSTE);
        request.setQuantidadeAjuste(250);
        request.setProdutoId(1L);
        // Sem descrição → deve falhar

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("descrição justificativa");
    }

    /* ==========================================================
       DEVOLUÇÃO
       ========================================================== */

    @Test
    @DisplayName("DEVOLUCAO aumenta o stock (como ENTRADA)")
    void devolucaoDeveAumentarStock() {

        MovimentoStockRequest request = new MovimentoStockRequest();
        request.setAcao(Acao.DEVOLUCAO);
        request.setQuantidade(20);
        request.setProdutoId(1L);

        service.criar(request);

        assertThat(estoque.getQuantidade()).isEqualTo(120);
        verify(applicationEventPublisher).publishEvent(any(StockAlteradoEvent.class));
    }

    /* ==========================================================
       VALIDAÇÕES
       ========================================================== */

    @Test
    @DisplayName("Rejeita ENTRADA com quantidade <= 0")
    void rejeitaQuantidadeZero() {

        MovimentoStockRequest request = new MovimentoStockRequest();
        request.setAcao(Acao.ENTRADA);
        request.setQuantidade(0);
        request.setProdutoId(1L);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    @DisplayName("Rejeita SAÍDA sem produto")
    void rejeitaProdutoInexistente() {

        when(produtoRepository.findByIdAndEmpresaId(999L, 10L))
                .thenReturn(Optional.empty());

        MovimentoStockRequest request = new MovimentoStockRequest();
        request.setAcao(Acao.SAIDA);
        request.setQuantidade(1);
        request.setProdutoId(999L);

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}