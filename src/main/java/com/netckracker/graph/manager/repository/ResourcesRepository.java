/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netckracker.graph.manager.repository;

import com.netckracker.graph.manager.model.Resources;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author eliza
 */
@Repository
public interface ResourcesRepository extends JpaRepository <Resources, String> {
    Resources findByResourceId(String resourceId);
    List<Resources> findFirst10ByNameStartingWith(String resourceName);
}