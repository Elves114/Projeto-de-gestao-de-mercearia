package WD.works.V2.caixa.service;

import WD.works.V2.caixa.dto.FechoCaixaResponse;
import WD.works.V2.configuracao.context.EmpresaContext;
import WD.works.V2.venda.entity.Venda;
import WD.works.V2.venda.repository.VendaRepository;
import WD.works.V2.venda.status.StatusVenda;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CaixaService {

    private final VendaRepository vendaRepository;
    private final EmpresaContext empresaContext;

    @Transactional(readOnly = true)
    public FechoCaixaResponse fechoDoDia(LocalDate data) {

        Long empresaId = empresaContext.getEmpresaIdAtual();

        LocalDate dia = (data != null) ? data : LocalDate.now();
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.plusDays(1).atStartOfDay();

        List<Venda> vendas = vendaRepository
                .findByEmpresaIdAndDataVendaBetween(empresaId, inicio, fim);

        long numero = 0;
        long canceladas = 0;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal lucro = BigDecimal.ZERO;
        BigDecimal totalCancelado = BigDecimal.ZERO;

        Map<Long, FechoCaixaResponse.VendaPorUtilizador> porUser = new LinkedHashMap<>();

        for (Venda v : vendas) {

            if (v.getStatus() == StatusVenda.CANCELADA) {
                canceladas++;
                totalCancelado = totalCancelado.add(v.getTotal());
                continue;
            }

            numero++;
            total = total.add(v.getTotal());
            lucro = lucro.add(v.getLucroTotal());

            Long uid = v.getUsuario().getId();

            porUser.compute(uid, (k, existente) -> {
                if (existente == null) {
                    return new FechoCaixaResponse.VendaPorUtilizador(
                            uid,
                            v.getUsuario().getNome(),
                            1,
                            v.getTotal()
                    );
                }
                existente.setNumeroVendas(existente.getNumeroVendas() + 1);
                existente.setTotalVendas(existente.getTotalVendas().add(v.getTotal()));
                return existente;
            });
        }

        BigDecimal ticketMedio = numero > 0
                ? total.divide(BigDecimal.valueOf(numero), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new FechoCaixaResponse(
                dia,
                numero,
                total,
                lucro,
                ticketMedio,
                totalCancelado,
                canceladas,
                new ArrayList<>(porUser.values())
        );
    }
}