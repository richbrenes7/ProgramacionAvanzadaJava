package com.banco.core.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ApiUsageLogService {

    private static final int MAX_LOGS = 100;
    private final ArrayDeque<ApiUsageLog> logs = new ArrayDeque<>();

    public synchronized void registrar(ApiUsageLog log) {
        logs.addFirst(log);
        while (logs.size() > MAX_LOGS) {
            logs.removeLast();
        }
    }

    public synchronized List<ApiUsageLog> recientes(int limite) {
        return logs.stream()
                .limit(Math.max(1, limite))
                .toList();
    }

    public synchronized Map<String, Long> resumenPorRuta() {
        return new ArrayList<>(logs).stream()
                .collect(Collectors.groupingBy(
                        log -> log.metodo() + " " + log.ruta(),
                        LinkedHashMap::new,
                        Collectors.counting()));
    }
}
