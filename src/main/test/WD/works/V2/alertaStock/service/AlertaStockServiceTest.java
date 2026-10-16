package WD.works.V2.alertaStock.service;

import WD.works.V2.alertaStock.entity.AlertaStock;
import WD.works.V2.alertaStock.repository.AlertaStockRepository;
import WD.works.V2.alertaStock.service.AlertaStockService;
import WD.works.V2.configuracao.context.UsuarioContext;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.exception.RegraNegocioException;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertaStockService — regras de alerta de stock")
class AlertaStockServiceTest {

    @Mock
    private AlertaStockRepository alertaStockRepository;

    @Mock
    private UsuarioContext usuarioContext;

    @InjectMocks
    private AlertaStockService service;

    private Estoque estoque;

    @BeforeEach
    void setUp() {
        Produto produto = TestDataFactory.produto(1L, 10L, 100L, "Arroz");
        estoque = TestDataFactory.estoque(1L, produto, 10L, 5, 10);
    }

    /* ==========================================================
       CRIAR ALERTA
       ========================================================== */

    @Test
    @DisplayName("Cria alerta quando stock baixo e não existe alerta ativo")
    void deveCriarAlertaQuandoStockBaixo() {

        when(alertaStockRepository.findByEstoqueIdAndAtivoTrue(1L))
                .thenReturn(Optional.empty());

        service.verificarEstoque(estoque);

        ArgumentCaptor<AlertaStock> captor =
                ArgumentCaptor.forClass(AlertaStock.class);

        verify(alertaStockRepository).save(captor.capture());

        AlertaStock salvo = captor.getValue();

        assertThat(salvo.isAtivo()).isTrue();
        assertThat(salvo.getEstoque()).isEqualTo(estoque);
        assertThat(salvo.getEmpresa()).isEqualTo(estoque.getEmpresa());
        assertThat(salvo.getCriadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Não cria alerta duplicado quando já existe um ativo")
    void naoDeveCriarAlertaDuplicado() {

        AlertaStock existente = new AlertaStock();
        existente.setId(99L);
        existente.setAtivo(true);

        when(alertaStockRepository.findByEstoqueIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(existente));

        service.verificarEstoque(estoque);

        verify(alertaStockRepository, never()).save(any());
    }

    /* ==========================================================
       RESOLVER ALERTA
       ========================================================== */

    @Test
    @DisplayName("Resolve alerta ativo quando stock volta ao normal")
    void deveResolverAlertaQuandoStockNormaliza() {

        estoque.setQuantidade(50);  // 50 > 10 → normal

        AlertaStock ativo = new AlertaStock();
        ativo.setId(99L);
        ativo.setAtivo(true);

        when(alertaStockRepository.findByEstoqueIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(ativo));

        service.verificarEstoque(estoque);

        ArgumentCaptor<AlertaStock> captor =
                ArgumentCaptor.forClass(AlertaStock.class);

        verify(alertaStockRepository).save(captor.capture());

        AlertaStock resolvido = captor.getValue();

        assertThat(resolvido.isAtivo()).isFalse();
        assertThat(resolvido.getResolvidoEm()).isNotNull();
    }

    @Test
    @DisplayName("Não faz nada quando stock normal e não existe alerta ativo")
    void naoDeveFazerNadaQuandoStockNormal() {

        estoque.setQuantidade(50);

        when(alertaStockRepository.findByEstoqueIdAndAtivoTrue(1L))
                .thenReturn(Optional.empty());

        service.verificarEstoque(estoque);

        verify(alertaStockRepository, never()).save(any());
    }

    /* ==========================================================
       VALIDAÇÕES
       ========================================================== */

    @Test
    @DisplayName("Lança exceção quando estoque é null")
    void deveLancarExcecaoQuandoEstoqueNull() {

        assertThatThrownBy(() -> service.verificarEstoque(null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não pode ser nulo");
    }

    @Test
    @DisplayName("Lança exceção quando estoque não tem empresa")
    void deveLancarExcecaoQuandoSemEmpresa() {

        estoque.setEmpresa(null);

        assertThatThrownBy(() -> service.verificarEstoque(estoque))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("empresa");
    }
}