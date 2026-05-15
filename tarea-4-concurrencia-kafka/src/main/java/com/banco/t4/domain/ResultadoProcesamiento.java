package com.banco.t4.domain;

public class ResultadoProcesamiento {
    private final String transaccionId;
    private final boolean success;
    private final String message;

    public ResultadoProcesamiento(String transaccionId, boolean success, String message) {
        this.transaccionId = transaccionId;
        this.success = success;
        this.message = message;
    }

    public static ResultadoProcesamiento success(String id) {
        return new ResultadoProcesamiento(id, true, "OK");
    }

    public static ResultadoProcesamiento error(String id, String reason) {
        return new ResultadoProcesamiento(id, false, reason);
    }

    public String getTransaccionId() { return transaccionId; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
