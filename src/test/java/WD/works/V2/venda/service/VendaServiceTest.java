package WD.works.V2.venda.service;

import WD.works.V2.alertaStock.service.AlertaStockService;
import WD.works.V2.auditoria.service.AuditoriaService;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.configuracao.context.UsuarioContext;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.movimentoStock.acoes.Acao;
import WD.works.V2.movimentoStock.entity.MovimentoStock;
import WD.works.V2.movimentoStock.repository.MovimentoStockRepository;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.produtos.repository.ProdutoRepository;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.venda.dto.CancelarVendaRequest;
import WD.works.V2.venda.dto.VendaResponse;
import WD.works.V2.venda.entity.Venda;
import WD.works.V2.venda.itensVenda.entity.ItemVenda;
import WD.works.V2.venda.repository.VendaRepository;
import WD.works.V2.venda.status.StatusVenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private MovimentoStockRepository movimentoRepository;

    @Mock
    private UsuarioContext usuarioContext;

    @Mock
    private EmpresaContext empresaContext;

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private AlertaStockService alertaStockService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private VendaService service;

    private Empresa empresa;
    private Usuario usuario;
    private Produto produto;
    private Estoque estoque;

    @BeforeEach
    void setUp() {
        service = new VendaService(
                vendaRepository,
                produtoRepository,
                estoqueRepository,
                movimentoRepository,
                usuarioContext,
                empresaContext,
                auditoriaService,
                alertaStockService,
                applicationEventPublisher
        );

        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNome("Empresa Teste");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmpresa(empresa);

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setEmpresa(empresa);

        estoque = new Estoque();
        estoque.setId(1L);
        estoque.setProduto(produto);
        estoque.setEmpresa(empresa);
        estoque.setQuantidade(97);
        estoque.setQuantidadeMinima(10);
    }

    @Test
    @DisplayName("Cancelar venda devolve stock e marca como CANCELADA")
    void deveCancelarVenda() {
        // Setup: venda com 1 item de 3 unidades, estoque com 97
        Venda venda = criarVendaComItem(3);

        when(usuarioContext.getUsuarioAtual()).thenReturn(usuario);
        when(empresaContext.getEmpresaAtual()).thenReturn(empresa);
        when(vendaRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(venda));
        when(estoqueRepository.findByProdutoIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(estoque));
        when(vendaRepository.save(venda)).thenReturn(venda);

        // Executar
        VendaResponse response = service.cancelar(1L, new CancelarVendaRequest("erro"));

        // Verificações
        assertThat(estoque.getQuantidade()).isEqualTo(100);
        assertThat(venda.getStatus()).isEqualTo(StatusVenda.CANCELADA);

        ArgumentCaptor<MovimentoStock> captor = ArgumentCaptor.forClass(MovimentoStock.class);
        verify(movimentoRepository).save(captor.capture());
        MovimentoStock movimento = captor.getValue();
        assertThat(movimento.getAcao()).isEqualTo(Acao.DEVOLUCAO);
        assertThat(movimento.getQuantidade()).isEqualTo(3);

        verify(auditoriaService).registrar(any(), eq(usuario), eq(empresa), any(), any());
    }

    @Test
    @DisplayName("Cancelar venda já CANCELADA lança RegraNegocioException")
    void naoDeveCancelarVendaJaCancelada() {
        Venda venda = criarVendaComItem(3);
        venda.setStatus(StatusVenda.CANCELADA);
        venda.setCanceladaEm(java.time.LocalDateTime.now());

        when(usuarioContext.getUsuarioAtual()).thenReturn(usuario);
        when(empresaContext.getEmpresaAtual()).thenReturn(empresa);
        when(vendaRepository.findByIdAndEmpresaId(1L, 1L)).thenReturn(Optional.of(venda));

        assertThatThrownBy(() -> service.cancelar(1L, new CancelarVendaRequest("motivo")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("já foi cancelada");

        verify(vendaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cancelar venda inexistente lança RecursoNaoEncontradoException")
    void naoDeveCancelarVendaInexistente() {
        when(usuarioContext.getUsuarioAtual()).thenReturn(usuario);
        when(empresaContext.getEmpresaAtual()).thenReturn(empresa);
        when(vendaRepository.findByIdAndEmpresaId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelar(999L, new CancelarVendaRequest("motivo")))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("não encontrada");
    }

    /*
     * Helper para criar uma venda com um item.
     */
    private Venda criarVendaComItem(int quantidade) {
        Venda venda = new Venda();
        venda.setId(1L);
        venda.setEmpresa(empresa);
        venda.setUsuario(usuario);
        venda.setDataVenda(java.time.LocalDateTime.now());
        venda.setTotal(BigDecimal.valueOf(quantidade).multiply(new BigDecimal("15.00")));
        venda.setLucroTotal(BigDecimal.valueOf(quantidade).multiply(new BigDecimal("5.00")));
        venda.setStatus(StatusVenda.CONCLUIDA);

        ItemVenda item = new ItemVenda();
        item.setId(1L);
        item.setVenda(venda);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(new BigDecimal("15.00"));
        item.setSubtotal(BigDecimal.valueOf(quantidade).multiply(new BigDecimal("15.00")));
        item.setLucro(BigDecimal.valueOf(quantidade).multiply(new BigDecimal("5.00")));

        List<ItemVenda> itens = new ArrayList<>();
        itens.add(item);
        venda.setItens(itens);

        return venda;
    }
}
