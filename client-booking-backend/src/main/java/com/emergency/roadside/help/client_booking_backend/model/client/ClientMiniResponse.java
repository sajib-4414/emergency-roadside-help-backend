package com.emergency.roadside.help.client_booking_backend.model.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientMiniResponse {
    private Long id;
    private String name;
    private String phoneNo;
    private String email;
    private String username;
    public ClientMiniResponse(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.phoneNo = client.getPhoneNo();
        this.email = client.getUser().getEmail();
        this.username = client.getUser().getUsername();
    }
}
