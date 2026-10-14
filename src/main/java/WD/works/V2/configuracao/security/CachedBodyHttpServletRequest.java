package WD.works.V2.configuracao.security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Permite ler o corpo da requisição várias vezes.
 *
 * Necessário para o LoginRateLimitFilter poder inspecionar o email
 * sem impedir o controller de ler o corpo do pedido depois.
 *
 * Sem isto, o stream do servlet é consumido uma única vez
 * e o controller recebe um corpo vazio.
 */
@Getter
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);

        InputStream is = request.getInputStream();

        this.cachedBody = is.readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() {

        ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);

        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // No-op
            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }
}