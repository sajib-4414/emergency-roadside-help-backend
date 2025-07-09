package com.emergency.roadside.help.responder_assignment_backend.services;


import com.emergency.roadside.help.responder_assignment_backend.common_module.commonexternal.ExternalUser;
import com.emergency.roadside.help.responder_assignment_backend.common_module.commonmodels.AssignStatus;
import com.emergency.roadside.help.responder_assignment_backend.common_module.exceptions.customexceptions.ItemNotFoundException;
import com.emergency.roadside.help.responder_assignment_backend.common_module.exceptions.customexceptions.UnprocessableEntityException;
import com.emergency.roadside.help.responder_assignment_backend.cqrs.commands.ResponderAcceptedCommand;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.Assignment;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.AssignmentRepository;
import com.emergency.roadside.help.responder_assignment_backend.model.assignment.AssignmentStatusResponse;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.Responder;
import com.emergency.roadside.help.responder_assignment_backend.model.responder.ResponderRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.emergency.roadside.help.responder_assignment_backend.configs.auth.AuthHelper.getCurrentUser;

@Service
@AllArgsConstructor
public class AssignmentService
{
    private final AssignmentRepository assignmentRepository;
    private final ResponderRepository responderRepository;
    private final CommandGateway commandGateway;

    public AssignmentStatusResponse acceptAssignment(String assignmentId){
        //first check if it is not accepted yet, if accepted, do nothing, just return the assignment
        //this is to make idempotent api
        //also if the assignment if its cancelled, dont allow accepting it again,
        Assignment assignment = assignmentRepository.findByAssignmentId(assignmentId);
        if(assignment == null)
            throw new ItemNotFoundException("Assignment not found");
        if(assignment.getAssignStatus() == AssignStatus.CANCELLED){
            throw new UnprocessableEntityException("Assignment was cancelled already");
        }
        else if(assignment.getAssignStatus() == AssignStatus.ASSIGNED){
            AssignmentStatusResponse response = new AssignmentStatusResponse();
            BeanUtils.copyProperties(assignment,response);
            response.setInformation("Assignment was already accepted before");
            return response;
        }
        else{
            ResponderAcceptedCommand command = ResponderAcceptedCommand.builder()
                    .bookingId(assignment.getBookingId())
                    .assignmentId(assignment.getAssignmentId())
                    .responderId(assignment.getResponder().getId())
                    .responderName(assignment.getResponder().getName())
                    .build();
            commandGateway.sendAndWait(command);
            AssignmentStatusResponse response = new AssignmentStatusResponse();
            BeanUtils.copyProperties(assignment,response);
            response.setInformation("Assignment accepted");
            return response;
        }
    }

    public List<Assignment> getMyAssignments(){
        ExternalUser user = getCurrentUser();
        Responder currentResponder = responderRepository.findByUserId(user.getId()).orElseThrow(()->new ItemNotFoundException("profile not found"));
        return assignmentRepository.findByResponder(currentResponder);
    }

    public Assignment getAssignmentDetailByAssignmentId(String assignmentId){
        return assignmentRepository.findByAssignmentId(assignmentId);
    }

}
