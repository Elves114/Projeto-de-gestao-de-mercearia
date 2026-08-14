package WD.works.V2.empresa.service;

import WD.works.V2.configuracao.security.SecurityUtils;
import WD.works.V2.empresa.dto.EmpresaRequest;
import WD.works.V2.empresa.dto.EmpresaResponse;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.repository.EmpresaRepository;
import WD.works.V2.empresa.status.Status;
import WD.works.V2.exception.RecursoNaoEncontradoException;
import WD.works.V2.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;

    @Transactional
    public EmpresaResponse criar(EmpresaRequest request) {

        validarNuit(request.getNuit());
        validarEmail(request.getEmail());

        Empresa empresa = new Empresa();

        empresa.setNome(request.getNome());
        empresa.setNuit(request.getNuit());
        empresa.setEmail(request.getEmail());
        empresa.setContacto(request.getContacto());
        empresa.setEndereco(request.getEndereco());

        // Toda a empresa começa ativa
        empresa.setStatus(Status.ATIVO);

        Empresa empresaSalva = empresaRepository.save(empresa);

        return converterParaResponse(empresaSalva);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse buscarPorId(Long id) {

        validarAcessoEmpresa(id);

        Empresa empresa = buscarEntidadePorId(id);

        return converterParaResponse(empresa);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse buscarPorNuit(String nuit) {

        Empresa empresa = empresaRepository.findByNuit(nuit)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Empresa não encontrada com o NUIT: " + nuit
                        )
                );

        validarAcessoEmpresa(empresa.getId());

        return converterParaResponse(empresa);
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponse> listarTodas() {

        return empresaRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional
    public EmpresaResponse atualizar(
            Long id,
            EmpresaRequest request
    ) {

        validarAcessoEmpresa(id);

        Empresa empresa = buscarEntidadePorId(id);
        validarNuitNaAtualizacao(
                request.getNuit(),
                empresa.getId()
        );

        validarEmailNaAtualizacao(
                request.getEmail(),
                empresa.getId()
        );

        empresa.setNome(request.getNome());
        empresa.setNuit(request.getNuit());
        empresa.setEmail(request.getEmail());
        empresa.setContacto(request.getContacto());
        empresa.setEndereco(request.getEndereco());

        Empresa empresaAtualizada =
                empresaRepository.save(empresa);

        return converterParaResponse(empresaAtualizada);
    }

    @Transactional
    public EmpresaResponse ativar(Long id) {

        validarAcessoEmpresa(id);

        Empresa empresa = buscarEntidadePorId(id);

        if (empresa.getStatus() == Status.ATIVO) {
            throw new RegraNegocioException(
                    "A empresa já está ativa."
            );
        }

        empresa.setStatus(Status.ATIVO);

        return converterParaResponse(
                empresaRepository.save(empresa)
        );
    }

    @Transactional
    public EmpresaResponse desativar(Long id) {

        validarAcessoEmpresa(id);

        Empresa empresa = buscarEntidadePorId(id);

        if (empresa.getStatus() == Status.INATIVO) {
            throw new RegraNegocioException(
                    "A empresa já está inativa."
            );
        }

        empresa.setStatus(Status.INATIVO);

        return converterParaResponse(
                empresaRepository.save(empresa)
        );
    }

    /*
     * ============================================================
     * Métodos internos
     * ============================================================
     */

    private Empresa buscarEntidadePorId(Long id) {

        return empresaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Empresa não encontrada com o ID: " + id
                        )
                );
    }

    private void validarNuit(String nuit) {

        if (empresaRepository.existsByNuit(nuit)) {
            throw new RegraNegocioException(
                    "Já existe uma empresa com este NUIT."
            );
        }
    }

    private void validarEmail(String email) {

        if (empresaRepository.existsByEmail(email)) {
            throw new RegraNegocioException(
                    "Já existe uma empresa com este email."
            );
        }
    }

    private void validarNuitNaAtualizacao(
            String nuit,
            Long empresaId
    ) {

        empresaRepository.findByNuit(nuit)
                .ifPresent(empresa -> {

                    if (!empresa.getId().equals(empresaId)) {
                        throw new RegraNegocioException(
                                "Já existe outra empresa com este NUIT."
                        );
                    }
                });
    }

    private void validarEmailNaAtualizacao(
            String email,
            Long empresaId
    ) {

        empresaRepository.findByEmail(email)
                .ifPresent(empresa -> {

                    if (!empresa.getId().equals(empresaId)) {
                        throw new RegraNegocioException(
                                "Já existe outra empresa com este email."
                        );
                    }
                });
    }

    private EmpresaResponse converterParaResponse(
            Empresa empresa
    ) {

        return new EmpresaResponse(
                empresa.getId(),
                empresa.getNome(),
                empresa.getNuit(),
                empresa.getEmail(),
                empresa.getContacto(),
                empresa.getEndereco(),
                empresa.getStatus(),
                empresa.getDataCriacao()
        );
    }

    private void validarAcessoEmpresa(Long empresaId) {

        Long empresaAutenticadaId =
                securityUtils.getEmpresaId();

        if (!empresaAutenticadaId.equals(empresaId)) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "Acesso negado à empresa."
            );
        }
    }
}