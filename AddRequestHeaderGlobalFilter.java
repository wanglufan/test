import org.springframework.cloud.gateway.filter.GlobalFilter;  
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;  
import org.springframework.cloud.gateway.filter.GatewayFilterChain;  
import org.springframework.core.Ordered;  
import org.springframework.core.io.buffer.DataBuffer;  
import org.springframework.core.io.buffer.DataBufferFactory;  
import org.springframework.core.io.buffer.DataBufferUtils;  
import org.springframework.http.HttpHeaders;  
import org.springframework.http.server.reactive.ServerHttpRequest;  
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;  
import org.springframework.stereotype.Component;  
import org.springframework.web.server.ServerWebExchange;  
import reactor.core.publisher.Flux;  
import reactor.core.publisher.Mono;  
  
@Component  
public class AddRequestHeaderGlobalFilter implements GlobalFilter, Ordered {  
  
    private static final String HEADER_NAME = "X-Custom-Header";  
    private static final String HEADER_VALUE = "CustomValue";  
  
    @Override  
    public int getOrder() {  
        // 设置过滤器的顺序，确保在请求发送到下游服务之前执行  
        return -1;  
    }  
  
    @Override  
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {  
        ServerHttpRequest request = exchange.getRequest();  
        HttpHeaders headers = new HttpHeaders();  
        headers.addAll(request.getHeaders());  
        // 添加自定义请求头  
        headers.set(HEADER_NAME, HEADER_VALUE);  
  
        ServerHttpRequest newRequest = new ServerHttpRequestDecorator(request) {  
            @Override  
            public HttpHeaders getHeaders() {  
                return headers;  
            }  
        };  
  
        // 使用修改后的请求继续过滤器链  
        return chain.filter(exchange.mutate().request(newRequest).build());  
    }  
}