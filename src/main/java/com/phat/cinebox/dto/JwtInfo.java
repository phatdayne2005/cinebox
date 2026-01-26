package com.phat.cinebox.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtInfo {
    private String jwtId;
    private Date issueTime;
    private Date expirationTime;
}
