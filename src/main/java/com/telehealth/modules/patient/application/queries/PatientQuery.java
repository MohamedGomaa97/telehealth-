package com.telehealth.modules.patient.application.queries;

import java.util.UUID;

/**
 * Patient Module - Queries (Read Side of CQRS)
 *
 * Queries are side-effect free and return data.
 * They never modify state.
 */
public sealed interface PatientQuery
        permits PatientQuery.GetPatientById,
                PatientQuery.GetPatientByEmail,
                PatientQuery.GetAllActivePatients,
                PatientQuery.GetPatientsByCondition {

    record GetPatientById(@jakarta.validation.constraints.NotNull UUID patientId) implements PatientQuery {}

    record GetPatientByEmail(@jakarta.validation.constraints.NotBlank String email) implements PatientQuery {}

    record GetAllActivePatients() implements PatientQuery {}

    record GetPatientsByCondition(@jakarta.validation.constraints.NotBlank String condition) implements PatientQuery {}
}
