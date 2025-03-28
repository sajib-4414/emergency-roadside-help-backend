package com.emergency.roadside.help.common_module.commonexternal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalUser {

    private Long id;

    private String email;


    private String username;

}
