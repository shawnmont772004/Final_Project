package com.shawn.medcom.handler;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {

}