package com.egiantsprojects.practice.swaggerapi.student;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import io.swagger.annotations.ApiOperation;

@RestController
public class StudentResourceController {

	@Autowired
	private StudentRepository studentRepository;

	@GetMapping("/students")
	@ApiOperation(value = "Find all students", produces = "application/json")
			public List<Student> retrieveAllStudents() {
		return studentRepository.findAll();
	}

	@GetMapping("/students/{id}")
	@ApiOperation(value = "Find student by id", notes = "Also returns a link to retrieve all students with rel - all-students",
			      produces = "application/json")
	public Resource<Student> retrieveStudent(@PathVariable long id) {
		Optional<Student> student = studentRepository.findById(id);

		if (!student.isPresent())
			throw new StudentNotFoundException("id-" + id);

		Resource<Student> resource = new Resource<Student>(student.get());
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllStudents());

		resource.add(linkTo.withRel("all-students"));
		return resource;
	}

	@DeleteMapping("/students/{id}")
	@ApiOperation(value = "Delete a student by ID", produces = "application/json")
	public void deleteStudent(@PathVariable long id) {
		studentRepository.deleteById(id);
	}

	@PostMapping("/students")
	@ApiOperation(value = "Insert/create a new student", produces = "application/json")
	public ResponseEntity<Object> createStudent(@RequestBody Student student) {
		Student savedStudent = studentRepository.save(student);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedStudent.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PutMapping("/students/{id}")
	@ApiOperation(value = "Update a Student", produces = "application/json")
	public ResponseEntity<Object> updateStudent(@RequestBody Student student, @PathVariable long id) {

		Optional<Student> studentOptional = studentRepository.findById(id);
		if (!studentOptional.isPresent())
			return ResponseEntity.notFound().build();

		student.setId(id);
		studentRepository.save(student);

		return ResponseEntity.noContent().build();
	}
}
