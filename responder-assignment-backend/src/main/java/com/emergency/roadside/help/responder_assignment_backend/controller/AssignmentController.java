package com.emergency.roadside.help.responder_assignment_backend.controller;

import com.emergency.roadside.help.common_module.commonexternal.AuthResponse;
import com.emergency.roadside.help.common_module.commonmodels.AssignStatus;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.UnprocessableEntityException;
import com.emergency.roadside.help.responder_assignment_backend.cqrs.commands.ResponderAcceptedCommand;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.Assignment;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.AssignmentRepository;
import com.emergency.roadside.help.responder_assignment_backend.model.auth.RegisterDTO;
import com.emergency.roadside.help.responder_assignment_backend.services.AuthService;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/assignments")
public class AssignmentController {

    private final AssignmentRepository assignmentRepository;
    private final CommandGateway commandGateway;

    @PostMapping("/accept-request/{assignmentDBId}")
    public ResponseEntity<?> register(@PathVariable Long assignmentDBId) {
        //first check if it is not accepted yet, if accepted, do nothing, just return the assignment
        //this is to make idempotent api
        //also if the assignment if its cancelled, dont allow accepting it again,
        Assignment assignment = assignmentRepository.findById(assignmentDBId).orElseThrow(()-> new ItemNotFoundException("Assignment not found"));
        if(assignment.getAssignStatus() == AssignStatus.CANCELLED){
            throw new UnprocessableEntityException("Assignment was cancelled already");
        }
        else if(assignment.getAssignStatus() == AssignStatus.ASSIGNED){
            return ResponseEntity.ok(assignment);
        }
        else{
            ResponderAcceptedCommand command = ResponderAcceptedCommand.builder()
                    .bookingId(assignment.getBookingId())
                    .assignmentId(assignment.getAssignmentId())
                    .responderId(assignment.getResponder().getId())
                    .responderName(assignment.getResponder().getName())
                    .build();
            commandGateway.sendAndWait(command);
            return ResponseEntity.ok(command);
        }
    }

}
