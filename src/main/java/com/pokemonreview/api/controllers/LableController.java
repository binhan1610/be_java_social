package com.pokemonreview.api.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.pokemonreview.api.dto.CreateLableDto;
import com.pokemonreview.api.dto.lable.LableDto;
import com.pokemonreview.api.dto.lable.ResponseLableDto;
import com.pokemonreview.api.models.Lable;
import com.pokemonreview.api.security.JWTGenerator;
import com.pokemonreview.api.service.LableService;
import com.pokemonreview.api.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lable")
public class LableController {

    private  JWTGenerator jwtGenerator;
    private LableService lableService;
    private TemplateService templateService;

    @Autowired
    public LableController(TemplateService templateService,JWTGenerator jwtGenerator, LableService lableService) {
        this.jwtGenerator = jwtGenerator;
        this.lableService = lableService;
        this.templateService = templateService;
    }
    @PostMapping("/create")
    public ResponseEntity<?> createLable(@RequestBody final CreateLableDto createLableDto, @RequestHeader("Authorization") String token) {
      try {
          String jwtToken=token.substring(7);
          String username= jwtGenerator.getUsernameFromJWT((jwtToken));
          Map<String, Object> model = new HashMap<>();
          LableDto lable = lableService.createLableByUser(username,createLableDto.getLable_name());
          model.put("lable_id", lable.getId());
          model.put("lable_name",lable.getLabelName());
          JsonNode jsonResponse = templateService.generateJsonFromTemplate("responseLable.ftl",model);

          return new ResponseEntity<>(jsonResponse, HttpStatus.CREATED);
      }
      catch (Exception e) {
          e.printStackTrace();
          return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

//    @GetMapping("/delete")
//    public ResponseEntity<?> deleteLable(@RequestParam(value = "lable_id") int lable_id) {
//        System.out.println(lable_id);
//        lableService.deleteLable(lable_id);
//        return new ResponseEntity<>("ok", HttpStatus.OK);
//    }

    @GetMapping("/get-by-user")
    public  ResponseEntity<?> getLablesByUser(@RequestHeader("Authorization") String token, @RequestParam(value = "page",defaultValue = "0") int page, @RequestParam(value = "size",defaultValue = "10") int size) {
try{
    String jwtToken=token.substring(7);
    String username= jwtGenerator.getUsernameFromJWT((jwtToken));
     Map<String, Object> model = new HashMap<>();
     ResponseLableDto responseLableDto = lableService.findAllLableByUser(username,page,size);
     model.put("labels",responseLableDto.getContent());
     model.put("pageNo",responseLableDto.getPageNo());
     model.put("pageSize",responseLableDto.getPageSize());
     model.put("total",responseLableDto.getTotal());
     model.put("totalPages",responseLableDto.getTotalPages());
     JsonNode jsonNode = templateService.generateJsonFromTemplate("responseListLable.ftl",model);
    return  new ResponseEntity<>(jsonNode, HttpStatus.OK);
}
catch (Exception e) {
    e.printStackTrace();
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
}
    }
}
