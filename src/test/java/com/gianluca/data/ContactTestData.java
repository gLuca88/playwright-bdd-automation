package com.gianluca.data;

import java.util.List;

public record ContactTestData(
        String nome,
        String cognome,
        String email,
        String subject,
        String messaggio,
        List<String> opzioniSubjectAttese
) {}

