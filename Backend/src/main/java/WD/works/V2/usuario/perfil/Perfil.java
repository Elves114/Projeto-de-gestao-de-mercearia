package WD.works.V2.usuario.perfil;

import WD.works.V2.usuario.auth.permissao.Permissao;

import java.util.Set;

public enum Perfil {

    ADMIN(
            Permissao.EMPRESA_VISUALIZAR,
            Permissao.EMPRESA_EDITAR,

            Permissao.USUARIO_CRIAR,
            Permissao.USUARIO_VISUALIZAR,
            Permissao.USUARIO_EDITAR,
            Permissao.USUARIO_BLOQUEAR,

            Permissao.PRODUTO_CRIAR,
            Permissao.PRODUTO_VISUALIZAR,
            Permissao.PRODUTO_EDITAR,
            Permissao.PRODUTO_EXCLUIR,

            Permissao.CATEGORIA_CRIAR,
            Permissao.CATEGORIA_VISUALIZAR,
            Permissao.CATEGORIA_EDITAR,
            Permissao.CATEGORIA_EXCLUIR,

            Permissao.VENDA_CRIAR,
            Permissao.VENDA_VISUALIZAR,
            Permissao.VENDA_CANCELAR,

            Permissao.ESTOQUE_VISUALIZAR,
            Permissao.ESTOQUE_AJUSTAR,
            Permissao.ESTOQUE_MOVIMENTAR,

            Permissao.AUDITORIA_VISUALIZAR
    ),

    GERENTE(
            Permissao.EMPRESA_VISUALIZAR,

            Permissao.USUARIO_VISUALIZAR,
            Permissao.USUARIO_EDITAR,

            Permissao.PRODUTO_CRIAR,
            Permissao.PRODUTO_VISUALIZAR,
            Permissao.PRODUTO_EDITAR,
            Permissao.PRODUTO_EXCLUIR,

            Permissao.CATEGORIA_CRIAR,
            Permissao.CATEGORIA_VISUALIZAR,
            Permissao.CATEGORIA_EDITAR,
            Permissao.CATEGORIA_EXCLUIR,

            Permissao.VENDA_CRIAR,
            Permissao.VENDA_VISUALIZAR,
            Permissao.VENDA_CANCELAR,

            Permissao.ESTOQUE_VISUALIZAR,
            Permissao.ESTOQUE_AJUSTAR,
            Permissao.ESTOQUE_MOVIMENTAR,

            Permissao.AUDITORIA_VISUALIZAR
    ),

    FUNCIONARIO(
            Permissao.EMPRESA_VISUALIZAR,

            Permissao.USUARIO_VISUALIZAR,

            Permissao.PRODUTO_VISUALIZAR,

            Permissao.CATEGORIA_VISUALIZAR,

            Permissao.VENDA_CRIAR,
            Permissao.VENDA_VISUALIZAR,

            Permissao.ESTOQUE_VISUALIZAR
    );

    private final Set<Permissao> permissoes;

    Perfil(Permissao... permissoes) {
        this.permissoes = Set.of(permissoes);
    }

    public Set<Permissao> getPermissoes() {
        return permissoes;
    }
}