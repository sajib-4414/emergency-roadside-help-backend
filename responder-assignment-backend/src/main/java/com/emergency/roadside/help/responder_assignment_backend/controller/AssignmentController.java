package com.emergency.roadside.help.responder_assignment_backend.controller;

import com.emergency.roadside.help.common_module.commonexternal.AuthResponse;
import com.emergency.roadside.help.common_module.commonexternal.ExternalUser;
import com.emergency.roadside.help.common_module.commonmodels.AssignStatus;
import com.emergency.roadside.help.common_module.commonmodels.Priority;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.common_module.exceptions.customexceptions.UnprocessableEntityException;
import com.emergency.roadside.help.responder_assignment_backend.cqrs.commands.ResponderAcceptedCommand;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.Assignment;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.AssignmentRepository;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.AssignmentStatusResponse;
import com.emergency.roadside.help.responder_assignment_backend.model.auth.RegisterDTO;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.ResponderRepository;
import com.emergency.roadside.help.responder_assignment_backend.services.AssignmentService;
import com.emergency.roadside.help.responder_assignment_backend.services.AuthService;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;

import static com.emergency.roadside.help.responder_assignment_backend.configs.auth.AuthHelper.getCurrentUser;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/assignments")
public class AssignmentController {

    private AssignmentService assignmentService;

    @PostMapping("/accept-request/{assignmentId}")
    public ResponseEntity<AssignmentStatusResponse> acceptAssignment(@PathVariable String assignmentId) {
        return ResponseEntity.ok(assignmentService.acceptAssignment(assignmentId));
    }

    @GetMapping("/my-assignments")
    public ResponseEntity<List<Assignment>> getMyAssignments(){
        return ResponseEntity.ok(assignmentService.getMyAssignments());
    }

    @GetMapping("/assignment-by-id/{assignmentId}")
    public ResponseEntity<Assignment> getAssignmentDetail(@PathVariable String assignmentId){
        return ResponseEntity.ok(assignmentService.getAssignmentDetailByAssignmentId(assignmentId));
    }


}
