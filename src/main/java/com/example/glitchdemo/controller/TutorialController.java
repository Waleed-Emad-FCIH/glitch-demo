package com.example.glitchdemo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.glitchdemo.model.Tutorial;
import com.example.glitchdemo.repository.TutorialRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TutorialController {

  private boolean prepared;

  @Autowired
  TutorialRepository tutorialRepository;

  @GetMapping("/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
    try {
      List<Tutorial> tutorials = new ArrayList<>();

      if (title == null) {
        tutorials.addAll(tutorialRepository.findAll());
      } else {
        tutorials.addAll(tutorialRepository.findByTitleContaining(title));
      }

      if (tutorials.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(tutorials, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
    Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

    return tutorialData.map(tutorial -> new ResponseEntity<>(tutorial, HttpStatus.OK))
      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping("/tutorials")
  public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
    try {
      Tutorial newTutorial = tutorialRepository
        .save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false));
      return new ResponseEntity<>(newTutorial, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
    Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

    if (tutorialData.isPresent()) {
      Tutorial currentTutorial = tutorialData.get();
      currentTutorial.setTitle(tutorial.getTitle());
      currentTutorial.setDescription(tutorial.getDescription());
      currentTutorial.setPublished(tutorial.isPublished());
      return new ResponseEntity<>(tutorialRepository.save(currentTutorial), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/tutorials/{id}")
  public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") long id) {
    try {
      tutorialRepository.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/tutorials/published")
  public ResponseEntity<List<Tutorial>> findByPublished() {
    try {
      List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

      if (tutorials.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(tutorials, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/prepare")
  public ResponseEntity<HttpStatus> testData() {
    if (!prepared) {
      Tutorial t1 = new Tutorial();
      t1.setTitle("Java how to program");
      t1.setDescription("Learn how to develop an application using Java");
      t1.setPublished(true);

      Tutorial t2 = new Tutorial();
      t2.setTitle("Spring boot framework");
      t2.setDescription("Learn how to start an application using Spring boot framework");
      t2.setPublished(true);

      Tutorial t3 = new Tutorial();
      t3.setTitle("Databases");
      t3.setDescription("Create databases and use it with Java apps");
      t3.setPublished(true);

      Tutorial t4 = new Tutorial();
      t4.setTitle("CRUD operations");
      t4.setDescription("Learn how to create CRUD operations using Java");
      t4.setPublished(true);

      Tutorial t5 = new Tutorial();
      t5.setTitle("What's Glitch");
      t5.setDescription("Deploy your Java app on Glitch");
      t5.setPublished(true);

      tutorialRepository.saveAll(List.of(t1, t2, t3, t4, t5));
      prepared = !prepared;
      return new ResponseEntity<>(HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
    }
  }
}