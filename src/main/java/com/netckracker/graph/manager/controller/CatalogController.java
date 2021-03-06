/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netckracker.graph.manager.controller;

import com.netckracker.graph.manager.model.Catalog;
import com.netckracker.graph.manager.modelDto.TagsDto;
import com.netckracker.graph.manager.service.CatalogService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author eliza
 */
@RestController
public class CatalogController {
    @Autowired
    private CatalogService catalogService;
    
    @RequestMapping(value = "/catalog/create", method = RequestMethod.POST, 
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    public ResponseEntity<String> addCatalog(@RequestParam String catalogName, @RequestParam String description){
        String catalogId=catalogService.createCatalog(catalogName, description);
     return new ResponseEntity<>(catalogId, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/catalog/getbyname/{catalogName}", method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    public ResponseEntity<Catalog> getCatalogByName(@PathVariable ("catalogName") String catalogeName)  {
        Catalog catalog=catalogService.findCatalog(catalogeName); 
    
        if (catalog==null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
         else   return new ResponseEntity<>(catalog, HttpStatus.OK);            
    }    
    
    @RequestMapping(value = "/catalog/getbyletters/{letters}",method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    public  ResponseEntity<?> getCatalogsByLetters(@PathVariable  String  letters, @RequestParam(required=false, value="page" ) Integer page,
           @RequestParam(required=false, value="size" ) Integer size  ){
        
        if (size==null&&page==null)
        {
            page=0;
            size=6;
        }
        
        List<Catalog> catalogs=catalogService.findByLetters(letters, page, size);
        if (catalogs.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{           
            return new ResponseEntity<>(catalogs, HttpStatus.OK);
        }
    }
    
    @RequestMapping(value = "/catalog/getall",method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    public  ResponseEntity<?> getCatalogs(){
        
       
        List<Catalog> catalogs=catalogService.getAll();
        if (catalogs.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{           
            return new ResponseEntity<>(catalogs, HttpStatus.OK);
        }
    }
}
