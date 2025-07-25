package com.emergency.roadside.help.responder_assignment_backend.external;




import com.emergency.roadside.help.common_module.commonexternal.AuthResponse;
import com.emergency.roadside.help.common_module.commonexternal.ExternalUser;
import com.emergency.roadside.help.common_module.commonmodels.RegisterRequest;
import com.emergency.roadside.help.responder_assignment_backend.configs.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "client-service", url = "http://localhost:9091", configuration = FeignClientConfig.class)
public interface UserServiceClient {

    @PostMapping("/api/v1/auth/register-user-only")
    AuthResponse registerDriverUser(@RequestBody RegisterRequest payload);

    //for login driver will hit the client service directly
    @PostMapping("/api/v1/auth/validate-and-get-user")
    //pass the token forward to validate user
    ExternalUser validateAndGetUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

}
