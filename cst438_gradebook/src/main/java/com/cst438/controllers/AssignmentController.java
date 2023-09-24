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
	public AssignmentDTO[] getAllAssignmentsForInstructor() {
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
	@GetMapping("/assignment/{id}")
	public AssignmentDTO getAssignment(@PathVariable("id") int id)  {
		// check that assignment belongs to the instructor
		String instructorEmail = "dwisneski@csumb.edu";
		if (assignmentRepository.findByEmail(instructorEmail)) {
			
		}
		// return Assignment data for the given assignment 
		// if assignment not found, return HTTP status code 404
	}
	
	// create
	@PostMapping("/assignment")
	public int createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
		String instructorEmail = "dwisneski@csumb.edu";
		Assignment as = new Assignment();
		Course cs = new Course();
		
		cs.setTitle(assignmentDTO.courseTitle());
		as.setId(assignmentDTO.id());
		as.setName(assignmentDTO.assignmentName());
        as.setCourse(cs);
        
        
        return as.getId();
	}
	
	// delete
	@DeleteMapping("/assignment/{id}")
	public void deleteAssignment(@PathVariable("id") int id, 
								 @RequestParam("force") Optional<String> force) {		
		Assignment as = assignmentRepository.findById(id).orElseThrow(() -> 
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
		
		boolean e = courseRepository.findById(id).isEmpty();
		
		if (e && force.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignment_id invalid");
		}
		
		else {
			assignmentRepository.delete(as);
		}
	}
	
	// update
	@PostMapping("/assignment/{id}")
	public void updateAssignment(@RequestBody AssignmentDTO assignmentDTO, 
								 @PathVariable("assignment_id") int id) {
		Assignment as = assignmentRepository.findById(id).orElseThrow(() -> 
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
		
		as.setId(assignmentDTO.id());
		as.setName(assignmentDTO.assignmentName());
		
		assignmentRepository.save(as);
	}
	
	
}
