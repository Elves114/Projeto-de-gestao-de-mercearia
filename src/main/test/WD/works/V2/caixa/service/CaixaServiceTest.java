package WD.works.V2.caixa.service;


import WD.works.V2.caixa.dto.FechoCaixaResponse;
import WD.works.V2.caixa.service.CaixaService;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;
import WD.works.V2.util.TestDataFactory;
import WD.works.V2.venda.entity.Venda;
import WD.works.V2.venda.repository.VendaRepository;
import WD.works.V2.venda.status.StatusVenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CaixaService — fecho de caixa diário")
class CaixaServiceTest {

    @Mock private VendaRepository vendaRepository;
    @Mock private EmpresaContext empresaContext;

    @InjectMocks
    private CaixaService service;

    private Empresa empresa;
    private Usuario userAna;
    private Usuario userCarlos;

    @BeforeEach
    void setUp() {

        empresa = TestDataFactory.empresa(10L);
        userAna = TestDataFactory.usuario(1L, 10L, Perfil.FUNCIONARIO);
        userAna.setNome("Ana");

        userCarlos = TestDataFactory.usuario(2L, 10L, Perfil.FUNCIONARIO);
        userCarlos.setNome("Carlos");

        when(empresaContext.getEmpresaIdAtual()).thenReturn(10L);
    }

    @Test
    @DisplayName("Calcula totais corretos com várias vendas")
    void deveCalcularTotaisCorretos() {

        LocalDate hoje = LocalDate.now();

        Venda v1 = criarVenda(1L, userAna, "100.00", "30.00", StatusVenda.CONCLUIDA);
        Venda v2 = criarVenda(2L, userAna, "50.00", "15.00", StatusVenda.CONCLUIDA);
        Venda v3 = criarVenda(3L, userCarlos, "200.00", "60.00", StatusVenda.CONCLUIDA);

        when(vendaRepository.findByEmpresaIdAndDataVendaBetween(
                any(), any(), any()
        )).thenReturn(List.of(v1, v2, v3));

        FechoCaixaResponse response = service.fechoDoDia(hoje);

        assertThat(response.getNumeroVendas()).isEqualTo(3);
        assertThat(response.getTotalVendas()).isEqualByComparingTo("350.00");
        assertThat(response.getLucroTotal()).isEqualByComparingTo("105.00");
        assertThat(response.getTicketMedio()).isEqualByComparingTo("116.67");
        assertThat(response.getVendasCanceladas()).isEqualTo(0);
        assertThat(response.getVendasPorUtilizador()).hasSize(2);
    }

    @Test
    @DisplayName("Separa vendas canceladas dos totais")
    void deveSepararVendasCanceladas() {

        LocalDate hoje = LocalDate.now();

        Venda v1 = criarVenda(1L, userAna, "100.00", "30.00", StatusVenda.CONCLUIDA);
        Venda v2 = criarVenda(2L, userAna, "50.00", "15.00", StatusVenda.CANCELADA);

        when(vendaRepository.findByEmpresaIdAndDataVendaBetween(
                any(), any(), any()
        )).thenReturn(List.of(v1, v2));

        FechoCaixaResponse response = service.fechoDoDia(hoje);

        assertThat(response.getNumeroVendas()).isEqualTo(1);
        assertThat(response.getTotalVendas()).isEqualByComparingTo("100.00");
        assertThat(response.getLucroTotal()).isEqualByComparingTo("30.00");
        assertThat(response.getVendasCanceladas()).isEqualTo(1);
        assertThat(response.getTotalCancelado()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("Dia sem vendas devolve zeros")
    void diaSemVendas() {

        when(vendaRepository.findByEmpresaIdAndDataVendaBetween(
                any(), any(), any()
        )).thenReturn(List.of());

        FechoCaixaResponse response = service.fechoDoDia(LocalDate.now());

        assertThat(response.getNumeroVendas()).isZero();
        assertThat(response.getTotalVendas()).isEqualByComparingTo("0");
        assertThat(response.getLucroTotal()).isEqualByComparingTo("0");
        assertThat(response.getTicketMedio()).isEqualByComparingTo("0");
        assertThat(response.getVendasPorUtilizador()).isEmpty();
    }

    @Test
    @DisplayName("Agrupa corretamente por utilizador")
    void deveAgruparPorUtilizador() {

        Venda v1 = criarVenda(1L, userAna, "100.00", "30.00", StatusVenda.CONCLUIDA);
        Venda v2 = criarVenda(2L, userAna, "50.00", "15.00", StatusVenda.CONCLUIDA);
        Venda v3 = criarVenda(3L, userCarlos, "200.00", "60.00", StatusVenda.CONCLUIDA);

        when(vendaRepository.findByEmpresaIdAndDataVendaBetween(
                any(), any(), any()
        )).thenReturn(List.of(v1, v2, v3));

        FechoCaixaResponse response = service.fechoDoDia(LocalDate.now());

        FechoCaixaResponse.VendaPorUtilizador ana = response.getVendasPorUtilizador()
                .stream()
                .filter(u -> u.getUsuarioId().equals(1L))
                .findFirst()
                .orElseThrow();

        assertThat(ana.getNumeroVendas()).isEqualTo(2);
        assertThat(ana.getTotalVendas()).isEqualByComparingTo("150.00");
    }

    /* ==========================================================
       Auxiliar
       ========================================================== */

    private Venda criarVenda(
            Long id,
            Usuario usuario,
            String total,
            String lucro,
            StatusVenda status
    ) {

        Venda v = new Venda();
        v.setId(id);
        v.setUsuario(usuario);
        v.setEmpresa(empresa);
        v.setTotal(new BigDecimal(total));
        v.setLucroTotal(new BigDecimal(lucro));
        v.setDataVenda(LocalDateTime.now());
        v.setStatus(status);

        return v;
    }
}