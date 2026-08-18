package WD.works.V2.usuario.auth.security;

import WD.works.V2.usuario.entity.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class UsuarioDetails implements UserDetails {

    private final Usuario usuario;

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        // Adiciona o perfil como ROLE
        authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + usuario.getPerfil().name()
                )
        );

        // Adiciona as permissões do perfil
        usuario.getPerfil()
                .getPermissoes()
                .forEach(permissao ->
                        authorities.add(
                                new SimpleGrantedAuthority(
                                        permissao.name()
                                )
                        )
                );

        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.getStatus()
                != WD.works.V2.usuario.status.Status.BLOQUEADO;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getStatus()
                == WD.works.V2.usuario.status.Status.ATIVO;
    }
}