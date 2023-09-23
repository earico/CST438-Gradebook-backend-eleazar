package com.cst438.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin 
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@GetMapping("/assignment")
	public AssignmentDTO[] getAllAssignmentsForInstructor(@RequestParam("name") Optional<String> name,
			 @RequestParam("dueDate") Optional<String> dueDate,
			 @RequestParam("course") Optional<String> course) {
		// get all assignments for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(), 
					as.getName(), 
					as.getDueDate().toString(), 
					as.getCourse().getTitle(), 
					as.getCourse().getCourse_id());
			result[i]=dto;
		}
		return result;
	}
	
	// TODO create CRUD methods for Assignment
	// read
	
	// create
	@PostMapping("/assignment")
	public int createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
		Assignment assignment = new Assignment();
        //assignment.setName(assignmentDTO.getAssignmentName());
        
	}
	
	// delete
	@DeleteMapping("/assignment/{assignment_id}")
	public void deleteAssignment(@PathVariable("assignment_id") int assignment_id, 
								 @RequestParam("force") Optional<String> force) {		
		assignmentRepository.deleteById(assignment_id);
	}
	
	// update
	@PostMapping("/assignment/{assignment_id}")
	public void updateAssignment(@RequestBody AssignmentDTO assignmentDTO, 
								 @PathVariable("assignment_id") int assignment_id) {
        Optional<Assignment> updatedAssignment = assignmentRepository.findById(assignment_id);
        
		
	}
	
	
}
