package com.telehealth.modules.doctor.application.queries;

import com.telehealth.modules.doctor.domain.model.ConsultationType;
import com.telehealth.modules.doctor.domain.model.Specialization;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public sealed interface DoctorQuery
        permits DoctorQuery.GetDoctorById,
                DoctorQuery.GetAvailableDoctors,
                DoctorQuery.GetDoctorsBySpecialization {

    record GetDoctorById(@NotNull UUID doctorId) implements DoctorQuery {}

    record GetAvailableDoctors(@NotNull ConsultationType type) implements DoctorQuery {}

    record GetDoctorsBySpecialization(
            @NotNull ConsultationType type,
            @NotNull Specialization specialization
    ) implements DoctorQuery {}
}
