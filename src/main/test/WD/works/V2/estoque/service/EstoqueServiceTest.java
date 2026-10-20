package WD.works.V2.estoque.service;


import WD.works.V2.alertaStock.event.StockAlteradoEvent;
import WD.works.V2.alertaStock.service.AlertaStockService;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.estoque.dto.EstoqueResponse;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.estoque.repository.EstoqueRepository;
import WD.works.V2.estoque.service.EstoqueService;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.produtos.entity.Produto;
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
@DisplayName("EstoqueService — consultas e alterações de estoque")
class EstoqueServiceTest {

    @Mock private EstoqueRepository estoqueRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private EmpresaContext empresaContext;
    @Mock private AlertaStockService alertaStockService;
    @Mock private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private EstoqueService service;

    private Empresa empresa;
    private Produto produto;
    private Estoque estoque;

    @BeforeEach
    void setUp() {

        empresa = TestDataFactory.empresa(10L);
        produto = TestDataFactory.produto(1L, 10L, 100L, "Arroz");
        estoque = TestDataFactory.estoque(1L, produto, 10L, 100, 10);

        when(empresaContext.getEmpresaIdAtual()).thenReturn(10L);
    }

    /* ==========================================================
       BUSCAR POR ID
       ========================================================== */

    @Test
    @DisplayName("Busca estoque por ID")
    void deveBuscarEstoquePorId() {

        when(estoqueRepository.findByIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(estoque));

        EstoqueResponse response = service.buscarPorId(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getProdutoNome()).isEqualTo("Arroz");
        assertThat(response.getQuantidade()).isEqualTo(100);
    }

    @Test
    @DisplayName("Lança exceção quando estoque não existe")
    void deveLancarExcecaoQuandoNaoExiste() {

        when(estoqueRepository.findByIdAndEmpresaId(99L, 10L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Estoque não encontrado");
    }

    /* ==========================================================
       BUSCAR POR PRODUTO
       ========================================================== */

    @Test
    @DisplayName("Busca estoque por produto")
    void deveBuscarEstoquePorProduto() {

        when(estoqueRepository.findByProdutoIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(estoque));

        EstoqueResponse response = service.buscarPorProduto(1L);

        assertThat(response.getProdutoId()).isEqualTo(1L);
    }

    /* ==========================================================
       ALTERAR QUANTIDADE MÍNIMA
       ========================================================== */

    @Test
    @DisplayName("Altera quantidade mínima com sucesso e publica evento")
    void deveAlterarQuantidadeMinima() {

        when(estoqueRepository.findByProdutoIdAndEmpresaId(1L, 10L))
                .thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any(Estoque.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        EstoqueResponse response = service.alterarQuantidadeMinima(1L, 20);

        assertThat(response.getQuantidadeMinima()).isEqualTo(20);
        verify(estoqueRepository).save(estoque);

        // ★ Verifica que o evento foi publicado (I6)
        verify(applicationEventPublisher).publishEvent(any(StockAlteradoEvent.class));
    }

    @Test
    @DisplayName("Rejeita quantidade mínima negativa")
    void deveRejeitarQuantidadeMinimaNegativa() {

        assertThatThrownBy(() -> service.alterarQuantidadeMinima(1L, -5))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não pode ser negativa");
    }
}