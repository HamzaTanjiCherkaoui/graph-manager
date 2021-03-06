/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netckracker.graph.manager.repository;

import com.netckracker.graph.manager.model.Tags;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author eliza
 */
@Repository
public interface TagsRepository extends JpaRepository <Tags, String> {
    Tags findByTagId(String tagId);
    Tags findByName(String tagName);
    Page <Tags> findFirst10ByNameStartingWith(String tagName, Pageable pageable);
}
