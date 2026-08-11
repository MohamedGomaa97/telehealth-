package com.telehealth.modules.patient.api.mapper;

import com.telehealth.modules.patient.api.dto.PatientRequest;
import com.telehealth.modules.patient.api.dto.PatientResponse;
import com.telehealth.modules.patient.application.commands.PatientCommand;
import com.telehealth.modules.patient.domain.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientApiMapper {

    public PatientCommand.RegisterPatient toCommand(PatientRequest.RegisterPatientRequest req) {
        return new PatientCommand.RegisterPatient(
                req.fullName(), req.email(), req.phoneNumber(), req.password(),
                req.dateOfBirth(), req.nationalId(), req.bloodType(), req.address()
        );
    }

    public PatientResponse.PatientSummary toSummary(Patient p) {
        return new PatientResponse.PatientSummary(
                p.getId(), p.getFullName(), p.getEmail(), p.getPhoneNumber(),
                p.getStatus().name(),
                p.getBloodType() != null ? p.getBloodType().getDisplay() : null,
                p.getCreatedAt()
        );
    }

    public PatientResponse.PatientDetail toDetail(Patient p) {
        var ec = p.getEmergencyContact() != null
                ? new PatientResponse.EmergencyContactDto(
                        p.getEmergencyContact().name(),
                        p.getEmergencyContact().relationship(),
                        p.getEmergencyContact().phoneNumber())
                : null;

        var history = p.getMedicalHistory().stream()
                .map(r -> new PatientResponse.MedicalRecordDto(
                        r.recordId(), r.consultationId(), r.diagnosis(),
                        r.prescription(), r.doctorName(), r.recordedAt(), r.notes()))
                .toList();

        return new PatientResponse.PatientDetail(
                p.getId(), p.getFullName(), p.getEmail(), p.getPhoneNumber(),
                p.getDateOfBirth(), p.getNationalId(),
                p.getBloodType() != null ? p.getBloodType().getDisplay() : null,
                p.getAddress(), p.getLatitude(), p.getLongitude(),
                p.getStatus().name(), p.getAllergies(), p.getChronicConditions(),
                ec, history, p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
