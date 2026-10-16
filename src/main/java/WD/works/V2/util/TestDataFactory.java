package WD.works.V2.util;

import WD.works.V2.categorias.entity.CategoriaProduto;
import WD.works.V2.empresa.entity.Empresa;
import WD.works.V2.empresa.status.Status;
import WD.works.V2.estoque.entity.Estoque;
import WD.works.V2.produtos.entity.Produto;
import WD.works.V2.produtos.status.StatusProduto;
import WD.works.V2.usuario.entity.Usuario;
import WD.works.V2.usuario.perfil.Perfil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Fábrica de entidades para testes.
 * Evita repetição e mantém os testes legíveis.
 */
public final class TestDataFactory {

    private TestDataFactory() {}

    public static Empresa empresa(Long id) {
        Empresa e = new Empresa();
        e.setId(id);
        e.setNome("Empresa " + id);
        e.setNuit("123456789");
        e.setEmail("empresa" + id + "@teste.com");
        e.setContacto("841234567");
        e.setEndereco("Endereço " + id);
        e.setStatus(Status.ATIVO);
        e.setDataCriacao(LocalDateTime.now());
        return e;
    }

    public static Usuario usuario(Long id, Long empresaId, Perfil perfil) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNome("Usuário " + id);
        u.setEmail("user" + id + "@teste.com");
        u.setSenha("$2a$10$hashed");
        u.setPerfil(perfil);
        u.setStatus(WD.works.V2.usuario.status.Status.ATIVO);
        u.setEmpresa(empresa(empresaId));
        u.setTentativasLogin(0);
        return u;
    }

    public static CategoriaProduto categoria(Long id, Long empresaId, String nome) {
        CategoriaProduto c = new CategoriaProduto();
        c.setId(id);
        c.setNome(nome);
        c.setEmpresa(empresa(empresaId));
        return c;
    }

    public static Produto produto(Long id, Long empresaId, Long categoriaId, String nome) {
        Produto p = new Produto();
        p.setId(id);
        p.setNome(nome);
        p.setPrecoCompra(new BigDecimal("10.00"));
        p.setPrecoVenda(new BigDecimal("15.00"));
        p.setEmpresa(empresa(empresaId));
        p.setCategoria(categoria(categoriaId, empresaId, "Cat " + categoriaId));
        p.setStatus(StatusProduto.ATIVO);
        return p;
    }

    public static Estoque estoque(
            Long id,
            Produto produto,
            Long empresaId,
            int qtd,
            int qtdMin
    ) {
        Estoque e = new Estoque();
        e.setId(id);
        e.setProduto(produto);
        e.setEmpresa(empresa(empresaId));
        e.setQuantidade(qtd);
        e.setQuantidadeMinima(qtdMin);
        e.setVersion(0L);
        return e;
    }
}